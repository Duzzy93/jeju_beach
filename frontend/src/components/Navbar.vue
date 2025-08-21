<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <router-link class="navbar-brand" to="/">
        🏖️ 제주 해변 AI 가이드
      </router-link>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">홈</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/beach-crowd">해변 혼잡도</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/chatbot">AI 챗봇</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin">관리자</router-link>
          </li>
        </ul>

        <!-- 오른쪽 로그인 버튼 -->
        <div class="navbar-nav">
          <button class="btn btn-outline-light" type="button">
            <i class="bi bi-box-arrow-in-right me-1"></i>
            로그인
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar'
}
</script>

<style scoped>
.navbar-brand {
  font-weight: bold;
  font-size: 1.5rem;
}

.nav-link {
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #ffd700 !important;
}

.navbar-nav .nav-link.active {
  color: #ffd700 !important;
}
</style>
