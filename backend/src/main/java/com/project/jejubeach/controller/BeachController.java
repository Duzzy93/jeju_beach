package com.project.jejubeach.controller;

import com.project.jejubeach.dto.BeachRequest;
import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.service.BeachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beaches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BeachController {
    
    private final BeachService beachService;

    @GetMapping
    public ResponseEntity<List<Beach>> getAllBeaches() {
        List<Beach> beaches = beachService.getAllBeaches();
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Beach>> getActiveBeaches() {
        List<Beach> beaches = beachService.getActiveBeaches();
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beach> getBeachById(@PathVariable Long id) {
        return beachService.getBeachById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<Beach>> getBeachesByRegion(@PathVariable String region) {
        List<Beach> beaches = beachService.getBeachesByRegion(region);
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Beach>> searchBeachesByName(@RequestParam String name) {
        List<Beach> beaches = beachService.searchBeachesByName(name);
        return ResponseEntity.ok(beaches);
    }

    @PostMapping
    public ResponseEntity<Beach> createBeach(@Valid @RequestBody BeachRequest request) {
        String username = getCurrentUsername();
        Beach beach = beachService.createBeach(request, username);
        return ResponseEntity.ok(beach);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beach> updateBeach(@PathVariable Long id, @Valid @RequestBody BeachRequest request) {
        String username = getCurrentUsername();
        Beach beach = beachService.updateBeach(id, request, username);
        return ResponseEntity.ok(beach);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeach(@PathVariable Long id) {
        String username = getCurrentUsername();
        beachService.deleteBeach(id, username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Beach> toggleBeachStatus(@PathVariable Long id) {
        String username = getCurrentUsername();
        Beach beach = beachService.toggleBeachStatus(id, username);
        return ResponseEntity.ok(beach);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
