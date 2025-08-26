package com.project.jejubeach.repository;

import com.project.jejubeach.entity.BeachManager;
import com.project.jejubeach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeachManagerRepository extends JpaRepository<BeachManager, Long> {
    
    // 특정 매니저가 관리하는 해변들 조회
    List<BeachManager> findByUserAndIsActiveTrue(User user);
    
    // 특정 해변을 관리하는 매니저 조회
    Optional<BeachManager> findByBeachIdAndIsActiveTrue(Long beachId);
    
    // 특정 해변과 매니저의 매핑 조회
    Optional<BeachManager> findByBeachIdAndUserIdAndIsActiveTrue(Long beachId, Long userId);
    
    // 특정 매니저가 관리하는 해변 ID 목록 조회
    @Query("SELECT bm.beach.id FROM BeachManager bm WHERE bm.user.id = :userId AND bm.isActive = true")
    List<Long> findBeachIdsByUserId(@Param("userId") Long userId);
    
    // 특정 해변을 관리하는 매니저 ID 조회
    @Query("SELECT bm.user.id FROM BeachManager bm WHERE bm.beach.id = :beachId AND bm.isActive = true")
    Optional<Long> findUserIdByBeachId(@Param("beachId") Long beachId);
}
