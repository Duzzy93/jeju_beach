package com.project.jejubeach.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "beaches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beach {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String region;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "video_path", length = 512)
    private String videoPath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BeachStatus status = BeachStatus.ACTIVE;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BeachStatus {
        ACTIVE, INACTIVE
    }
}
