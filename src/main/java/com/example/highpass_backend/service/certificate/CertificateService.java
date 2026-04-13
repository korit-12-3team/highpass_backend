package com.example.highpass_backend.service.certificate;

import com.example.highpass_backend.dto.certificate.CertificateScheduleResponse;
import com.example.highpass_backend.dto.certificate.CertificateSyncResponse;
import com.example.highpass_backend.entity.certificate.NationalCertificate;
import com.example.highpass_backend.repository.certificate.NationalCertificateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        CertificateSyncResponse syncResult = syncSchedules();
        if (syncResult.getFetchedCount() == 0) {
            log.warn("Qnet API에서 자격증 일정을 가져오지 못했습니다.");
            return;
        }

        log.info(
                "Qnet 자격증 일정 최초 적재 완료: fetched={}, created={}, updated={}, total={}",
                syncResult.getFetchedCount(),
                syncResult.getCreatedCount(),
                syncResult.getUpdatedCount(),
                syncResult.getTotalCount()
        );
    }

    @Transactional(readOnly = true)
    public List<CertificateScheduleResponse> getSchedules() {
        return nationalCertificateRepository.findAll().stream()
                .map(entity -> CertificateScheduleResponse.builder()
                        .id(entity.getId())
                        .certificateName(entity.getCertificateName())
                        .year(entity.getYear())
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

    @Transactional
    public CertificateSyncResponse syncSchedules() {
        List<NationalCertificate> fetched = certificateDataService.fetchAll();
        if (fetched.isEmpty()) {
            return CertificateSyncResponse.builder()
                    .fetchedCount(0)
                    .createdCount(0)
                    .updatedCount(0)
                    .totalCount((int) nationalCertificateRepository.count())
                    .message("Q-Net 일정 데이터를 가져오지 못했습니다.")
                    .build();
        }

        int createdCount = 0;
        int updatedCount = 0;

        for (NationalCertificate incoming : fetched) {
            NationalCertificate existing = nationalCertificateRepository
                    .findByCertificateNameAndWrittenApplyStartAndPracticalApplyStart(
                            incoming.getCertificateName(),
                            incoming.getWrittenApplyStart(),
                            incoming.getPracticalApplyStart()
                    )
                    .orElse(null);

            if (existing == null) {
                nationalCertificateRepository.save(incoming);
                createdCount++;
                continue;
            }

            if (applyUpdates(existing, incoming)) {
                updatedCount++;
            }
        }

        return CertificateSyncResponse.builder()
                .fetchedCount(fetched.size())
                .createdCount(createdCount)
                .updatedCount(updatedCount)
                .totalCount((int) nationalCertificateRepository.count())
                .message("자격증 일정 동기화가 완료되었습니다.")
                .build();
    }

    private boolean applyUpdates(NationalCertificate existing, NationalCertificate incoming) {
        boolean changed = false;

        if (!sameDate(existing.getWrittenApplyStart(), incoming.getWrittenApplyStart())) {
            existing.setWrittenApplyStart(incoming.getWrittenApplyStart());
            changed = true;
        }
        if (!sameDate(existing.getWrittenApplyEnd(), incoming.getWrittenApplyEnd())) {
            existing.setWrittenApplyEnd(incoming.getWrittenApplyEnd());
            changed = true;
        }
        if (!sameDate(existing.getWrittenExamDate(), incoming.getWrittenExamDate())) {
            existing.setWrittenExamDate(incoming.getWrittenExamDate());
            changed = true;
        }
        if (!sameDate(existing.getWrittenResultDate(), incoming.getWrittenResultDate())) {
            existing.setWrittenResultDate(incoming.getWrittenResultDate());
            changed = true;
        }
        if (!sameDate(existing.getPracticalApplyStart(), incoming.getPracticalApplyStart())) {
            existing.setPracticalApplyStart(incoming.getPracticalApplyStart());
            changed = true;
        }
        if (!sameDate(existing.getPracticalApplyEnd(), incoming.getPracticalApplyEnd())) {
            existing.setPracticalApplyEnd(incoming.getPracticalApplyEnd());
            changed = true;
        }
        if (!sameDate(existing.getPracticalExamDate(), incoming.getPracticalExamDate())) {
            existing.setPracticalExamDate(incoming.getPracticalExamDate());
            changed = true;
        }
        if (!sameDate(existing.getPracticalResultDate(), incoming.getPracticalResultDate())) {
            existing.setPracticalResultDate(incoming.getPracticalResultDate());
            changed = true;
        }

        return changed;
    }

    private boolean sameDate(LocalDate left, LocalDate right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.isEqual(right);
    }
}
