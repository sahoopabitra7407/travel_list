package com.example.travel.repository;

import com.example.travel.model.TripExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TripExpenseRepository extends JpaRepository<TripExpense, Long> {
    List<TripExpense> findByDestinationId(Long destinationId);

    @Transactional
    void deleteByDestinationId(Long destinationId);
}
