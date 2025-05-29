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
            <button class="btn btn-create-league" @click="openCreateLeagueModal">
              ➕ Crear Liga
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        
        <!-- Próximo Evento UFC - Destacado -->
        <div class="next-event-section" v-if="nextEvent">
          <div class="event-card-featured" @click="goToUFCEvents">
            <div class="event-image">
              <img 
                :src="nextEvent.imageUrl || '/images/ufc-event-vegas-107.jpg'" 
                :alt="nextEvent.name"
                @error="handleImageError"
              />
              <div class="event-overlay">
                <div class="event-badge">{{ eventBadge }}</div>
              </div>
            </div>
            <div class="event-info">
              <div class="event-details">
                <h3 class="event-title">{{ eventTitle }}</h3>
                <h4 class="event-subtitle">{{ nextEvent.description || 'Próximo evento UFC' }}</h4>
                <div class="event-meta">
                  <span class="event-date">📅 {{ formattedDate }}</span>
                  <span class="event-location">📍 {{ nextEvent.location || 'Por confirmar' }}</span>
                </div>
              </div>
              <div class="event-action">
                <button class="btn btn-event">Ver Cartelera</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Loading state del evento -->
        <div v-if="isLoadingEvent" class="loading-event">
          <div class="loading-spinner">
            <span class="spinner"></span>
            <p>Cargando próximo evento...</p>
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
            
            <!-- Loading state para mis ligas -->
            <div v-if="isLoadingMyLeagues" class="loading-section">
              <div class="loading-spinner">
                <span class="spinner"></span>
                <p>Cargando tus ligas...</p>
              </div>
            </div>
            
            <div v-else class="leagues-list">
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
                      <span v-if="league.userPosition && league.userPosition <= 3" class="position-badge" :class="getPositionClass(league.userPosition)">
                        #{{ league.userPosition }}
                      </span>
                    </div>
                  </div>
                  <p class="league-description">{{ league.description || 'Liga de fantasy de UFC' }}</p>
                </div>
                
                <div class="league-stats">
                  <div class="stat-item">
                    <span class="stat-label">Posición</span>
                    <span class="stat-value">#{{ league.userPosition || '-' }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">Puntos</span>
                    <span class="stat-value">{{ league.userPoints || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">Miembros</span>
                    <span class="stat-value">{{ league.memberCount || league.members?.length || 0 }}</span>
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
                      :disabled="isJoiningPrivate"
                    />
                    <button 
                      class="btn btn-join-private"
                      @click="joinPrivateLeague"
                      :disabled="!privateCode.trim() || isJoiningPrivate"
                    >
                      <span v-if="isJoiningPrivate">Uniéndose...</span>
                      <span v-else>Unirse</span>
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
                
                <!-- Loading state para ligas públicas -->
                <div v-if="isLoadingPublicLeagues" class="loading-section">
                  <div class="loading-spinner">
                    <span class="spinner"></span>
                    <p>Cargando ligas públicas...</p>
                  </div>
                </div>
                
                <div v-else class="public-leagues-list">
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
                        <span class="member-count">👥 {{ league.memberCount || league.members?.length || 0 }} miembros</span>
                        <span v-if="league.event" class="event-name">🎯 {{ league.event.name }}</span>
                      </div>
                    </div>
                    <div class="public-league-action">
                      <button 
                        class="btn btn-join-public"
                        :disabled="isJoiningPublic === league.id"
                      >
                        <span v-if="isJoiningPublic === league.id">Uniéndose...</span>
                        <span v-else>Unirse</span>
                      </button>
                    </div>
                  </div>

                  <!-- Estado vacío para ligas públicas -->
                  <div v-if="publicLeagues.length === 0" class="empty-public">
                    <p class="empty-text">No hay ligas públicas disponibles</p>
                    <button class="btn btn-refresh" @click="loadPublicLeagues" :disabled="isLoadingPublicLeagues">
                      🔄 {{ isLoadingPublicLeagues ? 'Actualizando...' : 'Actualizar' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de crear liga privada -->
    <div v-if="showCreateLeagueModal" class="modal-overlay" @click="closeCreateLeagueModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">Crear Liga Privada</h3>
          <button class="modal-close" @click="closeCreateLeagueModal">✕</button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="createPrivateLeague">
            <div class="form-group">
              <label for="league-name" class="form-label">Nombre de la Liga *</label>
              <input
                id="league-name"
                v-model="createLeagueForm.name"
                type="text"
                class="form-input"
                placeholder="Ej: Liga de Amigos"
                maxlength="50"
                required
              />
            </div>

            <div class="form-group">
              <label for="league-description" class="form-label">Descripción</label>
              <textarea
                id="league-description"
                v-model="createLeagueForm.description"
                class="form-textarea"
                placeholder="Describe tu liga (opcional)"
                maxlength="200"
                rows="3"
              ></textarea>
            </div>

            <div class="form-group">
              <label for="initial-budget" class="form-label">Presupuesto Inicial</label>
              <select id="initial-budget" v-model="createLeagueForm.initialBudget" class="form-select">
                <option value="50000">50,000 (Principiante)</option>
                <option value="100000">100,000 (Estándar)</option>
                <option value="150000">150,000 (Avanzado)</option>
              </select>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="max-fighters-event" class="form-label">Máx. Luchadores por Evento</label>
                <select id="max-fighters-event" v-model="createLeagueForm.maxFightersEvent" class="form-select">
                  <option value="1">1 Luchador</option>
                  <option value="2">2 Luchadores</option>
                  <option value="3">3 Luchadores</option>
                </select>
              </div>

              <div class="form-group">
                <label for="min-fighters-event" class="form-label">Mín. Luchadores por Evento</label>
                <select id="min-fighters-event" v-model="createLeagueForm.minFightersEvent" class="form-select">
                  <option value="1">1 Luchador</option>
                  <option value="2">2 Luchadores</option>
                </select>
              </div>
            </div>

            <div class="modal-actions">
              <button 
                type="button" 
                class="btn btn-secondary" 
                @click="closeCreateLeagueModal"
                :disabled="isCreatingLeague"
              >
                Cancelar
              </button>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="!createLeagueForm.name.trim() || isCreatingLeague"
              >
                <span v-if="isCreatingLeague">Creando...</span>
                <span v-else>Crear Liga</span>
              </button>
            </div>
          </form>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth.js'
import { eventsService, leaguesService, leaderboardService } from '../api/index.js'
import { useDateFormatter } from '../composables/useDateFormatter.js'

export default {
  name: 'HomeLoggedView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const { formatEventDate } = useDateFormatter()

    // ✅ Obtener user del store correctamente
    const user = computed(() => authStore.user)

    // Estados
    const privateCode = ref('')
    
    // Estados para crear liga privada
    const showCreateLeagueModal = ref(false)
    const isCreatingLeague = ref(false)
    const createLeagueForm = ref({
      name: '',
      description: '',
      initialBudget: 100000,
      maxFighters: 10,
      maxFightersEvent: 3,
      minFightersEvent: 1
    })
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Estados de carga
    const isLoadingEvent = ref(false)
    const isLoadingMyLeagues = ref(false)
    const isLoadingPublicLeagues = ref(false)
    const isJoiningPrivate = ref(false)
    const isJoiningPublic = ref(null) // ID de la liga que se está uniendo

    // Datos
    const nextEvent = ref(null)
    const myLeagues = ref([])
    const publicLeagues = ref([])

    // Computed properties
    const userDisplayName = computed(() => {
      if (!user.value) return 'Usuario'
      
      console.log('🔍 Datos del usuario para displayName:', user.value)
      
      // Primero intentar con firstName y lastName
      if (user.value.firstName && user.value.lastName) {
        return `${user.value.firstName} ${user.value.lastName}`
      }
      
      // Si no hay firstName/lastName, usar username
      if (user.value.username) {
        return user.value.username
      }
      
      // Fallback
      return 'Usuario'
    })

    const userInitials = computed(() => {
      if (!user.value) return 'U'
      
      console.log('🔍 Datos del usuario para initials:', user.value)
      
      // Primero intentar con firstName y lastName
      if (user.value.firstName && user.value.lastName) {
        return `${user.value.firstName[0]}${user.value.lastName[0]}`.toUpperCase()
      }
      
      // Si no hay firstName/lastName, usar username
      if (user.value.username && user.value.username.length >= 2) {
        return user.value.username.substring(0, 2).toUpperCase()
      }
      
      // Fallback
      return 'U'
    })

    const eventBadge = computed(() => {
      if (!nextEvent.value?.name) return 'UFC'
      const parts = nextEvent.value.name.split(':')
      return parts[0].trim()
    })

    const eventTitle = computed(() => {
      if (!nextEvent.value?.name) return ''
      const parts = nextEvent.value.name.split(':')
      return parts.length > 1 ? parts[1].trim() : parts[0].trim()
    })

    const formattedDate = computed(() => {
      if (!nextEvent.value?.startDate) return ''
      return formatEventDate(nextEvent.value.startDate)
    })

    // Funciones para cargar datos
    const loadNextEvent = async () => {
      isLoadingEvent.value = true
      try {
        const event = await eventsService.getNextEvent()
        nextEvent.value = event
      } catch (error) {
        console.error('Error al cargar el próximo evento:', error)
        // No mostrar error para el evento ya que es opcional
      } finally {
        isLoadingEvent.value = false
      }
    }

    const loadMyLeagues = async () => {
      isLoadingMyLeagues.value = true
      try {
        const leagues = await leaguesService.getMyLeagues()
        
        // Enriquecer cada liga con datos de posición y puntos
        const enrichedLeagues = await Promise.all(
          leagues.map(async (league) => {
            try {
              // Obtener mi posición en esta liga
              const position = await leaderboardService.getMyPosition(league.id)
              
              return {
                ...league,
                userPosition: position.position,
                userPoints: position.totalPoints,
                memberCount: league.members?.length || 0
              }
            } catch (error) {
              console.error(`Error al obtener posición en liga ${league.id}:`, error)
              return {
                ...league,
                userPosition: null,
                userPoints: 0,
                memberCount: league.members?.length || 0
              }
            }
          })
        )
        
        myLeagues.value = enrichedLeagues
      } catch (error) {
        console.error('Error al cargar mis ligas:', error)
        showFloatingNotification('error', 'Error al cargar tus ligas')
      } finally {
        isLoadingMyLeagues.value = false
      }
    }

    const loadPublicLeagues = async () => {
      isLoadingPublicLeagues.value = true
      try {
        const leagues = await leaguesService.getPublicLeagues()
        
        // Filtrar ligas donde ya soy miembro
        const myLeagueIds = myLeagues.value.map(league => league.id)
        const filteredLeagues = leagues.filter(league => !myLeagueIds.includes(league.id))
        
        publicLeagues.value = filteredLeagues
        console.log('🔍 Ligas públicas cargadas (filtradas):', filteredLeagues.length)
      } catch (error) {
        console.error('Error al cargar ligas públicas:', error)
        showFloatingNotification('error', 'Error al cargar ligas públicas')
      } finally {
        isLoadingPublicLeagues.value = false
      }
    }

    // Funciones de acción
    const joinPrivateLeague = async () => {
      if (!privateCode.value.trim()) return
      
      // Verificar si ya estoy en una liga con este código (aunque no deberíamos saberlo)
      isJoiningPrivate.value = true
      try {
        const response = await leaguesService.joinPrivateLeague(privateCode.value)
        showFloatingNotification('success', `¡Te has unido a la liga!`)
        privateCode.value = ''
        
        // Recargar mis ligas después de unirse
        await loadMyLeagues()
        // También recargar ligas públicas por si alguna cambió de estado
        await loadPublicLeagues()
      } catch (error) {
        console.error('Error al unirse a liga privada:', error)
        
        // Manejo específico de errores
        if (error.message.includes('miembro') || error.message.includes('member')) {
          showFloatingNotification('error', 'Ya eres miembro de esta liga')
          // Limpiar el código
          privateCode.value = ''
          // Refrescar las listas para sincronizar
          await loadMyLeagues()
        } else if (error.message.includes('código') || error.message.includes('inválido') || error.message.includes('invalid')) {
          showFloatingNotification('error', 'Código de invitación inválido')
        } else {
          showFloatingNotification('error', error.message || 'Error al unirse a la liga privada')
        }
      } finally {
        isJoiningPrivate.value = false
      }
    }

    const joinPublicLeague = async (league) => {
      // Verificar si ya estoy en esta liga (doble check)
      const alreadyMember = myLeagues.value.some(myLeague => myLeague.id === league.id)
      if (alreadyMember) {
        showFloatingNotification('error', 'Ya eres miembro de esta liga')
        return
      }
      
      isJoiningPublic.value = league.id
      try {
        const response = await leaguesService.joinPublicLeague(league.id)
        showFloatingNotification('success', `¡Te has unido a ${league.name}!`)
        
        // Recargar ambas listas
        await Promise.all([
          loadMyLeagues(),
          loadPublicLeagues() // Esto filtrará automáticamente la liga donde me acabo de unir
        ])
      } catch (error) {
        console.error('Error al unirse a liga pública:', error)
        
        // Manejo específico de errores
        if (error.message.includes('miembro') || error.message.includes('member')) {
          showFloatingNotification('error', 'Ya eres miembro de esta liga')
          // Refrescar las listas para sincronizar
          await Promise.all([
            loadMyLeagues(),
            loadPublicLeagues()
          ])
        } else {
          showFloatingNotification('error', error.message || 'Error al unirse a la liga')
        }
      } finally {
        isJoiningPublic.value = null
      }
    }

    // Funciones para crear liga privada
    const openCreateLeagueModal = () => {
      showCreateLeagueModal.value = true
      // Resetear formulario
      createLeagueForm.value = {
        name: '',
        description: '',
        initialBudget: 100000,
        maxFighters: 10,
        maxFightersEvent: 3,
        minFightersEvent: 1
      }
    }

    const closeCreateLeagueModal = () => {
      showCreateLeagueModal.value = false
    }

    const createPrivateLeague = async () => {
      if (!createLeagueForm.value.name.trim()) {
        showFloatingNotification('error', 'El nombre de la liga es obligatorio')
        return
      }

      isCreatingLeague.value = true
      try {
        const response = await leaguesService.createPrivateLeague(createLeagueForm.value)
        showFloatingNotification('success', `¡Liga "${createLeagueForm.value.name}" creada exitosamente!`)
        
        // Cerrar modal
        closeCreateLeagueModal()
        
        // Recargar mis ligas
        await loadMyLeagues()
        
        // Opcional: mostrar código de invitación
        if (response.invitationCode) {
          setTimeout(() => {
            showFloatingNotification('success', `Código de invitación: ${response.invitationCode}`)
          }, 2000)
        }
      } catch (error) {
        console.error('Error al crear liga privada:', error)
        showFloatingNotification('error', error.message || 'Error al crear la liga')
      } finally {
        isCreatingLeague.value = false
      }
    }

    const enterLeague = (league) => {
      router.push(`/league/${league.id}`)
    }

    const goToProfile = () => {
      router.push('/profile')
    }

    const handleLogout = async () => {
      try {
        await authStore.logout()
        router.push('/')
      } catch (error) {
        console.error('Error al cerrar sesión:', error)
        router.push('/')
      }
    }

    const goToUFCEvents = () => {
      window.open('https://www.ufc.com/events', '_blank')
    }

    // Funciones de utilidad
    const getPositionClass = (position) => {
      if (position === 1) return 'gold'
      if (position === 2) return 'silver'
      if (position === 3) return 'bronze'
      return ''
    }

    const handleImageError = (event) => {
      event.target.src = '/images/ufc-default.jpg'
    }

    const showFloatingNotification = (type, text) => {
      notificationType.value = type
      notificationText.value = text
      showNotification.value = true
      
      setTimeout(() => {
        hideNotification()
      }, 4000)
    }

    const hideNotification = () => {
      showNotification.value = false
    }

    // Cargar datos al montar
    onMounted(async () => {
      console.log('🚀 Cargando datos del dashboard...')
      console.log('👤 Usuario actual:', user.value)
      
      // Debug del usuario
      if (user.value) {
        console.log('📧 Email:', user.value.email)
        console.log('👨‍💼 Username:', user.value.username) 
        console.log('🏷️ First Name:', user.value.firstName)
        console.log('🏷️ Last Name:', user.value.lastName)
        console.log('🔐 Email confirmado:', user.value.emailConfirmed)
        console.log('👥 Roles:', user.value.roles)
      }
      
      // Cargar datos en paralelo
      await Promise.all([
        loadNextEvent(),
        loadMyLeagues()
      ])
      
      // Cargar ligas públicas después de cargar mis ligas (para filtrar correctamente)
      await loadPublicLeagues()
      
      console.log('✅ Datos del dashboard cargados')
    })

    return {
      // ✅ Estado del usuario - ahora como computed
      user,
      userDisplayName,
      userInitials,
      
      // Estados de carga
      isLoadingEvent,
      isLoadingMyLeagues,
      isLoadingPublicLeagues,
      isJoiningPrivate,
      isJoiningPublic,
      
      // Datos
      nextEvent,
      myLeagues,
      publicLeagues,
      privateCode,
      
      // Estados del modal de crear liga
      showCreateLeagueModal,
      isCreatingLeague,
      createLeagueForm,
      
      // Computed del evento
      eventBadge,
      eventTitle,
      formattedDate,
      
      // Notificaciones
      showNotification,
      notificationType,
      notificationText,
      
      // Funciones
      loadPublicLeagues,
      joinPrivateLeague,
      joinPublicLeague,
      enterLeague,
      goToProfile,
      handleLogout,
      goToUFCEvents,
      getPositionClass,
      handleImageError,
      hideNotification,
      
      // Funciones de crear liga
      openCreateLeagueModal,
      closeCreateLeagueModal,
      createPrivateLeague
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

.btn-create-league {
  background: var(--gradient-primary);
  color: var(--white);
  border: 1px solid var(--primary);
}

.btn-create-league:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* === MODAL === */
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
  z-index: 2000;
}

.modal-content {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
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
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  color: var(--gray-light);
  font-size: 1.5rem;
  cursor: pointer;
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
}

.modal-close:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

.modal-body {
  padding: var(--space-xl);
}

.form-group {
  margin-bottom: var(--space-lg);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
}

.form-label {
  display: block;
  color: var(--white);
  font-weight: 600;
  margin-bottom: var(--space-sm);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--gray-light);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-select {
  cursor: pointer;
}

.modal-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: flex-end;
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-actions .btn {
  padding: var(--space-md) var(--space-xl);
  font-weight: 600;
}

.btn-secondary {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-secondary:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.4);
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.btn-primary:disabled,
.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

/* === RESPONSIVE MODAL === */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: var(--space-md);
  }
  
  .modal-header,
  .modal-body {
    padding: var(--space-lg);
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: var(--space-md);
  }
  
  .modal-actions {
    flex-direction: column;
  }
  
  .modal-actions .btn {
    width: 100%;
  }
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

/* === ESTADOS DE CARGA === */
.loading-event,
.loading-section {
  padding: var(--space-xl);
  text-align: center;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-md);
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

.private-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.btn-join-public:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.btn-refresh:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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