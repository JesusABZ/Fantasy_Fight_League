// Servicio para manejar eventos UFC
import { httpService, endpoints } from './http.js'

export const eventsService = {
  // Obtener el próximo evento
  async getNextEvent() {
    try {
      // Usar el endpoint correcto que devuelve directamente el próximo evento
      const response = await httpService.get('/events/next')
      return response || null
    } catch (error) {
      console.error('Error al obtener el próximo evento:', error)
      throw new Error(error.message || 'Error al obtener el próximo evento')
    }
  },

  // Obtener todos los eventos próximos
  async getUpcomingEvents() {
    try {
      const response = await httpService.get(endpoints.events.upcoming)
      return response || []
    } catch (error) {
      console.error('Error al obtener eventos próximos:', error)
      throw new Error(error.message || 'Error al obtener eventos próximos')
    }
  },

  // Obtener todos los eventos
  async getAllEvents() {
    try {
      const response = await httpService.get(endpoints.events.all)
      return response || []
    } catch (error) {
      console.error('Error al obtener todos los eventos:', error)
      throw new Error(error.message || 'Error al obtener eventos')
    }
  },

  // Obtener evento por ID
  async getEventById(id) {
    try {
      const response = await httpService.get(endpoints.events.byId(id))
      return response
    } catch (error) {
      console.error(`Error al obtener evento ${id}:`, error)
      throw new Error(error.message || 'Error al obtener el evento')
    }
  },

  // Obtener luchadores de un evento
  async getEventFighters(id) {
    try {
      const response = await httpService.get(endpoints.events.fighters(id))
      return response || []
    } catch (error) {
      console.error(`Error al obtener luchadores del evento ${id}:`, error)
      throw new Error(error.message || 'Error al obtener luchadores del evento')
    }
  }
}