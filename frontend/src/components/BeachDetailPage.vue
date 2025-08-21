<template>
  <div class="beach-detail-page">

    <!-- 해변 정보 섹션 -->
    <section class="beach-info-section py-5">
      <div class="container">
        <div class="row">
          <div class="col-lg-8">
            <h1 class="display-5 fw-bold mb-4">{{ beachDisplayName }}</h1>
            <p class="lead mb-4">{{ beachDescription }}</p>
            
            <!-- 실시간 혼잡도 정보 -->
            <div class="row g-4 mb-5">
              <div class="col-md-3">
                <div class="card text-center">
                  <div class="card-body">
                    <i class="bi bi-people display-4 text-primary mb-3"></i>
                    <h5 class="card-title">현재 인원</h5>
                    <p class="display-6 fw-bold" :class="getDensityTextColor()">{{ personCount }}명</p>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card text-center">
                  <div class="card-body">
                    <i class="bi bi-person-badge display-4 text-success mb-3"></i>
                    <h5 class="card-title">총 방문자</h5>
                    <p class="display-6 fw-bold text-success">{{ uniquePersonCount }}명</p>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card text-center">
                  <div class="card-body">
                    <i class="bi bi-speedometer2 display-4 text-warning mb-3"></i>
                    <h5 class="card-title">혼잡도</h5>
                    <p class="display-6 fw-bold" :class="getDensityTextColor()">{{ densityLevel }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card text-center">
                  <div class="card-body">
                    <i class="bi bi-clock display-4 text-info mb-3"></i>
                    <h5 class="card-title">업데이트</h5>
                    <p class="h6">{{ lastUpdate }}</p>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 쓰러짐 감지 알림 -->
            <div v-if="fallenCount > 0" class="alert alert-danger mb-4" role="alert">
              <div class="d-flex align-items-center">
                <i class="bi bi-exclamation-triangle-fill me-3 display-6"></i>
                <div>
                  <h5 class="alert-heading mb-1">⚠️ 쓰러짐 감지 알림</h5>
                  <p class="mb-0">현재 {{ fallenCount }}명의 쓰러짐 상황이 감지되었습니다. 즉시 확인이 필요합니다.</p>
                </div>
              </div>
            </div>

            <!-- 동영상 플레이어 -->
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-camera-video me-2"></i>
                  실시간 해변 영상
                </h5>
              </div>
              <div class="card-body">
                <div class="video-container">
                  <video 
                    ref="videoPlayer" 
                    controls
                    autoplay
                    loop
                    muted
                    class="w-100"
                    :src="videoSource"
                    @loadedmetadata="onVideoLoaded"
                  >
                    브라우저가 비디오 태그를 지원하지 않습니다.
                  </video>
                </div>
                <div class="mt-3">
                  <small class="text-muted">
                    <i class="bi bi-info-circle me-1"></i>
                    AI 기술로 분석된 실시간 혼잡도 정보가 5초마다 업데이트됩니다.
                  </small>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <!-- 해변 정보 카드 -->
            <div class="card mb-4">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-geo-alt me-2"></i>
                  해변 정보
                </h5>
              </div>
              <div class="card-body">
                <ul class="list-unstyled">
                  <li class="mb-2">
                    <i class="bi bi-map me-2 text-primary"></i>
                    <strong>위치:</strong> {{ beachLocation }}
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-water me-2 text-info"></i>
                    <strong>특징:</strong> {{ beachFeatures }}
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-sun me-2 text-warning"></i>
                    <strong>최적 방문시간:</strong> {{ bestTime }}
                  </li>
                </ul>
              </div>
            </div>

            <!-- 혼잡도 히스토리 -->
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-graph-up me-2"></i>
                  혼잡도 추이
                </h5>
              </div>
              <div class="card-body">
                <canvas ref="densityChart" width="300" height="200"></canvas>
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
  name: 'BeachDetailPage',
  data() {
    return {
      beachName: '',
      beachDisplayName: '',
      beachDescription: '',
      beachLocation: '',
      beachFeatures: '',
      bestTime: '',
      personCount: 0,
      uniquePersonCount: 0,
      fallenCount: 0,
      densityLevel: '낮음',
      lastUpdate: '방금 전',
      videoSource: '',
      densityHistory: [],
      updateInterval: null,
      stompClient: null
    }
  },
  mounted() {
    this.initializeBeachData();
    this.startRealTimeUpdates();
    this.loadVideo();
  },
  watch: {
    '$route.params.beachName'(newVal) {
      // 파라미터 변경 시 데이터 재초기화
      this.initializeBeachData();
      this.loadVideo();
      this.startRealTimeUpdates();
    }
  },
  beforeUnmount() {
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
    }
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  },
  methods: {
    initializeBeachData() {
      const beachName = this.$route.params.beachName;
      this.beachName = beachName;
      
      // 해변별 정보 설정
      switch (beachName) {
        case 'hamduck':
          this.beachDisplayName = '함덕해변';
          this.beachDescription = '제주도 동부에 위치한 아름다운 해변으로, 맑은 물과 깨끗한 모래사장이 특징입니다.';
          this.beachLocation = '제주특별자치도 제주시 구좌읍';
          this.beachFeatures = '맑은 물, 깨끗한 모래사장, 아름다운 경관';
          this.bestTime = '오전 9시 ~ 오후 3시';
          this.videoSource = 'http://localhost:8080/hamduck_beach.mp4';
          break;
        case 'iho':
          this.beachDisplayName = '이호해변';
          this.beachDescription = '제주도 서부의 평화로운 해변으로, 일몰을 감상하기 좋은 곳입니다.';
          this.beachLocation = '제주특별자치도 제주시 이호동';
          this.beachFeatures = '평화로운 분위기, 일몰 감상, 조용한 환경';
          this.bestTime = '오후 4시 ~ 저녁 8시';
          this.videoSource = 'http://localhost:8080/iho_beach.mp4';
          break;
        case 'walljeonglee':
          this.beachDisplayName = '월정리해변';
          this.beachDescription = '제주도 동부의 유명한 해변으로, 투명한 물과 다양한 해양생물이 특징입니다.';
          this.beachLocation = '제주특별자치도 제주시 구좌읍';
          this.beachFeatures = '투명한 물, 해양생물, 스노클링';
          this.bestTime = '오전 10시 ~ 오후 4시';
          this.videoSource = 'http://localhost:8080/walljeonglee_beach.mp4';
          break;
      }
    },
    startRealTimeUpdates() {
      // WebSocket 연결 시도
      this.connectWebSocket();
      
      // WebSocket 연결 실패 시 폴백으로 시뮬레이션 데이터 사용
      this.updateInterval = setInterval(() => {
        if (!this.stompClient || !this.stompClient.connected) {
          this.updateCrowdData();
        }
      }, 5000);
      
      // 초기 데이터 로드
      this.updateCrowdData();
    },
    updateCrowdData() {
      // 시뮬레이션 데이터 (실제로는 WebSocket으로 받음)
      this.personCount = Math.floor(Math.random() * 20) + 5;
      this.uniquePersonCount = this.personCount + Math.floor(Math.random() * 5);
      this.fallenCount = Math.floor(Math.random() * 3);
      this.densityLevel = this.getDensityLevel(this.personCount);
      this.lastUpdate = '방금 전';
      
      // 히스토리에 추가
      this.densityHistory.push({
        time: new Date().toLocaleTimeString(),
        count: this.personCount,
        uniqueCount: this.uniquePersonCount,
        fallenCount: this.fallenCount,
        density: this.densityLevel
      });
      
      // 최근 10개만 유지
      if (this.densityHistory.length > 10) {
        this.densityHistory.shift();
      }
      
      this.updateChart();
    },
    getDensityLevel(count) {
      if (count < 5) return '낮음';
      else if (count < 15) return '중간';
      else return '높음';
    },
    getDensityTextColor() {
      switch (this.densityLevel) {
        case '낮음': return 'text-success';
        case '중간': return 'text-warning';
        case '높음': return 'text-danger';
        default: return 'text-secondary';
      }
    },
    loadVideo() {
      // 비디오 로드 로직
      this.$nextTick(() => {
        if (this.$refs.videoPlayer) {
          this.$refs.videoPlayer.load();
        }
      });
    },
    onVideoLoaded() {
      console.log('비디오 로드 완료');
    },
    updateChart() {
      // 차트 업데이트 로직 (Chart.js 등 사용)
      // 여기서는 간단한 텍스트 표시
    },
    
    connectWebSocket() {
      try {
        // SockJS와 STOMP를 사용한 WebSocket 연결
        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
          console.log('WebSocket 연결됨:', frame);
          
          // 해당 해변의 혼잡도 정보 구독
          this.stompClient.subscribe(`/topic/beach-crowd/${this.beachName}`, (message) => {
            const data = JSON.parse(message.body);
            this.personCount = data.personCount;
            this.uniquePersonCount = data.uniquePersonCount || data.personCount;
            this.fallenCount = data.fallenCount || 0;
            this.densityLevel = data.densityLevel;
            this.lastUpdate = '방금 전';
            
            // 히스토리에 추가
            this.densityHistory.push({
              time: new Date().toLocaleTimeString(),
              count: this.personCount,
              uniqueCount: this.uniquePersonCount,
              fallenCount: this.fallenCount,
              density: this.densityLevel
            });
            
            // 최근 10개만 유지
            if (this.densityHistory.length > 10) {
              this.densityHistory.shift();
            }
            
            this.updateChart();
          });
        }, (error) => {
          console.error('WebSocket 연결 실패:', error);
        });
      } catch (error) {
        console.error('WebSocket 초기화 실패:', error);
      }
    }
  }
}
</script>

<style scoped>
.beach-detail-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.beach-info-section {
  background-color: white;
}

.video-container {
  position: relative;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-container video {
  border-radius: 8px;
}

.card {
  border: none;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  border-radius: 12px;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  border-radius: 12px 12px 0 0 !important;
}

.navbar-brand {
  font-weight: bold;
  font-size: 1.5rem;
}

.dropdown-menu {
  border: none;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  border-radius: 8px;
}

.dropdown-item:hover {
  background-color: #e9ecef;
}
</style>
