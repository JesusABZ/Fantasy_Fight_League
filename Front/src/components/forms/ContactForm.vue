<template>
  <form @submit.prevent="handleSubmit" class="contact-form">
    <!-- Email -->
    <BaseInput
      v-model="formData.email"
      type="email"
      label="Correo Electrónico"
      placeholder="tu.email@ejemplo.com"
      :error="errors.email"
      :size="size"
      required
      @blur="validateField('email')"
      @input="clearFieldError('email')"
    />

    <!-- Asunto -->
    <BaseInput
      v-model="formData.subject"
      label="Asunto"
      placeholder="Describe brevemente tu problema"
      :error="errors.subject"
      :size="size"
      :maxlength="200"
      required
      @blur="validateField('subject')"
      @input="clearFieldError('subject')"
    />

    <!-- Mensaje -->
    <div class="form-group">
      <label for="message" class="form-label" :class="{ 'form-label-large': size === 'large' }">
        Mensaje
        <span class="required-mark">*</span>
      </label>
      <textarea
        id="message"
        v-model="formData.message"
        class="form-textarea"
        :class="{ 'form-textarea-large': size === 'large', 'error': errors.message }"
        placeholder="Describe tu problema con el mayor detalle posible..."
        rows="6"
        maxlength="2000"
        required
        @blur="validateField('message')"
        @input="clearFieldError('message')"
      ></textarea>
      <span v-if="errors.message" class="error-message" :class="{ 'error-message-large': size === 'large' }">
        {{ errors.message }}
      </span>
      <p class="input-help" :class="{ 'input-help-large': size === 'large' }">
        {{ formData.message.length }}/2000 caracteres
      </p>
    </div>

    <!-- Tipo de consulta (opcional) -->
    <div v-if="showCategories" class="form-group">
      <label for="category" class="form-label" :class="{ 'form-label-large': size === 'large' }">
        Categoría
      </label>
      <select
        id="category"
        v-model="formData.category"
        class="form-select"
        :class="{ 'form-select-large': size === 'large' }"
      >
        <option value="">Selecciona una categoría</option>
        <option value="technical">Problema técnico</option>
        <option value="account">Problemas de cuenta</option>
        <option value="payment">Problemas de pago</option>
        <option value="feature">Solicitud de función</option>
        <option value="bug">Reporte de error</option>
        <option value="other">Otro</option>
      </select>
    </div>

    <!-- Prioridad (opcional) -->
    <div v-if="showPriority" class="form-group">
      <label class="form-label" :class="{ 'form-label-large': size === 'large' }">
        Prioridad
      </label>
      <div class="radio-group">
        <label class="radio-label">
          <input
            v-model="formData.priority"
            type="radio"
            value="low"
            class="radio-input"
          />
          <span class="radio-custom"></span>
          <span class="radio-text">Baja</span>
        </label>
        <label class="radio-label">
          <input
            v-model="formData.priority"
            type="radio"
            value="medium"
            class="radio-input"
          />
          <span class="radio-custom"></span>
          <span class="radio-text">Media</span>
        </label>
        <label class="radio-label">
          <input
            v-model="formData.priority"
            type="radio"
            value="high"
            class="radio-input"
          />
          <span class="radio-custom"></span>
          <span class="radio-text">Alta</span>
        </label>
      </div>
    </div>

    <!-- Mensaje de error general -->
    <div v-if="generalError" class="error-banner" :class="{ 'error-banner-large': size === 'large' }">
      {{ generalError }}
    </div>

    <!-- Mensaje de éxito -->
    <div v-if="successMessage" class="success-banner" :class="{ 'success-banner-large': size === 'large' }">
      {{ successMessage }}
    </div>

    <!-- Botón de envío -->
    <div class="form-actions" :class="{ 'form-actions-large': size === 'large' }">
      <BaseButton
        type="submit"
        variant="primary"
        :size="size"
        :loading="isSubmitting"
        :disabled="!canSubmit"
        full-width
      >
        <template v-if="isSubmitting">
          Enviando...
        </template>
        <template v-else>
          📩 Enviar Mensaje
        </template>
      </BaseButton>
    </div>

    <!-- Información adicional -->
    <div class="form-info">
      <div class="info-card">
        <div class="info-icon">ℹ️</div>
        <div class="info-text">
          <h4>Tiempo de respuesta</h4>
          <p>Normalmente respondemos en menos de 24 horas</p>
        </div>
      </div>
    </div>
  </form>
</template>

<script>
import { ref, reactive } from 'vue'
import { useForm } from '@/composables/useForm'
import { useNotifications } from '@/composables/useNotifications'

export default {
  name: 'ContactForm',
  
  props: {
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['medium', 'large'].includes(value)
    },
    
    showCategories: {
      type: Boolean,
      default: true
    },
    
    showPriority: {
      type: Boolean,
      default: false
    },
    
    defaultEmail: {
      type: String,
      default: ''
    },
    
    defaultSubject: {
      type: String,
      default: ''
    },
    
    showNotifications: {
      type: Boolean,
      default: true
    }
  },
  
  emits: ['success', 'error'],
  
  setup(props, { emit }) {
    const { showNotification } = useNotifications()
    const successMessage = ref('')
    
    // Formulario con validación
    const {
      formData,
      errors,
      isSubmitting,
      generalError,
      canSubmit,
      validateField,
      clearFieldError,
      submitForm
    } = useForm(
      {
        email: props.defaultEmail,
        subject: props.defaultSubject,
        message: '',
        category: '',
        priority: 'medium'
      },
      {
        validationRules: {
          email: [
            (value) => value.trim() ? null : 'El email es obligatorio',
            (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? null : 'El formato del email no es válido'
          ],
          subject: [
            (value) => value.trim() ? null : 'El asunto es obligatorio',
            (value) => value.length <= 200 ? null : 'El asunto no puede tener más de 200 caracteres'
          ],
          message: [
            (value) => value.trim() ? null : 'El mensaje es obligatorio',
            (value) => value.length <= 2000 ? null : 'El mensaje no puede tener más de 2000 caracteres',
            (value) => value.length >= 10 ? null : 'El mensaje debe tener al menos 10 caracteres'
          ]
        },
        showNotifications: false // Manejamos las notificaciones manualmente
      }
    )

    // Manejar envío del formulario
    const handleSubmit = async () => {
      const result = await submitForm(async (data) => {
        try {
          // TODO: Conectar con el backend
          console.log('Enviando mensaje de soporte:', data)
          
          // Simular llamada al API
          await new Promise(resolve => setTimeout(resolve, 2000))
          
          successMessage.value = '¡Mensaje enviado correctamente! Te responderemos pronto.'
          
          if (props.showNotifications) {
            showNotification('¡Mensaje enviado correctamente!', 'success')
          }
          
          emit('success', data)
          
          // Limpiar formulario después del éxito
          setTimeout(() => {
            formData.subject = ''
            formData.message = ''
            formData.category = ''
            formData.priority = 'medium'
            successMessage.value = ''
          }, 3000)
          
          return { success: true }
        } catch (error) {
          if (props.showNotifications) {
            showNotification('Error al enviar el mensaje', 'error')
          }
          emit('error', error)
          throw error
        }
      })
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      canSubmit,
      validateField,
      clearFieldError,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.contact-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  width: 100%;
}

.form-group {
  margin-bottom: var(--space-lg);
}

.form-label {
  display: block;
  font-weight: 600;
  color: var(--white);
  margin-bottom: var(--space-sm);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-label-large {
  font-size: 1rem;
  margin-bottom: var(--space-md);
}

.required-mark {
  color: var(--error);
  margin-left: var(--space-xs);
}

.form-textarea {
  width: 100%;
  padding: var(--space-lg);
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1.1rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  font-family: inherit;
  resize: vertical;
  min-height: 150px;
  line-height: 1.6;
}

.form-textarea::placeholder {
  color: var(--gray-light);
}

.form-textarea:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-textarea.error {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.form-textarea-large {
  padding: var(--space-xl);
  font-size: 1.2rem;
}

.form-select {
  width: 100%;
  padding: var(--space-lg);
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1.1rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-select option {
  background: var(--dark);
  color: var(--white);
}

.form-select-large {
  padding: var(--space-xl);
  font-size: 1.2rem;
}

/* === RADIO BUTTONS === */
.radio-group {
  display: flex;
  gap: var(--space-lg);
  flex-wrap: wrap;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
}

.radio-input {
  display: none;
}

.radio-custom {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.radio-input:checked + .radio-custom {
  background: var(--gradient-primary);
  border-color: var(--primary);
}

.radio-input:checked + .radio-custom::after {
  content: '';
  width: 8px;
  height: 8px;
  background: var(--white);
  border-radius: 50%;
}

.radio-text {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.input-help {
  color: var(--gray-light);
  font-size: 0.85rem;
  margin-top: var(--space-sm);
  text-align: right;
  font-style: italic;
}

.input-help-large {
  font-size: 1rem;
}

.error-message {
  display: block;
  color: var(--error);
  font-size: 0.8rem;
  margin-top: var(--space-xs);
  font-weight: 500;
}

.error-message-large {
  font-size: 0.9rem;
  margin-top: var(--space-sm);
}

.form-actions {
  margin-bottom: var(--space-lg);
}

.form-actions-large {
  margin-bottom: var(--space-xl);
}

/* === INFORMACIÓN ADICIONAL === */
.form-info {
  margin-top: var(--space-lg);
}

.info-card {
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.info-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.info-text h4 {
  color: var(--white);
  font-size: 1rem;
  margin-bottom: var(--space-xs);
  font-weight: 600;
}

.info-text p {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin: 0;
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .radio-group {
    flex-direction: column;
    gap: var(--space-md);
  }
  
  .info-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>