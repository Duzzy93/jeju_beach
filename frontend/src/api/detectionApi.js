import apiClient from './config'

export const detectionApi = {
  // 최신 탐지 결과 조회
  getLatestDetections: async () => {
    try {
      const response = await apiClient.get('/detections/latest')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '최신 탐지 결과 조회에 실패했습니다.' }
    }
  },

  // 특정 해변의 최신 탐지 결과 조회
  getLatestDetectionsByBeach: async (beachName) => {
    try {
      const response = await apiClient.get(`/detections/beach/${beachName}/latest`)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변별 탐지 결과 조회에 실패했습니다.' }
    }
  }
}
