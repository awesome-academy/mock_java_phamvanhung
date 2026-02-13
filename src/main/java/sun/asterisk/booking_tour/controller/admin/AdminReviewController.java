package sun.asterisk.booking_tour.controller.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.enums.ReviewStatus;
import sun.asterisk.booking_tour.service.AdminReviewService;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @GetMapping
    public String manageReviews(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());

        ReviewStatus filterStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                filterStatus = ReviewStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                filterStatus = null;
            }
        }

        Page<Map<String, Object>> reviews = adminReviewService.getReviews(filterStatus, pageable);

        model.addAttribute("reviews", reviews);
        model.addAttribute("hasReviews", reviews.getTotalElements() > 0);

        model.addAttribute("totalReviews", adminReviewService.getTotalReviews());
        model.addAttribute("averageRating", adminReviewService.getAverageRatingAll());
        model.addAttribute("pendingReviews", adminReviewService.getPendingReviews());

        model.addAttribute("selectedStatus", status != null ? status : "");
        model.addAttribute("statuses", adminReviewService.reviewStatusNames());

        return "admin/reviews";
    }

    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable("id") Long id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        adminReviewService.updateStatus(id, ReviewStatus.APPROVED);
        String safeStatus = status != null ? status : "";
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        return "redirect:/admin/reviews?status=" + safeStatus + "&page=" + safePage + "&size=" + safeSize;
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable("id") Long id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        adminReviewService.updateStatus(id, ReviewStatus.REJECTED);
        String safeStatus = status != null ? status : "";
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        return "redirect:/admin/reviews?status=" + safeStatus + "&page=" + safePage + "&size=" + safeSize;
    }
}
