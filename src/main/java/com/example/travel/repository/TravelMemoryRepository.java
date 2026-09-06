package com.example.travel.repository;

import com.example.travel.model.TravelMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelMemoryRepository extends JpaRepository<TravelMemory, Long> {
    List<TravelMemory> findByDestinationId(Long destinationId);
}
