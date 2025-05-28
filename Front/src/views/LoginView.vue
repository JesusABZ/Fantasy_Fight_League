<template>
  <div class="login">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="login-container">
      <!-- Formulario de login -->
      <div class="login-form-container">
        <form @submit.prevent="handleSubmit" class="login-form">
          <!-- Título del formulario -->
          <h2 class="form-title">Iniciar Sesión</h2>
          <p class="form-subtitle">¡Bienvenido de vuelta, fighter!</p>
          
          <!-- Campos del formulario -->
          <div class="form-group">
            <label for="username" class="form-label">Usuario</label>
            <input
              id="username"
              v-model="formData.username"
              type="text"
              class="form-input"
              :class="{ 'error': errors.username }"
              placeholder="Ingresa tu usuario"
              @blur="validateField('username')"
              @input="clearFieldError('username')"
              required
            />
            <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
          </div>

          <div class="form-group">
            <label for="password" class="form-label">Contraseña</label>
            <div class="password-field">
              <input
                id="password"
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                :class="{ 'error': errors.password }"
                placeholder="Ingresa tu contraseña"
                @blur="validateField('password')"
                @input="clearFieldError('password')"
                required
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
                :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
              >
                <span v-if="showPassword">👁️</span>
                <span v-else>🙈</span>
              </button>
            </div>
            <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
          </div>

          <!-- Link olvidaste contraseña -->
          <div class="forgot-password-container">
            <router-link to="/forgot-password" class="link forgot-password">¿Olvidaste tu contraseña?</router-link>
          </div>

          <!-- Mensaje de error general -->
          <div v-if="authError" class="error-banner">
            {{ authError }}
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
              <span v-if="isSubmitting">Iniciando sesión...</span>
              <span v-else>Iniciar Sesión</span>
            </button>
          </div>

          <!-- Link para ir al registro -->
          <div class="form-footer">
            <p class="register-link">
              ¿No tienes cuenta? 
              <router-link to="/register" class="link">Regístrate aquí</router-link>
            </p>
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
import { useAuth } from '../composables/useAuth.js'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const { login, error: authError, isLoading } = useAuth()

    // Estado del formulario
    const formData = reactive({
      username: '',
      password: ''
    })

    // Estado de errores de validación (no confundir con errores de autenticación)
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = computed(() => isLoading.value)
    const successMessage = ref('')
    const showPassword = ref(false)

    // Computed property para validar si el formulario está completo y válido
    const isFormValid = computed(() => {
      return formData.username.trim() !== '' &&
             formData.password !== '' &&
             Object.keys(errors).length === 0
    })

    // Limpiar error de un campo específico
    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
    }

    // Validar un campo específico
    const validateField = (fieldName) => {
      // Limpiar error previo del campo
      clearFieldError(fieldName)

      switch (fieldName) {
        case 'username':
          if (!formData.username.trim()) {
            errors.username = 'El usuario es obligatorio'
          } else if (formData.username.length < 3) {
            errors.username = 'El usuario debe tener al menos 3 caracteres'
          }
          break

        case 'password':
          if (!formData.password) {
            errors.password = 'La contraseña es obligatoria'
          } else if (formData.password.length < 6) {
            errors.password = 'La contraseña debe tener al menos 6 caracteres'
          }
          break
      }
    }

    // Validaciones (para el submit final)
    const validateForm = () => {
      // Limpiar errores previos
      Object.keys(errors).forEach(key => delete errors[key])
      
      // Validar todos los campos
      validateField('username')
      validateField('password')

      return Object.keys(errors).length === 0
    }

    // Manejar envío del formulario
    const handleSubmit = async () => {
      successMessage.value = ''

      if (!validateForm()) {
        return
      }

      try {
        console.log('🚀 Iniciando login con:', { username: formData.username })
        
        // Llamar al método de login del store
        const response = await login({
          username: formData.username.trim(),
          password: formData.password
        })
        
        console.log('✅ Login exitoso:', response)
        successMessage.value = '¡Inicio de sesión exitoso!'
        
        // Verificar el estado del email y redirigir en consecuencia
        if (response.emailConfirmed) {
          console.log('📧 Email verificado - Redirigiendo al dashboard')
          setTimeout(() => {
            router.push('/dashboard')
          }, 1000)
        } else {
          console.log('⚠️ Email no verificado - Redirigiendo a verificación')
          setTimeout(() => {
            router.push({
              name: 'EmailUnverified',
              query: { email: response.email }
            })
          }, 1000)
        }
        
      } catch (error) {
        console.error('❌ Error en login:', error)
        // El error ya se maneja en el store, solo mostramos el mensaje
        // authError ya está vinculado al error del store
      }
    }

    // Función para volver a la home
    const goBack = () => {
      router.push('/')
    }

    return {
      formData,
      errors,
      isSubmitting,
      authError,
      successMessage,
      showPassword,
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
.login {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-xl) 0;
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
.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 650px;
  margin: 0 auto;
  padding: var(--space-lg);
}

/* === FORMULARIO === */
.login-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl) 3rem;
  box-shadow: var(--shadow-lg);
  min-height: 600px;
  display: flex;
  align-items: center;
}

.login-form {
  width: 100%;
}

.form-title {
  font-family: var(--font-impact);
  font-size: 2.8rem;
  font-weight: 400;
  color: var(--white);
  text-align: center;
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-subtitle {
  color: var(--gray-light);
  text-align: center;
  margin-bottom: 3rem;
  font-size: 1.2rem;
}

.form-group {
  margin-bottom: 2rem;
}

.form-label {
  display: block;
  font-weight: 600;
  color: var(--white);
  margin-bottom: var(--space-md);
  font-size: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-input {
  width: 100%;
  padding: 1.2rem;
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
  font-size: 1rem;
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

/* === CAMPO DE CONTRASEÑA === */
.password-field {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 1rem;
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

/* === OPCIONES DEL FORMULARIO === */
.forgot-password-container {
  text-align: right;
  margin-bottom: 2rem;
}

.forgot-password {
  font-size: 1rem;
}

/* === MENSAJES === */
.error-message {
  display: block;
  color: var(--error);
  font-size: 0.9rem;
  margin-top: var(--space-sm);
  font-weight: 500;
}

.error-banner {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid var(--error);
  color: var(--error);
  padding: 1rem;
  border-radius: var(--radius-md);
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1rem;
}

.success-banner {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid var(--success);
  color: var(--success);
  padding: 1rem;
  border-radius: var(--radius-md);
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1rem;
}

/* === BOTONES Y ACCIONES === */
.form-actions {
  margin-bottom: 2rem;
}

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
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(.loading):not(.disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-large {
  width: 100%;
  padding: 1.2rem;
  font-size: 1.2rem;
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

.form-footer {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 1.5rem;
}

.register-link {
  color: var(--gray-light);
  font-size: 1rem;
}

.link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.link:hover {
  color: var(--primary-light);
  text-decoration: underline;
}

/* === BOTÓN VOLVER === */
.back-button-container {
  margin-top: 1.5rem;
  text-align: center;
}

.btn-back {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: var(--space-md) var(--space-xl);
  font-size: 1rem;
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
  .login {
    padding: var(--space-lg) 0;
  }

  .login-container {
    max-width: 90%;
    padding: var(--space-md);
  }

  .login-form-container {
    padding: var(--space-xl) var(--space-lg);
    min-height: auto;
  }

  .form-title {
    font-size: 2.2rem;
  }

  .forgot-password-container {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: var(--space-sm);
  }

  .login-form-container {
    padding: var(--space-lg);
  }

  .form-title {
    font-size: 2rem;
  }
}
</style>