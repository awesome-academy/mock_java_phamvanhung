package sun.asterisk.booking_tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Category;
import sun.asterisk.booking_tour.enums.CategoryStatus;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    Optional<Category> findByIdAndStatus(Long id, CategoryStatus status);
}
