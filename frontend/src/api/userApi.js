import apiClient from './config'

export const userApi = {
  // 사용자 역할 조회
  getUserRole: async () => {
    try {
      const response = await apiClient.get('/user/role')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '사용자 역할 조회에 실패했습니다.' }
    }
  },

  // 사용자 프로필 조회
  getUserProfile: async () => {
    try {
      const response = await apiClient.get('/user/profile')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '사용자 프로필 조회에 실패했습니다.' }
    }
  },

  // 접근 가능한 해변 조회
  getAccessibleBeaches: async () => {
    try {
      const response = await apiClient.get('/user/beaches')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '접근 가능한 해변 조회에 실패했습니다.' }
    }
  }
}
