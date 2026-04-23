package com.example.highpass_backend.service.admin;

import com.example.highpass_backend.dto.admin.AdminPostResponse;
import com.example.highpass_backend.dto.admin.AdminReportResponse;
import com.example.highpass_backend.dto.admin.AdminUserResponse;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.auth.OAuth2Account;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.dto.user.UserDisplayName;
import com.example.highpass_backend.repository.auth.OAuth2AccountRepository;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
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

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers(Long adminUserId) {
        requireAdmin(adminUserId);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != User.Role.ADMIN)
                .map(user -> AdminUserResponse.from(user, findSocialProvider(user.getId()), userPresenceService.isOnline(user.getId()), countPosts(user.getId()), countUserComments(user.getId())))
                .toList();
    }

    @Transactional
    public AdminUserResponse updateUserStatus(Long adminUserId, Long userId, String status) {
        requireAdmin(adminUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.updateStatus(parseUserStatus(status));

        return AdminUserResponse.from(user, findSocialProvider(user.getId()), userPresenceService.isOnline(user.getId()), countPosts(user.getId()), countUserComments(user.getId()));
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
        return List.of();
    }

    @Transactional
    public AdminReportResponse updateReportStatus(Long adminUserId, String reportId, String status) {
        requireAdmin(adminUserId);
        return new AdminReportResponse(reportId, "", "", "", "Report domain is not implemented yet.", "", "", normalizeStatus(status));
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
                0
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
                0
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

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }
}
