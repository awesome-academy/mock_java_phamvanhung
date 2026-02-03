package sun.asterisk.booking_tour.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Review page request for pagination")
public class ReviewPageRequest {

    @Builder.Default
    @Schema(description = "Page number (0-indexed)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Builder.Default
    @Schema(description = "Page size", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Builder.Default
    @Schema(description = "Sort field", example = "createdAt", defaultValue = "createdAt")
    private String sortBy = "createdAt";

    @Builder.Default
    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC", defaultValue = "DESC")
    private String sortDirection = "DESC";
}
