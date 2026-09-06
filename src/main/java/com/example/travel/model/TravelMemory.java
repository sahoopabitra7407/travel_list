package com.example.travel.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "travel_memories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
