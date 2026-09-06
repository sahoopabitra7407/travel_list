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

    @PostMapping
    public TravelMemory createMemory(@RequestBody TravelMemory memory) {
        return repository.save(memory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelMemory> updateMemory(@PathVariable Long id, @RequestBody TravelMemory updated) {
        return repository.findById(id).map(existing -> {
            existing.setDestinationId(updated.getDestinationId());
            existing.setCaption(updated.getCaption());
            existing.setImageUrl(updated.getImageUrl());
            existing.setMemoryDate(updated.getMemoryDate());
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
