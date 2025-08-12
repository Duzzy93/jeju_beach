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

    <!-- CCTV 그리드 -->
    <div class="container-fluid">
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
                loop
                muted
                @play="onVideoPlay(video.id)"
                @pause="onVideoPause(video.id)"
                @volumechange="onVolumeChange(video.id)"
              >
                동영상을 지원하지 않는 브라우저입니다.
              </video>
              
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
            </div>
            <div class="card-footer">
              <div class="row text-center">
                <div class="col-4">
                  <small class="text-muted d-block">상태</small>
                  <span class="badge bg-success">정상</span>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">해수욕장</small>
                  <span class="badge bg-primary">{{ video.title }}</span>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">화질</small>
                  <span class="badge bg-info">HD</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 통계 패널 -->
    <div class="container-fluid mt-4">
      <div class="row g-3">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <i class="bi bi-camera-video display-4"></i>
              <h4 class="mt-2">{{ videos.length }}</h4>
              <p class="mb-0">활성 CCTV</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body text-center">
              <i class="bi bi-check-circle display-4"></i>
              <h4 class="mt-2">{{ videos.length }}</h4>
              <p class="mb-0">정상 작동</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-info text-white">
            <div class="card-body text-center">
              <i class="bi bi-eye display-4"></i>
              <h4 class="mt-2">실시간</h4>
              <p class="mb-0">모니터링</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-white">
            <div class="card-body text-center">
              <i class="bi bi-clock display-4"></i>
              <h4 class="mt-2">{{ getCurrentTime() }}</h4>
              <p class="mb-0">현재 시간</p>
            </div>
          </div>
        </div>
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
      playingVideos: new Set(),
      mutedVideos: new Set(),
      currentTime: new Date()
    }
  },
  mounted() {
    this.loadVideos()
    this.startTimeUpdate()
  },
  beforeUnmount() {
    this.stopTimeUpdate()
  },
  methods: {
    async loadVideos() {
      try {
        const response = await fetch('http://localhost:8080/api/videos')
        const data = await response.json()
        this.videos = data
      } catch (error) {
        console.error('동영상 로드 실패:', error)
        // 테스트용 더미 데이터
        this.videos = [
          {
            id: 1,
            title: '함덕 해변',
            videoUrl: '/data/hamduck_beach.mp4',
            description: '함덕 해변 CCTV'
          },
          {
            id: 2,
            title: '이호 해변',
            videoUrl: '/data/iho_beach.mp4',
            description: '이호 해변 CCTV'
          },
          {
            id: 3,
            title: '월정리 해변',
            videoUrl: '/data/walljeonglee_beach.mp4',
            description: '월정리 해변 CCTV'
          }
        ]
      }
    },
    refreshVideos() {
      this.loadVideos()
    },
    togglePlayPause(videoId) {
      const video = this.$refs[`video-${videoId}`]?.[0]
      if (video) {
        if (this.isPlaying(videoId)) {
          video.pause()
        } else {
          video.play()
        }
      }
    },
    toggleMute(videoId) {
      const video = this.$refs[`video-${videoId}`]?.[0]
      if (video) {
        video.muted = !video.muted
        if (video.muted) {
          this.mutedVideos.add(videoId)
        } else {
          this.mutedVideos.delete(videoId)
        }
      }
    },
    isPlaying(videoId) {
      return this.playingVideos.has(videoId)
    },
    isMuted(videoId) {
      return this.mutedVideos.has(videoId)
    },
    onVideoPlay(videoId) {
      this.playingVideos.add(videoId)
    },
    onVideoPause(videoId) {
      this.playingVideos.delete(videoId)
    },
    onVolumeChange(videoId) {
      const video = this.$refs[`video-${videoId}`]?.[0]
      if (video && video.muted) {
        this.mutedVideos.add(videoId)
      } else {
        this.mutedVideos.delete(videoId)
      }
    },
    getCurrentTime() {
      return this.currentTime.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    startTimeUpdate() {
      this.timeInterval = setInterval(() => {
        this.currentTime = new Date()
      }, 1000)
    },
    stopTimeUpdate() {
      if (this.timeInterval) {
        clearInterval(this.timeInterval)
      }
    },
    toggleFullscreen() {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen()
      } else {
        document.exitFullscreen()
      }
    }
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.cctv-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 2px solid #dee2e6;
}

.cctv-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  right: 10px;
  z-index: 10;
}

.overlay-info {
  display: flex;
  gap: 8px;
}

.cctv-controls .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.page-header {
  border-bottom: 1px solid #dee2e6;
}

.card-header {
  border-bottom: none;
}

.card-footer {
  background-color: #f8f9fa;
  border-top: 1px solid #dee2e6;
}

.badge {
  font-size: 0.75rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .cctv-video {
    height: 150px;
  }
  
  .page-header .btn {
    font-size: 0.875rem;
    padding: 0.375rem 0.75rem;
  }
}
</style>
