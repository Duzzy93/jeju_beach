package com.project.jejubeach.service;

import com.project.jejubeach.entity.Detection;
import com.project.jejubeach.repository.DetectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectionService {
    
    private final DetectionRepository repo;
    
    @Transactional
    public Detection saveAndTrim(int personCount, int fallenCount, String source) {
        Detection saved = repo.save(
            Detection.builder()
                     .personCount(personCount)
                     .fallenCount(fallenCount)
                     .source(source)
                     .build()
        );
        // 최신 10개만 남기기
        repo.trimToKeep(10);
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Detection getLatest() {
        return repo.findTopByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public List<Detection> getLatest10() {
        return repo.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Detection getLatestByBeach(String beachName) {
        // 해변 이름에 따른 source 패턴 매칭
        String sourcePattern = getSourcePattern(beachName);
        return repo.findTopBySourceContainingOrderByCreatedAtDesc(sourcePattern);
    }

    private String getSourcePattern(String beachName) {
        switch (beachName.toLowerCase()) {
            case "hamduck":
            case "함덕":
                return "hamduck";
            case "iho":
            case "이호":
                return "iho";
            case "walljeonglee":
            case "월정리":
                return "walljeonglee";
            default:
                return beachName.toLowerCase();
        }
    }
}
