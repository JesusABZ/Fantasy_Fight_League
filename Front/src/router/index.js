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
      title: 'Iniciar Sesión - Fantasy Fight League'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: {
      title: 'Crear Cuenta - Fantasy Fight League'
    }
  },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('../views/VerifyEmailView.vue'),
    meta: {
      title: 'Verificar Email - Fantasy Fight League'
    }
  },
  {
    path: '/confirm-email',
    name: 'ConfirmEmail',
    component: () => import('../views/EmailConfirmationView.vue'),
    meta: {
      title: 'Confirmación de Email - Fantasy Fight League'
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPasswordView.vue'),
    meta: {
      title: 'Recuperar Contraseña - Fantasy Fight League'
    }
  },
  {
    path: '/email-unverified',
    name: 'EmailUnverified',
    component: () => import('../views/EmailUnverifiedView.vue'),
    meta: {
      title: 'Email No Verificado - Fantasy Fight League'
    }
  },
  {
    path: '/support',
    name: 'Support',
    component: () => import('../views/SupportView.vue'),
    meta: {
      title: 'Contactar Soporte - Fantasy Fight League'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/HomeLoggedView.vue'),
    meta: {
      title: 'Dashboard - Fantasy Fight League'
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: {
      title: 'Mi Perfil - Fantasy Fight League'
    }
  },
  {
    path: '/profile/edit',
    name: 'EditProfile',
    component: () => import('../views/EditProfileView.vue'),
    meta: {
      title: 'Editar Perfil - Fantasy Fight League'
    }
  },
  {
    path: '/profile/change-email',
    name: 'ChangeEmail',
    component: () => import('../views/ChangeEmailView.vue'),
    meta: {
      title: 'Cambiar Email - Fantasy Fight League'
    }
  },
  {
    path: '/profile/change-password',
    name: 'ChangePassword',
    component: () => import('../views/ChangePasswordView.vue'),
    meta: {
      title: 'Cambiar Contraseña - Fantasy Fight League'
    }
  },
  {
    path: '/league/:id',
    name: 'LeagueDetail',
    component: () => import('../views/LeagueDetailView.vue')
  },
  {
    path: '/league/:id/picks/:eventId',
    name: 'PicksSelection',
    component: () => import('../views/PicksSelectionView.vue'),
    meta: {
      title: 'Seleccionar Picks - Fantasy Fight League',
      requiresAuth: true
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

// Guard de navegación simple
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