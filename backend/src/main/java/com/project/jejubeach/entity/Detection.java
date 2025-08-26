package com.project.jejubeach.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "detection")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Detection {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "person_count", nullable = false)
    private int personCount;
    
    @Column(name = "fallen_count", nullable = false)
    private int fallenCount;
    
    @Column(length = 64)
    private String source;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
