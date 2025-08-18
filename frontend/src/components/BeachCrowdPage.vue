<template>
  <div class="beach-crowd-page">
    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="container text-center">
        <h1 class="display-4 fw-bold mb-4">제주 해변 실시간 혼잡도</h1>
        <p class="lead">AI 기술로 분석하는 제주 해변의 실시간 혼잡도 정보</p>
      </div>
    </section>

    <!-- 해변 카드 섹션 -->
    <section class="py-5">
      <div class="container">
        <div class="row g-4">
          <!-- 함덕해변 -->
          <div class="col-md-4">
            <div class="card beach-card h-100" @click="goToBeachDetail('hamduck')">
              <div class="card-body text-center">
                <i class="bi bi-water display-1 text-primary mb-3"></i>
                <h5 class="card-title">함덕해변</h5>
                <p class="card-text">제주도 동부의 아름다운 해변</p>
                <div class="mt-3">
                  <span class="badge" :class="getDensityBadgeClass(hamduckDensity)">
                    {{ getDensityText(hamduckDensity) }}
                  </span>
                </div>
                <div class="mt-2">
                  <small class="text-muted">인원: {{ hamduckCount }}명</small>
                </div>
              </div>
            </div>
          </div>

          <!-- 이호해변 -->
          <div class="col-md-4">
            <div class="card beach-card h-100" @click="goToBeachDetail('iho')">
              <div class="card-body text-center">
                <i class="bi bi-water display-1 text-primary mb-3"></i>
                <h5 class="card-title">이호해변</h5>
                <p class="card-text">제주도 서부의 평화로운 해변</p>
                <div class="mt-3">
                  <span class="badge" :class="getDensityBadgeClass(ihoDensity)">
                    {{ getDensityText(ihoDensity) }}
                  </span>
                </div>
                <div class="mt-2">
                  <small class="text-muted">인원: {{ ihoCount }}명</small>
                </div>
              </div>
            </div>
          </div>

          <!-- 월정리해변 -->
          <div class="col-md-4">
            <div class="card beach-card h-100" @click="goToBeachDetail('walljeonglee')">
              <div class="card-body text-center">
                <i class="bi bi-water display-1 text-primary mb-3"></i>
                <h5 class="card-title">월정리해변</h5>
                <p class="card-text">제주도 동부의 유명한 해변</p>
                <div class="mt-3">
                  <span class="badge" :class="getDensityBadgeClass(walljeongleeDensity)">
                    {{ getDensityText(walljeongleeDensity) }}
                  </span>
                </div>
                <div class="mt-2">
                  <small class="text-muted">인원: {{ walljeongleeCount }}명</small>
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
export default {
  name: 'BeachCrowdPage',
  data() {
    return {
      hamduckDensity: 'low',
      hamduckCount: 0,
      ihoDensity: 'medium',
      ihoCount: 0,
      walljeongleeDensity: 'high',
      walljeongleeCount: 0,
      stompClient: null
    }
  },
  mounted() {
    this.connectWebSocket();
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  },
  methods: {
    goToBeachDetail(beachName) {
      this.$router.push(`/beach-crowd/${beachName}`);
    },
    connectWebSocket() {
      // WebSocket 연결 로직
      try {
        // SockJS와 STOMP를 사용한 WebSocket 연결
        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
          console.log('WebSocket 연결됨:', frame);
          
          // 각 해변별로 구독
          this.stompClient.subscribe('/topic/beach-crowd/hamduck', (message) => {
            const data = JSON.parse(message.body);
            this.hamduckCount = data.personCount;
            this.hamduckDensity = this.getDensityLevel(this.hamduckCount);
          });
          
          this.stompClient.subscribe('/topic/beach-crowd/iho', (message) => {
            const data = JSON.parse(message.body);
            this.ihoCount = data.personCount;
            this.ihoDensity = this.getDensityLevel(this.ihoCount);
          });
          
          this.stompClient.subscribe('/topic/beach-crowd/walljeonglee', (message) => {
            const data = JSON.parse(message.body);
            this.walljeongleeCount = data.personCount;
            this.walljeongleeDensity = this.getDensityLevel(this.walljeongleeCount);
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
    startSimulation() {
      // 각 해변별로 시뮬레이션 데이터 생성
      setInterval(() => {
        this.hamduckCount = Math.floor(Math.random() * 20) + 5;
        this.hamduckDensity = this.getDensityLevel(this.hamduckCount);
        
        this.ihoCount = Math.floor(Math.random() * 20) + 5;
        this.ihoDensity = this.getDensityLevel(this.ihoCount);
        
        this.walljeongleeCount = Math.floor(Math.random() * 20) + 5;
        this.walljeongleeDensity = this.getDensityLevel(this.walljeongleeCount);
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
