// composables/useFighters.js
import { ref, computed } from 'vue'
import { useNotifications } from './useNotifications'
import { useFormatters } from './useFormatters'
// import { fightersService } from '@/api' // Cuando esté creado

export function useFighters() {
  const { common: notifications, handleApiError } = useNotifications()
  const { formatMoney, getInitials } = useFormatters()

  // Estado
  const fighters = ref([])
  const selectedFighters = ref([])
  const eventFighters = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  // Configuración
  const maxFighters = ref(3)
  const totalBudget = ref(100000)

  // Computed
  const availableFighters = computed(() => {
    return fighters.value.filter(fighter => 
      !selectedFighters.value.some(selected => selected.id === fighter.id)
    )
  })

  const spentBudget = computed(() => {
    return selectedFighters.value.reduce((total, fighter) => total + fighter.cost, 0)
  })

  const availableBudget = computed(() => {
    return totalBudget.value - spentBudget.value
  })

  const canAddMoreFighters = computed(() => {
    return selectedFighters.value.length < maxFighters.value
  })

  const canAffordFighter = computed(() => (cost) => {
    return cost <= availableBudget.value
  })

  const isValidLineup = computed(() => {
    return selectedFighters.value.length >= 1 && 
           selectedFighters.value.length <= maxFighters.value &&
           spentBudget.value <= totalBudget.value
  })

  const fightersByWeightClass = computed(() => {
    const grouped = {}
    fighters.value.forEach(fighter => {
      const weightClass = fighter.weightClass || 'Other'
      if (!grouped[weightClass]) {
        grouped[weightClass] = []
      }
      grouped[weightClass].push(fighter)
    })
    return grouped
  })

  const fightersByFightType = computed(() => {
    const grouped = {}
    fighters.value.forEach(fighter => {
      const fightType = fighter.fightType || 'Prelims'
      if (!grouped[fightType]) {
        grouped[fightType] = []
      }
      grouped[fightType].push(fighter)
    })
    return grouped
  })

  // Métodos para obtener datos
  const fetchEventFighters = async (eventId) => {
    isLoading.value = true
    error.value = null

    try {
      // TODO: Implementar servicio
      // const response = await fightersService.getEventFighters(eventId)
      
      // Datos simulados
      eventFighters.value = [
        {
          id: 1,
          name: 'Jon Jones',
          record: '27-1',
          cost: 75000,
          weightClass: 'Heavyweight',
          fightType: 'Main Event',
          imageUrl: 'https://dynl.mktgcdn.com/p/xnYGAGWMpbap_gSsJDIFPvBDPsTv5j_W3V0gCeAFyIQ/200x1.png',
          points: 0,
          isActive: true
        },
        {
          id: 2,
          name: 'Ilia Topuria',
          record: '14-0',
          cost: 65000,
          weightClass: 'Featherweight',
          fightType: 'Co-Main Event',
          imageUrl: 'https://dynl.mktgcdn.com/p/jMTyWWXdzwWDaswQzek-X6JLObRd1otuQaU4KQZElfw/200x1.png',
          points: 0,
          isActive: true
        }
      ]
      
      fighters.value = eventFighters.value
      return fighters.value
    } catch (err) {
      error.value = err.message
      handleApiError(err, 'Error al cargar los luchadores')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchFighterDetails = async (fighterId) => {
    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // const response = await fightersService.getFighterById(fighterId)
      
      const fighter = fighters.value.find(f => f.id === fighterId)
      return fighter
    } catch (err) {
      handleApiError(err, 'Error al cargar los detalles del luchador')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Métodos de selección
  const addFighter = (fighter) => {
    // Verificar si ya está seleccionado
    if (isSelected(fighter.id)) {
      notifications.warning(`${fighter.name} ya está en tu lineup`)
      return false
    }

    // Verificar límite de luchadores
    if (!canAddMoreFighters.value) {
      notifications.maxFightersReached(maxFighters.value)
      return false
    }

    // Verificar presupuesto
    if (!canAffordFighter.value(fighter.cost)) {
      notifications.insufficientBudget()
      return false
    }

    selectedFighters.value.push(fighter)
    notifications.fighterAdded(fighter.name)
    return true
  }

  const removeFighter = (fighter) => {
    const index = selectedFighters.value.findIndex(f => f.id === fighter.id)
    if (index > -1) {
      selectedFighters.value.splice(index, 1)
      notifications.fighterRemoved(fighter.name)
      return true
    }
    return false
  }

  const toggleFighter = (fighter) => {
    if (isSelected(fighter.id)) {
      return removeFighter(fighter)
    } else {
      return addFighter(fighter)
    }
  }

  const clearSelection = () => {
    selectedFighters.value = []
    notifications.info('Lineup limpio')
  }

  const isSelected = (fighterId) => {
    return selectedFighters.value.some(fighter => fighter.id === fighterId)
  }

  // Métodos de picks
  const savePicks = async (leagueId, eventId) => {
    if (!isValidLineup.value) {
      notifications.warning('Tu lineup no es válido')
      return false
    }

    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // await fightersService.savePicks(leagueId, eventId, selectedFighters.value)
      
      notifications.picksSaved()
      return true
    } catch (err) {
      handleApiError(err, 'Error al guardar los picks')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const loadUserPicks = async (leagueId, eventId) => {
    isLoading.value = true

    try {
      // TODO: Implementar servicio
      // const response = await fightersService.getUserPicks(leagueId, eventId)
      
      // Datos simulados
      const userPicks = [
        {
          id: 1,
          name: 'Jon Jones',
          record: '27-1',
          cost: 75000,
          points: 120,
          weightClass: 'Heavyweight',
          fightType: 'Main Event'
        }
      ]
      
      selectedFighters.value = userPicks
      return userPicks
    } catch (err) {
      handleApiError(err, 'Error al cargar tus picks')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Utilidades
  const getFighterInitials = (fighterName) => {
    return getInitials(fighterName)
  }

  const formatFighterCost = (cost) => {
    return `$${formatMoney(cost)}`
  }

  const getFighterById = (fighterId) => {
    return fighters.value.find(fighter => fighter.id === fighterId)
  }

  const getSelectedFighterById = (fighterId) => {
    return selectedFighters.value.find(fighter => fighter.id === fighterId)
  }

  const calculateLineupValue = () => {
    return selectedFighters.value.reduce((total, fighter) => total + fighter.cost, 0)
  }

  const calculateLineupPoints = () => {
    return selectedFighters.value.reduce((total, fighter) => total + (fighter.points || 0), 0)
  }

  const getWeightClassColor = (weightClass) => {
    const colors = {
      'Heavyweight': 'var(--error)',
      'Light Heavyweight': 'var(--warning)',
      'Middleweight': 'var(--info)',
      'Welterweight': 'var(--success)',
      'Lightweight': 'var(--primary)',
      'Featherweight': 'var(--secondary)',
      'Bantamweight': 'var(--info)',
      'Flyweight': 'var(--success)',
      'Strawweight': 'var(--primary)'
    }
    return colors[weightClass] || 'var(--gray)'
  }

  const getFightTypeIcon = (fightType) => {
    const icons = {
      'Main Event': '👑',
      'Co-Main Event': '🥇',
      'Main Card': '⭐',
      'Prelims': '🥊',
      'Early Prelims': '🎯'
    }
    return icons[fightType] || '🥊'
  }

  // Filtros
  const filterByWeightClass = (weightClass) => {
    return fighters.value.filter(fighter => fighter.weightClass === weightClass)
  }

  const filterByFightType = (fightType) => {
    return fighters.value.filter(fighter => fighter.fightType === fightType)
  }

  const filterByCostRange = (minCost, maxCost) => {
    return fighters.value.filter(fighter => 
      fighter.cost >= minCost && fighter.cost <= maxCost
    )
  }

  const searchFighters = (searchTerm) => {
    if (!searchTerm) return fighters.value
    
    const term = searchTerm.toLowerCase()
    return fighters.value.filter(fighter =>
      fighter.name.toLowerCase().includes(term) ||
      fighter.record.includes(term) ||
      fighter.weightClass.toLowerCase().includes(term)
    )
  }

  // Limpiar estado
  const clearError = () => {
    error.value = null
  }

  const reset = () => {
    fighters.value = []
    selectedFighters.value = []
    eventFighters.value = []
    error.value = null
  }

  // Configuración
  const setMaxFighters = (max) => {
    maxFighters.value = max
  }

  const setBudget = (budget) => {
    totalBudget.value = budget
  }

  return {
    // Estado
    fighters,
    selectedFighters,
    eventFighters,
    isLoading,
    error,
    maxFighters,
    totalBudget,

    // Computed
    availableFighters,
    spentBudget,
    availableBudget,
    canAddMoreFighters,
    canAffordFighter,
    isValidLineup,
    fightersByWeightClass,
    fightersByFightType,

    // Métodos de datos
    fetchEventFighters,
    fetchFighterDetails,

    // Métodos de selección
    addFighter,
    removeFighter,
    toggleFighter,
    clearSelection,
    isSelected,

    // Métodos de picks
    savePicks,
    loadUserPicks,

    // Utilidades
    getFighterInitials,
    formatFighterCost,
    getFighterById,
    getSelectedFighterById,
    calculateLineupValue,
    calculateLineupPoints,
    getWeightClassColor,
    getFightTypeIcon,

    // Filtros
    filterByWeightClass,
    filterByFightType,
    filterByCostRange,
    searchFighters,

    // Configuración
    setMaxFighters,
    setBudget,

    // Limpiar
    clearError,
    reset
  }
}