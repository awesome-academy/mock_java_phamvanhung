package sun.asterisk.booking_tour.controller.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import sun.asterisk.booking_tour.dto.admin.BookingListDto;
import sun.asterisk.booking_tour.dto.admin.BookingStatsDto;
import sun.asterisk.booking_tour.enums.BookingStatus;
import sun.asterisk.booking_tour.service.AdminBookingService;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    private static List<String> bookingStatusNames() {
        return Arrays.stream(BookingStatus.values())
                .map(Enum::name)
                .toList();
    }

    private static Map<String, Object> toBookingViewModel(BookingListDto booking) {
        Map<String, Object> viewModel = new HashMap<>();
        viewModel.put("id", booking.getId());
        viewModel.put("code", booking.getCode());
        viewModel.put("customerName", booking.getCustomerName());
        viewModel.put("customerEmail", booking.getCustomerEmail());
        viewModel.put("tourName", booking.getTourName());
        viewModel.put("bookingDate", booking.getBookingDate());
        viewModel.put("totalPeople", booking.getTotalPeople());
        viewModel.put("finalTotal", booking.getFinalTotal());
        viewModel.put("status", booking.getStatus() != null ? booking.getStatus().name() : null);
        return viewModel;
    }

    @GetMapping
    public String manageBookings(
            @RequestParam(required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());
        BookingStatsDto stats = adminBookingService.getBookingStats();
        
        Page<BookingListDto> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                bookings = adminBookingService.getBookingsByStatus(bookingStatus, pageable);
            } catch (IllegalArgumentException e) {
                bookings = adminBookingService.getAllNonCancelledBookings(pageable);
            }
        } else {
            bookings = adminBookingService.getAllNonCancelledBookings(pageable);
        }

        // Add individual stats fields
        model.addAttribute("totalBookings", stats.getTotalBookings());
        model.addAttribute("pendingBookings", stats.getPendingBookings());
        model.addAttribute("confirmedBookings", stats.getConfirmedBookings());
        model.addAttribute("cancelledBookings", stats.getCancelledBookings());
        
        Page<Map<String, Object>> bookingList = bookings.map(AdminBookingController::toBookingViewModel);
        model.addAttribute("bookings", bookingList);
        model.addAttribute("hasBookings", bookingList.getTotalElements() > 0);
        model.addAttribute("selectedStatus", status != null ? status : "");
        model.addAttribute("statuses", bookingStatusNames());

        return "admin/bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        adminBookingService.cancelBooking(id);
        String safeStatus = status != null ? status : "";
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        return "redirect:/admin/bookings?status=" + safeStatus + "&page=" + safePage + "&size=" + safeSize;
    }
}
