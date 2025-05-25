<template>
  <div class="home-logged">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header del usuario -->
    <div class="user-header">
      <div class="container">
        <div class="header-content">
          <div class="welcome-section">
            <h1 class="title-hero">¡BIENVENIDO, {{ userDisplayName }}!</h1>
            <p class="welcome-subtitle">Selecciona una liga o únete a una nueva</p>
          </div>
          <div class="user-actions">
            <button class="btn btn-profile" @click="goToProfile">
              <span class="profile-avatar">{{ userInitials }}</span>
              Mi Perfil
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        
        <!-- Próximo Evento UFC - Destacado -->
        <div class="next-event-section">
          <div class="event-card-featured" @click="goToUFCEvents">
            <div class="event-image">
              <img src="/images/ufc-event-vegas-107.jpg" alt="UFC Vegas 107" />
              <div class="event-overlay">
                <div class="event-badge">PRÓXIMO EVENTO</div>
              </div>
            </div>
            <div class="event-info">
              <div class="event-details">
                <h3 class="event-title">UFC VEGAS 107</h3>
                <h4 class="event-subtitle">BLANCHFIELD VS BARBER</h4>
                <div class="event-meta">
                  <span class="event-date">📅 31 de Mayo, 2025</span>
                  <span class="event-location">📍 Las Vegas, Nevada</span>
                </div>
              </div>
              <div class="event-action">
                <button class="btn btn-event">Ver Cartelera</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Grid principal: Mis Ligas y Unirse a Liga -->
        <div class="main-grid">
          
          <!-- Sección: Mis Ligas -->
          <div class="section-card my-leagues-section">
            <div class="section-header">
              <h2 class="section-title">⚔️ Mis Ligas</h2>
              <span class="leagues-count">{{ myLeagues.length }} liga{{ myLeagues.length !== 1 ? 's' : '' }}</span>
            </div>
            
            <div class="leagues-list">
              <!-- Liga individual -->
              <div 
                v-for="league in myLeagues" 
                :key="league.id"
                class="league-item"
                @click="enterLeague(league)"
              >
                <div class="league-main-info">
                  <div class="league-header">
                    <h3 class="league-name">{{ league.name }}</h3>
                    <div class="league-badges">
                      <span class="league-type" :class="league.type.toLowerCase()">
                        {{ league.type === 'PUBLIC' ? '🌍' : '🔒' }}
                      </span>
                      <span v-if="league.userPosition <= 3" class="position-badge" :class="getPositionClass(league.userPosition)">
                        #{{ league.userPosition }}
                      </span>
                    </div>
                  </div>
                  <p class="league-description">{{ league.description || 'Liga de fantasy de UFC' }}</p>
                </div>
                
                <div class="league-stats">
                  <div class="stat-item">
                    <span class="stat-label">Posición</span>
                    <span class="stat-value">#{{ league.userPosition }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">Puntos</span>
                    <span class="stat-value">{{ league.userPoints }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">Miembros</span>
                    <span class="stat-value">{{ league.memberCount }}</span>
                  </div>
                </div>
                
                <div class="league-action">
                  <button class="btn btn-enter">Entrar a Liga →</button>
                </div>
              </div>

              <!-- Estado vacío -->
              <div v-if="myLeagues.length === 0" class="empty-state">
                <div class="empty-icon">🏆</div>
                <h3 class="empty-title">No tienes ligas</h3>
                <p class="empty-description">Únete a una liga para empezar a competir</p>
              </div>
            </div>
          </div>

          <!-- Sección: Unirse a Liga -->
          <div class="section-card join-league-section">
            <div class="section-header">
              <h2 class="section-title">🏆 Unirse a Liga</h2>
            </div>
            
            <div class="join-options">
              
              <!-- Unirse a Liga Privada -->
              <div class="join-option private-join">
                <div class="option-header">
                  <div class="option-icon">🔐</div>
                  <div class="option-info">
                    <h3 class="option-title">Liga Privada</h3>
                    <p class="option-description">Usa un código de invitación</p>
                  </div>
                </div>
                
                <div class="private-form">
                  <div class="input-group">
                    <input
                      v-model="privateCode"
                      type="text"
                      class="private-input"
                      placeholder="Código de invitación (ej: ABC12DEF)"
                      maxlength="8"
                      @keyup.enter="joinPrivateLeague"
                    />
                    <button 
                      class="btn btn-join-private"
                      @click="joinPrivateLeague"
                      :disabled="!privateCode.trim()"
                    >
                      Unirse
                    </button>
                  </div>
                </div>
              </div>

              <!-- Separador -->
              <div class="separator">
                <span class="separator-text">O</span>
              </div>

              <!-- Ligas Públicas Disponibles -->
              <div class="join-option public-join">
                <div class="option-header">
                  <div class="option-icon">🌍</div>
                  <div class="option-info">
                    <h3 class="option-title">Ligas Públicas</h3>
                    <p class="option-description">Únete a una liga abierta</p>
                  </div>
                </div>
                
                <div class="public-leagues-list">
                  <!-- Liga pública individual -->
                  <div 
                    v-for="league in publicLeagues" 
                    :key="league.id"
                    class="public-league-item"
                    @click="joinPublicLeague(league)"
                  >
                    <div class="public-league-info">
                      <h4 class="public-league-name">{{ league.name }}</h4>
                      <p class="public-league-description">{{ league.description }}</p>
                      <div class="public-league-meta">
                        <span class="member-count">👥 {{ league.memberCount }} miembros</span>
                        <span class="event-name">🎯 {{ league.eventName }}</span>
                      </div>
                    </div>
                    <div class="public-league-action">
                      <button class="btn btn-join-public">Unirse</button>
                    </div>
                  </div>

                  <!-- Estado vacío para ligas públicas -->
                  <div v-if="publicLeagues.length === 0" class="empty-public">
                    <p class="empty-text">No hay ligas públicas disponibles</p>
                    <button class="btn btn-refresh" @click="loadPublicLeagues">
                      🔄 Actualizar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotification" class="notification" :class="notificationType" @click="hideNotification">
      <div class="notification-icon">
        <span v-if="notificationType === 'success'">✅</span>
        <span v-else>❌</span>
      </div>
      <div class="notification-text">{{ notificationText }}</div>
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
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Datos simulados de mis ligas
    const myLeagues = ref([
      {
        id: 1,
        name: 'Liga Oficina',
        description: 'Liga entre compañeros de trabajo',
        type: 'PRIVATE',
        memberCount: 12,
        userPosition: 1,
        userPoints: 1890
      },
      {
        id: 2,
        name: 'Amigos Luchadores',
        description: 'Liga con mis amigos de toda la vida',
        type: 'PRIVATE',
        memberCount: 8,
        userPosition: 3,
        userPoints: 1250
      },
      {
        id: 3,
        name: 'UFC Vegas 107 - Global',
        description: 'Liga pública para el evento UFC Vegas 107',
        type: 'PUBLIC',
        memberCount: 156,
        userPosition: 5,
        userPoints: 980
      }
    ])

    // Datos simulados de ligas públicas disponibles
    const publicLeagues = ref([
      {
        id: 4,
        name: 'UFC Vegas 107 - Élite',
        description: 'Liga para los mejores fighters',
        memberCount: 89,
        eventName: 'UFC Vegas 107'
      },
      {
        id: 5,
        name: 'Novatos UFC',
        description: 'Liga perfecta para principiantes',
        memberCount: 34,
        eventName: 'UFC Vegas 107'
      },
      {
        id: 6,
        name: 'España Fantasy UFC',
        description: 'Liga en español para fans de UFC',
        memberCount: 67,
        eventName: 'UFC Vegas 107'
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
    const getPositionClass = (position) => {
      if (position === 1) return 'gold'
      if (position === 2) return 'silver'
      if (position === 3) return 'bronze'
      return ''
    }

    const enterLeague = (league) => {
      router.push(`/league/${league.id}`)
    }

    const joinPrivateLeague = () => {
      if (!privateCode.value.trim()) return
      
      console.log('Uniéndose a liga privada con código:', privateCode.value)
      showFloatingNotification('success', `¡Te has unido a la liga con código: ${privateCode.value}!`)
      privateCode.value = ''
      
      // Simular que se añade una nueva liga
      setTimeout(() => {
        myLeagues.value.push({
          id: Date.now(),
          name: 'Nueva Liga Privada',
          description: 'Liga recién agregada',
          type: 'PRIVATE',
          memberCount: 5,
          userPosition: 1,
          userPoints: 0
        })
      }, 1000)
    }

    const joinPublicLeague = (league) => {
      console.log('Uniéndose a liga pública:', league.name)
      showFloatingNotification('success', `¡Te has unido a ${league.name}!`)
      
      // Simular que se añade la liga a mis ligas
      setTimeout(() => {
        myLeagues.value.push({
          id: league.id,
          name: league.name,
          description: league.description,
          type: 'PUBLIC',
          memberCount: league.memberCount + 1,
          userPosition: league.memberCount + 1,
          userPoints: 0
        })
        
        // Remover de ligas públicas disponibles
        const index = publicLeagues.value.findIndex(l => l.id === league.id)
        if (index > -1) {
          publicLeagues.value.splice(index, 1)
        }
      }, 1000)
    }

    const loadPublicLeagues = () => {
      console.log('Actualizando ligas públicas...')
      showFloatingNotification('success', 'Ligas públicas actualizadas')
    }

    const goToUFCEvents = () => {
      window.open('https://www.ufc.com/events', '_blank')
    }

    const goToProfile = () => {
      router.push('/profile')
    }

    const handleLogout = () => {
      console.log('Cerrando sesión...')
      router.push('/')
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

    return {
      currentUser,
      userDisplayName,
      userInitials,
      privateCode,
      myLeagues,
      publicLeagues,
      showNotification,
      notificationType,
      notificationText,
      getPositionClass,
      enterLeague,
      joinPrivateLeague,
      joinPublicLeague,
      loadPublicLeagues,
      goToUFCEvents,
      goToProfile,
      handleLogout,
      hideNotification
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

/* === HEADER === */
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

.welcome-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
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
  text-decoration: none;
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

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

/* === PRÓXIMO EVENTO === */
.next-event-section {
  margin-bottom: var(--space-2xl);
}

.event-card-featured {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-xl);
  padding: var(--space-xl);
}

.event-card-featured:hover {
  transform: translateY(-5px);
  border-color: var(--primary);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.event-image {
  position: relative;
  width: 200px;
  height: 120px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  flex-shrink: 0;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.event-card-featured:hover .event-image img {
  transform: scale(1.05);
}

.event-overlay {
  position: absolute;
  top: var(--space-sm);
  right: var(--space-sm);
}

.event-badge {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-weight: 700;
  font-size: 0.8rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.event-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-title {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
}

.event-subtitle {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--primary);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
}

.event-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.event-date,
.event-location {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.btn-event {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-md);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.btn-event:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* === GRID PRINCIPAL === */
.main-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-2xl);
}

/* === SECCIONES === */
.section-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);
  padding-bottom: var(--space-md);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  font-weight: 400;
  color: var(--white);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.leagues-count {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: bold;
}

/* === MIS LIGAS === */
.leagues-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.league-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.league-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.league-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-sm);
}

.league-name {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
  text-transform: uppercase;
}

.league-badges {
  display: flex;
  gap: var(--space-sm);
  align-items: center;
}

.league-type {
  font-size: 1.2rem;
}

.position-badge {
  background: var(--gray);
  color: var(--white);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: bold;
}

.position-badge.gold {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: var(--dark);
}

.position-badge.silver {
  background: linear-gradient(135deg, #c0c0c0 0%, #e5e5e5 100%);
  color: var(--dark);
}

.position-badge.bronze {
  background: linear-gradient(135deg, #cd7f32 0%, #e6a85c 100%);
  color: var(--white);
}

.league-description {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin-bottom: var(--space-md);
}

.league-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
  margin-bottom: var(--space-md);
}

.stat-item {
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  padding: var(--space-sm);
  border-radius: var(--radius-md);
}

.stat-label {
  display: block;
  color: var(--gray-light);
  font-size: 0.8rem;
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.stat-value {
  display: block;
  color: var(--primary);
  font-weight: bold;
  font-size: 1.1rem;
}

.league-action {
  text-align: center;
}

.btn-enter {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
  font-weight: 600;
  width: 100%;
}

/* === ESTADO VACÍO === */
.empty-state {
  text-align: center;
  padding: var(--space-2xl) var(--space-lg);
}

.empty-icon {
  font-size: 3rem;
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
}

/* === UNIRSE A LIGA === */
.join-options {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

.join-option {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
}

.option-header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.option-icon {
  font-size: 1.8rem;
  width: 50px;
  height: 50px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.option-title {
  font-family: var(--font-impact);
  font-size: 1.1rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
}

.option-description {
  color: var(--gray-light);
  font-size: 0.9rem;
}

/* === UNIRSE A LIGA PRIVADA === */
.input-group {
  display: flex;
  gap: var(--space-md);
}

.private-input {
  flex: 1;
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1rem;
}

.private-input::placeholder {
  color: var(--gray-light);
}

.private-input:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
}

.btn-join-private {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-md);
  font-weight: 600;
  white-space: nowrap;
}

.btn-join-private:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* === SEPARADOR === */
.separator {
  position: relative;
  text-align: center;
  margin: var(--space-lg) 0;
}

.separator::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(255, 255, 255, 0.2);
}

.separator-text {
  background: var(--dark-light);
  color: var(--gray-light);
  padding: 0 var(--space-md);
  font-weight: 600;
  font-size: 0.9rem;
  position: relative;
  z-index: 1;
}

/* === LIGAS PÚBLICAS === */
.public-leagues-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  max-height: 400px;
  overflow-y: auto;
}

.public-league-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.public-league-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateX(0px);
}

.public-league-info {
  flex: 1;
}

.public-league-name {
  font-family: var(--font-impact);
  font-size: 1rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
}

.public-league-description {
  color: var(--gray-light);
  font-size: 0.8rem;
  margin-bottom: var(--space-sm);
}

.public-league-meta {
  display: flex;
  gap: var(--space-md);
  font-size: 0.8rem;
}

.member-count,
.event-name {
  color: var(--gray-light);
}

.btn-join-public {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: 0.9rem;
  white-space: nowrap;
}

.btn-join-public:hover {
  background: var(--primary);
  color: var(--white);
}

/* === ESTADO VACÍO LIGAS PÚBLICAS === */
.empty-public {
  text-align: center;
  padding: var(--space-xl);
  color: var(--gray-light);
}

.empty-text {
  margin-bottom: var(--space-md);
  font-size: 0.9rem;
}

.btn-refresh {
  background: transparent;
  color: var(--primary);
  border: 1px solid var(--primary);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
}

.btn-refresh:hover {
  background: var(--primary);
  color: var(--white);
}

/* === NOTIFICACIÓN FLOTANTE === */
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

@keyframes slideIn {
  to {
    transform: translateX(0);
  }
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
  .main-grid {
    grid-template-columns: 1fr;
    gap: var(--space-xl);
  }
  
  .event-card-featured {
    flex-direction: column;
    text-align: center;
  }
  
  .event-image {
    width: 100%;
    height: 150px;
  }
  
  .event-info {
    flex-direction: column;
    gap: var(--space-lg);
    width: 100%;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
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
  
  .section-card {
    padding: var(--space-lg);
  }
  
  .event-card-featured {
    padding: var(--space-lg);
  }
  
  .league-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .input-group {
    flex-direction: column;
  }
  
  .btn-join-private {
    width: 100%;
  }
  
  .public-league-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-md);
  }
  
  .btn-join-public {
    width: 100%;
  }
  
  .notification {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 var(--space-md);
  }
  
  .main-content {
    padding: var(--space-lg) 0;
  }
  
  .title-hero {
    font-size: 1.8rem;
  }
  
  .section-card {
    padding: var(--space-md);
  }
  
  .event-card-featured {
    padding: var(--space-md);
  }
  
  .league-item {
    padding: var(--space-md);
  }
  
  .league-stats {
    grid-template-columns: 1fr;
  }
  
  .join-option {
    padding: var(--space-md);
  }
  
  .option-header {
    flex-direction: column;
    text-align: center;
  }
  
  .public-leagues-list {
    max-height: 300px;
  }
}
</style>