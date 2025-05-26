<template>
  <Teleport to="body">
    <Transition name="notification" appear>
      <div 
        v-if="show" 
        :class="notificationClasses"
        @click="handleClick"
        role="alert"
        :aria-live="type === 'error' ? 'assertive' : 'polite'"
      >
        <!-- Icono -->
        <div class="notification-icon">
          <span v-if="type === 'success'">✅</span>
          <span v-else-if="type === 'error'">❌</span>
          <span v-else-if="type === 'warning'">⚠️</span>
          <span v-else-if="type === 'info'">ℹ️</span>
          <span v-else>{{ customIcon }}</span>
        </div>
        
        <!-- Contenido -->
        <div class="notification-content">
          <div v-if="title" class="notification-title">{{ title }}</div>
          <div class="notification-text">
            <slot>{{ message }}</slot>
          </div>
        </div>
        
        <!-- Botón cerrar -->
        <button 
          v-if="closable"
          class="notification-close"
          @click.stop="handleClose"
          aria-label="Cerrar notificación"
        >
          ✕
        </button>
        
        <!-- Barra de progreso para auto-dismiss -->
        <div 
          v-if="autoDismiss && duration > 0"
          class="notification-progress"
          :style="{ animationDuration: duration + 'ms' }"
        ></div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
import { computed, onMounted, onUnmounted } from 'vue'

export default {
  name: 'BaseNotification',
  
  props: {
    // Control
    show: {
      type: Boolean,
      default: false
    },
    
    // Contenido
    title: {
      type: String,
      default: ''
    },
    
    message: {
      type: String,
      default: ''
    },
    
    // Tipo de notificación
    type: {
      type: String,
      default: 'info',
      validator: (value) => ['success', 'error', 'warning', 'info', 'custom'].includes(value)
    },
    
    // Icono personalizado
    customIcon: {
      type: String,
      default: '📢'
    },
    
    // Comportamiento
    closable: {
      type: Boolean,
      default: true
    },
    
    clickable: {
      type: Boolean,
      default: false
    },
    
    // Auto-dismiss
    autoDismiss: {
      type: Boolean,
      default: true
    },
    
    duration: {
      type: Number,
      default: 4000 // 4 segundos
    },
    
    // Posición
    position: {
      type: String,
      default: 'top-right',
      validator: (value) => [
        'top-left', 'top-center', 'top-right',
        'bottom-left', 'bottom-center', 'bottom-right'
      ].includes(value)
    },
    
    // Tamaño
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    }
  },
  
  emits: ['close', 'click'],
  
  setup(props, { emit }) {
    let timeoutId = null
    
    // Computed properties
    const notificationClasses = computed(() => [
      'notification',
      `notification-${props.type}`,
      `notification-${props.position}`,
      `notification-${props.size}`,
      {
        'notification-clickable': props.clickable
      }
    ])
    
    // Métodos
    const handleClose = () => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
      emit('close')
    }
    
    const handleClick = () => {
      if (props.clickable) {
        emit('click')
        if (props.autoDismiss) {
          handleClose()
        }
      }
    }
    
    // Auto-dismiss
    onMounted(() => {
      if (props.autoDismiss && props.duration > 0) {
        timeoutId = setTimeout(() => {
          handleClose()
        }, props.duration)
      }
    })
    
    onUnmounted(() => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
    })
    
    return {
      notificationClasses,
      handleClose,
      handleClick
    }
  }
}
</script>

<style scoped>
.notification {
  position: fixed;
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: flex-start;
  gap: var(--space-md);
  z-index: 1000;
  max-width: 400px;
  min-width: 300px;
  overflow: hidden;
  position: relative;
}

/* Posiciones */
.notification-top-left {
  top: var(--space-xl);
  left: var(--space-xl);
}

.notification-top-center {
  top: var(--space-xl);
  left: 50%;
  transform: translateX(-50%);
}

.notification-top-right {
  top: var(--space-xl);
  right: var(--space-xl);
}

.notification-bottom-left {
  bottom: var(--space-xl);
  left: var(--space-xl);
}

.notification-bottom-center {
  bottom: var(--space-xl);
  left: 50%;
  transform: translateX(-50%);
}

.notification-bottom-right {
  bottom: var(--space-xl);
  right: var(--space-xl);
}

/* Tipos */
.notification-success {
  border: 2px solid var(--success);
}

.notification-error {
  border: 2px solid var(--error);
}

.notification-warning {
  border: 2px solid var(--warning);
}

.notification-info {
  border: 2px solid var(--info);
}

.notification-custom {
  border: 2px solid var(--primary);
}

/* Tamaños */
.notification-small {
  padding: var(--space-md);
  max-width: 300px;
  min-width: 250px;
}

.notification-medium {
  padding: var(--space-lg);
  max-width: 400px;
  min-width: 300px;
}

.notification-large {
  padding: var(--space-xl);
  max-width: 500px;
  min-width: 350px;
}

/* Elementos */
.notification-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: var(--space-xs);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 600;
  color: var(--white);
  margin-bottom: var(--space-xs);
  font-size: 0.9rem;
}

.notification-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
  word-wrap: break-word;
}

.notification-close {
  background: none;
  border: none;
  color: var(--gray-light);
  cursor: pointer;
  font-size: 1.2rem;
  padding: var(--space-xs);
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.notification-close:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

/* Clickable */
.notification-clickable {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.notification-clickable:hover {
  transform: translateY(-2px);
}

/* Barra de progreso */
.notification-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background: var(--primary);
  width: 100%;
  transform-origin: left;
  animation: progress linear forwards;
}

@keyframes progress {
  from {
    transform: scaleX(1);
  }
  to {
    transform: scaleX(0);
  }
}

/* Animaciones */
.notification-enter-active {
  transition: all 0.3s ease;
}

.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.notification-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

/* Para posiciones de la izquierda */
.notification-top-left.notification-enter-from,
.notification-bottom-left.notification-enter-from {
  transform: translateX(-100%);
}

.notification-top-left.notification-leave-to,
.notification-bottom-left.notification-leave-to {
  transform: translateX(-100%);
}

/* Para posiciones del centro */
.notification-top-center.notification-enter-from,
.notification-bottom-center.notification-enter-from {
  transform: translateX(-50%) translateY(-100%);
}

.notification-top-center.notification-leave-to,
.notification-bottom-center.notification-leave-to {
  transform: translateX(-50%) translateY(-100%);
}

/* Responsive */
@media (max-width: 768px) {
  .notification {
    max-width: 90%;
    min-width: 280px;
  }
  
  .notification-top-left,
  .notification-top-right,
  .notification-bottom-left,
  .notification-bottom-right {
    left: var(--space-md);
    right: var(--space-md);
  }
  
  .notification-top-left,
  .notification-top-right {
    top: var(--space-md);
  }
  
  .notification-bottom-left,
  .notification-bottom-right {
    bottom: var(--space-md);
  }
}
</style>