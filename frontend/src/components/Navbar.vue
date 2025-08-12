<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <!-- 홈페이지 로고 -->
      <router-link class="navbar-brand" to="/">
        <i class="bi bi-water me-2"></i>
        제주 해변
      </router-link>

      <!-- 모바일 토글 버튼 -->
      <button 
        class="navbar-toggler" 
        type="button" 
        data-bs-toggle="collapse" 
        data-bs-target="#navbarNav"
        aria-controls="navbarNav" 
        aria-expanded="false" 
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- 내비게이션 메뉴 -->
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">
              <i class="bi bi-house me-1"></i>
              홈
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin">
              <i class="bi bi-person-gear me-1"></i>
              관리자
            </router-link>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">
              <i class="bi bi-geo-alt me-1"></i>
              담당해변
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">
              <i class="bi bi-robot me-1"></i>
              챗봇
            </a>
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
  text-decoration: none;
}

.navbar-nav .nav-link {
  font-weight: 500;
  transition: color 0.3s ease;
  text-decoration: none;
}

.navbar-nav .nav-link:hover {
  color: #ffc107 !important;
}

.navbar-nav .nav-link.router-link-active {
  color: #ffc107 !important;
  font-weight: 600;
}

.btn-outline-light:hover {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #000;
}
</style>
