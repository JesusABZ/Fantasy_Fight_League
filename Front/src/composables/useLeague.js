// composables/useLeagues.js
import { ref, computed } from 'vue'
import { useNotifications } from './useNotifications'
import { useAuth } from './useAuth'
// import { leaguesService } from '@/api' // Cuando esté creado

export function useLeagues() {
  const { common: notifications, handleApiError } = useNotifications()
  const { user } = useAuth()

  // Estado
  const leagues = ref([])
  const publicLeagues = ref([])
  const currentLeague = ref(null)
  const leaderboard = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  // Computed
  const myLeagues = computed(() => {
    return leagues.value.filter(league => 
      league.members?.some(member => member.id === user.value?.id)
    )
  })

  const availablePublicLeagues = computed(() => {
    return publicLeagues.value.filter(league => 
      !league.members?.some(member => member.id === user.value?.id)
    )
  })

  const userPositionInLeague = computed(() => (leagueId) => {
    const league = leagues.value.find(l => l.id === leagueId)
    if (!league || !league.leaderboard) return null
    
    const userEntry = league.leaderboard.find(entry => entry.userId === user.value?.id)
    return userEntry?.position || null
  })

  const userPointsInLeague = computed(() => (leagueId) => {
    const league = leagues.value.find(l => l.id === leagueId)
    if (!league || !league.leaderboard) return 0
    
    const userEntry = league.leaderboard.find(entry => entry.userId === user.value?.id)
    return userEntry?.points || 0
  })

  // Métodos para obtener datos
  const fetchMyLeagues = async () => {
    isLoading.value = true
    error.value = null

    try {
      // TODO: Implementar cuando tengas el servicio
      // const response = await leaguesService.getMyLeagues()
      
      // Datos simulados por ahora
      leagues.value = [
        {
          id: 1,
          name: 'Liga Oficina',
          description: 'Liga entre compañeros de trabajo',
          type: 'PRIVATE',
          memberCount: 12,
          userPosition: 1,
          userPoints: 1890,
          members: [{ id: user.value?.id }],
          leaderboard: [
            { userId: user.value?.id, position: 1, points: 1890 }
          ]
        }
      ]
      
      return leagues.value
    } catch (err) {
      error.value = err.message
      handleApiError(err, 'Error al cargar tus ligas')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchPublicLeagues = async () => {
    isLoading.value = true
    error.value = null

    try {
      // TODO: Implementar servicio
      // const response = await leaguesService.getPublicLeagues()
      
      publicLeagues.value = [
        {
          id: 4,
          name: 'UFC Vegas 107 - Élite',
          description: 'Liga para los mejores fighters',
          memberCount: 89,
          eventName: 'UFC Vegas 107'
        }
      ]
      
      return publicLeagues.value
    } catch (err) {
      error.value = err.message
      handleApiError(err, 'Error al cargar ligas públicas')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchLeagueDetail = async (leagueId) => {
    isLoading.value = true
    error.value = null

    try {
      // TODO: Implementar servicio
      // const response = await leaguesService.getLeagueById(leagueId)
      
      currentLeague.value = {
        id: leagueId,
        name: 'Liga de Ejemplo',
        type: 'PRIVATE',
        memberCount: 12
      }
      
      return currentLeague.value
    } catch (err) {
      error.value = err.message
      handleApiError(err, 'Error al cargar los detalles de la liga')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchLeaderboard = async (leagueId, eventId = null) => {
    isLoading.value = true
    error.value = null

    try {
      // TODO: Implementar servicio
      // const response = await leaguesService.getLeaderboard(leagueId, eventId)
      
      leaderboard.value = [
        {
          id: 1,
          username: 'usuario_prueba2',
          totalPoints: 2890,
          position: 1,
          isCurrentUser: true
        }
      ]
      
      return leaderboard.value
    } catch (err) {
      error.value = err.message
      handleApiError(err, 'Error al cargar la clasificación')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Métodos de acciones
  const joinPublicLeague = async (leagueId) => {
    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // await leaguesService.joinPublicLeague(leagueId)
      
      const league = publicLeagues.value.find(l => l.id === leagueId)
      if (league) {
        notifications.leagueJoined(league.name)
        
        // Mover de públicas a mis ligas
        leagues.value.push({
          ...league,
          userPosition: league.memberCount + 1,
          userPoints: 0
        })
        
        publicLeagues.value = publicLeagues.value.filter(l => l.id !== leagueId)
      }
      
      return true
    } catch (err) {
      handleApiError(err, 'Error al unirse a la liga')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const joinPrivateLeague = async (inviteCode) => {
    if (!inviteCode?.trim()) {
      notifications.warning('Ingresa un código de invitación válido')
      return false
    }

    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // const response = await leaguesService.joinPrivateLeague(inviteCode)
      
      const newLeague = {
        id: Date.now(),
        name: 'Nueva Liga Privada',
        description: 'Liga recién agregada',
        type: 'PRIVATE',
        memberCount: 5,
        userPosition: 1,
        userPoints: 0
      }
      
      leagues.value.push(newLeague)
      notifications.success(`¡Te has unido a la liga con código: ${inviteCode}!`)
      
      return newLeague
    } catch (err) {
      handleApiError(err, 'Código de invitación inválido')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const createPrivateLeague = async (leagueData) => {
    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // const response = await leaguesService.createPrivateLeague(leagueData)
      
      const newLeague = {
        id: Date.now(),
        ...leagueData,
        type: 'PRIVATE',
        memberCount: 1,
        userPosition: 1,
        userPoints: 0,
        inviteCode: generateInviteCode()
      }
      
      leagues.value.push(newLeague)
      notifications.success('¡Liga creada correctamente!')
      
      return newLeague
    } catch (err) {
      handleApiError(err, 'Error al crear la liga')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const leaveLeague = async (leagueId) => {
    const confirmed = await notifications.confirmAction(
      '¿Estás seguro de que quieres abandonar esta liga?',
      'Abandonar',
      'Cancelar'
    )

    if (!confirmed) return false

    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // await leaguesService.leaveLeague(leagueId)
      
      leagues.value = leagues.value.filter(l => l.id !== leagueId)
      notifications.success('Has abandonado la liga')
      
      return true
    } catch (err) {
      handleApiError(err, 'Error al abandonar la liga')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Utilidades
  const generateInviteCode = () => {
    return Math.random().toString(36).substring(2, 10).toUpperCase()
  }

  const getLeagueById = (leagueId) => {
    return leagues.value.find(league => league.id === leagueId)
  }

  const getUserRankInLeague = (leagueId) => {
    const position = userPositionInLeague.value(leagueId)
    if (!position) return 'N/A'
    
    if (position === 1) return '🥇 1º'
    if (position === 2) return '🥈 2º'
    if (position === 3) return '🥉 3º'
    return `#${position}`
  }

  const isUserInLeague = (leagueId) => {
    return leagues.value.some(league => league.id === leagueId)
  }

  // Limpiar estado
  const clearError = () => {
    error.value = null
  }

  const reset = () => {
    leagues.value = []
    publicLeagues.value = []
    currentLeague.value = null
    leaderboard.value = []
    error.value = null
  }

  return {
    // Estado
    leagues,
    publicLeagues,
    currentLeague,
    leaderboard,
    isLoading,
    error,

    // Computed
    myLeagues,
    availablePublicLeagues,
    userPositionInLeague,
    userPointsInLeague,

    // Métodos de datos
    fetchMyLeagues,
    fetchPublicLeagues,
    fetchLeagueDetail,
    fetchLeaderboard,

    // Métodos de acciones
    joinPublicLeague,
    joinPrivateLeague,
    createPrivateLeague,
    leaveLeague,

    // Utilidades
    getLeagueById,
    getUserRankInLeague,
    isUserInLeague,
    clearError,
    reset
  }
}