<template>
  <div class="beach-management-container">
    <div class="container mt-4">
      <div class="row">
        <div class="col-12">
          <h2 class="mb-4">해변 관리</h2>
          
          <!-- 해변 추가 버튼 -->
          <div class="mb-3">
            <button class="btn btn-primary" @click="showAddForm = true">
              <i class="fas fa-plus"></i> 새 해변 추가
            </button>
          </div>
          
          <!-- 해변 목록 -->
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>이름</th>
                  <th>지역</th>
                  <th>위도</th>
                  <th>경도</th>
                  <th>상태</th>
                  <th>생성자</th>
                  <th>생성일</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="beach in beaches" :key="beach.id">
                  <td>{{ beach.id }}</td>
                  <td>{{ beach.name }}</td>
                  <td>{{ beach.region }}</td>
                  <td>{{ beach.latitude }}</td>
                  <td>{{ beach.longitude }}</td>
                  <td>
                    <span :class="['badge', beach.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary']">
                      {{ beach.status === 'ACTIVE' ? '활성' : '비활성' }}
                    </span>
                  </td>
                  <td>{{ beach.createdBy ? beach.createdBy.username : 'N/A' }}</td>
                  <td>{{ formatDate(beach.createdAt) }}</td>
                  <td>
                    <div class="btn-group" role="group">
                      <button class="btn btn-sm btn-outline-primary" @click="editBeach(beach)">
                        수정
                      </button>
                      <button class="btn btn-sm btn-outline-warning" @click="toggleStatus(beach)">
                        {{ beach.status === 'ACTIVE' ? '비활성화' : '활성화' }}
                      </button>
                      <button class="btn btn-sm btn-outline-danger" @click="deleteBeach(beach.id)">
                        삭제
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 해변 추가/수정 모달 -->
    <div v-if="showAddForm || showEditForm" class="modal-overlay" @click="closeForm">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title">{{ showEditForm ? '해변 수정' : '새 해변 추가' }}</h5>
          <button type="button" class="btn-close" @click="closeForm"></button>
        </div>
        
        <form @submit.prevent="showEditForm ? updateBeach() : createBeach()">
          <div class="modal-body">
            <div class="mb-3">
              <label for="beach-name" class="form-label">해변명 *</label>
              <input
                type="text"
                class="form-control"
                id="beach-name"
                v-model="beachForm.name"
                required
                placeholder="해변명을 입력하세요"
              />
            </div>
            
            <div class="mb-3">
              <label for="beach-region" class="form-label">지역</label>
              <input
                type="text"
                class="form-control"
                id="beach-region"
                v-model="beachForm.region"
                placeholder="지역을 입력하세요 (예: 제주시 구좌읍)"
              />
            </div>
            
            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="beach-latitude" class="form-label">위도 *</label>
                  <input
                    type="number"
                    class="form-control"
                    id="beach-latitude"
                    v-model="beachForm.latitude"
                    required
                    step="0.0000001"
                    placeholder="위도 (예: 33.5431)"
                  />
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label for="beach-longitude" class="form-label">경도 *</label>
                  <input
                    type="number"
                    class="form-control"
                    id="beach-longitude"
                    v-model="beachForm.longitude"
                    required
                    step="0.0000001"
                    placeholder="경도 (예: 126.6674)"
                  />
                </div>
              </div>
            </div>
            
            <div class="mb-3">
              <label for="beach-description" class="form-label">설명</label>
              <textarea
                class="form-control"
                id="beach-description"
                v-model="beachForm.description"
                rows="3"
                placeholder="해변에 대한 설명을 입력하세요"
              ></textarea>
            </div>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeForm">취소</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? '처리 중...' : (showEditForm ? '수정' : '추가') }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 알림 메시지 -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger', 'alert-dismissible', 'position-fixed', 'top-0', 'end-0', 'm-3']" style="z-index: 1050;">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''"></button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BeachManagementPage',
  data() {
    return {
      beaches: [],
      beachForm: {
        name: '',
        region: '',
        latitude: null,
        longitude: null,
        description: ''
      },
      showAddForm: false,
      showEditForm: false,
      editingBeachId: null,
      loading: false,
      message: '',
      messageType: 'success'
    }
  },
  async mounted() {
    await this.loadBeaches();
  },
  methods: {
    async loadBeaches() {
      try {
        const response = await fetch('http://localhost:8080/api/beaches');
        if (response.ok) {
          this.beaches = await response.json();
        } else {
          this.showMessage('해변 목록을 불러오는데 실패했습니다', 'danger');
        }
      } catch (error) {
        this.showMessage('서버 연결에 실패했습니다', 'danger');
      }
    },
    
    editBeach(beach) {
      this.beachForm = {
        name: beach.name,
        region: beach.region || '',
        latitude: beach.latitude,
        longitude: beach.longitude,
        description: beach.description || ''
      };
      this.editingBeachId = beach.id;
      this.showEditForm = true;
    },
    
    async createBeach() {
      this.loading = true;
      
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/api/beaches', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(this.beachForm)
        });
        
        if (response.ok) {
          this.showMessage('해변이 성공적으로 추가되었습니다', 'success');
          this.closeForm();
          await this.loadBeaches();
        } else {
          const error = await response.json();
          this.showMessage(error.message || '해변 추가에 실패했습니다', 'danger');
        }
      } catch (error) {
        this.showMessage('서버 연결에 실패했습니다', 'danger');
      } finally {
        this.loading = false;
      }
    },
    
    async updateBeach() {
      this.loading = true;
      
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8080/api/beaches/${this.editingBeachId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(this.beachForm)
        });
        
        if (response.ok) {
          this.showMessage('해변이 성공적으로 수정되었습니다', 'success');
          this.closeForm();
          await this.loadBeaches();
        } else {
          const error = await response.json();
          this.showMessage(error.message || '해변 수정에 실패했습니다', 'danger');
        }
      } catch (error) {
        this.showMessage('서버 연결에 실패했습니다', 'danger');
      } finally {
        this.loading = false;
      }
    },
    
    async deleteBeach(beachId) {
      if (!confirm('정말로 이 해변을 삭제하시겠습니까?')) {
        return;
      }
      
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8080/api/beaches/${beachId}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (response.ok) {
          this.showMessage('해변이 성공적으로 삭제되었습니다', 'success');
          await this.loadBeaches();
        } else {
          const error = await response.json();
          this.showMessage(error.message || '해변 삭제에 실패했습니다', 'danger');
        }
      } catch (error) {
        this.showMessage('서버 연결에 실패했습니다', 'danger');
      }
    },
    
    async toggleStatus(beach) {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8080/api/beaches/${beach.id}/toggle-status`, {
          method: 'PATCH',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (response.ok) {
          this.showMessage('해변 상태가 변경되었습니다', 'success');
          await this.loadBeaches();
        } else {
          const error = await response.json();
          this.showMessage(error.message || '상태 변경에 실패했습니다', 'danger');
        }
      } catch (error) {
        this.showMessage('서버 연결에 실패했습니다', 'danger');
      }
    },
    
    closeForm() {
      this.showAddForm = false;
      this.showEditForm = false;
      this.editingBeachId = null;
      this.beachForm = {
        name: '',
        region: '',
        latitude: null,
        longitude: null,
        description: ''
      };
    },
    
    showMessage(message, type = 'success') {
      this.message = message;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 3000);
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Date(dateString).toLocaleDateString('ko-KR');
    }
  }
}
</script>

<style scoped>
.beach-management-container {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 1rem;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.btn-close:hover {
  opacity: 0.7;
}

.table th {
  background-color: #e9ecef;
  border-top: none;
}

.btn-group .btn {
  margin-right: 2px;
}

.btn-group .btn:last-child {
  margin-right: 0;
}
</style>
