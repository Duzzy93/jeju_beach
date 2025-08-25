import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
    email: localStorage.getItem('email') || ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    isManager: (state) => state.role === 'MANAGER' || state.role === 'ADMIN',
    userRole: (state) => state.role
  },

  actions: {
    setAuth(authData) {
      this.token = authData.token
      this.username = authData.username
      this.role = authData.role
      this.email = authData.email || 'user@example.com'
      
      // localStorage에 저장
      localStorage.setItem('token', authData.token)
      localStorage.setItem('username', authData.username)
      localStorage.setItem('role', authData.role)
      localStorage.setItem('email', authData.email || 'user@example.com')
    },

    clearAuth() {
      this.token = null
      this.username = ''
      this.role = ''
      this.email = ''
      
      // localStorage에서 제거
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('email')
    },

    updateAuthStatus() {
      // localStorage에서 최신 상태 가져오기
      this.token = localStorage.getItem('token')
      this.username = localStorage.getItem('username')
      this.role = localStorage.getItem('role')
      this.email = localStorage.getItem('email')
    }
  }
})
