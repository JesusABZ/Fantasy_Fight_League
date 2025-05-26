<template>
  <header v-if="showHeader" class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo/Título -->
        <div class="header-brand">
          <h1 class="title-hero">FANTASY FIGHT LEAGUE</h1>
          <p v-if="subtitle" class="header-subtitle">{{ subtitle }}</p>
        </div>
        
        <!-- Acciones del header -->
        <div v-if="$slots.actions" class="header-actions">
          <slot name="actions" />
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'AppHeader',
  
  props: {
    // Subtítulo opcional
    subtitle: {
      type: String,
      default: '¡Bienvenido a la plataforma de fantasy más emocionante de MMA!'
    },
    
    // Mostrar/ocultar header
    show: {
      type: Boolean,
      default: null // null = automático basado en la ruta
    }
  },
  
  setup(props) {
    const route = useRoute()
    
    // Rutas que muestran el header público
    const publicRoutes = ['Home', 'Login', 'Register', 'VerifyEmail', 'ForgotPassword', 'EmailUnverified', 'Support', 'About']
    
    const showHeader = computed(() => {
      // Si se especifica explícitamente, usar ese valor
      if (props.show !== null) {
        return props.show
      }
      
      // Si no, determinar automáticamente basado en la ruta
      return publicRoutes.includes(route.name)
    })
    
    return {
      showHeader
    }
  }
}
</script>

<style scoped>
.app-header {
  position: relative;
  z-index: 1;
  padding: var(--space-2xl) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.header-brand {
  flex: 1;
  text-align: center;
}

.header-subtitle {
  color: var(--gray-light);
  font-size: 1.2rem;
  margin-top: var(--space-md);
  text-align: center;
}

.header-actions {
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

/* Responsive */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
}
</style>