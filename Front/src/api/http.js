// Servicio HTTP para comunicarse con el backend - VERSIÓN MEJORADA
import { apiConfig, endpoints } from './config.js'

class HttpService {
  constructor() {
    this.baseURL = apiConfig.baseURL
    this.timeout = apiConfig.timeout
    this.isRefreshing = false
    this.failedQueue = []
  }

  // Método para obtener el token JWT del localStorage
  getAuthToken() {
    return localStorage.getItem('ffl_token')
  }

  // Método para establecer el token JWT
  setAuthToken(token) {
    localStorage.setItem('ffl_token', token)
  }

  // Método para limpiar el token
  clearAuthToken() {
    localStorage.removeItem('ffl_token')
  }

  // 🆕 NUEVO: Método para manejar logout automático
  handleLogout() {
    this.clearAuthToken()
    
    // Obtener el store de auth de forma dinámica para evitar dependencias circulares
    import('../store/auth.js').then(({ useAuthStore }) => {
      const authStore = useAuthStore()
      authStore.user = null
      authStore.error = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
    })

    // Redirigir al login solo si no estamos ya en una página pública
    const currentPath = window.location.pathname
    const publicPaths = ['/', '/login', '/register', '/forgot-password', '/verify-email', '/confirm-email', '/support', '/about']
    
    if (!publicPaths.includes(currentPath) && !currentPath.startsWith('/reset-password')) {
      // Guardar la ruta actual para redirigir después del login
      const redirectPath = currentPath !== '/login' ? currentPath : null
      
      // Usar Vue Router de forma dinámica
      import('../router/index.js').then(({ default: router }) => {
        router.push({
          name: 'Login',
          query: redirectPath ? { redirect: redirectPath } : {}
        })
      })
    }
  }

  // 🆕 NUEVO: Método para verificar si el token está expirado
  isTokenExpired(token) {
    if (!token) return true
    
    try {
      // Verificar que el token tenga el formato correcto (3 partes separadas por punto)
      const parts = token.split('.')
      if (parts.length !== 3) {
        console.warn('Token JWT malformado')
        return true
      }
      
      // Decodificar el payload
      const payload = JSON.parse(atob(parts[1]))
      
      if (!payload.exp) {
        console.warn('Token sin fecha de expiración')
        return true
      }
      
      const currentTime = Date.now() / 1000
      const expirationTime = payload.exp
      
      // Verificar si el token expira en los próximos 30 segundos (buffer de seguridad)
      const isExpired = expirationTime < (currentTime + 30)
      
      if (isExpired) {
        console.warn('🔴 Token expirado detectado')
      } else {
        console.log('✅ Token válido, expira en:', Math.round((expirationTime - currentTime) / 60), 'minutos')
      }
      
      return isExpired
    } catch (error) {
      console.error('Error al verificar token:', error)
      return true
    }
  }


  // Método base para hacer peticiones - MEJORADO
  async request(url, options = {}) {
    const token = this.getAuthToken()
    
    // 🔥 VERIFICAR TOKEN ANTES DE HACER LA PETICIÓN
    if (token && this.isTokenExpired(token)) {
      console.warn('🔴 Token expirado detectado, limpiando sesión...')
      this.handleLogout()
      throw new Error('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.')
    }
    
    const config = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
      },
      ...options
    }

    try {
      const response = await fetch(`${this.baseURL}${url}`, config)
      
      // 🔥 MANEJO MEJORADO DE RESPUESTAS 401
      if (response.status === 401) {
        console.warn('🔴 Respuesta 401 recibida del servidor')
        
        // Verificar si es un endpoint de autenticación (login, register, etc.)
        const authEndpoints = ['/auth/signin', '/auth/signup', '/auth/confirm', '/auth/forgot-password', '/auth/reset-password']
        const isAuthEndpoint = authEndpoints.some(endpoint => url.includes(endpoint))
        
        if (!isAuthEndpoint) {
          // Solo hacer logout automático si NO es un endpoint de autenticación
          this.handleLogout()
          throw new Error('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.')
        }
      }

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || `Error ${response.status}: ${response.statusText}`)
      }

      return data
    } catch (error) {
      // 🔥 MANEJO MEJORADO DE ERRORES DE RED
      if (error.name === 'TypeError' && error.message.includes('fetch')) {
        throw new Error('Error de conexión. Verifica tu conexión a internet y vuelve a intentarlo.')
      }
      
      console.error('Error en petición HTTP:', error)
      throw error
    }
  }

  // Métodos específicos (sin cambios)
  async get(url) {
    return this.request(url, { method: 'GET' })
  }

  async post(url, body) {
    return this.request(url, {
      method: 'POST',
      body: JSON.stringify(body)
    })
  }

  async put(url, body) {
    return this.request(url, {
      method: 'PUT',
      body: JSON.stringify(body)
    })
  }

  async delete(url) {
    return this.request(url, { method: 'DELETE' })
  }
}

// Exportar una instancia única
export const httpService = new HttpService()

// Exportar también los endpoints para fácil acceso
export { endpoints }