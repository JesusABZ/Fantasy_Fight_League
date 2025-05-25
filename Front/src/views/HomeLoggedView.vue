<template>
  <div class="home-logged">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header del usuario más compacto -->
    <div class="user-header">
      <div class="container">
        <div class="header-content">
          <div class="welcome-section">
            <h1 class="title-hero">¡BIENVENIDO, {{ userDisplayName }}!</h1>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-icon">🏆</span>
                <span class="stat-text">5 Ligas</span>
              </div>
              <div class="stat-item">
                <span class="stat-icon">⚔️</span>
                <span class="stat-text">2,450 Puntos</span>
              </div>
              <div class="stat-item">
                <span class="stat-icon">🥇</span>
                <span class="stat-text">#3 Global</span>
              </div>
            </div>
          </div>
          <div class="user-actions">
            <button class="btn btn-profile" @click="goToProfile">
              <span class="profile-avatar">{{ userInitials }}</span>
              Mi Perfil
            </button>
            <button class="btn btn-logout" @click="handleLogout">
              🚪 Salir
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Dashboard Grid Layout -->
    <div class="dashboard-content">
      <div class="container">
        <div class="dashboard-grid">
          
          <!-- Card 1: Próximo Evento -->
          <div class="dashboard-card next-event-card">
            <div class="card-header">
              <h3 class="card-title">🔥 Próximo Evento</h3>
              <span class="event-date">31 MAY</span>
            </div>
            <div class="event-preview">
              <div class="event-image">
                <img src="/images/ufc-event-vegas-107.jpg" alt="UFC Vegas 107" />
                <div class="event-badge">UFC VEGAS 107</div>
              </div>
              <div class="event-info">
                <h4 class="event-title">BLANCHFIELD VS BARBER</h4>
                <p class="event-time">⏰ 21:00 GMT</p>
                <button class="btn btn-event">Ver Cartelera</button>
              </div>
            </div>
          </div>

          <!-- Card 2: Mis Ligas Resumen -->
          <div class="dashboard-card my-leagues-summary">
            <div class="card-header">
              <h3 class="card-title">⚔️ Mis Ligas</h3>
              <button class="btn-see-all" @click="scrollToMyLeagues">Ver todas</button>
            </div>
            <div class="leagues-summary">
              <div class="league-quick-stats">
                <div class="quick-stat">
                  <span class="quick-number">5</span>
                  <span class="quick-label">Ligas Activas</span>
                </div>
                <div class="quick-stat">
                  <span class="quick-number">#1</span>
                  <span class="quick-label">Mejor Posición</span>
                </div>
                <div class="quick-stat">
                  <span class="quick-number">89%</span>
                  <span class="quick-label">Win Rate</span>
                </div>
              </div>
              <div class="top-league">
                <div class="league-mini">
                  <span class="league-rank">#1</span>
                  <div class="league-details">
                    <span class="league-name">Liga Oficina</span>
                    <span class="league-points">1,890 pts</span>
                  </div>
                  <span class="league-badge">🔥</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Card 3: Unirse a Ligas Rápido -->
          <div class="dashboard-card join-quick">
            <div class="card-header">
              <h3 class="card-title">🏆 Unirse Rápido</h3>
            </div>
            <div class="quick-join">
              <div class="join-option" @click="joinPublicLeague">
                <div class="join-icon">🌍</div>
                <div class="join-text">
                  <span class="join-title">Liga Pública</span>
                  <span class="join-subtitle">Únete a una liga existente</span>
                </div>
                <div class="join-arrow">→</div>
              </div>
              <div class="join-separator"></div>
              <div class="private-join-form">
                <input
                  v-model="privateCode"
                  type="text"
                  class="quick-input"
                  placeholder="Código de invitación"
                  maxlength="8"
                  @keyup.enter="joinPrivateQuick"
                />
                <button 
                  class="btn btn-join-private"
                  @click="joinPrivateQuick"
                  :disabled="!privateCode.trim()"
                >
                  🔐
                </button>
              </div>
            </div>
          </div>

          <!-- Card 4: Últimos Resultados -->
          <div class="dashboard-card recent-results">
            <div class="card-header">
              <h3 class="card-title">📊 Últimos Resultados</h3>
            </div>
            <div class="results-list">
              <div class="result-item">
                <div class="result-event">UFC 297</div>
                <div class="result-score">+185 pts</div>
                <div class="result-position">#2</div>
              </div>
              <div class="result-item">
                <div class="result-event">UFC Vegas 85</div>
                <div class="result-score">+92 pts</div>
                <div class="result-position">#7</div>
              </div>
              <div class="result-item">
                <div class="result-event">UFC 296</div>
                <div class="result-score">+234 pts</div>
                <div class="result-position">#1</div>
              </div>
            </div>
          </div>

          <!-- Card 5: Leaderboard Global -->
          <div class="dashboard-card global-leaderboard">
            <div class="card-header">
              <h3 class="card-title">🌟 Top Fighters Global</h3>
            </div>
            <div class="leaderboard-mini">
              <div class="leader-item">
                <span class="leader-rank">1</span>
                <div class="leader-info">
                  <span class="leader-name">FighterKing</span>
                  <span class="leader-points">3,456 pts</span>
                </div>
                <span class="leader-badge">👑</span>
              </div>
              <div class="leader-item">
                <span class="leader-rank">2</span>
                <div class="leader-info">
                  <span class="leader-name">MMAExpert</span>
                  <span class="leader-points">3,234 pts</span>
                </div>
                <span class="leader-badge">🥈</span>
              </div>
              <div class="leader-item you">
                <span class="leader-rank">3</span>
                <div class="leader-info">
                  <span class="leader-name">{{ userDisplayName }} (Tú)</span>
                  <span class="leader-points">2,450 pts</span>
                </div>
                <span class="leader-badge">🔥</span>
              </div>
            </div>
          </div>

          <!-- Card 6: Estadísticas Rápidas -->
          <div class="dashboard-card quick-stats">
            <div class="card-header">
              <h3 class="card-title">📈 Tus Stats</h3>
            </div>
            <div class="stats-grid">
              <div class="stat-box">
                <span class="stat-value">23</span>
                <span class="stat-label">Eventos</span>
              </div>
              <div class="stat-box">
                <span class="stat-value">67%</span>
                <span class="stat-label">Top 3</span>
              </div>
              <div class="stat-box">
                <span class="stat-value">12</span>
                <span class="stat-label">Victorias</span>
              </div>
              <div class="stat-box">
                <span class="stat-value">156</span>
                <span class="stat-label">Avg Points</span>
              </div>
            </div>
          </div>

        </div>

        <!-- Sección expandida: Mis Ligas (si se necesita ver todas) -->
        <div id="my-leagues-section" class="expanded-section" v-if="showExpandedLeagues">
          <div class="section-header">
            <h2 class="section-title">⚔️ Todas Mis Ligas</h2>
            <button class="btn-collapse" @click="showExpandedLeagues = false">
              Ocultar
            </button>
          </div>
          
          <div class="leagues-grid">
            <div 
              v-for="league in myLeagues" 
              :key="league.id"
              class="league-card-detailed"
              @click="goToLeague(league)"
            >
              <div class="league-header">
                <h4 class="league-name">{{ league.name }}</h4>
                <span class="league-type" :class="league.type.toLowerCase()">
                  {{ league.type === 'PUBLIC' ? '🌍' : '🔒' }}
                </span>
              </div>
              <div class="league-stats-detailed">
                <div class="stat-detailed">
                  <span class="stat-icon">👥</span>
                  <span class="stat-text">{{ league.memberCount }} miembros</span>
                </div>
                <div class="stat-detailed">
                  <span class="stat-icon">🏆</span>
                  <span class="stat-text">Posición #{{ league.userPosition }}</span>
                </div>
                <div class="stat-detailed">
                  <span class="stat-icon">⭐</span>
                  <span class="stat-text">{{ league.userPoints }} puntos</span>
                </div>
              </div>
              <div class="league-action">
                <button class="btn btn-enter-league">Entrar a Liga</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Mensaje flotante -->
    <div v-if="showMessage" class="floating-message" :class="messageType" @click="hideMessage">
      <div class="message-icon">
        <span v-if="messageType === 'success'">✅</span>
        <span v-else>❌</span>
      </div>
      <div class="message-text">{{ messageText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'HomeLoggedView',
  setup() {
    const router = useRouter()

    // Estado del usuario (simulado)
    const currentUser = ref({
      id: 1,
      username: 'fighter_user',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com'
    })

    // Estados
    const privateCode = ref('')
    const showExpandedLeagues = ref(false)
    const showMessage = ref(false)
    const messageType = ref('success')
    const messageText = ref('')

    // Datos simulados de ligas
    const myLeagues = ref([
      {
        id: 1,
        name: 'Liga Oficina',
        type: 'PRIVATE',
        memberCount: 12,
        userPosition: 1,
        userPoints: 1890
      },
      {
        id: 2,
        name: 'Amigos Luchadores',
        type: 'PRIVATE',
        memberCount: 8,
        userPosition: 3,
        userPoints: 1250
      },
      {
        id: 3,
        name: 'UFC Vegas 107',
        type: 'PUBLIC',
        memberCount: 156,
        userPosition: 5,
        userPoints: 980
      }
    ])

    // Computed properties
    const userDisplayName = computed(() => {
      if (currentUser.value.firstName && currentUser.value.lastName) {
        return `${currentUser.value.firstName} ${currentUser.value.lastName}`
      }
      return currentUser.value.username
    })

    const userInitials = computed(() => {
      if (currentUser.value.firstName && currentUser.value.lastName) {
        return `${currentUser.value.firstName[0]}${currentUser.value.lastName[0]}`.toUpperCase()
      }
      return currentUser.value.username.substring(0, 2).toUpperCase()
    })

    // Funciones
    const joinPublicLeague = () => {
      showFloatingMessage('success', '¡Te has unido a la liga pública!')
    }

    const joinPrivateQuick = () => {
      if (!privateCode.value.trim()) return
      showFloatingMessage('success', `¡Te has unido usando el código: ${privateCode.value}!`)
      privateCode.value = ''
    }

    const scrollToMyLeagues = () => {
      showExpandedLeagues.value = !showExpandedLeagues.value
    }

    const goToLeague = (league) => {
      console.log('Navegando a liga:', league.name)
      showFloatingMessage('success', `Abriendo liga: ${league.name}`)
    }

    const goToProfile = () => {
      router.push('/profile')
    }

    const handleLogout = () => {
      console.log('Cerrando sesión...')
      router.push('/')
    }

    const showFloatingMessage = (type, text) => {
      messageType.value = type
      messageText.value = text
      showMessage.value = true
      
      setTimeout(() => {
        hideMessage()
      }, 3000)
    }

    const hideMessage = () => {
      showMessage.value = false
    }

    return {
      currentUser,
      userDisplayName,
      userInitials,
      privateCode,
      showExpandedLeagues,
      myLeagues,
      showMessage,
      messageType,
      messageText,
      joinPublicLeague,
      joinPrivateQuick,
      scrollToMyLeagues,
      goToLeague,
      goToProfile,
      handleLogout,
      hideMessage
    }
  }
}
</script>

<style scoped>
.home-logged {
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

/* === HEADER COMPACTO === */
.user-header {
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

.title-hero {
  font-family: var(--font-impact);
  font-size: clamp(1.8rem, 3vw, 2.5rem);
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.user-stats {
  display: flex;
  gap: var(--space-lg);
  margin-top: var(--space-sm);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  background: rgba(255, 255, 255, 0.1);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.stat-icon {
  font-size: 1rem;
}

.stat-text {
  color: var(--white);
  font-size: 0.9rem;
  font-weight: 600;
}

.user-actions {
  display: flex;
  gap: var(--space-sm);
  align-items: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-md);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.btn-profile {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: rgba(255, 255, 255, 0.1);
  color: var(--white);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.profile-avatar {
  width: 28px;
  height: 28px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.75rem;
}

.btn-logout {
  background: transparent;
  color: var(--error);
  border: 1px solid var(--error);
}

.btn-logout:hover {
  background: var(--error);
  color: var(--white);
}

/* === DASHBOARD GRID === */
.dashboard-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--space-lg);
  margin-bottom: var(--space-2xl);
}

/* === DASHBOARD CARDS === */
.dashboard-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-lg);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.dashboard-card:hover {
  transform: translateY(-3px);
  border-color: rgba(255, 107, 53, 0.3);
  box-shadow: var(--shadow-lg), 0 0 20px rgba(255, 107, 53, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-lg);
}

.card-title {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--white);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

/* === PRÓXIMO EVENTO === */
.next-event-card {
  grid-column: span 2;
}

.event-date {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-md);
  font-weight: bold;
  font-size: 0.8rem;
}

.event-preview {
  display: flex;
  gap: var(--space-lg);
  align-items: center;
}

.event-image {
  position: relative;
  width: 150px;
  height: 100px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  flex-shrink: 0;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-badge {
  position: absolute;
  top: var(--space-xs);
  right: var(--space-xs);
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-xs);
  border-radius: var(--radius-sm);
  font-size: 0.7rem;
  font-weight: bold;
}

.event-info {
  flex: 1;
}

.event-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.event-time {
  color: var(--gray-light);
  margin-bottom: var(--space-md);
}

.btn-event {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
}

/* === RESUMEN DE LIGAS === */
.leagues-summary {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.league-quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
}

.quick-stat {
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  padding: var(--space-md);
  border-radius: var(--radius-md);
}

.quick-number {
  display: block;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary);
  margin-bottom: var(--space-xs);
}

.quick-label {
  font-size: 0.8rem;
  color: var(--gray-light);
  text-transform: uppercase;
}

.league-mini {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  background: rgba(255, 107, 53, 0.1);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.league-rank {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
  font-weight: bold;
}

.league-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.league-name {
  color: var(--white);
  font-weight: 600;
  font-size: 0.9rem;
}

.league-points {
  color: var(--primary);
  font-size: 0.8rem;
}

.league-badge {
  font-size: 1.2rem;
}

/* === UNIRSE RÁPIDO === */
.join-option {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: var(--space-md);
}

.join-option:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.join-icon {
  font-size: 1.5rem;
}

.join-text {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.join-title {
  color: var(--white);
  font-weight: 600;
  font-size: 0.9rem;
}

.join-subtitle {
  color: var(--gray-light);
  font-size: 0.8rem;
}

.join-arrow {
  color: var(--primary);
  font-weight: bold;
  transition: transform 0.3s ease;
}

.join-option:hover .join-arrow {
  transform: translateX(3px);
}

.join-separator {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: var(--space-md) 0;
}

.private-join-form {
  display: flex;
  gap: var(--space-sm);
}

.quick-input {
  flex: 1;
  padding: var(--space-sm);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 0.9rem;
}

.quick-input::placeholder {
  color: var(--gray-light);
}

.btn-join-private {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm);
  border-radius: var(--radius-md);
  width: 40px;
  height: 40px;
}

/* === ÚLTIMOS RESULTADOS === */
.results-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
}

.result-event {
  color: var(--white);
  font-weight: 600;
  font-size: 0.9rem;
}

.result-score {
  color: var(--success);
  font-weight: bold;
  font-size: 0.9rem;
}

.result-position {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: bold;
}

/* === LEADERBOARD GLOBAL === */
.leaderboard-mini {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.leader-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-sm);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
}

.leader-item.you {
  background: rgba(255, 107, 53, 0.1);
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.leader-rank {
  width: 24px;
  height: 24px;
  background: var(--gray);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
  font-weight: bold;
  font-size: 0.8rem;
}

.leader-item.you .leader-rank {
  background: var(--gradient-primary);
}

.leader-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.leader-name {
  color: var(--white);
  font-weight: 600;
  font-size: 0.9rem;
}

.leader-points {
  color: var(--gray-light);
  font-size: 0.8rem;
}

.leader-badge {
  font-size: 1.2rem;
}

/* === ESTADÍSTICAS RÁPIDAS === */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-md);
}

.stat-box {
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.stat-box:hover {
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.stat-value {
  display: block;
  font-size: 1.8rem;
  font-weight: bold;
  color: var(--primary);
  margin-bottom: var(--space-xs);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--gray-light);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* === BOTONES AUXILIARES === */
.btn-see-all {
  background: none;
  border: none;
  color: var(--primary);
  font-size: 0.8rem;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.3s ease;
}

.btn-see-all:hover {
  color: var(--primary-light);
}

.btn-collapse {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  cursor: pointer;
}

/* === SECCIÓN EXPANDIDA === */
.expanded-section {
  margin-top: var(--space-2xl);
  padding: var(--space-xl);
  background: var(--gradient-card);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);
}

.section-title {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  font-weight: 400;
  color: var(--white);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.leagues-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-lg);
}

.league-card-detailed {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.league-card-detailed:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.league-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-md);
}

.league-name {
  font-family: var(--font-impact);
  font-size: 1.1rem;
  color: var(--white);
  text-transform: uppercase;
}

.league-type {
  font-size: 1.2rem;
}

.league-stats-detailed {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  margin-bottom: var(--space-lg);
}

.stat-detailed {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.stat-detailed .stat-icon {
  width: 20px;
  text-align: center;
}

.stat-detailed .stat-text {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.league-action {
  text-align: center;
}

.btn-enter-league {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
  font-weight: 600;
  width: 100%;
}

/* === MENSAJE FLOTANTE === */
.floating-message {
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
  max-width: 300px;
}

.floating-message.success {
  border: 2px solid var(--success);
}

.floating-message.error {
  border: 2px solid var(--error);
}

@keyframes slideIn {
  to {
    transform: translateX(0);
  }
}

.message-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.message-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
}

/* === RESPONSIVE === */
@media (max-width: 1200px) {
  .dashboard-grid {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  }
  
  .next-event-card {
    grid-column: span 1;
  }
  
  .event-preview {
    flex-direction: column;
    text-align: center;
  }
  
  .event-image {
    width: 100%;
    height: 120px;
  }
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: var(--space-md);
  }
  
  .dashboard-card {
    padding: var(--space-lg);
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }
  
  .user-stats {
    justify-content: center;
    flex-wrap: wrap;
    gap: var(--space-md);
  }
  
  .title-hero {
    font-size: 2rem;
  }
  
  .floating-message {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-sm);
  }
  
  .league-quick-stats {
    grid-template-columns: repeat(3, 1fr);
    gap: var(--space-sm);
  }
  
  .quick-stat {
    padding: var(--space-sm);
  }
  
  .quick-number {
    font-size: 1.2rem;
  }
}

@media (max-width: 480px) {
  .dashboard-card {
    padding: var(--space-md);
  }
  
  .user-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .btn-profile,
  .btn-logout {
    width: 100%;
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .league-quick-stats {
    grid-template-columns: 1fr;
  }
  
  .event-preview {
    gap: var(--space-md);
  }
  
  .leagues-grid {
    grid-template-columns: 1fr;
  }
}
</style>