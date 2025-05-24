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
            <p class="welcome-subtitle">
              Listo para dominar las ligas de fantasy MMA
            </p>
          </div>
          <div class="user-actions">
            <button class="btn btn-profile" @click="goToProfile">
              <span class="profile-avatar">{{ userInitials }}</span>
              Mi Perfil
            </button>
            <button class="btn btn-logout" @click="handleLogout">
              🚪 Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        <div class="content-grid">
          
          <!-- Sección: Unirse a Ligas -->
          <div class="section-card">
            <div class="section-header">
              <h2 class="section-title">🏆 Unirse a Ligas</h2>
              <p class="section-subtitle">
                Encuentra ligas públicas o únete con un código de invitación
              </p>
            </div>

            <!-- Ligas Públicas -->
            <div class="public-leagues">
              <h3 class="subsection-title">Ligas Públicas Disponibles</h3>
              
              <!-- Loading state -->
              <div v-if="isLoadingPublicLeagues" class="loading-state">
                <div class="spinner"></div>
                <p>Cargando ligas disponibles...</p>
              </div>

              <!-- Lista de ligas públicas -->
              <div v-else-if="publicLeagues.length > 0" class="leagues-grid">
                <div 
                  v-for="league in publicLeagues" 
                  :key="league.id"
                  class="league-card"
                  @click="joinPublicLeague(league)"
                >
                  <div class="league-info">
                    <h4 class="league-name">{{ league.name }}</h4>
                    <p class="league-description">{{ league.description }}</p>
                    <div class="league-meta">
                      <span class="league-members">👥 {{ league.memberCount || 0 }} miembros</span>
                      <span v-if="league.event" class="league-event">🎯 {{ league.event.name }}</span>
                    </div>
                  </div>
                  <div class="league-action">
                    <button class="btn btn-join">Unirse</button>
                  </div>
                </div>
              </div>

              <!-- Sin ligas públicas -->
              <div v-else class="empty-state">
                <div class="empty-icon">🏟️</div>
                <p>No hay ligas públicas disponibles en este momento</p>
              </div>
            </div>

            <!-- Liga Privada -->
            <div class="private-league">
              <h3 class="subsection-title">Unirse a Liga Privada</h3>
              <div class="private-form">
                <div class="form-group">
                  <input
                    v-model="privateLeagueCode"
                    type="text"
                    class="form-input"
                    :class="{ 'error': privateLeagueError }"
                    placeholder="Ingresa el código de invitación"
                    maxlength="8"
                    @input="clearPrivateLeagueError"
                    @keyup.enter="joinPrivateLeague"
                  />
                  <button 
                    class="btn btn-primary"
                    @click="joinPrivateLeague"
                    :disabled="!privateLeagueCode.trim() || isJoiningPrivate"
                  >
                    <span v-if="isJoiningPrivate">Uniéndose...</span>
                    <span v-else>🔐 Unirse</span>
                  </button>
                </div>
                <span v-if="privateLeagueError" class="error-message">{{ privateLeagueError }}</span>
                <p class="form-help">
                  El código de invitación te lo proporciona el creador de la liga
                </p>
              </div>
            </div>
          </div>

          <!-- Sección: Mis Ligas -->
          <div class="section-card">
            <div class="section-header">
              <h2 class="section-title">⚔️ Mis Ligas</h2>
              <p class="section-subtitle">
                Ligas en las que estás participando
              </p>
            </div>

            <!-- Loading state -->
            <div v-if="isLoadingMyLeagues" class="loading-state">
              <div class="spinner"></div>
              <p>Cargando tus ligas...</p>
            </div>

            <!-- Lista de mis ligas -->
            <div v-else-if="myLeagues.length > 0" class="my-leagues-grid">
              <div 
                v-for="league in myLeagues" 
                :key="league.id"
                class="my-league-card"
                @click="goToLeague(league)"
              >
                <div class="league-header">
                  <h4 class="league-name">{{ league.name }}</h4>
                  <span class="league-type" :class="league.type.toLowerCase()">
                    {{ league.type === 'PUBLIC' ? '🌍 Pública' : '🔒 Privada' }}
                  </span>
                </div>
                <p class="league-description">{{ league.description }}</p>
                <div class="league-stats">
                  <div class="stat">
                    <span class="stat-label">Miembros:</span>
                    <span class="stat-value">{{ league.memberCount || 0 }}</span>
                  </div>
                  <div class="stat">
                    <span class="stat-label">Tu posición:</span>
                    <span class="stat-value">#{{ league.userPosition || '-' }}</span>
                  </div>
                  <div class="stat">
                    <span class="stat-label">Puntos:</span>
                    <span class="stat-value">{{ league.userPoints || 0 }}</span>
                  </div>
                </div>
                <div class="league-footer">
                  <button class="btn btn-enter">📊 Ver Liga</button>
                  <span v-if="league.invitationCode" class="invitation-code">
                    Código: {{ league.invitationCode }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Sin ligas -->
            <div v-else class="empty-state">
              <div class="empty-icon">🥊</div>
              <h3>¡Aún no estás en ninguna liga!</h3>
              <p>Únete a una liga pública o crea una privada para empezar a competir</p>
              <button class="btn btn-primary" @click="scrollToJoinSection">
                🏆 Buscar Ligas
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- Mensaje de éxito/error -->
    <div v-if="showMessage" class="message-overlay" @click="hideMessage">
      <div class="message-content" :class="messageType">
        <div class="message-icon">
          <span v-if="messageType === 'success'">✅</span>
          <span v-else>❌</span>
        </div>
        <div class="message-text">
          <h4>{{ messageTitle }}</h4>
          <p>{{ messageText }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
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

    // Estados de carga
    const isLoadingPublicLeagues = ref(false)
    const isLoadingMyLeagues = ref(false)
    const isJoiningPrivate = ref(false)

    // Datos de ligas
    const publicLeagues = ref([])
    const myLeagues = ref([])

    // Liga privada
    const privateLeagueCode = ref('')
    const privateLeagueError = ref('')

    // Mensajes
    const showMessage = ref(false)
    const messageType = ref('success')
    const messageTitle = ref('')
    const messageText = ref('')

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

    // Cargar datos al montar el componente
    onMounted(() => {
      loadPublicLeagues()
      loadMyLeagues()
    })

    // Cargar ligas públicas
    const loadPublicLeagues = async () => {
      isLoadingPublicLeagues.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        publicLeagues.value = [
          {
            id: 1,
            name: 'Liga UFC Vegas 107',
            description: 'Liga oficial para el evento UFC Vegas 107',
            memberCount: 156,
            type: 'PUBLIC',
            event: { name: 'UFC Vegas 107' }
          },
          {
            id: 2,
            name: 'Guerreros del Octágono',
            description: 'Para los verdaderos fanáticos del MMA',
            memberCount: 89,
            type: 'PUBLIC',
            event: { name: 'UFC Vegas 107' }
          }
        ]
      } catch (error) {
        console.error('Error cargando ligas públicas:', error)
      } finally {
        isLoadingPublicLeagues.value = false
      }
    }

    // Cargar mis ligas
    const loadMyLeagues = async () => {
      isLoadingMyLeagues.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 800))
        
        myLeagues.value = [
          {
            id: 3,
            name: 'Amigos Luchadores',
            description: 'Liga privada con los amigos',
            type: 'PRIVATE',
            memberCount: 8,
            userPosition: 3,
            userPoints: 1250,
            invitationCode: 'ABC123XY'
          },
          {
            id: 4,
            name: 'Liga Oficina',
            description: 'Competencia entre compañeros de trabajo',
            type: 'PRIVATE',
            memberCount: 12,
            userPosition: 1,
            userPoints: 1890,
            invitationCode: 'WORK2025'
          }
        ]
      } catch (error) {
        console.error('Error cargando mis ligas:', error)
      } finally {
        isLoadingMyLeagues.value = false
      }
    }

    // Unirse a liga pública
    const joinPublicLeague = async (league) => {
      try {
        console.log('Uniéndose a liga pública:', league.name)
        
        showSuccessMessage(
          '¡Liga joined!',
          `Te has unido exitosamente a "${league.name}"`
        )
        
        loadMyLeagues()
      } catch (error) {
        showErrorMessage(
          'Error al unirse',
          'No se pudo unir a la liga. Inténtalo de nuevo.'
        )
      }
    }

    // Unirse a liga privada
    const joinPrivateLeague = async () => {
      if (!privateLeagueCode.value.trim()) {
        privateLeagueError.value = 'Ingresa un código de invitación'
        return
      }

      isJoiningPrivate.value = true
      privateLeagueError.value = ''

      try {
        console.log('Uniéndose a liga privada con código:', privateLeagueCode.value)
        
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        showSuccessMessage(
          '¡Liga joined!',
          'Te has unido exitosamente a la liga privada'
        )
        
        privateLeagueCode.value = ''
        loadMyLeagues()
      } catch (error) {
        privateLeagueError.value = 'Código inválido o liga no encontrada'
      } finally {
        isJoiningPrivate.value = false
      }
    }

    // Limpiar error de liga privada
    const clearPrivateLeagueError = () => {
      privateLeagueError.value = ''
    }

    // Ir a una liga específica
    const goToLeague = (league) => {
      console.log('Navegando a liga:', league.name)
      alert(`Navegando a la liga: ${league.name}`)
    }

    // Ir al perfil
    const goToProfile = () => {
      router.push('/profile')
    }

    // Cerrar sesión
    const handleLogout = async () => {
      try {
        console.log('Cerrando sesión...')
        router.push('/')
      } catch (error) {
        console.error('Error al cerrar sesión:', error)
      }
    }

    // Scroll a la sección de unirse
    const scrollToJoinSection = () => {
      const element = document.querySelector('.section-card')
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' })
      }
    }

    // Mostrar mensajes
    const showSuccessMessage = (title, text) => {
      messageType.value = 'success'
      messageTitle.value = title
      messageText.value = text
      showMessage.value = true
      
      setTimeout(() => {
        hideMessage()
      }, 4000)
    }

    const showErrorMessage = (title, text) => {
      messageType.value = 'error'
      messageTitle.value = title
      messageText.value = text
      showMessage.value = true
      
      setTimeout(() => {
        hideMessage()
      }, 5000)
    }

    const hideMessage = () => {
      showMessage.value = false
    }

    return {
      currentUser,
      userDisplayName,
      userInitials,
      isLoadingPublicLeagues,
      isLoadingMyLeagues,
      isJoiningPrivate,
      publicLeagues,
      myLeagues,
      privateLeagueCode,
      privateLeagueError,
      showMessage,
      messageType,
      messageTitle,
      messageText,
      joinPublicLeague,
      joinPrivateLeague,
      clearPrivateLeagueError,
      goToLeague,
      goToProfile,
      handleLogout,
      scrollToJoinSection,
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
  filter: brightness(0.3) contrast(0.8) grayscale(0.2);
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

/* === HEADER DEL USUARIO === */
.user-header {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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

.welcome-section {
  flex: 1;
}

.title-hero {
  font-family: var(--font-impact);
  font-size: clamp(2rem, 4vw, 3.5rem);
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.empty-state p {
  margin-bottom: var(--space-lg);
  line-height: 1.5;
}

/* === MENSAJES === */
.message-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.message-content {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  max-width: 500px;
  margin: var(--space-lg);
}

.message-content.success {
  border: 2px solid var(--success);
}

.message-content.error {
  border: 2px solid var(--error);
}

.message-icon {
  font-size: 3rem;
  flex-shrink: 0;
}

.message-text h4 {
  color: var(--white);
  margin: 0 0 var(--space-sm) 0;
  font-family: var(--font-impact);
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.message-text p {
  color: var(--gray-light);
  margin: 0;
  line-height: 1.5;
}

/* === RESPONSIVE === */
@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
    gap: var(--space-xl);
  }

  .header-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-lg);
  }

  .user-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .section-card {
    padding: var(--space-xl);
  }

  .section-title {
    font-size: 1.6rem;
  }

  .title-hero {
    font-size: 2.5rem;
  }

  .private-form .form-group {
    flex-direction: column;
  }

  .league-stats {
    grid-template-columns: 1fr;
    gap: var(--space-sm);
  }

  .league-footer {
    flex-direction: column;
    gap: var(--space-md);
    align-items: stretch;
  }

  .btn-enter {
    width: 100%;
  }

  .message-content {
    flex-direction: column;
    text-align: center;
    margin: var(--space-md);
  }
}

@media (max-width: 480px) {
  .section-card {
    padding: var(--space-lg);
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
}
</style>: uppercase;
  letter-spacing: 0.02em;
}

.welcome-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
}

.user-actions {
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-height: 48px;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-profile {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: rgba(255, 255, 255, 0.1);
  color: var(--white);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
}

.profile-avatar {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.8rem;
}

.btn-logout {
  background: transparent;
  color: var(--error);
  border: 1px solid var(--error);
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
}

.btn-logout:hover {
  background: var(--error);
  color: var(--white);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-2xl) 0;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-2xl);
  margin-bottom: var(--space-2xl);
}

/* === TARJETAS DE SECCIÓN === */
.section-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.section-header {
  margin-bottom: var(--space-xl);
}

.section-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.section-subtitle {
  color: var(--gray-light);
  font-size: 1rem;
  line-height: 1.5;
}

.subsection-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

/* === ESTADOS DE CARGA === */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-2xl);
  color: var(--gray-light);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 107, 53, 0.3);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* === LIGAS === */
.public-leagues {
  margin-bottom: var(--space-2xl);
}

.leagues-grid {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.league-card {
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

.league-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.league-info {
  flex: 1;
}

.league-name {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.league-description {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin-bottom: var(--space-sm);
}

.league-meta {
  display: flex;
  gap: var(--space-md);
  font-size: 0.8rem;
  color: var(--gray-light);
}

.btn-join {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
}

/* === LIGA PRIVADA === */
.private-league {
  padding-top: var(--space-xl);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.private-form .form-group {
  display: flex;
  gap: var(--space-md);
  margin-bottom: var(--space-sm);
}

.form-input {
  flex: 1;
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-input::placeholder {
  color: var(--gray-light);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
}

.form-input.error {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.error-message {
  color: var(--error);
  font-size: 0.8rem;
  font-weight: 500;
}

.form-help {
  color: var(--gray-light);
  font-size: 0.8rem;
  font-style: italic;
}

/* === MIS LIGAS === */
.my-leagues-grid {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.my-league-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.my-league-card:hover {
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

.league-type {
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: bold;
  text-transform: uppercase;
}

.league-type.public {
  background: rgba(16, 185, 129, 0.2);
  color: var(--success);
}

.league-type.private {
  background: rgba(59, 130, 246, 0.2);
  color: var(--info);
}

.league-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
  margin: var(--space-md) 0;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  color: var(--gray-light);
  font-size: 0.7rem;
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.stat-value {
  color: var(--white);
  font-weight: bold;
  font-size: 1.1rem;
}

.league-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-md);
  padding-top: var(--space-md);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-enter {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
  font-weight: 600;
}

.invitation-code {
  color: var(--gray-light);
  font-size: 0.8rem;
  font-family: monospace;
  background: rgba(255, 255, 255, 0.05);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-sm);
}

/* === ESTADO VACÍO === */
.empty-state {
  text-align: center;
  padding: var(--space-2xl);
  color: var(--gray-light);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--space-lg);
  opacity: 0.5;
}

.empty-state h3 {
  color: var(--white);
  margin-bottom: var(--space-md);
  font-family: var(--font-impact);
  font-weight: 400;
  text-transform
}