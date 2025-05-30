// Front/src/api/leagueDetailService.js
import { httpService, endpoints } from './http.js'

export const leagueDetailService = {
  // Obtener detalles completos de una liga específica
  async getLeagueDetails(leagueId) {
    try {
      const response = await httpService.get(`/leagues/${leagueId}`)
      return response
    } catch (error) {
      console.error(`Error al obtener detalles de liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al obtener los detalles de la liga')
    }
  },

  // Obtener clasificación global de una liga
  async getGlobalLeaderboard(leagueId) {
    try {
      const response = await httpService.get(endpoints.leaderboard.global(leagueId))
      return response
    } catch (error) {
      console.error(`Error al obtener clasificación global de liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al obtener la clasificación')
    }
  },

  // Obtener clasificación de un evento específico
  async getEventLeaderboard(leagueId, eventId) {
    try {
      const response = await httpService.get(endpoints.leaderboard.event(leagueId, eventId))
      return response
    } catch (error) {
      console.error(`Error al obtener clasificación del evento ${eventId}:`, error)
      throw new Error(error.message || 'Error al obtener la clasificación del evento')
    }
  },

  // Obtener mi posición en una liga
  async getMyPosition(leagueId) {
    try {
      const response = await httpService.get(endpoints.leaderboard.myPosition(leagueId))
      return response
    } catch (error) {
      console.error(`Error al obtener mi posición en liga ${leagueId}:`, error)
      // Devolver datos por defecto en caso de error
      return {
        position: null,
        totalPoints: 0,
        totalParticipants: 0,
        eventsParticipated: 0
      }
    }
  },

  // Obtener mi historial de picks en una liga
  async getMyHistory(leagueId) {
    try {
      const response = await httpService.get(endpoints.leaderboard.myHistory(leagueId))
      return response
    } catch (error) {
      console.error(`Error al obtener historial en liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al obtener el historial')
    }
  }
}