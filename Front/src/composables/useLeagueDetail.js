// Front/src/composables/useLeagueDetail.js
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth.js'
import { useDateFormatter } from './useDateFormatter.js'
import { leagueDetailService, eventsService, leaderboardService, leaguesService, picksService } from '../api/index.js'

export function useLeagueDetail(leagueId) {
  const router = useRouter()
  const authStore = useAuthStore()
  const { formatEventDate } = useDateFormatter()

  // Estados reactivos
  const currentLeague = ref(null)
  const currentEvent = ref(null)
  const previousEvent = ref(null) // 🆕 Evento anterior
  const globalLeaderboard = ref([])
  const currentEventLeaderboard = ref([])
  const previousEventLeaderboard = ref([]) // 🆕 Clasificación del evento anterior
  const currentUserPicks = ref([])
  const previousUserPicks = ref([]) // 🆕 Picks del evento anterior
  const myPosition = ref(null)
  const myHistory = ref(null)

  // Estados de carga
  const isLoadingLeague = ref(false)
  const isLoadingGlobal = ref(false)
  const isLoadingCurrentEvent = ref(false)
  const isLoadingPreviousEvent = ref(false) // 🆕 Loading evento anterior
  const isLeavingLeague = ref(false)
  
  // Estados UI
  const activeTab = ref('global')
  const selectedFighter = ref(null)
  const showLeagueInfo = ref(false)
  const showNotificationModal = ref(false)
  const notificationText = ref('')
  const showLeaveConfirmation = ref(false)

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

  // Verificar si el usuario puede salir de la liga
  const canLeaveLeague = computed(() => {
    if (!currentLeague.value || !user.value) return false
    
    // El creador no puede salir de su propia liga
    return currentLeague.value.creator?.id !== user.value.id
  })

  // 🆕 Verificar si hay evento anterior disponible
  const hasPreviousEvent = computed(() => {
    return !!previousEvent.value
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

  // 🔥 FUNCIÓN MEJORADA: Cargar eventos actual y anterior
  async function loadEvents() {
    try {
      console.log('📅 Cargando eventos...')
      
      // Para ligas públicas, el evento está asociado directamente
      if (currentLeague.value?.event) {
        currentEvent.value = currentLeague.value.event
        console.log('✅ Evento de liga pública cargado:', currentEvent.value)
        
        // Para ligas públicas no hay evento anterior (son específicas de un evento)
        previousEvent.value = null
        return
      }
      
      // Para ligas privadas, obtener todos los eventos disponibles
      const allEvents = await eventsService.getAllEvents()
      console.log('📋 Todos los eventos obtenidos:', allEvents.length)
      
      if (allEvents.length === 0) {
        console.warn('⚠️ No se encontraron eventos')
        currentEvent.value = null
        previousEvent.value = null
        return
      }
      
      // Filtrar y ordenar eventos por fecha (más reciente primero)
      const sortedEvents = allEvents
        .filter(event => event.startDate) // Solo eventos con fecha
        .sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
      
      console.log('📊 Eventos ordenados por fecha:', sortedEvents.map(e => ({
        name: e.name,
        date: e.startDate,
        status: e.status
      })))
      
      // Determinar evento actual y anterior
      const now = new Date()
      
      // Buscar el próximo evento o el evento actual
      let currentEventIndex = -1
      for (let i = 0; i < sortedEvents.length; i++) {
        const eventDate = new Date(sortedEvents[i].startDate)
        if (eventDate >= now || sortedEvents[i].status === 'UPCOMING' || sortedEvents[i].status === 'LIVE') {
          currentEventIndex = i
          break
        }
      }
      
      // Si no hay eventos futuros, tomar el más reciente
      if (currentEventIndex === -1 && sortedEvents.length > 0) {
        currentEventIndex = 0
      }
      
      // Establecer evento actual
      if (currentEventIndex >= 0) {
        currentEvent.value = sortedEvents[currentEventIndex]
        console.log('✅ Evento actual establecido:', currentEvent.value.name)
      }
      
      // Establecer evento anterior (el siguiente en la lista)
      if (currentEventIndex >= 0 && currentEventIndex < sortedEvents.length - 1) {
        previousEvent.value = sortedEvents[currentEventIndex + 1]
        console.log('✅ Evento anterior establecido:', previousEvent.value.name)
      } else {
        previousEvent.value = null
        console.log('ℹ️ No hay evento anterior disponible')
      }
      
    } catch (error) {
      console.error('❌ Error al cargar eventos:', error)
      currentEvent.value = null
      previousEvent.value = null
    }
  }

  async function loadGlobalLeaderboard() {
    if (!currentLeague.value || isPublicLeague.value) return
    
    isLoadingGlobal.value = true
    try {
      console.log('🏆 Cargando clasificación global...')
      
      const response = await leaderboardService.getGlobalLeaderboard(leagueId)
      
      // Procesar los datos para incluir toda la información de miembros
      const processedLeaderboard = response.leaderboard.map((entry, index) => ({
        id: entry.userId,
        username: entry.username,
        firstName: entry.firstName,
        lastName: entry.lastName,
        profileImageUrl: entry.profileImageUrl,
        totalPoints: entry.totalPoints || 0,
        lastEventPoints: entry.lastEventPoints || 0,
        eventsParticipated: entry.eventsParticipated || 0,
        averagePointsPerEvent: entry.averagePointsPerEvent || 0,
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
      console.log('⚡ Cargando clasificación del evento actual:', currentEvent.value.name)
      
      const response = await leaderboardService.getEventLeaderboard(leagueId, currentEvent.value.id)
      
      // Procesar los datos para el formato esperado
      const processedLeaderboard = response.leaderboard.map((entry, index) => ({
        id: entry.userId,
        username: entry.username,
        firstName: entry.firstName,
        lastName: entry.lastName,
        profileImageUrl: entry.profileImageUrl,
        eventPoints: entry.eventPoints || 0,
        fightersSelected: entry.selectedFighters?.length || 0,
        totalCost: entry.totalCost || 0,
        remainingBudget: entry.remainingBudget || 0,
        selectedFighters: entry.selectedFighters || [],
        isCurrentUser: entry.userId === user.value?.id,
        position: index + 1
      }))
      
      currentEventLeaderboard.value = processedLeaderboard
      console.log('✅ Clasificación del evento actual cargada:', processedLeaderboard.length, 'participantes')
      
    } catch (error) {
      console.error('❌ Error al cargar clasificación del evento actual:', error)
      currentEventLeaderboard.value = []
    } finally {
      isLoadingCurrentEvent.value = false
    }
  }

  // 🆕 NUEVA FUNCIÓN: Cargar clasificación del evento anterior
  async function loadPreviousEventLeaderboard() {
    if (!currentLeague.value || !previousEvent.value || isPublicLeague.value) {
      console.log('ℹ️ No se puede cargar evento anterior: liga pública o sin evento anterior')
      return
    }
    
    isLoadingPreviousEvent.value = true
    try {
      console.log('📊 Cargando clasificación del evento anterior:', previousEvent.value.name)
      
      const response = await leaderboardService.getEventLeaderboard(leagueId, previousEvent.value.id)
      
      // Procesar los datos similar al evento actual
      const processedLeaderboard = response.leaderboard.map((entry, index) => ({
        id: entry.userId,
        username: entry.username,
        firstName: entry.firstName,
        lastName: entry.lastName,
        profileImageUrl: entry.profileImageUrl,
        eventPoints: entry.eventPoints || 0,
        fightersSelected: entry.selectedFighters?.length || 0,
        totalCost: entry.totalCost || 0,
        remainingBudget: entry.remainingBudget || 0,
        selectedFighters: entry.selectedFighters || [],
        isCurrentUser: entry.userId === user.value?.id,
        position: index + 1
      }))
      
      previousEventLeaderboard.value = processedLeaderboard
      console.log('✅ Clasificación del evento anterior cargada:', processedLeaderboard.length, 'participantes')
      
    } catch (error) {
      console.error('❌ Error al cargar clasificación del evento anterior:', error)
      previousEventLeaderboard.value = []
    } finally {
      isLoadingPreviousEvent.value = false
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

  // 🔥 FUNCIÓN MEJORADA: Cargar historial con picks de eventos actual y anterior
  async function loadMyHistory() {
  if (!currentLeague.value) return
  
  try {
    console.log('📈 Cargando mi historial...')
    
    const history = await leaderboardService.getMyHistory(leagueId)
    myHistory.value = history
    
    // 🆕 NUEVA LÓGICA: Buscar picks del evento actual con datos completos
    if (currentEvent.value) {
      await loadPicksForEvent(currentEvent.value, 'current')
    }
    
    // 🆕 NUEVA LÓGICA: Buscar picks del evento anterior con datos completos
    if (previousEvent.value) {
      await loadPicksForEvent(previousEvent.value, 'previous')
    }
    
    console.log('✅ Mi historial cargado:', history)
    
  } catch (error) {
    console.error('❌ Error al cargar mi historial:', error)
    myHistory.value = null
    currentUserPicks.value = []
    previousUserPicks.value = []
  }
}

// 🆕 NUEVA FUNCIÓN: Cargar picks específicos para un evento
async function loadPicksForEvent(event, eventType) {
    try {
      console.log(`🎯 Cargando picks para evento ${eventType}:`, event.name)
      
      // Obtener el pick específico para este evento y liga
      const pickData = await picksService.getMyPick(leagueId, event.id)
      
      if (pickData && pickData.selectedFighters && pickData.selectedFighters.length > 0) {
        // Mapear los luchadores con todos sus datos
        const fightersWithCompleteData = pickData.selectedFighters.map(fighter => ({
          id: fighter.id,
          name: fighter.name,
          record: fighter.record || 'N/A',
          nationality: fighter.nationality || 'Unknown',
          weightClass: fighter.weightClass || 'Unknown',
          imageUrl: fighter.imageUrl,
          price: fighter.price || 0,
          cost: fighter.price || 0, // Alias para compatibilidad
          points: 0, // Se actualizará con los resultados
          // Datos adicionales del pick
          pickId: pickData.id,
          totalCost: pickData.totalCost || 0,
          eventPoints: pickData.eventPoints || 0,
          isLocked: pickData.isLocked || false
        }))
        
        // Asignar a la variable correspondiente
        if (eventType === 'current') {
          currentUserPicks.value = fightersWithCompleteData
          console.log('✅ Picks del evento actual cargados:', fightersWithCompleteData.length)
        } else if (eventType === 'previous') {
          previousUserPicks.value = fightersWithCompleteData
          console.log('✅ Picks del evento anterior cargados:', fightersWithCompleteData.length)
        }
        
      } else {
        console.log(`ℹ️ No hay picks para el evento ${eventType}`)
        
        if (eventType === 'current') {
          currentUserPicks.value = []
        } else if (eventType === 'previous') {
          previousUserPicks.value = []
        }
      }
      
    } catch (error) {
      console.error(`❌ Error al cargar picks del evento ${eventType}:`, error)
      
      if (eventType === 'current') {
        currentUserPicks.value = []
      } else if (eventType === 'previous') {
        previousUserPicks.value = []
      }
    }
  }


  // Función para salir de la liga
  async function leaveLeague() {
    if (!currentLeague.value || !canLeaveLeague.value) {
      displayNotification('No puedes salir de esta liga')
      return
    }
    
    isLeavingLeague.value = true
    try {
      console.log('🚪 Saliendo de la liga:', leagueId)
      
      const response = await leaguesService.leaveLeague(leagueId)
      
      console.log('✅ Has salido de la liga exitosamente')
      displayNotification('Has salido de la liga exitosamente')
      
      // Redirigir al dashboard después de un breve delay
      setTimeout(() => {
        router.push('/dashboard')
      }, 2000)
      
    } catch (error) {
      console.error('❌ Error al salir de la liga:', error)
      displayNotification('Error al salir de la liga: ' + error.message)
    } finally {
      isLeavingLeague.value = false
      showLeaveConfirmation.value = false
    }
  }

  // Función para copiar código de invitación
  async function copyInvitationCode() {
    if (!currentLeague.value?.invitationCode) return
    
    try {
      await navigator.clipboard.writeText(currentLeague.value.invitationCode)
      displayNotification('Código copiado al portapapeles')
    } catch (error) {
      console.error('Error al copiar código:', error)
      displayNotification('Error al copiar el código')
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

  function getUserInitials(user) {
    if (user.firstName && user.lastName) {
      return (user.firstName[0] + user.lastName[0]).toUpperCase()
    }
    return user.username.substring(0, 2).toUpperCase()
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

  // Funciones para manejo de modales
  function showLeaveLeagueConfirmation() {
    showLeaveConfirmation.value = true
  }

  function hideLeaveConfirmation() {
    showLeaveConfirmation.value = false
  }

  // 🔥 FUNCIÓN MEJORADA: Cargar todos los datos incluyendo evento anterior
  async function loadAllData() {
    try {
      console.log('🚀 Iniciando carga de datos de la liga...')
      
      // 1. Cargar detalles de la liga primero
      await loadLeagueDetails()
      
      // 2. Cargar eventos (actual y anterior)
      await loadEvents()
      
      // 3. Cargar datos en paralelo
      await Promise.all([
        loadGlobalLeaderboard(),
        loadCurrentEventLeaderboard(),
        loadPreviousEventLeaderboard(),
        loadMyPosition(),
        loadMyHistory() // Esta función ahora carga los picks con datos completos
      ])
      
      console.log('✅ Todos los datos de la liga cargados correctamente')
      
    } catch (error) {
      console.error('💥 Error al cargar datos de la liga:', error)
      displayNotification('Error al cargar los datos de la liga')
    }
  }

  // 🔥 FUNCIÓN MEJORADA: Recargar datos específicos según la pestaña activa
  async function refreshTabData(tabName) {
    console.log('🔄 Recargando datos para pestaña:', tabName)
    
    switch (tabName) {
      case 'global':
        await loadGlobalLeaderboard()
        break
      case 'current':
        await loadCurrentEventLeaderboard()
        break
      case 'previous': // 🆕 Manejar pestaña del evento anterior
        await loadPreviousEventLeaderboard()
        break
    }
  }

  return {
    // Estados
    currentLeague,
    currentEvent,
    previousEvent, // 🆕
    globalLeaderboard,
    currentEventLeaderboard,
    previousEventLeaderboard, // 🆕
    currentUserPicks,
    previousUserPicks, // 🆕
    myPosition,
    myHistory,
    
    // Estados de carga
    isLoadingLeague,
    isLoadingGlobal,
    isLoadingCurrentEvent,
    isLoadingPreviousEvent, // 🆕
    isLeavingLeague,
    
    // Estados UI
    activeTab,
    selectedFighter,
    showLeagueInfo,
    showNotificationModal,
    notificationText,
    showLeaveConfirmation,
    
    // Computed
    isPublicLeague,
    isPrivateLeague,
    canMakePicks,
    canLeaveLeague,
    hasPreviousEvent, // 🆕
    formattedEventDate,
    formattedPreviousEventDate, // 🆕
    
    // Funciones
    loadAllData,
    refreshTabData,
    leaveLeague,
    copyInvitationCode,
    getPositionClass,
    getFighterInitials,
    getUserInitials,
    showFighterDetails,
    displayNotification,
    hideNotification,
    goBackToDashboard,
    goToPicksSelection,
    showLeaveLeagueConfirmation,
    hideLeaveConfirmation
  }
}