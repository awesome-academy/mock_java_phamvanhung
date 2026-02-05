package sun.asterisk.booking_tour.dto.revenue;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record MonthlyRevenueDto(
    Integer month,
    Integer year,
    BigDecimal revenue,
    Long bookingCount
) {
}
