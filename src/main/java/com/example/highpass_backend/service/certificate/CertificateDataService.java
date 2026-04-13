package com.example.highpass_backend.service.certificate;

import com.example.highpass_backend.entity.certificate.NationalCertificate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateDataService {

    @Value("${api.public-data.key}")
    private String apiServiceKey;

    private static final String ENGINEER_URL = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/getEList?serviceKey=";
    private static final String CRAFTSMAN_URL = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/getCList?serviceKey=";

    public List<NationalCertificate> fetchAll() {
        List<NationalCertificate> result = new ArrayList<>();
        result.addAll(fetchEntitiesWithRetry(ENGINEER_URL));
        result.addAll(fetchEntitiesWithRetry(CRAFTSMAN_URL));
        return dedupe(result);
    }

    private List<NationalCertificate> fetchEntities(String baseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + apiServiceKey;

        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }

            JSONObject json = response.trim().startsWith("<") ? XML.toJSONObject(response) : new JSONObject(response);
            JSONObject responseObj = json.optJSONObject("response");
            if (responseObj == null) return Collections.emptyList();

            JSONObject body = responseObj.optJSONObject("body");
            if (body == null) return Collections.emptyList();

            JSONObject items = body.optJSONObject("items");
            if (items == null) return Collections.emptyList();

            Object item = items.opt("item");
            JSONArray itemArray;
            if (item instanceof JSONArray jsonArray) {
                itemArray = jsonArray;
            } else if (item instanceof JSONObject jsonObject) {
                itemArray = new JSONArray();
                itemArray.put(jsonObject);
            } else {
                return Collections.emptyList();
            }

            List<NationalCertificate> result = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject element = itemArray.getJSONObject(i);
                LocalDate writtenApplyStart = parseDate(optString(element, "docregstartdt"));
                LocalDate writtenApplyEnd = parseDate(optString(element, "docregenddt"));
                LocalDate writtenExamDate = parseDate(optString(element, "docexamdt"));
                LocalDate writtenResultDate = parseDate(optString(element, "docpassdt"));
                LocalDate practicalApplyStart = parseDate(optString(element, "pracregstartdt"));
                LocalDate practicalApplyEnd = parseDate(optString(element, "pracregenddt"));
                LocalDate practicalExamDate = parseDate(optString(element, "pracexamstartdt"));
                LocalDate practicalResultDate = parseDate(optString(element, "pracpassdt"));

                result.add(NationalCertificate.builder()
                        .certificateName(optString(element, "description"))
                        .year(extractYear(element, writtenApplyStart, writtenExamDate, practicalExamDate))
                        .writtenApplyStart(writtenApplyStart)
                        .writtenApplyEnd(writtenApplyEnd)
                        .writtenExamDate(writtenExamDate)
                        .writtenResultDate(writtenResultDate)
                        .practicalApplyStart(practicalApplyStart)
                        .practicalApplyEnd(practicalApplyEnd)
                        .practicalExamDate(practicalExamDate)
                        .practicalResultDate(practicalResultDate)
                        .build());
            }

            return result;
        } catch (Exception exception) {
            log.error("Qnet 자격증 일정 수집 중 오류", exception);
            return Collections.emptyList();
        }
    }

    private List<NationalCertificate> fetchEntitiesWithRetry(String baseUrl) {
        int maxRetry = 3;
        for (int i = 0; i < maxRetry; i++) {
            List<NationalCertificate> result = fetchEntities(baseUrl);
            if (!result.isEmpty()) return result;
            log.warn("Qnet API 재시도 {}/{}", i + 1, maxRetry);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private List<NationalCertificate> dedupe(List<NationalCertificate> source) {
        Map<String, NationalCertificate> map = new LinkedHashMap<>();
        for (NationalCertificate certificate : source) {
            String name = certificate.getCertificateName();
            if (name == null || name.isBlank()) continue;
            String key = buildDedupeKey(
                    certificate.getCertificateName(),
                    certificate.getWrittenApplyStart(),
                    certificate.getPracticalApplyStart()
            );
            map.put(key, certificate);
        }
        return new ArrayList<>(map.values());
    }

    private String buildDedupeKey(String certificateName, LocalDate writtenApplyStart, LocalDate practicalApplyStart) {
        return normalize(certificateName) + "|" + normalizeDate(writtenApplyStart) + "|" + normalizeDate(practicalApplyStart);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeDate(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String optString(JSONObject element, String key) {
        String value = element.optString(key, null);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private int extractYear(JSONObject element, LocalDate... fallbackDates) {
        for (String key : List.of("implYy", "implyy", "year", "examYear")) {
            String value = optString(element, key);
            if (value != null) {
                String digits = value.replaceAll("[^0-9]", "");
                if (digits.length() >= 4) {
                    return Integer.parseInt(digits.substring(0, 4));
                }
            }
        }

        for (LocalDate date : fallbackDates) {
            if (date != null) return date.getYear();
        }

        return LocalDate.now().getYear();
    }

    private int extractRound(JSONObject element) {
        for (String key : List.of("implSeq", "implseq", "round", "turn", "series", "seq")) {
            String value = optString(element, key);
            if (value != null) {
                String digits = value.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    return Integer.parseInt(digits);
                }
            }
        }
        return 0;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            String cleanDate = dateStr.replaceAll("[^0-9]", "");
            if (cleanDate.length() < 8) return null;
            return LocalDate.parse(cleanDate.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception exception) {
            return null;
        }
    }
}
