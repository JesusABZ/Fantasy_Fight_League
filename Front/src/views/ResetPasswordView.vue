<template>
  <div class="reset-password">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="reset-container">
      
      <!-- Loading mientras se valida el token -->
      <div v-if="isValidatingToken" class="loading-container">
        <div class="loading-spinner">⏳</div>
        <h2 class="loading-title">Validando enlace...</h2>
        <p class="loading-text">Por favor espera mientras verificamos tu enlace de recuperación</p>
      </div>

      <!-- Token inválido o expirado -->
      <div v-else-if="!tokenValid" class="error-container">
        <div class="error-icon">❌</div>
        <h2 class="error-title">Enlace Inválido</h2>
        <p class="error-message">{{ tokenError }}</p>
        <div class="error-actions">
          <button class="btn btn-primary" @click="goToForgotPassword">
            Solicitar Nuevo Enlace
          </button>
          <button class="btn btn-secondary" @click="goToLogin">
            Volver al Login
          </button>
        </div>
      </div>

      <!-- Formulario de cambio de contraseña -->
      <div v-else class="reset-form-container">
        <form @submit.prevent="handleSubmit" class="reset-form">
          
          <!-- Icono de éxito -->
          <div class="success-icon-container">
            <div class="success-icon">🔑</div>
          </div>

          <!-- Información del usuario -->
          <div class="user-info">
            <h2 class="form-title">Cambiar Contraseña</h2>
            <p class="form-subtitle">
              Cambiando contraseña para: <strong>{{ userEmail }}</strong>
            </p>
          </div>

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

          <!-- Mensaje de éxito -->
          <div v-if="successMessage" class="success-banner">
            <div class="success-content">
              <div class="success-icon-small">✅</div>
              <div class="success-text">
                <h4>¡Contraseña Cambiada!</h4>
                <p>{{ successMessage }}</p>
              </div>
            </div>
          </div>

          <!-- Botón de envío -->
          <div class="form-actions">
            <button
              type="submit"
              class="btn btn-primary btn-large"
              :disabled="isSubmitting || !isFormValid || passwordChanged"
              :class="{ 
                'loading': isSubmitting, 
                'disabled': !isFormValid,
                'success': passwordChanged 
              }"
            >
              <span v-if="isSubmitting">Cambiando Contraseña...</span>
              <span v-else-if="passwordChanged">¡Contraseña Cambiada!</span>
              <span v-else>🔑 Cambiar Contraseña</span>
            </button>
          </div>

          <!-- Botones de navegación (solo después del éxito) -->
          <div v-if="passwordChanged" class="navigation-actions">
            <button 
              type="button" 
              class="btn btn-secondary btn-large"
              @click="goToLogin"
            >
              Ir al Login
            </button>
          </div>

        </form>
      </div>

    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { passwordResetService } from '../api/index.js'

export default {
  name: 'ResetPasswordView',
  setup() {
    const router = useRouter()
    const route = useRoute()

    // Estado del token
    const isValidatingToken = ref(true)
    const tokenValid = ref(false)
    const tokenError = ref('')
    const userEmail = ref('')
    const resetToken = ref('')

    // Estado del formulario
    const formData = reactive({
      newPassword: '',
      confirmPassword: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const successMessage = ref('')
    const passwordChanged = ref(false)
    const showNewPassword = ref(false)
    const showConfirmPassword = ref(false)

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
      return formData.newPassword.trim() !== '' &&
             formData.confirmPassword.trim() !== '' &&
             formData.newPassword === formData.confirmPassword &&
             formData.newPassword.length >= 6 &&
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
        case 'newPassword':
          if (!formData.newPassword.trim()) {
            errors.newPassword = 'La nueva contraseña es obligatoria'
          } else if (formData.newPassword.length < 6) {
            errors.newPassword = 'La contraseña debe tener al menos 6 caracteres'
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
      validateField('newPassword')
      validateField('confirmPassword')
      return Object.keys(errors).length === 0
    }

    // Validar token al cargar la página
    const validateToken = async () => {
      const token = route.query.token
      
      if (!token) {
        tokenValid.value = false
        tokenError.value = 'No se proporcionó un token de recuperación válido'
        isValidatingToken.value = false
        return
      }

      resetToken.value = token

      try {
        const response = await passwordResetService.validateResetToken(token)
        
        if (response.valid) {
          tokenValid.value = true
          userEmail.value = response.userEmail || 'tu cuenta'
        } else {
          tokenValid.value = false
          tokenError.value = response.message || 'El enlace de recuperación no es válido'
        }
      } catch (error) {
        tokenValid.value = false
        tokenError.value = error.message || 'El enlace de recuperación ha expirado o no es válido'
      } finally {
        isValidatingToken.value = false
      }
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
        const response = await passwordResetService.resetPassword(
          resetToken.value,
          formData.newPassword,
          formData.confirmPassword
        )
        
        passwordChanged.value = true
        successMessage.value = response.message || '¡Contraseña cambiada exitosamente!'
        
        // Limpiar formulario
        Object.keys(formData).forEach(key => formData[key] = '')
        
      } catch (error) {
        generalError.value = error.message || 'Error al cambiar la contraseña. Inténtalo de nuevo.'
      } finally {
        isSubmitting.value = false
      }
    }

    // Funciones de navegación
    const goToLogin = () => {
      router.push('/login')
    }

    const goToForgotPassword = () => {
      router.push('/forgot-password')
    }

    // Validar token al montar el componente
    onMounted(() => {
      validateToken()
    })

    return {
      // Estado del token
      isValidatingToken,
      tokenValid,
      tokenError,
      userEmail,
      
      // Estado del formulario
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      passwordChanged,
      showNewPassword,
      showConfirmPassword,
      
      // Computed
      passwordChecks,
      passwordStrength,
      isFormValid,
      
      // Funciones
      handleSubmit,
      validateField,
      clearFieldError,
      goToLogin,
      goToForgotPassword
    }
  }
}
</script>

<style scoped>
.reset-password {
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
.reset-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: var(--space-lg);
}

/* === LOADING === */
.loading-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  text-align: center;
  box-shadow: var(--shadow-lg);
}

.loading-spinner {
  font-size: 3rem;
  margin-bottom: var(--space-lg);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-title {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  color: var(--white);
  margin-bottom: var(--space-md);
}

.loading-text {
  color: var(--gray-light);
  font-size: 1rem;
}

/* === ERROR CONTAINER === */
.error-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid var(--error);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  text-align: center;
  box-shadow: var(--shadow-lg);
}

.error-icon {
  font-size: 4rem;
  margin-bottom: var(--space-lg);
}

.error-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  color: var(--error);
  margin-bottom: var(--space-md);
}

.error-message {
  color: var(--gray-light);
  font-size: 1.1rem;
  margin-bottom: var(--space-xl);
  line-height: 1.5;
}

.error-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
  flex-wrap: wrap;
}

/* === FORMULARIO === */
.reset-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.reset-form {
  width: 100%;
}

/* === ICONO DE ÉXITO === */
.success-icon-container {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.success-icon {
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

/* === INFO DEL USUARIO === */
.user-info {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.form-title {
  font-family: var(--font-impact);
  font-size: 2.2rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-sm);
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

.success-icon-small {
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

/* === BOTONES === */
.btn {
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

.btn-large {
  width: 100%;
  padding: var(--space-lg);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-height: 60px;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-secondary {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

.btn.loading,
.btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.btn.success {
  background: var(--success) !important;
  border-color: var(--success) !important;
}

/* === ACCIONES === */
.form-actions {
  margin-bottom: var(--space-lg);
}

.navigation-actions {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: var(--space-lg);
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .reset-container {
    padding: var(--space-md);
  }

  .reset-form-container,
  .error-container,
  .loading-container {
    padding: var(--space-xl);
  }

  .form-title {
    font-size: 2rem;
  }

  .success-icon {
    width: 70px;
    height: 70px;
    font-size: 2rem;
  }

  .error-actions {
    flex-direction: column;
  }

  .success-content {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .reset-form-container,
  .error-container,
  .loading-container {
    padding: var(--space-lg);
  }
}
</style>