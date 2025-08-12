<template>
  <div id="app" class="d-flex flex-column min-vh-100">
    <!-- 내비게이션 바 -->
    <Navbar />
    
    <!-- 라우터 뷰 (페이지 콘텐츠) -->
    <router-view />
    
    <!-- 푸터 -->
    <Footer />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'

export default {
  name: 'App',
  components: {
    Navbar,
    Footer
  }
}
</script>

<style>
#app {
  background-color: #f8f9fa;
}
</style>
