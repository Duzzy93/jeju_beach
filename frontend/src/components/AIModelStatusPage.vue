<template>
  <div class="ai-model-status-page">
    <div class="container py-5">
      <div class="row">
        <div class="col-12">
          <h1 class="display-5 fw-bold mb-4">
            <i class="bi bi-robot me-3"></i>
            AI 모델 상태 관리
          </h1>
          <p class="lead mb-5">
            제주 해변 실시간 혼잡도 분석을 위한 AI 모델의 상태를 확인하고 제어할 수 있습니다.
          </p>
        </div>
      </div>

      <!-- AI 모델 상태 카드 -->
      <div class="row mb-4">
        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-info-circle me-2"></i>
                현재 상태
              </h5>
            </div>
            <div class="card-body text-center">
              <div class="mb-3">
                <i class="bi" :class="getStatusIcon()" :style="{ fontSize: '4rem', color: getStatusColor() }"></i>
              </div>
              <h4 class="card-title" :class="getStatusColor()">
                {{ getStatusText() }}
              </h4>
              <p class="card-text">
                {{ getStatusDescription() }}
              </p>
              <div class="mt-3">
                <small class="text-muted">
                  마지막 업데이트: {{ lastUpdateTime }}
                </small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-gear me-2"></i>
                제어 패널
              </h5>
            </div>
            <div class="card-body">
              <div class="d-grid gap-2">
                <button 
                  @click="startAIModel" 
                  class="btn btn-success btn-lg"
                  :disabled="aiModelStatus === 'RUNNING'"
                >
                  <i class="bi bi-play-circle me-2"></i>
                  AI 모델 시작
                </button>
                
                <button 
                  @click="stopAIModel" 
                  class="btn btn-danger btn-lg"
                  :disabled="aiModelStatus === 'STOPPED' || aiModelStatus === 'NOT_STARTED'"
                >
                  <i class="bi bi-stop-circle me-2"></i>
                  AI 모델 중지
                </button>
                
                <button 
                  @click="restartAIModel" 
                  class="btn btn-warning btn-lg"
                  :disabled="aiModelStatus === 'NOT_STARTED'"
                >
                  <i class="bi bi-arrow-clockwise me-2"></i>
                  AI 모델 재시작
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 상세 정보 -->
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-list-ul me-2"></i>
                상세 정보
              </h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <h6>시스템 정보</h6>
                  <ul class="list-unstyled">
                    <li><strong>Python 경로:</strong> {{ systemInfo.pythonPath }}</li>
                    <li><strong>작업 디렉토리:</strong> {{ systemInfo.workingDir }}</li>
                    <li><strong>스크립트 파일:</strong> {{ systemInfo.scriptPath }}</li>
                    <li><strong>분석 간격:</strong> {{ systemInfo.analysisInterval }}초</li>
                  </ul>
                </div>
                <div class="col-md-6">
                  <h6>실행 정보</h6>
                  <ul class="list-unstyled">
                    <li><strong>상태:</strong> <span :class="getStatusBadgeClass()">{{ getStatusText() }}</span></li>
                    <li><strong>실행 시간:</strong> {{ runningTime }}</li>
                    <li><strong>마지막 분석:</strong> {{ lastAnalysisTime }}</li>
                    <li><strong>총 분석 횟수:</strong> {{ totalAnalysisCount }}</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 로그 출력 -->
      <div class="row mt-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0">
                <i class="bi bi-terminal me-2"></i>
                실시간 로그
              </h5>
              <button @click="clearLogs" class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-trash me-1"></i>
                로그 지우기
              </button>
            </div>
            <div class="card-body">
              <div class="log-container" ref="logContainer">
                <div 
                  v-for="(log, index) in logs" 
                  :key="index" 
                  class="log-entry"
                  :class="getLogLevelClass(log.level)"
                >
                  <span class="log-timestamp">{{ log.timestamp }}</span>
                  <span class="log-level">{{ log.level }}</span>
                  <span class="log-message">{{ log.message }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AIModelStatusPage',
  data() {
    return {
      aiModelStatus: 'NOT_STARTED',
      lastUpdateTime: '로딩 중...',
      runningTime: '00:00:00',
      lastAnalysisTime: '아직 없음',
      totalAnalysisCount: 0,
      logs: [],
      systemInfo: {
        pythonPath: 'python',
        workingDir: '../beach_project',
        scriptPath: 'simple_detection_windows.py',
        analysisInterval: 30
      },
      statusUpdateInterval: null,
      logUpdateInterval: null
    }
  },
  mounted() {
    console.log('AIModelStatusPage mounted');
    console.log('Current route:', this.$route.path);
    console.log('Auth check starting...');
    
    // 인증 상태 확인
    if (!this.checkAuth()) {
      console.log('Auth check failed, redirecting to login');
      this.$router.push('/login');
      return;
    }
    
    console.log('Auth check passed, loading data...');
    this.loadStatus();
    this.loadSystemInfo();
    this.startStatusPolling();
    this.startLogPolling();
  },
  beforeUnmount() {
    if (this.statusUpdateInterval) {
      clearInterval(this.statusUpdateInterval);
    }
    if (this.logUpdateInterval) {
      clearInterval(this.logUpdateInterval);
    }
  },
  methods: {
    checkAuth() {
      console.log('checkAuth called');
      const token = localStorage.getItem('token');
      const userRole = localStorage.getItem('role'); // 'userRole' -> 'role'로 수정
      
      console.log('Token:', token);
      console.log('User role:', userRole);
      
      if (!token) {
        console.log('No token found');
        return false;
      }
      
      // ADMIN 또는 MANAGER만 접근 가능
      if (userRole !== 'ADMIN' && userRole !== 'MANAGER') {
        console.log('Insufficient role:', userRole);
        this.addLog('ERROR', 'AI 모델 관리 권한이 없습니다. ADMIN 또는 MANAGER 권한이 필요합니다.');
        return false;
      }
      
      console.log('Auth check passed for role:', userRole);
      return true;
    },
    
    async loadStatus() {
      try {
        // 인증 토큰 가져오기
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('인증 토큰이 없습니다. 로그인이 필요합니다.');
        }

        const response = await fetch('http://localhost:8080/api/ai-model/status', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const responseText = await response.text();
        
        if (!responseText || responseText.trim() === '') {
          throw new Error('응답이 비어있습니다');
        }
        
        let data;
        try {
          data = JSON.parse(responseText);
        } catch (parseError) {
          throw new Error(`JSON 파싱 실패: ${parseError.message}. 응답: ${responseText}`);
        }
        
        if (data && data.status) {
          this.aiModelStatus = data.status;
          this.lastUpdateTime = new Date().toLocaleString();
          
          // 실행 시간과 분석 횟수 업데이트
          if (data.runningTime) {
            this.runningTime = this.formatRunningTime(data.runningTime);
          }
          if (data.analysisCount !== undefined) {
            this.totalAnalysisCount = data.analysisCount;
          }
          
          this.addLog('INFO', `AI 모델 상태 업데이트: ${data.status}`);
        } else {
          throw new Error('응답에 status 필드가 없습니다');
        }
        
      } catch (error) {
        console.error('AI 모델 상태 로드 실패:', error);
        this.addLog('ERROR', 'AI 모델 상태 로드 실패: ' + error.message);
        
        // 오류 발생 시 상태를 UNKNOWN으로 설정
        this.aiModelStatus = 'UNKNOWN';
        this.lastUpdateTime = '오류 발생';
      }
    },

    async loadSystemInfo() {
      try {
        // 인증 토큰 가져오기
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('인증 토큰이 없습니다. 로그인이 필요합니다.');
        }

        const response = await fetch('http://localhost:8080/api/ai-model/info', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        const data = await response.json();
        if (data) {
          this.systemInfo = {
            pythonPath: data.pythonPath || 'python',
            workingDir: data.workingDir || '../beach_project',
            scriptPath: data.scriptPath || 'simple_detection_windows.py',
            analysisInterval: data.analysisInterval || 30
          };
          this.addLog('INFO', '시스템 정보 업데이트 완료');
        } else {
          this.addLog('WARN', '시스템 정보 로드 실패 또는 응답이 비어있습니다.');
        }
      } catch (error) {
        console.error('시스템 정보 로드 실패:', error);
        this.addLog('ERROR', '시스템 정보 로드 중 오류: ' + error.message);
      }
    },

    // 공통 API 호출 메서드
    async callAIModelAPI(endpoint, action) {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('인증 토큰이 없습니다. 로그인이 필요합니다.');
        }

        const response = await fetch(`http://localhost:8080/api/ai-model/${endpoint}`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        const data = await response.json();
        
        if (data.success) {
          this.addLog('INFO', `AI 모델 ${action} 요청 성공`);
          this.loadStatus();
        } else {
          this.addLog('ERROR', `AI 모델 ${action} 실패: ${data.message}`);
        }
      } catch (error) {
        console.error(`AI 모델 ${action} 실패:`, error);
        this.addLog('ERROR', `AI 모델 ${action} 중 오류: ${error.message}`);
      }
    },

    async startAIModel() {
      await this.callAIModelAPI('start', '시작');
    },

    async stopAIModel() {
      await this.callAIModelAPI('stop', '중지');
    },

    async restartAIModel() {
      await this.callAIModelAPI('restart', '재시작');
    },

    startStatusPolling() {
      this.statusUpdateInterval = setInterval(() => {
        this.loadStatus();
      }, 5000); // 5초마다 상태 업데이트
    },

    startLogPolling() {
      this.logUpdateInterval = setInterval(() => {
        // 실제로는 백엔드에서 로그를 가져와야 함
        // 여기서는 시뮬레이션
        if (this.aiModelStatus === 'RUNNING') {
          this.simulateLogs();
        }
      }, 10000); // 10초마다 로그 업데이트
    },

    simulateLogs() {
      const logMessages = [
        { level: 'INFO', message: '함덕해변 분석 완료 - 현재 인원: 15명, 쓰러짐: 0명' },
        { level: 'INFO', message: '이호해변 분석 완료 - 현재 인원: 8명, 쓰러짐: 1명' },
        { level: 'INFO', message: '월정리해변 분석 완료 - 현재 인원: 23명, 쓰러짐: 0명' },
        { level: 'INFO', message: '탐지 데이터를 백엔드로 전송 완료' }
      ];

      const randomLog = logMessages[Math.floor(Math.random() * logMessages.length)];
      this.addLog(randomLog.level, randomLog.message);
    },

    addLog(level, message) {
      this.logs.push({
        timestamp: new Date().toLocaleTimeString(),
        level: level,
        message: message
      });

      // 최근 100개 로그만 유지
      if (this.logs.length > 100) {
        this.logs.shift();
      }

      // 로그 컨테이너를 맨 아래로 스크롤
      this.$nextTick(() => {
        if (this.$refs.logContainer) {
          this.$refs.logContainer.scrollTop = this.$refs.logContainer.scrollHeight;
        }
      });
    },

    clearLogs() {
      this.logs = [];
    },

    // 상태별 설정 정보
    getStatusConfig() {
      const configs = {
        'RUNNING': {
          icon: 'bi-play-circle-fill text-success',
          color: 'text-success',
          text: '실행 중',
          description: 'AI 모델이 정상적으로 실행 중이며 해변 혼잡도를 실시간으로 분석하고 있습니다.',
          badge: 'badge bg-success'
        },
        'STOPPED': {
          icon: 'bi-stop-circle-fill text-danger',
          color: 'text-danger',
          text: '중지됨',
          description: 'AI 모델이 중지되었습니다. 필요시 다시 시작할 수 있습니다.',
          badge: 'badge bg-danger'
        },
        'NOT_STARTED': {
          icon: 'bi-question-circle-fill text-secondary',
          color: 'text-secondary',
          text: '시작되지 않음',
          description: 'AI 모델이 아직 시작되지 않았습니다. 시작 버튼을 시작하세요.',
          badge: 'badge bg-secondary'
        },
        'UNKNOWN': {
          icon: 'bi-question-circle-fill text-warning',
          color: 'text-warning',
          text: '상태 불명',
          description: 'AI 모델의 상태를 확인할 수 없습니다. 백엔드 연결을 확인해주세요.',
          badge: 'badge bg-warning'
        }
      };
      
      return configs[this.aiModelStatus] || configs['UNKNOWN'];
    },

    getStatusIcon() {
      return this.getStatusConfig().icon;
    },

    getStatusColor() {
      return this.getStatusConfig().color;
    },

    getStatusText() {
      return this.getStatusConfig().text;
    },

    getStatusDescription() {
      return this.getStatusConfig().description;
    },

    getStatusBadgeClass() {
      return this.getStatusConfig().badge;
    },

    getLogLevelClass(level) {
      const levelClasses = {
        'ERROR': 'log-error',
        'WARN': 'log-warn',
        'INFO': 'log-info'
      };
      return levelClasses[level] || 'log-debug';
    },
    
    formatRunningTime(milliseconds) {
      const totalSeconds = Math.floor(milliseconds / 1000);
      const h = Math.floor(totalSeconds / 3600);
      const m = Math.floor((totalSeconds % 3600) / 60);
      const s = totalSeconds % 60;
      return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    }
  }
}
</script>

<style scoped>
.ai-model-status-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.log-container {
  height: 300px;
  overflow-y: auto;
  background-color: #1e1e1e;
  color: #ffffff;
  padding: 1rem;
  border-radius: 0.375rem;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}

.log-entry {
  margin-bottom: 0.5rem;
  padding: 0.25rem 0;
}

.log-timestamp {
  color: #888;
  margin-right: 0.5rem;
}

.log-level {
  font-weight: bold;
  margin-right: 0.5rem;
  min-width: 60px;
  display: inline-block;
}

.log-info .log-level {
  color: #17a2b8;
}

.log-warn .log-level {
  color: #ffc107;
}

.log-error .log-level {
  color: #dc3545;
}

.log-debug .log-level {
  color: #6c757d;
}

.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
  border: 1px solid rgba(0, 0, 0, 0.125);
}

.btn-lg {
  padding: 0.75rem 1.5rem;
  font-size: 1.1rem;
}

.badge {
  font-size: 0.875rem;
  padding: 0.5rem 0.75rem;
}
</style>
