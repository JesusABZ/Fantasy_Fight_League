<template>
  <div class="forgot-password">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="forgot-container">
      <!-- Formulario de recuperación -->
      <div class="forgot-form-container">
        <form @submit.prevent="handleSubmit" class="forgot-form">
          <!-- Icono de email -->
          <div class="email-icon-container">
            <div class="email-icon">
              📧
            </div>
          </div>

          <!-- Título del formulario -->
          <h2 class="form-title">¿Olvidaste tu contraseña?</h2>
          <p class="form-subtitle">
            No te preocupes, te enviaremos instrucciones para recuperarla
          </p>
          
          <!-- Campo de email -->
          <div class="form-group">
            <label for="email" class="form-label">Correo Electrónico</label>
            <input
              id="email"
              v-model="formData.email"
              type="email"
              class="form-input"
              :class="{ 'error': errors.email }"
              placeholder="Ingresa tu email registrado"
              @blur="validateField('email')"
              @input="clearFieldError('email')"
              required
            />
            <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
            <p class="input-help">
              Ingresa el email que usaste para registrarte en Fantasy Fight League
            </p>
          </div>

          <!-- Mensaje de error general -->
          <div v-if="generalError" class="error-banner">
            {{ generalError }}
          </div>

          <!-- Mensaje de éxito -->
          <div v-if="successMessage" class="success-banner">
            <div class="success-content">
              <div class="success-icon">✅</div>
              <div class="success-text">
                <h4>¡Email enviado!</h4>
                <p>{{ successMessage }}</p>
              </div>
            </div>
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
              <span v-else>Enviar Instrucciones</span>
            </button>
          </div>

          <!-- Instrucciones adicionales (mostrar después del envío) -->
          <div v-if="showInstructions" class="instructions-container">
            <h3 class="instructions-title">¿Qué hacer ahora?</h3>
            <div class="steps">
              <div class="step">
                <span class="step-number">1</span>
                <span class="step-text">Revisa tu bandeja de entrada y carpeta de spam</span>
              </div>
              <div class="step">
                <span class="step-number">2</span>
                <span class="step-text">Haz clic en el enlace de recuperación</span>
              </div>
              <div class="step">
                <span class="step-number">3</span>
                <span class="step-text">Crea tu nueva contraseña</span>
              </div>
            </div>
          </div>

          <!-- Opciones adicionales -->
          <div class="additional-options">
            <div class="option-group">
              <p class="help-text">
                ¿No recibiste el email? 
                <button 
                  type="button"
                  class="link-button" 
                  @click="resendEmail"
                  :disabled="isResending || !emailSent"
                >
                  <span v-if="isResending">Reenviando...</span>
                  <span v-else>Reenviar</span>
                </button>
              </p>
            </div>

            <div class="option-group">
              <p class="help-text">
                ¿Recordaste tu contraseña? 
                <button 
                  type="button"
                  class="link-button"
                  @click="goToLogin"
                >
                  Volver al login
                </button>
              </p>
            </div>

            <div class="option-group">
              <p class="help-text">
                ¿No tienes cuenta? 
                <button 
                  type="button"
                  class="link-button"
                  @click="goToRegister"
                >
                  Registrarse
                </button>
              </p>
            </div>
          </div>

          <!-- Botón volver -->
          <div class="back-button-container">
            <button 
              type="button" 
              class="btn btn-back"
              @click="goBack"
            >
              ← Volver a la Home
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ForgotPasswordView',
  setup() {
    const router = useRouter()

    // Estado del formulario
    const formData = reactive({
      email: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const isResending = ref(false)
    const generalError = ref('')
    const successMessage = ref('')
    const emailSent = ref(false)
    const showInstructions = ref(false)

    // Computed property para validar si el formulario está completo y válido
    const isFormValid = computed(() => {
      return formData.email.trim() !== '' && Object.keys(errors).length === 0
    })

    // Limpiar error de un campo específico
    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
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
      }
    }

    // Validaciones (para el submit final)
    const validateForm = () => {
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('email')
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
        // TODO: Aquí conectaremos con el backend
        console.log('Solicitud de recuperación para:', formData.email)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        emailSent.value = true
        showInstructions.value = true
        successMessage.value = `Hemos enviado las instrucciones de recuperación a ${formData.email}`
        
      } catch (error) {
        generalError.value = error.message || 'Error al enviar el email de recuperación. Inténtalo de nuevo.'
      } finally {
        isSubmitting.value = false
      }
    }

    // Reenviar email
    const resendEmail = async () => {
      if (!emailSent.value) return

      isResending.value = true
      generalError.value = ''

      try {
        // TODO: Conectar con backend para reenviar
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        successMessage.value = `Email reenviado a ${formData.email}`
        
      } catch (error) {
        generalError.value = 'Error al reenviar el email. Inténtalo de nuevo.'
      } finally {
        isResending.value = false
      }
    }

    // Funciones de navegación
    const goBack = () => {
      router.push('/')
    }

    const goToLogin = () => {
      router.push('/login')
    }

    const goToRegister = () => {
      router.push('/register')
    }

    return {
      formData,
      errors,
      isSubmitting,
      isResending,
      generalError,
      successMessage,
      emailSent,
      showInstructions,
      isFormValid,
      handleSubmit,
      validateField,
      clearFieldError,
      resendEmail,
      goBack,
      goToLogin,
      goToRegister
    }
  }
}
</script>

<style scoped>
.forgot-password {
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
.forgot-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: var(--space-lg);
}

/* === FORMULARIO === */
.forgot-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.forgot-form {
  width: 100%;
}

/* === ICONO DE EMAIL === */
.email-icon-container {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.email-icon {
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

/* === TÍTULOS === */
.form-title {
  font-family: var(--font-impact);
  font-size: 2.2rem;
  font-weight: 400;
  color: var(--white);
  text-align: center;
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-subtitle {
  color: var(--gray-light);
  text-align: center;
  margin-bottom: var(--space-2xl);
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

.form-input {
  width: 100%;
  padding: var(--space-lg);
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1.1rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.form-input::placeholder {
  color: var(--gray-light);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-input.error {
  border-color: var(--error);
  background: rgba(239, 68, 68, 0.1);
}

.input-help {
  color: var(--gray-light);
  font-size: 0.85rem;
  margin-top: var(--space-sm);
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
  padding: var(--space-lg);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-lg);
}

.success-content {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  text-align: left;
}

.success-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.success-text h4 {
  margin: 0 0 var(--space-xs) 0;
  font-weight: 600;
}

.success-text p {
  margin: 0;
  opacity: 0.9;
}

/* === INSTRUCCIONES === */
.instructions-container {
  margin: var(--space-xl) 0;
  padding: var(--space-lg);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.instructions-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  text-align: center;
}

.steps {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.step {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-sm);
}

.step-number {
  width: 24px;
  height: 24px;
  background: var(--gradient-primary);
  color: var(--white);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.8rem;
  flex-shrink: 0;
}

.step-text {
  color: var(--gray-light);
  font-size: 0.9rem;
}

/* === BOTONES === */
.form-actions {
  margin-bottom: var(--space-xl);
}

.btn-large {
  width: 100%;
  padding: var(--space-lg);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
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

/* === OPCIONES ADICIONALES === */
.additional-options {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: var(--space-lg);
  margin-bottom: var(--space-lg);
}

.option-group {
  margin-bottom: var(--space-md);
  text-align: center;
}

.option-group:last-child {
  margin-bottom: 0;
}

.help-text {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.link-button {
  background: none;
  border: none;
  color: var(--primary);
  text-decoration: underline;
  cursor: pointer;
  font-size: inherit;
  font-weight: 600;
  transition: color 0.3s ease;
}

.link-button:hover:not(:disabled) {
  color: var(--primary-light);
}

.link-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  color: var(--gray);
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
  .forgot-container {
    padding: var(--space-md);
  }

  .forgot-form-container {
    padding: var(--space-xl);
  }

  .form-title {
    font-size: 2rem;
  }

  .email-icon {
    width: 70px;
    height: 70px;
    font-size: 2rem;
  }

  .success-content {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .forgot-form-container {
    padding: var(--space-lg);
  }
}
</style>