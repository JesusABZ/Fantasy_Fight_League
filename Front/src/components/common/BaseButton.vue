<template>
  <component
    :is="tag"
    :type="tag === 'button' ? type : undefined"
    :to="tag === 'router-link' ? to : undefined"
    :href="tag === 'a' ? href : undefined"
    :disabled="disabled || loading"
    :class="buttonClasses"
    @click="handleClick"
  >
    <!-- Icono de carga -->
    <span v-if="loading" class="btn-loading-spinner">⏳</span>
    
    <!-- Icono personalizado -->
    <span v-else-if="icon" class="btn-icon">{{ icon }}</span>
    
    <!-- Contenido del botón -->
    <span class="btn-content">
      <slot>{{ text }}</slot>
    </span>
  </component>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'BaseButton',
  props: {
    // Variante del botón
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => [
        'primary', 'secondary', 'back', 'danger', 'info', 
        'event', 'join', 'security', 'profile'
      ].includes(value)
    },
    
    // Tamaño del botón
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    
    // Tipo de elemento HTML
    tag: {
      type: String,
      default: 'button',
      validator: (value) => ['button', 'a', 'router-link'].includes(value)
    },
    
    // Tipo de botón (solo para tag="button")
    type: {
      type: String,
      default: 'button'
    },
    
    // Para router-link
    to: {
      type: [String, Object],
      default: null
    },
    
    // Para enlaces externos
    href: {
      type: String,
      default: null
    },
    
    // Estados
    disabled: {
      type: Boolean,
      default: false
    },
    
    loading: {
      type: Boolean,
      default: false
    },
    
    // Contenido
    text: {
      type: String,
      default: ''
    },
    
    icon: {
      type: String,
      default: ''
    },
    
    // Ancho completo
    fullWidth: {
      type: Boolean,
      default: false
    }
  },
  
  emits: ['click'],
  
  setup(props, { emit }) {
    // Clases CSS computadas
    const buttonClasses = computed(() => [
      'btn',
      `btn-${props.variant}`,
      `btn-${props.size}`,
      {
        'btn-full-width': props.fullWidth,
        'loading': props.loading,
        'disabled': props.disabled
      }
    ])

    // Manejar clic
    const handleClick = (event) => {
      if (!props.disabled && !props.loading) {
        emit('click', event)
      }
    }

    return {
      buttonClasses,
      handleClick
    }
  }
}
</script>

<style scoped>
/* Los estilos ya están en /assets/styles/components/buttons.css */
/* Solo agregamos estilos específicos del componente aquí */

.btn-loading-spinner {
  animation: spin 1s linear infinite;
}

.btn-content {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.btn-icon {
  font-size: 1.2em;
  flex-shrink: 0;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>