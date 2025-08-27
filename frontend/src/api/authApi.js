import apiClient from './config'

export const authApi = {
  // 로그인
  login: async (credentials) => {
    try {
      const response = await apiClient.post('/auth/login', credentials)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '로그인에 실패했습니다.' }
    }
  },

  // 회원가입
  register: async (userData) => {
    try {
      const response = await apiClient.post('/auth/register', userData)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '회원가입에 실패했습니다.' }
    }
  },

  // 사용자 정보 조회
  getProfile: async () => {
    try {
      const response = await apiClient.get('/user/profile')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '사용자 정보 조회에 실패했습니다.' }
    }
  }
}
