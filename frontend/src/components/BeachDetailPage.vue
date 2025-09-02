<template>
  <div class="beach-detail-page">

    <!-- 해변 정보 섹션 -->
    <section class="beach-info-section py-5">
      <div class="container">
        <div class="row">
          <div class="col-lg-8">
            <h1 class="display-5 fw-bold mb-4">{{ beachData?.name || beachDisplayName }}</h1>
            <p class="lead mb-4">{{ beachData?.description || beachDescription }}</p>
            
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
            <div class="card mb-4">
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
            <!-- 해변 기본 정보 카드 -->
            <div class="card mb-4">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-geo-alt me-2"></i>
                  기본 정보
                </h5>
              </div>
              <div class="card-body">
                <ul class="list-unstyled">
                  <li class="mb-2">
                    <i class="bi bi-map me-2 text-primary"></i>
                    <strong>위치:</strong> {{ beachData?.region || beachLocation }}
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-geo-alt me-2 text-success"></i>
                    <strong>좌표:</strong> {{ beachData?.latitude ? `${beachData.latitude}, ${beachData.longitude}` : '좌표 정보가 없습니다.' }}
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-calendar me-2 text-info"></i>
                    <strong>등록일:</strong> {{ beachData?.createdAt ? new Date(beachData.createdAt).toLocaleDateString('ko-KR') : '등록일 정보가 없습니다.' }}
                  </li>
                </ul>
              </div>
            </div>

            <!-- 해변 상세 정보 -->
            <div class="card mb-4">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-info-circle me-2"></i>
                  상세 정보
                </h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-6">
                    <small class="text-muted d-block">해변 ID</small>
                    <strong>{{ beachData?.id || 'N/A' }}</strong>
                  </div>
                  <div class="col-6">
                    <small class="text-muted d-block">상태</small>
                    <span class="badge bg-success">{{ beachData?.status || '활성' }}</span>
                  </div>
                </div>
                <hr>
                <div class="row">
                  <div class="col-6">
                    <small class="text-muted d-block">생성자</small>
                    <strong>{{ beachData?.createdBy || 'N/A' }}</strong>
                  </div>
                  <div class="col-6">
                    <small class="text-muted d-block">수정일</small>
                    <strong>{{ beachData?.updatedAt ? new Date(beachData.updatedAt).toLocaleDateString('ko-KR') : 'N/A' }}</strong>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 해변 위치 정보 -->
            <div v-if="beachData?.latitude && beachData?.longitude" class="card">
              <div class="card-header">
                <h5 class="mb-0">
                  <i class="bi bi-map me-2"></i>
                  해변 위치
                </h5>
              </div>
              <div class="card-body">
                <div class="text-center">
                  <div class="mb-3">
                    <i class="bi bi-geo-alt display-4 text-primary"></i>
                  </div>
                  <p class="mb-2">
                    <strong>위도:</strong> {{ beachData.latitude }}°N
                  </p>
                  <p class="mb-2">
                    <strong>경도:</strong> {{ beachData.longitude }}°E
                  </p>
                  <p class="mb-0">
                    <small class="text-muted">
                      <i class="bi bi-info-circle me-1"></i>
                      정확한 GPS 좌표로 해변을 찾을 수 있습니다.
                    </small>
                  </p>
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
  name: 'BeachDetailPage',
  data() {
    return {
      beachName: '',
      beachDisplayName: '',
      beachDescription: '',
      beachLocation: '',
      personCount: 0,
      uniquePersonCount: 0,
      fallenCount: 0,
      densityLevel: '낮음',
      lastUpdate: '방금 전',
      videoSource: '',
      densityHistory: [],
      beachData: null, // 해변 정보를 저장할 데이터
      userRole: 'GUEST', // 사용자 역할
      hasAccess: false, // 해변 접근 권한
      detectionInterval: null, // 탐지 데이터 폴링을 위한 인터벌
      stompClient: null // WebSocket 클라이언트
    }
  },
  mounted() {
    this.initializeBeachData();
    this.startRealTimeUpdates();
    this.loadVideo();
    this.checkUserAccess(); // 페이지 로드 시 사용자 권한 확인
  },
  watch: {
    '$route.params.beachName'(newVal) {
      // 파라미터 변경 시 데이터 재초기화
      this.initializeBeachData();
      this.loadVideo();
      this.startRealTimeUpdates();
      this.checkUserAccess(); // 해변 이름 변경 시 권한 다시 확인
    }
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.close();
    }
    if (this.detectionInterval) {
      clearInterval(this.detectionInterval);
    }
  },
  methods: {
    async initializeBeachData() {
      const beachName = this.$route.params.beachName;
      this.beachName = beachName;
      
      try {
        // API에서 해변 정보 가져오기 (더 정확한 검색)
        console.log('해변 검색 시작:', beachName);
        const response = await fetch(`${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/api/beaches/search?name=${encodeURIComponent(beachName)}`);
        console.log('검색 API 응답 상태:', response.status);
        
        if (response.ok) {
          const beaches = await response.json();
          console.log('검색 결과:', beaches);
          
          if (beaches.length > 0) {
            this.beachData = beaches[0];
            
            // 기본 정보 설정 (fallback용)
            this.beachDisplayName = this.beachData.name;
            this.beachDescription = this.beachData.description || '해변 설명이 없습니다.';
            this.beachLocation = this.beachData.region || '위치 정보가 없습니다.';
            
            // 동영상 경로 설정
            if (this.beachData.videoPath) {
              this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}${this.beachData.videoPath}`;
            } else {
              // 동영상 경로가 없는 경우 기본 경로 사용
              this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/videos/${beachName}_beach.mp4`;
            }
            
            console.log('해변 정보 로드 성공:', this.beachData);
            console.log('해변 상세 정보:', {
              id: this.beachData.id,
              name: this.beachData.name,
              region: this.beachData.region,
              description: this.beachData.description,
              latitude: this.beachData.latitude,
              longitude: this.beachData.longitude,
              videoPath: this.beachData.videoPath,
              status: this.beachData.status,
              createdBy: this.beachData.createdBy,
              createdAt: this.beachData.createdAt,
              updatedAt: this.beachData.updatedAt
            });
          } else {
            // API에서 찾을 수 없는 경우 fallback으로 모든 해변 조회
            console.log('검색 결과가 없어 모든 해변을 조회합니다.');
            await this.fetchAllBeachesAsFallback(beachName);
          }
        } else {
          // API 오류 시 fallback으로 모든 해변 조회
          console.log('검색 API 오류로 모든 해변을 조회합니다.');
          await this.fetchAllBeachesAsFallback(beachName);
        }
      } catch (error) {
        console.error('해변 정보 로드 실패:', error);
        // 오류 시 오류 처리
        this.handleAPIError(beachName);
      }
    },
    
    async fetchAllBeachesAsFallback(beachName) {
      try {
        console.log('fallback: 모든 해변 정보를 가져오는 중...');
        const fallbackResponse = await fetch(`${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/api/beaches`);
        
        if (fallbackResponse.ok) {
          const allBeaches = await fallbackResponse.json();
          console.log('fallback: 모든 해변 데이터:', allBeaches);
          
          // 해변 키워드로 매핑하여 찾기
          const mappedName = this.mapBeachKeyword(beachName);
          const foundBeach = allBeaches.find(beach => 
            beach.name.includes(mappedName) || 
            beach.name.toLowerCase().includes(beachName.toLowerCase())
          );
          
          if (foundBeach) {
            console.log('fallback: 해변을 찾았습니다:', foundBeach);
            this.beachData = foundBeach;
            this.beachDisplayName = foundBeach.name;
            this.beachDescription = foundBeach.description || '해변 설명이 없습니다.';
            this.beachLocation = foundBeach.region || '위치 정보가 없습니다.';
            
            if (foundBeach.videoPath) {
              this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}${foundBeach.videoPath}`;
            } else {
              this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/videos/${beachName}_beach.mp4`;
            }
          } else {
            console.log('fallback: 해변을 찾을 수 없어 기본 정보를 사용합니다.');
            this.handleBeachNotFound(beachName);
          }
        } else {
          console.error('fallback API 실패:', fallbackResponse.status);
          this.handleBeachNotFound(beachName);
        }
      } catch (error) {
        console.error('fallback 실행 중 오류:', error);
        this.handleBeachNotFound(beachName);
      }
    },
    
    mapBeachKeyword(beachName) {
      switch (beachName.toLowerCase()) {
        case 'hamduck':
          return '함덕해변';
        case 'iho':
          return '이호테우해변';
        case 'walljeonglee':
          return '월정리해변';
        default:
          return beachName;
      }
    },
    
    handleBeachNotFound(beachName) {
      console.error(`해변을 찾을 수 없습니다: ${beachName}`);
      this.beachDisplayName = '해변 정보 없음';
      this.beachDescription = '해변 정보를 찾을 수 없습니다.';
      this.beachLocation = '위치 정보 없음';
      this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/videos/${beachName}_beach.mp4`;
      
      // 사용자에게 알림
      this.$nextTick(() => {
        if (this.$router.currentRoute.value.name === 'BeachDetail') {
          alert('해당 해변 정보를 찾을 수 없습니다. 관리자에게 문의하세요.');
        }
      });
    },
    
    handleAPIError(beachName) {
      console.error(`API 오류 발생: ${beachName}`);
      this.beachDisplayName = '오류 발생';
      this.beachDescription = '해변 정보를 불러오는 중 오류가 발생했습니다.';
      this.beachLocation = '오류';
      this.videoSource = `${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/videos/${beachName}_beach.mp4`;
      
      // 사용자에게 알림
      this.$nextTick(() => {
        if (this.$router.currentRoute.value.name === 'BeachDetail') {
          alert('해변 정보를 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        }
      });
    },

    startRealTimeUpdates() {
      // WebSocket 연결 시도
      this.connectWebSocket();
      
      // 실시간 탐지 데이터 폴링 시작 (폴백용)
      this.startDetectionPolling();
      
      // 초기 데이터 로드
      this.updateCrowdData();
    },

    startDetectionPolling() {
      // 5초마다 최신 탐지 데이터 폴링 (실시간 갱신)
      this.detectionInterval = setInterval(() => {
        this.fetchLatestDetection();
      }, 5000);
    },

    connectWebSocket() {
      try {
        // WebSocket 연결 (백엔드에서 WebSocket 지원 시)
        const socket = new WebSocket(process.env.NODE_ENV === 'production' 
          ? 'ws://15.165.30.16:8080/ws/detections' 
          : 'ws://localhost:8080/ws/detections');
        
        socket.onopen = () => {
          console.log('WebSocket 연결 성공');
          // 특정 해변의 탐지 데이터 구독
          const subscribeMessage = {
            type: 'SUBSCRIBE',
            beachName: this.beachName
          };
          socket.send(JSON.stringify(subscribeMessage));
        };
        
        socket.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data);
            if (data.type === 'DETECTION_UPDATE' && data.beachName === this.beachName) {
              // 실시간 탐지 데이터 업데이트
              this.updateFromDetectionData(data.detection);
            }
          } catch (error) {
            console.error('WebSocket 메시지 파싱 오류:', error);
          }
        };
        
        socket.onerror = (error) => {
          console.warn('WebSocket 연결 오류:', error);
          // WebSocket 실패 시 폴링 방식으로 폴백
        };
        
        socket.onclose = () => {
          console.log('WebSocket 연결 종료');
          // 연결이 끊어지면 폴링 방식으로 폴백
        };
        
        this.stompClient = socket;
      } catch (error) {
        console.warn('WebSocket 연결 실패, 폴링 방식 사용:', error);
        // WebSocket 지원이 안 되는 경우 폴링 방식으로 폴백
      }
    },

    async fetchLatestDetection() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          return; // 게스트는 탐지 데이터를 가져올 수 없음
        }

        // 현재 해변의 최신 탐지 데이터 조회
        const beachName = this.beachName;
        const response = await fetch(`${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/api/detections/beach/${beachName}/latest`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.ok) {
          const data = await response.json();
          this.updateFromDetectionData(data);
        } else if (response.status === 204) {
          // 데이터가 없는 경우 로그만 출력
          console.log('해변별 탐지 데이터가 없습니다.');
        }
      } catch (error) {
        console.error('최신 탐지 데이터 페칭 실패:', error);
      }
    },

    updateFromDetectionData(data) {
      if (data && data.personCount !== undefined) {
        this.personCount = data.personCount;
      }
      if (data && data.uniquePersonCount !== undefined) {
        this.uniquePersonCount = data.uniquePersonCount;
      } else {
        // uniquePersonCount가 없으면 personCount 기반으로 계산
        this.uniquePersonCount = this.personCount + Math.floor(Math.random() * 3);
      }
      if (data && data.fallenCount !== undefined) {
        this.fallenCount = data.fallenCount;
      }
      
      // densityLevel 계산 (데이터에 없으면 personCount 기반으로)
      if (data && data.densityLevel !== undefined) {
        this.densityLevel = data.densityLevel;
      } else {
        this.densityLevel = this.getDensityLevel(this.personCount);
      }
      
      // lastUpdate 설정
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

    updateCrowdData() {
      // 실제 탐지 데이터로 업데이트 (WebSocket 또는 API 호출)
      this.fetchLatestDetection();
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

    async checkUserAccess() {
      try {
        // 로컬 스토리지에서 사용자 정보 확인
        const token = localStorage.getItem('token');
        if (!token) {
          this.userRole = 'GUEST';
          this.hasAccess = true; // 게스트는 모든 해변 조회 가능
          return;
        }

        // 사용자 정보 가져오기 (간단한 구현)
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        this.userRole = userInfo.role || 'USER';
        
        if (this.userRole === 'ADMIN') {
          this.hasAccess = true; // ADMIN은 모든 해변 접근 가능
        } else if (this.userRole === 'MANAGER') {
          // MANAGER는 할당된 해변만 접근 가능
          await this.checkManagerBeachAccess();
        } else {
          this.hasAccess = true; // USER는 모든 해변 조회 가능
        }
      } catch (error) {
        console.error('사용자 권한 확인 실패:', error);
        this.hasAccess = true; // 오류 시 기본적으로 접근 허용
      }
    },

    async checkManagerBeachAccess() {
      try {
        const response = await fetch(`${process.env.NODE_ENV === 'production' ? 'http://15.165.30.16:8080' : 'http://localhost:8080'}/api/beaches/my-beaches`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (response.ok) {
          const myBeaches = await response.json();
          const currentBeachName = this.$route.params.beachName;
          
          // 현재 해변에 대한 접근 권한 확인
          this.hasAccess = myBeaches.some(beach => {
            const beachKey = this.getBeachKey(beach.name);
            return beachKey === currentBeachName;
          });
          
          if (!this.hasAccess) {
            alert('해당 해변에 대한 관리 권한이 없습니다.');
            this.$router.push('/');
          }
        }
      } catch (error) {
        console.error('매니저 해변 접근 권한 확인 실패:', error);
        this.hasAccess = true; // 오류 시 기본적으로 접근 허용
      }
    },

    getBeachKey(beachName) {
      // 해변 이름을 URL 경로에 맞는 키로 변환
      // 데이터베이스의 해변 이름을 기반으로 동적 처리
      return beachName.toLowerCase().replace(/[^a-z0-9]/g, '_');
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
