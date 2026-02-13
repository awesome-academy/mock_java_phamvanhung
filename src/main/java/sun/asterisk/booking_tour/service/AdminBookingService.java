package sun.asterisk.booking_tour.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sun.asterisk.booking_tour.dto.admin.BookingListDto;
import sun.asterisk.booking_tour.dto.admin.BookingStatsDto;
import sun.asterisk.booking_tour.entity.Booking;
import sun.asterisk.booking_tour.enums.BookingStatus;
import sun.asterisk.booking_tour.repository.BookingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBookingService {

    private final BookingRepository bookingRepository;

    public BookingStatsDto getBookingStats() {
        Long total = bookingRepository.count();
        Long pending = bookingRepository.countByStatus(BookingStatus.PENDING);
        Long confirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED) 
                       + bookingRepository.countByStatus(BookingStatus.PAID)
                       + bookingRepository.countByStatus(BookingStatus.COMPLETED);
        Long cancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        return BookingStatsDto.builder()
            .totalBookings(total)
            .pendingBookings(pending)
            .confirmedBookings(confirmed)
            .cancelledBookings(cancelled)
            .build();
    }

    public List<BookingListDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAllWithTourInfo();
        return bookings.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Page<BookingListDto> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public Page<BookingListDto> getAllNonCancelledBookings(Pageable pageable) {
        return bookingRepository.findByStatusNot(BookingStatus.CANCELLED, pageable)
                .map(this::convertToDto);
    }

    public List<BookingListDto> getBookingsByStatus(BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatusWithTourInfo(status);
        return bookings.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Page<BookingListDto> getBookingsByStatus(BookingStatus status, Pageable pageable) {
        return bookingRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingListDto convertToDto(Booking booking) {
        int totalPeople = booking.getNumAdults() + booking.getNumChildren();
        String tourName = booking.getTourDeparture().getTour().getName();
        
        return BookingListDto.builder()
            .id(booking.getId())
            .code(booking.getCode())
            .customerName(booking.getContactName())
            .customerEmail(booking.getContactEmail())
            .tourName(tourName)
            .bookingDate(booking.getCreatedAt())
            .totalPeople(totalPeople)
            .finalTotal(booking.getFinalTotal())
            .status(booking.getStatus())
            .build();
    }
}
