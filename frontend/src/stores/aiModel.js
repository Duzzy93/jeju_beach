import { defineStore } from 'pinia'
import { aiModelApi } from '../api/aiModelApi'

export const useAiModelStore = defineStore('aiModel', {
  state: () => ({
    status: null,
    info: null,
    loading: false,
    error: null
  }),

  getters: {
    // AI 모델이 실행 중인지 확인
    isRunning: (state) => state.status?.status === 'RUNNING',
    
    // AI 모델이 중지된 상태인지 확인
    isStopped: (state) => state.status?.status === 'STOPPED'
  },

  actions: {
    // AI 모델 상태 조회
    async fetchStatus() {
      this.loading = true
      this.error = null
      
      try {
        const status = await aiModelApi.getStatus()
        this.status = status
        return { success: true, data: status }
      } catch (error) {
        this.error = error.message || 'AI 모델 상태 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // AI 모델 정보 조회
    async fetchInfo() {
      this.loading = true
      this.error = null
      
      try {
        const info = await aiModelApi.getInfo()
        this.info = info
        return { success: true, data: info }
      } catch (error) {
        this.error = error.message || 'AI 모델 정보 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // AI 모델 제어
    async controlModel(endpoint) {
      this.loading = true
      this.error = null
      
      try {
        const result = await aiModelApi.controlModel(endpoint)
        
        // 제어 후 상태 재조회
        await this.fetchStatus()
        
        return { success: true, data: result }
      } catch (error) {
        this.error = error.message || `AI 모델 ${endpoint}에 실패했습니다.`
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 에러 초기화
    clearError() {
      this.error = null
    }
  }
})
