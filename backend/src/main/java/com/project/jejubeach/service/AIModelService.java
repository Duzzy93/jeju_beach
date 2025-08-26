package com.project.jejubeach.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AIModelService {

    @Value("${ai.model.python-path:python}")
    private String pythonPath;

    @Value("${ai.model.script-path:simple_detection_windows.py}")
    private String scriptPath;

    @Value("${ai.model.working-dir:../beach_project}")
    private String workingDir;

    @Value("${ai.model.enabled:true}")
    private boolean aiModelEnabled;

    private Process aiModelProcess;
    private long startTime;
    private int analysisCount = 0;

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
            // 작업 디렉토리 확인
            Path workingPath = Paths.get(workingDir);
            if (!Files.exists(workingPath)) {
                log.error("AI 모델 작업 디렉토리를 찾을 수 없습니다: {}", workingDir);
                return;
            }

            // Python 스크립트 파일 확인
            Path scriptFilePath = workingPath.resolve(scriptPath);
            if (!Files.exists(scriptFilePath)) {
                log.error("AI 모델 스크립트를 찾을 수 없습니다: {}", scriptFilePath);
                log.error("사용 가능한 스크립트 파일들:");
                try {
                    Files.list(workingPath)
                        .filter(path -> path.toString().endsWith(".py"))
                        .forEach(path -> log.error("   - {}", path.getFileName()));
                } catch (IOException e) {
                    log.error("디렉토리 목록 읽기 실패: {}", e.getMessage());
                }
                return;
            }

            log.info("✅ AI 모델 스크립트 확인 완료: {}", scriptFilePath);

            // 필요한 Python 패키지 자동 설치
            installRequiredPackages(workingPath);

            // 환경변수 설정
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                pythonPath,
                scriptPath
            );
            
            // 작업 디렉토리 설정
            processBuilder.directory(workingPath.toFile());
            
            // 환경변수 설정
            processBuilder.environment().put("BACKEND_URL", "http://localhost:8080");
            processBuilder.environment().put("ANALYSIS_INTERVAL", "30");
            
            // 표준 출력과 에러를 현재 프로세스와 연결
            processBuilder.inheritIO();
            
            log.info("🚀 AI 모델 프로세스 시작: {}", workingPath);
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
            
            // 기본 패키지 먼저 설치 (requests)
            log.info("🔄 기본 패키지 설치 시작: requests");
            installBasicPackage("requests");
            
            // requirements 파일로 추가 패키지 설치
            try {
                log.info("🔄 추가 패키지 설치 시작: requirements.txt");
                
                ProcessBuilder pipBuilder = new ProcessBuilder();
                pipBuilder.command(
                    pythonPath, "-m", "pip", "install", "-r", "requirements.txt"
                );
                pipBuilder.directory(workingPath.toFile());
                
                Process pipProcess = pipBuilder.start();
                boolean completed = pipProcess.waitFor(2, java.util.concurrent.TimeUnit.MINUTES);
                
                if (completed && pipProcess.exitValue() == 0) {
                    log.info("✅ Python 패키지 설치가 완료되었습니다.");
                } else {
                    log.warn("⚠️ Python 패키지 설치 중 일부 오류가 발생했습니다. (종료 코드: {})", 
                        completed ? pipProcess.exitValue() : "타임아웃");
                }
                
            } catch (Exception e) {
                log.warn("⚠️ 추가 패키지 설치 중 오류 발생: {}", e.getMessage());
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
