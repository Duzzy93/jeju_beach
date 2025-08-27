import { defineStore } from 'pinia'
import { authApi } from '../api/authApi'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
    email: localStorage.getItem('email') || '',
    loading: false,
    error: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    isManager: (state) => state.role === 'MANAGER' || state.role === 'ADMIN',
    userRole: (state) => state.role
  },

  actions: {
    // 로그인 액션
    async login(credentials) {
      this.loading = true
      this.error = null
      
      try {
        const authData = await authApi.login(credentials)
        this.setAuth(authData)
        return { success: true, data: authData }
      } catch (error) {
        this.error = error.message || '로그인에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 회원가입 액션
    async register(userData) {
      this.loading = true
      this.error = null
      
      try {
        const result = await authApi.register(userData)
        return { success: true, data: result }
      } catch (error) {
        this.error = error.message || '회원가입에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 사용자 프로필 조회
    async fetchProfile() {
      try {
        const profile = await authApi.getProfile()
        this.updateProfile(profile)
        return { success: true, data: profile }
      } catch (error) {
        this.error = error.message || '프로필 조회에 실패했습니다.'
        return { success: false, error: this.error }
      }
    },

    // 사용자 프로필 로드 (라우터 가드용)
    async loadUserProfile() {
      try {
        const profile = await authApi.getProfile()
        this.updateProfile(profile)
        return { success: true, data: profile }
      } catch (error) {
        throw error
      }
    },

    // 인증 정보 설정
    setAuth(authData) {
      this.token = authData.token
      this.username = authData.username
      this.role = authData.role
      this.email = authData.email || ''
      
      // localStorage에 저장
      localStorage.setItem('token', authData.token)
      localStorage.setItem('username', authData.username)
      localStorage.setItem('role', authData.role)
      localStorage.setItem('email', authData.email || '')
    },

    // 프로필 정보 업데이트
    updateProfile(profile) {
      this.username = profile.username || this.username
      this.role = profile.role || this.role
      this.email = profile.email || this.email
      
      // localStorage 업데이트
      localStorage.setItem('username', this.username)
      localStorage.setItem('role', this.role)
      localStorage.setItem('email', this.email)
    },

    // 인증 정보 초기화
    clearAuth() {
      this.token = null
      this.username = ''
      this.role = ''
      this.email = ''
      this.error = null
      
      // localStorage에서 제거
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('email')
    },

    // 인증 상태 업데이트
    updateAuthStatus() {
      this.token = localStorage.getItem('token')
      this.username = localStorage.getItem('username')
      this.role = localStorage.getItem('role')
      this.email = localStorage.getItem('email')
    },

    // 에러 초기화
    clearError() {
      this.error = null
    },

    // 로그아웃
    logout() {
      this.clearAuth()
    }
  }
})
