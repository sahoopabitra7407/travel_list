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

    @PostMapping
    public TripExpense createExpense(@RequestBody TripExpense expense) {
        return repository.save(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripExpense> updateExpense(@PathVariable Long id, @RequestBody TripExpense updated) {
        return repository.findById(id).map(existing -> {
            existing.setDestinationId(updated.getDestinationId());
            existing.setCategory(updated.getCategory());
            existing.setEstimatedAmount(updated.getEstimatedAmount());
            existing.setActualAmount(updated.getActualAmount());
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
