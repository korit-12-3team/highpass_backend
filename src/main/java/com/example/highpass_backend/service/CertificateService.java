package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.certificate.CertificateScheduleResponse;
import com.example.highpass_backend.entity.certificate.NationalCertificate;
import com.example.highpass_backend.repository.certificate.NationalCertificateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateDataService certificateDataService;
    private final NationalCertificateRepository nationalCertificateRepository;

    @PostConstruct
    public void syncCertificateSchedules() {
        log.info("자격증 일정 저장 시작");
        List<NationalCertificate> entities = certificateDataService.fetchAll();
        nationalCertificateRepository.saveAll(entities);
        log.info("자격증 일정 저장 완료 → {}건", entities.size());
    }



    @Transactional(readOnly = true)
    public List<CertificateScheduleResponse> getSchedules() {
        return nationalCertificateRepository.findAll().stream()
                .map(entity -> CertificateScheduleResponse.builder()
                        .id(entity.getId())
                        .certificateName(entity.getCertificateName())
                        .writtenApplyStart(entity.getWrittenApplyStart())
                        .writtenApplyEnd(entity.getWrittenApplyEnd())
                        .writtenExamDate(entity.getWrittenExamDate())
                        .writtenResultDate(entity.getWrittenResultDate())
                        .practicalApplyStart(entity.getPracticalApplyStart())
                        .practicalApplyEnd(entity.getPracticalApplyEnd())
                        .practicalExamDate(entity.getPracticalExamDate())
                        .practicalResultDate(entity.getPracticalResultDate())
                        .build())
                .collect(Collectors.toList());
    }
}