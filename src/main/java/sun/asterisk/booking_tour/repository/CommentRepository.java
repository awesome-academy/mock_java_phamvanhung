package sun.asterisk.booking_tour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
        
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.review.id = :reviewId AND c.parent IS NULL " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findByReviewIdWithUser(@Param("reviewId") Long reviewId);
    
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.parent.id = :parentId " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
    
    Long countByReviewId(Long reviewId);
    
    Long countByTourId(Long tourId);
    
    @Query("SELECT c.review.id as reviewId, COUNT(c) as commentCount " +
           "FROM Comment c " +
           "WHERE c.review.id IN :reviewIds " +
           "GROUP BY c.review.id")
    List<ReviewCommentCount> countCommentsByReviewIds(@Param("reviewIds") List<Long> reviewIds);
    
    interface ReviewCommentCount {
        Long getReviewId();
        Long getCommentCount();
    }
    
    @Query("SELECT c FROM Comment c " +
           "WHERE c.id = :commentId AND c.user.id = :userId")
    Optional<Comment> findByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
