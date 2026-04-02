package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.certificate.CertificateApiDto;
import com.example.highpass_backend.dto.certificate.CertificateScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateDataService {

    @Value("${api.public-data.key}")
    private String apiServiceKey;

    public List<CertificateScheduleResponse> getSchedules() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/getEList?serviceKey=" + apiServiceKey;

        try {
            CertificateApiDto apiDto = restTemplate.getForObject(new URI(url), CertificateApiDto.class);

            if (apiDto == null || apiDto.getResponse() == null ||
                    apiDto.getResponse().getBody() == null ||
                    apiDto.getResponse().getBody().getItems() == null) {
                return Collections.emptyList();
            }

            List<CertificateApiDto.Item> rawItems = apiDto.getResponse().getBody().getItems().getItem();

            return rawItems.stream()
                    .map(item -> CertificateScheduleResponse.builder()
                            .certificateName(item.getDescription())
                            .writtenApplyStart(parseDate(item.getDocregstartdt()))
                            .writtenApplyEnd(parseDate(item.getDocregenddt()))
                            .writtenExamDate(parseDate(item.getDocexamdt()))
                            .writtenResultDate(parseDate(item.getDocpassdt()))
                            .practicalApplyStart(parseDate(item.getPracregstartdt()))
                            .practicalApplyEnd(parseDate(item.getPracregenddt()))
                            .practicalExamDate(parseDate(item.getPracexamstartdt()))
                            .practicalResultDate(parseDate(item.getPracpassdt()))
                            .build())
                    .collect(Collectors.toList());

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
}