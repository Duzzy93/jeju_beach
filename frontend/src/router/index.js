import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import HomePage from '../components/HomePage.vue'
import BeachCrowdPage from '../components/BeachCrowdPage.vue'
import BeachDetailPage from '../components/BeachDetailPage.vue'
import AdminPage from '../components/AdminPage.vue'
import ChatbotPage from '../components/ChatbotPage.vue'
import LoginPage from '../components/LoginPage.vue'
import BeachManagementPage from '../components/BeachManagementPage.vue'
import AIModelStatusPage from '../components/AIModelStatusPage.vue'

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
    component: BeachCrowdPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/beach-crowd/:beachName',
    name: 'BeachDetail',
    component: BeachDetailPage,
    props: true,
    meta: { requiresAuth: true }
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
    component: ChatbotPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/ai-model-status',
    name: 'AIModelStatus',
    component: AIModelStatusPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 라우터 가드
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 토큰이 있지만 사용자 정보가 로드되지 않은 경우 로드
  if (authStore.token && !authStore.user) {
    try {
      await authStore.loadUserProfile()
    } catch (error) {
      console.error('사용자 정보 로드 실패:', error)
      authStore.logout()
      next('/login')
      return
    }
  }
  
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
