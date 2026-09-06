package com.example.travel.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "travel_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Integer target;

    @Column(nullable = false)
    private Integer completed;

    private String category;
}
