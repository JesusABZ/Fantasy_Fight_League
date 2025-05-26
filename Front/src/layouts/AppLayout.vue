<template>
  <div class="app-layout">
    <!-- Fondo de la aplicación -->
    <AppBackground variant="enhanced" />

    <!-- Header de la aplicación logueada -->
    <header class="app-header">
      <div class="container">
        <div class="header-content">
          <!-- Navegación izquierda -->
          <div class="header-nav">
            <button 
              v-if="showBackButton"
              class="btn btn-back" 
              @click="handleBack"
            >
              ← {{ backButtonText }}
            </button>
            <slot name="nav-left" />
          </div>
          
          <!-- Título central -->
          <div class="header-title">
            <h1 v-if="title" class="page-title">{{ title }}</h1>
            <p v-if="subtitle" class="page-subtitle">{{ subtitle }}</p>
            <slot name="title" />
          </div>
          
          <!-- Acciones derecha -->
          <div class="header-actions">
            <slot name="actions">
              <!-- Usuario por defecto -->
              <div v-if="showUserInfo" class="user-info">
                <div class="user-avatar">
                  <img v-if="user?.profileImageUrl" :src="user.profileImageUrl" :alt="user.username" />
                  <span v-else class="user-initials">{{ userInitials }}</span>
                </div>
                <div class="user-details">
                  <span class="username">{{ userDisplayName }}</span>
                </div>
              </div>
              
              <!-- Botón de perfil -->
              <BaseButton
                v-if="showProfileButton"
                variant="profile"
                @click="goToProfile"
              >
                Mi Perfil
              </BaseButton>
              
              <!-- Botón de logout -->
              <BaseButton
                v-if="showLogoutButton"
                variant="danger"
                @click="handleLogout"
              >
                Cerrar Sesión
              </BaseButton>
            </slot>
          </div>
        </div>
      </div>
    </header>

    <!-- Contenido principal -->
    <main class="app-main" :class="mainClasses">
      <div v-if="useContainer" class="container" :class="containerClasses">
        <slot />
      </div>
      <slot v-else />
    </main>

    <!-- Notificaciones -->
    <BaseNotification
      v-if="showNotification"
      :show="showNotification"
      :type="notificationType"
      :message="notificationMessage"
      :position="notificationPosition"
      @close="hideNotification"
    />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { useNotifications } from '@/composables/useNotifications'
import { useNavigation } from '@/composables/useNavigation'

export default {
  name: 'AppLayout',
  
  props: {
    // Header
    title: {
      type: String,
      default: ''
    },
    
    subtitle: {
      type: String,
      default: ''
    },
    
    // Navegación
    showBackButton: {
      type: Boolean,
      default: false
    },
    
    backButtonText: {
      type: String,
      default: 'Volver'
    },
    
    backRoute: {
      type: String,
      default: '/dashboard'
    },
    
    // Usuario
    showUserInfo: {
      type: Boolean,
      default: true
    },
    
    showProfileButton: {
      type: Boolean,
      default: true
    },
    
    showLogoutButton: {
      type: Boolean,
      default: true
    },
    
    // Layout
    useContainer: {
      type: Boolean,
      default: true
    },
    
    containerSize: {
      type: String,
      default: 'large',
      validator: (value) => ['small', 'medium', 'large', 'full'].includes(value)
    },
    
    paddingBottom: {
      type: String,
      default: 'normal',
      validator: (value) => ['none', 'small', 'normal', 'large'].includes(value)
    },
    
    // Notificaciones
    notificationPosition: {
      type: String,
      default: 'top-right'
    }
  },
  
  emits: ['back', 'logout'],
  
  setup(props, { emit }) {
    const router = useRouter()
    const { user, getUserDisplayName, getUserInitials, handleLogout: authLogout } = useAuth()
    const { goBackSmart } = useNavigation()
    const {
      showNotification,
      notificationType,
      notificationText: notificationMessage,
      hideNotification
    } = useNotifications()
    
    // Computed properties
    const userDisplayName = computed(() => getUserDisplayName())
    const userInitials = computed(() => getUserInitials())
    
    const mainClasses = computed(() => [
      {
        [`app-main--padding-${props.paddingBottom}`]: props.paddingBottom !== 'normal'
      }
    ])
    
    const containerClasses = computed(() => [
      `container--${props.containerSize}`
    ])
    
    // Métodos
    const handleBack = () => {
      emit('back')
      
      // Si no se maneja el evento, usar navegación inteligente
      if (!emit('back')) {
        if (props.backRoute) {
          router.push(props.backRoute)
        } else {
          goBackSmart('/dashboard')
        }
      }
    }
    
    const goToProfile = () => {
      router.push('/profile')
    }
    
    const handleLogout = async () => {
      emit('logout')
      
      // Si no se maneja el evento, usar logout por defecto
      if (!emit('logout')) {
        await authLogout()
      }
    }
    
    return {
      user,
      userDisplayName,
      userInitials,
      mainClasses,
      containerClasses,
      showNotification,
      notificationType,
      notificationMessage,
      hideNotification,
      handleBack,
      goToProfile,
      handleLogout
    }
  }
}
</script>

<style scoped>
.app-layout {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* === HEADER === */
.app-header {
  position: relative;
  z-index: 10;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.container {
  max-width: var(--breakpoint-2xl);
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.container--small { max-width: 600px; }
.container--medium { max-width: 900px; }
.container--large { max-width: var(--breakpoint-2xl); }
.container--full { max-width: 100%; }

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.header-nav {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  min-width: 120px;
}

.header-title {
  flex: 1;
  text-align: center;
}

.page-title {
  font-family: var(--font-impact);
  font-size: clamp(1.5rem, 3vw, 2.2rem);
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  margin-bottom: var(--space-xs);
}

.page-subtitle {
  color: var(--gray-light);
  font-size: 1rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  min-width: 120px;
  justify-content: flex-end;
}

/* === USER INFO === */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 107, 53, 0.3);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-initials {
  font-family: var(--font-impact);
  font-size: 0.9rem;
  color: var(--white);
  font-weight: bold;
}

.username {
  color: var(--white);
  font-weight: 600;
  font-size: 0.9rem;
}

/* === MAIN === */
.app-main {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.app-main--padding-none {
  padding: 0;
}

.app-main--padding-small {
  padding: var(--space-lg) 0;
}

.app-main--padding-large {
  padding: var(--space-2xl) 0 4rem;
}

/* === BOTONES === */
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
  gap: var(--space-xs);
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
  
  .header-nav,
  .header-actions {
    min-width: auto;
  }
  
  .header-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .user-info {
    order: -1;
  }
}

@media (max-width: 480px) {
  .app-header {
    padding: var(--space-md) 0;
  }
  
  .header-actions {
    flex-direction: column;
    gap: var(--space-sm);
  }
}
</style>