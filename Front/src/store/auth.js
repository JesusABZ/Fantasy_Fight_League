// Store para manejar el estado de autenticación
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../api/index.js'

export const useAuthStore = defineStore('auth', () => {
  // Estado reactivo
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  // Getters (computed)
  const isAuthenticated = computed(() => !!user.value)
  const userRoles = computed(() => {
    // 🔥 CORRECCIÓN: Verificar que roles sea un array antes de procesarlo
    if (!user.value?.roles || !Array.isArray(user.value.roles)) {
      return []
    }
    return user.value.roles
  })
  const isAdmin = computed(() => userRoles.value.includes('ROLE_ADMIN'))
  const isEmailConfirmed = computed(() => user.value?.emailConfirmed || false)

  // Actions
  async function login(credentials) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authService.login(credentials)
      
      // Después del login exitoso, intentar obtener el perfil completo del usuario
      try {
        const { userService } = await import('../api/userService.js')
        const userProfile = await userService.getProfile()
        
        // Combinar datos del login con el perfil completo
        user.value = {
          id: response.id,
          username: response.username,
          email: response.email,
          emailConfirmed: response.emailConfirmed,
          roles: Array.isArray(response.roles) ? response.roles : [], // 🔥 SEGURIDAD: Asegurar que roles sea array
          // Agregar datos del perfil
          firstName: userProfile.firstName,
          lastName: userProfile.lastName,
          profileImageUrl: userProfile.profileImageUrl,
          createdAt: userProfile.createdAt
        }
      } catch (profileError) {
        console.warn('No se pudo cargar el perfil completo:', profileError)
        // Usar solo los datos del login si falla el perfil
        user.value = {
          id: response.id,
          username: response.username,
          email: response.email,
          emailConfirmed: response.emailConfirmed,
          roles: Array.isArray(response.roles) ? response.roles : [] // 🔥 SEGURIDAD: Asegurar que roles sea array
        }
      }
      
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function register(userData) {
    isLoading.value = true
    error.value = null
    
    try {
      // Preparar datos para el backend
      const registrationData = {
        username: userData.username.trim(),
        email: userData.email.trim().toLowerCase(),
        password: userData.password,
        firstName: userData.firstName.trim(),
        lastName: userData.lastName.trim()
      }
      
      const response = await authService.register(registrationData)
      
      // El registro fue exitoso pero no logueamos automáticamente
      // El usuario debe verificar su email primero
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // 🔥 FUNCIÓN CORREGIDA - logout ahora SÍ llama al endpoint del backend
  async function logout() {
    isLoading.value = true
    
    try {
      console.log('🔄 Iniciando logout...')
      
      // ✅ LLAMAR al endpoint de logout del backend
      await authService.logout()
      console.log('✅ Logout exitoso en el backend')
      
    } catch (err) {
      console.error('❌ Error durante logout en backend:', err)
      // Continuar con la limpieza local aunque falle el backend
    } finally {
      // ✅ SIEMPRE limpiar estado local independientemente del resultado del backend
      console.log('🧹 Limpiando estado local...')
      user.value = null
      error.value = null
      isLoading.value = false
      
      console.log('✅ Logout completado')
    }
  }

  async function confirmEmail(token) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authService.confirmEmail(token)
      
      // Actualizar estado de confirmación si el usuario está logueado
      if (user.value) {
        user.value.emailConfirmed = true
      }
      
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Reenviar email de verificación
  async function resendVerificationEmail(email) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authService.resendVerificationEmail(email)
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  // Inicializar el estado cuando se carga la app
  async function initializeAuth() {
    // Verificar si hay un token guardado
    if (authService.isAuthenticated()) {
      console.log('Usuario previamente autenticado encontrado')
      
      // Intentar recuperar el perfil del usuario
      try {
        const { userService } = await import('../api/userService.js')
        const userProfile = await userService.getProfile()
        
        // 🔥 SEGURIDAD: Asegurar que roles sea un array
        if (userProfile.roles && !Array.isArray(userProfile.roles)) {
          userProfile.roles = []
        }
        
        user.value = userProfile
        console.log('Perfil de usuario recuperado:', userProfile)
      } catch (error) {
        console.warn('No se pudo recuperar el perfil del usuario:', error)
        // Si falla, limpiar el token porque probablemente expiró
        authService.clearAuthToken()
      }
    }
  }

  return {
    // Estado
    user,
    isLoading,
    error,
    
    // Getters
    isAuthenticated,
    userRoles,
    isAdmin,
    isEmailConfirmed,
    
    // Actions
    login,
    register,
    logout,
    confirmEmail,
    resendVerificationEmail,
    clearError,
    initializeAuth
  }
})