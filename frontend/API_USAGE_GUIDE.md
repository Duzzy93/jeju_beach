# 🚀 Pinia + Axios REST API 연동 가이드

## 📋 개요

이 프로젝트는 Vue 3 + Pinia + Axios를 사용하여 Spring Boot REST API와 연동하는 구조로 개선되었습니다.

## 🏗️ 구조

```
src/
├── api/
│   ├── config.js          # Axios 설정 및 인터셉터
│   ├── authApi.js         # 인증 관련 API
│   ├── beachApi.js        # 해변 관련 API
│   ├── chatbotApi.js      # 챗봇 관련 API
│   └── index.js           # API 서비스 통합 export
├── stores/
│   ├── auth.js            # 인증 상태 관리
│   ├── beach.js           # 해변 데이터 관리
│   └── chatbot.js         # 챗봇 대화 관리
└── components/
    └── LoginPage.vue      # 업데이트된 로그인 컴포넌트
```

## 🔧 주요 기능

### 1. Axios 설정 (`config.js`)
- 기본 URL 설정: `http://localhost:8080/api`
- 요청 인터셉터: JWT 토큰 자동 추가
- 응답 인터셉터: 401 에러 시 자동 로그아웃

### 2. API 서비스 레이어
- 각 도메인별로 분리된 API 서비스
- 에러 처리 및 응답 정규화
- TypeScript 친화적인 구조

### 3. Pinia 스토어
- API 통신 로직과 상태 관리 통합
- 비동기 액션 지원
- 에러 상태 관리

## 📖 사용법

### 컴포넌트에서 스토어 사용

```vue
<template>
  <div>
    <div v-if="beachStore.loading">로딩 중...</div>
    <div v-if="beachStore.error" class="alert alert-danger">
      {{ beachStore.error }}
    </div>
    <div v-for="beach in beachStore.beaches" :key="beach.id">
      {{ beach.name }}
    </div>
  </div>
</template>

<script>
import { useBeachStore } from '../stores/beach'

export default {
  setup() {
    const beachStore = useBeachStore()
    
    // 컴포넌트 마운트 시 해변 데이터 로드
    onMounted(async () => {
      await beachStore.fetchAllBeaches()
    })
    
    return { beachStore }
  }
}
</script>
```

### 직접 API 호출

```javascript
import { beachApi } from '../api'

// 해변 검색
const searchBeaches = async (name) => {
  try {
    const result = await beachApi.searchBeaches(name)
    console.log('검색 결과:', result)
  } catch (error) {
    console.error('검색 실패:', error.message)
  }
}
```

### 스토어 액션 사용

```javascript
import { useBeachStore } from '../stores/beach'

const beachStore = useBeachStore()

// 해변 생성
const createBeach = async (beachData) => {
  const result = await beachStore.createBeach(beachData)
  
  if (result.success) {
    console.log('해변 생성 성공:', result.data)
  } else {
    console.error('해변 생성 실패:', result.error)
  }
}
```

## 🔐 인증 처리

### 로그인
```javascript
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const result = await authStore.login({ username, password })

if (result.success) {
  // 로그인 성공, 홈페이지로 이동
  router.push('/')
} else {
  // 에러 메시지 표시
  console.error(result.error)
}
```

### 토큰 자동 관리
- 로그인 시 토큰이 자동으로 localStorage에 저장
- API 요청 시 자동으로 Authorization 헤더에 토큰 추가
- 401 에러 시 자동으로 로그아웃 처리

## 🚨 에러 처리

### API 레벨 에러 처리
```javascript
try {
  const result = await beachApi.getAllBeaches()
  // 성공 처리
} catch (error) {
  // 에러 메시지 표시
  console.error(error.message)
}
```

### 스토어 레벨 에러 처리
```javascript
const result = await beachStore.fetchAllBeaches()

if (result.success) {
  // 성공 처리
  console.log(result.data)
} else {
  // 에러 처리
  console.error(result.error)
}
```

## 📱 상태 관리

### 로딩 상태
```javascript
// 스토어의 loading 상태 확인
if (beachStore.loading) {
  return <LoadingSpinner />
}
```

### 에러 상태
```javascript
// 스토어의 error 상태 확인
if (beachStore.error) {
  return <ErrorMessage message={beachStore.error} />
}
```

## 🔄 데이터 동기화

### 로컬 상태 업데이트
- API 호출 성공 시 자동으로 로컬 상태 업데이트
- CRUD 작업 후 즉시 UI 반영
- 불필요한 API 재호출 방지

### 캐시 관리
- 해변 목록 등 자주 사용되는 데이터는 스토어에 캐시
- 필요 시에만 API 재호출

## 🚀 성능 최적화

### 1. 요청 중복 방지
- 로딩 중일 때 추가 요청 차단
- 동일한 API 호출 중복 방지

### 2. 에러 재시도
- 네트워크 에러 시 자동 재시도 로직 추가 가능
- 사용자 경험 향상

### 3. 데이터 프리페칭
- 필요한 데이터를 미리 로드하여 사용자 대기 시간 단축

## 📝 추가 개선 사항

### 1. TypeScript 지원
- API 응답 타입 정의
- 더 나은 타입 안정성

### 2. 캐싱 전략
- Redis나 메모리 캐시 연동
- API 응답 캐싱

### 3. 실시간 업데이트
- WebSocket 연동
- 실시간 데이터 동기화

## 🎯 결론

이 구조를 통해 다음과 같은 이점을 얻을 수 있습니다:

1. **코드 가독성 향상**: API 로직과 상태 관리가 명확하게 분리
2. **재사용성**: API 서비스를 여러 컴포넌트에서 재사용
3. **유지보수성**: 각 도메인별로 분리된 구조로 유지보수 용이
4. **에러 처리**: 일관된 에러 처리 및 사용자 피드백
5. **성능**: 불필요한 API 호출 방지 및 로컬 상태 관리
