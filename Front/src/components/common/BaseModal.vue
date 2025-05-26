<template>
  <Teleport to="body">
    <Transition name="modal" appear>
      <div v-if="show" class="modal-overlay" @click="handleOverlayClick">
        <div 
          :class="modalClasses" 
          @click.stop
          role="dialog"
          :aria-labelledby="titleId"
          aria-modal="true"
        >
          <!-- Header -->
          <div v-if="hasHeader" class="modal-header">
            <div class="modal-title-section">
              <h3 :id="titleId" class="modal-title">{{ title }}</h3>
              <p v-if="subtitle" class="modal-subtitle">{{ subtitle }}</p>
            </div>
            <button
              v-if="closable"
              class="modal-close"
              @click="handleClose"
              aria-label="Cerrar modal"
            >
              ✕
            </button>
          </div>

          <!-- Content -->
          <div class="modal-content">
            <slot />
          </div>

          <!-- Footer -->
          <div v-if="$slots.footer" class="modal-footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
import { computed, watch, nextTick } from 'vue'

export default {
  name: 'BaseModal',
  
  props: {
    // Control del modal
    show: {
      type: Boolean,
      default: false
    },
    
    // Contenido del header
    title: {
      type: String,
      default: ''
    },
    
    subtitle: {
      type: String,
      default: ''
    },
    
    // Comportamiento
    closable: {
      type: Boolean,
      default: true
    },
    
    closeOnOverlay: {
      type: Boolean,
      default: true
    },
    
    // Tamaño del modal
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large', 'full'].includes(value)
    },
    
    // Variante
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'danger', 'info', 'success'].includes(value)
    }
  },
  
  emits: ['close', 'overlay-click'],
  
  setup(props, { emit }) {
    // ID único para el título
    const titleId = `modal-title-${Math.random().toString(36).substr(2, 9)}`
    
    // Computed properties
    const hasHeader = computed(() => {
      return props.title || props.subtitle || props.closable
    })
    
    const modalClasses = computed(() => [
      'modal-content',
      `modal-${props.size}`,
      `modal-${props.variant}`
    ])
    
    // Métodos
    const handleClose = () => {
      emit('close')
    }
    
    const handleOverlayClick = () => {
      emit('overlay-click')
      if (props.closeOnOverlay) {
        handleClose()
      }
    }
    
    // Manejar escape key
    const handleKeydown = (event) => {
      if (event.key === 'Escape' && props.closable) {
        handleClose()
      }
    }
    
    // Bloquear scroll del body cuando el modal está abierto
    watch(() => props.show, (newValue) => {
      if (newValue) {
        document.body.style.overflow = 'hidden'
        document.addEventListener('keydown', handleKeydown)
        
        // Focus management
        nextTick(() => {
          const modal = document.querySelector('.modal-content')
          if (modal) {
            modal.focus()
          }
        })
      } else {
        document.body.style.overflow = ''
        document.removeEventListener('keydown', handleKeydown)
      }
    })
    
    return {
      titleId,
      hasHeader,
      modalClasses,
      handleClose,
      handleOverlayClick
    }
  }
}
</script>

<style scoped>
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
  z-index: 1000;
  padding: var(--space-lg);
}

.modal-content {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  max-height: 90vh;
  overflow-y: auto;
  outline: none;
  box-shadow: var(--shadow-lg);
}

/* Tamaños */
.modal-small {
  max-width: 400px;
  width: 90%;
}

.modal-medium {
  max-width: 600px;
  width: 90%;
}

.modal-large {
  max-width: 800px;
  width: 95%;
}

.modal-full {
  width: 95%;
  height: 90vh;
  max-width: none;
  max-height: 90vh;
}

/* Variantes */
.modal-danger {
  border-color: rgba(239, 68, 68, 0.5);
}

.modal-info {
  border-color: rgba(59, 130, 246, 0.5);
}

.modal-success {
  border-color: rgba(16, 185, 129, 0.5);
}

/* Header */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--space-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title-section {
  flex: 1;
}

.modal-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.modal-subtitle {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.modal-close {
  background: none;
  border: none;
  color: var(--gray-light);
  font-size: 1.5rem;
  cursor: pointer;
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
  margin-left: var(--space-md);
}

.modal-close:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

/* Content */
.modal-content {
  padding: var(--space-xl);
}

/* Footer */
.modal-footer {
  padding: var(--space-xl);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  gap: var(--space-md);
  justify-content: flex-end;
}

/* Animaciones */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.9) translateY(-50px);
}

/* Responsive */
@media (max-width: 768px) {
  .modal-overlay {
    padding: var(--space-md);
  }
  
  .modal-small,
  .modal-medium,
  .modal-large {
    width: 95%;
    max-width: none;
  }
  
  .modal-header,
  .modal-content,
  .modal-footer {
    padding: var(--space-lg);
  }
  
  .modal-footer {
    flex-direction: column;
  }
}
</style>