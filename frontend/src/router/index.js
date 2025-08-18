import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import AdminPage from '../components/AdminPage.vue'
import BeachCrowdPage from '../components/BeachCrowdPage.vue'
import BeachDetailPage from '../components/BeachDetailPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminPage
  },
  {
    path: '/beach-crowd',
    name: 'BeachCrowd',
    component: BeachCrowdPage
  },
  {
    path: '/beach-crowd/:beachName',
    name: 'BeachDetail',
    component: BeachDetailPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
