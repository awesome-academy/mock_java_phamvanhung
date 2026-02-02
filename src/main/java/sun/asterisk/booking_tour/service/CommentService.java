package sun.asterisk.booking_tour.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.config.CustomUserDetails;
import sun.asterisk.booking_tour.dto.comment.CommentResponse;
import sun.asterisk.booking_tour.dto.comment.CreateCommentRequest;
import sun.asterisk.booking_tour.dto.user.UserBasicResponse;
import sun.asterisk.booking_tour.entity.Comment;
import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.exception.UnauthorizedException;
import sun.asterisk.booking_tour.exception.ValidationException;
import sun.asterisk.booking_tour.repository.CommentRepository;
import sun.asterisk.booking_tour.repository.ReviewRepository;
import sun.asterisk.booking_tour.repository.TourRepository;
import sun.asterisk.booking_tour.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Validate: must have either tourId or reviewId, but not both
        if ((request.getTourId() == null && request.getReviewId() == null) ||
            (request.getTourId() != null && request.getReviewId() != null)) {
            throw new ValidationException("Must provide either tourId or reviewId, but not both");
        }
        
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(request.getContent());
        
        // Set tour or review
        if (request.getTourId() != null) {
            Tour tour = tourRepository.findById(request.getTourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
            comment.setTour(tour);
        }
        
        if (request.getReviewId() != null) {
            Review review = reviewRepository.findById(request.getReviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
            comment.setReview(review);
        }
        
        // Set parent comment if this is a reply
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParent(parent);
        }
        
        comment = commentRepository.save(comment);
        log.info("Created comment with id: {} by user: {}", comment.getId(), userId);
        
        return mapToCommentResponse(comment, false);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Long userId = getCurrentUserId();
        
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found or you don't have permission"));
        
        commentRepository.delete(comment);
        log.info("Deleted comment with id: {} by user: {}", commentId, userId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        
        List<Comment> comments = commentRepository.findByReviewIdWithUser(reviewId);
        return comments.stream()
                .map(comment -> mapToCommentResponse(comment, true))
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment, boolean includeReplies) {
        String userFullName = "";
        if (comment.getUser().getFirstName() != null && comment.getUser().getLastName() != null) {
            userFullName = comment.getUser().getFirstName() + " " + comment.getUser().getLastName();
        } else if (comment.getUser().getFirstName() != null) {
            userFullName = comment.getUser().getFirstName();
        } else if (comment.getUser().getLastName() != null) {
            userFullName = comment.getUser().getLastName();
        }
        
        List<CommentResponse> replies = new ArrayList<>();
        if (includeReplies && comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replies = comment.getReplies().stream()
                    .map(reply -> mapToCommentResponse(reply, false))
                    .collect(Collectors.toList());
        }
        
        UserBasicResponse userBasicResponse = UserBasicResponse.builder()
                .id(comment.getUser().getId())
                .fullName(userFullName.trim())
                .avatarUrl(comment.getUser().getAvatarUrl())
                .build();
        
        return CommentResponse.builder()
                .id(comment.getId())
                .user(userBasicResponse)
                .tourId(comment.getTour() != null ? comment.getTour().getId() : null)
                .reviewId(comment.getReview() != null ? comment.getReview().getId() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .replyCount(comment.getReplies() != null ? comment.getReplies().size() : 0)
                .replies(replies)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnauthorizedException("User not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
