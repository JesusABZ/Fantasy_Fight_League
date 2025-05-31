<template>
  <div class="picks-selection">
    <!-- Fondo estático -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Loading overlay -->
    <div v-if="isLoadingLeague || isLoadingEvent || isLoadingFighters" class="loading-overlay">
      <div class="loading-content">
        <div class="loading-spinner">⏳</div>
        <h3 class="loading-title">Cargando datos del evento...</h3>
        <p class="loading-text">
          <span v-if="isLoadingLeague">Cargando liga...</span>
          <span v-else-if="isLoadingEvent">Cargando evento...</span>
          <span v-else-if="isLoadingFighters">Cargando luchadores...</span>
        </p>
      </div>
    </div>

    <!-- Contenido principal -->
    <div v-else class="content-wrapper">
      <!-- Header -->
      <div class="picks-header">
        <div class="container">
          <div class="header-content">
            <button class="btn btn-back" @click="goBack">
              ← Volver a Liga
            </button>
            
            <div class="event-info">
              <h1 class="event-name">{{ currentEvent?.name || 'Cargando evento...' }}</h1>
              <p v-if="currentEvent?.startDate" class="event-details">
                {{ formatDate(currentEvent.startDate) }} • {{ currentEvent.location || 'TBD' }}
              </p>
            </div>
            
            <div class="league-info-small">
              <span class="league-name-small">{{ currentLeague?.name || 'Cargando...' }}</span>
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
                <!-- Loading de picks existentes -->
                <div v-if="isLoadingPick" class="loading-picks">
                  <div class="loading-spinner-small">⏳</div>
                  <p>Cargando tus picks...</p>
                </div>
                
                <!-- Lista de luchadores seleccionados -->
                <div v-else-if="selectedFighters.length > 0" class="selected-fighters-list">
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
                      <div class="fighter-nationality">🌍 {{ fighter.nationality }}</div>
                      <div class="fighter-badges">
                        <span class="weight-class-badge">{{ fighter.weightClass }}</span>
                        <span class="fight-type-badge">{{ fighter.fightType }}</span>
                      </div>
                    </div>
                    
                    <div class="fighter-actions-section">
                      <div class="fighter-cost">${{ formatMoney(fighter.price || fighter.cost) }}</div>
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
                  <p class="empty-description">Elige entre {{ minFighters }} y {{ maxFighters }} luchadores de la lista</p>
                </div>
              </div>
            </div>

            <!-- Panel derecho: Luchadores disponibles -->
            <div class="available-fighters-panel">
              <div class="panel-header">
                <h2 class="panel-title">⚔️ Luchadores Disponibles</h2>
                <span class="panel-subtitle">{{ filteredAvailableFighters.length }} luchadores en esta cartelera</span>
              </div>
              
              <div class="available-fighters-container">
                <div v-if="filteredAvailableFighters.length > 0" class="fighters-list">
                  <div 
                    v-for="fighter in filteredAvailableFighters" 
                    :key="fighter.id"
                    class="fighter-card"
                    :class="{ 
                      'selected': isSelected(fighter.id),
                      'too-expensive': !canAfford(fighter.price || fighter.cost),
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
                        <p class="fighter-nationality">🌍 {{ fighter.nationality }}</p>
                        <div class="fighter-meta">
                          <span class="weight-class">{{ fighter.weightClass }}</span>
                          <span class="fight-type">{{ fighter.fightType }}</span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="fighter-selection">
                      <div class="fighter-cost">
                        <span class="cost-amount">${{ formatMoney(fighter.price || fighter.cost) }}</span>
                      </div>
                      <div class="selection-indicator">
                        <span v-if="isSelected(fighter.id)" class="selected-check">✓</span>
                        <span v-else-if="!canAfford(fighter.price || fighter.cost)" class="too-expensive-icon">💰</span>
                        <span v-else-if="selectedFighters.length >= maxFighters" class="max-reached-icon">🚫</span>
                        <span v-else class="add-icon">+</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Estado sin luchadores -->
                <div v-else class="no-fighters">
                  <div class="no-fighters-icon">🔍</div>
                  <h3 class="no-fighters-title">No se encontraron luchadores</h3>
                  <p class="no-fighters-description">No hay luchadores disponibles para este evento</p>
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
                <span v-if="hasChanges" class="changes-indicator">• Cambios sin guardar</span>
              </span>
            </div>
            <div class="action-buttons">
              <button 
                class="btn btn-cancel"
                @click="cancelChanges"
                :disabled="isSaving"
              >
                Cancelar
              </button>
              <button 
                class="btn btn-save"
                :disabled="!canSavePicks"
                :class="{ 'disabled': !canSavePicks }"
                @click="savePicks"
              >
                <span v-if="isSaving">
                  <span class="loading-spinner-inline">⏳</span>
                  Guardando...
                </span>
                <span v-else>💾 Guardar Picks</span>
              </button>
            </div>
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
import { onMounted } from 'vue'
import { usePicksSelection } from '../composables/usePicksSelection.js'

export default {
  name: 'PicksSelectionView',
  setup() {
    // Usar el composable que maneja toda la lógica
    const picksSelection = usePicksSelection()

    // Cargar datos cuando se monta el componente
    onMounted(() => {
      picksSelection.loadAllData()
    })

    // Retornar todas las propiedades y funciones del composable
    return {
      ...picksSelection
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

/* === LOADING OVERLAY === */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  text-align: center;
  background: var(--gradient-card);
  padding: var(--space-2xl);
  border-radius: var(--radius-xl);
  border: 2px solid rgba(255, 107, 53, 0.3);
  backdrop-filter: blur(15px);
  max-width: 400px;
}

.loading-spinner {
  font-size: 3rem;
  margin-bottom: var(--space-lg);
  animation: spin 2s linear infinite;
}

.loading-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
}

.loading-text {
  color: var(--gray-light);
  font-size: 1rem;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* === LOADING PICKS === */
.loading-picks {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-2xl);
  text-align: center;
}

.loading-spinner-small {
  font-size: 2rem;
  margin-bottom: var(--space-md);
  animation: spin 1s linear infinite;
}

.loading-spinner-inline {
  display: inline-block;
  margin-right: var(--space-sm);
  animation: spin 1s linear infinite;
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
  margin-bottom: var(--space-xs);
}

.fighter-nationality {
  color: var(--gray-light);
  font-size: 0.9rem;
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

.fighter-nationality {
  color: var(--gray-light);
  font-size: 0.85rem;
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

.changes-indicator {
  color: var(--warning);
  font-weight: 600;
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

.btn-cancel:hover:not(:disabled) {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

.btn-cancel:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

/* === TÍTULOS CON FONT-IMPACT - LETTER SPACING === */
.event-name {
  letter-spacing: 0.05em; /* Añade esta línea */
}

.panel-title {
  letter-spacing: 0.05em; /* Añade esta línea */
}

.fighter-name {
  letter-spacing: 0.05em; /* Añade esta línea */
}

.loading-title {
  letter-spacing: 0.05em; /* Añade esta línea */
}

.empty-title {
  letter-spacing: 0.05em; /* Añade esta línea */
}

.no-fighters-title {
  letter-spacing: 0.05em; /* Añade esta línea */
}
</style>