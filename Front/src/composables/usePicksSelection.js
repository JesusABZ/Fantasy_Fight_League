// Front/src/composables/usePicksSelection.js
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth.js'
import { eventsService, leaguesService, picksService } from '../api/index.js'
import { useDateFormatter } from './useDateFormatter.js'

export function usePicksSelection() {
  const router = useRouter()
  const route = useRoute()
  const authStore = useAuthStore()
  const { formatEventDate } = useDateFormatter()

  // Parámetros de la ruta
  const leagueId = computed(() => route.params.id)
  const eventId = computed(() => route.params.eventId)

  // Estados reactivos
  const currentLeague = ref(null)
  const currentEvent = ref(null)
  const availableFighters = ref([])
  const selectedFighters = ref([])
  const originalSelectedFighters = ref([]) // Para poder cancelar cambios

  // Estados de carga
  const isLoadingLeague = ref(false)
  const isLoadingEvent = ref(false)
  const isLoadingFighters = ref(false)
  const isLoadingPick = ref(false)
  const isSaving = ref(false)

  // Estados de UI
  const showNotification = ref(false)
  const notificationType = ref('success')
  const notificationText = ref('')

  // Configuración por defecto (se sobrescribirá con datos de la liga)
  const totalBudget = computed(() => currentLeague.value?.initialBudget || 100000)
  const maxFighters = computed(() => currentLeague.value?.maxFightersEvent || 3)
  const minFighters = computed(() => currentLeague.value?.minFightersEvent || 1)

  // Computed properties para presupuesto
  const spentBudget = computed(() => {
    return selectedFighters.value.reduce((total, fighter) => total + (fighter.price || 0), 0)
  })

  const availableBudget = computed(() => {
    return totalBudget.value - spentBudget.value
  })

  // Computed para validaciones
  const canSavePicks = computed(() => {
    const fighterCount = selectedFighters.value.length
    const budgetOk = spentBudget.value <= totalBudget.value
    const countOk = fighterCount >= minFighters.value && fighterCount <= maxFighters.value
    
    return budgetOk && countOk && !isSaving.value
  })

  const hasChanges = computed(() => {
    if (originalSelectedFighters.value.length !== selectedFighters.value.length) {
      return true
    }
    
    const originalIds = originalSelectedFighters.value.map(f => f.id).sort()
    const currentIds = selectedFighters.value.map(f => f.id).sort()
    
    return JSON.stringify(originalIds) !== JSON.stringify(currentIds)
  })

  // Computed para luchadores filtrados
  const filteredAvailableFighters = computed(() => {
    // Por ahora devolvemos todos, pero aquí se pueden agregar filtros
    return availableFighters.value
  })

  // Funciones de carga de datos
  async function loadLeagueDetails() {
    if (!leagueId.value) return

    isLoadingLeague.value = true
    try {
      console.log('🏆 Cargando detalles de la liga:', leagueId.value)
      
      const league = await leaguesService.getLeagueById(leagueId.value)
      currentLeague.value = league
      
      console.log('✅ Liga cargada:', league)
      
    } catch (error) {
      console.error('❌ Error al cargar liga:', error)
      showFloatingNotification('error', 'Error al cargar los detalles de la liga')
      throw error
    } finally {
      isLoadingLeague.value = false
    }
  }

  async function loadEventDetails() {
    if (!eventId.value) return

    isLoadingEvent.value = true
    try {
      console.log('📅 Cargando detalles del evento:', eventId.value)
      
      const event = await eventsService.getEventById(eventId.value)
      currentEvent.value = event
      
      console.log('✅ Evento cargado:', event)
      
    } catch (error) {
      console.error('❌ Error al cargar evento:', error)
      showFloatingNotification('error', 'Error al cargar los detalles del evento')
      throw error
    } finally {
      isLoadingEvent.value = false
    }
  }

  async function loadEventFighters() {
    if (!eventId.value) return

    isLoadingFighters.value = true
    try {
      console.log('⚔️ Cargando luchadores del evento:', eventId.value)
      
      const fighters = await eventsService.getEventFighters(eventId.value)
      
      // Procesar los datos de luchadores para el formato esperado
      availableFighters.value = fighters.map(fighter => ({
        id: fighter.id,
        name: fighter.name,
        record: fighter.record || 'N/A',
        nationality: fighter.nationality || 'Unknown',
        weightClass: fighter.weightClass || 'Unknown',
        imageUrl: fighter.imageUrl,
        price: fighter.price || 60000, // Precio por defecto si no está establecido
        cost: fighter.price || 60000, // Alias para compatibilidad
        active: fighter.active,
        // Determinar tipo de pelea basado en el precio (esto es una aproximación)
        fightType: determineFightType(fighter.price || 60000)
      }))
      
      console.log('✅ Luchadores cargados:', availableFighters.value.length)
      
    } catch (error) {
      console.error('❌ Error al cargar luchadores:', error)
      showFloatingNotification('error', 'Error al cargar los luchadores del evento')
      availableFighters.value = []
      throw error
    } finally {
      isLoadingFighters.value = false
    }
  }

  async function loadExistingPick() {
    if (!leagueId.value || !eventId.value) return

    isLoadingPick.value = true
    try {
      console.log('📋 Cargando pick existente...')
      
      const existingPick = await picksService.getMyPick(leagueId.value, eventId.value)
      
      if (existingPick && existingPick.selectedFighters && existingPick.selectedFighters.length > 0) {
        // Mapear los luchadores seleccionados existentes
        const selectedFighterIds = existingPick.selectedFighters.map(f => f.id)
        
        // Encontrar los luchadores completos en la lista disponible
        const selectedFightersData = availableFighters.value.filter(fighter => 
          selectedFighterIds.includes(fighter.id)
        )
        
        selectedFighters.value = selectedFightersData
        originalSelectedFighters.value = [...selectedFightersData] // Copia para detectar cambios
        
        console.log('✅ Pick existente cargado:', selectedFightersData.length, 'luchadores')
      } else {
        console.log('ℹ️ No hay pick existente para este evento')
        selectedFighters.value = []
        originalSelectedFighters.value = []
      }
      
    } catch (error) {
      console.error('❌ Error al cargar pick existente:', error)
      // No mostramos error al usuario porque es normal no tener pick previo
      selectedFighters.value = []
      originalSelectedFighters.value = []
    } finally {
      isLoadingPick.value = false
    }
  }

  // Funciones auxiliares
  function determineFightType(price) {
    if (price >= 70000) return 'Main Event'
    if (price >= 60000) return 'Co-Main Event'
    if (price >= 40000) return 'Main Card'
    if (price >= 25000) return 'Prelims'
    return 'Early Prelims'
  }

  // Funciones de manejo de luchadores
  function isSelected(fighterId) {
    return selectedFighters.value.some(fighter => fighter.id === fighterId)
  }

  function canAfford(cost) {
    if (isSelected(cost)) return true // Ya seleccionado, siempre puede "pagarlo"
    return cost <= availableBudget.value
  }

  function addFighter(fighter) {
    // Verificaciones
    if (isSelected(fighter.id)) {
      showFloatingNotification('info', `${fighter.name} ya está seleccionado`)
      return
    }

    if (selectedFighters.value.length >= maxFighters.value) {
      showFloatingNotification('error', `Solo puedes seleccionar ${maxFighters.value} luchadores`)
      return
    }

    if (!canAfford(fighter.cost)) {
      showFloatingNotification('error', 'No tienes suficiente presupuesto para este luchador')
      return
    }

    // Agregar luchador
    selectedFighters.value.push(fighter)
    showFloatingNotification('success', `${fighter.name} agregado a tus picks`)
  }

  function removeFighter(fighter) {
    const index = selectedFighters.value.findIndex(f => f.id === fighter.id)
    if (index > -1) {
      selectedFighters.value.splice(index, 1)
      showFloatingNotification('info', `${fighter.name} removido de tus picks`)
    }
  }

  function toggleFighter(fighter) {
    if (isSelected(fighter.id)) {
      removeFighter(fighter)
    } else {
      addFighter(fighter)
    }
  }

  // Función para guardar picks
  async function savePicks() {
    if (!canSavePicks.value) {
      showFloatingNotification('error', 'Verifica tus picks antes de guardar')
      return
    }

    isSaving.value = true

    try {
      console.log('💾 Guardando picks...')
      
      const pickData = {
        leagueId: parseInt(leagueId.value),
        eventId: parseInt(eventId.value),
        fighterIds: selectedFighters.value.map(fighter => fighter.id)
      }
      
      console.log('📤 Datos del pick:', pickData)
      
      const response = await picksService.createOrUpdatePick(pickData)
      
      console.log('✅ Picks guardados exitosamente:', response)
      
      // Actualizar los picks originales para evitar alertas de cambios no guardados
      originalSelectedFighters.value = [...selectedFighters.value]
      
      showFloatingNotification('success', '¡Picks guardados correctamente!')
      
      // No redirigir automáticamente, dejar que el usuario decida
      
    } catch (error) {
      console.error('❌ Error al guardar picks:', error)
      
      // Manejo específico de errores comunes
      if (error.message.includes('EMAIL_NOT_VERIFIED')) {
        showFloatingNotification('error', 'Debes verificar tu email antes de hacer picks')
      } else if (error.message.includes('picks están cerrados')) {
        showFloatingNotification('error', 'Los picks están cerrados para este evento')
      } else if (error.message.includes('presupuesto')) {
        showFloatingNotification('error', 'El costo de tus picks excede el presupuesto')
      } else {
        showFloatingNotification('error', 'Error al guardar los picks. Inténtalo de nuevo.')
      }
    } finally {
      isSaving.value = false
    }
  }

  // Funciones de navegación
  function goBack() {
    router.push(`/league/${leagueId.value}`)
  }

  function cancelChanges() {
    if (hasChanges.value) {
      // Restaurar picks originales
      selectedFighters.value = [...originalSelectedFighters.value]
      showFloatingNotification('info', 'Cambios cancelados')
    }
    goBack()
  }

  // Funciones de utilidad
  function formatMoney(amount) {
    return new Intl.NumberFormat('en-US').format(amount)
  }

  function getFighterInitials(name) {
    return name.split(' ').map(word => word[0]).join('').toUpperCase()
  }

  function showFloatingNotification(type, text) {
    notificationType.value = type
    notificationText.value = text
    showNotification.value = true
    
    setTimeout(() => {
      hideNotification()
    }, 4000)
  }

  function hideNotification() {
    showNotification.value = false
  }

  // Función principal para cargar todos los datos
  async function loadAllData() {
    try {
      console.log('🚀 Iniciando carga de datos para picks...')
      
      // Cargar datos secuencialmente para evitar problemas de dependencias
      await loadLeagueDetails()
      await loadEventDetails()
      await loadEventFighters()
      
      // Solo cargar pick existente después de tener los luchadores disponibles
      await loadExistingPick()
      
      console.log('✅ Todos los datos cargados correctamente')
      
    } catch (error) {
      console.error('💥 Error al cargar datos:', error)
      showFloatingNotification('error', 'Error al cargar los datos. Inténtalo de nuevo.')
    }
  }

  return {
    // Estados
    currentLeague,
    currentEvent,
    availableFighters,
    selectedFighters,
    
    // Estados de carga
    isLoadingLeague,
    isLoadingEvent,
    isLoadingFighters,
    isLoadingPick,
    isSaving,
    
    // Estados UI
    showNotification,
    notificationType,
    notificationText,
    
    // Computed
    totalBudget,
    maxFighters,
    minFighters,
    spentBudget,
    availableBudget,
    canSavePicks,
    hasChanges,
    filteredAvailableFighters,
    
    // Funciones
    loadAllData,
    isSelected,
    canAfford,
    toggleFighter,
    removeFighter,
    savePicks,
    cancelChanges,
    goBack,
    formatMoney,
    getFighterInitials,
    showFloatingNotification,
    hideNotification,
    
    // Formateo de fechas
    formatDate: formatEventDate
  }
}