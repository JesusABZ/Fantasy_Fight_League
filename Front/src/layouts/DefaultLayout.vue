<template>
  <div class="default-layout">
    <!-- Fondo de la aplicación -->
    <AppBackground :variant="backgroundVariant" />

    <!-- Header - Solo en páginas públicas -->
    <AppHeader v-if="showHeader" :subtitle="headerSubtitle">
      <template #actions>
        <slot name="header-actions" />
      </template>
    </AppHeader>

    <!-- Contenido principal -->
    <main class="layout-main" :class="mainClasses">
      <slot />
    </main>

    <!-- Footer - Solo en páginas públicas -->
    <AppFooter v-if="showFooter">
      <template #links>
        <slot name="footer-links" />
      </template>
    </AppFooter>

    <!-- Notificaciones globales -->
    <BaseNotification
      v-if="showGlobalNotification"
      :show="showGlobalNotification"
      :type="globalNotificationType"
      :message="globalNotificationMessage"
      :position="notificationPosition"
      @close="hideGlobalNotification"
    />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useNotifications } from '@/composables/useNotifications'

export default {
  name: 'DefaultLayout',
  
  props: {
    // Control de header y footer
    showHeader: {
      type: Boolean,
      default: null // null = automático basado en la ruta
    },
    
    showFooter: {
      type: Boolean,
      default: null // null = automático basado en la ruta
    },
    
    headerSubtitle: {
      type: String,
      default: '¡Bienvenido a la plataforma de fantasy más emocionante de MMA!'
    },
    
    // Configuración del fondo
    backgroundVariant: {
      type: String,
      default: 'enhanced'
    },
    
    // Configuración del main
    centerContent: {
      type: Boolean,
      default: false
    },
    
    fullHeight: {
      type: Boolean,
      default: false
    },
    
    padding: {
      type: String,
      default: 'normal', // 'none', 'small', 'normal', 'large'
      validator: (value) => ['none', 'small', 'normal', 'large'].includes(value)
    },
    
    // Notificaciones
    notificationPosition: {
      type: String,
      default: 'top-right'
    }
  },
  
  setup(props) {
    const route = useRoute()
    const {
      showNotification: showGlobalNotification,
      notificationType: globalNotificationType,
      notificationText: globalNotificationMessage,
      hideNotification: hideGlobalNotification
    } = useNotifications()
    
    // Rutas que usan el layout público (con header y footer)
    const publicRoutes = ['Home', 'Login', 'Register', 'VerifyEmail', 'ForgotPassword', 'EmailUnverified', 'Support', 'About']
    
    // Determinar si mostrar header/footer automáticamente
    const showHeaderComputed = computed(() => {
      if (props.showHeader !== null) return props.showHeader
      return publicRoutes.includes(route.name)
    })
    
    const showFooterComputed = computed(() => {
      if (props.showFooter !== null) return props.showFooter
      return publicRoutes.includes(route.name)
    })
    
    // Clases CSS para el main
    const mainClasses = computed(() => [
      {
        'layout-main--centered': props.centerContent,
        'layout-main--full-height': props.fullHeight,
        'layout-main--no-header': !showHeaderComputed.value,
        'layout-main--no-footer': !showFooterComputed.value,
        [`layout-main--padding-${props.padding}`]: props.padding !== 'normal'
      }
    ])
    
    return {
      showHeader: showHeaderComputed,
      showFooter: showFooterComputed,
      mainClasses,
      showGlobalNotification,
      globalNotificationType,
      globalNotificationMessage,
      hideGlobalNotification
    }
  }
}
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.layout-main {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: var(--space-2xl) 0;
}

.layout-main--centered {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 200px); /* Ajustar según header/footer */
}

.layout-main--full-height {
  min-height: 100vh;
}

.layout-main--no-header {
  padding-top: 0;
}

.layout-main--no-footer {
  padding-bottom: 0;
}

.layout-main--no-header.layout-main--no-footer {
  min-height: 100vh;
}

/* Variantes de padding */
.layout-main--padding-none {
  padding: 0;
}

.layout-main--padding-small {
  padding: var(--space-lg) 0;
}

.layout-main--padding-large {
  padding: var(--space-2xl) 0 4rem;
}

/* Responsive */
@media (max-width: 768px) {
  .layout-main--centered {
    min-height: calc(100vh - 150px);
    align-items: flex-start;
    padding-top: var(--space-xl);
  }
}
</style>