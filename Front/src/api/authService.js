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
  
  // 🔥 FUNCIÓN CORREGIDA - Cerrar sesión
  async logout() {
    try {
      console.log('🔄 Llamando al endpoint de logout del backend...')
      
      // ✅ LLAMAR al endpoint correcto del backend
      await httpService.post(endpoints.auth.logout)
      console.log('✅ Logout exitoso en el backend')
      
    } catch (error) {
      console.error('❌ Error al cerrar sesión en el backend:', error)
      // No lanzar el error para que siempre se limpie el token local
    } finally {
      // ✅ SIEMPRE limpiar el token local
      console.log('🧹 Limpiando token local...')
      httpService.clearAuthToken()
      console.log('✅ Token local limpiado')
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

  // 🔥 REENVIAR EMAIL DE VERIFICACIÓN - MÉTODO CORREGIDO
  async resendVerificationEmail(email) {
    try {
      console.log('Enviando petición de reenvío para email:', email)
      
      const response = await httpService.post(endpoints.auth.resendVerification, { 
        email: email 
      })
      
      console.log('Respuesta del servidor:', response)
      return response
    } catch (error) {
      console.error('Error en resendVerificationEmail:', error)
      throw new Error(error.message || 'Error al reenviar el email de verificación')
    }
  },

  // Verificar si el usuario está autenticado
  isAuthenticated() {
    return !!httpService.getAuthToken()
  },

  // Obtener token actual
  getToken() {
    return httpService.getAuthToken()
  },

  // 🔥 NUEVA FUNCIÓN - Limpiar token (para uso interno y externo)
  clearAuthToken() {
    httpService.clearAuthToken()
  }
}