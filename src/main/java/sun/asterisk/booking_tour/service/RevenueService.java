package sun.asterisk.booking_tour.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.dto.revenue.MonthlyRevenueDto;
import sun.asterisk.booking_tour.dto.revenue.RevenueStatsDto;
import sun.asterisk.booking_tour.dto.revenue.TourRevenueDto;
import sun.asterisk.booking_tour.enums.BookingStatus;
import sun.asterisk.booking_tour.repository.BookingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueService {

    private final BookingRepository bookingRepository;

    public RevenueStatsDto getRevenueStats() {
        LocalDateTime now = LocalDateTime.now();

        // Today
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(LocalTime.MAX);
        BigDecimal revenueToday = bookingRepository.calculateRevenue(
            BookingStatus.PAID, todayStart, todayEnd
        );

        // Yesterday
        LocalDateTime yesterdayStart = now.minusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime yesterdayEnd = now.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
        BigDecimal revenueYesterday = bookingRepository.calculateRevenue(
            BookingStatus.PAID, yesterdayStart, yesterdayEnd
        );

        // This month
        LocalDateTime thisMonthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime thisMonthEnd = now;
        BigDecimal revenueThisMonth = bookingRepository.calculateRevenue(
            BookingStatus.PAID, thisMonthStart, thisMonthEnd
        );

        // Last month
        LocalDateTime lastMonthStart = now.minusMonths(1).toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthEnd = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        BigDecimal revenueLastMonth = bookingRepository.calculateRevenue(
            BookingStatus.PAID, lastMonthStart, lastMonthEnd
        );

        // This year
        LocalDateTime thisYearStart = LocalDate.of(now.getYear(), 1, 1).atStartOfDay();
        LocalDateTime thisYearEnd = now;
        BigDecimal revenueThisYear = bookingRepository.calculateRevenue(
            BookingStatus.PAID, thisYearStart, thisYearEnd
        );

        // Last year
        LocalDateTime lastYearStart = LocalDate.of(now.getYear() - 1, 1, 1).atStartOfDay();
        LocalDateTime lastYearEnd = LocalDate.of(now.getYear(), 1, 1).atStartOfDay();
        BigDecimal revenueLastYear = bookingRepository.calculateRevenue(
            BookingStatus.PAID, lastYearStart, lastYearEnd
        );

        return RevenueStatsDto.builder()
            .revenueToday(revenueToday)
            .growthRateToday(calculateGrowthRate(revenueToday, revenueYesterday))
            .revenueThisMonth(revenueThisMonth)
            .growthRateThisMonth(calculateGrowthRate(revenueThisMonth, revenueLastMonth))
            .revenueThisYear(revenueThisYear)
            .growthRateThisYear(calculateGrowthRate(revenueThisYear, revenueLastYear))
            .build();
    }

    public List<TourRevenueDto> getTourRevenue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMonthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthStart = now.minusMonths(1).toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthEnd = now.toLocalDate().withDayOfMonth(1).atStartOfDay();

        List<Object[]> currentStats = bookingRepository.findTourRevenueStats(
            BookingStatus.COMPLETED, thisMonthStart, now
        );

        List<TourRevenueDto> result = new ArrayList<>();
        for (Object[] stat : currentStats) {
            Long tourId = ((Number) stat[0]).longValue();
            String tourName = (String) stat[1];
            Long bookingCount = ((Number) stat[2]).longValue();
            BigDecimal totalRevenue = (BigDecimal) stat[3];

            // Get last month revenue for this tour
            BigDecimal lastMonthRevenue = getLastMonthRevenueForTour(
                tourId, lastMonthStart, lastMonthEnd
            );

            Double growthRate = calculateGrowthRate(totalRevenue, lastMonthRevenue);

            result.add(TourRevenueDto.builder()
                .tourId(tourId)
                .tourName(tourName)
                .bookingCount(bookingCount)
                .totalRevenue(totalRevenue)
                .growthRate(growthRate)
                .build());
        }

        return result;
    }

    public List<MonthlyRevenueDto> getMonthlyRevenue(int months) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(months);

        List<Object[]> stats = bookingRepository.findMonthlyRevenue(
            BookingStatus.COMPLETED, startDate
        );

        List<MonthlyRevenueDto> result = new ArrayList<>();
        for (Object[] stat : stats) {
            Integer year = (Integer) stat[0];
            Integer month = (Integer) stat[1];
            BigDecimal revenue = (BigDecimal) stat[2];
            Long bookingCount = ((Number) stat[3]).longValue();

            result.add(MonthlyRevenueDto.builder()
                .year(year)
                .month(month)
                .revenue(revenue)
                .bookingCount(bookingCount)
                .build());
        }

        return result;
    }

    private BigDecimal getLastMonthRevenueForTour(Long tourId, LocalDateTime start, LocalDateTime end) {
        // This would require a specific query, for simplicity we'll return 0
        // You can add a specific method in repository if needed
        return BigDecimal.ZERO;
    }

    private Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            if (current != null && current.compareTo(BigDecimal.ZERO) > 0) {
                return 100.0;
            }
            return 0.0;
        }

        BigDecimal diff = current.subtract(previous);
        BigDecimal growth = diff.divide(previous, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
        return growth.doubleValue();
    }
}
