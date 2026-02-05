package sun.asterisk.booking_tour.controller.client;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.dto.comment.CommentResponse;
import sun.asterisk.booking_tour.dto.common.PageResponse;
import sun.asterisk.booking_tour.dto.review.CreateReviewRequest;
import sun.asterisk.booking_tour.dto.review.ReviewPageRequest;
import sun.asterisk.booking_tour.dto.review.ReviewResponse;
import sun.asterisk.booking_tour.dto.review.UpdateReviewRequest;
import sun.asterisk.booking_tour.service.CommentService;
import sun.asterisk.booking_tour.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "API endpoints for review management")
public class ReviewController {

    private final ReviewService reviewService;
    private final CommentService commentService;

    @Operation(summary = "Create a review", description = "Create a new review for a tour. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.BadRequest
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a review", 
               description = "Update your own review. All fields (rating, title, content) are optional - only provided fields will be updated. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.Forbidden
    @CommonApiResponses.NotFound
    @CommonApiResponses.BadRequest
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a review", description = "Delete your own review. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.Forbidden
    @CommonApiResponses.NotFound
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get review by ID", description = "Get a single review by its ID")
    @CommonApiResponses.NotFound
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId) {
        ReviewResponse response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get my reviews", description = "Get all reviews created by current user with pagination. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @GetMapping("/my-reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getMyReviews(
            @Valid @ModelAttribute ReviewPageRequest request) {
        PageResponse<ReviewResponse> response = reviewService.getMyReviews(request.toPageable());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get reviews by tour", description = "Get all approved reviews for a specific tour with pagination")
    @CommonApiResponses.NotFound
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByTour(
            @Parameter(description = "Tour ID", required = true) @PathVariable Long tourId,
            @Valid @ModelAttribute ReviewPageRequest request) {
        PageResponse<ReviewResponse> response = reviewService.getReviewsByTour(tourId, request.toPageable());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get comments by review", description = "Get all comments for a specific review")
    @CommonApiResponses.NotFound
    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId) {
        List<CommentResponse> response = commentService.getCommentsByReview(reviewId);
        return ResponseEntity.ok(response);
    }
}
