package com.example.travel.controller;

import com.example.travel.model.TravelGoal;
import com.example.travel.repository.TravelGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class TravelGoalController {

    @Autowired
    private TravelGoalRepository repository;

    @GetMapping
    public List<TravelGoal> getAllGoals() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelGoal> getGoalById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TravelGoal createGoal(@RequestBody TravelGoal goal) {
        return repository.save(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelGoal> updateGoal(@PathVariable Long id, @RequestBody TravelGoal updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setTarget(updated.getTarget());
            existing.setCompleted(updated.getCompleted());
            existing.setCategory(updated.getCategory());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
