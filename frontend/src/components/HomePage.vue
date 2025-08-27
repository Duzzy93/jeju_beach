<template>
  <div class="home-page">
    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="container text-center">
        <h1 class="display-3 fw-bold mb-4">🏖️ 제주 해변 AI 가이드</h1>
        <p class="lead mb-4">AI 기술로 분석하는 제주 해변의 실시간 혼잡도와 맞춤형 정보</p>
        
        <!-- 로그인 상태에 따른 환영 메시지 -->
        <div v-if="isLoggedIn" class="welcome-message mb-4">
          <div class="alert alert-success d-inline-block">
            <i class="bi bi-person-check me-2"></i>
            안녕하세요, <strong>{{ username }}</strong>님! 
            <span class="badge bg-primary ms-2">{{ getRoleDisplayName() }}</span>
          </div>
        </div>
        
        <div class="hero-buttons">
          <!-- 관리자: 해변 관리, 관리자 버튼만 표시 -->
          <template v-if="isLoggedIn && isAdmin">
            <router-link to="/beach-management" class="btn btn-warning btn-lg me-3">
              🏖️ 해변 관리
            </router-link>
            <router-link to="/admin" class="btn btn-danger btn-lg me-3">
              ⚙️ 관리자
            </router-link>
            <router-link to="/chatbot" class="btn btn-outline-light btn-lg">
              🤖 AI 챗봇과 대화하기
            </router-link>
          </template>
          
          <!-- 매니저: 해변 혼잡도만 표시 -->
          <template v-else-if="isLoggedIn && isManager && !isAdmin">
            <router-link to="/beach-crowd" class="btn btn-primary btn-lg me-3">
              🏊 해변 혼잡도 보기
            </router-link>
            <router-link to="/chatbot" class="btn btn-outline-light btn-lg">
              🤖 AI 챗봇과 대화하기
            </router-link>
          </template>
          
          <!-- 일반 사용자: AI 챗봇 표시 -->
          <template v-else-if="isLoggedIn && !isManager && !isAdmin">
            <router-link to="/chatbot" class="btn btn-outline-light btn-lg">
              🤖 AI 챗봇과 대화하기
            </router-link>
          </template>
          
          <!-- 비로그인 사용자: 기본 버튼 -->
          <template v-else>
          </template>
        </div>
      </div>
    </section>

    <!-- 주요 기능 소개 -->
    <section class="features-section py-5">
      <div class="container">
        <h2 class="text-center mb-5">✨ 주요 기능</h2>
        <div class="row g-4">
          <div class="col-md-4">
            <div class="feature-card text-center">
              <div class="feature-icon mb-3">
                <i class="bi bi-camera-video display-1 text-primary"></i>
              </div>
              <h4>실시간 혼잡도 분석</h4>
              <p>AI 기술을 활용한 실시간 사람 수 분석으로 해변의 혼잡도를 정확하게 파악할 수 있습니다.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card text-center">
              <div class="feature-icon mb-3">
                <i class="bi bi-robot display-1 text-success"></i>
              </div>
              <h4>AI 챗봇 가이드</h4>
              <p>제주 해변 전문 AI 가이드와 대화하며 개인 맞춤형 해변 정보와 추천을 받아보세요.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card text-center">
              <div class="feature-icon mb-3">
                <i class="bi bi-geo-alt display-1 text-info"></i>
              </div>
              <h4>상세 해변 정보</h4>
              <p>함덕해변, 이호해변, 월정리해변의 위치, 특징, 편의시설 등 상세한 정보를 제공합니다.</p>
            </div>
          </div>
        </div>
        
        <!-- 로그인 상태에 따른 추가 기능 -->
        <div v-if="isLoggedIn" class="row g-4 mt-4">
          <div class="col-md-6">
            <div class="feature-card text-center border-success">
              <div class="feature-icon mb-3">
                <i class="bi bi-shield-check display-1 text-success"></i>
              </div>
              <h4>개인화된 서비스</h4>
              <p>로그인한 사용자만 이용할 수 있는 개인화된 해변 정보와 추천 서비스를 제공합니다.</p>
            </div>
          </div>
          <div class="col-md-6">
            <div class="feature-card text-center border-warning">
              <div class="feature-icon mb-3">
                <i class="bi bi-gear display-1 text-warning"></i>
              </div>
              <h4>관리 기능</h4>
              <p v-if="isManager">해변 정보 관리 및 업데이트가 가능합니다.</p>
              <p v-if="isAdmin">전체 시스템 관리 및 사용자 권한 관리가 가능합니다.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 해변 소개 -->
    <section class="beaches-section py-5 bg-light">
      <div class="container">
        <h2 class="text-center mb-5">🏖️ 제주 대표 해변</h2>
        <div class="row g-4">
          <div class="col-md-4">
            <div class="beach-card">
              <div class="beach-image hamduck"></div>
              <div class="beach-content">
                <h4>함덕해변</h4>
                <p>제주도 동부의 대표적인 해변으로, 맑은 바다와 백사장이 아름다운 해변입니다.</p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="beach-card">
              <div class="beach-image iho"></div>
              <div class="beach-content">
                <h4>이호해변</h4>
                <p>공항 근처에 위치해 있어 접근성이 좋고, 평화로운 분위기의 해변입니다.</p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="beach-card">
              <div class="beach-image walljeonglee"></div>
              <div class="beach-content">
                <h4>월정리해변</h4>
                <p>맑은 바다와 카페 거리로 유명하며, 다양한 편의시설을 갖춘 해변입니다.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI 챗봇 소개 -->
    <section class="chatbot-section py-5">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-lg-6">
            <h2 class="mb-4">🤖 AI 챗봇과 함께하는 제주 해변 여행</h2>
            <p class="lead mb-4">
              제주 해변에 대한 모든 궁금증을 AI 챗봇에게 물어보세요! 
              개인 취향에 맞는 해변 추천부터 실시간 혼잡도 정보까지 모든 것을 알려드립니다.
            </p>
            <ul class="chatbot-features mb-4">
              <li>✅ 24시간 언제든지 질문 가능</li>
              <li>✅ 개인 맞춤형 해변 추천</li>
              <li>✅ 실시간 혼잡도 정보 제공</li>
              <li>✅ 한국어 자연어 대화 지원</li>
            </ul>
            <template v-if="isLoggedIn">
              <router-link to="/chatbot" class="btn btn-primary btn-lg">
                🚀 AI 챗봇 시작하기
              </router-link>
            </template>
          </div>
          <div class="col-lg-6">
            <div class="chatbot-preview">
              <div class="chat-mockup">
                <div class="chat-header">
                  <span class="chat-title">🏖️ 제주 해변 AI 가이드</span>
                </div>
                <div class="chat-messages">
                  <div class="message ai-message">
                    <div class="message-content">
                      안녕하세요! 제주 해변 전문 AI 가이드입니다. 
                      어떤 해변에 대해 궁금하신가요?
                    </div>
                  </div>
                  <div class="message user-message">
                    <div class="message-content">
                      혼잡도가 낮은 해변 추천해줘
                    </div>
                  </div>
                  <div class="message ai-message">
                    <div class="message-content">
                      이호해변을 추천드립니다! 공항 근처에 위치해 있어 
                      접근성이 좋고 혼잡도가 낮아 평화롭게 즐길 수 있어요.
                    </div>
                  </div>
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
import { useAuthStore } from '../stores/auth'
import { mapState } from 'pinia'

export default {
  name: 'HomePage',
  computed: {
    ...mapState(useAuthStore, ['isLoggedIn', 'username', 'role', 'isAdmin', 'isManager'])
  },
  methods: {
    getRoleDisplayName() {
      switch (this.role) {
        case 'ADMIN':
          return '관리자';
        case 'MANAGER':
          return '매니저';
        case 'USER':
          return '사용자';
        default:
          return '사용자';
      }
    }
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 120px 0;
}

.hero-buttons .btn {
  padding: 12px 30px;
  font-weight: 600;
}

.feature-card {
  background: white;
  padding: 30px 20px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  height: 100%;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.beach-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.beach-card:hover {
  transform: translateY(-5px);
}

.beach-image {
  height: 200px;
  background-size: cover;
  background-position: center;
}

.beach-image.hamduck {
  background-image: linear-gradient(45deg, #667eea, #764ba2);
}

.beach-image.iho {
  background-image: linear-gradient(45deg, #f093fb, #f5576c);
}

.beach-image.walljeonglee {
  background-image: linear-gradient(45deg, #4facfe, #00f2fe);
}

.beach-content {
  padding: 20px;
}

.chatbot-section {
  background: #f8f9fa;
}

.chatbot-features {
  list-style: none;
  padding: 0;
}

.chatbot-features li {
  margin-bottom: 10px;
  font-size: 1.1rem;
}

.chatbot-preview {
  display: flex;
  justify-content: center;
}

.chat-mockup {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  max-width: 400px;
  width: 100%;
}

.chat-header {
  background: #667eea;
  color: white;
  padding: 15px 20px;
  text-align: center;
  font-weight: 600;
}

.chat-messages {
  padding: 20px;
  height: 300px;
  overflow-y: auto;
}

.message {
  margin-bottom: 15px;
}

.message-content {
  max-width: 80%;
  padding: 10px 15px;
  border-radius: 18px;
  word-wrap: break-word;
}

.ai-message .message-content {
  background: #f1f3f4;
  color: #333;
}

.user-message .message-content {
  background: #667eea;
  color: white;
  margin-left: auto;
}

.cta-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cta-buttons .btn {
  padding: 12px 30px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 80px 0;
  }
  
  .hero-buttons .btn {
    display: block;
    margin: 10px auto;
    width: 80%;
  }
  
  .cta-buttons .btn {
    display: block;
    margin: 10px auto;
    width: 80%;
  }
}
</style>
