package sun.asterisk.booking_tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Tour;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    
    Optional<Tour> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
}
