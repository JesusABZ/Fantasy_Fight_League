<template>
  <div class="password-field-wrapper">
    <!-- Label -->
    <label 
      v-if="label" 
      :for="inputId" 
      class="form-label"
      :class="{ 'form-label-large': size === 'large' }"
    >
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>

    <!-- Campo de contraseña -->
    <div class="password-field" :class="passwordFieldClasses">
      <input
        :id="inputId"
        :type="showPassword ? 'text' : 'password'"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :required="required"
        class="form-input"
        :class="inputClasses"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />
      
      <!-- Botón toggle -->
      <button
        type="button"
        class="password-toggle"
        @click="togglePassword"
        :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
      >
        <span v-if="showPassword">👁️</span>
        <span v-else>🙈</span>
      </button>

      <!-- Indicador de fuerza (opcional) -->
      <div v-if="showStrength && modelValue" class="password-strength-indicator">
        <div class="strength-bar">
          <div 
            class="strength-fill" 
            :class="strengthClass"
            :style="{ width: strengthPercentage + '%' }"
          ></div>
        </div>
        <span class="strength-text" :class="strengthClass">
          {{ strengthText }}
        </span>
      </div>
    </div>

    <!-- Mensaje de error -->
    <span 
      v-if="error" 
      class="error-message"
      :class="{ 'error-message-large': size === 'large' }"
    >
      {{ error }}
    </span>

    <!-- Texto de ayuda -->
    <p 
      v-if="help" 
      class="input-help"
      :class="{ 'input-help-large': size === 'large' }"
    >
      {{ help }}
    </p>

    <!-- Criterios de validación (opcional) -->
    <div v-if="showCriteria && modelValue" class="password-criteria">
      <h4 class="criteria-title">Criterios de seguridad:</h4>
      <ul class="criteria-list">
        <li :class="{ 'valid': criteria.length }">
          <span class="criteria-icon">{{ criteria.length ? '✓' : '○' }}</span>
          Al menos {{ minLength }} caracteres
        </li>
        <li :class="{ 'valid': criteria.hasUpper }">
          <span class="criteria-icon">{{ criteria.hasUpper ? '✓' : '○' }}</span>
          Al menos una mayúscula
        </li>
        <li :class="{ 'valid': criteria.hasLower }">
          <span class="criteria-icon">{{ criteria.hasLower ? '✓' : '○' }}</span>
          Al menos una minúscula
        </li>
        <li :class="{ 'valid': criteria.hasNumber }">
          <span class="criteria-icon">{{ criteria.hasNumber ? '✓' : '○' }}</span>
          Al menos un número
        </li>
        <li v-if="requireSpecial" :class="{ 'valid': criteria.hasSpecial }">
          <span class="criteria-icon">{{ criteria.hasSpecial ? '✓' : '○' }}</span>
          Al menos un carácter especial
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'PasswordField',
  
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    
    label: {
      type: String,
      default: 'Contraseña'
    },
    
    placeholder: {
      type: String,
      default: 'Ingresa tu contraseña'
    },
    
    error: {
      type: String,
      default: ''
    },
    
    help: {
      type: String,
      default: ''
    },
    
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['medium', 'large'].includes(value)
    },
    
    disabled: {
      type: Boolean,
      default: false
    },
    
    required: {
      type: Boolean,
      default: false
    },
    
    // Validación de fuerza
    showStrength: {
      type: Boolean,
      default: false
    },
    
    showCriteria: {
      type: Boolean,
      default: false
    },
    
    minLength: {
      type: Number,
      default: 6
    },
    
    requireSpecial: {
      type: Boolean,
      default: false
    },
    
    // ID personalizado
    id: {
      type: String,
      default: null
    }
  },
  
  emits: ['update:modelValue', 'blur', 'focus', 'strength-change'],
  
  setup(props, { emit }) {
    const showPassword = ref(false)
    
    // ID único
    const inputId = computed(() => {
      return props.id || `password-${Math.random().toString(36).substr(2, 9)}`
    })
    
    // Criterios de validación
    const criteria = computed(() => {
      const password = props.modelValue
      return {
        length: password.length >= props.minLength,
        hasUpper: /[A-Z]/.test(password),
        hasLower: /[a-z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(password)
      }
    })
    
    // Cálculo de fuerza
    const strengthScore = computed(() => {
      const checks = criteria.value
      let score = 0
      
      if (checks.length) score++
      if (checks.hasUpper) score++
      if (checks.hasLower) score++
      if (checks.hasNumber) score++
      if (props.requireSpecial && checks.hasSpecial) score++
      
      return score
    })
    
    const strengthData = computed(() => {
      const maxScore = props.requireSpecial ? 5 : 4
      const score = strengthScore.value
      
      if (score <= 1) {
        return { class: 'weak', text: 'Muy débil', percentage: 20 }
      } else if (score === 2) {
        return { class: 'weak', text: 'Débil', percentage: 40 }
      } else if (score === 3) {
        return { class: 'medium', text: 'Regular', percentage: 60 }
      } else if (score === maxScore - 1) {
        return { class: 'strong', text: 'Fuerte', percentage: 80 }
      } else {
        return { class: 'very-strong', text: 'Muy fuerte', percentage: 100 }
      }
    })
    
    const strengthClass = computed(() => strengthData.value.class)
    const strengthText = computed(() => strengthData.value.text)
    const strengthPercentage = computed(() => strengthData.value.percentage)
    
    // Classes CSS
    const passwordFieldClasses = computed(() => [
      {
        'has-error': props.error,
        'password-field-large': props.size === 'large'
      }
    ])
    
    const inputClasses = computed(() => [
      {
        'form-input-large': props.size === 'large',
        'error': props.error
      }
    ])
    
    // Métodos
    const togglePassword = () => {
      showPassword.value = !showPassword.value
    }
    
    const handleInput = (event) => {
      const value = event.target.value
      emit('update:modelValue', value)
      
      if (props.showStrength) {
        emit('strength-change', {
          score: strengthScore.value,
          strength: strengthData.value.class,
          isValid: strengthScore.value >= (props.requireSpecial ? 4 : 3)
        })
      }
    }
    
    const handleBlur = (event) => {
      emit('blur', event)
    }
    
    const handleFocus = (event) => {
      emit('focus', event)
    }
    
    return {
      showPassword,
      inputId,
      criteria,
      strengthClass,
      strengthText,
      strengthPercentage,
      passwordFieldClasses,
      inputClasses,
      togglePassword,
      handleInput,
      handleBlur,
      handleFocus
    }
  }
}
</script>

<style scoped>
.password-field-wrapper {
  width: 100%;
}

.password-field {
  position: relative;
}

.password-field.has-error .form-input {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.password-toggle {
  position: absolute;
  right: var(--space-lg);
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--gray-light);
  cursor: pointer;
  font-size: 1.3rem;
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

.required-mark {
  color: var(--error);
  margin-left: var(--space-xs);
}

/* === INDICADOR DE FUERZA === */
.password-strength-indicator {
  margin-top: var(--space-md);
}

.strength-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--space-xs);
}

.strength-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
}

.strength-fill.weak {
  background: var(--error);
}

.strength-fill.medium {
  background: var(--warning);
}

.strength-fill.strong {
  background: var(--info);
}

.strength-fill.very-strong {
  background: var(--success);
}

.strength-text {
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.strength-text.weak {
  color: var(--error);
}

.strength-text.medium {
  color: var(--warning);
}

.strength-text.strong {
  color: var(--info);
}

.strength-text.very-strong {
  color: var(--success);
}

/* === CRITERIOS === */
.password-criteria {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  margin-top: var(--space-lg);
}

.criteria-title {
  color: var(--white);
  font-size: 1rem;
  margin-bottom: var(--space-md);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.criteria-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.criteria-list li {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  color: var(--gray-light);
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.criteria-list li.valid {
  color: var(--success);
}

.criteria-icon {
  font-weight: bold;
  width: 16px;
  text-align: center;
}

/* === RESPONSIVE === */
@media (max-width: 480px) {
  .password-toggle {
    right: var(--space-md);
    font-size: 1.2rem;
  }
}
</style>