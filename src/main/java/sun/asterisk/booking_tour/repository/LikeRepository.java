package sun.asterisk.booking_tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Long countByTourId(Long tourId);
}
