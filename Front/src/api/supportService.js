// Servicio para manejar tickets de soporte
import { httpService, endpoints } from './http.js'

export const supportService = {
  // Crear un ticket de soporte
  async createSupportTicket(ticketData) {
    try {
      const response = await httpService.post(endpoints.support.ticket, ticketData)
      return response
    } catch (error) {
      throw new Error(error.message || 'Error al enviar el ticket de soporte')
    }
  },

  // Obtener categorías de soporte disponibles
  async getSupportCategories() {
    try {
      const response = await httpService.get(endpoints.support.categories)
      return response
    } catch (error) {
      console.error('Error al obtener categorías de soporte:', error)
      return {}
    }
  },

  // Obtener información de contacto
  async getContactInfo() {
    try {
      const response = await httpService.get(endpoints.support.contactInfo)
      return response
    } catch (error) {
      console.error('Error al obtener información de contacto:', error)
      return {}
    }
  },

  // Obtener estado del sistema de soporte
  async getSupportStatus() {
    try {
      const response = await httpService.get(endpoints.support.status)
      return response
    } catch (error) {
      console.error('Error al obtener estado del soporte:', error)
      return { available: true }
    }
  }
}