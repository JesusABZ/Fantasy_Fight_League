<template>
  <div class="support">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="support-container">
      <!-- Formulario de contacto -->
      <div class="support-form-container">
        <form @submit.prevent="handleSubmit" class="support-form">
          <!-- Icono y título -->
          <div class="form-header">
            <div class="support-icon">
              <span class="icon">📧</span>
            </div>
            <h2 class="form-title">Contactar Soporte</h2>
            <p class="form-subtitle">
              ¿Tienes algún problema o pregunta? Escríbenos y te ayudaremos
            </p>
          </div>

          <!-- Campo de email -->
          <div class="form-group">
            <label for="email" class="form-label">Correo Electrónico</label>
            <input
              id="email"
              v-model="formData.email"
              type="email"
              class="form-input"
              :class="{ 'error': errors.email }"
              placeholder="tu.email@ejemplo.com"
              @blur="validateField('email')"
              @input="clearFieldError('email')"
              required
            />
            <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
          </div>

          <!-- Campo de categoría (opcional) -->
          <div class="form-group">
            <label for="category" class="form-label">Categoría (Opcional)</label>
            <select
              id="category"
              v-model="formData.category"
              class="form-input form-select"
            >
              <option value="">Selecciona una categoría</option>
              <option value="BUG">Reportar un Error</option>
              <option value="ACCOUNT_ISSUE">Problema con la Cuenta</option>
              <option value="SCORING_ISSUE">Problema con Puntuaciones</option>
              <option value="LEAGUE_ISSUE">Problema con Ligas</option>
              <option value="FEATURE_REQUEST">Sugerencia de Mejora</option>
              <option value="PAYMENT_ISSUE">Problema de Pago</option>
              <option value="GENERAL">Consulta General</option>
              <option value="OTHER">Otro</option>
            </select>
          </div>

          <!-- Campo de asunto -->
          <div class="form-group">
            <label for="subject" class="form-label">Asunto</label>
            <input
              id="subject"
              v-model="formData.subject"
              type="text"
              class="form-input"
              :class="{ 'error': errors.subject }"
              placeholder="Describe brevemente tu problema"
              maxlength="200"
              @blur="validateField('subject')"
              @input="clearFieldError('subject')"
              required
            />
            <span v-if="errors.subject" class="error-message">{{ errors.subject }}</span>
          </div>

          <!-- Campo de mensaje -->
          <div class="form-group">
            <label for="message" class="form-label">Mensaje</label>
            <textarea
              id="message"
              v-model="formData.message"
              class="form-textarea"
              :class="{ 'error': errors.message }"
              placeholder="Describe tu problema con el mayor detalle posible..."
              rows="6"
              maxlength="2000"
              @blur="validateField('message')"
              @input="clearFieldError('message')"
              required
            ></textarea>
            <span v-if="errors.message" class="error-message">{{ errors.message }}</span>
            <p class="input-help">{{ formData.message.length }}/2000 caracteres</p>
          </div>

          <!-- Mensaje de error general -->
          <div v-if="generalError" class="error-banner">
            {{ generalError }}
          </div>

          <!-- Mensaje de éxito -->
          <div v-if="successMessage" class="success-banner">
            {{ successMessage }}
          </div>

          <!-- Botones -->
          <div class="form-actions">
            <button
              type="submit"
              class="btn btn-primary btn-large"
              :disabled="isSubmitting || !isFormValid"
              :class="{ 'loading': isSubmitting, 'disabled': !isFormValid }"
            >
              <span v-if="isSubmitting">Enviando...</span>
              <span v-else>📩 Enviar Mensaje</span>
            </button>
          </div>

          <!-- Botón volver -->
          <div class="back-button-container">
            <button 
              type="button" 
              class="btn btn-back"
              @click="goBack"
            >
              ← Volver
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { supportService } from '../api/supportService.js'
import { useAuth } from '../composables/useAuth.js'

export default {
  name: 'SupportView',
  setup() {
    const router = useRouter()
    const { user, isAuthenticated } = useAuth()

    // Estado del formulario
    const formData = reactive({
      email: '',
      username: '',
      category: '',
      subject: '',
      message: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const successMessage = ref('')

    // Computed property para validar si el formulario está completo y válido
    const isFormValid = computed(() => {
      const requiredFields = ['email', 'subject', 'message']
      const hasRequiredFields = requiredFields.every(field => 
        formData[field] && formData[field].trim() !== ''
      )
      const hasNoErrors = Object.keys(errors).length === 0
      return hasRequiredFields && hasNoErrors
    })

    // Inicializar datos del usuario si está autenticado
    onMounted(() => {
      if (isAuthenticated.value && user.value) {
        formData.email = user.value.email || ''
        formData.username = user.value.username || ''
      }
    })

    // Limpiar error de un campo específico
    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
      // Limpiar mensajes generales
      generalError.value = ''
      successMessage.value = ''
    }

    // Validar un campo específico
    const validateField = (fieldName) => {
      clearFieldError(fieldName)

      switch (fieldName) {
        case 'email':
          if (!formData.email.trim()) {
            errors.email = 'El email es obligatorio'
          } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            errors.email = 'El formato del email no es válido'
          }
          break

        case 'subject':
          if (!formData.subject.trim()) {
            errors.subject = 'El asunto es obligatorio'
          } else if (formData.subject.length > 200) {
            errors.subject = 'El asunto no puede tener más de 200 caracteres'
          }
          break

        case 'message':
          if (!formData.message.trim()) {
            errors.message = 'El mensaje es obligatorio'
          } else if (formData.message.length > 2000) {
            errors.message = 'El mensaje no puede tener más de 2000 caracteres'
          }
          break
      }
    }

    // Validaciones (para el submit final)
    const validateForm = () => {
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('email')
      validateField('subject')
      validateField('message')
      return Object.keys(errors).length === 0
    }

    // Manejar envío del formulario
    const handleSubmit = async () => {
      generalError.value = ''
      successMessage.value = ''

      if (!validateForm()) {
        return
      }

      isSubmitting.value = true

      try {
        // Preparar datos para el backend
        const ticketData = {
          email: formData.email.trim(),
          username: formData.username.trim() || null,
          category: formData.category || 'GENERAL',
          subject: formData.subject.trim(),
          message: formData.message.trim()
        }

        console.log('Enviando ticket de soporte:', ticketData)
        
        // 🔥 LLAMADA REAL AL BACKEND
        const response = await supportService.createSupportTicket(ticketData)
        
        console.log('Respuesta del servidor:', response)
        
        // Mostrar mensaje de éxito
        successMessage.value = `¡Ticket enviado correctamente! Referencia: #${response.ticketReference || 'N/A'}`
        
        // Limpiar formulario
        Object.keys(formData).forEach(key => {
          if (key !== 'email' && key !== 'username') {
            formData[key] = ''
          }
        })
        
      } catch (error) {
        console.error('Error al enviar ticket:', error)
        generalError.value = error.message || 'Error al enviar el mensaje. Inténtalo de nuevo más tarde.'
      } finally {
        isSubmitting.value = false
      }
    }

    // Función inteligente para volver a la página anterior
    const goBack = () => {
      if (window.history.length > 1) {
        router.go(-1)
      } else {
        router.push('/')
      }
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      isFormValid,
      handleSubmit,
      validateField,
      clearFieldError,
      goBack
    }
  }
}
</script>

<style scoped>
.support {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* === FONDO === */
.background-static {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/images/carrusel3.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: brightness(0.3) contrast(0.8) grayscale(0.2);
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(10, 10, 10, 0.95) 0%,
    rgba(26, 26, 26, 0.85) 30%,
    rgba(42, 42, 42, 0.75) 50%,
    rgba(26, 26, 26, 0.85) 70%,
    rgba(10, 10, 10, 0.95) 100%
  );
  backdrop-filter: blur(1px);
}

/* === CONTENEDOR PRINCIPAL === */
.support-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: var(--space-lg);
}

/* === FORMULARIO === */
.support-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.support-form {
  width: 100%;
}

/* === HEADER DEL FORMULARIO === */
.form-header {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.support-icon {
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
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
  line-height: 1.5;
}

/* === CAMPOS DEL FORMULARIO === */
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

.form-input,
.form-textarea,
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
  font-family: inherit;
}

.form-select {
  cursor: pointer;
}

.form-select option {
  background: var(--dark);
  color: var(--white);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--gray-light);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-input.error,
.form-textarea.error {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 150px;
  line-height: 1.6;
}

.input-help {
  color: var(--gray-light);
  font-size: 0.85rem;
  margin-top: var(--space-sm);
  text-align: right;
  font-style: italic;
}

/* === MENSAJES === */
.error-message {
  display: block;
  color: var(--error);
  font-size: 0.8rem;
  margin-top: var(--space-xs);
  font-weight: 500;
}

.error-banner {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid var(--error);
  color: var(--error);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-lg);
  text-align: center;
}

.success-banner {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid var(--success);
  color: var(--success);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-lg);
  text-align: center;
}

/* === BOTONES === */
.form-actions {
  margin-bottom: var(--space-lg);
}

.btn-large {
  width: 100%;
  padding: var(--space-lg);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-height: 56px;
}

.btn.loading {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none !important;
}

.btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
  background: var(--gray) !important;
  border-color: var(--gray) !important;
}

.btn.disabled:hover {
  transform: none !important;
  box-shadow: var(--shadow-md) !important;
}

/* === BOTÓN VOLVER === */
.back-button-container {
  text-align: center;
}

.btn-back {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--space-sm) var(--space-lg);
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-1px);
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .support-container {
    padding: var(--space-md);
  }

  .support-form-container {
    padding: var(--space-xl);
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

@media (max-width: 480px) {
  .support-form-container {
    padding: var(--space-lg);
  }
}
</style>