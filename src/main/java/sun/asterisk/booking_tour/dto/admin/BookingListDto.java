package sun.asterisk.booking_tour.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.asterisk.booking_tour.enums.BookingStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookingListDto {
    private Long id;
    private String code;
    private String customerName;
    private String customerEmail;
    private String tourName;
    private LocalDateTime bookingDate;
    private Integer totalPeople;
    private BigDecimal finalTotal;
    private BookingStatus status;
}
