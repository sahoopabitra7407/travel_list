package com.example.travel.controller;

import com.example.travel.model.TravelMemory;
import com.example.travel.repository.TravelMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memories")
@CrossOrigin(origins = "*")
public class TravelMemoryController {

    @Autowired
    private TravelMemoryRepository repository;

    @GetMapping
    public List<TravelMemory> getAllMemories() {
        return repository.findAll();
    }

    @GetMapping("/destination/{destinationId}")
    public List<TravelMemory> getMemoriesByDestination(@PathVariable Long destinationId) {
        return repository.findByDestinationId(destinationId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelMemory> getMemoryById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TravelMemory> createMemory(@RequestBody TravelMemory memory) {
        if (memory == null) {
            return ResponseEntity.badRequest().build();
        }
        memory.setId(null);
        return ResponseEntity.ok(repository.save(memory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelMemory> updateMemory(@PathVariable Long id, @RequestBody TravelMemory updated) {
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return repository.findById(id).map(existing -> {
            if (updated.getDestinationId() != null) existing.setDestinationId(updated.getDestinationId());
            if (updated.getCaption() != null) existing.setCaption(updated.getCaption());
            if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());
            if (updated.getMemoryDate() != null) existing.setMemoryDate(updated.getMemoryDate());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemory(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
