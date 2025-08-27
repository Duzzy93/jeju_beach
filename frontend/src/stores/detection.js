import { defineStore } from 'pinia'
import { detectionApi } from '../api/detectionApi'

export const useDetectionStore = defineStore('detection', {
  state: () => ({
    latestDetections: [],
    beachDetections: {},
    loading: false,
    error: null
  }),

  getters: {
    // 최신 탐지 결과 개수
    latestDetectionCount: (state) => state.latestDetections.length,
    
    // 특정 해변의 탐지 결과
    getBeachDetections: (state) => (beachName) => {
      return state.beachDetections[beachName] || []
    }
  },

  actions: {
    // 최신 탐지 결과 조회
    async fetchLatestDetections() {
      this.loading = true
      this.error = null
      
      try {
        const detections = await detectionApi.getLatestDetections()
        this.latestDetections = detections
        return { success: true, data: detections }
      } catch (error) {
        this.error = error.message || '최신 탐지 결과 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 특정 해변의 최신 탐지 결과 조회
    async fetchBeachDetections(beachName) {
      this.loading = true
      this.error = null
      
      try {
        const detections = await detectionApi.getLatestDetectionsByBeach(beachName)
        this.beachDetections[beachName] = detections
        return { success: true, data: detections }
      } catch (error) {
        this.error = error.message || '해변별 탐지 결과 조회에 실패했습니다.'
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
