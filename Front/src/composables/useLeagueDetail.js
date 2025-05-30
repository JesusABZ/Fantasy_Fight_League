// Front/src/composables/useLeagueDetail.js
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth.js'
import { leagueDetailService, eventsService, leaderboardService } from '../api/index.js'
import { useDateFormatter } from './useDateFormatter.js'

export function useLeagueDetail(leagueId) {
  const router = useRouter()
  const authStore = useAuthStore()
  const { formatEventDate } = useDateFormatter()

  // Estados reactivos
  const currentLeague = ref(null)
  const currentEvent = ref(null)
  const previousEvent = ref(null)
  const globalLeaderboard = ref([])
  const currentEventLeaderboard = ref([])
  const previousEventLeaderboard = ref([])
  const currentUserPicks = ref([])
  const previousUserPicks = ref([])
  const myPosition = ref(null)
  const myHistory = ref(null)

  // Estados de carga
  const isLoadingLeague = ref(false)
  const isLoadingGlobal = ref(false)
  const isLoadingCurrentEvent = ref(false)
  const isLoadingPreviousEvent = ref(false)
  
  // Estados UI
  const activeTab = ref('global')
  const selectedFighter = ref(null)
  const showLeagueInfo = ref(false)
  const showNotificationModal = ref(false)
  const notificationText = ref('')

  // Computed properties
  const user = computed(() => authStore.user)
  
  const isPublicLeague = computed(() => {
    return currentLeague.value?.type === 'PUBLIC'
  })

  const isPrivateLeague = computed(() => {
    return currentLeague.value?.type === 'PRIVATE'
  })

  const canMakePicks = computed(() => {
    if (!currentEvent.value) return false
    
    // Verificar si los picks están abiertos para el evento
    const now = new Date()
    const eventStart = new Date(currentEvent.value.startDate)
    const picksDeadline = new Date(currentEvent.value.picksDeadline || eventStart.getTime() - (24 * 60 * 60 * 1000))
    
    return now < picksDeadline && currentEvent.value.status !== 'COMPLETED'
  })

  const formattedEventDate = computed(() => {
    if (!currentEvent.value?.startDate) return ''
    return formatEventDate(currentEvent.value.startDate)
  })

  const formattedPreviousEventDate = computed(() => {
    if (!previousEvent.value?.startDate) return ''
    return formatEventDate(previousEvent.value.startDate)
  })

  // Funciones para cargar datos
  async function loadLeagueDetails() {
    if (!leagueId) return
    
    isLoadingLeague.value = true
    try {
      console.log('🏆 Cargando detalles de la liga:', leagueId)
      
      const leagueData = await leagueDetailService.getLeagueDetails(leagueId)
      currentLeague.value = leagueData
      
      console.log('✅ Liga cargada:', leagueData)
      
      // Establecer tab inicial según el tipo de liga
      if (leagueData.type === 'PUBLIC') {
        activeTab.value = 'current'
      } else {
        activeTab.value = 'global'
      }
      
    } catch (error) {
      console.error('❌ Error al cargar liga:', error)
      throw error
    } finally {
      isLoadingLeague.value = false
    }
  }

  async function loadEvents() {
    try {
      console.log('📅 Cargando eventos...')
      
      // Para ligas públicas, el evento está asociado directamente
      if (currentLeague.value?.event) {
        currentEvent.value = currentLeague.value.event
        console.log('✅ Evento de liga pública cargado:', currentEvent.value)
        return
      }
      
      // Para ligas privadas, obtener el próximo evento
      const nextEvent = await eventsService.getNextEvent()
      if (nextEvent) {
        currentEvent.value = nextEvent
        console.log('✅ Próximo evento cargado:', nextEvent)
      }
      
      // TODO: Implementar lógica para evento anterior
      // Por ahora simulamos que no hay evento anterior
      previousEvent.value = null
      
    } catch (error) {
      console.error('❌ Error al cargar eventos:', error)
    }
  }

  async function loadGlobalLeaderboard() {
    if (!currentLeague.value || isPublicLeague.value) return
    
    isLoadingGlobal.value = true
    try {
      console.log('🏆 Cargando clasificación global...')
      
      const response = await leaderboardService.getGlobalLeaderboard(leagueId)
      
      // Procesar los datos para el formato esperado por el componente
      const processedLeaderboard = response.leaderboard.map((entry, index) => ({
        id: entry.userId,
        username: entry.username,
        totalPoints: entry.totalPoints || 0,
        lastEventPoints: 0, // TODO: Implementar último evento
        eventsParticipated: entry.eventsParticipated || 0,
        isCurrentUser: entry.userId === user.value?.id,
        position: index + 1
      }))
      
      globalLeaderboard.value = processedLeaderboard
      console.log('✅ Clasificación global cargada:', processedLeaderboard.length, 'participantes')
      
    } catch (error) {
      console.error('❌ Error al cargar clasificación global:', error)
      globalLeaderboard.value = []
    } finally {
      isLoadingGlobal.value = false
    }
  }

  async function loadCurrentEventLeaderboard() {
    if (!currentLeague.value || !currentEvent.value) return
    
    isLoadingCurrentEvent.value = true
    try {
      console.log('⚡ Cargando clasificación del evento actual...')
      
      const response = await leaderboardService.getEventLeaderboard(leagueId, currentEvent.value.id)
      
      // Procesar los datos para el formato esperado
      const processedLeaderboard = response.leaderboard.map((entry, index) => ({
        id: entry.userId,
        username: entry.username,
        eventPoints: entry.eventPoints || 0,
        fightersSelected: entry.selectedFighters?.length || 0,
        isCurrentUser: entry.userId === user.value?.id,
        position: index + 1
      }))
      
      currentEventLeaderboard.value = processedLeaderboard
      console.log('✅ Clasificación del evento cargada:', processedLeaderboard.length, 'participantes')
      
    } catch (error) {
      console.error('❌ Error al cargar clasificación del evento:', error)
      currentEventLeaderboard.value = []
    } finally {
      isLoadingCurrentEvent.value = false
    }
  }

  async function loadMyPosition() {
    if (!currentLeague.value) return
    
    try {
      console.log('📊 Cargando mi posición...')
      
      const position = await leaderboardService.getMyPosition(leagueId)
      myPosition.value = position
      
      console.log('✅ Mi posición cargada:', position)
      
    } catch (error) {
      console.error('❌ Error al cargar mi posición:', error)
      myPosition.value = {
        position: null,
        totalPoints: 0,
        totalParticipants: 0,
        eventsParticipated: 0
      }
    }
  }

  async function loadMyHistory() {
    if (!currentLeague.value) return
    
    try {
      console.log('📈 Cargando mi historial...')
      
      const history = await leaderboardService.getMyHistory(leagueId)
      myHistory.value = history
      
      // Buscar picks del evento actual
      const currentEventPick = history.history?.find(pick => 
        pick.eventId === currentEvent.value?.id
      )
      
      if (currentEventPick) {
        // Simular estructura de picks (TODO: mejorar con datos reales)
        currentUserPicks.value = currentEventPick.fighterNames?.map((name, index) => ({
          id: index + 1,
          name: name,
          record: '0-0', // TODO: obtener datos reales
          cost: 50000, // TODO: obtener datos reales
          points: 0, // TODO: obtener datos reales
          weightClass: 'Unknown' // TODO: obtener datos reales
        })) || []
      }
      
      console.log('✅ Mi historial cargado:', history)
      
    } catch (error) {
      console.error('❌ Error al cargar mi historial:', error)
      myHistory.value = null
      currentUserPicks.value = []
    }
  }

  // Funciones de utilidad
  function getPositionClass(position) {
    if (position === 1) return 'gold'
    if (position === 2) return 'silver'
    if (position === 3) return 'bronze'
    return ''
  }

  function getFighterInitials(name) {
    return name.split(' ').map(word => word[0]).join('').toUpperCase()
  }

  function showFighterDetails(fighter) {
    selectedFighter.value = fighter
  }

  function displayNotification(message) {
    notificationText.value = message
    showNotificationModal.value = true
    setTimeout(() => {
      showNotificationModal.value = false
    }, 3000)
  }

  function hideNotification() {
    showNotificationModal.value = false
  }

  function goBackToDashboard() {
    router.push('/dashboard')
  }

  function goToPicksSelection() {
    if (!currentEvent.value) {
      displayNotification('No hay evento disponible para hacer picks')
      return
    }
    
    router.push(`/league/${leagueId}/picks/${currentEvent.value.id}`)
  }

  // Función principal para cargar todos los datos
  async function loadAllData() {
    try {
      console.log('🚀 Iniciando carga de datos de la liga...')
      
      // 1. Cargar detalles de la liga primero
      await loadLeagueDetails()
      
      // 2. Cargar eventos
      await loadEvents()
      
      // 3. Cargar datos en paralelo
      await Promise.all([
        loadGlobalLeaderboard(),
        loadCurrentEventLeaderboard(),
        loadMyPosition(),
        loadMyHistory()
      ])
      
      console.log('✅ Todos los datos de la liga cargados correctamente')
      
    } catch (error) {
      console.error('💥 Error al cargar datos de la liga:', error)
      displayNotification('Error al cargar los datos de la liga')
    }
  }

  // Función para recargar datos específicos según la pestaña activa
  async function refreshTabData(tabName) {
    switch (tabName) {
      case 'global':
        await loadGlobalLeaderboard()
        break
      case 'current':
        await loadCurrentEventLeaderboard()
        break
      case 'previous':
        // TODO: Implementar carga de evento anterior
        break
    }
  }

  return {
    // Estados
    currentLeague,
    currentEvent,
    previousEvent,
    globalLeaderboard,
    currentEventLeaderboard,
    previousEventLeaderboard,
    currentUserPicks,
    previousUserPicks,
    myPosition,
    myHistory,
    
    // Estados de carga
    isLoadingLeague,
    isLoadingGlobal,
    isLoadingCurrentEvent,
    isLoadingPreviousEvent,
    
    // Estados UI
    activeTab,
    selectedFighter,
    showLeagueInfo,
    showNotificationModal,
    notificationText,
    
    // Computed
    isPublicLeague,
    isPrivateLeague,
    canMakePicks,
    formattedEventDate,
    formattedPreviousEventDate,
    
    // Funciones
    loadAllData,
    refreshTabData,
    getPositionClass,
    getFighterInitials,
    showFighterDetails,
    displayNotification,
    hideNotification,
    goBackToDashboard,
    goToPicksSelection
  }
}