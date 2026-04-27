package com.example.highpass_backend.service.report;

import com.example.highpass_backend.dto.report.CreateReportRequest;
import com.example.highpass_backend.dto.report.CreateSupportInquiryRequest;
import com.example.highpass_backend.dto.report.ReportResponse;
import com.example.highpass_backend.dto.user.UserDisplayName;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.entity.report.Report;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.chat.ChatParticipantRepository;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import com.example.highpass_backend.repository.report.ReportRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final StudyBoardRepository studyBoardRepository;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public ReportResponse createReport(Long reporterUserId, CreateReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Report payload is required.");
        }

        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found."));

        Report.TargetType targetType = parseTargetType(request.targetType());
        String reasonCode = normalizeRequired(request.reasonCode(), "reasonCode");
        String detail = normalizeRequired(request.detail(), "detail");
        if (detail.length() < 10) {
            throw new IllegalArgumentException("Report detail must be at least 10 characters.");
        }

        ResolvedTarget target = resolveTarget(
                reporter,
                targetType,
                request.targetId(),
                request.targetLabel()
        );

        Report report = reportRepository.save(
                Report.builder()
                        .reporter(reporter)
                        .targetType(targetType)
                        .targetId(target.targetId())
                        .targetLabel(target.targetLabel())
                        .reasonCode(reasonCode)
                        .reason(detail)
                        .build()
        );

        return ReportResponse.from(report);
    }

    @Transactional
    public ReportResponse createSupportInquiry(CreateSupportInquiryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Inquiry payload is required.");
        }

        String email = normalizeRequired(request.email(), "email");
        String title = normalizeRequired(request.title(), "title");
        String reasonCode = normalizeRequired(request.reasonCode(), "reasonCode");
        String detail = normalizeRequired(request.detail(), "detail");
        if (detail.length() < 10) {
            throw new IllegalArgumentException("Inquiry detail must be at least 10 characters.");
        }

        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입된 이메일로만 계정 문의를 접수할 수 있습니다."));

        Report report = reportRepository.save(
                Report.builder()
                        .reporter(reporter)
                        .targetType(Report.TargetType.INQUIRY)
                        .targetId("support-" + reporter.getId())
                        .targetLabel(title)
                        .reasonCode(reasonCode)
                        .reason(detail)
                        .build()
        );

        return ReportResponse.from(report);
    }

    private Report.TargetType parseTargetType(String targetType) {
        String normalized = normalizeRequired(targetType, "targetType");
        try {
            return Report.TargetType.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported report targetType: " + targetType);
        }
    }

    private ResolvedTarget resolveTarget(
            User reporter,
            Report.TargetType targetType,
            String rawTargetId,
            String rawTargetLabel
    ) {
        return switch (targetType) {
            case USER -> resolveUserTarget(reporter, rawTargetId);
            case POST -> resolvePostTarget(reporter, rawTargetId);
            case COMMENT -> resolveCommentTarget(reporter, rawTargetId);
            case CHAT -> resolveChatTarget(reporter, rawTargetId);
            case INQUIRY -> resolveInquiryTarget(reporter, rawTargetId, rawTargetLabel);
        };
    }

    private ResolvedTarget resolveUserTarget(User reporter, String rawTargetId) {
        Long targetUserId = parseNumericTargetId(rawTargetId, "Report targetId must be numeric.");
        if (reporter.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot report yourself.");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Report target not found."));

        return new ResolvedTarget(String.valueOf(targetUser.getId()), UserDisplayName.nickname(targetUser));
    }

    private ResolvedTarget resolvePostTarget(User reporter, String rawTargetId) {
        String normalized = normalizeRequired(rawTargetId, "targetId");
        String[] parts = normalized.split("-", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Post report targetId must include board type.");
        }

        Long postId = parseNumericTargetId(parts[1], "Post report targetId must be numeric.");
        return switch (parts[0].toLowerCase(Locale.ROOT)) {
            case "free" -> {
                FreeBoard board = freeBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("Report target not found."));
                if (reporter.getId().equals(board.getUser().getId())) {
                    throw new IllegalArgumentException("You cannot report your own post.");
                }
                yield new ResolvedTarget("free-" + board.getId(), "[자유] " + board.getTitle());
            }
            case "study" -> {
                StudyBoard study = studyBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("Report target not found."));
                if (reporter.getId().equals(study.getUser().getId())) {
                    throw new IllegalArgumentException("You cannot report your own post.");
                }
                yield new ResolvedTarget("study-" + study.getId(), "[스터디] " + study.getTitle());
            }
            default -> throw new IllegalArgumentException("Unsupported post target type: " + parts[0]);
        };
    }

    private ResolvedTarget resolveCommentTarget(User reporter, String rawTargetId) {
        Long commentId = parseNumericTargetId(rawTargetId, "Comment report targetId must be numeric.");
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Report target not found."));
        if (reporter.getId().equals(comment.getUser().getId())) {
            throw new IllegalArgumentException("You cannot report your own comment.");
        }

        String label = UserDisplayName.nickname(comment.getUser()) + " · " + abbreviate(comment.getContent(), 36);
        return new ResolvedTarget(String.valueOf(comment.getId()), label);
    }

    private ResolvedTarget resolveChatTarget(User reporter, String rawTargetId) {
        Long roomId = parseNumericTargetId(rawTargetId, "Chat report targetId must be numeric.");
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Report target not found."));
        if (!chatParticipantRepository.existsByChatRoomIdAndUserId(roomId, reporter.getId())) {
            throw new IllegalArgumentException("You can only report chat rooms you joined.");
        }

        ChatParticipant partner = chatParticipantRepository.findByChatRoomId(roomId).stream()
                .filter(participant -> !participant.getUser().getId().equals(reporter.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Chat report target not found."));

        String roomLabel = room.getName();
        if (roomLabel == null || roomLabel.isBlank()) {
            roomLabel = UserDisplayName.nickname(partner.getUser()) + " 채팅방";
        }
        return new ResolvedTarget(String.valueOf(room.getId()), roomLabel);
    }

    private ResolvedTarget resolveInquiryTarget(
            User reporter,
            String rawTargetId,
            String rawTargetLabel
    ) {
        String targetId = rawTargetId == null || rawTargetId.trim().isBlank()
                ? "support-" + reporter.getId()
                : rawTargetId.trim();
        String targetLabel = normalizeRequired(rawTargetLabel, "targetLabel");
        return new ResolvedTarget(targetId, targetLabel);
    }

    private Long parseNumericTargetId(String targetId, String errorMessage) {
        String normalized = normalizeRequired(targetId, "targetId");
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return normalized;
    }

    private String abbreviate(String value, int maxLength) {
        String normalized = normalizeRequired(value, "label");
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 1)) + "…";
    }

    private record ResolvedTarget(String targetId, String targetLabel) {
    }
}
