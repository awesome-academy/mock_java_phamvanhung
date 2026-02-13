package sun.asterisk.booking_tour.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.repository.TourRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTourService {

    private final TourRepository tourRepository;

    public Page<Map<String, Object>> getTours(Pageable pageable) {
        return tourRepository.findAll(pageable).map(AdminTourService::toTourViewModel);
    }

    private static Map<String, Object> toTourViewModel(Tour tour) {
        Map<String, Object> viewModel = new HashMap<>();
        viewModel.put("id", tour.getId());
        viewModel.put("name", tour.getName());
        viewModel.put("title", tour.getTitle());
        viewModel.put("slug", tour.getSlug());
        viewModel.put("thumbnailUrl", tour.getThumbnailUrl());
        viewModel.put("departureLocation", tour.getDepartureLocation());
        viewModel.put("mainDestination", tour.getMainDestination());
        viewModel.put("durationDays", tour.getDurationDays());
        viewModel.put("durationNights", tour.getDurationNights());
        viewModel.put("priceAdult", tour.getPriceAdult());
        viewModel.put("priceChild", tour.getPriceChild());
        viewModel.put("discountRate", tour.getDiscountRate());
        viewModel.put("createdAt", tour.getCreatedAt());
        viewModel.put("updatedAt", tour.getUpdatedAt());
        viewModel.put("creatorName", tour.getCreator() != null ? tour.getCreator().getEmail() : null);
        viewModel.put("categoryName", tour.getCategory() != null ? tour.getCategory().getName() : null);
        return viewModel;
    }
}
