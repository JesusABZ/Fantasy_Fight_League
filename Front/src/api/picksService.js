// Front/src/api/picksService.js
import { httpService, endpoints } from './http.js'

export const picksService = {
  // Crear o actualizar picks
  async createOrUpdatePick(pickData) {
    try {
      console.log('🎯 Enviando picks al backend:', pickData)
      
      const response = await httpService.post(endpoints.picks.create, pickData)
      
      console.log('✅ Picks guardados exitosamente:', response)
      return response
    } catch (error) {
      console.error('❌ Error al guardar picks:', error)
      throw new Error(error.message || 'Error al guardar los picks')
    }
  },

  // Obtener mi pick para una liga y evento específico
  async getMyPick(leagueId, eventId) {
    try {
      console.log('📋 Obteniendo mi pick para liga:', leagueId, 'evento:', eventId)
      
      const response = await httpService.get(`${endpoints.picks.myPick}?leagueId=${leagueId}&eventId=${eventId}`)
      
      console.log('✅ Pick obtenido:', response)
      return response
    } catch (error) {
      console.error('❌ Error al obtener pick:', error)
      throw new Error(error.message || 'Error al obtener el pick')
    }
  },

  // Obtener todos mis picks en una liga
  async getMyPicksInLeague(leagueId) {
    try {
      const response = await httpService.get(endpoints.picks.myPicks(leagueId))
      return response || []
    } catch (error) {
      console.error(`Error al obtener mis picks en liga ${leagueId}:`, error)
      throw new Error(error.message || 'Error al obtener picks')
    }
  },

  // Eliminar un pick
  async deletePick(pickId) {
    try {
      const response = await httpService.delete(`/picks/${pickId}`)
      return response
    } catch (error) {
      console.error(`Error al eliminar pick ${pickId}:`, error)
      throw new Error(error.message || 'Error al eliminar el pick')
    }
  }
}