package com.example.travel.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long destinationId;

    @Column(nullable = false)
    private String category; // Flights, Hotel, Food, Transport, Activities, Shopping, Other

    @Column(nullable = false)
    private Double estimatedAmount;

    @Column(nullable = false)
    private Double actualAmount;
}
