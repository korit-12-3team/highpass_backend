package com.example.highpass_backend.service.admin;

import com.example.highpass_backend.dto.admin.AdminPostResponse;
import com.example.highpass_backend.dto.admin.AdminReportChatMessageResponse;
import com.example.highpass_backend.dto.admin.AdminReportResponse;
import com.example.highpass_backend.dto.admin.AdminUserResponse;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.chat.ChatMessage;
import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.entity.report.Report;
import com.example.highpass_backend.entity.auth.OAuth2Account;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.dto.user.UserDisplayName;
import com.example.highpass_backend.repository.report.ReportRepository;
import com.example.highpass_backend.repository.auth.OAuth2AccountRepository;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.chat.ChatMessageRepository;
import com.example.highpass_backend.repository.chat.ChatParticipantRepository;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import com.example.highpass_backend.service.user.UserPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final StudyBoardRepository studyBoardRepository;
    private final CommentRepository commentRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;
    private final UserPresenceService userPresenceService;
    private final ReportRepository reportRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers(Long adminUserId) {
        requireAdmin(adminUserId);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != User.Role.ADMIN)
                .map(user -> AdminUserResponse.from(
                        user,
                        findSocialProvider(user.getId()),
                        userPresenceService.isOnline(user.getId()),
                        countPosts(user.getId()),
                        countUserComments(user.getId()),
                        countReportsForUser(user.getId())
                ))
                .toList();
    }

    @Transactional
    public AdminUserResponse updateUserStatus(Long adminUserId, Long userId, String status) {
        requireAdmin(adminUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.updateStatus(parseUserStatus(status));

        return AdminUserResponse.from(
                user,
                findSocialProvider(user.getId()),
                userPresenceService.isOnline(user.getId()),
                countPosts(user.getId()),
                countUserComments(user.getId()),
                countReportsForUser(user.getId())
        );
    }

    @Transactional(readOnly = true)
    public List<AdminPostResponse> getPosts(Long adminUserId) {
        requireAdmin(adminUserId);
        List<AdminPostResponse> posts = new ArrayList<>();

        freeBoardRepository.findAll().forEach(board -> posts.add(toFreePostResponse(board)));
        studyBoardRepository.findAll().forEach(study -> posts.add(toStudyPostResponse(study)));

        return posts.stream()
                .sorted(Comparator.comparing(AdminPostResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    @Transactional
    public AdminPostResponse updatePostStatus(Long adminUserId, String postId, String status) {
        requireAdmin(adminUserId);
        String[] parts = postId.split("-", 2);
        if (parts.length == 2 && "study".equalsIgnoreCase(parts[0])) {
            return updateStudyStatus(Long.parseLong(parts[1]), status);
        }
        if (parts.length == 2 && "free".equalsIgnoreCase(parts[0])) {
            return updateFreeStatus(Long.parseLong(parts[1]), status);
        }

        Long numericId = Long.parseLong(postId);
        return freeBoardRepository.findById(numericId)
                .map(board -> {
                    board.updateStatus(parseFreeStatus(status));
                    return toFreePostResponse(board);
                })
                .orElseGet(() -> updateStudyStatus(numericId, status));
    }

    @Transactional(readOnly = true)
    public List<AdminReportResponse> getReports(Long adminUserId) {
        requireAdmin(adminUserId);
        return reportRepository.findAll().stream()
                .sorted(Comparator.comparing(Report::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toAdminReportResponse)
                .toList();
    }

    @Transactional
    public AdminReportResponse updateReportStatus(Long adminUserId, String reportId, String status) {
        requireAdmin(adminUserId);
        Report report = reportRepository.findById(Long.parseLong(reportId))
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.updateStatus(parseReportStatus(status));
        return toAdminReportResponse(report);
    }

    private void requireAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin permission is required.");
        }
    }

    private AdminPostResponse updateFreeStatus(Long postId, String status) {
        FreeBoard board = freeBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Free board not found."));
        board.updateStatus(parseFreeStatus(status));
        return toFreePostResponse(board);
    }

    private AdminPostResponse updateStudyStatus(Long postId, String status) {
        StudyBoard study = studyBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Study board not found."));
        study.updateStatus(parseStudyStatus(status));
        return toStudyPostResponse(study);
    }

    private AdminPostResponse toFreePostResponse(FreeBoard board) {
        FreeBoard.Status status = board.getStatus() == null ? FreeBoard.Status.VISIBLE : board.getStatus();
        return new AdminPostResponse(
                "free-" + board.getId(),
                "free",
                board.getTitle(),
                board.getContent(),
                String.valueOf(board.getUser().getId()),
                UserDisplayName.nickname(board.getUser()),
                status.name().toLowerCase(),
                board.getCreatedAt(),
                board.getViewCount(),
                commentRepository.findByTargetIdAndTargetType(board.getId(), Comment.TargetType.FREE).size(),
                reportRepository.countByTargetTypeAndTargetId(Report.TargetType.POST, "free-" + board.getId())
        );
    }

    private AdminPostResponse toStudyPostResponse(StudyBoard study) {
        StudyBoard.Status status = study.getStatus() == null ? StudyBoard.Status.VISIBLE : study.getStatus();
        return new AdminPostResponse(
                "study-" + study.getId(),
                "study",
                study.getTitle(),
                study.getContent(),
                String.valueOf(study.getUser().getId()),
                UserDisplayName.nickname(study.getUser()),
                status.name().toLowerCase(),
                study.getCreatedAt(),
                study.getViewCount(),
                commentRepository.findByTargetIdAndTargetType(study.getId(), Comment.TargetType.STUDY).size(),
                reportRepository.countByTargetTypeAndTargetId(Report.TargetType.POST, "study-" + study.getId())
        );
    }

    private int countPosts(Long userId) {
        return freeBoardRepository.findByUserId(userId).size()
                + studyBoardRepository.findAll().stream()
                .filter(study -> study.getUser().getId().equals(userId))
                .toList()
                .size();
    }

    private int countUserComments(Long userId) {
        return (int) commentRepository.findAll().stream()
                .filter(comment -> comment.getUser().getId().equals(userId))
                .count();
    }

    private int countReportsForUser(Long userId) {
        return reportRepository.countByTargetTypeAndTargetId(Report.TargetType.USER, String.valueOf(userId));
    }

    private String findSocialProvider(Long userId) {
        return oauth2AccountRepository.findAllByUserId(userId).stream()
                .findFirst()
                .map(OAuth2Account::getProvider)
                .map(Enum::name)
                .orElse("");
    }

    private User.Status parseUserStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "active" -> User.Status.ACTIVE;
            case "suspended" -> User.Status.SUSPENDED;
            case "deleted" -> User.Status.DELETED;
            default -> throw new IllegalArgumentException("Unsupported user status: " + status);
        };
    }

    private FreeBoard.Status parseFreeStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "visible" -> FreeBoard.Status.VISIBLE;
            case "hidden" -> FreeBoard.Status.HIDDEN;
            case "deleted" -> FreeBoard.Status.DELETED;
            default -> throw new IllegalArgumentException("Unsupported post status: " + status);
        };
    }

    private StudyBoard.Status parseStudyStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "visible" -> StudyBoard.Status.VISIBLE;
            case "hidden" -> StudyBoard.Status.HIDDEN;
            case "deleted" -> StudyBoard.Status.DELETED;
            default -> throw new IllegalArgumentException("Unsupported post status: " + status);
        };
    }

    private Report.Status parseReportStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "pending" -> Report.Status.PENDING;
            case "resolved" -> Report.Status.RESOLVED;
            case "dismissed" -> Report.Status.DISMISSED;
            default -> throw new IllegalArgumentException("Unsupported report status: " + status);
        };
    }

    private AdminReportResponse toAdminReportResponse(Report report) {
        User reporter = report.getReporter();
        String reporterName = reporter == null ? "" : UserDisplayName.nickname(reporter);
        String reporterEmail = reporter == null ? "" : safe(reporter.getEmail());

        AdminReportDetail detail = switch (report.getTargetType()) {
            case USER -> buildUserReportDetail(report);
            case POST -> buildPostReportDetail(report);
            case COMMENT -> buildCommentReportDetail(report);
            case CHAT -> buildChatReportDetail(report);
            case INQUIRY -> AdminReportDetail.empty();
        };

        return new AdminReportResponse(
                String.valueOf(report.getId()),
                report.getTargetType().name().toLowerCase(),
                report.getTargetId(),
                report.getTargetLabel(),
                report.getReason(),
                reporterName,
                reporterEmail,
                report.getCreatedAt() == null ? "" : report.getCreatedAt().toString(),
                report.getStatus().name().toLowerCase(),
                detail.targetUserId(),
                detail.targetUserNickname(),
                detail.targetUserEmail(),
                detail.postId(),
                detail.postType(),
                detail.postTitle(),
                detail.postContent(),
                detail.postAuthor(),
                detail.commentId(),
                detail.commentContent(),
                detail.commentAuthor(),
                detail.commentPostType(),
                detail.commentPostId(),
                detail.commentPostTitle(),
                detail.chatRoomId(),
                detail.chatRoomName(),
                detail.chatPartnerId(),
                detail.chatPartnerNickname(),
                detail.chatPartnerEmail(),
                detail.chatMessages()
        );
    }

    private AdminReportDetail buildUserReportDetail(Report report) {
        return userRepository.findById(Long.parseLong(report.getTargetId()))
                .map(user -> new AdminReportDetail(
                        String.valueOf(user.getId()),
                        UserDisplayName.nickname(user),
                        safe(user.getEmail()),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of()
                ))
                .orElseGet(AdminReportDetail::empty);
    }

    private AdminReportDetail buildPostReportDetail(Report report) {
        String targetId = safe(report.getTargetId());
        String[] parts = targetId.split("-", 2);
        if (parts.length != 2) {
            return AdminReportDetail.empty();
        }

        Long postId = Long.parseLong(parts[1]);
        if ("free".equalsIgnoreCase(parts[0])) {
            return freeBoardRepository.findById(postId)
                    .map(board -> new AdminReportDetail(
                            String.valueOf(board.getUser().getId()),
                            UserDisplayName.nickname(board.getUser()),
                            safe(board.getUser().getEmail()),
                            String.valueOf(board.getId()),
                            "free",
                            board.getTitle(),
                            safe(board.getContent()),
                            UserDisplayName.nickname(board.getUser()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            List.of()
                    ))
                    .orElseGet(AdminReportDetail::empty);
        }

        return studyBoardRepository.findById(postId)
                .map(study -> new AdminReportDetail(
                        String.valueOf(study.getUser().getId()),
                        UserDisplayName.nickname(study.getUser()),
                        safe(study.getUser().getEmail()),
                        String.valueOf(study.getId()),
                        "study",
                        study.getTitle(),
                        safe(study.getContent()),
                        UserDisplayName.nickname(study.getUser()),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of()
                ))
                .orElseGet(AdminReportDetail::empty);
    }

    private AdminReportDetail buildCommentReportDetail(Report report) {
        return commentRepository.findById(Long.parseLong(report.getTargetId()))
                .map(comment -> {
                    String commentPostType = comment.getTargetType().name().toLowerCase(Locale.ROOT);
                    Long commentPostId = comment.getTargetId();
                    String commentPostTitle = resolveCommentPostTitle(comment);

                    return new AdminReportDetail(
                            String.valueOf(comment.getUser().getId()),
                            UserDisplayName.nickname(comment.getUser()),
                            safe(comment.getUser().getEmail()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            String.valueOf(comment.getId()),
                            safe(comment.getContent()),
                            UserDisplayName.nickname(comment.getUser()),
                            commentPostType,
                            String.valueOf(commentPostId),
                            commentPostTitle,
                            null,
                            null,
                            null,
                            List.of()
                    );
                })
                .orElseGet(AdminReportDetail::empty);
    }

    private AdminReportDetail buildChatReportDetail(Report report) {
        Long roomId = Long.parseLong(report.getTargetId());
        return chatRoomRepository.findById(roomId)
                .map(room -> {
                    User reporter = report.getReporter();
                    ChatParticipant partner = chatParticipantRepository.findByChatRoomId(roomId).stream()
                            .filter(participant -> reporter == null || !participant.getUser().getId().equals(reporter.getId()))
                            .findFirst()
                            .orElse(null);

                    List<AdminReportChatMessageResponse> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId).stream()
                            .filter(message -> message.getType() == ChatMessage.MessageType.TALK)
                            .sorted(Comparator.comparing(ChatMessage::getCreatedAt).reversed())
                            .limit(10)
                            .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                            .map(message -> new AdminReportChatMessageResponse(
                                    String.valueOf(message.getId()),
                                    message.getSender() == null ? "알 수 없음" : UserDisplayName.nickname(message.getSender()),
                                    safe(message.getMessage()),
                                    message.getCreatedAt() == null ? "" : message.getCreatedAt().toString()
                            ))
                            .toList();

                    return new AdminReportDetail(
                            partner == null ? null : String.valueOf(partner.getUser().getId()),
                            partner == null ? null : UserDisplayName.nickname(partner.getUser()),
                            partner == null ? null : safe(partner.getUser().getEmail()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            String.valueOf(room.getId()),
                            safe(room.getName()),
                            partner == null ? null : String.valueOf(partner.getUser().getId()),
                            partner == null ? null : UserDisplayName.nickname(partner.getUser()),
                            partner == null ? null : safe(partner.getUser().getEmail()),
                            messages
                    );
                })
                .orElseGet(AdminReportDetail::empty);
    }

    private String resolveCommentPostTitle(Comment comment) {
        if (comment.getTargetType() == Comment.TargetType.FREE) {
            return freeBoardRepository.findById(comment.getTargetId())
                    .map(FreeBoard::getTitle)
                    .orElse("");
        }
        return studyBoardRepository.findById(comment.getTargetId())
                .map(StudyBoard::getTitle)
                .orElse("");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    private record AdminReportDetail(
            String targetUserId,
            String targetUserNickname,
            String targetUserEmail,
            String postId,
            String postType,
            String postTitle,
            String postContent,
            String postAuthor,
            String commentId,
            String commentContent,
            String commentAuthor,
            String commentPostType,
            String commentPostId,
            String commentPostTitle,
            String chatRoomId,
            String chatRoomName,
            String chatPartnerId,
            String chatPartnerNickname,
            String chatPartnerEmail,
            List<AdminReportChatMessageResponse> chatMessages
    ) {
        private static AdminReportDetail empty() {
            return new AdminReportDetail(
                    null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    List.of()
            );
        }
    }
}
