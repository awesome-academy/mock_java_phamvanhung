package sun.asterisk.booking_tour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookingTourApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingTourApplication.class, args);
	}

}
