package com.example.travel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_goals")
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

    public TravelGoal() {}

    public TravelGoal(Long id, String title, String description, Integer target, Integer completed, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.target = target;
        this.completed = completed;
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static TravelGoalBuilder builder() {
        return new TravelGoalBuilder();
    }

    public static class TravelGoalBuilder {
        private Long id;
        private String title;
        private String description;
        private Integer target;
        private Integer completed;
        private String category;

        public TravelGoalBuilder id(Long id) { this.id = id; return this; }
        public TravelGoalBuilder title(String title) { this.title = title; return this; }
        public TravelGoalBuilder description(String description) { this.description = description; return this; }
        public TravelGoalBuilder target(Integer target) { this.target = target; return this; }
        public TravelGoalBuilder completed(Integer completed) { this.completed = completed; return this; }
        public TravelGoalBuilder category(String category) { this.category = category; return this; }

        public TravelGoal build() {
            return new TravelGoal(id, title, description, target, completed, category);
        }
    }
}
