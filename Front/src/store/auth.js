// Store para manejar el estado de autenticación
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../api/index.js'

export const useAuthStore = defineStore('auth', () => {
  // Estado reactivo
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const isInitialized = ref(false) // ✅ NUEVO: Para saber si ya se inicializó

  // Getters (computed)
  const isAuthenticated = computed(() => !!user.value)
  const userRoles = computed(() => {
    if (!user.value?.roles || !Array.isArray(user.value.roles)) {
      return []
    }
    return user.value.roles
  })
  const isAdmin = computed(() => userRoles.value.includes('ROLE_ADMIN'))
  const isEmailConfirmed = computed(() => user.value?.emailConfirmed || false)

  // ✅ NUEVO: Computed para verificar si el usuario puede acceder a áreas protegidas
  const canAccessProtectedRoutes = computed(() => {
    return isAuthenticated.value && isEmailConfirmed.value
  })

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
          roles: Array.isArray(response.roles) ? response.roles : [],
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
          roles: Array.isArray(response.roles) ? response.roles : []
        }
      }
      
      console.log('✅ Usuario logueado:', user.value)
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

  async function logout() {
    isLoading.value = true
    
    try {
      console.log('🔄 Iniciando logout...')
      
      // Llamar al endpoint de logout del backend
      await authService.logout()
      console.log('✅ Logout exitoso en el backend')
      
    } catch (err) {
      console.error('❌ Error durante logout en backend:', err)
      // Continuar con la limpieza local aunque falle el backend
    } finally {
      // Siempre limpiar estado local independientemente del resultado del backend
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

  // ✅ MEJORADO: Inicializar el estado cuando se carga la app
  async function initializeAuth() {
    if (isInitialized.value) {
      console.log('🔄 Auth ya inicializado, omitiendo...')
      return
    }

    console.log('🚀 Inicializando autenticación...')
    isLoading.value = true
    
    try {
      // Obtener el token almacenado
      const storedToken = authService.getToken()
      
      if (!storedToken) {
        console.log('ℹ️ No hay token almacenado')
        user.value = null
        return
      }
      
      console.log('🔑 Token encontrado, verificando validez...')
      
      // Verificar si el token es válido estructuralmente
      if (authService.isTokenExpired && authService.isTokenExpired(storedToken)) {
        console.warn('❌ Token expirado, limpiando...')
        authService.clearAuthToken()
        user.value = null
        return
      }
      
      // Intentar validar el token con el servidor
      try {
        const { userService } = await import('../api/userService.js')
        const userProfile = await userService.getProfile()
        
        // Verificar que el perfil tiene los datos mínimos requeridos
        if (!userProfile || !userProfile.id || !userProfile.username) {
          throw new Error('Perfil de usuario incompleto')
        }
        
        // Asegurar que roles sea un array
        if (!Array.isArray(userProfile.roles)) {
          userProfile.roles = userProfile.roles ? [userProfile.roles] : []
        }
        
        user.value = userProfile
        console.log('✅ Usuario autenticado recuperado:', userProfile.username)
        
      } catch (profileError) {
        console.warn('❌ Error al validar token con servidor:', profileError.message)
        
        // Si es error 401, el token no es válido en el servidor
        if (profileError.message.includes('401') || profileError.message.includes('unauthorized')) {
          console.log('🧹 Token inválido en servidor, limpiando...')
          authService.clearAuthToken()
          user.value = null
        } else {
          // Si es otro tipo de error (red, etc), mantener el token pero sin usuario
          console.log('⚠️ Error de red, manteniendo token pero sin cargar usuario')
          user.value = null
        }
      }
      
    } catch (error) {
      console.error('💥 Error crítico al inicializar autenticación:', error)
      // En caso de error crítico, limpiar todo
      authService.clearAuthToken()
      user.value = null
    } finally {
      isLoading.value = false
      isInitialized.value = true
      console.log('🏁 Inicialización de auth completada')
    }
  }


  // ✅ NUEVO: Función para verificar si el usuario puede acceder a una ruta
  function canAccessRoute(routeMeta) {
    // Si no requiere autenticación, permitir acceso
    if (!routeMeta.requiresAuth) {
      return true
    }

    // Si requiere autenticación pero no está autenticado
    if (!isAuthenticated.value) {
      return false
    }

    // Si requiere email verificado pero no lo está
    if (routeMeta.requiresEmailVerified && !isEmailConfirmed.value) {
      return false
    }

    // Si es una ruta solo para invitados y está autenticado
    if (routeMeta.requiresGuest && isAuthenticated.value) {
      return false
    }

    return true
  }

  return {
    // Estado
    user,
    isLoading,
    error,
    isInitialized, // ✅ NUEVO
    
    // Getters
    isAuthenticated,
    userRoles,
    isAdmin,
    isEmailConfirmed,
    canAccessProtectedRoutes, // ✅ NUEVO
    
    // Actions
    login,
    register,
    logout,
    confirmEmail,
    resendVerificationEmail,
    clearError,
    initializeAuth,
    canAccessRoute // ✅ NUEVO
  }
})