package com.project.jejubeach.service;

import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.entity.User;
import com.project.jejubeach.repository.UserRepository;
import com.project.jejubeach.repository.BeachManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final BeachManagerRepository beachManagerRepository;

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
            return user.getBeaches();
        } else if (user.getRole() == User.UserRole.MANAGER) {
            // MANAGER는 할당된 해변만 접근 가능
            List<Long> beachIds = beachManagerRepository.findBeachIdsByUserId(user.getId());
            if (beachIds.isEmpty()) {
                return List.of(); // 할당된 해변이 없음
            }
            // 해변 ID로 해변 정보 조회
            return beachIds.stream()
                    .map(beachId -> {
                        // 여기서는 간단하게 해변 정보를 생성
                        // 실제로는 BeachRepository를 통해 조회해야 함
                        return Beach.builder()
                                .id(beachId)
                                .name(getBeachNameById(beachId))
                                .region(getBeachRegionById(beachId))
                                .description(getBeachDescriptionById(beachId))
                                .build();
                    })
                    .toList();
        } else {
            // USER는 모든 해변 조회 가능 (수정/삭제 불가)
            return user.getBeaches();
        }
    }
    
    // 임시 메서드 - 실제로는 BeachRepository를 통해 조회해야 함
    private String getBeachNameById(Long beachId) {
        switch (beachId.intValue()) {
            case 1: return "함덕해변";
            case 2: return "이호테우해변";
            case 3: return "월정리해변";
            default: return "알 수 없는 해변";
        }
    }
    
    private String getBeachRegionById(Long beachId) {
        switch (beachId.intValue()) {
            case 1: return "제주시 구좌읍";
            case 2: return "제주시 이도1동";
            case 3: return "제주시 구좌읍";
            default: return "알 수 없는 지역";
        }
    }
    
    private String getBeachDescriptionById(Long beachId) {
        switch (beachId.intValue()) {
            case 1: return "제주도 동부의 아름다운 백사장과 에메랄드빛 바다가 어우러진 해변";
            case 2: return "제주시내에서 가까운 접근성이 좋은 해변";
            case 3: return "제주 최동단에 위치한 아름다운 해변";
            default: return "해변 정보가 없습니다.";
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
