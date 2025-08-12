<template>
  <footer class="bg-dark text-light py-4 mt-auto">
    <div class="container">
      <div class="row">
        <div class="col-md-4">
          <h5 class="mb-3 text-white">
            <i class="bi bi-water me-2"></i>
            제주 해변
          </h5>
          <p class="text-white-50">
            아름다운 제주의 해변을 소개하는 플랫폼입니다.
          </p>
        </div>
        <div class="col-md-4">
          <h6 class="mb-3 text-white">빠른 링크</h6>
          <ul class="list-unstyled">
            <li><a href="#" class="text-white-50 text-decoration-none">홈</a></li>
            <li><a href="#" class="text-white-50 text-decoration-none">해변 정보</a></li>
            <li><a href="#" class="text-white-50 text-decoration-none">문의하기</a></li>
          </ul>
        </div>
        <div class="col-md-4">
          <h6 class="mb-3 text-white">연락처</h6>
          <p class="text-white-50 mb-1">
            <i class="bi bi-envelope me-2"></i>
            info@jejubeach.com
          </p>
          <p class="text-white-50 mb-1">
            <i class="bi bi-telephone me-2"></i>
            064-123-4567
          </p>
        </div>
      </div>
      <hr class="my-4 border-light">
      <div class="row align-items-center">
        <div class="col-md-6">
          <p class="text-white-50 mb-0">
            &copy; 2024 제주 해변. 모든 권리 보유.
          </p>
        </div>
        <div class="col-md-6 text-md-end">
          <div class="social-links">
            <a href="#" class="text-white-50 me-3">
              <i class="bi bi-facebook"></i>
            </a>
            <a href="#" class="text-white-50 me-3">
              <i class="bi bi-instagram"></i>
            </a>
            <a href="#" class="text-white-50">
              <i class="bi bi-twitter"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
.social-links a {
  font-size: 1.2rem;
  transition: color 0.3s ease;
}

.social-links a:hover {
  color: #ffc107 !important;
}

footer {
  margin-top: auto;
}

/* 링크 호버 효과 */
footer a:hover {
  color: #ffc107 !important;
  text-decoration: none;
}
</style>
