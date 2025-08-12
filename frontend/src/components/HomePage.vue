<template>
  <!-- 메인 콘텐츠 -->
  <main class="flex-grow-1">
    <div class="container-fluid py-4">
      <div class="row">
        <!-- 왼쪽 패널 -->
        <div class="col-lg-8">
          <div class="card">
            <div class="card-body">
              <video
                ref="videoPlayer"
                class="w-100 rounded"
                :src="selectedVideo?.videoUrl"
                loop
                muted
                @mouseenter="playVideo"
                @mouseleave="pauseVideo"
                controls
              >
                동영상을 지원하지 않는 브라우저입니다.
              </video>

              <div class="mt-3">
                <h6 class="card-title">해변 동영상 목록</h6>
                <div class="list-group">
                  <button
                    v-for="video in videos"
                    :key="video.id"
                    :class="[
                      'list-group-item list-group-item-action',
                      { 'active': video.id === selectedVideo?.id }
                    ]"
                    @click="selectVideo(video)"
                  >
                    <i class="bi bi-play-circle me-2"></i>
                    {{ video.title }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽 패널 -->
        <div class="col-lg-4" v-if="selectedVideo">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-geo-alt text-primary me-2"></i>
                {{ selectedVideo.title }}
              </h5>
              <p class="card-text">{{ selectedVideo.description }}</p>
              
              <div class="mt-3">
                <h6>해변 정보</h6>
                <ul class="list-unstyled">
                  <li class="mb-2">
                    <i class="bi bi-water text-info me-2"></i>
                    해수욕장 등급: A급
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-thermometer-half text-warning me-2"></i>
                    수온: 22°C
                  </li>
                  <li class="mb-2">
                    <i class="bi bi-wind text-success me-2"></i>
                    바람: 남동풍 3m/s
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      videos: [],
      selectedVideo: null,
    };
  },
  mounted() {
    this.loadVideos();
  },
  methods: {
    async loadVideos() {
      try {
        const response = await fetch('http://localhost:8080/api/videos');
        const data = await response.json();
        this.videos = data;
        if (data.length > 0) this.selectedVideo = data[0];
      } catch (error) {
        console.error('동영상 로드 실패:', error);
        // 테스트용 더미 데이터
        this.videos = [
          {
            id: 1,
            title: '함덕 해변',
            videoUrl: '/data/hamduck_beach.mp4',
            description: '함덕 해변의 아름다운 풍경을 감상할 수 있습니다.'
          },
          {
            id: 2,
            title: '이호 해변',
            videoUrl: '/data/iho_beach.mp4',
            description: '이호 해변의 평화로운 모습을 확인할 수 있습니다.'
          },
          {
            id: 3,
            title: '월정리 해변',
            videoUrl: '/data/walljeonglee_beach.mp4',
            description: '월정리 해변의 장엄한 풍경을 경험할 수 있습니다.'
          }
        ];
        if (this.videos.length > 0) this.selectedVideo = this.videos[0];
      }
    },
    selectVideo(video) {
      this.selectedVideo = video;
      this.pauseVideo(); // 선택 변경 시 재생 중단
    },
    playVideo() {
      this.$refs.videoPlayer?.play();
    },
    pauseVideo() {
      this.$refs.videoPlayer?.pause();
    },
  },
};
</script>

<style scoped>
.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
  border: 1px solid rgba(0, 0, 0, 0.125);
}

.list-group-item.active {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.list-group-item:hover {
  background-color: #e9ecef;
}

video {
  max-height: 500px;
  object-fit: cover;
}
</style>
