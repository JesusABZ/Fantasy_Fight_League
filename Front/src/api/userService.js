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

  // 🆕 NUEVO: Cambiar contraseña (requiere contraseña actual)
  async changePasswordWithCurrentPassword(passwordData) {
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
  },

   // 🆕 NUEVO: Subir imagen de perfil
  // En userService.js - Actualizar uploadProfileImage con más debugging

  async uploadProfileImage(file) {
    try {
      console.log('🚀 Iniciando subida de imagen:', {
        fileName: file.name,
        fileSize: file.size,
        fileType: file.type
      })
      
      const formData = new FormData()
      formData.append('file', file)
      
      const token = httpService.getAuthToken()
      console.log('🔑 Token disponible:', !!token)
      
      const url = `${httpService.baseURL}/files/profile-image`
      console.log('📡 URL del endpoint:', url)
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
          // NO incluir Content-Type para FormData
        },
        body: formData
      })
      
      console.log('📨 Respuesta HTTP:', {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok,
        headers: Object.fromEntries(response.headers.entries())
      })
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('❌ Error del servidor (texto):', errorText)
        
        try {
          const errorData = JSON.parse(errorText)
          console.error('❌ Error del servidor (JSON):', errorData)
          throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`)
        } catch (parseError) {
          throw new Error(`Error ${response.status}: ${errorText || response.statusText}`)
        }
      }
      
      const responseText = await response.text()
      console.log('📄 Respuesta raw del servidor:', responseText)
      
      try {
        const result = JSON.parse(responseText)
        console.log('✅ Respuesta parseada:', result)
        return result
      } catch (parseError) {
        console.error('❌ Error al parsear respuesta JSON:', parseError)
        console.log('📄 Texto que no se pudo parsear:', responseText)
        
        // Si la respuesta es texto plano, podría ser la URL directamente
        if (responseText && responseText.startsWith('http')) {
          return { imageUrl: responseText }
        }
        
        throw new Error('Respuesta del servidor no es JSON válido: ' + responseText)
      }
      
    } catch (error) {
      console.error('💥 Error en uploadProfileImage:', error)
      throw error
    }
  }

}