package com.example.travel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String country;

    private String city;

    @Column(length = 2000)
    private String description;

    private String category; // e.g. Adventure, Beach, Culture, Nature, Mountain, Food, Romantic, City, Wildlife, Photography
    
    private String mood; // e.g. Adventure, Relaxation, Food, Culture, Nature, Nightlife, Romantic, Photography

    private String targetDate; // e.g. "Spring 2027" or "2026-10-15"

    @Column(nullable = false)
    private String status; // WISHLIST, PLANNED, VISITED

    private Integer rating; // 1-5 stars

    @Column(length = 1000)
    private String imageUrl;

    private Double budget;

    private String bestSeason; // e.g. "Mar–Apr", "Summer", "Year-round"

    private String duration; // e.g. "5–7 days"

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "WISHLIST";
        }
    }
}
