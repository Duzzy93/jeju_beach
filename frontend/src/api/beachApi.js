import apiClient from './config'

export const beachApi = {
  // 모든 해변 조회
  getAllBeaches: async () => {
    try {
      const response = await apiClient.get('/beaches')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 정보 조회에 실패했습니다.' }
    }
  },

  // 활성 해변 조회
  getActiveBeaches: async () => {
    try {
      const response = await apiClient.get('/beaches/active')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '활성 해변 조회에 실패했습니다.' }
    }
  },

  // 특정 해변 조회
  getBeachById: async (id) => {
    try {
      const response = await apiClient.get(`/beaches/${id}`)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 정보 조회에 실패했습니다.' }
    }
  },

  // 지역별 해변 조회
  getBeachesByRegion: async (region) => {
    try {
      const response = await apiClient.get(`/beaches/region/${region}`)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '지역별 해변 조회에 실패했습니다.' }
    }
  },

  // 내가 관리하는 해변 조회
  getMyBeaches: async () => {
    try {
      const response = await apiClient.get('/beaches/my-beaches')
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '내 해변 조회에 실패했습니다.' }
    }
  },

  // 해변 검색
  searchBeaches: async (name) => {
    try {
      const response = await apiClient.get('/beaches/search', {
        params: { name }
      })
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 검색에 실패했습니다.' }
    }
  },

  // 해변 생성
  createBeach: async (beachData) => {
    try {
      const response = await apiClient.post('/beaches', beachData)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 생성에 실패했습니다.' }
    }
  },

  // 해변 수정
  updateBeach: async (id, beachData) => {
    try {
      const response = await apiClient.put(`/beaches/${id}`, beachData)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 수정에 실패했습니다.' }
    }
  },

  // 해변 삭제
  deleteBeach: async (id) => {
    try {
      await apiClient.delete(`/beaches/${id}`)
      return { success: true }
    } catch (error) {
      throw error.response?.data || { message: '해변 삭제에 실패했습니다.' }
    }
  },

  // 해변 상태 토글
  toggleBeachStatus: async (id) => {
    try {
      const response = await apiClient.patch(`/beaches/${id}/toggle-status`)
      return response.data
    } catch (error) {
      throw error.response?.data || { message: '해변 상태 변경에 실패했습니다.' }
    }
  }
}
