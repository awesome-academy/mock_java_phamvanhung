package sun.asterisk.booking_tour.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sun.asterisk.booking_tour.dto.tour.TourSearchRequest;
import sun.asterisk.booking_tour.entity.Category;
import sun.asterisk.booking_tour.entity.Tour;
import sun.asterisk.booking_tour.entity.TourDeparture;
import sun.asterisk.booking_tour.enums.CategoryStatus;
import sun.asterisk.booking_tour.enums.TourDepartureStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecification {

    public static Specification<Tour> withSearchCriteria(TourSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), keyword);
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), keyword);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), keyword);
                Predicate destPredicate = cb.like(cb.lower(root.get("mainDestination")), keyword);

                predicates.add(cb.or(namePredicate, titlePredicate, descPredicate, destPredicate));
            }

            // Departure location
            if (request.getDepartureLocation() != null && !request.getDepartureLocation().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("departureLocation")),
                        "%" + request.getDepartureLocation().toLowerCase() + "%"
                ));
            }

            // Destination
            if (request.getDestination() != null && !request.getDestination().trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("mainDestination")),
                        "%" + request.getDestination().toLowerCase() + "%"
                ));
            }

            // Category filter - only ACTIVE categories
            Join<Tour, Category> categoryJoin = root.join("category", JoinType.LEFT);
            predicates.add(cb.equal(categoryJoin.get("status"), CategoryStatus.ACTIVE));

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(categoryJoin.get("id"), request.getCategoryId()));
            }

            // Price range
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceAdult"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("priceAdult"), request.getMaxPrice()));
            }

            // Duration range
            if (request.getMinDuration() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationDays"), request.getMinDuration()));
            }
            if (request.getMaxDuration() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("durationDays"), request.getMaxDuration()));
            }

            // Discount filter
            if (request.getHasDiscount() != null) {
                if (request.getHasDiscount()) {
                    predicates.add(cb.greaterThan(root.get("discountRate"), BigDecimal.ZERO));
                } else {
                    predicates.add(cb.or(
                            cb.isNull(root.get("discountRate")),
                            cb.equal(root.get("discountRate"), BigDecimal.ZERO)
                    ));
                }
            }

            // Available slots filter
            if (request.getHasAvailableSlots() != null && request.getHasAvailableSlots()) {
                Join<Tour, TourDeparture> departureJoin = root.join("tourDepartures", JoinType.LEFT);
                predicates.add(cb.greaterThan(departureJoin.get("availableSlots"), 0));
                predicates.add(cb.equal(departureJoin.get("status"), TourDepartureStatus.OPEN));
                predicates.add(cb.greaterThanOrEqualTo(departureJoin.get("departureDate"), LocalDate.now()));
                query.distinct(true);
            }

            // Departure date range
            if (request.getDepartureFrom() != null || request.getDepartureTo() != null) {
                Join<Tour, TourDeparture> departureJoin = root.join("tourDepartures", JoinType.LEFT);

                if (request.getDepartureFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(
                            departureJoin.get("departureDate"),
                            request.getDepartureFrom()
                    ));
                }
                if (request.getDepartureTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(
                            departureJoin.get("departureDate"),
                            request.getDepartureTo()
                    ));
                }

                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
