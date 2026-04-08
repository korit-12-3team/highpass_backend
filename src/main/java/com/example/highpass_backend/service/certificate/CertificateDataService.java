package com.example.highpass_backend.service.certificate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import com.example.highpass_backend.entity.certificate.NationalCertificate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        List<NationalCertificate> engineer = fetchEntities(ENGINEER_URL);
        log.info("기사 데이터 → {}건", engineer.size());

        List<NationalCertificate> craftsman = fetchEntities(CRAFTSMAN_URL);
        log.info("기능사 데이터 → {}건", craftsman.size());
        result.addAll(fetchEntitiesWithRetry(ENGINEER_URL));
        result.addAll(fetchEntitiesWithRetry(CRAFTSMAN_URL));
        return result;
    }

    private List<NationalCertificate> fetchEntities(String baseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + apiServiceKey;

        try {
            String response = restTemplate.getForObject(new URI(url), String.class);

            JSONObject json;
            if (response != null && response.trim().startsWith("<")) {
                json = XML.toJSONObject(response); // XML → JSON
            } else {
                json = new JSONObject(response);   // 이미 JSON
            }

            JSONObject responseObj = json.optJSONObject("response");
            if (responseObj == null) return Collections.emptyList();

            JSONObject body = responseObj.optJSONObject("body");
            if (body == null) return Collections.emptyList();

            JSONObject items = body.optJSONObject("items");
            if (items == null) return Collections.emptyList();

            Object item = items.opt("item");
            JSONArray itemArray;
            if (item instanceof JSONArray) {
                itemArray = (JSONArray) item;
            } else if (item instanceof JSONObject) {
                itemArray = new JSONArray();
                itemArray.put(item);
            } else {
                return Collections.emptyList();
            }

            List<NationalCertificate> result = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject el = itemArray.getJSONObject(i);
                result.add(NationalCertificate.builder()
                        .certificateName(el.optString("description", null))
                        .writtenApplyStart(parseDate(String.valueOf(el.opt("docregstartdt"))))
                        .writtenApplyEnd(parseDate(String.valueOf(el.opt("docregenddt"))))
                        .writtenExamDate(parseDate(String.valueOf(el.opt("docexamdt"))))
                        .writtenResultDate(parseDate(String.valueOf(el.opt("docpassdt"))))
                        .practicalApplyStart(parseDate(String.valueOf(el.opt("pracregstartdt"))))
                        .practicalApplyEnd(parseDate(String.valueOf(el.opt("pracregenddt"))))
                        .practicalExamDate(parseDate(String.valueOf(el.opt("pracexamstartdt"))))
                        .practicalResultDate(parseDate(String.valueOf(el.opt("pracpassdt"))))
                        .build());
            }
            return result;

        } catch (Exception e) {
            log.error("API 통신/매핑 중 에러: ", e);
            return Collections.emptyList();
        }
    }


    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.length() < 8) return null;
        try {
            String cleanDate = dateStr.replaceAll("[^0-9]", "");
            return LocalDate.parse(cleanDate.substring(0, 8), java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }

    private List<NationalCertificate> fetchEntitiesWithRetry(String baseUrl) {
        int maxRetry = 3;
        for (int i = 0; i < maxRetry; i++) {
            List<NationalCertificate> result = fetchEntities(baseUrl);
            if (!result.isEmpty()) return result;
            log.warn("재시도 {}/{}...", i + 1, maxRetry);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
        return Collections.emptyList();
    } // api 두 개 써서 api 호출 타이밍이 안맞아서 임시로 추가해둘게요 ㅜㅜ....


}