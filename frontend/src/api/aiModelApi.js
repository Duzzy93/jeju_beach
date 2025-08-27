import apiClient from './config'

export const aiModelApi = {
  // AI 모델 상태 조회
  getStatus: async () => {
    try {
      const response = await apiClient.get('/ai-model/status')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: 'AI 모델 상태 조회에 실패했습니다.' }
    }
  },

  // AI 모델 정보 조회
  getInfo: async () => {
    try {
      const response = await apiClient.get('/ai-model/info')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: 'AI 모델 정보 조회에 실패했습니다.' }
    }
  },

  // AI 모델 제어 (시작/중지/재시작)
  controlModel: async (endpoint) => {
    try {
      const response = await apiClient.post(`/ai-model/${endpoint}`)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: `AI 모델 ${endpoint}에 실패했습니다.` }
    }
  }
}
