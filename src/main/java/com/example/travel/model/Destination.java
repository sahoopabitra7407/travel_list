package com.example.travel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "destinations")
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

    public Destination() {}

    public Destination(Long id, String title, String country, String city, String description,
                       String category, String mood, String targetDate, String status,
                       Integer rating, String imageUrl, Double budget, String bestSeason,
                       String duration, Double latitude, Double longitude, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.country = country;
        this.city = city;
        this.description = description;
        this.category = category;
        this.mood = mood;
        this.targetDate = targetDate;
        this.status = status;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.budget = budget;
        this.bestSeason = bestSeason;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "WISHLIST";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(String bestSeason) {
        this.bestSeason = bestSeason;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static DestinationBuilder builder() {
        return new DestinationBuilder();
    }

    public static class DestinationBuilder {
        private Long id;
        private String title;
        private String country;
        private String city;
        private String description;
        private String category;
        private String mood;
        private String targetDate;
        private String status;
        private Integer rating;
        private String imageUrl;
        private Double budget;
        private String bestSeason;
        private String duration;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;

        public DestinationBuilder id(Long id) { this.id = id; return this; }
        public DestinationBuilder title(String title) { this.title = title; return this; }
        public DestinationBuilder country(String country) { this.country = country; return this; }
        public DestinationBuilder city(String city) { this.city = city; return this; }
        public DestinationBuilder description(String description) { this.description = description; return this; }
        public DestinationBuilder category(String category) { this.category = category; return this; }
        public DestinationBuilder mood(String mood) { this.mood = mood; return this; }
        public DestinationBuilder targetDate(String targetDate) { this.targetDate = targetDate; return this; }
        public DestinationBuilder status(String status) { this.status = status; return this; }
        public DestinationBuilder rating(Integer rating) { this.rating = rating; return this; }
        public DestinationBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public DestinationBuilder budget(Double budget) { this.budget = budget; return this; }
        public DestinationBuilder bestSeason(String bestSeason) { this.bestSeason = bestSeason; return this; }
        public DestinationBuilder duration(String duration) { this.duration = duration; return this; }
        public DestinationBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public DestinationBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public DestinationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Destination build() {
            return new Destination(id, title, country, city, description, category, mood, targetDate, status, rating, imageUrl, budget, bestSeason, duration, latitude, longitude, createdAt);
        }
    }
}
