package sun.asterisk.booking_tour.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.config.CustomUserDetails;
import sun.asterisk.booking_tour.dto.common.PageResponse;
import sun.asterisk.booking_tour.dto.review.CreateReviewRequest;
import sun.asterisk.booking_tour.dto.review.ReviewResponse;
import sun.asterisk.booking_tour.dto.review.UpdateReviewRequest;
import sun.asterisk.booking_tour.dto.user.UserBasicResponse;
import sun.asterisk.booking_tour.entity.Booking;
import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.enums.ReviewStatus;
import sun.asterisk.booking_tour.exception.ForbiddenException;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.exception.UnauthorizedException;
import sun.asterisk.booking_tour.repository.BookingRepository;
import sun.asterisk.booking_tour.repository.CommentRepository;
import sun.asterisk.booking_tour.repository.LikeRepository;
import sun.asterisk.booking_tour.repository.ReviewRepository;
import sun.asterisk.booking_tour.repository.TourRepository;
import sun.asterisk.booking_tour.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
        
        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        }
        
        Review review = new Review();
        review.setUser(user);
        review.setTour(tour);
        review.setBooking(booking);
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setStatus(ReviewStatus.PENDING);
        
        review = reviewRepository.save(review);
        log.info("Created review with id: {} for tour: {} by user: {}", review.getId(), tour.getId(), userId);
        
        return mapToReviewResponse(review, userId);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        Long userId = getCurrentUserId();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this review");
        }
        
        // Only update fields that are provided
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            review.setContent(request.getContent());
        }
        
        review = reviewRepository.save(review);
        log.info("Updated review with id: {} by user: {}", reviewId, userId);
        
        return mapToReviewResponse(review, userId);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Long userId = getCurrentUserId();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this review");
        }
        
        reviewRepository.delete(review);
        log.info("Deleted review with id: {} by user: {}", reviewId, userId);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Long userId = getCurrentUserIdOrNull();
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        return mapToReviewResponse(review, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getMyReviews(Pageable pageable) {
        Long userId = getCurrentUserId();
        
        Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);
        
        List<ReviewResponse> reviews = mapToReviewResponseList(reviewPage.getContent(), userId);
        
        return PageResponse.<ReviewResponse>builder()
                .content(reviews)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .isLast(reviewPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviewsByTour(Long tourId, Pageable pageable) {
        Long userId = getCurrentUserIdOrNull();
        
        if (!tourRepository.existsById(tourId)) {
            throw new ResourceNotFoundException("Tour not found");
        }
        
        Page<Review> reviewPage = reviewRepository.findByTourIdAndStatus(
                tourId, ReviewStatus.APPROVED, pageable);
        
        List<ReviewResponse> reviews = mapToReviewResponseList(reviewPage.getContent(), userId);
        
        return PageResponse.<ReviewResponse>builder()
                .content(reviews)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .isLast(reviewPage.isLast())
                .build();
    }

    private List<ReviewResponse> mapToReviewResponseList(List<Review> reviews, Long currentUserId) {
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Extract review IDs
        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList());
        
        // Bulk fetch like counts
        Map<Long, Long> likeCountMap = likeRepository.countLikesByReviewIds(reviewIds)
                .stream()
                .collect(Collectors.toMap(
                    LikeRepository.ReviewLikeCount::getReviewId,
                    LikeRepository.ReviewLikeCount::getLikeCount
                ));
        
        // Bulk fetch comment counts
        Map<Long, Long> commentCountMap = commentRepository.countCommentsByReviewIds(reviewIds)
                .stream()
                .collect(Collectors.toMap(
                    CommentRepository.ReviewCommentCount::getReviewId,
                    CommentRepository.ReviewCommentCount::getCommentCount
                ));
        
        // Bulk fetch liked review IDs for current user
        Set<Long> likedReviewIds = currentUserId != null
                ? likeRepository.findLikedReviewIdsByUser(currentUserId, reviewIds)
                    .stream()
                    .collect(Collectors.toSet())
                : Collections.emptySet();
        
        // Map reviews to responses
        return reviews.stream()
                .map(review -> mapToReviewResponse(
                    review,
                    likeCountMap.getOrDefault(review.getId(), 0L),
                    commentCountMap.getOrDefault(review.getId(), 0L),
                    likedReviewIds.contains(review.getId())
                ))
                .collect(Collectors.toList());
    }
    
    private ReviewResponse mapToReviewResponse(Review review, Long currentUserId) {
        Long likeCount = likeRepository.countByReviewId(review.getId());
        Long commentCount = commentRepository.countByReviewId(review.getId());
        Boolean isLiked = currentUserId != null && 
                likeRepository.existsByUserIdAndReviewId(currentUserId, review.getId());
        
        return mapToReviewResponse(review, likeCount, commentCount, isLiked);
    }
    
    private ReviewResponse mapToReviewResponse(Review review, Long likeCount, Long commentCount, Boolean isLiked) {
        
        String userFullName = "";
        if (review.getUser().getFirstName() != null && review.getUser().getLastName() != null) {
            userFullName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
        } else if (review.getUser().getFirstName() != null) {
            userFullName = review.getUser().getFirstName();
        } else if (review.getUser().getLastName() != null) {
            userFullName = review.getUser().getLastName();
        }
        
        UserBasicResponse userBasicResponse = UserBasicResponse.builder()
                .id(review.getUser().getId())
                .fullName(userFullName.trim())
                .avatarUrl(review.getUser().getAvatarUrl())
                .build();
        
        return ReviewResponse.builder()
                .id(review.getId())
                .user(userBasicResponse)
                .tourId(review.getTour().getId())
                .tourName(review.getTour().getName())
                .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                .bookingCode(review.getBooking() != null ? review.getBooking().getCode() : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .status(review.getStatus())
                .commentCount(commentCount != null ? commentCount.intValue() : 0)
                .likeCount(likeCount != null ? likeCount.intValue() : 0)
                .isLiked(isLiked)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
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

    private Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            log.debug("No authenticated user found: {}", e.getMessage());
            return null;
        }
    }
}
