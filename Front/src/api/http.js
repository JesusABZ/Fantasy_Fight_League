// Servicio HTTP para comunicarse con el backend
import { apiConfig, endpoints } from './config.js'

class HttpService {
  constructor() {
    this.baseURL = apiConfig.baseURL
    this.timeout = apiConfig.timeout
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

  // Método base para hacer peticiones
  async request(url, options = {}) {
    const token = this.getAuthToken()
    
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
      
      // Si es 401 (no autorizado), limpiar token y redirigir
      if (response.status === 401) {
        this.clearAuthToken()
        // Aquí podrías redirigir al login
        throw new Error('Sesión expirada')
      }

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Error en la petición')
      }

      return data
    } catch (error) {
      console.error('Error en petición HTTP:', error)
      throw error
    }
  }

  // Métodos específicos
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