package sun.asterisk.booking_tour.dto.revenue;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record RevenueStatsDto(
    BigDecimal revenueToday,
    Double growthRateToday,
    BigDecimal revenueThisMonth,
    Double growthRateThisMonth,
    BigDecimal revenueThisYear,
    Double growthRateThisYear
) {
}
