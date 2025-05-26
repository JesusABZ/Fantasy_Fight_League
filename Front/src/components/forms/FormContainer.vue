<template>
  <div class="form-page">
    <!-- Fondo específico si se necesita -->
    <AppBackground v-if="showBackground" :variant="backgroundVariant" />

    <!-- Contenedor principal -->
    <div class="form-container-wrapper">
      <!-- Formulario -->
      <div :class="containerClasses">
        
        <!-- Header del formulario -->
        <div v-if="hasHeader" class="form-header">
          <!-- Icono -->
          <div v-if="icon" class="form-icon">
            <span class="icon">{{ icon }}</span>
          </div>
          
          <!-- Títulos -->
          <h2 :class="titleClasses">{{ title }}</h2>
          <p v-if="subtitle" :class="subtitleClasses">{{ subtitle }}</p>
        </div>

        <!-- Contenido del formulario -->
        <div class="form-content">
          <slot />
        </div>

        <!-- Footer del formulario -->
        <div v-if="$slots.footer" class="form-footer">
          <slot name="footer" />
        </div>

        <!-- Botón volver -->
        <div v-if="showBackButton" class="back-button-container">
          <BaseButton 
            variant="back"
            @click="handleBack"
          >
            ← {{ backButtonText }}
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'FormContainer',
  
  props: {
    // Contenido del header
    title: {
      type: String,
      required: true
    },
    
    subtitle: {
      type: String,
      default: ''
    },
    
    icon: {
      type: String,
      default: ''
    },
    
    // Tamaño del contenedor
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    
    // Mostrar fondo
    showBackground: {
      type: Boolean,
      default: false
    },
    
    backgroundVariant: {
      type: String,
      default: 'enhanced'
    },
    
    // Botón volver
    showBackButton: {
      type: Boolean,
      default: true
    },
    
    backButtonText: {
      type: String,
      default: 'Volver'
    },
    
    backRoute: {
      type: String,
      default: '/'
    },
    
    // Centrado vertical
    centerVertically: {
      type: Boolean,
      default: true
    }
  },
  
  emits: ['back'],
  
  setup(props, { emit }) {
    const router = useRouter()
    
    // Computed properties
    const hasHeader = computed(() => {
      return props.title || props.subtitle || props.icon
    })
    
    const containerClasses = computed(() => [
      'form-container',
      `form-container-${props.size}`,
      {
        'form-container-centered': props.centerVertically
      }
    ])
    
    const titleClasses = computed(() => [
      'form-title',
      {
        'form-title-large': props.size === 'large'
      }
    ])
    
    const subtitleClasses = computed(() => [
      'form-subtitle',
      {
        'form-subtitle-large': props.size === 'large'
      }
    ])
    
    // Métodos
    const handleBack = () => {
      emit('back')
      
      // Si no se maneja el evento, usar navegación por defecto
      if (!emit('back')) {
        if (window.history.length > 1) {
          router.go(-1)
        } else {
          router.push(props.backRoute)
        }
      }
    }
    
    return {
      hasHeader,
      containerClasses,
      titleClasses,
      subtitleClasses,
      handleBack
    }
  }
}
</script>

<style scoped>
.form-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-xl) 0;
}

.form-container-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  padding: 0 var(--space-lg);
}

.form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
  width: 100%;
  margin: 0 auto;
}

.form-container-centered {
  min-height: 600px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Tamaños */
.form-container-small {
  max-width: 400px;
  padding: var(--space-xl);
}

.form-container-medium {
  max-width: 600px;
}

.form-container-large {
  max-width: 800px;
  padding: var(--space-2xl) 3rem;
}

/* Header */
.form-header {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.form-icon {
  margin-bottom: var(--space-lg);
}

.icon {
  width: 80px;
  height: 80px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  font-size: 2.5rem;
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.form-title {
  font-family: var(--font-impact);
  font-size: 2.2rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-title-large {
  font-size: 2.8rem;
  margin-bottom: var(--space-lg);
}

.form-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
  line-height: 1.5;
}

.form-subtitle-large {
  font-size: 1.2rem;
  margin-bottom: var(--space-lg);
}

/* Content */
.form-content {
  flex: 1;
}

/* Footer */
.form-footer {
  margin-top: var(--space-xl);
  text-align: center;
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* Back button */
.back-button-container {
  margin-top: var(--space-xl);
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .form-page {
    padding: var(--space-lg) 0;
    align-items: flex-start;
  }
  
  .form-container-wrapper {
    padding: 0 var(--space-md);
  }
  
  .form-container-large {
    padding: var(--space-xl) var(--space-lg);
    min-height: auto;
  }
  
  .form-container-centered {
    min-height: auto;
  }
  
  .form-title-large {
    font-size: 2.2rem;
  }
}

@media (max-width: 480px) {
  .form-container {
    padding: var(--space-lg);
  }
  
  .form-title {
    font-size: 2rem;
  }
  
  .icon {
    width: 70px;
    height: 70px;
    font-size: 2rem;
  }
}
</style>