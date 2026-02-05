package sun.asterisk.booking_tour.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.config.CustomUserDetails;
import sun.asterisk.booking_tour.dto.like.LikeResponse;
import sun.asterisk.booking_tour.entity.Like;
import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.exception.UnauthorizedException;
import sun.asterisk.booking_tour.exception.ValidationException;
import sun.asterisk.booking_tour.repository.LikeRepository;
import sun.asterisk.booking_tour.repository.ReviewRepository;
import sun.asterisk.booking_tour.repository.TourRepository;
import sun.asterisk.booking_tour.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public LikeResponse toggleLikeTour(Long tourId) {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
        
        var existingLike = likeRepository.findByUserIdAndTourId(userId, tourId);
        
        if (existingLike.isPresent()) {
            // Unlike
            likeRepository.delete(existingLike.get());
            Long totalLikes = likeRepository.countByTourId(tourId);
            log.info("Unliked tour {} by user {}", tourId, userId);
            
            return LikeResponse.builder()
                    .success(true)
                    .isLiked(false)
                    .totalLikes(totalLikes.intValue())
                    .message("Unliked successfully")
                    .build();
        } else {
            // Like
            Like like = new Like();
            like.setUser(user);
            like.setTour(tour);
            likeRepository.save(like);
            
            Long totalLikes = likeRepository.countByTourId(tourId);
            log.info("Liked tour {} by user {}", tourId, userId);
            
            return LikeResponse.builder()
                    .success(true)
                    .isLiked(true)
                    .totalLikes(totalLikes.intValue())
                    .message("Liked successfully")
                    .build();
        }
    }

    @Transactional
    public LikeResponse toggleLikeReview(Long reviewId) {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        var existingLike = likeRepository.findByUserIdAndReviewId(userId, reviewId);
        
        if (existingLike.isPresent()) {
            // Unlike
            likeRepository.delete(existingLike.get());
            Long totalLikes = likeRepository.countByReviewId(reviewId);
            log.info("Unliked review {} by user {}", reviewId, userId);
            
            return LikeResponse.builder()
                    .success(true)
                    .isLiked(false)
                    .totalLikes(totalLikes.intValue())
                    .message("Unliked successfully")
                    .build();
        } else {
            // Like
            Like like = new Like();
            like.setUser(user);
            like.setReview(review);
            likeRepository.save(like);
            
            Long totalLikes = likeRepository.countByReviewId(reviewId);
            log.info("Liked review {} by user {}", reviewId, userId);
            
            return LikeResponse.builder()
                    .success(true)
                    .isLiked(true)
                    .totalLikes(totalLikes.intValue())
                    .message("Liked successfully")
                    .build();
        }
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
