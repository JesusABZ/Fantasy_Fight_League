<template>
  <div :class="cardClasses" @click="handleClick">
    <!-- Header -->
    <div v-if="hasHeader" class="card-header">
      <div class="card-header-content">
        <h3 v-if="title" class="card-title">{{ title }}</h3>
        <p v-if="subtitle" class="card-subtitle">{{ subtitle }}</p>
        <slot name="header" />
      </div>
      <div v-if="$slots.actions || badge" class="card-header-actions">
        <span v-if="badge" :class="badgeClasses">{{ badge }}</span>
        <slot name="actions" />
      </div>
    </div>

    <!-- Content -->
    <div v-if="$slots.default" class="card-content">
      <slot />
    </div>

    <!-- Footer -->
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer" />
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="card-loading">
      <span class="loading-spinner"></span>
    </div>

    <!-- Empty state -->
    <div v-if="empty && !loading" class="card-empty">
      <div class="card-empty-icon">{{ emptyIcon }}</div>
      <h4 class="card-empty-title">{{ emptyTitle }}</h4>
      <p class="card-empty-description">{{ emptyDescription }}</p>
      <slot name="empty-actions" />
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'BaseCard',
  
  props: {
    // Variante de la tarjeta
    variant: {
      type: String,
      default: 'default',
      validator: (value) => [
        'default', 'primary', 'section', 'profile', 'featured', 
        'league', 'fighter', 'event'
      ].includes(value)
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
    
    badge: {
      type: String,
      default: ''
    },
    
    badgeType: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'success', 'warning', 'error'].includes(value)
    },
    
    // Estados
    loading: {
      type: Boolean,
      default: false
    },
    
    empty: {
      type: Boolean,
      default: false
    },
    
    disabled: {
      type: Boolean,
      default: false
    },
    
    clickable: {
      type: Boolean,
      default: false
    },
    
    // Estado vacío
    emptyIcon: {
      type: String,
      default: '📦'
    },
    
    emptyTitle: {
      type: String,
      default: 'Sin contenido'
    },
    
    emptyDescription: {
      type: String,
      default: 'No hay elementos para mostrar'
    },
    
    // Tamaño
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    }
  },
  
  emits: ['click'],
  
  setup(props, { emit, slots }) {
    // Computed properties
    const hasHeader = computed(() => {
      return props.title || props.subtitle || slots.header || slots.actions || props.badge
    })
    
    const cardClasses = computed(() => [
      'card',
      {
        [`card-${props.variant}`]: props.variant !== 'default',
        'card-clickable': props.clickable,
        'card-disabled': props.disabled,
        'card-loading': props.loading,
        'card-fade-in': !props.loading
      }
    ])
    
    const badgeClasses = computed(() => [
      'card-badge', 
      {
        [`card-badge-${props.badgeType}`]: props.badgeType !== 'default'
      }
    ])
    
    // Métodos
    const handleClick = (event) => {
      if (props.clickable && !props.disabled && !props.loading) {
        emit('click', event)
      }
    }
    
    return {
      hasHeader,
      cardClasses,
      badgeClasses,
      handleClick
    }
  }
}
</script>

<style scoped>
/* Los estilos principales están en /assets/styles/components/cards.css */

.card-clickable {
  cursor: pointer;
}

.card-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-header-content {
  flex: 1;
}

.card-header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255, 107, 53, 0.3);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>