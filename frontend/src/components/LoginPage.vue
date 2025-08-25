<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="text-center mb-4">제주 해변 로그인</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label for="username" class="form-label">사용자명</label>
          <input
            type="text"
            class="form-control"
            id="username"
            v-model="loginForm.username"
            required
            placeholder="사용자명을 입력하세요"
          />
        </div>
        
        <div class="mb-3">
          <label for="password" class="form-label">비밀번호</label>
          <input
            type="password"
            class="form-control"
            id="password"
            v-model="loginForm.password"
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        
        <div class="d-grid gap-2">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '로그인 중...' : '로그인' }}
          </button>
        </div>
      </form>
      
      <div class="text-center mt-3">
        <p>계정이 없으신가요? <a href="#" @click.prevent="showRegister = true">회원가입</a></p>
      </div>
      
      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']">
        {{ message }}
      </div>
    </div>
    
    <!-- 회원가입 모달 -->
    <div v-if="showRegister" class="modal-overlay" @click="showRegister = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title">회원가입</h5>
          <button type="button" class="btn-close" @click="showRegister = false"></button>
        </div>
        
        <form @submit.prevent="handleRegister">
          <div class="modal-body">
            <div class="mb-3">
              <label for="reg-username" class="form-label">사용자명</label>
              <input
                type="text"
                class="form-control"
                id="reg-username"
                v-model="registerForm.username"
                required
                minlength="3"
                maxlength="50"
                placeholder="3자 이상 50자 이하"
              />
            </div>
            
            <div class="mb-3">
              <label for="reg-email" class="form-label">이메일</label>
              <input
                type="email"
                class="form-control"
                id="reg-email"
                v-model="registerForm.email"
                required
                placeholder="이메일을 입력하세요"
              />
            </div>
            
            <div class="mb-3">
              <label for="reg-password" class="form-label">비밀번호</label>
              <input
                type="password"
                class="form-control"
                id="reg-password"
                v-model="registerForm.password"
                required
                minlength="6"
                placeholder="6자 이상"
              />
            </div>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showRegister = false">취소</button>
            <button type="submit" class="btn btn-primary" :disabled="registerLoading">
              {{ registerLoading ? '가입 중...' : '회원가입' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'

export default {
  name: 'LoginPage',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        email: '',
        password: ''
      },
      loading: false,
      registerLoading: false,
      showRegister: false,
      message: '',
      messageType: 'success'
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      this.message = '';
      
      try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.loginForm)
        });
        
        const data = await response.json();
        
        if (response.ok && data.token) {
          // Pinia 스토어에 인증 정보 저장
          const authStore = useAuthStore();
          authStore.setAuth({
            token: data.token,
            username: data.username,
            role: data.role,
            email: data.email || 'user@example.com'
          });
          
          this.message = data.message || '로그인이 완료되었습니다';
          this.messageType = 'success';
          
          // 로그인 성공 후 홈페이지로 이동
          setTimeout(() => {
            this.$router.push('/');
          }, 1500);
        } else {
          // 토큰이 없거나 응답이 실패인 경우
          this.message = data.message || '사용자명 또는 비밀번호가 올바르지 않습니다';
          this.messageType = 'danger';
          
          // 폼 초기화
          this.loginForm.password = '';
          
          // 로그인 실패 시 현재 페이지에 머물러 있음
          console.log('로그인 실패 - 페이지 이동 차단됨');
        }
      } catch (error) {
        console.error('로그인 에러:', error);
        this.message = '서버 연결에 실패했습니다. 다시 시도해주세요.';
        this.messageType = 'danger';
        
        // 에러 발생 시에도 현재 페이지에 머물러 있음
        console.log('로그인 에러 - 페이지 이동 차단됨');
      } finally {
        this.loading = false;
      }
    },
    
    async handleRegister() {
      this.registerLoading = true;
      this.message = '';
      
      try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.registerForm)
        });
        
        const data = await response.json();
        
        if (response.ok) {
          this.message = data.message;
          this.messageType = 'success';
          
          // 회원가입 성공 후 로그인 폼으로 이동
          setTimeout(() => {
            this.showRegister = false;
            this.registerForm = { username: '', email: '', password: '' };
          }, 1000);
        } else {
          this.message = data.message || '회원가입에 실패했습니다';
          this.messageType = 'danger';
        }
      } catch (error) {
        this.message = '서버 연결에 실패했습니다';
        this.messageType = 'danger';
      } finally {
        this.registerLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 1rem;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.btn-close:hover {
  opacity: 0.7;
}
</style>
