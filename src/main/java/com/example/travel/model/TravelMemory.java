package com.example.travel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_memories")
public class TravelMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long destinationId;

    @Column(nullable = false)
    private String caption;

    @Column(length = 1000)
    private String imageUrl;

    private String memoryDate;

    public TravelMemory() {}

    public TravelMemory(Long id, Long destinationId, String caption, String imageUrl, String memoryDate) {
        this.id = id;
        this.destinationId = destinationId;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.memoryDate = memoryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMemoryDate() {
        return memoryDate;
    }

    public void setMemoryDate(String memoryDate) {
        this.memoryDate = memoryDate;
    }

    public static TravelMemoryBuilder builder() {
        return new TravelMemoryBuilder();
    }

    public static class TravelMemoryBuilder {
        private Long id;
        private Long destinationId;
        private String caption;
        private String imageUrl;
        private String memoryDate;

        public TravelMemoryBuilder id(Long id) { this.id = id; return this; }
        public TravelMemoryBuilder destinationId(Long destinationId) { this.destinationId = destinationId; return this; }
        public TravelMemoryBuilder caption(String caption) { this.caption = caption; return this; }
        public TravelMemoryBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public TravelMemoryBuilder memoryDate(String memoryDate) { this.memoryDate = memoryDate; return this; }

        public TravelMemory build() {
            return new TravelMemory(id, destinationId, caption, imageUrl, memoryDate);
        }
    }
}
