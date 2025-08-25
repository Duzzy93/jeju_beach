package com.project.jejubeach.service;

import com.project.jejubeach.dto.BeachRequest;
import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.entity.User;
import com.project.jejubeach.repository.BeachRepository;
import com.project.jejubeach.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeachService {
    
    private final BeachRepository beachRepository;
    private final UserRepository userRepository;

    public List<Beach> getAllBeaches() {
        return beachRepository.findAll();
    }

    public List<Beach> getActiveBeaches() {
        return beachRepository.findByStatus(Beach.BeachStatus.ACTIVE);
    }

    public Optional<Beach> getBeachById(Long id) {
        return beachRepository.findById(id);
    }

    public List<Beach> getBeachesByRegion(String region) {
        return beachRepository.findByRegion(region);
    }

    public List<Beach> searchBeachesByName(String name) {
        return beachRepository.findByNameContainingIgnoreCase(name);
    }

    public Beach createBeach(BeachRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Beach beach = Beach.builder()
                .name(request.getName())
                .region(request.getRegion())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .description(request.getDescription())
                .status(Beach.BeachStatus.ACTIVE)
                .createdBy(user)
                .build();

        return beachRepository.save(beach);
    }

    public Beach updateBeach(Long id, BeachRequest request, String username) {
        Beach beach = beachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해변을 찾을 수 없습니다"));

        // 권한 확인 (생성자 또는 ADMIN만 수정 가능)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (!beach.getCreatedBy().getId().equals(user.getId()) && 
            !user.getRole().equals(User.UserRole.ADMIN)) {
            throw new RuntimeException("해변을 수정할 권한이 없습니다");
        }

        beach.setName(request.getName());
        beach.setRegion(request.getRegion());
        beach.setLatitude(request.getLatitude());
        beach.setLongitude(request.getLongitude());
        beach.setDescription(request.getDescription());

        return beachRepository.save(beach);
    }

    public void deleteBeach(Long id, String username) {
        Beach beach = beachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해변을 찾을 수 없습니다"));

        // 권한 확인 (생성자 또는 ADMIN만 삭제 가능)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (!beach.getCreatedBy().getId().equals(user.getId()) && 
            !user.getRole().equals(User.UserRole.ADMIN)) {
            throw new RuntimeException("해변을 삭제할 권한이 없습니다");
        }

        beachRepository.delete(beach);
    }

    public Beach toggleBeachStatus(Long id, String username) {
        Beach beach = beachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해변을 찾을 수 없습니다"));

        // 권한 확인 (ADMIN만 상태 변경 가능)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (!user.getRole().equals(User.UserRole.ADMIN)) {
            throw new RuntimeException("해변 상태를 변경할 권한이 없습니다");
        }

        if (beach.getStatus() == Beach.BeachStatus.ACTIVE) {
            beach.setStatus(Beach.BeachStatus.INACTIVE);
        } else {
            beach.setStatus(Beach.BeachStatus.ACTIVE);
        }

        return beachRepository.save(beach);
    }
}
