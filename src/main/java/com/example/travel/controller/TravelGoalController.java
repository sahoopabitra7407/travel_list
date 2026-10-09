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
    public ResponseEntity<TravelGoal> createGoal(@RequestBody TravelGoal goal) {
        if (goal == null) {
            return ResponseEntity.badRequest().build();
        }
        goal.setId(null);
        if (goal.getTarget() == null) {
            goal.setTarget(1);
        }
        if (goal.getCompleted() == null) {
            goal.setCompleted(0);
        }
        return ResponseEntity.ok(repository.save(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelGoal> updateGoal(@PathVariable Long id, @RequestBody TravelGoal updated) {
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
            if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
            if (updated.getTarget() != null) existing.setTarget(updated.getTarget());
            if (updated.getCompleted() != null) existing.setCompleted(updated.getCompleted());
            if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
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
