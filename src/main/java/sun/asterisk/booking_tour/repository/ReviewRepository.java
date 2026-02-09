package sun.asterisk.booking_tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Review;
import sun.asterisk.booking_tour.enums.ReviewStatus;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Override
    @EntityGraph(attributePaths = { "user", "tour", "booking" })
    Page<Review> findAll(Pageable pageable);

    Long countByStatus(ReviewStatus status);

    @EntityGraph(attributePaths = { "user", "tour", "booking" })
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r")
    Double findAverageRatingAll();
    
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
    
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.tour " +
           "LEFT JOIN FETCH r.booking " +
           "WHERE r.tour.id = :tourId AND r.status = :status " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findByTourIdAndStatus(@Param("tourId") Long tourId, 
                                        @Param("status") ReviewStatus status,
                                        Pageable pageable);
    
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.tour " +
           "LEFT JOIN FETCH r.booking " +
           "WHERE r.user.id = :userId " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.tour " +
           "LEFT JOIN FETCH r.booking " +
           "WHERE r.id = :reviewId AND r.user.id = :userId")
    Optional<Review> findByIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
