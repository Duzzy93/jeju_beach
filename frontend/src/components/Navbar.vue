<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <router-link class="navbar-brand" to="/">
        🏖️ 제주 해변 AI 가이드
      </router-link>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <!-- 관리자: 해변 관리, CCTV 모니터링, 해변 혼잡도, AI 챗봇 표시 -->
          <template v-if="isLoggedIn && isAdmin">
            <li class="nav-item">
              <router-link class="nav-link" to="/beach-management">해변 관리</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin">CCTV 모니터링</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/beach-crowd">해변 혼잡도</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chatbot">AI 챗봇</router-link>
            </li>
          </template>
          
          <!-- 매니저: 해변 혼잡도만 표시 -->
          <template v-else-if="isLoggedIn && isManager && !isAdmin">
            <li class="nav-item">
              <router-link class="nav-link" to="/beach-crowd">해변 혼잡도</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chatbot">AI 챗봇</router-link>
            </li>
          </template>
          
          <!-- 일반 사용자: 홈, AI 챗봇만 표시 -->
          <template v-else-if="isLoggedIn && !isManager && !isAdmin">
            <li class="nav-item">
              <router-link class="nav-link" to="/">홈</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chatbot">AI 챗봇</router-link>
            </li>
          </template>
          
          <!-- 비로그인 사용자: 기본 메뉴 -->
          <template v-else>
            <li class="nav-item">
              <router-link class="nav-link" to="/">홈</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/chatbot">AI 챗봇</router-link>
            </li>
          </template>
        </ul>

        <!-- 오른쪽 사용자 메뉴 -->
        <div class="navbar-nav">
          <div v-if="!isLoggedIn" class="d-flex">
            <router-link class="btn btn-outline-light me-2" to="/login">
              <i class="bi bi-box-arrow-in-right me-1"></i>
              로그인
            </router-link>
          </div>
          
          <div v-else class="dropdown">
            <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
              <i class="bi bi-person-circle me-1"></i>
              {{ username }}
              <span :class="['badge', getRoleBadgeClass()]">{{ getRoleDisplayName() }}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><span class="dropdown-item-text text-muted">{{ email || '이메일 정보 없음' }}</span></li>
              <li><hr class="dropdown-divider"></li>
              <li v-if="isAdmin">
                <router-link class="dropdown-item" to="/ai-model-status">
                  <i class="bi bi-robot me-2"></i>
                  AI 모델 상태
                </router-link>
              </li>
              <li v-if="isAdmin">
                <hr class="dropdown-divider">
              </li>
              <li><a class="dropdown-item" href="#" @click.prevent="logout">
                <i class="bi bi-box-arrow-right me-2"></i>로그아웃
              </a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import { mapState } from 'pinia'

export default {
  name: 'Navbar',
  computed: {
    ...mapState(useAuthStore, ['isLoggedIn', 'username', 'role', 'email', 'isAdmin', 'isManager'])
  },
  mounted() {
    // 컴포넌트 마운트 시 인증 상태 업데이트
    const authStore = useAuthStore();
    authStore.updateAuthStatus();
  },
  methods: {
    logout() {
      const authStore = useAuthStore();
      authStore.clearAuth();
      
      // 홈페이지로 이동
      this.$router.push('/');
    },
    
    getRoleBadgeClass() {
      switch (this.role) {
        case 'ADMIN':
          return 'bg-danger';
        case 'MANAGER':
          return 'bg-warning text-dark';
        case 'USER':
          return 'bg-info';
        default:
          return 'bg-secondary';
      }
    },
    
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
.navbar-brand {
  font-weight: bold;
  font-size: 1.5rem;
}

.nav-link {
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #ffd700 !important;
}

.navbar-nav .nav-link.active {
  color: #ffd700 !important;
}

.dropdown-menu {
  min-width: 200px;
}

.badge {
  font-size: 0.7rem;
  margin-left: 0.5rem;
}

.dropdown-item-text {
  font-size: 0.875rem;
}

.btn-outline-light:hover {
  background-color: #ffd700;
  border-color: #ffd700;
  color: #000;
}
</style>
