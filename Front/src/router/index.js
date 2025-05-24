import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: {
      requiresGuest: true, // Solo usuarios no autenticados
      title: 'Iniciar Sesión - Fantasy Fight League'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: {
      requiresGuest: true, // Solo usuarios no autenticados
      title: 'Crear Cuenta - Fantasy Fight League'
    }
  },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('../views/VerifyEmailView.vue'),
    meta: {
      requiresGuest: true, // Solo usuarios no autenticados
      title: 'Verificar Email - Fantasy Fight League'
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPasswordView.vue'),
    meta: {
      requiresGuest: true, // Solo usuarios no autenticados
      title: 'Recuperar Contraseña - Fantasy Fight League'
    }
  },
  {
    path: '/email-unverified',
    name: 'EmailUnverified',
    component: () => import('../views/EmailUnverifiedView.vue'),
    meta: {
      requiresGuest: true, // Solo usuarios no autenticados
      title: 'Email No Verificado - Fantasy Fight League'
    }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/AboutView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Guard de navegación para manejar títulos de página
router.beforeEach((to, from, next) => {
  // Actualizar título de la página
  if (to.meta.title) {
    document.title = to.meta.title
  } else {
    document.title = 'Fantasy Fight League'
  }
  
  next()
})

export default router