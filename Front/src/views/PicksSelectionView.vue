<template>
  <div class="picks-selection">
    <!-- Fondo estático -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header -->
    <div class="picks-header">
      <div class="container">
        <div class="header-content">
          <button class="btn btn-back" @click="goBack">
            ← Volver a Liga
          </button>
          
          <div class="event-info">
            <h1 class="event-name">{{ currentEvent.name }}</h1>
            <p class="event-details">{{ formatDate(currentEvent.date) }} • {{ currentEvent.location }}</p>
          </div>
          
          <div class="league-info-small">
            <span class="league-name-small">{{ currentLeague.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Panel de estado del presupuesto -->
    <div class="budget-panel">
      <div class="container">
        <div class="budget-content">
          <div class="budget-info">
            <div class="budget-item">
              <span class="budget-label">Presupuesto Total:</span>
              <span class="budget-value total">${{ formatMoney(totalBudget) }}</span>
            </div>
            <div class="budget-item">
              <span class="budget-label">Gastado:</span>
              <span class="budget-value spent">${{ formatMoney(spentBudget) }}</span>
            </div>
            <div class="budget-item">
              <span class="budget-label">Disponible:</span>
              <span class="budget-value available" :class="{ 'low': availableBudget < 20000, 'empty': availableBudget <= 0 }">
                ${{ formatMoney(availableBudget) }}
              </span>
            </div>
          </div>
          <div class="picks-count">
            <span class="picks-label">Luchadores seleccionados:</span>
            <span class="picks-value" :class="{ 'max': selectedFighters.length >= maxFighters }">
              {{ selectedFighters.length }}/{{ maxFighters }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        <div class="picks-layout">
          
          <!-- Panel izquierdo: Tus picks actuales -->
          <div class="selected-picks-panel">
            <div class="panel-header">
              <h2 class="panel-title">🎯 Tus Picks</h2>
              <span class="panel-subtitle">Luchadores seleccionados para este evento</span>
            </div>
            
            <div class="selected-picks-container">
              <!-- Lista de luchadores seleccionados -->
              <div v-if="selectedFighters.length > 0" class="selected-fighters-list">
                <div 
                  v-for="fighter in selectedFighters" 
                  :key="fighter.id"
                  class="selected-fighter-card"
                >
                  <div class="fighter-avatar-section">
                    <div class="fighter-avatar">
                      <img v-if="fighter.imageUrl" :src="fighter.imageUrl" :alt="fighter.name" />
                      <span v-else class="fighter-initials">{{ getFighterInitials(fighter.name) }}</span>
                    </div>
                  </div>
                  
                  <div class="fighter-info-section">
                    <h4 class="fighter-name">{{ fighter.name }}</h4>
                    <div class="fighter-record">{{ fighter.record }}</div>
                    <div class="fighter-badges">
                      <span class="weight-class-badge">{{ fighter.weightClass }}</span>
                      <span class="fight-type-badge">{{ fighter.fightType }}</span>
                    </div>
                  </div>
                  
                  <div class="fighter-actions-section">
                    <div class="fighter-cost">${{ formatMoney(fighter.cost) }}</div>
                    <button 
                      class="btn-remove" 
                      @click="removeFighter(fighter)"
                      title="Quitar luchador"
                    >
                      ✕
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- Estado vacío -->
              <div v-else class="empty-picks">
                <div class="empty-icon">🥊</div>
                <h3 class="empty-title">No hay luchadores seleccionados</h3>
                <p class="empty-description">Elige hasta {{ maxFighters }} luchadores de la lista</p>
              </div>
            </div>
          </div>

          <!-- Panel derecho: Luchadores disponibles -->
          <div class="available-fighters-panel">
            <div class="panel-header">
              <h2 class="panel-title">⚔️ Luchadores Disponibles</h2>
            </div>
            
            <div class="available-fighters-container">
              <div class="fighters-list">
                <div 
                  v-for="fighter in filteredAvailableFighters" 
                  :key="fighter.id"
                  class="fighter-card"
                  :class="{ 
                    'selected': isSelected(fighter.id),
                    'too-expensive': !canAfford(fighter.cost),
                    'max-reached': selectedFighters.length >= maxFighters && !isSelected(fighter.id)
                  }"
                  @click="toggleFighter(fighter)"
                >
                  <div class="fighter-main-info">
                    <div class="fighter-avatar">
                      <img v-if="fighter.imageUrl" :src="fighter.imageUrl" :alt="fighter.name" />
                      <span v-else class="fighter-initials">{{ getFighterInitials(fighter.name) }}</span>
                    </div>
                    <div class="fighter-details">
                      <h4 class="fighter-name">{{ fighter.name }}</h4>
                      <p class="fighter-record">{{ fighter.record }}</p>
                      <div class="fighter-meta">
                        <span class="weight-class">{{ fighter.weightClass }}</span>
                        <span class="fight-type">{{ fighter.fightType }}</span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="fighter-selection">
                    <div class="fighter-cost">
                      <span class="cost-amount">${{ formatMoney(fighter.cost) }}</span>
                    </div>
                    <div class="selection-indicator">
                      <span v-if="isSelected(fighter.id)" class="selected-check">✓</span>
                      <span v-else-if="!canAfford(fighter.cost)" class="too-expensive-icon">💰</span>
                      <span v-else-if="selectedFighters.length >= maxFighters" class="max-reached-icon">🚫</span>
                      <span v-else class="add-icon">+</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Estado de carga o sin resultados -->
              <div v-if="filteredAvailableFighters.length === 0" class="no-fighters">
                <div class="no-fighters-icon">🔍</div>
                <h3 class="no-fighters-title">No se encontraron luchadores</h3>
                <p class="no-fighters-description">Intenta cambiar los filtros de búsqueda</p>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- Panel de acciones flotante -->
    <div class="floating-actions">
      <div class="container">
        <div class="actions-content">
          <div class="picks-summary">
            <span class="summary-text">
              {{ selectedFighters.length }} de {{ maxFighters }} luchadores • 
              ${{ formatMoney(spentBudget) }} gastado
            </span>
          </div>
          <div class="action-buttons">
            <button 
              class="btn btn-cancel"
              @click="cancelChanges"
            >
              Cancelar
            </button>
            <button 
              class="btn btn-save"
              :disabled="!canSavePicks"
              :class="{ 'disabled': !canSavePicks }"
              @click="savePicks"
            >
              <span v-if="isSaving">Guardando...</span>
              <span v-else>💾 Guardar Picks</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotification" class="notification" :class="notificationType" @click="hideNotification">
      <div class="notification-icon">
        <span v-if="notificationType === 'success'">✅</span>
        <span v-else-if="notificationType === 'error'">❌</span>
        <span v-else>ℹ️</span>
      </div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'PicksSelectionView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // Estados principales
    const isSaving = ref(false)
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Configuración de la liga y evento
    const currentLeague = reactive({
      id: 1,
      name: 'Liga Oficina',
      maxFighters: 3,
      budget: 100000
    })

    const currentEvent = reactive({
      id: 1,
      name: 'UFC Vegas 107',
      date: '2025-05-31',
      location: 'Las Vegas, Nevada'
    })

    // Presupuesto
    const totalBudget = ref(100000)
    const maxFighters = ref(3)

    // Luchadores seleccionados actualmente
    const selectedFighters = ref([
      {
        id: 1,
        name: 'Jon Jones',
        record: '27-1',
        cost: 75000,
        weightClass: 'Heavyweight',
        fightType: 'Main Event',
        imageUrl: 'https://dynl.mktgcdn.com/p/xnYGAGWMpbap_gSsJDIFPvBDPsTv5j_W3V0gCeAFyIQ/200x1.png'
      },
      {
        id: 2,
        name: 'Ilia Topuria',
        record: '14-0',
        cost: 65000,
        weightClass: 'Featherweight',
        fightType: 'Co-Main Event',
        imageUrl: 'https://dynl.mktgcdn.com/p/jMTyWWXdzwWDaswQzek-X6JLObRd1otuQaU4KQZElfw/200x1.png'
      }
    ])

    // Lista completa de luchadores disponibles para el evento
    const availableFighters = ref([
      {
        id: 1,
        name: 'Jon Jones',
        record: '27-1',
        cost: 75000,
        weightClass: 'Heavyweight',
        fightType: 'Main Event',
        imageUrl: 'https://dynl.mktgcdn.com/p/xnYGAGWMpbap_gSsJDIFPvBDPsTv5j_W3V0gCeAFyIQ/200x1.png'
      },
      {
        id: 2,
        name: 'Ilia Topuria',
        record: '14-0',
        cost: 65000,
        weightClass: 'Featherweight',
        fightType: 'Co-Main Event',
        imageUrl: 'https://dynl.mktgcdn.com/p/jMTyWWXdzwWDaswQzek-X6JLObRd1otuQaU4KQZElfw/200x1.png'
      },
      {
        id: 3,
        name: 'Ciryl Gane',
        record: '11-2',
        cost: 45000,
        weightClass: 'Heavyweight',
        fightType: 'Main Card'
      },
      {
        id: 4,
        name: 'Alexander Volkov',
        record: '35-10',
        cost: 35000,
        weightClass: 'Heavyweight',
        fightType: 'Main Card'
      },
      {
        id: 5,
        name: 'Sean O\'Malley',
        record: '17-1',
        cost: 55000,
        weightClass: 'Bantamweight',
        fightType: 'Main Card'
      },
      {
        id: 6,
        name: 'Marlon Vera',
        record: '21-8',
        cost: 40000,
        weightClass: 'Bantamweight',
        fightType: 'Main Card'
      },
      {
        id: 7,
        name: 'Islam Makhachev',
        record: '25-1',
        cost: 70000,
        weightClass: 'Lightweight',
        fightType: 'Main Card'
      },
      {
        id: 8,
        name: 'Charles Oliveira',
        record: '34-9',
        cost: 60000,
        weightClass: 'Lightweight',
        fightType: 'Main Card'
      },
      {
        id: 9,
        name: 'Leon Edwards',
        record: '21-3',
        cost: 50000,
        weightClass: 'Welterweight',
        fightType: 'Prelims'
      },
      {
        id: 10,
        name: 'Jorge Masvidal',
        record: '35-16',
        cost: 30000,
        weightClass: 'Welterweight',
        fightType: 'Prelims'
      }
    ])

    // Computed properties
    const spentBudget = computed(() => {
      return selectedFighters.value.reduce((total, fighter) => total + fighter.cost, 0)
    })

    const availableBudget = computed(() => {
      return totalBudget.value - spentBudget.value
    })

    const filteredAvailableFighters = computed(() => {
      return availableFighters.value
    })

    const canSavePicks = computed(() => {
      return selectedFighters.value.length >= 1 && 
             selectedFighters.value.length <= maxFighters.value &&
             spentBudget.value <= totalBudget.value
    })

    // Funciones
    const formatMoney = (amount) => {
      return new Intl.NumberFormat('en-US').format(amount)
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const getFighterInitials = (name) => {
      return name.split(' ').map(word => word[0]).join('').toUpperCase()
    }

    const isSelected = (fighterId) => {
      return selectedFighters.value.some(fighter => fighter.id === fighterId)
    }

    const canAfford = (cost) => {
      return cost <= availableBudget.value
    }

    const addFighter = (fighter) => {
      // Verificar si ya está seleccionado
      if (isSelected(fighter.id)) return

      // Verificar límite de luchadores
      if (selectedFighters.value.length >= maxFighters.value) {
        showFloatingNotification('error', `Solo puedes seleccionar ${maxFighters.value} luchadores`)
        return
      }

      // Verificar presupuesto
      if (!canAfford(fighter.cost)) {
        showFloatingNotification('error', 'No tienes suficiente presupuesto para este luchador')
        return
      }

      selectedFighters.value.push(fighter)
      showFloatingNotification('success', `${fighter.name} agregado a tus picks`)
    }

    const removeFighter = (fighter) => {
      const index = selectedFighters.value.findIndex(f => f.id === fighter.id)
      if (index > -1) {
        selectedFighters.value.splice(index, 1)
        showFloatingNotification('info', `${fighter.name} removido de tus picks`)
      }
    }

    const toggleFighter = (fighter) => {
      if (isSelected(fighter.id)) {
        removeFighter(fighter)
      } else {
        addFighter(fighter)
      }
    }

    const savePicks = async () => {
      if (!canSavePicks.value) return

      isSaving.value = true

      try {
        // TODO: Conectar con backend
        console.log('Guardando picks:', selectedFighters.value)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        showFloatingNotification('success', '¡Picks guardados correctamente!')
        
        // Ya NO redirigir, quedarse en la pantalla
        // setTimeout(() => {
        //   router.push(`/league/${currentLeague.id}`)
        // }, 1500)
        
      } catch (error) {
        showFloatingNotification('error', 'Error al guardar los picks. Inténtalo de nuevo.')
      } finally {
        isSaving.value = false
      }
    }

    const cancelChanges = () => {
      // TODO: Restaurar picks originales si había cambios
      router.push(`/league/${currentLeague.id}`)
    }

    const goBack = () => {
      router.push(`/league/${currentLeague.id}`)
    }

    const showFloatingNotification = (type, text) => {
      notificationType.value = type
      notificationText.value = text
      showNotification.value = true
      
      setTimeout(() => {
        hideNotification()
      }, 3000)
    }

    const hideNotification = () => {
      showNotification.value = false
    }

    // Cargar datos al montar
    onMounted(() => {
      // TODO: Cargar datos reales basándose en route.params
      const leagueId = route.params.id
      const eventId = route.params.eventId
      console.log('Cargando picks para liga:', leagueId, 'evento:', eventId)
    })

    return {
      isSaving,
      showNotification,
      notificationType,
      notificationText,
      currentLeague,
      currentEvent,
      totalBudget,
      maxFighters,
      selectedFighters,
      spentBudget,
      availableBudget,
      filteredAvailableFighters,
      canSavePicks,
      formatMoney,
      formatDate,
      getFighterInitials,
      isSelected,
      canAfford,
      toggleFighter,
      removeFighter,
      savePicks,
      cancelChanges,
      goBack,
      hideNotification
    }
  }
}
</script>

<style scoped>
.picks-selection {
  position: relative;
  min-height: 100vh;
  padding-bottom: 120px; /* Espacio para el panel flotante */
}

/* === FONDO === */
.background-static {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/images/carrusel3.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: brightness(0.2) contrast(0.8) grayscale(0.3);
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(10, 10, 10, 0.95) 0%,
    rgba(26, 26, 26, 0.85) 30%,
    rgba(42, 42, 42, 0.75) 50%,
    rgba(26, 26, 26, 0.85) 70%,
    rgba(10, 10, 10, 0.95) 100%
  );
  backdrop-filter: blur(1px);
}

/* === HEADER === */
.picks-header {
  position: relative;
  z-index: 1;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.event-info {
  flex: 1;
  text-align: center;
}

.event-name {
  font-family: var(--font-impact);
  font-size: 2.2rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  margin-bottom: var(--space-xs);
}

.event-details {
  color: var(--gray-light);
  font-size: 1rem;
}

.league-name-small {
  color: var(--primary);
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
}

/* === PANEL DE PRESUPUESTO === */
.budget-panel {
  position: relative;
  z-index: 1;
  background: rgba(0, 0, 0, 0.4);
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
  padding: var(--space-lg) 0;
}

.budget-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.budget-info {
  display: flex;
  gap: var(--space-xl);
}

.budget-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.budget-label {
  color: var(--gray-light);
  font-size: 0.9rem;
  font-weight: 500;
}

.budget-value {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  font-weight: 400;
}

.budget-value.total {
  color: var(--white);
}

.budget-value.spent {
  color: var(--primary);
}

.budget-value.available {
  color: var(--success);
}

.budget-value.low {
  color: var(--warning);
}

.budget-value.empty {
  color: var(--error);
}

.picks-count {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
  text-align: right;
}

.picks-label {
  color: var(--gray-light);
  font-size: 0.9rem;
  font-weight: 500;
}

.picks-value {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
}

.picks-value.max {
  color: var(--warning);
}

/* === BOTONES === */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-decoration: none;
  gap: var(--space-sm);
}

.btn-back {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

/* === LAYOUT PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.picks-layout {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: var(--space-2xl);
  height: calc(100vh - 300px);
}

/* === PANEL DE PICKS SELECCIONADOS === */
.selected-picks-panel {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: var(--space-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.panel-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.panel-subtitle {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.selected-picks-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-lg);
}

/* === TARJETAS DE LUCHADORES SELECCIONADOS === */
.selected-fighters-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.selected-fighter-card {
  background: rgba(255, 107, 53, 0.1);
  border: 2px solid rgba(255, 107, 53, 0.4);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  transition: all 0.3s ease;
  position: relative;
}

.selected-fighter-card:hover {
  background: rgba(255, 107, 53, 0.15);
  border-color: var(--primary);
  transform: translateY(-2px);
}

.fighter-avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: var(--space-md);
}

.fighter-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid var(--primary);
  box-shadow: var(--shadow-md);
}

.fighter-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fighter-initials {
  font-family: var(--font-impact);
  font-size: 1.4rem;
  color: var(--white);
  font-weight: bold;
}

.fighter-info-section {
  text-align: center;
  margin-bottom: var(--space-lg);
}

.fighter-name {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  line-height: 1.2;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.fighter-record {
  color: var(--gray-light);
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: var(--space-md);
}

.fighter-badges {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  align-items: center;
}

.weight-class-badge {
  background: rgba(255, 107, 53, 0.5);
  color: var(--white);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-full);
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  border: 2px solid var(--primary);
}

.fight-type-badge {
  background: rgba(245, 158, 11, 0.5);
  color: var(--white);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  border: 2px solid var(--warning);
}

.fighter-actions-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fighter-cost {
  font-family: var(--font-impact);
  font-size: 1.4rem;
  color: var(--primary);
  font-weight: bold;
}

.btn-remove {
  background: transparent;
  border: 2px solid var(--error);
  color: var(--error);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: var(--error);
  color: var(--white);
  transform: scale(1.1);
}

/* === ESTADO VACÍO === */
.empty-picks {
  text-align: center;
  padding: var(--space-2xl);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--space-lg);
}

.empty-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.empty-description {
  color: var(--gray-light);
  font-size: 1rem;
}

/* === PANEL DE LUCHADORES DISPONIBLES === */
.available-fighters-panel {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel-controls {
  display: flex;
  gap: var(--space-md);
  margin-top: var(--space-md);
}

.panel-controls {
  display: flex;
  gap: var(--space-md);
  margin-top: var(--space-md);
}

.search-input,
.filter-select {
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.search-input {
  flex: 1;
}

.filter-select {
  min-width: 200px;
}

.search-input::placeholder {
  color: var(--gray-light);
}

.search-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
}

.available-fighters-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-lg);
}

.fighters-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

/* === TARJETAS DE LUCHADORES === */
.fighter-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.fighter-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-1px);
}

.fighter-card.selected {
  background: rgba(255, 107, 53, 0.2);
  border-color: var(--primary);
}

.fighter-card.too-expensive {
  opacity: 0.5;
  cursor: not-allowed;
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
}

.fighter-card.max-reached {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.3);
}

.fighter-main-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex: 1;
}

.fighter-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.fighter-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fighter-initials {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
}

.fighter-details {
  flex: 1;
}

.fighter-name {
  font-family: var(--font-impact);
  font-size: 1.1rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.fighter-record {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin-bottom: var(--space-xs);
}

.fighter-meta {
  display: flex;
  gap: var(--space-md);
}

.weight-class,
.fight-type {
  font-size: 0.8rem;
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-weight: 600;
  text-transform: uppercase;
}

.weight-class {
  background: rgba(255, 107, 53, 0.5);
  color: var(--white);
  border: 2px solid var(--primary);
}

.fight-type {
  background: rgba(245, 158, 11, 0.5);
  color: var(--white);
  border: 2px solid var(--warning);
}

.fighter-selection {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.fighter-cost {
  text-align: right;
}

.cost-amount {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--primary);
}

.selection-indicator {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
}

.selected-check {
  background: var(--success);
  color: var(--white);
  border-radius: 50%;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon {
  background: var(--gradient-primary);
  color: var(--white);
  border-radius: 50%;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.too-expensive-icon,
.max-reached-icon {
  color: var(--gray-light);
  font-size: 1.5rem;
}

.btn-remove {
  background: transparent;
  border: 1px solid var(--error);
  color: var(--error);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: var(--error);
  color: var(--white);
}

/* === SIN RESULTADOS === */
.no-fighters {
  text-align: center;
  padding: var(--space-2xl);
}

.no-fighters-icon {
  font-size: 3rem;
  margin-bottom: var(--space-lg);
}

.no-fighters-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.no-fighters-description {
  color: var(--gray-light);
  font-size: 1rem;
}

/* === PANEL FLOTANTE === */
.floating-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-top: 2px solid rgba(255, 107, 53, 0.3);
  padding: var(--space-lg) 0;
  z-index: 100;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.3);
}

.actions-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.picks-summary {
  flex: 1;
}

.summary-text {
  color: var(--gray-light);
  font-size: 1rem;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: var(--space-md);
}

.btn-cancel {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--space-md) var(--space-xl);
}

.btn-cancel:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

.btn-save {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-md) var(--space-xl);
  font-size: 1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  box-shadow: var(--shadow-md);
}

.btn-save:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-save.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--gray);
  transform: none !important;
  box-shadow: var(--shadow-md) !important;
}

/* === NOTIFICACIÓN === */
.notification {
  position: fixed;
  top: var(--space-xl);
  right: var(--space-xl);
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  z-index: 1000;
  cursor: pointer;
  transform: translateX(100%);
  animation: slideIn 0.3s ease forwards;
  max-width: 350px;
}

.notification.success {
  border: 2px solid var(--success);
}

.notification.error {
  border: 2px solid var(--error);
}

.notification.info {
  border: 2px solid var(--info);
}

@keyframes slideIn {
  to { transform: translateX(0); }
}

.notification-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notification-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
}

/* === RESPONSIVE === */
@media (max-width: 1200px) {
  .picks-layout {
    grid-template-columns: 350px 1fr;
    gap: var(--space-lg);
  }

  .budget-info {
    gap: var(--space-lg);
  }
}

@media (max-width: 968px) {
  .picks-layout {
    grid-template-columns: 1fr;
    gap: var(--space-lg);
    height: auto;
  }

  .selected-picks-panel {
    height: 400px;
  }

  .budget-content {
    flex-direction: column;
    gap: var(--space-md);
    text-align: center;
  }

  .budget-info {
    justify-content: center;
  }

  .panel-controls {
    flex-direction: column;
  }

  .filter-select {
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-md);
  }

  .header-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }

  .event-name {
    font-size: 1.8rem;
  }

  .budget-info {
    flex-direction: column;
    gap: var(--space-md);
  }

  .selected-picks-panel {
    height: 300px;
  }

  .fighter-card,
  .selected-fighter-card {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }

  .fighter-main-info {
    flex-direction: column;
    text-align: center;
  }

  .fighter-meta {
    justify-content: center;
  }

  .actions-content {
    flex-direction: column;
    gap: var(--space-md);
    text-align: center;
  }

  .action-buttons {
    justify-content: center;
    width: 100%;
  }

  .btn-cancel,
  .btn-save {
    flex: 1;
    max-width: 150px;
  }

  .notification {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
}

@media (max-width: 480px) {
  .picks-selection {
    padding-bottom: 140px;
  }

  .event-name {
    font-size: 1.6rem;
  }

  .selected-picks-panel {
    height: 250px;
  }

  .panel-header {
    padding: var(--space-lg);
  }

  .available-fighters-container,
  .selected-picks-container {
    padding: var(--space-md);
  }

  .fighter-card,
  .selected-fighter-card {
    padding: var(--space-md);
  }
}
</style>