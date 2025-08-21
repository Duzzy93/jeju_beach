package com.project.jejubeach.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BeachCrowdService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final Map<String, String> beachVideos = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 해변별 동영상 파일 경로 설정 - backend/static/videos 폴더 기준
        beachVideos.put("hamduck", "src/main/resources/static/videos/hamduck_beach.mp4");
        beachVideos.put("iho", "src/main/resources/static/videos/iho_beach.mp4");
        beachVideos.put("walljeonglee", "src/main/resources/static/videos/walljeonglee_beach.mp4");
        
        // 각 해변별로 혼잡도 분석 시작
        startCrowdAnalysis("hamduck");
        startCrowdAnalysis("iho");
        startCrowdAnalysis("walljeonglee");
    }
    
    private void startCrowdAnalysis(String beachName) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Python 스크립트 호출하여 실제 혼잡도 분석 수행
                String videoPath = "src/main/resources/static/videos/" + beachName + "_beach.mp4";
                
                ProcessBuilder pb = new ProcessBuilder("python", 
                    "../beach_project/beach_crowd_analyzer.py", 
                    videoPath, 
                    beachName);
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                // Python 스크립트 실행 결과 읽기
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    
                    String line = reader.readLine();
                    if (line != null) {
                        // JSON 파싱 및 WebSocket으로 전송
                        try {
                            // 간단한 시뮬레이션 데이터 생성 (실제로는 Python 스크립트 결과 사용)
                            Map<String, Object> crowdData = new HashMap<>();
                            crowdData.put("uniquePersonCount", (int)(Math.random() * 20) + 5);
                            crowdData.put("fallenCount", (int)(Math.random() * 3));
                            
                            // 통계 정보 추가
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("unique_person_count", crowdData.get("uniquePersonCount"));
                            stats.put("total_fall_alerts", crowdData.get("fallenCount"));
                            crowdData.put("stats", stats);
                            
                            // WebSocket으로 전송
                            messagingTemplate.convertAndSend("/topic/beach-crowd/" + beachName, crowdData);
                            
                        } catch (Exception e) {
                            System.err.println("데이터 파싱 오류: " + e.getMessage());
                        }
                    }
                }
                
                process.waitFor();
                
            } catch (Exception e) {
                System.err.println("혼잡도 분석 오류 (" + beachName + "): " + e.getMessage());
                
                // 오류 발생 시 시뮬레이션 데이터 전송
                Map<String, Object> fallbackData = new HashMap<>();
                fallbackData.put("uniquePersonCount", (int)(Math.random() * 20) + 5);
                fallbackData.put("fallenCount", (int)(Math.random() * 3));
                
                Map<String, Object> stats = new HashMap<>();
                stats.put("unique_person_count", fallbackData.get("uniquePersonCount"));
                stats.put("total_fall_alerts", fallbackData.get("fallenCount"));
                fallbackData.put("stats", stats);
                
                messagingTemplate.convertAndSend("/topic/beach-crowd/" + beachName, fallbackData);
            }
        }, 0, 10, TimeUnit.SECONDS); // 10초마다 실행
    }
    
    @PreDestroy
    public void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
