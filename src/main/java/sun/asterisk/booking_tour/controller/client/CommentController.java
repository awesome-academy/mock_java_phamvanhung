package sun.asterisk.booking_tour.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sun.asterisk.booking_tour.config.CommonApiResponses;
import sun.asterisk.booking_tour.dto.comment.CommentResponse;
import sun.asterisk.booking_tour.dto.comment.CreateCommentRequest;
import sun.asterisk.booking_tour.service.CommentService;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "API endpoints for comment management")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create a comment", 
               description = "Create a comment on a tour, review, or reply to another comment. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.BadRequest
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete a comment", description = "Delete your own comment. Requires authentication.")
    @CommonApiResponses.Unauthorized
    @CommonApiResponses.NotFound
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get comments by review", description = "Get all comments for a specific review")
    @CommonApiResponses.NotFound
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long reviewId) {
        List<CommentResponse> response = commentService.getCommentsByReview(reviewId);
        return ResponseEntity.ok(response);
    }
}
