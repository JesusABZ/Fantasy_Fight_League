<template>
  <div class="base-input-wrapper">
    <!-- Label -->
    <label 
      v-if="label" 
      :for="inputId" 
      :class="labelClasses"
    >
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>

    <!-- Input wrapper para campos especiales -->
    <div :class="inputWrapperClasses">
      <!-- Input principal -->
      <component
        :is="inputComponent"
        :id="inputId"
        :type="computedType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :maxlength="maxlength"
        :rows="rows"
        :class="inputClasses"
        v-bind="$attrs"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />

      <!-- Botón para mostrar/ocultar contraseña -->
      <button
        v-if="type === 'password'"
        type="button"
        class="password-toggle"
        @click="togglePasswordVisibility"
        :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
      >
        <span v-if="showPassword">👁️</span>
        <span v-else>🙈</span>
      </button>

      <!-- Indicador de validación -->
      <div v-if="showValidationIcon" class="validation-icon">
        <span v-if="validationState === 'valid'" class="icon-valid">✓</span>
        <span v-else-if="validationState === 'invalid'" class="icon-invalid">✕</span>
        <span v-else-if="validationState === 'loading'" class="icon-loading">⏳</span>
      </div>
    </div>

    <!-- Mensaje de error -->
    <span 
      v-if="error" 
      :class="errorClasses"
    >
      {{ error }}
    </span>

    <!-- Texto de ayuda -->
    <p 
      v-if="help" 
      :class="helpClasses"
    >
      {{ help }}
    </p>

    <!-- Contador de caracteres (para campos con maxlength) -->
    <div 
      v-if="showCharacterCount && maxlength" 
      class="character-count"
      :class="{ 'near-limit': isNearLimit }"
    >
      {{ currentLength }}/{{ maxlength }}
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useValidation } from '@/composables/useValidation'

export default {
  name: 'BaseInput',
  inheritAttrs: false,
  
  props: {
    // v-model
    modelValue: {
      type: [String, Number],
      default: ''
    },
    
    // Tipo de input
    type: {
      type: String,
      default: 'text',
      validator: (value) => [
        'text', 'email', 'password', 'number', 'tel', 'url', 'search', 'textarea'
      ].includes(value)
    },
    
    // Label y ayuda
    label: {
      type: String,
      default: ''
    },
    
    placeholder: {
      type: String,
      default: ''
    },
    
    help: {
      type: String,
      default: ''
    },
    
    // Estados
    error: {
      type: String,
      default: ''
    },
    
    disabled: {
      type: Boolean,
      default: false
    },
    
    readonly: {
      type: Boolean,
      default: false
    },
    
    required: {
      type: Boolean,
      default: false
    },
    
    // Tamaños
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    
    // Atributos específicos
    maxlength: {
      type: [String, Number],
      default: null
    },
    
    // Para textarea
    rows: {
      type: [String, Number],
      default: 4
    },
    
    // ID personalizado
    id: {
      type: String,
      default: null
    },
    
    // Validación automática
    validationRules: {
      type: Array,
      default: () => []
    },
    
    // Validación en tiempo real
    validateOnInput: {
      type: Boolean,
      default: false
    },
    
    validateOnBlur: {
      type: Boolean,
      default: true
    },
    
    // UI/UX
    showValidationIcon: {
      type: Boolean,
      default: false
    },
    
    showCharacterCount: {
      type: Boolean,
      default: false
    },
    
    // Esquemas de validación predefinidos
    validationSchema: {
      type: String,
      default: '',
      validator: (value) => [
        '', 'email', 'password', 'username', 'required', 'name'
      ].includes(value)
    }
  },
  
  emits: ['update:modelValue', 'blur', 'focus', 'input', 'validation'],
  
  setup(props, { emit }) {
    // Estado para mostrar/ocultar contraseña
    const showPassword = ref(false)
    const validationState = ref('') // '', 'valid', 'invalid', 'loading'
    
    // Composable de validación
    const { rules } = useValidation()
    
    // ID único para el input
    const inputId = computed(() => {
      return props.id || `input-${Math.random().toString(36).substr(2, 9)}`
    })
    
    // Componente a renderizar
    const inputComponent = computed(() => {
      return props.type === 'textarea' ? 'textarea' : 'input'
    })
    
    // Tipo computado (para manejar password toggle)
    const computedType = computed(() => {
      if (props.type === 'password') {
        return showPassword.value ? 'text' : 'password'
      }
      return props.type === 'textarea' ? undefined : props.type
    })
    
    // Reglas de validación computadas
    const computedValidationRules = computed(() => {
      if (props.validationRules.length > 0) {
        return props.validationRules
      }
      
      // Usar esquemas predefinidos
      const schemaRules = []
      
      switch (props.validationSchema) {
        case 'email':
          if (props.required) schemaRules.push((value) => rules.required(value, 'Este campo'))
          schemaRules.push(rules.email)
          break
        case 'password':
          schemaRules.push(rules.password)
          break
        case 'username':
          schemaRules.push(rules.username)
          break
        case 'required':
          schemaRules.push((value) => rules.required(value, props.label || 'Este campo'))
          break
        case 'name':
          schemaRules.push((value) => rules.required(value, props.label || 'Este campo'))
          schemaRules.push((value) => rules.minLength(value, 2, props.label || 'Este campo'))
          break
      }
      
      return schemaRules
    })
    
    // Longitud actual del texto
    const currentLength = computed(() => {
      return props.modelValue ? props.modelValue.toString().length : 0
    })
    
    // Está cerca del límite de caracteres
    const isNearLimit = computed(() => {
      if (!props.maxlength) return false
      return currentLength.value >= props.maxlength * 0.8
    })
    
    // Clases CSS computadas
    const labelClasses = computed(() => [
      'form-label',
      {
        'form-label-large': props.size === 'large',
        'form-label-required': props.required
      }
    ])
    
    const inputWrapperClasses = computed(() => [
      'input-wrapper',
      {
        'password-field': props.type === 'password',
        'has-validation-icon': props.showValidationIcon,
        'validation-valid': validationState.value === 'valid',
        'validation-invalid': validationState.value === 'invalid'
      }
    ])
    
    const inputClasses = computed(() => [
      props.type === 'textarea' ? 'form-textarea' : 'form-input',
      {
        'form-input-large': props.size === 'large',
        'form-input-small': props.size === 'small',
        'error': props.error,
        'has-validation': props.showValidationIcon
      }
    ])
    
    const errorClasses = computed(() => [
      'error-message',
      {
        'error-message-large': props.size === 'large'
      }
    ])
    
    const helpClasses = computed(() => [
      'input-help',
      {
        'input-help-large': props.size === 'large'
      }
    ])
    
    // Métodos de validación
    const validateValue = async (value) => {
      if (computedValidationRules.value.length === 0) return true
      
      validationState.value = 'loading'
      
      try {
        for (const rule of computedValidationRules.value) {
          const error = await rule(value)
          if (error) {
            validationState.value = 'invalid'
            emit('validation', { valid: false, error })
            return false
          }
        }
        
        validationState.value = 'valid'
        emit('validation', { valid: true, error: null })
        return true
      } catch (error) {
        validationState.value = 'invalid'
        emit('validation', { valid: false, error: error.message })
        return false
      }
    }
    
    // Métodos de manejo de eventos
    const handleInput = (event) => {
      const value = event.target.value
      emit('update:modelValue', value)
      emit('input', event)
      
      if (props.validateOnInput) {
        validateValue(value)
      }
    }
    
    const handleBlur = (event) => {
      emit('blur', event)
      
      if (props.validateOnBlur) {
        validateValue(event.target.value)
      }
    }
    
    const handleFocus = (event) => {
      emit('focus', event)
      
      // Limpiar estado de validación al enfocar
      if (validationState.value === 'invalid') {
        validationState.value = ''
      }
    }
    
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }
    
    return {
      showPassword,
      validationState,
      inputId,
      inputComponent,
      computedType,
      currentLength,
      isNearLimit,
      labelClasses,
      inputWrapperClasses,
      inputClasses,
      errorClasses,
      helpClasses,
      handleInput,
      handleBlur,
      handleFocus,
      togglePasswordVisibility,
      validateValue
    }
  }
}
</script>

<style scoped>
/* Los estilos principales están en /assets/styles/components/forms.css */

.base-input-wrapper {
  width: 100%;
  position: relative;
}

.required-mark {
  color: var(--error);
  margin-left: var(--space-xs);
}

.input-wrapper {
  position: relative;
}

.input-wrapper.has-validation-icon .form-input,
.input-wrapper.has-validation-icon .form-textarea {
  padding-right: 3rem;
}

.input-wrapper.password-field.has-validation-icon .form-input {
  padding-right: 5rem;
}

/* === ICONOS DE VALIDACIÓN === */
.validation-icon {
  position: absolute;
  right: var(--space-lg);
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}

.password-field .validation-icon {
  right: 3.5rem;
}

.icon-valid {
  color: var(--success);
  font-size: 1.2rem;
  font-weight: bold;
}

.icon-invalid {
  color: var(--error);
  font-size: 1.2rem;
  font-weight: bold;
}

.icon-loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* === ESTADOS DE VALIDACIÓN === */
.validation-valid .form-input,
.validation-valid .form-textarea {
  border-color: var(--success);
  background: rgba(16, 185, 129, 0.1);
}

.validation-invalid .form-input,
.validation-invalid .form-textarea {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

/* === CONTADOR DE CARACTERES === */
.character-count {
  text-align: right;
  font-size: 0.8rem;
  color: var(--gray-light);
  margin-top: var(--space-xs);
}

.character-count.near-limit {
  color: var(--warning);
}

/* === TAMAÑOS === */
.form-input-small {
  padding: var(--space-sm) var(--space-md);
  font-size: 0.9rem;
}

.form-label-required {
  position: relative;
}

.form-label-required::after {
  content: '';
  position: absolute;
  top: 0;
  right: -8px;
  width: 4px;
  height: 4px;
  background: var(--error);
  border-radius: 50%;
}

/* === ESTADOS ESPECIALES === */
.form-input:focus + .validation-icon,
.form-textarea:focus + .validation-icon {
  opacity: 0.7;
}

.form-input[readonly],
.form-textarea[readonly] {
  background: rgba(255, 255, 255, 0.05);
  cursor: not-allowed;
}

/* === RESPONSIVE === */
@media (max-width: 480px) {
  .input-wrapper.has-validation-icon .form-input,
  .input-wrapper.has-validation-icon .form-textarea {
    padding-right: 2.5rem;
  }
  
  .password-field .validation-icon {
    right: 3rem;
  }
}
</style>