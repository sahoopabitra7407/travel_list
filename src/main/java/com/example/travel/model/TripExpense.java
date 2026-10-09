package com.example.travel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trip_expenses")
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

    public TripExpense() {}

    public TripExpense(Long id, Long destinationId, String category, Double estimatedAmount, Double actualAmount) {
        this.id = id;
        this.destinationId = destinationId;
        this.category = category;
        this.estimatedAmount = estimatedAmount;
        this.actualAmount = actualAmount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(Double estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public static TripExpenseBuilder builder() {
        return new TripExpenseBuilder();
    }

    public static class TripExpenseBuilder {
        private Long id;
        private Long destinationId;
        private String category;
        private Double estimatedAmount;
        private Double actualAmount;

        public TripExpenseBuilder id(Long id) { this.id = id; return this; }
        public TripExpenseBuilder destinationId(Long destinationId) { this.destinationId = destinationId; return this; }
        public TripExpenseBuilder category(String category) { this.category = category; return this; }
        public TripExpenseBuilder estimatedAmount(Double estimatedAmount) { this.estimatedAmount = estimatedAmount; return this; }
        public TripExpenseBuilder actualAmount(Double actualAmount) { this.actualAmount = actualAmount; return this; }

        public TripExpense build() {
            return new TripExpense(id, destinationId, category, estimatedAmount, actualAmount);
        }
    }
}
