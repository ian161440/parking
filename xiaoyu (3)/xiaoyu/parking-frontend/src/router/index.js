import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import UserDashboard from '../views/UserDashboard.vue';
import AdminDashboard from '../views/AdminDashboard.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },
  {
    path: '/user',
    component: UserDashboard
  },
  {
    path: '/admin',
    component: AdminDashboard
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const publicPaths = ['/login', '/register'];
  if (publicPaths.includes(to.path)) {
    next();
    return;
  }
  const stored = localStorage.getItem('user');
  if (!stored) {
    next('/login');
    return;
  }
  const user = JSON.parse(stored);
  if (to.path.startsWith('/admin') && user.role !== 'ADMIN') {
    next('/user');
    return;
  }
  next();
});

export default router;
