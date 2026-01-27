package sun.asterisk.booking_tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.TourDeparture;
import sun.asterisk.booking_tour.enums.TourDepartureStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Long> {
    
    List<TourDeparture> findByTourId(Long tourId);
    
    @Query("SELECT CASE WHEN COUNT(td) > 0 THEN true ELSE false END " +
           "FROM TourDeparture td " +
           "WHERE td.tour.id = :tourId " +
           "AND td.availableSlots > 0 " +
           "AND td.status = :status " +
           "AND td.departureDate >= :currentDate")
    boolean existsAvailableDepartureByTourId(
        @Param("tourId") Long tourId,
        @Param("status") TourDepartureStatus status,
        @Param("currentDate") LocalDate currentDate
    );
}
