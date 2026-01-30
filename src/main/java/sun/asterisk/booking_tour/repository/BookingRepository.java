package sun.asterisk.booking_tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sun.asterisk.booking_tour.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByCode(String code);

    @Query("select b from Booking b "
            + "join fetch b.tourDeparture td "
            + "join fetch td.tour t "
            + "where b.code = :code")
    Optional<Booking> findByCodeWithDepartureAndTour(@Param("code") String code);

    boolean existsByCode(String code);
}
