<template>
  <div class="change-email">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <button class="btn btn-back" @click="goBack">
            ← Volver
          </button>
          <h1 class="title-hero">CAMBIAR EMAIL</h1>
          <div class="header-spacer"></div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        
        <!-- Formulario de cambio de email -->
        <div class="form-container">
          <form @submit.prevent="handleSubmit" class="change-form">
            
            <!-- Información actual -->
            <div class="current-info">
              <h3 class="section-title">📧 Email Actual</h3>
              <div class="current-value">
                <span class="current-email">{{ currentUser.email }}</span>
                <span 
                  class="verification-badge" 
                  :class="currentUser.emailConfirmed ? 'verified' : 'unverified'"
                >
                  {{ currentUser.emailConfirmed ? '✅ Verificado' : '❌ No Verificado' }}
                </span>
              </div>
            </div>

            <!-- Separador -->
            <div class="section-divider"></div>

            <!-- Formulario de cambio -->
            <div class="change-section">
              <h3 class="section-title">🔐 Cambiar Email</h3>
              
              <!-- Contraseña actual -->
              <div class="form-group">
                <label for="currentPassword" class="form-label">Contraseña Actual</label>
                <div class="password-field">
                  <input
                    id="currentPassword"
                    v-model="formData.currentPassword"
                    :type="showCurrentPassword ? 'text' : 'password'"
                    class="form-input"
                    :class="{ 'error': errors.currentPassword }"
                    placeholder="Ingresa tu contraseña actual"
                    @blur="validateField('currentPassword')"
                    @input="clearFieldError('currentPassword')"
                    required
                  />
                  <button
                    type="button"
                    class="password-toggle"
                    @click="showCurrentPassword = !showCurrentPassword"
                  >
                    <span v-if="showCurrentPassword">👁️</span>
                    <span v-else">🙈</span>
                  </button>
                </div>
                <span v-if="errors.currentPassword" class="error-message">{{ errors.currentPassword }}</span>
                <p class="input-help">Necesaria para verificar tu identidad</p>
              </div>

              <!-- Nuevo email -->
              <div class="form-group">
                <label for="newEmail" class="form-label">Nuevo Email</label>
                <input
                  id="newEmail"
                  v-model="formData.newEmail"
                  type="email"
                  class="form-input"
                  :class="{ 'error': errors.newEmail }"
                  placeholder="tu.nuevo.email@ejemplo.com"
                  @blur="validateField('newEmail')"
                  @input="clearFieldError('newEmail')"
                  required
                />
                <span v-if="errors.newEmail" class="error-message">{{ errors.newEmail }}</span>
                <p class="input-help">Se enviará un email de verificación a la nueva dirección</p>
              </div>
            </div>

            <!-- Información importante -->
            <div class="warning-section">
              <div class="warning-box">
                <h4 class="warning-title">⚠️ Importante</h4>
                <ul class="warning-list">
                  <li>Se enviará un email de verificación a la nueva dirección</li>
                  <li>Tu sesión actual seguirá activa hasta que te desloguees</li>
                  <li>Una vez deslogueado no podras iniciar sesión hasta que verifiques tu nuevo email</li>
                  <li>Asegúrate de tener acceso a la nueva dirección de correo</li>
                </ul>
              </div>
            </div>

            <!-- Mensaje de error general -->
            <div v-if="generalError" class="error-banner">
              {{ generalError }}
            </div>

            <!-- Botón de envío -->
            <div class="form-actions">
              <button
                type="submit"
                class="btn btn-primary btn-large"
                :disabled="isSubmitting || !isFormValid"
                :class="{ 'loading': isSubmitting, 'disabled': !isFormValid }"
              >
                <span v-if="isSubmitting">Cambiando Email...</span>
                <span v-else">📧 Cambiar Email</span>
              </button>
            </div>

          </form>
        </div>

      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotification" class="notification" :class="notificationType" @click="hideNotification">
      <div class="notification-icon">
        <span v-if="notificationType === 'success'">✅</span>
        <span v-else">❌</span>
      </div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ChangeEmailView',
  setup() {
    const router = useRouter()

    // Estado del usuario actual
    const currentUser = reactive({
      email: 'usuario@yopmail.com',
      emailConfirmed: true
    })

    // Estado del formulario
    const formData = reactive({
      currentPassword: '',
      newEmail: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const showCurrentPassword = ref(false)
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Computed property para validar si el formulario está completo y válido
    const isFormValid = computed(() => {
      return formData.currentPassword.trim() !== '' &&
             formData.newEmail.trim() !== '' &&
             formData.newEmail !== currentUser.email &&
             Object.keys(errors).length === 0
    })

    // Funciones de validación
    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
    }

    const validateField = (fieldName) => {
      clearFieldError(fieldName)

      switch (fieldName) {
        case 'currentPassword':
          if (!formData.currentPassword.trim()) {
            errors.currentPassword = 'La contraseña actual es obligatoria'
          } else if (formData.currentPassword.length < 6) {
            errors.currentPassword = 'La contraseña debe tener al menos 6 caracteres'
          }
          break

        case 'newEmail':
          if (!formData.newEmail.trim()) {
            errors.newEmail = 'El nuevo email es obligatorio'
          } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.newEmail)) {
            errors.newEmail = 'El formato del email no es válido'
          } else if (formData.newEmail === currentUser.email) {
            errors.newEmail = 'El nuevo email debe ser diferente al actual'
          }
          break
      }
    }

    const validateForm = () => {
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('currentPassword')
      validateField('newEmail')
      return Object.keys(errors).length === 0
    }

    // Manejar envío del formulario
    const handleSubmit = async () => {
      generalError.value = ''

      if (!validateForm()) {
        return
      }

      isSubmitting.value = true

      try {
        // TODO: Conectar con el backend
        const changeData = {
          currentPassword: formData.currentPassword,
          newEmail: formData.newEmail
        }

        console.log('Cambiando email:', changeData)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        showFloatingNotification('success', '¡Email cambiado correctamente! Verifica tu nuevo email.')
        
        // Esperar un momento para mostrar la notificación
        setTimeout(() => {
          // Volver a la edición de perfil
          router.push('/profile/edit')
        }, 2000)
        
      } catch (error) {
        generalError.value = error.message || 'Error al cambiar el email. Verifica tu contraseña.'
      } finally {
        isSubmitting.value = false
      }
    }

    const goBack = () => {
      if (window.history.length > 1) {
        router.go(-1)
      } else {
        router.push('/profile/edit')
      }
    }

    const showFloatingNotification = (type, text) => {
      notificationType.value = type
      notificationText.value = text
      showNotification.value = true
      
      setTimeout(() => {
        hideNotification()
      }, 3000)
    }

    const hideNotification = () => {
      showNotification.value = false
    }

    return {
      currentUser,
      formData,
      errors,
      isSubmitting,
      generalError,
      isFormValid,
      showCurrentPassword,
      showNotification,
      notificationType,
      notificationText,
      handleSubmit,
      validateField,
      clearFieldError,
      goBack,
      hideNotification
    }
  }
}
</script>

<style scoped>
.change-email {
  position: relative;
  min-height: 100vh;
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
  filter: brightness(0.2) contrast(0.8) grayscale(0.3);
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

/* === HEADER === */
.page-header {
  position: relative;
  z-index: 1;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.container {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-spacer {
  width: 100px;
}

.title-hero {
  font-family: var(--font-impact);
  font-size: 2.5rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

/* === BOTONES === */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-decoration: none;
  gap: var(--space-sm);
}

.btn-back {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

/* === SECCIONES === */
.section-title {
  font-family: var(--font-impact);
  font-size: 1.4rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.section-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 107, 53, 0.3) 50%,
    transparent 100%
  );
  margin: var(--space-2xl) 0;
}

/* === INFORMACIÓN ACTUAL === */
.current-info {
  margin-bottom: var(--space-xl);
}

.current-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  flex-wrap: wrap;
  gap: var(--space-md);
}

.current-email {
  color: var(--white);
  font-size: 1.1rem;
  font-weight: 600;
}

.verification-badge {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-full);
  font-size: 0.9rem;
  font-weight: 600;
}

.verification-badge.verified {
  background: rgba(16, 185, 129, 0.2);
  border: 1px solid var(--success);
  color: var(--success);
}

.verification-badge.unverified {
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid var(--error);
  color: var(--error);
}

/* === FORMULARIO === */
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

.password-field {
  position: relative;
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

.error-message {
  display: block;
  color: var(--error);
  font-size: 0.8rem;
  margin-top: var(--space-xs);
  font-weight: 500;
}

.input-help {
  color: var(--gray-light);
  font-size: 0.85rem;
  margin-top: var(--space-sm);
  font-style: italic;
}

.security-description,
.input-help,
.warning-list,
.warning-list li {
  color: #a0a0a0; /* Cambiar de var(--gray-light) a un gris más claro */
}

.form-input::placeholder {
  color: #b8b8b8; /* Gris más claro para mejor legibilidad */
  opacity: 1; /* Asegurar opacidad completa */
}

/* === SECCIÓN DE ADVERTENCIA === */
.warning-section {
  margin: var(--space-xl) 0;
}

.warning-box {
  background: rgba(245, 158, 11, 0.1);
  border: 2px solid rgba(245, 158, 11, 0.3);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
}

.warning-title {
  color: var(--warning);
  font-family: var(--font-impact);
  font-size: 1.2rem;
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.warning-list {
  color: var(--gray-light);
  margin: 0;
  padding-left: var(--space-lg);
}

.warning-list li {
  margin-bottom: var(--space-sm);
  line-height: 1.5;
}

/* === MENSAJES === */
.error-banner {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid var(--error);
  color: var(--error);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-lg);
  text-align: center;
}

/* === ACCIONES === */
.form-actions {
  margin-top: var(--space-xl);
}

.btn-large {
  width: 100%;
  padding: var(--space-lg);
  font-size: 1.2rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-height: 60px;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn.loading,
.btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* === NOTIFICACIÓN === */
.notification {
  position: fixed;
  top: var(--space-xl);
  right: var(--space-xl);
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  z-index: 1000;
  cursor: pointer;
  transform: translateX(100%);
  animation: slideIn 0.3s ease forwards;
  max-width: 350px;
}

.notification.success {
  border: 2px solid var(--success);
}

.notification.error {
  border: 2px solid var(--error);
}

@keyframes slideIn {
  to {
    transform: translateX(0);
  }
}

.notification-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notification-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-md);
  }

  .header-content {
    flex-direction: column;
    gap: var(--space-md);
  }

  .header-spacer {
    display: none;
  }

  .form-container {
    padding: var(--space-lg);
  }

  .current-value {
    flex-direction: column;
    align-items: flex-start;
  }

  .notification {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
}

@media (max-width: 480px) {
  .title-hero {
    font-size: 2rem;
  }

  .form-container {
    padding: var(--space-md);
  }
}
</style>