package sun.asterisk.booking_tour.dto.common;

import java.util.List;

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
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items in current page")
    private List<T> content;

    @Schema(description = "Current page number (0-based)", example = "0")
    private Integer pageNumber;

    @Schema(description = "Page size", example = "10")
    private Integer pageSize;

    @Schema(description = "Total elements", example = "100")
    private Long totalElements;

    @Schema(description = "Total pages", example = "10")
    private Integer totalPages;

    @Schema(description = "Is first page", example = "true")
    private Boolean isFirst;

    @Schema(description = "Is last page", example = "false")
    private Boolean isLast;

    @Schema(description = "Has next page", example = "true")
    private Boolean hasNext;

    @Schema(description = "Has previous page", example = "false")
    private Boolean hasPrevious;
}
