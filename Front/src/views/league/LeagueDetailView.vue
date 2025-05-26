<template>
  <div class="league-detail">
    <!-- Fondo estático -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header de la Liga -->
    <div class="league-header">
      <div class="container">
        <div class="header-content">
          <button class="btn btn-back" @click="goBackToDashboard">
            ← Volver a Ligas
          </button>
          
          <div class="league-info">
            <h1 class="league-name">{{ currentLeague.name }}</h1>
            <div class="league-meta">
              <span class="league-type" :class="currentLeague.type.toLowerCase()">
                {{ currentLeague.type === 'PUBLIC' ? '🌍 Pública' : '🔒 Privada' }}
              </span>
              <span class="member-count">👥 {{ currentLeague.memberCount }} miembros</span>
            </div>
          </div>
          
          <div class="league-actions">
            <button class="btn btn-info" @click="showLeagueInfo = true">
              ℹ️ Info
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Navegación por pestañas -->
    <div class="tabs-navigation">
      <div class="container">
        <div class="tabs">
          <!-- Para ligas privadas: Global, Evento Actual y Evento Anterior -->
          <template v-if="currentLeague.type === 'PRIVATE'">
            <button 
              class="tab-button" 
              :class="{ 'active': activeTab === 'global' }"
              @click="activeTab = 'global'"
            >
              🏆 Clasificación Global
            </button>
            <button 
              class="tab-button" 
              :class="{ 'active': activeTab === 'current' }"
              @click="activeTab = 'current'"
              v-if="currentEvent"
            >
              ⚡ Evento Actual
            </button>
            <button 
              class="tab-button" 
              :class="{ 'active': activeTab === 'previous' }"
              @click="activeTab = 'previous'"
              v-if="previousEvent"
            >
              📈 Evento Anterior
            </button>
          </template>
          
          <!-- Para ligas públicas: Solo Evento Actual -->
          <template v-else>
            <button 
              class="tab-button active"
              @click="activeTab = 'current'"
              v-if="currentEvent"
            >
              ⚡ {{ currentEvent.name }}
            </button>
          </template>
        </div>
      </div>
    </div>

    <!-- Contenido de las pestañas -->
    <div class="main-content">
      <div class="container">
        
        <!-- Pestaña: Clasificación Global (Solo para ligas privadas) -->
        <div v-if="activeTab === 'global' && currentLeague.type === 'PRIVATE'" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">🏆 Clasificación Global de la Liga</h2>
            <p class="content-subtitle">Puntuación acumulada de todos los eventos</p>
          </div>

          <div class="leaderboard-container">
            <div class="leaderboard">
              <div 
                v-for="(member, index) in globalLeaderboard" 
                :key="member.id"
                class="leaderboard-item"
                :class="{ 'is-you': member.isCurrentUser, 'top-three': index < 3 }"
              >
                <div class="position">
                  <span class="position-number" :class="getPositionClass(index + 1)">
                    #{{ index + 1 }}
                  </span>
                  <div v-if="index < 3" class="medal">
                    {{ index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉' }}
                  </div>
                </div>
                
                <div class="member-info">
                  <div class="member-avatar">
                    {{ member.username.substring(0, 2).toUpperCase() }}
                  </div>
                  <div class="member-details">
                    <h4 class="member-name">{{ member.username }}</h4>
                    <p class="member-subtitle">{{ member.eventsParticipated }} eventos participados</p>
                  </div>
                </div>
                
                <div class="member-stats">
                  <div class="total-points">{{ member.totalPoints }} pts</div>
                  <div class="last-event-points">Último: {{ member.lastEventPoints }} pts</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Pestaña: Evento Actual -->
        <div v-if="activeTab === 'current' && currentEvent" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">⚡ {{ currentEvent.name }}</h2>
            <p class="content-subtitle">{{ formatDate(currentEvent.date) }} • {{ currentEvent.location }}</p>
            <div class="event-status">
              <span class="status-badge current">🔴 En Curso</span>
            </div>
          </div>

          <!-- Botón para elegir picks (Solo si el usuario no tiene picks o puede modificarlos) -->
          <div v-if="canMakePicks" class="picks-action-section">
            <div class="picks-call-to-action">
              <div class="cta-content">
                <h3 class="cta-title">🎯 ¡Elige tu Lineup!</h3>
                <p class="cta-description">
                  {{ currentUserPicks.length > 0 ? 'Modifica tu lineup antes de que comience el evento' : 'Selecciona hasta 3 luchadores para este evento' }}
                </p>
              </div>
              <button class="btn btn-picks" @click="goToPicksSelection">
                {{ currentUserPicks.length > 0 ? '✏️ Modificar Picks' : '⚔️ Elegir Luchadores' }}
              </button>
            </div>
          </div>

          <!-- Tu lineup para este evento -->
          <div v-if="currentUserPicks.length > 0" class="your-picks-section">
            <h3 class="section-title">👤 Tu Lineup para este Evento</h3>
            <div class="picks-grid">
              <div 
                v-for="pick in currentUserPicks" 
                :key="pick.id"
                class="fighter-pick"
                @click="showFighterDetails(pick)"
              >
                <div class="fighter-avatar">
                  <img v-if="pick.imageUrl" :src="pick.imageUrl" :alt="pick.name" />
                  <span v-else class="fighter-initials">{{ getFighterInitials(pick.name) }}</span>
                </div>
                <div class="fighter-info">
                  <h4 class="fighter-name">{{ pick.name }}</h4>
                  <p class="fighter-record">{{ pick.record }}</p>
                  <div class="fighter-points">
                    <span class="points">{{ pick.points || 0 }} pts</span>
                    <span class="cost">${{ pick.cost }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Clasificación del evento actual -->
          <div class="event-leaderboard-section">
            <h3 class="section-title">📊 Clasificación del Evento</h3>
            <div class="leaderboard">
              <div 
                v-for="(member, index) in currentEventLeaderboard" 
                :key="member.id"
                class="leaderboard-item event-item"
                :class="{ 'is-you': member.isCurrentUser }"
                @click="member.isCurrentUser && showUserEventDetails(member)"
              >
                <div class="position">
                  <span class="position-number">#{{ index + 1 }}</span>
                </div>
                
                <div class="member-info">
                  <div class="member-avatar small">
                    {{ member.username.substring(0, 2).toUpperCase() }}
                  </div>
                  <div class="member-details">
                    <h4 class="member-name">
                      {{ member.username }}
                      <span v-if="member.isCurrentUser" class="you-badge">TÚ</span>
                    </h4>
                    <p class="fighters-count">{{ member.fightersSelected }} luchadores</p>
                  </div>
                </div>
                
                <div class="event-points">
                  <span class="points">{{ member.eventPoints }} pts</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Pestaña: Evento Anterior (Solo para ligas privadas) -->
        <div v-if="activeTab === 'previous' && previousEvent && currentLeague.type === 'PRIVATE'" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">📈 {{ previousEvent.name }}</h2>
            <p class="content-subtitle">{{ formatDate(previousEvent.date) }} • {{ previousEvent.location }}</p>
            <div class="event-status">
              <span class="status-badge completed">✅ Finalizado</span>
            </div>
          </div>

          <!-- Tu lineup del evento anterior -->
          <div v-if="previousUserPicks.length > 0" class="your-picks-section">
            <h3 class="section-title">👤 Tu Lineup Final</h3>
            <div class="picks-grid">
              <div 
                v-for="pick in previousUserPicks" 
                :key="pick.id"
                class="fighter-pick completed"
                @click="showFighterDetails(pick)"
              >
                <div class="fighter-avatar">
                  <img v-if="pick.imageUrl" :src="pick.imageUrl" :alt="pick.name" />
                  <span v-else class="fighter-initials">{{ getFighterInitials(pick.name) }}</span>
                </div>
                <div class="fighter-info">
                  <h4 class="fighter-name">{{ pick.name }}</h4>
                  <p class="fighter-record">{{ pick.record }}</p>
                  <div class="fighter-points">
                    <span class="points final">{{ pick.finalPoints }} pts</span>
                    <span class="result" :class="pick.result">{{ pick.result }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Clasificación final del evento anterior -->
          <div class="event-leaderboard-section">
            <h3 class="section-title">🏁 Clasificación Final</h3>
            <div class="leaderboard">
              <div 
                v-for="(member, index) in previousEventLeaderboard" 
                :key="member.id"
                class="leaderboard-item event-item"
                :class="{ 'is-you': member.isCurrentUser, 'top-three': index < 3 }"
                @click="member.isCurrentUser && showUserEventDetails(member)"
              >
                <div class="position">
                  <span class="position-number" :class="getPositionClass(index + 1)">
                    #{{ index + 1 }}
                  </span>
                  <div v-if="index < 3" class="medal small">
                    {{ index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉' }}
                  </div>
                </div>
                
                <div class="member-info">
                  <div class="member-avatar small">
                    {{ member.username.substring(0, 2).toUpperCase() }}
                  </div>
                  <div class="member-details">
                    <h4 class="member-name">
                      {{ member.username }}
                      <span v-if="member.isCurrentUser" class="you-badge">TÚ</span>
                    </h4>
                    <p class="fighters-count">{{ member.fightersSelected }} luchadores</p>
                  </div>
                </div>
                
                <div class="event-points">
                  <span class="points final">{{ member.finalPoints }} pts</span>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- Modal: Detalles del Luchador -->
    <div v-if="selectedFighter" class="modal-overlay" @click="selectedFighter = null">
      <div class="modal-content fighter-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">{{ selectedFighter.name }}</h3>
          <button class="modal-close" @click="selectedFighter = null">✕</button>
        </div>
        
        <div class="fighter-details">
          <div class="fighter-image">
            <img v-if="selectedFighter.imageUrl" :src="selectedFighter.imageUrl" :alt="selectedFighter.name" />
            <div v-else class="fighter-placeholder">
              {{ getFighterInitials(selectedFighter.name) }}
            </div>
          </div>
          
          <div class="fighter-stats">
            <div class="stat-row">
              <span class="stat-label">Record:</span>
              <span class="stat-value">{{ selectedFighter.record }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">Categoría:</span>
              <span class="stat-value">{{ selectedFighter.weightClass }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">Costo:</span>
              <span class="stat-value">${{ selectedFighter.cost }}</span>
            </div>
          </div>
          
          <div class="points-breakdown">
            <h4 class="breakdown-title">📊 Desglose de Puntos</h4>
            <div class="points-list">
              <div class="points-item">
                <span class="points-category">Victoria/Derrota:</span>
                <span class="points-value">{{ selectedFighter.winPoints || 0 }} pts</span>
              </div>
              <div class="points-item">
                <span class="points-category">Knockdowns:</span>
                <span class="points-value">{{ selectedFighter.knockdownPoints || 0 }} pts</span>
              </div>
              <div class="points-item">
                <span class="points-category">Takedowns:</span>
                <span class="points-value">{{ selectedFighter.takedownPoints || 0 }} pts</span>
              </div>
              <div class="points-item">
                <span class="points-category">Sumisiones:</span>
                <span class="points-value">{{ selectedFighter.submissionPoints || 0 }} pts</span>
              </div>
              <div class="points-item total">
                <span class="points-category">Total:</span>
                <span class="points-value">{{ selectedFighter.finalPoints || selectedFighter.points || 0 }} pts</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Info de la Liga -->
    <div v-if="showLeagueInfo" class="modal-overlay" @click="showLeagueInfo = false">
      <div class="modal-content info-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">📋 Información de la Liga</h3>
          <button class="modal-close" @click="showLeagueInfo = false">✕</button>
        </div>
        
        <div class="league-info-content">
          <div class="info-section">
            <h4 class="info-title">Detalles de la Liga</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Nombre:</span>
                <span class="info-value">{{ currentLeague.name }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Tipo:</span>
                <span class="info-value">{{ currentLeague.type === 'PUBLIC' ? 'Pública' : 'Privada' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Miembros:</span>
                <span class="info-value">{{ currentLeague.memberCount }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Creada:</span>
                <span class="info-value">{{ formatDate(currentLeague.createdAt) }}</span>
              </div>
            </div>
          </div>
          
          <div class="info-section">
            <h4 class="info-title">Reglas</h4>
            <ul class="rules-list">
              <li>Presupuesto inicial: ${{ currentLeague.initialBudget }}</li>
              <li>Máximo {{ currentLeague.maxFightersEvent }} luchadores por evento</li>
              <li>Mínimo {{ currentLeague.minFightersEvent }} luchador por evento</li>
              <li>Los puntos se calculan automáticamente tras cada evento</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotificationModal" class="notification" @click="hideNotification">
      <div class="notification-icon">✅</div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'LeagueDetailView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // Estados principales
    const activeTab = ref('global') // Para privadas inicia en global, para públicas será 'current'
    const selectedFighter = ref(null)
    const showLeagueInfo = ref(false)
    const showNotificationModal = ref(false)
    const notificationText = ref('')

    // Datos de la liga actual (simulados)
    const currentLeague = reactive({
      id: 1,
      name: 'Liga Oficina',
      type: 'PRIVATE', // Cambiar a 'PUBLIC' para probar ligas públicas
      memberCount: 12,
      userPosition: 1,
      description: 'Liga entre compañeros de trabajo',
      createdAt: '2025-01-15',
      initialBudget: 100000,
      maxFightersEvent: 3,
      minFightersEvent: 1
    })

    // Eventos (simulados)
    const currentEvent = reactive({
      id: 1,
      name: 'UFC Vegas 107',
      date: '2025-05-31',
      location: 'Las Vegas, Nevada',
      status: 'IN_PROGRESS'
    })

    const previousEvent = reactive({
      id: 2,
      name: 'UFC 295',
      date: '2025-05-15',
      location: 'Madison Square Garden',
      status: 'COMPLETED'
    })

    // Clasificación global (simulada) - Con últimos puntos del evento
    const globalLeaderboard = ref([
      { id: 1, username: 'usuario_prueba2', totalPoints: 2890, lastEventPoints: 285, eventsParticipated: 10, isCurrentUser: true },
      { id: 2, username: 'fighter_pro', totalPoints: 2750, lastEventPoints: 320, eventsParticipated: 10, isCurrentUser: false },
      { id: 3, username: 'mma_expert', totalPoints: 2680, lastEventPoints: 240, eventsParticipated: 10, isCurrentUser: false },
      { id: 4, username: 'cage_warrior', totalPoints: 2550, lastEventPoints: 190, eventsParticipated: 10, isCurrentUser: false },
      { id: 5, username: 'octagon_king', totalPoints: 2480, lastEventPoints: 260, eventsParticipated: 10, isCurrentUser: false }
      ])
    // Clasificación evento anterior (simulada)
    const previousEventLeaderboard = ref([
      { id: 1, username: 'usuario_prueba2', finalPoints: 320, fightersSelected: 3, isCurrentUser: true },
      { id: 2, username: 'mma_expert', finalPoints: 295, fightersSelected: 3, isCurrentUser: false },
      { id: 3, username: 'fighter_pro', finalPoints: 285, fightersSelected: 2, isCurrentUser: false },
      { id: 4, username: 'cage_warrior', finalPoints: 260, fightersSelected: 3, isCurrentUser: false }
    ])

    // Picks del usuario para evento anterior (simulados)
    const previousUserPicks = ref([
      { 
        id: 4, 
        name: 'Jon Jones', 
        record: '27-1', 
        cost: 75000, 
        finalPoints: 150, 
        result: 'WIN',
        weightClass: 'Heavyweight',
        winPoints: 100,
        knockdownPoints: 30,
        takedownPoints: 20,
        submissionPoints: 0
      },
      { 
        id: 5, 
        name: 'Ilia Topuria', 
        record: '14-0', 
        cost: 65000, 
        finalPoints: 120, 
        result: 'WIN',
        weightClass: 'Featherweight',
        winPoints: 100,
        knockdownPoints: 20,
        takedownPoints: 0,
        submissionPoints: 0
      },
      { 
        id: 6, 
        name: 'Ciryl Gane', 
        record: '11-2', 
        cost: 45000, 
        finalPoints: 50, 
        result: 'LOSS',
        weightClass: 'Heavyweight',
        winPoints: 0,
        knockdownPoints: 50,
        takedownPoints: 0,
        submissionPoints: 0
      }
    ])

    // Clasificación evento actual (simulada)
    const currentEventLeaderboard = ref([
      { id: 1, username: 'fighter_pro', eventPoints: 285, fightersSelected: 3, isCurrentUser: false },
      { id: 2, username: 'usuario_prueba2', eventPoints: 265, fightersSelected: 3, isCurrentUser: true },
      { id: 3, username: 'mma_expert', eventPoints: 240, fightersSelected: 2, isCurrentUser: false },
      { id: 4, username: 'cage_warrior', eventPoints: 220, fightersSelected: 3, isCurrentUser: false }
    ])

    // Picks del usuario para evento actual (simulados)
    const currentUserPicks = ref([
      { 
        id: 1, 
        name: 'Jon Jones', 
        record: '27-1', 
        cost: 75000, 
        points: 120, 
        weightClass: 'Heavyweight',
        imageUrl: 'https://dynl.mktgcdn.com/p/xnYGAGWMpbap_gSsJDIFPvBDPsTv5j_W3V0gCeAFyIQ/200x1.png'
      },
      { 
        id: 2, 
        name: 'Ilia Topuria', 
        record: '14-0', 
        cost: 65000, 
        points: 95, 
        weightClass: 'Featherweight',
        imageUrl: 'https://dynl.mktgcdn.com/p/jMTyWWXdzwWDaswQzek-X6JLObRd1otuQaU4KQZElfw/200x1.png'
      },
      { 
        id: 3, 
        name: 'Ciryl Gane', 
        record: '11-2', 
        cost: 45000, 
        points: 50, 
        weightClass: 'Heavyweight',
        imageUrl: 'https://dynl.mktgcdn.com/p/HREUg-pySQqB86Xxda5JUqmFInPekYteFBioxw1yUJw/200x1.png'
      }
    ])

    // Computed properties
    const canMakePicks = computed(() => {
      // Lógica para determinar si se pueden hacer picks
      // Por ejemplo: evento no ha comenzado, dentro del tiempo límite, etc.
      return currentEvent.status === 'IN_PROGRESS' || currentEvent.status === 'UPCOMING'
    })

    // Inicializar tab activo basado en el tipo de liga
    onMounted(() => {
      if (currentLeague.type === 'PUBLIC') {
        activeTab.value = 'current'
      }
    })

    // Funciones
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const getPositionClass = (position) => {
      if (position === 1) return 'gold'
      if (position === 2) return 'silver'
      if (position === 3) return 'bronze'
      return ''
    }

    const getFighterInitials = (name) => {
      return name.split(' ').map(word => word[0]).join('').toUpperCase()
    }

    const showFighterDetails = (fighter) => {
      selectedFighter.value = fighter
    }

    const showUserEventDetails = (member) => {
      if (member.isCurrentUser) {
        displayNotification('Aquí verías los detalles de tu lineup para este evento')
      }
    }

    const goBackToDashboard = () => {
      router.push('/dashboard')
    }

    const goToPicksSelection = () => {
  // Navegar a la pantalla de selección de picks
      router.push(`/league/${currentLeague.id}/picks/${currentEvent.id}`)
    }

    const displayNotification = (message) => {
      notificationText.value = message
      showNotificationModal.value = true
      setTimeout(() => {
        showNotificationModal.value = false
      }, 3000)
    }

    const hideNotification = () => {
      showNotificationModal.value = false
    }

    // Cargar datos de la liga al montar
    onMounted(() => {
      // TODO: Cargar datos reales de la liga basándose en route.params.id
      const leagueId = route.params.id
      console.log('Cargando liga:', leagueId)
      
      // TODO: Hacer llamadas al API para obtener datos reales
    })

    return {
      activeTab,
      selectedFighter,
      showLeagueInfo,
      showNotificationModal,
      notificationText,
      currentLeague,
      currentEvent,
      previousEvent,
      globalLeaderboard,
      currentEventLeaderboard,
      previousEventLeaderboard,
      currentUserPicks,
      previousUserPicks,
      canMakePicks,
      formatDate,
      getPositionClass,
      getFighterInitials,
      showFighterDetails,
      showUserEventDetails,
      goBackToDashboard,
      goToPicksSelection,
      hideNotification
    }
  }
}
</script>

<style scoped>
.league-detail {
  position: relative;
  min-height: 100vh;
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
.league-header {
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

.league-info {
  flex: 1;
  text-align: center;
}

.league-name {
  font-family: var(--font-impact);
  font-size: 2.5rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  margin-bottom: var(--space-sm);
}

.league-meta {
  display: flex;
  justify-content: center;
  gap: var(--space-lg);
  flex-wrap: wrap;
}

.league-type,
.member-count {
  background: rgba(255, 255, 255, 0.1);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-full);
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--white);
}

.league-type.public {
  border: 1px solid var(--info);
  color: var(--info);
}

.league-type.private {
  border: 1px solid var(--warning);
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

.btn-info {
  background: transparent;
  border: 1px solid var(--info);
  color: var(--info);
}

.btn-info:hover {
  background: var(--info);
  color: var(--white);
}

.btn-picks {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-lg) var(--space-xl);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  min-height: 50px;
}

.btn-picks:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

/* === NAVEGACIÓN POR PESTAÑAS === */
.tabs-navigation {
  position: relative;
  z-index: 1;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.tabs {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
  padding: var(--space-md) 0;
}

.tab-button {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
  padding: var(--space-md) var(--space-xl);
  border-radius: var(--radius-lg);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.tab-button:hover {
  border-color: rgba(255, 107, 53, 0.5);
  color: var(--white);
}

.tab-button.active {
  background: var(--gradient-primary);
  border-color: var(--primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.content-header {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.content-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.content-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
  margin-bottom: var(--space-md);
}

.event-status {
  display: flex;
  justify-content: center;
}

.status-badge {
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-full);
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.status-badge.current {
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid var(--error);
  color: var(--error);
}

.status-badge.completed {
  background: rgba(16, 185, 129, 0.2);
  border: 1px solid var(--success);
  color: var(--success);
}

/* === SECCIÓN DE PICKS === */
.picks-action-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  margin-bottom: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.picks-call-to-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.cta-content {
  flex: 1;
}

.cta-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.cta-description {
  color: var(--gray-light);
  font-size: 1rem;
  line-height: 1.5;
}

/* === CLASIFICACIONES === */
.leaderboard-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-lg);
}

.leaderboard {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.leaderboard-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  transition: all 0.3s ease;
  cursor: pointer;
}

.leaderboard-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.leaderboard-item.is-you {
  border-color: var(--primary);
  background: rgba(255, 107, 53, 0.1);
}

.leaderboard-item.top-three {
  border-color: var(--warning);
  background: rgba(245, 158, 11, 0.1);
}

.position {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  min-width: 80px;
}

.position-number {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--white);
}

.position-number.gold { color: #ffd700; }
.position-number.silver { color: #c0c0c0; }
.position-number.bronze { color: #cd7f32; }

.medal {
  font-size: 1.5rem;
}

.medal.small {
  font-size: 1.2rem;
}

.member-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex: 1;
}

.member-avatar {
  width: 50px;
  height: 50px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: var(--white);
  font-size: 1.2rem;
}

.member-avatar.small {
  width: 40px;
  height: 40px;
  font-size: 1rem;
}

.member-name {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.you-badge {
  background: var(--gradient-primary);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: bold;
}

.member-subtitle,
.fighters-count {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.member-stats {
  text-align: right;
}

.total-points,
.event-points .points {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--primary);
  margin-bottom: var(--space-xs);
}

.last-event-points {
  color: var(--gray-light);
  font-size: 0.9rem;
}

/* === TU LINEUP === */
.your-picks-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  margin-bottom: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.section-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.picks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-lg);
}

.fighter-pick {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  cursor: pointer;
  transition: all 0.3s ease;
}

.fighter-pick:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
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
  flex-shrink: 0;
}

.fighter-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fighter-initials {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
}

.fighter-info {
  flex: 1;
}

.fighter-name {
  font-family: var(--font-impact);
  font-size: 1.1rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
}

.fighter-record {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin-bottom: var(--space-sm);
}

.fighter-points {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--primary);
}

.cost {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.fighter-pick.completed {
  border-color: var(--success);
}

.points.final {
  color: var(--success);
}

.result {
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
}

.result.WIN {
  background: rgba(16, 185, 129, 0.2);
  color: var(--success);
}

.result.LOSS {
  background: rgba(239, 68, 68, 0.2);
  color: var(--error);
}

/* === SECCIÓN DE CLASIFICACIÓN === */
.event-leaderboard-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-lg);
}

.event-item {
  cursor: default;
}

.event-item.is-you {
  cursor: pointer;
}

/* === MODALES === */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-lg);
}

.modal-content {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  text-transform: uppercase;
}

.modal-close {
  background: none;
  border: none;
  color: var(--gray-light);
  font-size: 1.5rem;
  cursor: pointer;
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}

.modal-close:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

/* === DETALLES DEL LUCHADOR === */
.fighter-details {
  padding: var(--space-xl);
}

.fighter-image {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.fighter-image img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary);
}

.fighter-placeholder {
  width: 120px;
  height: 120px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  font-family: var(--font-impact);
  font-size: 2.5rem;
  color: var(--white);
}

.fighter-stats {
  margin-bottom: var(--space-xl);
}

.stat-row {
  display: flex;
  justify-content: space-between;
  padding: var(--space-md) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-label {
  color: var(--gray-light);
  font-weight: 600;
}

.stat-value {
  color: var(--white);
  font-weight: bold;
}

.breakdown-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
}

.points-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.points-item {
  display: flex;
  justify-content: space-between;
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
}

.points-item.total {
  background: rgba(255, 107, 53, 0.2);
  border: 1px solid var(--primary);
  font-weight: bold;
}

.points-category {
  color: var(--gray-light);
}

.points-value {
  color: var(--primary);
  font-weight: bold;
}

/* === INFO DE LA LIGA === */
.league-info-content {
  padding: var(--space-xl);
}

.info-section {
  margin-bottom: var(--space-xl);
}

.info-title {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.info-label {
  color: var(--gray-light);
  font-size: 0.9rem;
  font-weight: 600;
}

.info-value {
  color: var(--white);
  font-weight: bold;
}

.rules-list {
  color: var(--gray-light);
  margin: 0;
  padding-left: var(--space-lg);
}

.rules-list li {
  margin-bottom: var(--space-sm);
  line-height: 1.5;
}

/* === NOTIFICACIÓN === */
.notification {
  position: fixed;
  top: var(--space-xl);
  right: var(--space-xl);
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid var(--success);
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
  .picks-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .picks-call-to-action {
    flex-direction: column;
    text-align: center;
    gap: var(--space-lg);
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

  .league-meta {
    flex-direction: column;
    gap: var(--space-sm);
  }

  .tabs {
    flex-direction: column;
    align-items: center;
  }

  .tab-button {
    width: 100%;
    max-width: 280px;
  }

  .league-name {
    font-size: 2rem;
  }

  .leaderboard-item {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }

  .member-info {
    flex-direction: column;
    text-align: center;
  }

  .member-stats {
    text-align: center;
  }

  .fighter-pick {
    flex-direction: column;
    text-align: center;
  }

  .fighter-points {
    justify-content: center;
    gap: var(--space-lg);
  }
}

@media (max-width: 480px) {
  .league-name {
    font-size: 1.8rem;
  }

  .your-picks-section,
  .event-leaderboard-section,
  .leaderboard-container,
  .picks-action-section {
    padding: var(--space-lg);
  }

  .modal-content {
    margin: var(--space-md);
    max-width: none;
  }
}
</style>