<template>
  <div class="chatbot-page">
    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="container text-center">
        <h1 class="display-4 fw-bold mb-4">🏖️ 제주 해변 AI 가이드</h1>
        <p class="lead">AI와 대화하며 제주 해변에 대한 모든 정보를 알아보세요!</p>
      </div>
    </section>

    <!-- 메인 채팅 영역 -->
    <section class="py-5">
      <div class="container">
        <div class="row">
          <!-- 채팅 영역 -->
          <div class="col-lg-8">
            <div class="chat-container">
              <div class="chat-header">
                <h3>💬 AI 가이드와 대화하기</h3>
                <p class="text-muted">함덕해변, 이호해변, 월정리해변에 대해 무엇이든 물어보세요!</p>
              </div>

              <!-- 채팅 메시지 영역 -->
              <div class="chat-messages" ref="chatMessages">
                <div v-for="message in conversationHistory" :key="message.messageId" 
                     :class="['message', message.role === 'user' ? 'user-message' : 'ai-message']">
                  <div class="message-content">
                    <div class="message-avatar">
                      {{ message.role === 'user' ? '👤' : '🤖' }}
                    </div>
                    <div class="message-text">
                      {{ message.content }}
                    </div>
                    <div class="message-time">
                      {{ formatTime(message.timestamp) }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- 채팅 입력 영역 -->
              <div class="chat-input">
                <form @submit.prevent="sendMessage" class="d-flex">
                  <input 
                    v-model="userInput" 
                    type="text" 
                    class="form-control me-2" 
                    placeholder="질문을 입력하세요..."
                    :disabled="isLoading"
                  />
                  <button 
                    type="submit" 
                    class="btn btn-primary" 
                    :disabled="!userInput.trim() || isLoading"
                  >
                    <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                    {{ isLoading ? '처리중...' : '전송' }}
                  </button>
                </form>
              </div>
            </div>
          </div>

          <!-- 사이드바 -->
          <div class="col-lg-4">
            <!-- 빠른 질문 -->
            <div class="quick-questions mb-4">
              <h4>🚀 빠른 질문</h4>
              <div class="d-grid gap-2">
                <button 
                  v-for="question in quickQuestions" 
                  :key="question.content"
                  @click="askQuickQuestion(question.content)"
                  class="btn btn-outline-primary btn-sm text-start"
                  :disabled="isLoading"
                >
                  {{ question.content }}
                </button>
              </div>
            </div>

            <!-- 해변 정보 -->
            <div class="beach-info">
              <h4>📍 해변 정보</h4>
              <div class="accordion" id="beachAccordion">
                <div class="accordion-item" v-for="(info, name) in beachInfo" :key="name">
                  <h2 class="accordion-header">
                    <button class="accordion-button collapsed" type="button" 
                            data-bs-toggle="collapse" :data-bs-target="`#collapse${name}`">
                      🏖️ {{ name }}
                    </button>
                  </h2>
                  <div :id="`collapse${name}`" class="accordion-collapse collapse" 
                       data-bs-parent="#beachAccordion">
                    <div class="accordion-body">
                      <div v-for="(value, key) in info" :key="key" class="mb-2">
                        <strong>{{ key }}:</strong> {{ value }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 대화 초기화 -->
            <div class="mt-4">
              <button @click="clearConversation" class="btn btn-outline-secondary w-100">
                🗑️ 대화 초기화
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ChatbotPage',
  data() {
    return {
      userInput: '',
      conversationHistory: [],
      isLoading: false,
      quickQuestions: [],
      beachInfo: {
        '함덕해변': {
          '위치': '제주도 동부',
          '특징': '제주도 대표적인 해변, 맑은 바다와 백사장',
          '혼잡도': '보통',
          '추천시간': '오전 9시-11시, 오후 4시-6시',
          '주차': '무료주차장 있음',
          '편의시설': '샤워장, 화장실, 음식점'
        },
        '이호해변': {
          '위치': '제주도 서부, 공항 근처',
          '특징': '공항 근처에 위치해 있어 접근성이 좋음',
          '혼잡도': '낮음',
          '추천시간': '오전 8시-10시, 오후 5시-7시',
          '주차': '무료주차장 있음',
          '편의시설': '샤워장, 화장실, 카페'
        },
        '월정리해변': {
          '위치': '제주도 동부',
          '특징': '맑은 바다와 카페 거리로 유명',
          '혼잡도': '높음',
          '추천시간': '오전 7시-9시, 오후 6시-8시',
          '주차': '유료주차장 있음',
          '편의시설': '샤워장, 화장실, 카페거리, 음식점'
        }
      }
    }
  },
  mounted() {
    this.loadQuickQuestions();
    this.scrollToBottom();
  },
  updated() {
    this.scrollToBottom();
  },
  methods: {
    async sendMessage() {
      if (!this.userInput.trim() || this.isLoading) return;

      const userMessage = {
        role: 'user',
        content: this.userInput,
        timestamp: new Date(),
        messageId: this.generateId()
      };

      this.conversationHistory.push(userMessage);
      const messageToSend = this.userInput;
      this.userInput = '';
      this.isLoading = true;

      try {
        const response = await fetch('http://localhost:8080/api/chatbot/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            message: messageToSend,
            conversationHistory: this.conversationHistory
          })
        });

        if (response.ok) {
          const data = await response.json();
          const aiMessage = {
            role: 'assistant',
            content: data.message,
            timestamp: new Date(),
            messageId: data.messageId || this.generateId()
          };
          this.conversationHistory.push(aiMessage);
        } else {
          const errorText = await response.text();
          console.error('API 오류:', response.status, errorText);
          
          let errorMessage = 'API 요청 실패';
          if (response.status === 401) {
            errorMessage = 'OpenAI API 인증 오류입니다. 관리자에게 문의해주세요.';
          } else if (response.status === 429) {
            errorMessage = 'API 호출 한도를 초과했습니다. 잠시 후 다시 시도해주세요.';
          } else if (response.status >= 500) {
            errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
          }
          
          throw new Error(errorMessage);
        }
      } catch (error) {
        console.error('Error:', error);
        const errorMessage = {
          role: 'assistant',
          content: error.message || '죄송합니다. 오류가 발생했습니다. 다시 시도해주세요.',
          timestamp: new Date(),
          messageId: this.generateId()
        };
        this.conversationHistory.push(errorMessage);
      } finally {
        this.isLoading = false;
      }
    },

    async askQuickQuestion(question) {
      this.userInput = question;
      await this.sendMessage();
    },

    async loadQuickQuestions() {
      try {
        const response = await fetch('http://localhost:8080/api/chatbot/quick-questions');
        if (response.ok) {
          this.quickQuestions = await response.json();
        }
      } catch (error) {
        console.error('빠른 질문 로드 실패:', error);
      }
    },

    clearConversation() {
      this.conversationHistory = [];
    },

    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.chatMessages) {
          this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight;
        }
      });
    },

    formatTime(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return date.toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    },

    generateId() {
      return Date.now().toString(36) + Math.random().toString(36).substr(2);
    }
  }
}
</script>

<style scoped>
.chatbot-page {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
}

.chat-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  overflow: hidden;
}

.chat-header {
  background: #f8f9fa;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 20px;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-avatar {
  font-size: 24px;
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 50%;
}

.message-text {
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 18px;
  max-width: 70%;
  word-wrap: break-word;
}

.user-message .message-text {
  background: #007bff;
  color: white;
}

.ai-message .message-text {
  background: #e9ecef;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #6c757d;
  margin-top: 4px;
  margin-left: 52px;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.quick-questions {
  background: white;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.beach-info {
  background: white;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.accordion-button:not(.collapsed) {
  background-color: #e7f1ff;
  color: #0c63e4;
}

.accordion-button:focus {
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

@media (max-width: 768px) {
  .chat-messages {
    height: 300px;
  }
  
  .message-text {
    max-width: 85%;
  }
}
</style>
