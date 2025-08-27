import { defineStore } from 'pinia'
import { beachApi } from '../api/beachApi'

export const useBeachStore = defineStore('beach', {
  state: () => ({
    beaches: [],
    currentBeach: null,
    loading: false,
    error: null,
    searchResults: [],
    myBeaches: []
  }),

  getters: {
    // 활성 해변만 필터링
    activeBeaches: (state) => state.beaches.filter(beach => beach.status === 'ACTIVE'),
    
    // 지역별 해변 그룹화
    beachesByRegion: (state) => {
      return state.beaches.reduce((groups, beach) => {
        const region = beach.region || '기타'
        if (!groups[region]) {
          groups[region] = []
        }
        groups[region].push(beach)
        return groups
      }, {})
    },

    // 해변 총 개수
    totalBeaches: (state) => state.beaches.length,

    // 검색 결과 개수
    searchResultCount: (state) => state.searchResults.length
  },

  actions: {
    // 모든 해변 조회
    async fetchAllBeaches() {
      this.loading = true
      this.error = null
      
      try {
        const beaches = await beachApi.getAllBeaches()
        this.beaches = beaches
        return { success: true, data: beaches }
      } catch (error) {
        this.error = error.message || '해변 정보 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 활성 해변 조회
    async fetchActiveBeaches() {
      this.loading = true
      this.error = null
      
      try {
        const beaches = await beachApi.getActiveBeaches()
        this.beaches = beaches
        return { success: true, data: beaches }
      } catch (error) {
        this.error = error.message || '활성 해변 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 특정 해변 조회
    async fetchBeachById(id) {
      this.loading = true
      this.error = null
      
      try {
        const beach = await beachApi.getBeachById(id)
        this.currentBeach = beach
        return { success: true, data: beach }
      } catch (error) {
        this.error = error.message || '해변 정보 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 지역별 해변 조회
    async fetchBeachesByRegion(region) {
      this.loading = true
      this.error = null
      
      try {
        const beaches = await beachApi.getBeachesByRegion(region)
        return { success: true, data: beaches }
      } catch (error) {
        this.error = error.message || '지역별 해변 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 내가 관리하는 해변 조회
    async fetchMyBeaches() {
      this.loading = true
      this.error = null
      
      try {
        const beaches = await beachApi.getMyBeaches()
        this.myBeaches = beaches
        return { success: true, data: beaches }
      } catch (error) {
        this.error = error.message || '내 해변 조회에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 해변 검색
    async searchBeaches(name) {
      this.loading = true
      this.error = null
      
      try {
        const beaches = await beachApi.searchBeaches(name)
        this.searchResults = beaches
        return { success: true, data: beaches }
      } catch (error) {
        this.error = error.message || '해변 검색에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 해변 생성
    async createBeach(beachData) {
      this.loading = true
      this.error = null
      
      try {
        const beach = await beachApi.createBeach(beachData)
        this.beaches.push(beach)
        return { success: true, data: beach }
      } catch (error) {
        this.error = error.message || '해변 생성에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 해변 수정
    async updateBeach(id, beachData) {
      this.loading = true
      this.error = null
      
      try {
        const updatedBeach = await beachApi.updateBeach(id, beachData)
        
        // 로컬 상태 업데이트
        const index = this.beaches.findIndex(beach => beach.id === id)
        if (index !== -1) {
          this.beaches[index] = updatedBeach
        }
        
        if (this.currentBeach && this.currentBeach.id === id) {
          this.currentBeach = updatedBeach
        }
        
        return { success: true, data: updatedBeach }
      } catch (error) {
        this.error = error.message || '해변 수정에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 해변 삭제
    async deleteBeach(id) {
      this.loading = true
      this.error = null
      
      try {
        await beachApi.deleteBeach(id)
        
        // 로컬 상태에서 제거
        this.beaches = this.beaches.filter(beach => beach.id !== id)
        this.myBeaches = this.myBeaches.filter(beach => beach.id !== id)
        
        if (this.currentBeach && this.currentBeach.id === id) {
          this.currentBeach = null
        }
        
        return { success: true }
      } catch (error) {
        this.error = error.message || '해변 삭제에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 해변 상태 토글
    async toggleBeachStatus(id) {
      this.loading = true
      this.error = null
      
      try {
        const updatedBeach = await beachApi.toggleBeachStatus(id)
        
        // 로컬 상태 업데이트
        const index = this.beaches.findIndex(beach => beach.id === id)
        if (index !== -1) {
          this.beaches[index] = updatedBeach
        }
        
        if (this.currentBeach && this.currentBeach.id === id) {
          this.currentBeach = updatedBeach
        }
        
        return { success: true, data: updatedBeach }
      } catch (error) {
        this.error = error.message || '해변 상태 변경에 실패했습니다.'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // 에러 초기화
    clearError() {
      this.error = null
    },

    // 검색 결과 초기화
    clearSearchResults() {
      this.searchResults = []
    }
  }
})
