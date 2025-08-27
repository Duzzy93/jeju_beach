<template>
  <div class="beach-management-page">
    <div class="container py-5">
      <div class="row">
        <div class="col-lg-8">
          <h2 class="mb-4">
            <i class="bi bi-geo-alt text-primary me-2"></i>
            해변 관리
          </h2>
          
          <!-- 해변 목록 -->
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0">등록된 해변 목록</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>해변명</th>
                      <th>지역</th>
                      <th>동영상 경로</th>
                      <th>상태</th>
                      <th>작업</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="beach in beaches" :key="beach.id">
                      <td>{{ beach.name }}</td>
                      <td>{{ beach.region || '-' }}</td>
                      <td>
                        <span v-if="beach.videoPath" class="text-success">
                          <i class="bi bi-check-circle me-1"></i>
                          {{ beach.videoPath }}
                        </span>
                        <span v-else class="text-warning">
                          <i class="bi bi-exclamation-triangle me-1"></i>
                          미설정
                        </span>
                      </td>
                      <td>
                        <span :class="getStatusBadgeClass(beach.status)">
                          {{ getStatusText(beach.status) }}
                        </span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-outline-primary me-1" @click="editBeach(beach)">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" @click="deleteBeach(beach.id)">
                          <i class="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-lg-4">
          <!-- 해변 추가/수정 폼 -->
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-plus-circle text-success me-2"></i>
                {{ isEditing ? '해변 수정' : '해변 추가' }}
              </h5>
            </div>
            <div class="card-body">
              <form @submit.prevent="saveBeach">
                <div class="mb-3">
                  <label class="form-label">해변명 *</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="form.name" 
                    required
                    placeholder="예: 함덕해변"
                  >
                </div>
                
                <div class="mb-3">
                  <label class="form-label">지역</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="form.region" 
                    placeholder="예: 제주시 조천읍"
                  >
                </div>
                
                <div class="mb-3">
                  <label class="form-label">위도</label>
                  <input 
                    type="number" 
                    class="form-control" 
                    v-model="form.latitude" 
                    step="0.0000001"
                    placeholder="예: 33.5589"
                  >
                </div>
                
                <div class="mb-3">
                  <label class="form-label">경도</label>
                  <input 
                    type="number" 
                    class="form-control" 
                    v-model="form.longitude" 
                    step="0.0000001"
                    placeholder="예: 126.7944"
                  >
                </div>
                
                <div class="mb-3">
                  <label class="form-label">설명</label>
                  <textarea 
                    class="form-control" 
                    v-model="form.description" 
                    rows="3"
                    placeholder="해변에 대한 설명을 입력하세요"
                  ></textarea>
                </div>
                
                <div class="mb-3">
                  <label class="form-label">동영상 경로 *</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="form.videoPath" 
                    required
                    placeholder="예: /videos/hamduck_beach.mp4"
                  >
                  <small class="form-text text-muted">
                    서버의 videos 폴더 내 동영상 파일 경로를 입력하세요
                  </small>
                </div>
                
                <div class="d-grid gap-2">
                  <button type="submit" class="btn btn-primary">
                    <i class="bi bi-check-circle me-1"></i>
                    {{ isEditing ? '수정' : '추가' }}
                  </button>
                  <button 
                    v-if="isEditing" 
                    type="button" 
                    class="btn btn-outline-secondary"
                    @click="cancelEdit"
                  >
                    <i class="bi bi-x-circle me-1"></i>
                    취소
                  </button>
                </div>
              </form>
            </div>
          </div>
          
          <!-- 동영상 파일 업로드 가이드 -->
          <div class="card mt-3">
            <div class="card-header">
              <h6 class="mb-0">
                <i class="bi bi-info-circle text-info me-2"></i>
                동영상 업로드 가이드
              </h6>
            </div>
            <div class="card-body">
              <ol class="small">
                <li>동영상 파일을 <code>backend/videos/</code> 폴더에 업로드</li>
                <li>파일명은 영문과 언더스코어 사용 권장</li>
                <li>지원 형식: MP4, AVI, MOV</li>
                <li>경로 예시: <code>/videos/beach_name.mp4</code></li>
              </ol>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { beachApi } from '../api/beachApi'

export default {
  name: 'BeachManagementPage',
  data() {
    return {
      beaches: [],
      isEditing: false,
      editingId: null,
      form: {
        name: '',
        region: '',
        latitude: null,
        longitude: null,
        description: '',
        videoPath: ''
      }
    }
  },
  mounted() {
    this.loadBeaches()
  },
  methods: {
    async loadBeaches() {
      try {
        const beaches = await beachApi.getAllBeaches()
        this.beaches = beaches
      } catch (error) {
        console.error('해변 목록 로드 오류:', error)
        alert('해변 목록을 불러오는데 실패했습니다.')
      }
    },
    
    editBeach(beach) {
      this.isEditing = true
      this.editingId = beach.id
      this.form = { ...beach }
    },
    
    cancelEdit() {
      this.isEditing = false
      this.editingId = null
      this.resetForm()
    },
    
    resetForm() {
      this.form = {
        name: '',
        region: '',
        latitude: null,
        longitude: null,
        description: '',
        videoPath: ''
      }
    },
    
    async saveBeach() {
      try {
        if (this.isEditing) {
          await beachApi.updateBeach(this.editingId, this.form)
          alert('해변 정보가 수정되었습니다.')
        } else {
          await beachApi.createBeach(this.form)
          alert('새로운 해변이 추가되었습니다.')
        }
        this.loadBeaches()
        this.cancelEdit()
      } catch (error) {
        console.error('해변 저장 오류:', error)
        alert('저장 중 오류가 발생했습니다.')
      }
    },
    
    async deleteBeach(id) {
      if (!confirm('정말로 이 해변을 삭제하시겠습니까?')) {
        return
      }
      
      try {
        await beachApi.deleteBeach(id)
        alert('해변이 삭제되었습니다.')
        this.loadBeaches()
      } catch (error) {
        console.error('해변 삭제 오류:', error)
        alert('삭제 중 오류가 발생했습니다.')
      }
    },
    
    getStatusBadgeClass(status) {
      return status === 'ACTIVE' ? 'badge bg-success' : 'badge bg-secondary'
    },
    
    getStatusText(status) {
      return status === 'ACTIVE' ? '활성' : '비활성'
    }
  }
}
</script>

<style scoped>
.beach-management-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.card {
  border: none;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  border-radius: 12px;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  border-radius: 12px 12px 0 0 !important;
}

.table th {
  border-top: none;
  font-weight: 600;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style>
