// Servicio para manejar datos de usuario
import { httpService, endpoints } from './http.js'

export const userService = {
  // Obtener perfil del usuario
  async getProfile() {
    try {
      const response = await httpService.get(endpoints.user.profile)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al obtener el perfil')
    }
  },

  // Actualizar perfil
  async updateProfile(profileData) {
    try {
      const response = await httpService.put(endpoints.user.updateProfile, profileData)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al actualizar el perfil')
    }
  },

  // Cambiar contraseña
  async changePassword(passwordData) {
    try {
      const response = await httpService.put(endpoints.user.changePassword, passwordData)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al cambiar la contraseña')
    }
  },

  // Cambiar email
  async changeEmail(emailData) {
    try {
      const response = await httpService.put(endpoints.user.changeEmail, emailData)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al cambiar el email')
    }
  }
}