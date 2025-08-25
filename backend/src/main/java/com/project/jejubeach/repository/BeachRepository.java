package com.project.jejubeach.repository;

import com.project.jejubeach.entity.Beach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeachRepository extends JpaRepository<Beach, Long> {
    
    List<Beach> findByStatus(Beach.BeachStatus status);
    
    List<Beach> findByRegion(String region);
    
    List<Beach> findByNameContainingIgnoreCase(String name);
}
