package com.example.travel.controller;

import com.example.travel.model.TripExpense;
import com.example.travel.repository.TripExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class TripExpenseController {

    @Autowired
    private TripExpenseRepository repository;

    @GetMapping
    public List<TripExpense> getAllExpenses() {
        return repository.findAll();
    }

    @GetMapping("/destination/{destinationId}")
    public List<TripExpense> getExpensesByDestination(@PathVariable Long destinationId) {
        return repository.findByDestinationId(destinationId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripExpense> getExpenseById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TripExpense> createExpense(@RequestBody TripExpense expense) {
        if (expense == null) {
            return ResponseEntity.badRequest().build();
        }
        expense.setId(null);
        if (expense.getEstimatedAmount() == null) {
            expense.setEstimatedAmount(0.0);
        }
        if (expense.getActualAmount() == null) {
            expense.setActualAmount(0.0);
        }
        return ResponseEntity.ok(repository.save(expense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripExpense> updateExpense(@PathVariable Long id, @RequestBody TripExpense updated) {
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            if (updated.getDestinationId() != null) existing.setDestinationId(updated.getDestinationId());
            if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
            if (updated.getEstimatedAmount() != null) existing.setEstimatedAmount(updated.getEstimatedAmount());
            if (updated.getActualAmount() != null) existing.setActualAmount(updated.getActualAmount());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
