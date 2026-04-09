package com.example.highpass_backend.service.certificate;

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
    @Transactional
    public void initializeSchedules() {
        if (nationalCertificateRepository.count() > 0) {
            log.info("기존 자격증 일정 데이터가 있어 Qnet 초기 적재를 건너뜁니다.");
            return;
        }

        List<NationalCertificate> fetched = certificateDataService.fetchAll();
        if (fetched.isEmpty()) {
            log.warn("Qnet API에서 자격증 일정을 가져오지 못했습니다.");
            return;
        }

        nationalCertificateRepository.saveAll(fetched);
        log.info("Qnet 자격증 일정 최초 적재 완료: {}건", fetched.size());
    }

    @Transactional(readOnly = true)
    public List<CertificateScheduleResponse> getSchedules() {
        return nationalCertificateRepository.findAll().stream()
                .map(entity -> CertificateScheduleResponse.builder()
                        .id(entity.getId())
                        .certificateName(entity.getCertificateName())
                        .year(entity.getYear())
                        .round(entity.getRound())
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
