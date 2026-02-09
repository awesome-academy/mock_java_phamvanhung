package sun.asterisk.booking_tour.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.asterisk.booking_tour.enums.CategoryStatus;

import java.time.LocalDateTime;
import lombok.Data;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "Category response")
public class CategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Tour miền núi")
    private String name;

    @Schema(description = "Category description", example = "Các tour khám phá miền núi phía Bắc")
    private String description;

    @Schema(description = "Category slug", example = "tour-mien-nui")
    private String slug;

    @Schema(description = "Category status", example = "ACTIVE")
    private CategoryStatus status;

    @Schema(description = "Number of tours in category", example = "25")
    private Long tourCount;

    @Schema(description = "Created date")
    private LocalDateTime createdAt;

    @Schema(description = "Updated date")
    private LocalDateTime updatedAt;
}
