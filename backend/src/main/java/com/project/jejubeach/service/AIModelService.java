package com.project.jejubeach.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AIModelService {

    @Value("${ai.model.python-path:python3}")
    private String pythonPath;

    @Value("${ai.model.script-path:simple_detection_linux.py}")
    private String scriptPath;

    @Value("${ai.model.working-dir:classpath:python}")
    private String workingDir;

    @Value("${ai.model.enabled:true}")
    private boolean aiModelEnabled;

    private Process aiModelProcess;
    private long startTime;
    private int analysisCount = 0;
    private Path tempWorkingDir;

    @EventListener(ApplicationReadyEvent.class)
    public void startAIModel() {
        if (!aiModelEnabled) {
            log.info("AI 모델 자동 실행이 비활성화되어 있습니다.");
            return;
        }

        log.info("🏖️ AI 모델 자동 실행 시작...");
        log.info("📋 설정 정보:");
        log.info("   - Python 경로: {}", pythonPath);
        log.info("   - 스크립트 파일: {}", scriptPath);
        log.info("   - 작업 디렉토리: {}", workingDir);
        
        // 비동기로 AI 모델 실행
        CompletableFuture.runAsync(() -> {
            try {
                // 잠시 대기 (백엔드 완전 시작 후)
                Thread.sleep(3000);
                startAIModelProcess();
            } catch (Exception e) {
                log.error("AI 모델 실행 중 오류 발생", e);
            }
        });
    }

    private void startAIModelProcess() {
        try {
            // JAR 내부 리소스를 임시 디렉토리로 추출
            tempWorkingDir = extractJarResources();
            if (tempWorkingDir == null) {
                log.error("JAR 내부 리소스 추출 실패");
                return;
            }

            log.info("✅ 임시 작업 디렉토리 생성 완료: {}", tempWorkingDir);

            // Python 스크립트 파일 확인
            Path scriptFilePath = tempWorkingDir.resolve(scriptPath);
            if (!Files.exists(scriptFilePath)) {
                log.error("AI 모델 스크립트를 찾을 수 없습니다: {}", scriptFilePath);
                return;
            }

            log.info("✅ AI 모델 스크립트 확인 완료: {}", scriptFilePath);

            // 필요한 Python 패키지 자동 설치
            installRequiredPackages(tempWorkingDir);

            // 환경변수 설정
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                pythonPath,
                scriptPath
            );
            
            // 작업 디렉토리 설정
            processBuilder.directory(tempWorkingDir.toFile());
            
            // 환경변수 설정
            processBuilder.environment().put("BACKEND_URL", "http://localhost:8080");
            processBuilder.environment().put("ANALYSIS_INTERVAL", "30");
            
            // 표준 출력과 에러를 현재 프로세스와 연결
            processBuilder.inheritIO();
            
            log.info("🚀 AI 모델 프로세스 시작: {}", tempWorkingDir);
            log.info("🔗 백엔드 URL: http://localhost:8080");
            log.info("⏰ 분석 간격: 30초");
            
            // 프로세스 시작
            aiModelProcess = processBuilder.start();
            startTime = System.currentTimeMillis();
            
            // 프로세스가 정상적으로 시작되었는지 확인
            if (aiModelProcess.isAlive()) {
                log.info("✅ AI 모델 프로세스가 성공적으로 시작되었습니다. (PID: {})", 
                    getProcessId(aiModelProcess));
                
                // 프로세스 종료 대기 (별도 스레드에서)
                CompletableFuture.runAsync(() -> {
                    try {
                        int exitCode = aiModelProcess.waitFor();
                        log.info("🛑 AI 모델 프로세스가 종료되었습니다. (종료 코드: {})", exitCode);
                        
                        // 종료 코드에 따른 분석
                        if (exitCode == 0) {
                            log.info("✅ AI 모델이 정상적으로 종료되었습니다.");
                        } else if (exitCode == 1) {
                            log.warn("⚠️ AI 모델이 오류와 함께 종료되었습니다. (종료 코드: 1)");
                        } else {
                            log.warn("⚠️ AI 모델이 예상치 못한 종료 코드로 종료되었습니다. (종료 코드: {})", exitCode);
                        }
                        
                        // 임시 디렉토리 정리
                        cleanupTempDirectory();
                        
                    } catch (InterruptedException e) {
                        log.warn("AI 모델 프로세스 대기가 중단되었습니다.");
                        Thread.currentThread().interrupt();
                    }
                });
                
            } else {
                log.error("❌ AI 모델 프로세스 시작 실패");
            }
            
        } catch (IOException e) {
            log.error("AI 모델 프로세스 시작 중 IOException 발생", e);
        } catch (Exception e) {
            log.error("AI 모델 프로세스 시작 중 예상치 못한 오류 발생", e);
        }
    }

    private Path extractJarResources() {
        try {
            // 임시 디렉토리 생성
            Path tempDir = Files.createTempDirectory("jejubeach_ai_");
            log.info("📁 임시 디렉토리 생성: {}", tempDir);

            // Python 스크립트 추출
            extractResource("python/" + scriptPath, tempDir.resolve(scriptPath));
            
            // requirements.txt 추출
            extractResource("python/requirements.txt", tempDir.resolve("requirements.txt"));
            
            // YOLO 모델 파일 추출
            extractResource("python/yolov8n.pt", tempDir.resolve("yolov8n.pt"));
            
            // 비디오 파일들 추출
            String[] videoFiles = {"hamduck_beach.mp4", "iho_beach.mp4", "walljeonglee_beach.mp4", "test_beach.mp4"};
            for (String videoFile : videoFiles) {
                try {
                    extractResource("videos/" + videoFile, tempDir.resolve(videoFile));
                } catch (Exception e) {
                    log.warn("비디오 파일 {} 추출 실패: {}", videoFile, e.getMessage());
                }
            }

            return tempDir;
            
        } catch (Exception e) {
            log.error("JAR 리소스 추출 중 오류 발생", e);
            return null;
        }
    }

    private void extractResource(String resourcePath, Path targetPath) throws IOException {
        try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("✅ 리소스 추출 완료: {} -> {}", resourcePath, targetPath);
        } catch (IOException e) {
            log.warn("⚠️ 리소스 추출 실패: {} - {}", resourcePath, e.getMessage());
            throw e;
        }
    }

    private void cleanupTempDirectory() {
        if (tempWorkingDir != null && Files.exists(tempWorkingDir)) {
            try {
                // 임시 디렉토리 내 모든 파일 삭제
                Files.walk(tempWorkingDir)
                    .sorted((a, b) -> b.compareTo(a)) // 역순으로 정렬 (파일 먼저, 디렉토리 나중에)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.warn("임시 파일 삭제 실패: {}", path);
                        }
                    });
                log.info("✅ 임시 디렉토리 정리 완료: {}", tempWorkingDir);
            } catch (IOException e) {
                log.warn("⚠️ 임시 디렉토리 정리 중 오류: {}", e.getMessage());
            }
        }
    }

    private void installRequiredPackages(Path workingPath) {
        try {
            log.info("📦 필요한 Python 패키지 설치 확인 중...");
            
            // requirements.txt 파일 확인
            Path requirementsFile = workingPath.resolve("requirements.txt");
            
            if (!Files.exists(requirementsFile)) {
                log.warn("⚠️ requirements.txt 파일을 찾을 수 없습니다.");
                return;
            }
            
            log.info("📋 requirements.txt 사용");
            
            // 가상환경의 pip 사용
            String pipPath = pythonPath.replace("python3", "pip3");
            
            // requirements 파일로 패키지 설치
            try {
                log.info("🔄 패키지 설치 시작: requirements.txt");
                
                ProcessBuilder pipBuilder = new ProcessBuilder();
                pipBuilder.command(
                    pipPath, "install", "-r", "requirements.txt"
                );
                pipBuilder.directory(workingPath.toFile());
                
                Process pipProcess = pipBuilder.start();
                boolean completed = pipProcess.waitFor(3, java.util.concurrent.TimeUnit.MINUTES);
                
                if (completed && pipProcess.exitValue() == 0) {
                    log.info("✅ Python 패키지 설치가 완료되었습니다.");
                } else {
                    log.warn("⚠️ Python 패키지 설치 중 일부 오류가 발생했습니다. (종료 코드: {})", 
                        completed ? pipProcess.exitValue() : "타임아웃");
                }
                
            } catch (Exception e) {
                log.warn("⚠️ 패키지 설치 중 오류 발생: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            log.warn("⚠️ Python 패키지 설치 확인 중 오류 발생: {}", e.getMessage());
        }
    }
    
    private void installBasicPackage(String packageName) {
        try {
            ProcessBuilder pipBuilder = new ProcessBuilder();
            pipBuilder.command(pythonPath, "-m", "pip", "install", packageName);
            
            Process pipProcess = pipBuilder.start();
            boolean completed = pipProcess.waitFor(1, java.util.concurrent.TimeUnit.MINUTES);
            
            if (completed && pipProcess.exitValue() == 0) {
                log.info("✅ 기본 패키지 {} 설치 완료", packageName);
            } else {
                log.warn("⚠️ 기본 패키지 {} 설치 실패", packageName);
            }
            
        } catch (Exception e) {
            log.warn("⚠️ 기본 패키지 {} 설치 중 오류: {}", packageName, e.getMessage());
        }
    }

    private Long getProcessId(Process process) {
        try {
            // Java 9+ 에서는 ProcessHandle 사용
            if (process.getClass().getName().equals("java.lang.ProcessImpl")) {
                return process.pid();
            }
        } catch (Exception e) {
            // Java 8 이하에서는 pid() 메서드가 없음
        }
        return -1L;
    }

    public void startAIModelManually() {
        if (aiModelProcess != null && aiModelProcess.isAlive()) {
            log.warn("⚠️ AI 모델이 이미 실행 중입니다.");
            return;
        }
        
        log.info("🚀 AI 모델 수동 시작 요청...");
        
        // 비동기로 AI 모델 실행
        CompletableFuture.runAsync(() -> {
            try {
                startAIModelProcess();
            } catch (Exception e) {
                log.error("AI 모델 수동 실행 중 오류 발생", e);
            }
        });
    }

    public void stopAIModel() {
        if (aiModelProcess != null && aiModelProcess.isAlive()) {
            log.info("🛑 AI 모델 프로세스 종료 중...");
            
            try {
                // 프로세스 종료
                aiModelProcess.destroy();
                
                // 5초 대기 후 강제 종료
                if (!aiModelProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.warn("⚠️ AI 모델 프로세스 강제 종료 중...");
                    aiModelProcess.destroyForcibly();
                }
                
                log.info("✅ AI 모델 프로세스가 종료되었습니다.");
                
                // 임시 디렉토리 정리
                cleanupTempDirectory();
                
            } catch (InterruptedException e) {
                log.warn("AI 모델 프로세스 종료 대기 중 중단됨");
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isAIModelRunning() {
        return aiModelProcess != null && aiModelProcess.isAlive();
    }

    public String getAIModelStatus() {
        if (aiModelProcess == null) {
            return "NOT_STARTED";
        } else if (aiModelProcess.isAlive()) {
            return "RUNNING";
        } else {
            return "STOPPED";
        }
    }
    
    public long getRunningTime() {
        if (startTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public int getAnalysisCount() {
        return analysisCount;
    }
    
    public void incrementAnalysisCount() {
        analysisCount++;
    }
}
