// composables/useNavigation.js
import { useRouter } from 'vue-router'

export function useNavigation() {
  const router = useRouter()

  /**
   * Navega hacia atrás de forma inteligente
   * - Si hay historial, va a la página anterior
   * - Si no hay historial, va a la home
   * @param {string} fallbackRoute - Ruta por defecto si no hay historial (default: '/')
   */
  const goBackSmart = (fallbackRoute = '/') => {
    if (window.history.length > 1) {
      router.go(-1)
    } else {
      router.push(fallbackRoute)
    }
  }

  /**
   * Navega hacia atrás respetando el estado de autenticación
   * - Si está logueado y no hay historial, va al dashboard
   * - Si no está logueado y no hay historial, va a la home
   * @param {boolean} isAuthenticated - Estado de autenticación del usuario
   */
  const goBackWithAuth = (isAuthenticated = false) => {
    if (window.history.length > 1) {
      router.go(-1)
    } else {
      // Decidir el fallback basado en el estado de autenticación
      const fallbackRoute = isAuthenticated ? '/dashboard' : '/'
      router.push(fallbackRoute)
    }
  }

  /**
   * Verifica si es posible navegar hacia atrás
   * @returns {boolean} true si hay historial disponible
   */
  const canGoBack = () => {
    return window.history.length > 1
  }

  return {
    goBackSmart,
    goBackWithAuth,
    canGoBack
  }
}