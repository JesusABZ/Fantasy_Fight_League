// Servicio para manejar la autenticación
import { httpService, endpoints } from './http.js'

export const authService = {
  // Iniciar sesión
  async login(credentials) {
    try {
      const response = await httpService.post(endpoints.auth.login, credentials)
      
      // Guardar el token si el login es exitoso
      if (response.token) {
        httpService.setAuthToken(response.token)
      }
      
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al iniciar sesión')
    }
  },

  // Registrarse
  async register(userData) {
    try {
      const response = await httpService.post(endpoints.auth.register, userData)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al registrarse')
    }
  },

  // Cerrar sesión
  async logout() {
    try {
      await httpService.post(endpoints.auth.logout)
    } catch (error) {
      console.error('Error al cerrar sesión:', error)
    } finally {
      // Siempre limpiar el token local
      httpService.clearAuthToken()
    }
  },

  // Confirmar email
  async confirmEmail(token) {
    try {
      const response = await httpService.get(`${endpoints.auth.confirm}?token=${token}`)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al confirmar el email')
    }
  },

  // Verificar si el usuario está autenticado
  isAuthenticated() {
    return !!httpService.getAuthToken()
  },

  // Obtener token actual
  getToken() {
    return httpService.getAuthToken()
  }
}