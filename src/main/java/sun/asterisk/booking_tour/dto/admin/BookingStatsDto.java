package sun.asterisk.booking_tour.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookingStatsDto {
    private Long totalBookings;
    private Long pendingBookings;
    private Long confirmedBookings;
    private Long cancelledBookings;
}
