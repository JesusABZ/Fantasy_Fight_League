<template>
  <div class="auth-layout">
    <!-- Fondo estático -->
    <AppBackground variant="enhanced" />

    <!-- Contenido principal centrado -->
    <main class="auth-main">
      <div class="auth-container" :class="containerClasses">
        <slot />
      </div>
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
import { useNotifications } from '@/composables/useNotifications'

export default {
  name: 'AuthLayout',
  
  props: {
    // Tamaño del contenedor
    containerSize: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large', 'full'].includes(value)
    },
    
    // Centrado vertical
    centerVertically: {
      type: Boolean,
      default: true
    },
    
    // Padding del contenedor
    containerPadding: {
      type: String,
      default: 'normal',
      validator: (value) => ['small', 'normal', 'large'].includes(value)
    },
    
    // Posición de notificaciones
    notificationPosition: {
      type: String,
      default: 'top-right'
    }
  },
  
  setup(props) {
    const {
      showNotification,
      notificationType,
      notificationText: notificationMessage,
      hideNotification
    } = useNotifications()
    
    const containerClasses = computed(() => [
      `auth-container--${props.containerSize}`,
      {
        'auth-container--centered': props.centerVertically,
        [`auth-container--padding-${props.containerPadding}`]: props.containerPadding !== 'normal'
      }
    ])
    
    return {
      containerClasses,
      showNotification,
      notificationType,
      notificationMessage,
      hideNotification
    }
  }
}
</script>

<style scoped>
.auth-layout {
  position: relative;
  min-height: 100vh;
}

.auth-main {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-xl) var(--space-lg);
}

.auth-container {
  width: 100%;
  position: relative;
}

/* Tamaños del contenedor */
.auth-container--small {
  max-width: 400px;
}

.auth-container--medium {
  max-width: 600px;
}

.auth-container--large {
  max-width: 800px;
}

.auth-container--full {
  max-width: 100%;
}

/* Centrado vertical */
.auth-container--centered {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: calc(100vh - 2 * var(--space-xl));
}

/* Variantes de padding */
.auth-container--padding-small {
  padding: var(--space-md);
}

.auth-container--padding-large {
  padding: var(--space-2xl);
}

/* Responsive */
@media (max-width: 768px) {
  .auth-main {
    padding: var(--space-lg) var(--space-md);
    align-items: flex-start;
  }
  
  .auth-container--centered {
    min-height: auto;
    justify-content: flex-start;
    padding-top: var(--space-xl);
  }
  
  .auth-container--small,
  .auth-container--medium,
  .auth-container--large {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .auth-main {
    padding: var(--space-md);
  }
}
</style>