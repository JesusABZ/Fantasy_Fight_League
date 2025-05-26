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
  </div>
</template>

<script>
import { computed, ref } from 'vue'

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
    }
  },
  
  emits: ['update:modelValue', 'blur', 'focus', 'input'],
  
  setup(props, { emit }) {
    // Estado para mostrar/ocultar contraseña
    const showPassword = ref(false)
    
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
    
    // Clases CSS computadas
    const labelClasses = computed(() => [
      'form-label',
      {
        'form-label-large': props.size === 'large'
      }
    ])
    
    const inputWrapperClasses = computed(() => [
      {
        'password-field': props.type === 'password'
      }
    ])
    
    const inputClasses = computed(() => [
      props.type === 'textarea' ? 'form-textarea' : 'form-input',
      {
        'form-input-large': props.size === 'large',
        'error': props.error
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
    
    // Métodos
    const handleInput = (event) => {
      const value = event.target.value
      emit('update:modelValue', value)
      emit('input', event)
    }
    
    const handleBlur = (event) => {
      emit('blur', event)
    }
    
    const handleFocus = (event) => {
      emit('focus', event)
    }
    
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }
    
    return {
      showPassword,
      inputId,
      inputComponent,
      computedType,
      labelClasses,
      inputWrapperClasses,
      inputClasses,
      errorClasses,
      helpClasses,
      handleInput,
      handleBlur,
      handleFocus,
      togglePasswordVisibility
    }
  }
}
</script>

<style scoped>
/* Los estilos principales están en /assets/styles/components/forms.css */

.base-input-wrapper {
  width: 100%;
}

.required-mark {
  color: var(--error);
  margin-left: var(--space-xs);
}
</style>