<template>
  <div class="change-password">
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
          <h1 class="title-hero">CAMBIAR CONTRASEÑA</h1>
          <div class="header-spacer"></div>
        </div>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="main-content">
      <div class="container">
        
        <!-- Formulario de cambio de contraseña -->
        <div class="form-container">
          <form @submit.prevent="handleSubmit" class="change-form">
            
            <!-- Información de seguridad -->
            <div class="security-info">
              <h3 class="section-title">🔐 Cambiar Contraseña</h3>
              <p class="security-description">
                Por tu seguridad, necesitamos verificar tu contraseña actual antes de cambiarla.
              </p>
            </div>

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
                  <span v-else>🙈</span>
                </button>
              </div>
              <span v-if="errors.currentPassword" class="error-message">{{ errors.currentPassword }}</span>
            </div>

            <!-- Separador -->
            <div class="section-divider"></div>

            <!-- Nueva contraseña -->
            <div class="form-group">
              <label for="newPassword" class="form-label">Nueva Contraseña</label>
              <div class="password-field">
                <input
                  id="newPassword"
                  v-model="formData.newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  class="form-input"
                  :class="{ 'error': errors.newPassword }"
                  placeholder="Mínimo 6 caracteres"
                  @blur="validateField('newPassword')"
                  @input="clearFieldError('newPassword')"
                  required
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showNewPassword = !showNewPassword"
                >
                  <span v-if="showNewPassword">👁️</span>
                  <span v-else>🙈</span>
                </button>
              </div>
              <span v-if="errors.newPassword" class="error-message">{{ errors.newPassword }}</span>
              
              <!-- Indicador de fuerza de contraseña -->
              <div v-if="formData.newPassword" class="password-strength">
                <div class="strength-bar">
                  <div 
                    class="strength-fill" 
                    :class="passwordStrength.class"
                    :style="{ width: passwordStrength.percentage + '%' }"
                  ></div>
                </div>
                <span class="strength-text" :class="passwordStrength.class">
                  {{ passwordStrength.text }}
                </span>
              </div>
            </div>

            <!-- Confirmar nueva contraseña -->
            <div class="form-group">
              <label for="confirmPassword" class="form-label">Confirmar Nueva Contraseña</label>
              <div class="password-field">
                <input
                  id="confirmPassword"
                  v-model="formData.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-input"
                  :class="{ 'error': errors.confirmPassword }"
                  placeholder="Repite la nueva contraseña"
                  @blur="validateField('confirmPassword')"
                  @input="clearFieldError('confirmPassword')"
                  required
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <span v-if="showConfirmPassword">👁️</span>
                  <span v-else>🙈</span>
                </button>
              </div>
              <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
            </div>

            <!-- Recomendaciones de seguridad -->
            <div class="security-tips">
              <h4 class="tips-title">💡 Recomendaciones para una contraseña segura:</h4>
              <ul class="tips-list">
                <li :class="{ 'valid': passwordChecks.length }">Al menos 6 caracteres</li>
                <li :class="{ 'valid': passwordChecks.hasUpper }">Al menos una mayúscula</li>
                <li :class="{ 'valid': passwordChecks.hasLower }">Al menos una minúscula</li>
                <li :class="{ 'valid': passwordChecks.hasNumber }">Al menos un número</li>
                <li :class="{ 'valid': passwordChecks.hasSpecial }">Al menos un carácter especial</li>
              </ul>
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
                <span v-if="isSubmitting">Cambiando Contraseña...</span>
                <span v-else>🔑 Cambiar Contraseña</span>
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
        <span v-else>❌</span>
      </div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ChangePasswordView',
  setup() {
    const router = useRouter()

    // Estado del formulario
    const formData = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const showCurrentPassword = ref(false)
    const showNewPassword = ref(false)
    const showConfirmPassword = ref(false)
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Computed properties para validación de contraseña
    const passwordChecks = computed(() => {
      const password = formData.newPassword
      return {
        length: password.length >= 6,
        hasUpper: /[A-Z]/.test(password),
        hasLower: /[a-z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(password)
      }
    })

    const passwordStrength = computed(() => {
      const checks = passwordChecks.value
      const score = Object.values(checks).filter(Boolean).length

      if (score <= 1) {
        return { class: 'weak', text: 'Muy débil', percentage: 20 }
      } else if (score === 2) {
        return { class: 'weak', text: 'Débil', percentage: 40 }
      } else if (score === 3) {
        return { class: 'medium', text: 'Regular', percentage: 60 }
      } else if (score === 4) {
        return { class: 'strong', text: 'Fuerte', percentage: 80 }
      } else {
        return { class: 'very-strong', text: 'Muy fuerte', percentage: 100 }
      }
    })

    const isFormValid = computed(() => {
      return formData.currentPassword.trim() !== '' &&
             formData.newPassword.trim() !== '' &&
             formData.confirmPassword.trim() !== '' &&
             formData.newPassword === formData.confirmPassword &&
             formData.currentPassword !== formData.newPassword &&
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

        case 'newPassword':
          if (!formData.newPassword.trim()) {
            errors.newPassword = 'La nueva contraseña es obligatoria'
          } else if (formData.newPassword.length < 6) {
            errors.newPassword = 'La contraseña debe tener al menos 6 caracteres'
          } else if (formData.newPassword === formData.currentPassword) {
            errors.newPassword = 'La nueva contraseña debe ser diferente a la actual'
          }
          
          // Re-validar confirmación si ya se llenó
          if (formData.confirmPassword) {
            validateField('confirmPassword')
          }
          break

        case 'confirmPassword':
          if (!formData.confirmPassword.trim()) {
            errors.confirmPassword = 'Debes confirmar la nueva contraseña'
          } else if (formData.newPassword !== formData.confirmPassword) {
            errors.confirmPassword = 'Las contraseñas no coinciden'
          }
          break
      }
    }

    const validateForm = () => {
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('currentPassword')
      validateField('newPassword')
      validateField('confirmPassword')
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
          newPassword: formData.newPassword
        }

        console.log('Cambiando contraseña:', changeData)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        showFloatingNotification('success', '¡Contraseña cambiada correctamente!')
        
        // Limpiar formulario
        Object.keys(formData).forEach(key => formData[key] = '')
        
        // Esperar un momento para mostrar la notificación
        setTimeout(() => {
          // Volver a la edición de perfil
          router.push('/profile/edit')
        }, 2000)
        
      } catch (error) {
        generalError.value = error.message || 'Error al cambiar la contraseña. Verifica tu contraseña actual.'
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
      formData,
      errors,
      isSubmitting,
      generalError,
      isFormValid,
      showCurrentPassword,
      showNewPassword,
      showConfirmPassword,
      showNotification,
      notificationType,
      notificationText,
      passwordChecks,
      passwordStrength,
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
.change-password {
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
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.security-description {
  color: var(--gray-light);
  font-size: 1rem;
  line-height: 1.5;
  margin-bottom: var(--space-xl);
}

.section-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 107, 53, 0.3) 50%,
    transparent 100%
  );
  margin: var(--space-xl) 0;
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

/* Cambiar estos estilos */
.security-description,
.tips-list,
.tips-list li {
  color: #a0a0a0; /* Gris más claro y legible */
}

.form-input::placeholder {
  color: #b8b8b8; /* Gris más claro para mejor legibilidad */
  opacity: 1; /* Asegurar opacidad completa */
}

/* === INDICADOR DE FUERZA === */
.password-strength {
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

/* === RECOMENDACIONES === */
.security-tips {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  margin: var(--space-xl) 0;
}

.tips-title {
  color: var(--white);
  font-size: 1rem;
  margin-bottom: var(--space-md);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.tips-list {
  margin: 0;
  padding-left: var(--space-lg);
  color: var(--gray-light);
}

.tips-list li {
  margin-bottom: var(--space-xs);
  transition: color 0.3s ease;
}

.tips-list li.valid {
  color: var(--success);
}

.tips-list li.valid::before {
  content: '✓ ';
  font-weight: bold;
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