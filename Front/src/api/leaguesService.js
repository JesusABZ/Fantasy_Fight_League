// Servicio para manejar ligas
import { httpService, endpoints } from './http.js'

export const leaguesService = {
  // Obtener mis ligas (donde soy miembro)
  async getMyLeagues() {
    try {
      const response = await httpService.get(endpoints.leagues.myLeagues)
      return response || []
    } catch (error) {
      console.error('Error al obtener mis ligas:', error)
      throw new Error(error.message || 'Error al obtener las ligas')
    }
  },

  // Obtener ligas públicas activas
  async getPublicLeagues() {
    try {
      const response = await httpService.get(endpoints.leagues.public)
      return response || []
    } catch (error) {
      console.error('Error al obtener ligas públicas:', error)
      throw new Error(error.message || 'Error al obtener ligas públicas')
    }
  },

  // Unirse a una liga pública
  async joinPublicLeague(leagueId) {
    try {
      const response = await httpService.post(endpoints.leagues.join(leagueId))
      return response
    } catch (error) {
      console.error(`Error al unirse a la liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al unirse a la liga')
    }
  },

  // Unirse a una liga privada con código
  async joinPrivateLeague(invitationCode) {
    try {
      const response = await httpService.post(endpoints.leagues.joinPrivate, {
        invitationCode: invitationCode.trim().toUpperCase()
      })
      return response
    } catch (error) {
      console.error(`Error al unirse a liga privada con código ${invitationCode}:`, error)
      throw new Error(error.message || 'Error al unirse a la liga privada')
    }
  },

  // Crear una liga privada
  async createPrivateLeague(leagueData) {
    try {
      const response = await httpService.post(endpoints.leagues.createPrivate, leagueData)
      return response
    } catch (error) {
      console.error('Error al crear liga privada:', error)
      throw new Error(error.message || 'Error al crear la liga')
    }
  },

  // Obtener detalles de una liga específica
  async getLeagueById(leagueId) {
    try {
      const response = await httpService.get(endpoints.leagues.byId(leagueId))
      return response
    } catch (error) {
      console.error(`Error al obtener liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al obtener la liga')
    }
  },

  // Salir de una liga
  async leaveLeague(leagueId) {
    try {
      const response = await httpService.delete(endpoints.leagues.leave(leagueId))
      return response
    } catch (error) {
      console.error(`Error al salir de la liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al salir de la liga')
    }
  }
}