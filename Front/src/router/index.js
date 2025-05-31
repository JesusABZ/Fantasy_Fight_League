import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth.js'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: {
      title: 'Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: {
      title: 'Iniciar Sesión - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: true // Solo para usuarios NO autenticados
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: {
      title: 'Crear Cuenta - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: true // Solo para usuarios NO autenticados
    }
  },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('../views/VerifyEmailView.vue'),
    meta: {
      title: 'Verificar Email - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: true // Solo para usuarios NO autenticados
    }
  },
  {
    path: '/confirm-email',
    name: 'ConfirmEmail',
    component: () => import('../views/EmailConfirmationView.vue'),
    meta: {
      title: 'Confirmación de Email - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPasswordView.vue'),
    meta: {
      title: 'Recuperar Contraseña - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: true // Solo para usuarios NO autenticados
    }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/ResetPasswordView.vue'),
    meta: {
      title: 'Cambiar Contraseña - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos (necesita token válido)
    }
  },
  {
    path: '/email-unverified',
    name: 'EmailUnverified',
    component: () => import('../views/EmailUnverifiedView.vue'),
    meta: {
      title: 'Email No Verificado - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos
    }
  },
  {
    path: '/support',
    name: 'Support',
    component: () => import('../views/SupportView.vue'),
    meta: {
      title: 'Contactar Soporte - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos
    }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/AboutView.vue'),
    meta: {
      title: 'Acerca de - Fantasy Fight League',
      requiresAuth: false,
      requiresGuest: false // Accesible para todos
    }
  },
  // === RUTAS PROTEGIDAS ===
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/HomeLoggedView.vue'),
    meta: {
      title: 'Dashboard - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: true // ✅ NUEVO: Requiere email verificado
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: {
      title: 'Mi Perfil - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: false // Permitir acceso sin email verificado
    }
  },
  {
    path: '/profile/edit',
    name: 'EditProfile',
    component: () => import('../views/EditProfileView.vue'),
    meta: {
      title: 'Editar Perfil - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: false // Permitir acceso sin email verificado
    }
  },
  {
    path: '/profile/change-email',
    name: 'ChangeEmail',
    component: () => import('../views/ChangeEmailView.vue'),
    meta: {
      title: 'Cambiar Email - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: false // Permitir acceso sin email verificado
    }
  },
  {
    path: '/profile/change-password',
    name: 'ChangePassword',
    component: () => import('../views/ChangePasswordView.vue'),
    meta: {
      title: 'Cambiar Contraseña - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: false // Permitir acceso sin email verificado
    }
  },
  {
    path: '/league/:id',
    name: 'LeagueDetail',
    component: () => import('../views/LeagueDetailView.vue'),
    meta: {
      title: 'Detalle de Liga - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: true // ✅ NUEVO: Requiere email verificado
    }
  },
  {
    path: '/league/:id/picks/:eventId',
    name: 'PicksSelection',
    component: () => import('../views/PicksSelectionView.vue'),
    meta: {
      title: 'Seleccionar Picks - Fantasy Fight League',
      requiresAuth: true,
      requiresEmailVerified: true // ✅ NUEVO: Requiere email verificado
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// ===== GUARDS DE NAVEGACIÓN =====

// Guard principal que se ejecuta antes de cada navegación
router.beforeEach(async (to, from, next) => {
  console.log(`🧭 Navegando de ${from.name || 'ninguna'} a ${to.name}`)
  
  // Actualizar título de la página
  if (to.meta.title) {
    document.title = to.meta.title
  } else {
    document.title = 'Fantasy Fight League'
  }

  // Obtener el store de autenticación
  const authStore = useAuthStore()
  
  // ✅ PASO 1: Verificar si la ruta requiere autenticación
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      console.log('❌ Acceso denegado: Usuario no autenticado')
      // Redirigir al login con la ruta de destino como query param
      next({
        name: 'Login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  // ✅ PASO 2: Verificar si la ruta es solo para invitados (no autenticados)
  if (to.meta.requiresGuest) {
    if (authStore.isAuthenticated) {
      console.log('ℹ️ Usuario autenticado redirigido del área de invitados al dashboard')
      // Si está autenticado y trata de acceder a login/register, redirigir al dashboard
      next({ name: 'Dashboard' })
      return
    }
  }

  // ✅ PASO 3: Verificar si la ruta requiere email verificado
  if (to.meta.requiresEmailVerified) {
    if (authStore.isAuthenticated && !authStore.isEmailConfirmed) {
      console.log('⚠️ Acceso denegado: Email no verificado')
      // Redirigir a la página de email no verificado
      next({
        name: 'EmailUnverified',
        query: { email: authStore.user?.email }
      })
      return
    }
  }

  // ✅ PASO 4: Validaciones especiales para rutas específicas
  
  // Para reset-password: verificar que tenga token válido
  if (to.name === 'ResetPassword' && !to.query.token) {
    console.log('❌ Acceso denegado: Reset password sin token')
    next({ name: 'ForgotPassword' })
    return
  }

  // Para confirm-email: verificar que tenga token válido
  if (to.name === 'ConfirmEmail' && !to.query.token) {
    console.log('❌ Acceso denegado: Confirm email sin token')
    next({ name: 'Home' })
    return
  }

  // ✅ PASO 5: Permitir navegación
  console.log('✅ Navegación permitida')
  next()
})

// Guard que se ejecuta después de cada navegación
router.afterEach((to, from) => {
  // Log para debugging
  console.log(`🎯 Navegación completada: ${to.name}`)
  
  // Scroll to top en cambios de página
  if (to.name !== from.name) {
    window.scrollTo(0, 0)
  }
})

// Manejo de errores de navegación
router.onError((error) => {
  console.error('💥 Error de navegación:', error)
  
  // En caso de error, redirigir a la home
  router.push({ name: 'Home' })
})

export default router