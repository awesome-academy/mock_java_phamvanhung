package sun.asterisk.booking_tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.enums.ReviewStatus;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) " +
           "FROM Review r " +
           "WHERE r.tour.id = :tourId " +
           "AND r.status = :status")
    Double findAverageRatingByTourId(
        @Param("tourId") Long tourId,
        @Param("status") ReviewStatus status
    );
    
    @Query("SELECT COUNT(r) " +
           "FROM Review r " +
           "WHERE r.tour.id = :tourId " +
           "AND r.status = :status")
    Long countByTourIdAndStatus(
        @Param("tourId") Long tourId,
        @Param("status") ReviewStatus status
    );
}
