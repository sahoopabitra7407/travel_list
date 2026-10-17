package com.example.travel.config;

import com.example.travel.model.Destination;
import com.example.travel.model.TravelGoal;
import com.example.travel.model.TravelMemory;
import com.example.travel.model.TripExpense;
import com.example.travel.model.User;
import com.example.travel.repository.DestinationRepository;
import com.example.travel.repository.TravelGoalRepository;
import com.example.travel.repository.TravelMemoryRepository;
import com.example.travel.repository.TripExpenseRepository;
import com.example.travel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            DestinationRepository destinationRepository,
            TravelGoalRepository goalRepository,
            TravelMemoryRepository memoryRepository,
            TripExpenseRepository expenseRepository,
            UserRepository userRepository) {
        
        return args -> {
            // 0. Initialize Default User
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .name("Alex Morgan")
                        .email("alex.morgan@wanderlist.io")
                        .password("Explorer#2026")
                        .avatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80")
                        .role("Pro Explorer")
                        .build());
            }

            // 1. Initialize Destinations
            if (destinationRepository.count() == 0) {
                Destination kyoto = Destination.builder()
                        .title("Cherry Blossom Dreams")
                        .country("Japan")
                        .city("Kyoto")
                        .description("Experience spring in Kyoto wandering through Fushimi Inari shrine, historic bamboo groves of Arashiyama, and traditional tea houses surrounded by pink sakura blossoms.")
                        .category("Culture")
                        .mood("Photography")
                        .targetDate("Spring 2027")
                        .status("PLANNED")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80")
                        .budget(3200.0)
                        .bestSeason("Mar–Apr")
                        .duration("6 days")
                        .latitude(35.0116)
                        .longitude(135.7681)
                        .build();

                Destination santorini = Destination.builder()
                        .title("Caldera Sunset & Sailing")
                        .country("Greece")
                        .city("Santorini")
                        .description("Stay in whitewashed cliffside villas in Oia, sail across the Aegean volcanic caldera, and taste fresh Mediterranean seafood paired with crisp Assyrtiko wine.")
                        .category("Beach")
                        .mood("Relaxation")
                        .targetDate("September 2026")
                        .status("WISHLIST")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80")
                        .budget(2800.0)
                        .bestSeason("Jun–Sep")
                        .duration("5 days")
                        .latitude(36.3932)
                        .longitude(25.4615)
                        .build();

                Destination machuPicchu = Destination.builder()
                        .title("Inca Trail Mountain Trek")
                        .country("Peru")
                        .city("Cusco")
                        .description("Challenge yourself on a high altitude trek through the Andes mountains ending at the majestic Sun Gate overlooking the ancient lost city of the Incas.")
                        .category("Mountain")
                        .mood("Adventure")
                        .targetDate("May 2025")
                        .status("WISHLIST")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1526392060635-9d6019884377?auto=format&fit=crop&w=800&q=80")
                        .budget(2100.0)
                        .bestSeason("May–Oct")
                        .duration("7 days")
                        .latitude(-13.1631)
                        .longitude(-72.5450)
                        .build();

                Destination tromso = Destination.builder()
                        .title("Aurora Borealis Hunting")
                        .country("Norway")
                        .city("Tromsø")
                        .description("Watch dancing emerald Northern Lights over arctic snowscapes, take a reindeer sleigh ride, and experience winter wonderlands high above the Arctic Circle.")
                        .category("Nature")
                        .mood("Photography")
                        .targetDate("December 2026")
                        .status("WISHLIST")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1517411032315-54ef2cb783bb?auto=format&fit=crop&w=800&q=80")
                        .budget(3500.0)
                        .bestSeason("Nov–Mar")
                        .duration("4 days")
                        .latitude(69.6492)
                        .longitude(18.9553)
                        .build();

                Destination cairo = Destination.builder()
                        .title("Great Pyramids of Giza")
                        .country("Egypt")
                        .city("Cairo")
                        .description("Stand before the sole remaining wonder of the ancient world, explore the Valley Temple, and ride camels across the desert sands.")
                        .category("Culture")
                        .mood("Adventure")
                        .targetDate("October 2023")
                        .status("VISITED")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1539650116574-8efeb43e2750?auto=format&fit=crop&w=800&q=80")
                        .budget(1500.0)
                        .bestSeason("Oct–Apr")
                        .duration("4 days")
                        .latitude(29.9792)
                        .longitude(31.1342)
                        .build();

                Destination paris = Destination.builder()
                        .title("Romantic Parisian Getaway")
                        .country("France")
                        .city("Paris")
                        .description("Wander along the Seine, admire classical art inside the Louvre, enjoy croissants in cozy sidewalk cafes, and watch Eiffel Tower sparklings at night.")
                        .category("Romantic")
                        .mood("Romantic")
                        .targetDate("April 2024")
                        .status("VISITED")
                        .rating(4)
                        .imageUrl("https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80")
                        .budget(2600.0)
                        .bestSeason("Apr–Jun")
                        .duration("5 days")
                        .latitude(48.8566)
                        .longitude(2.3522)
                        .build();

                Destination banff = Destination.builder()
                        .title("Glacial Lakes of Banff")
                        .country("Canada")
                        .city("Banff")
                        .description("Canoe across vibrant turquoise waters of Lake Louise and Moraine Lake surrounded by towering snow-capped peaks and pristine wilderness.")
                        .category("Mountain")
                        .mood("Nature")
                        .targetDate("August 2022")
                        .status("VISITED")
                        .rating(5)
                        .imageUrl("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80")
                        .budget(1950.0)
                        .bestSeason("Jun–Sep")
                        .duration("5 days")
                        .latitude(51.1784)
                        .longitude(-115.5708)
                        .build();

                List<Destination> saved = destinationRepository.saveAll(Arrays.asList(kyoto, santorini, machuPicchu, tromso, cairo, paris, banff));
                Long kyotoId = saved.get(0).getId();
                Long cairoId = saved.get(4).getId();
                Long parisId = saved.get(5).getId();
                Long banffId = saved.get(6).getId();

                // 2. Initialize Trip Expenses (for Visited or Planned items)
                // Kyoto expenses (Planned)
                expenseRepository.saveAll(Arrays.asList(
                    TripExpense.builder().destinationId(kyotoId).category("Flights").estimatedAmount(1200.0).actualAmount(0.0).build(),
                    TripExpense.builder().destinationId(kyotoId).category("Hotel").estimatedAmount(1000.0).actualAmount(0.0).build(),
                    TripExpense.builder().destinationId(kyotoId).category("Food").estimatedAmount(400.0).actualAmount(0.0).build(),
                    TripExpense.builder().destinationId(kyotoId).category("Transport").estimatedAmount(300.0).actualAmount(0.0).build(),
                    TripExpense.builder().destinationId(kyotoId).category("Activities").estimatedAmount(300.0).actualAmount(0.0).build()
                ));

                // Paris expenses (Visited)
                expenseRepository.saveAll(Arrays.asList(
                    TripExpense.builder().destinationId(parisId).category("Flights").estimatedAmount(900.0).actualAmount(850.0).build(),
                    TripExpense.builder().destinationId(parisId).category("Hotel").estimatedAmount(1100.0).actualAmount(1150.0).build(),
                    TripExpense.builder().destinationId(parisId).category("Food").estimatedAmount(350.0).actualAmount(400.0).build(),
                    TripExpense.builder().destinationId(parisId).category("Activities").estimatedAmount(250.0).actualAmount(200.0).build()
                ));

                // 3. Initialize Travel Memories (Pinterest wall)
                memoryRepository.saveAll(Arrays.asList(
                    TravelMemory.builder().destinationId(cairoId).caption("Gazing at the great Sphinx in Cairo").imageUrl("https://images.unsplash.com/photo-1539650116574-8efeb43e2750?auto=format&fit=crop&w=800&q=80").memoryDate("Oct 2023").build(),
                    TravelMemory.builder().destinationId(parisId).caption("Sparkling Eiffel Tower under Parisian skies").imageUrl("https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80").memoryDate("Apr 2024").build(),
                    TravelMemory.builder().destinationId(banffId).caption("Beautiful turquoise waters of Lake Louise in Banff").imageUrl("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80").memoryDate("Aug 2022").build()
                ));
            }

            // 4. Initialize Goals
            if (goalRepository.count() == 0) {
                goalRepository.saveAll(Arrays.asList(
                    TravelGoal.builder().title("Visit 5 countries").description("Explore different cultures across continents").target(5).completed(4).category("Globetrotter").build(),
                    TravelGoal.builder().title("Explore Asia").description("Visit Japan, Vietnam, Thailand and India").target(4).completed(1).category("Continent Explorer").build(),
                    TravelGoal.builder().title("Visit 3 mountain destinations").description("Hike beautiful snowy peaks and trails").target(3).completed(2).category("Mountain Soul").build(),
                    TravelGoal.builder().title("Save 5 travel memories").description("Add high quality photos to memory wall").target(5).completed(3).category("Memory Maker").build()
                ));
            }
        };
    }
}
