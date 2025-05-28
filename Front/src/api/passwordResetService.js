// Front/src/api/passwordResetService.js
import { httpService, endpoints } from './http.js'

export const passwordResetService = {
  // Solicitar recuperación de contraseña
  async forgotPassword(email) {
    try {
      const response = await httpService.post(endpoints.auth.forgotPassword, { email })
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al enviar el email de recuperación')
    }
  },

  // Validar token de reset
  async validateResetToken(token) {
    try {
      const response = await httpService.get(`${endpoints.auth.validateResetToken}?token=${token}`)
      return response
    } catch (error) {
      throw new Error(error.message || 'Token inválido o expirado')
    }
  },

  // Cambiar contraseña con token
  async resetPassword(token, newPassword, confirmPassword) {
    try {
      const response = await httpService.post(endpoints.auth.resetPassword, {
        token,
        newPassword,
        confirmPassword
      })
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al cambiar la contraseña')
    }
  }
}