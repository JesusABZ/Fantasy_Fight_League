// Composable para facilitar el uso de autenticación en componentes
import { useAuthStore } from '../store/auth.js'
import { useRouter } from 'vue-router'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()

  // Función para redirigir después del login
  const redirectAfterLogin = (redirectTo = '/') => {
    router.push(redirectTo)
  }

  // Función para redirigir al login
  const redirectToLogin = (from = null) => {
    const query = from ? { redirect: from } : {}
    router.push({ name: 'Login', query })
  }

  // Función para manejar login con redirección automática
  const handleLogin = async (credentials, redirectTo = '/') => {
    try {
      await authStore.login(credentials)
      redirectAfterLogin(redirectTo)
      return true
    } catch (error) {
      console.error('Error en login:', error)
      return false
    }
  }

  // Función para manejar logout con redirección
  const handleLogout = async (redirectTo = '/') => {
    try {
      await authStore.logout()
      router.push(redirectTo)
      return true
    } catch (error) {
      console.error('Error en logout:', error)
      return false
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

  return {
    // Estado del store
    user: authStore.user,
    isLoading: authStore.isLoading,
    error: authStore.error,
    isAuthenticated: authStore.isAuthenticated,
    isAdmin: authStore.isAdmin,
    isEmailConfirmed: authStore.isEmailConfirmed,
    
    // Acciones del store
    login: authStore.login,
    register: authStore.register,
    logout: authStore.logout,
    confirmEmail: authStore.confirmEmail,
    clearError: authStore.clearError,
    
    // Funciones helper
    handleLogin,
    handleLogout,
    redirectToLogin,
    redirectAfterLogin,
    canAccess
  }
}