package com.example.travel.repository;

import com.example.travel.model.TravelGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelGoalRepository extends JpaRepository<TravelGoal, Long> {
}
