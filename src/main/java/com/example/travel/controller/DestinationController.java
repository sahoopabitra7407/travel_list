package com.example.travel.controller;

import com.example.travel.model.Destination;
import com.example.travel.repository.DestinationRepository;
import com.example.travel.repository.TravelMemoryRepository;
import com.example.travel.repository.TripExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*")
public class DestinationController {

    @Autowired
    private DestinationRepository repository;

    @Autowired
    private TripExpenseRepository expenseRepository;

    @Autowired
    private TravelMemoryRepository memoryRepository;

    @GetMapping
    public List<Destination> getAllDestinations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String mood,
            @RequestParam(required = false) String search) {
        
        if (search != null && !search.trim().isEmpty()) {
            return repository.searchDestinations(search.trim());
        }
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            return repository.findByStatus(status.toUpperCase());
        }
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            return repository.findByCategory(category);
        }
        if (mood != null && !mood.trim().isEmpty() && !"ALL".equalsIgnoreCase(mood)) {
            return repository.findByMood(mood);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Destination> createDestination(@RequestBody Destination destination) {
        if (destination == null) {
            return ResponseEntity.badRequest().build();
        }
        destination.setId(null);
        if (destination.getStatus() == null || destination.getStatus().trim().isEmpty()) {
            destination.setStatus("WISHLIST");
        }
        return ResponseEntity.ok(repository.save(destination));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @RequestBody Destination updated) {
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
            if (updated.getCountry() != null) existing.setCountry(updated.getCountry());
            if (updated.getCity() != null) existing.setCity(updated.getCity());
            if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
            if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
            if (updated.getMood() != null) existing.setMood(updated.getMood());
            if (updated.getTargetDate() != null) existing.setTargetDate(updated.getTargetDate());
            if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
            if (updated.getRating() != null) existing.setRating(updated.getRating());
            if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());
            if (updated.getBudget() != null) existing.setBudget(updated.getBudget());
            if (updated.getBestSeason() != null) existing.setBestSeason(updated.getBestSeason());
            if (updated.getDuration() != null) existing.setDuration(updated.getDuration());
            if (updated.getLatitude() != null) existing.setLatitude(updated.getLatitude());
            if (updated.getLongitude() != null) existing.setLongitude(updated.getLongitude());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Destination> updateStatus(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        if (payload == null || !payload.containsKey("status") || payload.get("status") == null) {
            return ResponseEntity.badRequest().build();
        }
        String newStatus = payload.get("status").trim();
        if (newStatus.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            existing.setStatus(newStatus.toUpperCase());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        if (repository.existsById(id)) {
            expenseRepository.deleteByDestinationId(id);
            memoryRepository.deleteByDestinationId(id);
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<Destination> all = repository.findAll();
        long total = all.size();
        long wishlist = all.stream().filter(d -> "WISHLIST".equalsIgnoreCase(d.getStatus())).count();
        long planned = all.stream().filter(d -> "PLANNED".equalsIgnoreCase(d.getStatus())).count();
        long visited = all.stream().filter(d -> "VISITED".equalsIgnoreCase(d.getStatus())).count();
        double totalBudget = all.stream().mapToDouble(d -> d.getBudget() != null ? d.getBudget().doubleValue() : 0.0).sum();
        
        // Count distinct countries null-safely
        long countriesCount = all.stream()
                .map(Destination::getCountry)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("wishlist", wishlist);
        stats.put("planned", planned);
        stats.put("visited", visited);
        stats.put("totalBudget", totalBudget);
        stats.put("countries", countriesCount);
        return stats;
    }
}
