<template>
  <div class="admin-page">
    <!-- 페이지 헤더 -->
    <div class="page-header bg-light py-4 mb-4">
      <div class="container">
        <div class="row align-items-center">
          <div class="col">
            <h2 class="mb-0">
              <i class="bi bi-camera-video text-primary me-2"></i>
              해변 CCTV 모니터링
            </h2>
            <p class="text-muted mb-0">실시간 해변 상황을 모니터링할 수 있습니다.</p>
          </div>
          <div class="col-auto">
            <div class="d-flex gap-2">
              <button class="btn btn-outline-primary" @click="refreshVideos">
                <i class="bi bi-arrow-clockwise me-1"></i>
                새로고침
              </button>
              <button class="btn btn-success" @click="toggleFullscreen">
                <i class="bi bi-fullscreen me-1"></i>
                전체화면
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="container text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">로딩 중...</span>
      </div>
      <p class="mt-3 text-muted">해변 정보를 불러오는 중...</p>
    </div>

    <!-- 오류 메시지 -->
    <div v-else-if="error" class="container py-5">
      <div class="alert alert-warning" role="alert">
        <i class="bi bi-exclamation-triangle me-2"></i>
        <strong>경고:</strong> {{ error }}
        <button class="btn btn-sm btn-outline-warning ms-3" @click="loadBeachVideos">
          <i class="bi bi-arrow-clockwise me-1"></i>
          다시 시도
        </button>
      </div>
    </div>

    <!-- CCTV 그리드 -->
    <div v-else class="container-fluid">
      <div class="row g-3">
        <div 
          v-for="video in videos" 
          :key="video.id" 
          class="col-xl-3 col-lg-4 col-md-6"
        >
          <div class="card cctv-card h-100">
            <div class="card-header bg-dark text-light d-flex justify-content-between align-items-center">
              <h6 class="mb-0">
                <i class="bi bi-geo-alt me-2"></i>
                {{ video.title }}
              </h6>
              <div class="cctv-controls">
                <button 
                  class="btn btn-sm btn-outline-light me-1"
                  @click="togglePlayPause(video.id)"
                  :title="isPlaying(video.id) ? '일시정지' : '재생'"
                >
                  <i :class="isPlaying(video.id) ? 'bi-pause' : 'bi-play'"></i>
                </button>
                <button 
                  class="btn btn-sm btn-outline-light"
                  @click="toggleMute(video.id)"
                  :title="isMuted(video.id) ? '음소거 해제' : '음소거'"
                >
                  <i :class="isMuted(video.id) ? 'bi-volume-mute' : 'bi-volume-up'"></i>
                </button>
              </div>
            </div>
            <div class="card-body p-0 position-relative">
              <video
                :ref="`video-${video.id}`"
                :id="`video-${video.id}`"
                class="cctv-video w-100"
                :src="video.videoUrl"
                autoplay
                loop
                muted
                @play="onVideoPlay(video.id)"
                @pause="onVideoPause(video.id)"
                @volumechange="onVolumeChange(video.id)"
                @loadstart="onVideoLoadStart(video.id)"
                @loadeddata="onVideoLoadedData(video.id)"
                @error="onVideoError(video.id, $event)"
                @canplay="onVideoCanPlay(video.id)"
              >
                동영상을 지원하지 않는 브라우저입니다.
              </video>
              
              <!-- 동영상 로드 실패 시 대체 표시 -->
              <div v-if="videoLoadError[video.id]" class="video-fallback d-flex align-items-center justify-content-center h-100">
                <div class="text-center text-muted">
                  <i class="bi bi-exclamation-triangle display-4"></i>
                  <p class="mt-2 mb-0">동영상 로드 실패</p>
                  <small>{{ videoLoadError[video.id] }}</small>
                </div>
              </div>
              
              <!-- 동영상 오버레이 정보 -->
              <div class="video-overlay">
                <div class="overlay-info">
                  <span class="badge bg-success me-2">
                    <i class="bi bi-circle-fill me-1"></i>
                    LIVE
                  </span>
                  <span class="badge bg-info">
                    <i class="bi bi-clock me-1"></i>
                    {{ getCurrentTime() }}
                  </span>
                </div>
              </div>
              
              <!-- 로딩 인디케이터 -->
              <div v-if="videoLoading[video.id]" class="loading-overlay d-flex align-items-center justify-content-center">
                <div class="spinner-border text-light" role="status">
                  <span class="visually-hidden">로딩 중...</span>
                </div>
              </div>
            </div>
            
            <!-- 동영상 정보 -->
            <div class="card-footer bg-light">
              <div class="row text-center">
                <div class="col-4">
                  <small class="text-muted d-block">상태</small>
                  <span :class="getStatusBadgeClass(video.id)">
                    {{ getStatusText(video.id) }}
                  </span>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">지역</small>
                  <small class="fw-bold">{{ video.region || 'N/A' }}</small>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">해상도</small>
                  <small class="fw-bold">{{ video.resolution || 'N/A' }}</small>
                </div>
              </div>
              <div v-if="video.description" class="mt-2">
                <small class="text-muted">{{ video.description }}</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 전체화면 모달 -->
    <div v-if="fullscreenVideo" class="fullscreen-modal" @click="closeFullscreen">
      <div class="fullscreen-content" @click.stop>
        <div class="fullscreen-header d-flex justify-content-between align-items-center mb-3">
          <h4 class="text-white mb-0">
            <i class="bi bi-camera-video me-2"></i>
            {{ fullscreenVideo.title }}
          </h4>
          <button class="btn btn-outline-light" @click="closeFullscreen">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        <video
          :ref="`fullscreen-video-${fullscreenVideo.id}`"
          class="fullscreen-video"
          :src="fullscreenVideo.videoUrl"
          controls
          autoplay
          loop
        >
          동영상을 지원하지 않는 브라우저입니다.
        </video>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminPage',
  data() {
    return {
      videos: [],
      videoStates: {},
      videoLoadError: {},
      videoLoading: {},
      fullscreenVideo: null,
      currentTime: new Date(),
      timeInterval: null,
      loading: true,
      error: null
    }
  },
  mounted() {
    this.loadBeachVideos();
    this.startTimeUpdate();
  },
  beforeUnmount() {
    this.stopTimeUpdate();
    this.stopAllVideos();
  },
  methods: {
    async loadBeachVideos() {
      try {
        this.loading = true;
        this.error = null;
        
        // 백엔드에서 해변 정보 가져오기
        const response = await fetch('/api/beaches', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error('해변 정보를 가져올 수 없습니다.');
        }
        
        const beaches = await response.json();
        
        // 해변 정보를 비디오 형식으로 변환
        this.videos = beaches.map(beach => ({
          id: beach.id,
          title: beach.name,
          videoUrl: beach.videoPath || `/videos/${beach.name.toLowerCase().replace(/[^a-z0-9]/g, '_')}_beach.mp4`,
          resolution: '1920x1080', // 기본값
          fps: '30', // 기본값
          region: beach.region,
          description: beach.description
        }));
        
        // 비디오 상태 초기화
        this.initializeVideos();
        
      } catch (error) {
        console.error('해변 정보 로드 오류:', error);
        this.error = error.message;
        
        // 오류 발생 시 기본 해변 정보 사용
        this.videos = [
          {
            id: 1,
            title: '함덕해변',
            videoUrl: '/videos/hamduck_beach.mp4',
            resolution: '1920x1080',
            fps: '30'
          },
          {
            id: 2,
            title: '이호테우해변',
            videoUrl: '/videos/iho_beach.mp4',
            resolution: '1920x1080',
            fps: '30'
          },
          {
            id: 3,
            title: '월정리해변',
            videoUrl: '/videos/walljeonglee_beach.mp4',
            resolution: '1920x1080',
            fps: '30'
          }
        ];
        this.initializeVideos();
      } finally {
        this.loading = false;
      }
    },
    
    initializeVideos() {
      this.videos.forEach(video => {
        this.videoStates[video.id] = {
          isPlaying: false,
          isMuted: true,
          isLoading: true,
          hasError: false
        };
        this.videoLoadError[video.id] = null;
        this.videoLoading[video.id] = true;
      });
    },
    
    startTimeUpdate() {
      this.timeInterval = setInterval(() => {
        this.currentTime = new Date();
      }, 1000);
    },
    
    stopTimeUpdate() {
      if (this.timeInterval) {
        clearInterval(this.timeInterval);
      }
    },
    
    getCurrentTime() {
      return this.currentTime.toLocaleTimeString('ko-KR');
    },
    
    refreshVideos() {
      // 해변 정보를 다시 로드하고 비디오 새로고침
      this.loadBeachVideos();
    },
    
    togglePlayPause(videoId) {
      const videoElement = this.$refs[`video-${videoId}`]?.[0];
      if (videoElement) {
        if (this.videoStates[videoId].isPlaying) {
          videoElement.pause();
        } else {
          videoElement.play();
        }
      }
    },
    
    toggleMute(videoId) {
      const videoElement = this.$refs[`video-${videoId}`]?.[0];
      if (videoElement) {
        videoElement.muted = !videoElement.muted;
        this.videoStates[videoId].isMuted = videoElement.muted;
      }
    },
    
    isPlaying(videoId) {
      return this.videoStates[videoId]?.isPlaying || false;
    },
    
    isMuted(videoId) {
      return this.videoStates[videoId]?.isMuted !== false;
    },
    
    onVideoPlay(videoId) {
      this.videoStates[videoId].isPlaying = true;
    },
    
    onVideoPause(videoId) {
      this.videoStates[videoId].isPlaying = false;
    },
    
    onVolumeChange(videoId) {
      const videoElement = this.$refs[`video-${videoId}`]?.[0];
      if (videoElement) {
        this.videoStates[videoId].isMuted = videoElement.muted;
      }
    },
    
    onVideoLoadStart(videoId) {
      this.videoLoading[videoId] = true;
      this.videoLoadError[videoId] = null;
    },
    
    onVideoLoadedData(videoId) {
      this.videoLoading[videoId] = false;
      this.videoStates[videoId].isLoading = false;
    },
    
    onVideoError(videoId, event) {
      this.videoLoading[videoId] = false;
      this.videoStates[videoId].hasError = true;
      this.videoLoadError[videoId] = '동영상을 로드할 수 없습니다.';
      console.error(`Video ${videoId} error:`, event);
    },
    
    onVideoCanPlay(videoId) {
      this.videoLoading[videoId] = false;
      this.videoStates[videoId].isLoading = false;
    },
    
    getStatusBadgeClass(videoId) {
      const state = this.videoStates[videoId];
      if (state?.hasError) return 'badge bg-danger';
      if (state?.isLoading) return 'badge bg-warning';
      if (state?.isPlaying) return 'badge bg-success';
      return 'badge bg-secondary';
    },
    
    getStatusText(videoId) {
      const state = this.videoStates[videoId];
      if (state?.hasError) return '오류';
      if (state?.isLoading) return '로딩';
      if (state?.isPlaying) return '재생중';
      return '정지';
    },
    
    toggleFullscreen() {
      if (this.fullscreenVideo) {
        this.closeFullscreen();
      } else {
        // 첫 번째 비디오를 전체화면으로 표시
        this.fullscreenVideo = this.videos[0];
        this.$nextTick(() => {
          const fullscreenElement = this.$refs[`fullscreen-video-${this.fullscreenVideo.id}`]?.[0];
          if (fullscreenElement) {
            fullscreenElement.play();
          }
        });
      }
    },
    
    closeFullscreen() {
      if (this.fullscreenVideo) {
        const fullscreenElement = this.$refs[`fullscreen-video-${this.fullscreenVideo.id}`]?.[0];
        if (fullscreenElement) {
          fullscreenElement.pause();
        }
        this.fullscreenVideo = null;
      }
    },
    
    stopAllVideos() {
      this.videos.forEach(video => {
        const videoElement = this.$refs[`video-${video.id}`]?.[0];
        if (videoElement) {
          videoElement.pause();
        }
      });
    }
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  border-bottom: 1px solid #dee2e6;
}

.cctv-card {
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
  transition: box-shadow 0.15s ease-in-out;
}

.cctv-card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.cctv-video {
  height: 200px;
  object-fit: cover;
  background-color: #000;
}

.video-overlay {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.overlay-info .badge {
  font-size: 0.75rem;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 20;
}

.video-fallback {
  height: 200px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
}

.fullscreen-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.fullscreen-content {
  width: 100%;
  max-width: 1200px;
  max-height: 100%;
}

.fullscreen-video {
  width: 100%;
  height: 70vh;
  object-fit: contain;
}

.cctv-controls .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

@media (max-width: 768px) {
  .cctv-video {
    height: 150px;
  }
  
  .video-fallback {
    height: 150px;
  }
  
  .fullscreen-video {
    height: 50vh;
  }
}
</style>
