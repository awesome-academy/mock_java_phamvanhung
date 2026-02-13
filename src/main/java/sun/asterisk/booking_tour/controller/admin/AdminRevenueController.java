package sun.asterisk.booking_tour.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.dto.revenue.MonthlyRevenueDto;
import sun.asterisk.booking_tour.dto.revenue.RevenueStatsDto;
import sun.asterisk.booking_tour.dto.revenue.TourRevenueDto;
import sun.asterisk.booking_tour.service.RevenueService;

@Controller
@RequestMapping("/admin/revenue")
@RequiredArgsConstructor
public class AdminRevenueController {

    private final RevenueService revenueService;

    @GetMapping
    public String manageRevenue(Model model) {
        RevenueStatsDto stats = revenueService.getRevenueStats();
        List<TourRevenueDto> tourRevenue = revenueService.getTourRevenue();
        List<MonthlyRevenueDto> monthlyRevenue = revenueService.getMonthlyRevenue(12);

        model.addAttribute("revenueToday", stats.revenueToday());
        model.addAttribute("growthRateToday", stats.growthRateToday());
        model.addAttribute("revenueThisMonth", stats.revenueThisMonth());
        model.addAttribute("growthRateThisMonth", stats.growthRateThisMonth());
        model.addAttribute("revenueThisYear", stats.revenueThisYear());
        model.addAttribute("growthRateThisYear", stats.growthRateThisYear());
        
        model.addAttribute("tourRevenue", tourRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        return "admin/revenue";
    }
}
