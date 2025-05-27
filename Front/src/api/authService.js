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
      
      // El registro exitoso no incluye token automáticamente
      // Solo devolvemos la respuesta del servidor
      return response
    } catch (error) {
      // Manejo específico de errores de registro
      if (error.message.includes('username')) {
        throw new Error('El nombre de usuario ya está en uso')
      } else if (error.message.includes('email')) {
        throw new Error('El email ya está registrado')
      } else {
        throw new Error(error.message || 'Error al registrarse. Inténtalo de nuevo.')
      }
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