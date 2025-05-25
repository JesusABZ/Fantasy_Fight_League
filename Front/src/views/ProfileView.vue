<template>
  <div class="profile">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header del perfil -->
    <div class="profile-header">
      <div class="container">
        <div class="header-content">
          <button class="btn btn-back" @click="goBack">
            ← Volver al Dashboard
          </button>
          <h1 class="title-hero">MI PERFIL</h1>
          <button class="btn btn-logout" @click="handleLogout">
            Cerrar Sesión
          </button>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        
        <!-- Tarjeta principal del perfil -->
        <div class="profile-main-card">
          
          <!-- Avatar grande centrado -->
          <div class="avatar-section">
            <div class="avatar-extra-large">
              <img 
                v-if="currentUser.profileImageUrl" 
                :src="currentUser.profileImageUrl" 
                :alt="currentUser.username"
                class="avatar-image"
              />
              <span v-else class="avatar-initials">{{ userInitials }}</span>
            </div>
          </div>

          <!-- Información del usuario vertical -->
          <div class="user-info-vertical">
            <h2 class="username-large">{{ currentUser.username }}</h2>
            <p class="full-name-large">{{ fullName }}</p>
            <p class="email-large">{{ currentUser.email }}</p>
            
            <div class="verification-status-centered">
              <span 
                class="status-badge-large" 
                :class="currentUser.emailConfirmed ? 'verified' : 'unverified'"
              >
                <span class="status-icon">
                  {{ currentUser.emailConfirmed ? '✅' : '❌' }}
                </span>
                {{ currentUser.emailConfirmed ? 'Email Verificado' : 'Email No Verificado' }}
              </span>
            </div>
          </div>

          <!-- Botones de acción centrados -->
          <div class="profile-actions-centered">
            <button class="btn btn-primary-large" @click="goToEditProfile">
              ✏️ Editar Perfil
            </button>
            
            <button class="btn btn-secondary-large" @click="goToSupport">
              📧 Contactar Soporte
            </button>
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
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ProfileView',
  setup() {
    const router = useRouter()

    // Estado del usuario (simulado - datos básicos que necesitas)
    const currentUser = reactive({
      id: 2,
      username: 'usuario_prueba2',
      firstName: 'Juan Carlos',
      lastName: 'Pérez García',
      email: 'usuario@yopmail.com',
      emailConfirmed: true,
      profileImageUrl: null // Cambia esto por una URL real si tienes imagen
    })

    // Estadísticas básicas del usuario (simuladas)
    const userStats = reactive({
      activeLeagues: 3,
      totalPoints: 2890,
      bestPosition: 1,
      level: 5
    })

    // Actividad reciente simplificada
    const recentActivity = ref([
      {
        id: 1,
        icon: '🏆',
        description: 'Ganaste la liga "Oficina Warriors"',
        date: 'Hace 2 días'
      },
      {
        id: 2,
        icon: '⚔️',
        description: 'Te uniste a la liga "UFC Vegas 107"',
        date: 'Hace 3 días'
      },
      {
        id: 3,
        icon: '🎯',
        description: 'Obtuviste 285 puntos en el último evento',
        date: 'Hace 5 días'
      }
    ])

    // Estados para notificaciones
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Computed properties
    const fullName = computed(() => {
      return `${currentUser.firstName} ${currentUser.lastName}`
    })

    const userInitials = computed(() => {
      return `${currentUser.firstName[0]}${currentUser.lastName[0]}`.toUpperCase()
    })

    // Funciones de navegación
    const goBack = () => {
      router.push('/dashboard')
    }

    const handleLogout = () => {
      // TODO: Limpiar store de autenticación
      console.log('Cerrando sesión...')
      showFloatingNotification('success', 'Sesión cerrada correctamente')
      
      setTimeout(() => {
        router.push('/')
      }, 1000)
    }

    const goToEditProfile = () => {
      router.push('/profile/edit')
    }

    const goToSupport = () => {
      router.push('/support')
    }

    // Función para mostrar notificaciones
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
      userStats,
      recentActivity,
      showNotification,
      notificationType,
      notificationText,
      fullName,
      userInitials,
      goBack,
      handleLogout,
      goToEditProfile,
      goToSupport,
      hideNotification
    }
  }
}
</script>

<style scoped>
.profile {
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
.profile-header {
  position: relative;
  z-index: 1;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-hero {
  font-family: var(--font-impact);
  font-size: 2.5rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
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

.btn-logout {
  background: transparent;
  color: var(--error);
  border: 1px solid var(--error);
}

.btn-logout:hover {
  background: var(--error);
  color: var(--white);
  transform: translateY(-2px);
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  box-shadow: var(--shadow-md);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-secondary {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-secondary:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

/* === TARJETA PRINCIPAL === */
.profile-main-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl) var(--space-xl);
  box-shadow: var(--shadow-lg);
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

/* === AVATAR SECCIÓN === */
.avatar-section {
  margin-bottom: var(--space-2xl);
}

.avatar-extra-large {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto;
  box-shadow: var(--shadow-lg), var(--shadow-glow);
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid rgba(255, 107, 53, 0.3);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-initials {
  font-size: 4.5rem;
  font-weight: bold;
  color: var(--white);
  font-family: var(--font-impact);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

/* === INFORMACIÓN VERTICAL === */
.user-info-vertical {
  margin-bottom: var(--space-2xl);
}

.username-large {
  font-family: var(--font-impact);
  font-size: 3.5rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
  line-height: 1;
}

.full-name-large {
  color: var(--gray-light);
  font-size: 1.8rem;
  margin-bottom: var(--space-md);
  font-weight: 500;
  line-height: 1.2;
}

.email-large {
  color: var(--primary);
  font-size: 1.4rem;
  margin-bottom: var(--space-xl);
  font-weight: 600;
  word-break: break-all;
}

.verification-status-centered {
  display: flex;
  justify-content: center;
  align-items: center;
}

.status-badge-large {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md) var(--space-xl);
  border-radius: var(--radius-full);
  font-size: 1.1rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-badge-large.verified {
  background: rgba(16, 185, 129, 0.2);
  border: 2px solid var(--success);
  color: var(--success);
}

.status-badge-large.unverified {
  background: rgba(239, 68, 68, 0.2);
  border: 2px solid var(--error);
  color: var(--error);
}

.status-icon {
  font-size: 1.3rem;
}

/* === BOTONES CENTRADOS === */
.profile-actions-centered {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  align-items: center;
}

.btn-primary-large,
.btn-secondary-large {
  width: 100%;
  max-width: 300px;
  padding: var(--space-lg) var(--space-xl);
  font-size: 1.2rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-radius: var(--radius-lg);
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
}

.btn-primary-large {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  box-shadow: var(--shadow-md);
}

.btn-primary-large:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-secondary-large {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-secondary-large:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-3px);
}

/* === SECCIÓN DE ACTIVIDAD === */
.activity-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
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

.notification.info {
  border: 2px solid var(--info);
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
@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-md);
  }

  .header-content {
    flex-direction: column;
    gap: var(--space-md);
    text-align: center;
  }

  .profile-main-card {
    padding: var(--space-xl) var(--space-lg);
    max-width: none;
    margin: 0;
  }

  .avatar-extra-large {
    width: 150px;
    height: 150px;
  }

  .avatar-initials {
    font-size: 3.5rem;
  }

  .username-large {
    font-size: 2.8rem;
  }

  .full-name-large {
    font-size: 1.5rem;
  }

  .email-large {
    font-size: 1.2rem;
  }

  .btn-primary-large,
  .btn-secondary-large {
    max-width: 280px;
    font-size: 1.1rem;
  }

  .notification {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: var(--space-lg) 0;
  }

  .title-hero {
    font-size: 2rem;
  }

  .profile-main-card {
    padding: var(--space-lg);
  }

  .avatar-extra-large {
    width: 120px;
    height: 120px;
  }

  .avatar-initials {
    font-size: 3rem;
  }

  .username-large {
    font-size: 2.2rem;
  }

  .full-name-large {
    font-size: 1.3rem;
  }

  .email-large {
    font-size: 1.1rem;
  }

  .btn-primary-large,
  .btn-secondary-large {
    max-width: 100%;
    font-size: 1rem;
    min-height: 50px;
  }

  .status-badge-large {
    font-size: 1rem;
    padding: var(--space-sm) var(--space-lg);
  }
}
</style>