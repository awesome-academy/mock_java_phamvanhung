package sun.asterisk.booking_tour.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.asterisk.booking_tour.dto.common.PageResponse;
import sun.asterisk.booking_tour.dto.tour.TourDetailResponse;
import sun.asterisk.booking_tour.dto.tour.TourSearchRequest;
import sun.asterisk.booking_tour.dto.tour.TourSearchResponse;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.TourDeparture;
import sun.asterisk.booking_tour.entity.TourImage;
import sun.asterisk.booking_tour.enums.ReviewStatus;
import sun.asterisk.booking_tour.enums.TourDepartureStatus;
import sun.asterisk.booking_tour.exception.ResourceNotFoundException;
import sun.asterisk.booking_tour.repository.LikeRepository;
import sun.asterisk.booking_tour.repository.ReviewRepository;
import sun.asterisk.booking_tour.repository.TourDepartureRepository;
import sun.asterisk.booking_tour.repository.TourRepository;
import sun.asterisk.booking_tour.specification.TourSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TourService {

    private final TourRepository tourRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final TourDepartureRepository tourDepartureRepository;

    public PageResponse<TourSearchResponse> searchTours(TourSearchRequest request) {
        Specification<Tour> spec = TourSpecification.withSearchCriteria(request);
        Pageable pageable = createPageable(request);

        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);

        List<TourSearchResponse> content = tourPage.getContent().stream()
                .map(this::mapToSearchResponse)
                .collect(Collectors.toList());

        return PageResponse.<TourSearchResponse>builder()
                .content(content)
                .pageNumber(tourPage.getNumber())
                .pageSize(tourPage.getSize())
                .totalElements(tourPage.getTotalElements())
                .totalPages(tourPage.getTotalPages())
                .isFirst(tourPage.isFirst())
                .isLast(tourPage.isLast())
                .hasNext(tourPage.hasNext())
                .hasPrevious(tourPage.hasPrevious())
                .build();
    }

    private Pageable createPageable(TourSearchRequest request) {
        Sort sort = createSort(request.getSortBy(), request.getSortOrder());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;

        return switch (sortBy != null ? sortBy.toLowerCase() : "newest") {
            case "price" -> Sort.by(direction, "priceAdult");
            case "discount" -> Sort.by(direction, "discountRate");
            case "newest" -> Sort.by(direction, "createdAt");
            default -> Sort.by(direction, "createdAt");
        };
    }

    private TourSearchResponse mapToSearchResponse(Tour tour) {
        Double averageRating = reviewRepository.findAverageRatingByTourId(
            tour.getId(), 
            ReviewStatus.APPROVED
        );
        
        Long totalReviews = reviewRepository.countByTourIdAndStatus(
            tour.getId(), 
            ReviewStatus.APPROVED
        );
        
        Long totalLikes = likeRepository.countByTourId(tour.getId());
        
        boolean hasAvailableSlots = tourDepartureRepository.existsAvailableDepartureByTourId(
            tour.getId(), 
            TourDepartureStatus.OPEN, 
            LocalDate.now()
        );

        LocalDate nextDepartureDate = getNextDepartureDate(tour.getId());

        BigDecimal finalPrice = calculateFinalPrice(tour.getPriceAdult(), tour.getDiscountRate());

        return TourSearchResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .title(tour.getTitle())
                .slug(tour.getSlug())
                .thumbnailUrl(tour.getThumbnailUrl())
                .departureLocation(tour.getDepartureLocation())
                .mainDestination(tour.getMainDestination())
                .durationDays(tour.getDurationDays())
                .durationNights(tour.getDurationNights())
                .priceAdult(tour.getPriceAdult())
                .priceChild(tour.getPriceChild())
                .discountRate(tour.getDiscountRate())
                .finalPrice(finalPrice)
                .categoryId(tour.getCategory() != null ? tour.getCategory().getId() : null)
                .categoryName(tour.getCategory() != null ? tour.getCategory().getName() : null)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .totalLikes(totalLikes)
                .hasAvailableSlots(hasAvailableSlots)
                .nextDepartureDate(nextDepartureDate)
                .build();
    }

    private LocalDate getNextDepartureDate(Long tourId) {
        List<TourDeparture> departures = tourDepartureRepository.findByTourId(tourId);
        
        return departures.stream()
                .filter(d -> d.getDepartureDate().isAfter(LocalDate.now().minusDays(1)))
                .filter(d -> d.getStatus() == TourDepartureStatus.OPEN)
                .filter(d -> d.getAvailableSlots() > 0)
                .map(TourDeparture::getDepartureDate)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    private BigDecimal calculateFinalPrice(BigDecimal priceAdult, BigDecimal discountRate) {
        if (priceAdult == null) {
            return BigDecimal.ZERO;
        }
        
        if (discountRate == null || discountRate.compareTo(BigDecimal.ZERO) <= 0) {
            return priceAdult;
        }

        BigDecimal discount = priceAdult
                .multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        return priceAdult.subtract(discount);
    }

    public TourDetailResponse getTourBySlug(String slug) {
        Tour tour = tourRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with slug: " + slug));

        return mapToDetailResponse(tour);
    }

    private TourDetailResponse mapToDetailResponse(Tour tour) {
        Double averageRating = reviewRepository.findAverageRatingByTourId(
            tour.getId(), 
            ReviewStatus.APPROVED
        );
        
        Long totalReviews = reviewRepository.countByTourIdAndStatus(
            tour.getId(), 
            ReviewStatus.APPROVED
        );
        
        Long totalLikes = likeRepository.countByTourId(tour.getId());

        List<TourDeparture> departures = tourDepartureRepository.findByTourId(tour.getId());
        List<TourDetailResponse.DepartureInfo> upcomingDepartures = departures.stream()
                .filter(d -> d.getDepartureDate().isAfter(LocalDate.now().minusDays(1)))
                .filter(d -> d.getStatus() == TourDepartureStatus.OPEN)
                .sorted((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                .limit(5)
                .map(this::mapToDepartureInfo)
                .collect(Collectors.toList());

        List<TourDetailResponse.TourImageInfo> images = tour.getTourImages() != null 
                ? tour.getTourImages().stream()
                    .sorted((i1, i2) -> {
                        if (i1.getIsPrimary() && !i2.getIsPrimary()) return -1;
                        if (!i1.getIsPrimary() && i2.getIsPrimary()) return 1;
                        return Integer.compare(
                            i1.getDisplayOrder() != null ? i1.getDisplayOrder() : 999,
                            i2.getDisplayOrder() != null ? i2.getDisplayOrder() : 999
                        );
                    })
                    .map(this::mapToImageInfo)
                    .collect(Collectors.toList())
                : List.of();

        BigDecimal finalPrice = calculateFinalPrice(tour.getPriceAdult(), tour.getDiscountRate());

        return TourDetailResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .title(tour.getTitle())
                .slug(tour.getSlug())
                .description(tour.getDescription())
                .itinerary(tour.getItinerary())
                .thumbnailUrl(tour.getThumbnailUrl())
                .departureLocation(tour.getDepartureLocation())
                .mainDestination(tour.getMainDestination())
                .durationDays(tour.getDurationDays())
                .durationNights(tour.getDurationNights())
                .priceAdult(tour.getPriceAdult())
                .priceChild(tour.getPriceChild())
                .discountRate(tour.getDiscountRate())
                .finalPrice(finalPrice)
                .category(tour.getCategory() != null ? mapToCategoryInfo(tour) : null)
                .creator(tour.getCreator() != null ? mapToCreatorInfo(tour) : null)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .totalLikes(totalLikes)
                .images(images)
                .upcomingDepartures(upcomingDepartures)
                .build();
    }

    private TourDetailResponse.CategoryInfo mapToCategoryInfo(Tour tour) {
        return TourDetailResponse.CategoryInfo.builder()
                .id(tour.getCategory().getId())
                .name(tour.getCategory().getName())
                .slug(tour.getCategory().getSlug())
                .build();
    }

    private TourDetailResponse.CreatorInfo mapToCreatorInfo(Tour tour) {
        return TourDetailResponse.CreatorInfo.builder()
                .id(tour.getCreator().getId())
                .name(tour.getCreator().getFirstName() + " " + tour.getCreator().getLastName())
                .email(tour.getCreator().getEmail())
                .build();
    }

    private TourDetailResponse.TourImageInfo mapToImageInfo(TourImage image) {
        return TourDetailResponse.TourImageInfo.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .caption(image.getCaption())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }

    private TourDetailResponse.DepartureInfo mapToDepartureInfo(TourDeparture departure) {
        return TourDetailResponse.DepartureInfo.builder()
                .id(departure.getId())
                .departureDate(departure.getDepartureDate())
                .returnDate(departure.getReturnDate())
                .totalSlots(departure.getTotalSlots())
                .availableSlots(departure.getAvailableSlots())
                .status(departure.getStatus().name())
                .build();
    }
}
