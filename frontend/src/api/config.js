import axios from 'axios'

// API 기본 설정 - 환경 변수 또는 기본값 사용
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? (process.env.VUE_APP_API_URL || 'http://15.165.30.16:8080/api')  // 환경 변수 또는 기본값
  : 'http://localhost:8080/api'  // 개발 환경에서는 localhost 사용

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터 - 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터 - 에러 처리
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // 토큰이 만료되었거나 유효하지 않은 경우
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('email')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
