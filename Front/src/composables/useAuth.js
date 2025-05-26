// composables/useAuth.js (Optimizado)
import { useAuthStore } from '../store/auth.js'
import { useRouter } from 'vue-router'
import { useNotifications } from './useNotifications.js'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()
  const { common: notifications, handleApiError } = useNotifications()

  // Función para redirigir después del login
  const redirectAfterLogin = (redirectTo = '/dashboard') => {
    router.push(redirectTo)
  }

  // Función para redirigir al login
  const redirectToLogin = (from = null) => {
    const query = from ? { redirect: from } : {}
    router.push({ name: 'Login', query })
  }

  // Función para manejar login con notificaciones automáticas
  const handleLogin = async (credentials, redirectTo = '/dashboard', showNotifications = true) => {
    try {
      await authStore.login(credentials)
      
      if (showNotifications) {
        notifications.loginSuccess()
      }
      
      redirectAfterLogin(redirectTo)
      return true
    } catch (error) {
      if (showNotifications) {
        handleApiError(error, 'Credenciales incorrectas')
      }
      throw error // Re-lanzar para que el componente pueda manejarlo si es necesario
    }
  }

  // Función para manejar registro con notificaciones automáticas
  const handleRegister = async (userData, showNotifications = true) => {
    try {
      await authStore.register(userData)
      
      if (showNotifications) {
        notifications.registerSuccess()
      }
      
      return true
    } catch (error) {
      if (showNotifications) {
        handleApiError(error, 'Error al crear la cuenta')
      }
      throw error
    }
  }

  // Función para manejar logout con redirección
  const handleLogout = async (redirectTo = '/', showNotifications = true) => {
    try {
      await authStore.logout()
      
      if (showNotifications) {
        notifications.logoutSuccess()
      }
      
      router.push(redirectTo)
      return true
    } catch (error) {
      console.error('Error en logout:', error)
      // Para logout, siempre redirigir aunque haya error
      router.push(redirectTo)
      return false
    }
  }

  // Función para confirmar email
  const handleEmailConfirmation = async (token, showNotifications = true) => {
    try {
      await authStore.confirmEmail(token)
      
      if (showNotifications) {
        notifications.success('¡Email verificado correctamente!')
      }
      
      return true
    } catch (error) {
      if (showNotifications) {
        handleApiError(error, 'Error al verificar el email')
      }
      throw error
    }
  }

  // Verificar si el usuario puede acceder a una ruta
  const canAccess = (requiredRoles = []) => {
    if (!authStore.isAuthenticated) return false
    if (requiredRoles.length === 0) return true
    
    return requiredRoles.some(role => 
      authStore.userRoles.includes(role)
    )
  }

  // Verificar si el usuario necesita verificar email
  const requiresEmailVerification = () => {
    return authStore.isAuthenticated && !authStore.isEmailConfirmed
  }

  // Guard para rutas que requieren autenticación
  const requireAuth = (to, from, next) => {
    if (!authStore.isAuthenticated) {
      redirectToLogin(to.fullPath)
      return false
    }
    return true
  }

  // Guard para rutas que requieren email verificado
  const requireVerifiedEmail = (to, from, next) => {
    if (!authStore.isAuthenticated) {
      redirectToLogin(to.fullPath)
      return false
    }
    
    if (!authStore.isEmailConfirmed) {
      router.push({ name: 'EmailUnverified', query: { email: authStore.user?.email } })
      return false
    }
    
    return true
  }

  // Guard para rutas que requieren roles específicos
  const requireRoles = (roles = []) => {
    return (to, from, next) => {
      if (!canAccess(roles)) {
        notifications.actionNotAllowed()
        router.push('/dashboard')
        return false
      }
      return true
    }
  }

  // Funciones utilitarias
  const getUserDisplayName = () => {
    const user = authStore.user
    if (!user) return ''
    
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`
    }
    return user.username || user.email || ''
  }

  const getUserInitials = () => {
    const user = authStore.user
    if (!user) return ''
    
    if (user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase()
    }
    
    if (user.username) {
      return user.username.substring(0, 2).toUpperCase()
    }
    
    return user.email ? user.email[0].toUpperCase() : ''
  }

  return {
    // Estado del store (reactive)
    user: authStore.user,
    isLoading: authStore.isLoading,
    error: authStore.error,
    isAuthenticated: authStore.isAuthenticated,
    isAdmin: authStore.isAdmin,
    isEmailConfirmed: authStore.isEmailConfirmed,
    userRoles: authStore.userRoles,
    
    // Acciones básicas del store
    login: authStore.login,
    register: authStore.register,
    logout: authStore.logout,
    confirmEmail: authStore.confirmEmail,
    clearError: authStore.clearError,
    
    // Funciones helper con notificaciones
    handleLogin,
    handleRegister,
    handleLogout,
    handleEmailConfirmation,
    redirectToLogin,
    redirectAfterLogin,
    
    // Verificaciones y guards
    canAccess,
    requiresEmailVerification,
    requireAuth,
    requireVerifiedEmail,
    requireRoles,
    
    // Utilidades de usuario
    getUserDisplayName,
    getUserInitials
  }
}