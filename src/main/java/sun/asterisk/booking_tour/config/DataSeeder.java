package sun.asterisk.booking_tour.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import sun.asterisk.booking_tour.entity.*;
import sun.asterisk.booking_tour.enums.*;
import sun.asterisk.booking_tour.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            TourRepository tourRepository,
            TourDepartureRepository tourDepartureRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            log.info("Starting data seeding...");

            // Seed roles
            seedRoles(roleRepository);

            // Seed users
            User adminUser = seedUsers(userRepository, roleRepository, passwordEncoder);

            // Seed categories
            List<Category> categories = seedCategories(categoryRepository);

            // Seed tours
            List<Tour> tours = seedTours(tourRepository, categoryRepository, userRepository, adminUser, categories);

            // Seed tour departures
            seedTourDepartures(tourDepartureRepository, tours);

            log.info("Data seeding completed successfully!");
        };
    }

    private void seedRoles(RoleRepository roleRepository) {
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        }

        if (!roleRepository.existsByName("USER")) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
            log.info("Created USER role");
        }
    }

    private User seedUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("System");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("+84123456789");
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setIsVerified(true);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setRole(adminRole);
            
            userRepository.save(admin);
            log.info("Created admin user: admin@example.com / admin123");
            return admin;
        }
        
        return userRepository.findByEmail("admin@example.com").orElseThrow();
    }

    private List<Category> seedCategories(CategoryRepository categoryRepository) {
        List<Category> categories = new ArrayList<>();

        String[][] categoryData = {
            {"Tour biển đảo", "Khám phá vẻ đẹp biển đảo Việt Nam", "tour-bien-dao"},
            {"Tour miền núi", "Chinh phục những đỉnh núi hùng vĩ", "tour-mien-nui"},
            {"Tour du lịch sinh thái", "Trải nghiệm thiên nhiên hoang dã", "tour-sinh-thai"},
            {"Tour tâm linh", "Hành trình tìm về cội nguồn", "tour-tam-linh"},
            {"Tour văn hóa lịch sử", "Khám phá di sản văn hóa", "tour-van-hoa"}
        };

        for (String[] data : categoryData) {
            if (!categoryRepository.existsBySlug(data[2])) {
                Category category = new Category();
                category.setName(data[0]);
                category.setDescription(data[1]);
                category.setSlug(data[2]);
                category.setStatus(CategoryStatus.ACTIVE);
                
                Category saved = categoryRepository.save(category);
                categories.add(saved);
                log.info("Created category: {}", data[0]);
            } else {
                categories.add(categoryRepository.findBySlug(data[2]).orElseThrow());
            }
        }

        return categories;
    }

    private List<Tour> seedTours(
            TourRepository tourRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            User creator,
            List<Category> categories
    ) {
        List<Tour> tours = new ArrayList<>();

        Object[][] tourData = {
            {"Tour Phú Quốc 3N2Đ", "Khám phá thiên đường biển đảo", "tour-phu-quoc-3n2d", 
                "Hà Nội", "Phú Quốc", 3, 2, 3500000, 2500000, 15.00, categories.get(0)},
            {"Tour Sapa 2N1Đ", "Chinh phục đỉnh Fansipan", "tour-sapa-2n1d",
                "Hà Nội", "Sapa", 2, 1, 2500000, 1800000, 10.00, categories.get(1)},
            {"Tour Nha Trang 4N3Đ", "Biển xanh cát trắng", "tour-nha-trang-4n3d",
                "TP.HCM", "Nha Trang", 4, 3, 4500000, 3200000, 20.00, categories.get(0)},
            {"Tour Đà Lạt 3N2Đ", "Thành phố ngàn hoa", "tour-da-lat-3n2d",
                "TP.HCM", "Đà Lạt", 3, 2, 2800000, 2000000, 0.00, categories.get(2)},
            {"Tour Hạ Long 2N1Đ", "Kỳ quan thiên nhiên thế giới", "tour-ha-long-2n1d",
                "Hà Nội", "Hạ Long", 2, 1, 3200000, 2300000, 12.00, categories.get(0)},
            {"Tour Huế 3N2Đ", "Cố đô ngàn năm", "tour-hue-3n2d",
                "Đà Nẵng", "Huế", 3, 2, 3000000, 2100000, 8.00, categories.get(4)},
            {"Tour Côn Đảo 4N3Đ", "Thiên đường biển xanh", "tour-con-dao-4n3d",
                "TP.HCM", "Côn Đảo", 4, 3, 6500000, 4800000, 25.00, categories.get(0)},
            {"Tour Mai Châu 2N1Đ", "Bản làng yên bình", "tour-mai-chau-2n1d",
                "Hà Nội", "Mai Châu", 2, 1, 1800000, 1200000, 5.00, categories.get(2)},
            {"Tour Mù Cang Chải 3N2Đ", "Ruộng bậc thang tuyệt đẹp", "tour-mu-cang-chai-3n2d",
                "Hà Nội", "Mù Cang Chải", 3, 2, 2900000, 2000000, 0.00, categories.get(1)},
            {"Tour Phan Thiết 3N2Đ", "Đồi cát bay và biển xanh", "tour-phan-thiet-3n2d",
                "TP.HCM", "Phan Thiết", 3, 2, 2600000, 1900000, 15.00, categories.get(0)}
        };

        for (Object[] data : tourData) {
            String slug = (String) data[2];
            if (!tourRepository.existsBySlug(slug)) {
                Tour tour = new Tour();
                tour.setName((String) data[0]);
                tour.setTitle((String) data[1]);
                tour.setSlug(slug);
                tour.setDescription("Tour " + data[0] + " - " + data[1] + ". Chương trình tour bao gồm: di chuyển, ăn uống, khách sạn, hướng dẫn viên chuyên nghiệp.");
                tour.setThumbnailUrl("https://example.com/tours/" + slug + ".jpg");
                tour.setDepartureLocation((String) data[3]);
                tour.setMainDestination((String) data[4]);
                tour.setDurationDays((Integer) data[5]);
                tour.setDurationNights((Integer) data[6]);
                tour.setPriceAdult(BigDecimal.valueOf((Integer) data[7]));
                tour.setPriceChild(BigDecimal.valueOf((Integer) data[8]));
                tour.setDiscountRate(BigDecimal.valueOf((Double) data[9]));
                tour.setItinerary("Ngày 1: Khởi hành...\nNgày 2: Tham quan...\nNgày 3: Về...");
                tour.setCreator(creator);
                tour.setCategory((Category) data[10]);

                Tour saved = tourRepository.save(tour);
                tours.add(saved);
                log.info("Created tour: {}", data[0]);
            } else {
                tours.add(tourRepository.findBySlug(slug).orElseThrow());
            }
        }

        return tours;
    }

    private void seedTourDepartures(TourDepartureRepository tourDepartureRepository, List<Tour> tours) {
        for (Tour tour : tours) {
            LocalDate[] departureDates = {
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(14),
                LocalDate.now().plusDays(21)
            };

            for (LocalDate departureDate : departureDates) {
                TourDeparture departure = new TourDeparture();
                departure.setTour(tour);
                departure.setDepartureDate(departureDate);
                departure.setReturnDate(departureDate.plusDays(tour.getDurationDays() - 1));
                departure.setTotalSlots(30);
                departure.setAvailableSlots(25);
                departure.setStatus(TourDepartureStatus.OPEN);

                tourDepartureRepository.save(departure);
            }
            
            log.info("Created {} departures for tour: {}", departureDates.length, tour.getName());
        }
    }
}
