package com.example.travel.controller;

import com.example.travel.model.Destination;
import com.example.travel.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*")
public class DestinationController {

    @Autowired
    private DestinationRepository repository;

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
    public Destination createDestination(@RequestBody Destination destination) {
        return repository.save(destination);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @RequestBody Destination updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setCountry(updated.getCountry());
            existing.setCity(updated.getCity());
            existing.setDescription(updated.getDescription());
            existing.setCategory(updated.getCategory());
            existing.setMood(updated.getMood());
            existing.setTargetDate(updated.getTargetDate());
            existing.setStatus(updated.getStatus());
            existing.setRating(updated.getRating());
            existing.setImageUrl(updated.getImageUrl());
            existing.setBudget(updated.getBudget());
            existing.setBestSeason(updated.getBestSeason());
            existing.setDuration(updated.getDuration());
            existing.setLatitude(updated.getLatitude());
            existing.setLongitude(updated.getLongitude());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Destination> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            existing.setStatus(newStatus.toUpperCase());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        if (repository.existsById(id)) {
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
        
        // Count distinct countries
        long countriesCount = all.stream()
                .map(d -> d.getCountry().trim().toLowerCase())
                .filter(c -> !c.isEmpty())
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
