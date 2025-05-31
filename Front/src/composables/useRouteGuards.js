// Composable para manejar guards de rutas y navegación segura
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth.js'

export function useRouteGuards() {
  const router = useRouter()
  const route = useRoute()
  const authStore = useAuthStore()

  // Computed properties para verificar permisos
  const canAccessCurrentRoute = computed(() => {
    return authStore.canAccessRoute(route.meta)
  })

  const shouldRedirectToEmailVerification = computed(() => {
    return authStore.isAuthenticated && 
           !authStore.isEmailConfirmed && 
           route.meta.requiresEmailVerified
  })

  const shouldRedirectToDashboard = computed(() => {
    return authStore.isAuthenticated && route.meta.requiresGuest
  })

  const shouldRedirectToLogin = computed(() => {
    return !authStore.isAuthenticated && route.meta.requiresAuth
  })

  // Funciones para navegación segura
  const navigateToLogin = (redirectPath = null) => {
    const loginRoute = {
      name: 'Login',
      query: redirectPath ? { redirect: redirectPath } : {}
    }
    router.push(loginRoute)
  }

  const navigateToDashboard = () => {
    router.push({ name: 'Dashboard' })
  }

  const navigateToEmailVerification = (email = null) => {
    const emailVerificationRoute = {
      name: 'EmailUnverified',
      query: email ? { email } : {}
    }
    router.push(emailVerificationRoute)
  }

  const navigateToHome = () => {
    router.push({ name: 'Home' })
  }

  // Función para verificar acceso a una ruta específica
  const checkRouteAccess = (routeName, routeParams = {}) => {
    try {
      const targetRoute = router.resolve({ name: routeName, params: routeParams })
      return authStore.canAccessRoute(targetRoute.meta)
    } catch (error) {
      console.error('Error al verificar acceso a ruta:', error)
      return false
    }
  }

  // Función para navegación condicionada
  const conditionalNavigate = (routeName, routeParams = {}, fallbackRoute = 'Home') => {
    if (checkRouteAccess(routeName, routeParams)) {
      router.push({ name: routeName, params: routeParams })
    } else {
      console.warn(`Acceso denegado a la ruta: ${routeName}`)
      router.push({ name: fallbackRoute })
    }
  }

  // Función para manejar redirecciones automáticas basadas en el estado actual
  const handleAutoRedirect = () => {
    if (shouldRedirectToLogin.value) {
      navigateToLogin(route.fullPath)
    } else if (shouldRedirectToEmailVerification.value) {
      navigateToEmailVerification(authStore.user?.email)
    } else if (shouldRedirectToDashboard.value) {
      navigateToDashboard()
    }
  }

  // Función para manejar logout con navegación segura
  const secureLogout = async () => {
    try {
      await authStore.logout()
      // Después del logout, redirigir a la home
      navigateToHome()
    } catch (error) {
      console.error('Error durante logout:', error)
      // Incluso si hay error, limpiar y redirigir
      navigateToHome()
    }
  }

  // Función para verificar si una URL es accesible manualmente
  const isUrlAccessible = (url) => {
    try {
      const targetRoute = router.resolve(url)
      return authStore.canAccessRoute(targetRoute.meta)
    } catch (error) {
      console.error('Error al verificar URL:', error)
      return false
    }
  }

  // Función para obtener la ruta de redirección después del login
  const getPostLoginRedirect = () => {
    // Prioridad: query param > ruta anterior > dashboard
    if (route.query.redirect && isUrlAccessible(route.query.redirect)) {
      return route.query.redirect
    }
    
    // Si no hay redirección específica, ir al dashboard
    return { name: 'Dashboard' }
  }

  return {
    // Estado computed
    canAccessCurrentRoute,
    shouldRedirectToEmailVerification,
    shouldRedirectToDashboard,
    shouldRedirectToLogin,
    
    // Funciones de navegación
    navigateToLogin,
    navigateToDashboard,
    navigateToEmailVerification,
    navigateToHome,
    
    // Funciones de verificación
    checkRouteAccess,
    isUrlAccessible,
    conditionalNavigate,
    
    // Funciones de manejo
    handleAutoRedirect,
    secureLogout,
    getPostLoginRedirect
  }
}