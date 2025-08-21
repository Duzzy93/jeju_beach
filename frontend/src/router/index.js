import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import BeachCrowdPage from '../components/BeachCrowdPage.vue'
import BeachDetailPage from '../components/BeachDetailPage.vue'
import AdminPage from '../components/AdminPage.vue'
import ChatbotPage from '../components/ChatbotPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
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
    path: '/admin',
    name: 'Admin',
    component: AdminPage
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

export default router
