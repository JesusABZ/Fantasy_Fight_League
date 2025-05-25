<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="buttonClasses"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner">⏳</span>
    <span v-else-if="icon" class="button-icon">{{ icon }}</span>
    
    <span class="button-text">
      <slot>{{ text }}</slot>
    </span>
  </button>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'BaseButton',
  props: {
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'secondary', 'back', 'event', 'join'].includes(value)
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    type: {
      type: String,
      default: 'button'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    },
    icon: {
      type: String,
      default: ''
    },
    text: {
      type: String,
      default: ''
    },
    fullWidth: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  setup(props, { emit }) {
    const buttonClasses = computed(() => [
      'base-button',
      `base-button--${props.variant}`,
      `base-button--${props.size}`,
      {
        'base-button--disabled': props.disabled,
        'base-button--loading': props.loading,
        'base-button--full-width': props.fullWidth
      }
    ])

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
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  gap: var(--space-sm);
  min-height: 44px;
}

.base-button:focus {
  outline: 2px solid var(--primary);
  outline-offset: 2px;
}

/* === VARIANTS === */
.base-button--primary {
  background: var(--gradient-primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

.base-button--primary:hover:not(.base-button--disabled):not(.base-button--loading) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.base-button--secondary {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.base-button--secondary:hover:not(.base-button--disabled):not(.base-button--loading) {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

.base-button--back {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.base-button--back:hover:not(.base-button--disabled):not(.base-button--loading) {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-1px);
}

.base-button--event {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.base-button--event:hover:not(.base-button--disabled):not(.base-button--loading) {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

.base-button--join {
  background: var(--gradient-primary);
  color: var(--white);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.base-button--join:hover:not(.base-button--disabled):not(.base-button--loading) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* === SIZES === */
.base-button--small {
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
  min-height: 36px;
}

.base-button--medium {
  padding: var(--space-sm) var(--space-lg);
  font-size: 1rem;
  min-height: 44px;
}

.base-button--large {
  padding: var(--space-lg);
  font-size: 1.2rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-height: 56px;
}

/* === STATES === */
.base-button--disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

.base-button--loading {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none !important;
}

.base-button--full-width {
  width: 100%;
}

/* === CONTENT === */
.loading-spinner {
  animation: spin 1s linear infinite;
}

.button-icon {
  font-size: 1.2em;
}

.button-text {
  flex: 1;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .base-button--large {
    font-size: 1.1rem;
    padding: var(--space-md) var(--space-lg);
  }
}
</style>