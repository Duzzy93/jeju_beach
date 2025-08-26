package com.project.jejubeach.service;

import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.entity.User;
import com.project.jejubeach.repository.UserRepository;
import com.project.jejubeach.repository.BeachManagerRepository;
import com.project.jejubeach.repository.BeachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final BeachManagerRepository beachManagerRepository;
    private final BeachRepository beachRepository;

    /**
     * 사용자의 역할을 조회합니다.
     */
    public String getUserRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return user.getRole().name();
    }

    /**
     * 사용자가 접근 가능한 해변들을 조회합니다.
     */
    public List<Beach> getAccessibleBeaches(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        if (user.getRole() == User.UserRole.ADMIN) {
            // ADMIN은 모든 해변 접근 가능
            return beachRepository.findAll();
        } else if (user.getRole() == User.UserRole.MANAGER) {
            // MANAGER는 할당된 해변만 접근 가능
            List<Long> beachIds = beachManagerRepository.findBeachIdsByUserId(user.getId());
            if (beachIds.isEmpty()) {
                return List.of(); // 할당된 해변이 없음
            }
            // 해변 ID로 해변 정보 조회
            return beachRepository.findAllById(beachIds);
        } else {
            // USER는 모든 해변 조회 가능 (수정/삭제 불가)
            return beachRepository.findAll();
        }
    }

    /**
     * 사용자명으로 사용자를 조회합니다.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }
}
