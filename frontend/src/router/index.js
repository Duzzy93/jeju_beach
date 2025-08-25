import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import HomePage from '../components/HomePage.vue'
import BeachCrowdPage from '../components/BeachCrowdPage.vue'
import BeachDetailPage from '../components/BeachDetailPage.vue'
import AdminPage from '../components/AdminPage.vue'
import ChatbotPage from '../components/ChatbotPage.vue'
import LoginPage from '../components/LoginPage.vue'
import BeachManagementPage from '../components/BeachManagementPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: { requiresGuest: true }
  },
  {
    path: '/beach-crowd',
    name: 'BeachCrowd',
    component: BeachCrowdPage
  },
  {
    path: '/beach-crowd/:beachName',
    name: 'BeachDetail',
    component: BeachDetailPage,
    props: true
  },
  {
    path: '/beach-management',
    name: 'BeachManagement',
    component: BeachManagementPage,
    meta: { requiresAuth: true, requiresManager: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/chatbot',
    name: 'Chatbot',
    component: ChatbotPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 라우터 가드
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 인증이 필요한 페이지
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
    return
  }
  
  // 매니저 권한이 필요한 페이지
  if (to.meta.requiresManager && !authStore.isManager) {
    next('/')
    return
  }
  
  // 관리자 권한이 필요한 페이지
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
    return
  }
  
  // 이미 로그인한 사용자가 로그인 페이지 접근 시
  if (to.meta.requiresGuest && authStore.isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

export default router
