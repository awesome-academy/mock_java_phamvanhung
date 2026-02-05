package sun.asterisk.booking_tour.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.dto.like.LikeResponse;
import sun.asterisk.booking_tour.service.LikeService;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "API endpoints for like/unlike functionality")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Toggle like on tour", 
               description = "Like or unlike a tour. If already liked, it will unlike. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.NotFound
    @PostMapping("/tour/{tourId}")
    public ResponseEntity<LikeResponse> toggleLikeTour(
            @Parameter(description = "Tour ID", required = true) @PathVariable Long tourId) {
        LikeResponse response = likeService.toggleLikeTour(tourId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle like on review", 
               description = "Like or unlike a review. If already liked, it will unlike. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.NotFound
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<LikeResponse> toggleLikeReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId) {
        LikeResponse response = likeService.toggleLikeReview(reviewId);
        return ResponseEntity.ok(response);
    }
}
