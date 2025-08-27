import { defineStore } from 'pinia'
import { chatbotApi } from '../api/chatbotApi'

export const useChatbotStore = defineStore('chatbot', {
  state: () => ({
    messages: [],
    loading: false,
    error: null,
    chatHistory: []
  }),

  getters: {
    // 메시지 개수
    messageCount: (state) => state.messages.length,
    
    // AI 메시지 개수
    aiMessageCount: (state) => state.messages.filter(msg => msg.type === 'ai').length,
    
    // 사용자 메시지 개수
    userMessageCount: (state) => state.messages.filter(msg => msg.type === 'user').length
  },

  actions: {
    // 메시지 추가
    addMessage(message, type = 'user') {
      const newMessage = {
        id: Date.now(),
        content: message,
        type: type,
        timestamp: new Date().toISOString()
      }
      this.messages.push(newMessage)
    },

    // AI 응답 추가
    addAIResponse(response) {
      this.addMessage(response, 'ai')
    },

    // 메시지 전송 및 AI 응답 받기
    async sendMessage(message) {
      this.loading = true
      this.error = null
      
      // 사용자 메시지 추가
      this.addMessage(message, 'user')
      
      try {
        const result = await chatbotApi.sendMessage(message)
        this.addAIResponse(result.response || result.message)
        return { success: true, data: result }
      } catch (error) {
        this.error = error.message || '챗봇 응답을 받지 못했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 빠른 질문 목록 조회
    async fetchQuickQuestions() {
      this.loading = true
      this.error = null
      
      try {
        const questions = await chatbotApi.getQuickQuestions()
        return { success: true, data: questions }
      } catch (error) {
        this.error = error.message || '빠른 질문 목록 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 대화 히스토리 조회
    async fetchChatHistory() {
      this.loading = true
      this.error = null
      
      try {
        const history = await chatbotApi.getChatHistory()
        this.chatHistory = history
        return { success: true, data: history }
      } catch (error) {
        this.error = error.message || '대화 히스토리 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 대화 초기화
    clearChat() {
      this.messages = []
      this.error = null
    },

    // 에러 초기화
    clearError() {
      this.error = null
    },

    // 대화 내보내기 (선택사항)
    exportChat() {
      const chatData = {
        timestamp: new Date().toISOString(),
        messages: this.messages,
        summary: {
          total: this.messageCount,
          user: this.userMessageCount,
          ai: this.aiMessageCount
        }
      }
      
      const dataStr = JSON.stringify(chatData, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      
      const link = document.createElement('a')
      link.href = URL.createObjectURL(dataBlob)
      link.download = `chat-export-${new Date().toISOString().split('T')[0]}.json`
      link.click()
    }
  }
})
