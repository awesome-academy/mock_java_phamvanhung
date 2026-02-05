package sun.asterisk.booking_tour.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "Category statistics response")
public class CategoryStatsResponse {

    @Schema(description = "Total categories", example = "24")
    private Long totalCategories;

    @Schema(description = "Active categories", example = "20")
    private Long activeCategories;

    @Schema(description = "Inactive categories", example = "4")
    private Long inactiveCategories;
}
