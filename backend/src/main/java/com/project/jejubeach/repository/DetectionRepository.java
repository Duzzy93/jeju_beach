package com.project.jejubeach.repository;

import com.project.jejubeach.entity.Detection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetectionRepository extends JpaRepository<Detection, Long> {
    
    // 최신 1건
    Detection findTopByOrderByCreatedAtDesc();
    
    // 최신 10건
    List<Detection> findTop10ByOrderByCreatedAtDesc();
    
    // 특정 source 패턴을 포함하는 최신 1건
    Detection findTopBySourceContainingOrderByCreatedAtDesc(String sourcePattern);
    
    // 최신 10건만 남기고 나머지 삭제 (MySQL 8+ 윈도우 함수)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM detection
        WHERE id IN (
          SELECT id FROM (
            SELECT id, ROW_NUMBER() OVER (ORDER BY created_at DESC) AS rn
            FROM detection
          ) t WHERE t.rn > :keep
        )
        """, nativeQuery = true)
    int trimToKeep(@Param("keep") int keep);
}
