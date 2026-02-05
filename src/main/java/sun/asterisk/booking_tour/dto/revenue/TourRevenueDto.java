package sun.asterisk.booking_tour.dto.revenue;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record TourRevenueDto(
    Long tourId,
    String tourName,
    Long bookingCount,
    BigDecimal totalRevenue,
    Double growthRate
) {
}
