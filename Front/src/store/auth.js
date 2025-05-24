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
  const userRoles = computed(() => user.value?.roles || [])
  const isAdmin = computed(() => userRoles.value.includes('ROLE_ADMIN'))
  const isEmailConfirmed = computed(() => user.value?.emailConfirmed || false)

  // Actions
  async function login(credentials) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authService.login(credentials)
      
      // Guardar datos del usuario
      user.value = {
        id: response.id,
        username: response.username,
        email: response.email,
        emailConfirmed: response.emailConfirmed,
        roles: response.roles
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
      const response = await authService.register(userData)
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
      await authService.logout()
    } catch (err) {
      console.error('Error durante logout:', err)
    } finally {
      // Limpiar estado local
      user.value = null
      error.value = null
      isLoading.value = false
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

  function clearError() {
    error.value = null
  }

  // Inicializar el estado cuando se carga la app
  function initializeAuth() {
    // Verificar si hay un token guardado
    if (authService.isAuthenticated()) {
      // Aquí podrías hacer una llamada para obtener los datos del usuario
      // o simplemente marcar como autenticado y obtener los datos después
      console.log('Usuario previamente autenticado encontrado')
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
    clearError,
    initializeAuth
  }
})