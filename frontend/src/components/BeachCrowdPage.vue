<template>
  <div class="beach-crowd-page">
    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="container text-center">
        <h1 class="display-4 fw-bold mb-4">제주 해변 실시간 혼잡도</h1>
        <p class="lead">AI 기술로 분석하는 제주 해변의 실시간 혼잡도 정보</p>
        <div v-if="userRole" class="mt-3">
          <span class="badge bg-info fs-6">
            <i class="bi bi-person-badge me-2"></i>
            {{ getUserRoleText() }} - {{ getAssignedBeachesText() }}
          </span>
        </div>
      </div>
    </section>

    <!-- 실시간 탐지 데이터 섹션 -->
    <section class="py-4 bg-light">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h3 class="text-center mb-4">
              <i class="bi bi-activity me-2"></i>
              실시간 탐지 현황
            </h3>
            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-primary">
                  <div class="card-body text-center">
                    <i class="bi bi-people display-4 text-primary mb-2"></i>
                    <h5 class="card-title">탐지된 인원 수</h5>
                    <div class="display-6 fw-bold text-primary">
                      {{ latestDetection?.personCount || 0 }}
                    </div>
                    <small class="text-muted">
                      {{ latestDetection?.createdAt ? formatTime(latestDetection.createdAt) : '데이터 없음' }}
                    </small>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-danger">
                  <div class="card-body text-center">
                    <i class="bi bi-exclamation-triangle display-4 text-danger mb-2"></i>
                    <h5 class="card-title">쓰러진 사람 수</h5>
                    <div class="display-6 fw-bold text-danger">
                      {{ latestDetection?.fallenCount || 0 }}
                    </div>
                    <small class="text-muted">
                      {{ latestDetection?.source || '카메라 정보 없음' }}
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 해변 카드 섹션 -->
    <section class="py-5">
      <div class="container">
        <div v-if="loading" class="text-center">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">로딩 중...</span>
          </div>
          <p class="mt-2">해변 정보를 불러오는 중...</p>
        </div>
        
        <div v-else-if="accessibleBeaches.length === 0" class="text-center">
          <div class="alert alert-warning" role="alert">
            <i class="bi bi-exclamation-triangle display-4 text-warning mb-3"></i>
            <h4>접근 가능한 해변이 없습니다</h4>
            <p>현재 계정으로는 관리할 수 있는 해변이 없습니다.</p>
            <p class="mb-0">관리자에게 문의하시기 바랍니다.</p>
          </div>
        </div>
        
        <div v-else class="row g-4">
          <div 
            v-for="beach in accessibleBeaches" 
            :key="beach.id" 
            class="col-md-4"
          >
            <div class="card beach-card h-100" @click="goToBeachDetail(beach)">
              <div class="card-body text-center">
                <i class="bi bi-water display-1 text-primary mb-3"></i>
                <h5 class="card-title">{{ beach.name }}</h5>
                <p class="card-text">{{ beach.description || '해변 정보' }}</p>
                <div class="mt-3">
                  <span class="badge" :class="getDensityBadgeClass(beach.density)">
                    {{ getDensityText(beach.density) }}
                  </span>
                </div>
                <div class="mt-2">
                  <small class="text-muted">
                    현재: {{ beach.currentCount }}명 | 총: {{ beach.uniqueCount }}명
                  </small>
                  <div v-if="beach.fallenCount > 0" class="mt-1">
                    <small class="text-danger">
                      <i class="bi bi-exclamation-triangle me-1"></i>
                      쓰러짐: {{ beach.fallenCount }}명
                    </small>
                  </div>
                </div>
                <div class="mt-2">
                  <small class="text-muted">{{ beach.region }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { userApi } from '../api/userApi'
import { beachApi } from '../api/beachApi'

export default {
  name: 'BeachCrowdPage',
  data() {
    return {
      userRole: null, // 사용자 권한 정보
      accessibleBeaches: [], // 사용자가 접근 가능한 해변 목록
      loading: true, // 데이터 로딩 상태
      stompClient: null,
      latestDetection: null, // 최신 탐지 데이터
      pollingInterval: null // WebSocket 연결 간격
    }
  },
  mounted() {
    this.fetchUserRole(); // 사용자 권한 페칭
    this.fetchAccessibleBeaches(); // 접근 가능한 해변 페칭
    this.startPolling(); // 실시간 탐지 데이터 폴링 시작
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  },
  methods: {
    goToBeachDetail(beach) {
      const beachKey = this.getBeachKey(beach.name);
      this.$router.push(`/beach-crowd/${beachKey}`);
    },
    
    async fetchUserRole() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.userRole = 'GUEST';
          return;
        }
        
        const data = await userApi.getUserRole();
        this.userRole = data.role;
        console.log('사용자 권한:', this.userRole);
      } catch (error) {
        console.error('사용자 권한 페칭 중 오류:', error);
        this.userRole = 'USER'; // 기본값
      }
    },
    
    async fetchAccessibleBeaches() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          // 게스트는 모든 해변 표시 (하지만 수정 불가)
          console.log('토큰이 없어 기본 해변 정보를 사용합니다.');
          this.accessibleBeaches = this.getDefaultBeaches();
          this.loading = false;
          return;
        }
        
        console.log('해변 정보를 가져오는 중...');
        const beaches = await userApi.getAccessibleBeaches();
        console.log('API에서 받은 해변 데이터:', beaches);
        
        // 각 해변에 혼잡도 데이터 초기화
        this.accessibleBeaches = beaches.map(beach => ({
          ...beach,
          currentCount: 0,
          uniqueCount: 0,
          fallenCount: 0,
          density: 'low'
        }));
        this.loading = false;
        console.log('처리된 해변 정보:', this.accessibleBeaches);
        
        // WebSocket 연결
        this.connectWebSocket();
      } catch (error) {
        console.error('접근 가능한 해변 페칭 중 오류:', error);
        console.error('오류 상세 정보:', error.message);
        
        // fallback으로 모든 해변 정보를 가져와보기
        console.log('fallback으로 모든 해변 정보를 가져오는 중...');
        try {
          const allBeaches = await beachApi.getAllBeaches();
          console.log('fallback 해변 데이터:', allBeaches);
          this.accessibleBeaches = allBeaches.map(beach => ({
            ...beach,
            currentCount: 0,
            uniqueCount: 0,
            fallenCount: 0,
            density: 'low'
          }));
        } catch (fallbackError) {
          console.error('fallback API 호출 중 오류:', fallbackError);
          this.accessibleBeaches = this.getDefaultBeaches();
        }
        
        this.loading = false;
      }
    },
    
    getDefaultBeaches() {
      return [
        { id: 1, name: '함덕해변', region: '제주시 구좌읍', description: '제주도 동부의 아름다운 해변', currentCount: 0, uniqueCount: 0, fallenCount: 0, density: 'low' },
        { id: 2, name: '이호테우해변', region: '제주시 이도1동', description: '제주도 서부의 평화로운 해변', currentCount: 0, uniqueCount: 0, fallenCount: 0, density: 'medium' },
        { id: 3, name: '월정리해변', region: '제주시 구좌읍', description: '제주도 동부의 유명한 해변', currentCount: 0, uniqueCount: 0, fallenCount: 0, density: 'high' }
      ];
    },
    
    connectWebSocket() {
      try {
        const socket = new SockJS(process.env.NODE_ENV === 'production' 
          ? 'http://15.165.30.16:8080/ws' 
          : 'http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
          console.log('WebSocket 연결됨:', frame);
          
          // 할당된 해변들에 대해서만 WebSocket 구독
          this.accessibleBeaches.forEach(beach => {
            const beachKey = this.getBeachKey(beach.name);
            this.stompClient.subscribe(`/topic/beach-crowd/${beachKey}`, (message) => {
              const data = JSON.parse(message.body);
              this.updateBeachData(beach.id, data);
            });
          });
        }, (error) => {
          console.error('WebSocket 연결 실패:', error);
          // 연결 실패 시 시뮬레이션 데이터 사용
          this.startSimulation();
        });
      } catch (error) {
        console.error('WebSocket 초기화 실패:', error);
        // 초기화 실패 시 시뮬레이션 데이터 사용
        this.startSimulation();
      }
    },
    
    updateBeachData(beachId, data) {
      const beach = this.accessibleBeaches.find(b => b.id === beachId);
      if (beach) {
        beach.currentCount = data.personCount;
        beach.uniqueCount = data.uniquePersonCount || data.personCount;
        beach.fallenCount = data.fallenCount || 0;
        beach.density = this.getDensityLevel(beach.currentCount);
      }
      this.latestDetection = data; // 최신 탐지 데이터 업데이트
    },
    
    getBeachKey(beachName) {
      if (beachName.includes('함덕')) return 'hamduck';
      if (beachName.includes('이호')) return 'iho';
      if (beachName.includes('월정리')) return 'walljeonglee';
      return beachName.toLowerCase().replace(/\s/g, '');
    },
    
    startSimulation() {
      // 할당된 해변들에 대해서만 시뮬레이션 데이터 생성
      setInterval(() => {
        this.accessibleBeaches.forEach(beach => {
          beach.currentCount = Math.floor(Math.random() * 20) + 5;
          beach.uniqueCount = beach.currentCount + Math.floor(Math.random() * 5);
          beach.fallenCount = Math.floor(Math.random() * 3);
          beach.density = this.getDensityLevel(beach.currentCount);
        });
      }, 5000);
    },
    
    getDensityLevel(count) {
      if (count < 5) return 'low';
      else if (count < 15) return 'medium';
      else return 'high';
    },
    
    getDensityText(density) {
      switch (density) {
        case 'low': return '혼잡도 낮음';
        case 'medium': return '혼잡도 중간';
        case 'high': return '혼잡도 높음';
        default: return '혼잡도 낮음';
      }
    },
    
    getDensityBadgeClass(density) {
      switch (density) {
        case 'low': return 'bg-success';
        case 'medium': return 'bg-warning text-dark';
        case 'high': return 'bg-danger';
        default: return 'bg-secondary';
      }
    },
    
    getUserRoleText() {
      switch (this.userRole) {
        case 'ADMIN': return '관리자';
        case 'MANAGER': return '매니저';
        case 'USER': return '사용자';
        case 'GUEST': return '게스트';
        default: return '알 수 없음';
      }
    },
    
    getAssignedBeachesText() {
      if (this.accessibleBeaches.length === 0) {
        return '해변을 관리하지 않습니다.';
      }
      if (this.userRole === 'ADMIN') {
        return '모든 해변 관리';
      } else if (this.userRole === 'MANAGER') {
        return this.accessibleBeaches.map(beach => beach.name).join(', ') + ' 관리';
      } else {
        return '모든 해변 조회';
      }
    },

    formatTime(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleTimeString();
    },

    startPolling() {
      this.pollingInterval = setInterval(() => {
        this.fetchLatestDetection();
      }, 1000); // 1초마다 최신 탐지 데이터 폴링
    },

    async fetchLatestDetection() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.latestDetection = null; // 게스트는 최신 탐지 데이터 표시 안 함
          return;
        }

        const apiUrl = process.env.NODE_ENV === 'production' 
          ? 'http://15.165.30.16:8080/api/detections/latest'
          : 'http://localhost:8080/api/detections/latest';
          
        const response = await fetch(apiUrl, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.ok) {
          const data = await response.json();
          this.latestDetection = data;
          console.log('최신 탐지 데이터:', this.latestDetection);
        } else {
          console.error('최신 탐지 데이터 페칭 실패:', response.status);
          this.latestDetection = null;
        }
      } catch (error) {
        console.error('최신 탐지 데이터 페칭 중 오류:', error);
        this.latestDetection = null;
      }
    }
  }
}
</script>

<style scoped>
.beach-crowd-page {
  min-height: 100vh;
}

.beach-card {
  transition: transform 0.3s ease;
  cursor: pointer;
}

.beach-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
}

.badge {
  font-size: 0.9rem;
  padding: 0.5rem 1rem;
}
</style>
