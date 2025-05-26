<template>
  <footer v-if="showFooter" class="app-footer">
    <div class="container">
      <div class="footer-content">
        <p class="footer-text">
          © 2025 Fantasy Fight League - Desarrollado por 
          <span class="footer-highlight">Jesús Álvarez</span>
        </p>
        
        <!-- Enlaces adicionales -->
        <div v-if="$slots.links" class="footer-links">
          <slot name="links" />
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'AppFooter',
  
  props: {
    // Mostrar/ocultar footer
    show: {
      type: Boolean,
      default: null // null = automático basado en la ruta
    },
    
    // Texto personalizado
    text: {
      type: String,
      default: ''
    }
  },
  
  setup(props) {
    const route = useRoute()
    
    // Rutas que muestran el footer público
    const publicRoutes = ['Home', 'Login', 'Register', 'VerifyEmail', 'ForgotPassword', 'EmailUnverified', 'Support', 'About']
    
    const showFooter = computed(() => {
      // Si se especifica explícitamente, usar ese valor
      if (props.show !== null) {
        return props.show
      }
      
      // Si no, determinar automáticamente basado en la ruta
      return publicRoutes.includes(route.name)
    })
    
    return {
      showFooter
    }
  }
}
</script>

<style scoped>
.app-footer {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: auto;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.footer-text {
  color: var(--gray-light);
  text-align: center;
  flex: 1;
}

.footer-highlight {
  color: var(--primary);
  font-weight: 600;
}

.footer-links {
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

/* Responsive */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>