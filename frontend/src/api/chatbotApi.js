import apiClient from './config'

export const chatbotApi = {
  // 챗봇과 대화
  sendMessage: async (message) => {
    try {
      const response = await apiClient.post('/chatbot/chat', { message })
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '챗봇 응답을 받지 못했습니다.' }
    }
  },

  // 대화 히스토리 조회
  getChatHistory: async () => {
    try {
      const response = await apiClient.get('/chatbot/history')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '대화 히스토리 조회에 실패했습니다.' }
    }
  },

  // 빠른 질문 목록 조회
  getQuickQuestions: async () => {
    try {
      const response = await apiClient.get('/chatbot/quick-questions')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '빠른 질문 목록 조회에 실패했습니다.' }
    }
  }
}
