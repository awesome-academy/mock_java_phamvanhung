package sun.asterisk.booking_tour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Long countByTourId(Long tourId);
    
    Long countByReviewId(Long reviewId);
    
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.tour.id = :tourId")
    Optional<Like> findByUserIdAndTourId(@Param("userId") Long userId, @Param("tourId") Long tourId);
    
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.review.id = :reviewId")
    Optional<Like> findByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
    
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.tour.id = :tourId")
    boolean existsByUserIdAndTourId(@Param("userId") Long userId, @Param("tourId") Long tourId);
    
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.review.id = :reviewId")
    boolean existsByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
    
    @Query("SELECT l.review.id as reviewId, COUNT(l) as likeCount " +
           "FROM Like l " +
           "WHERE l.review.id IN :reviewIds " +
           "GROUP BY l.review.id")
    List<ReviewLikeCount> countLikesByReviewIds(@Param("reviewIds") List<Long> reviewIds);
    
    @Query("SELECT l.review.id " +
           "FROM Like l " +
           "WHERE l.user.id = :userId AND l.review.id IN :reviewIds")
    List<Long> findLikedReviewIdsByUser(@Param("userId") Long userId, @Param("reviewIds") List<Long> reviewIds);
    
    interface ReviewLikeCount {
        Long getReviewId();
        Long getLikeCount();
    }
}
