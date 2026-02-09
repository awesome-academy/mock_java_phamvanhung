package sun.asterisk.booking_tour.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.enums.ReviewStatus;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getReviews(ReviewStatus status, Pageable pageable) {
        Page<Review> page;
        if (status != null) {
            page = reviewRepository.findByStatus(status, pageable);
        } else {
            page = reviewRepository.findAll(pageable);
        }
        return page.map(AdminReviewService::toReviewViewModel);
    }

    @Transactional(readOnly = true)
    public long getTotalReviews() {
        return reviewRepository.count();
    }

    @Transactional(readOnly = true)
    public double getAverageRatingAll() {
        Double avg = reviewRepository.findAverageRatingAll();
        return avg != null ? avg : 0.0;
    }

    @Transactional(readOnly = true)
    public long getPendingReviews() {
        Long pending = reviewRepository.countByStatus(ReviewStatus.PENDING);
        return pending != null ? pending : 0L;
    }

    @Transactional
    public void updateStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        review.setStatus(status);
        reviewRepository.save(review);
    }

    public List<String> reviewStatusNames() {
        return Arrays.stream(ReviewStatus.values()).map(Enum::name).toList();
    }

    private static Map<String, Object> toReviewViewModel(Review review) {
        Map<String, Object> viewModel = new HashMap<>();
        viewModel.put("id", review.getId());
        viewModel.put("rating", review.getRating());
        viewModel.put("title", review.getTitle());
        viewModel.put("content", review.getContent());
        viewModel.put("status", review.getStatus() != null ? review.getStatus().name() : null);
        viewModel.put("createdAt", review.getCreatedAt());

        viewModel.put("userName", review.getUser() != null
                ? (review.getUser().getFirstName() != null ? review.getUser().getFirstName() : "")
                        + (review.getUser().getLastName() != null ? (" " + review.getUser().getLastName()) : "")
                : null);
        viewModel.put("userEmail", review.getUser() != null ? review.getUser().getEmail() : null);
        viewModel.put("tourName", review.getTour() != null ? review.getTour().getName() : null);
        viewModel.put("tourId", review.getTour() != null ? review.getTour().getId() : null);

        viewModel.put("bookingCode", review.getBooking() != null ? review.getBooking().getCode() : null);
        viewModel.put("bookingId", review.getBooking() != null ? review.getBooking().getId() : null);

        return viewModel;
    }
}
