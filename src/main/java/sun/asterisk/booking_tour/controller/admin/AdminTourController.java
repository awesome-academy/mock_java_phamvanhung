package sun.asterisk.booking_tour.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.service.AdminTourService;

import java.util.Map;

@Controller
@RequestMapping("/admin/tours")
@RequiredArgsConstructor
public class AdminTourController {

    private final AdminTourService adminTourService;

    @GetMapping
    public String manageTours(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 9;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());

        Page<Map<String, Object>> tours = adminTourService.getTours(pageable);

        model.addAttribute("tours", tours);
        model.addAttribute("hasTours", tours.getTotalElements() > 0);

        return "admin/tours";
    }
}
