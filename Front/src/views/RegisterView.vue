<template>
  <div class="register">
    <!-- Fondo estático con overlay (igual que en Home) -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="register-container">
      <!-- Formulario de registro -->
      <div class="register-form-container">
        <form @submit.prevent="handleSubmit" class="register-form">
          <!-- Título del formulario -->
          <h2 class="form-title">Crear Cuenta</h2>
          
          <!-- Campos del formulario -->
          <div class="form-group">
            <label for="username" class="form-label">Nombre de Usuario</label>
            <input
              id="username"
              v-model="formData.username"
              type="text"
              class="form-input"
              :class="{ 'error': errors.username }"
              placeholder="Elige tu nombre de usuario"
              @blur="validateField('username')"
              @input="clearFieldError('username')"
              required
            />
            <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="firstName" class="form-label">Nombre</label>
              <input
                id="firstName"
                v-model="formData.firstName"
                type="text"
                class="form-input"
                :class="{ 'error': errors.firstName }"
                placeholder="Tu nombre"
                @blur="validateField('firstName')"
                @input="clearFieldError('firstName')"
                required
              />
              <span v-if="errors.firstName" class="error-message">{{ errors.firstName }}</span>
            </div>

            <div class="form-group">
              <label for="lastName" class="form-label">Apellidos</label>
              <input
                id="lastName"
                v-model="formData.lastName"
                type="text"
                class="form-input"
                :class="{ 'error': errors.lastName }"
                placeholder="Tus apellidos"
                @blur="validateField('lastName')"
                @input="clearFieldError('lastName')"
                required
              />
              <span v-if="errors.lastName" class="error-message">{{ errors.lastName }}</span>
            </div>
          </div>

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

          <div class="form-group">
            <label for="password" class="form-label">Contraseña</label>
            <div class="password-field">
              <input
                id="password"
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                :class="{ 'error': errors.password }"
                placeholder="Mínimo 6 caracteres"
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

          <div class="form-group">
            <label for="confirmPassword" class="form-label">Confirmar Contraseña</label>
            <div class="password-field">
              <input
                id="confirmPassword"
                v-model="formData.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                class="form-input"
                :class="{ 'error': errors.confirmPassword }"
                placeholder="Repite tu contraseña"
                @blur="validateField('confirmPassword')"
                @input="clearFieldError('confirmPassword')"
                required
              />
              <button
                type="button"
                class="password-toggle"
                @click="showConfirmPassword = !showConfirmPassword"
                :aria-label="showConfirmPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
              >
                <span v-if="showConfirmPassword">👁️</span>
                <span v-else>🙈</span>
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
          </div>

          <!-- Términos y condiciones -->
          <div class="form-group">
            <label class="checkbox-label">
              <input
                v-model="formData.acceptTerms"
                type="checkbox"
                class="checkbox-input"
                @change="validateField('acceptTerms')"
                required
              />
              <span class="checkbox-custom"></span>
              <span class="checkbox-text">
                Acepto los 
                <a href="#" class="link">Términos y Condiciones</a> 
                y la 
                <a href="#" class="link">Política de Privacidad</a>
              </span>
            </label>
            <span v-if="errors.acceptTerms" class="error-message">{{ errors.acceptTerms }}</span>
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
              <span v-if="isSubmitting">Creando cuenta...</span>
              <span v-else>Crear Cuenta</span>
            </button>
          </div>

          <!-- Link para ir al login -->
          <div class="form-footer">
            <p class="login-link">
              ¿Ya tienes cuenta? 
              <button 
                type="button" 
                class="link-button"
                @click="goToLogin"
              >
                Inicia sesión aquí
              </button>
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
import { authService } from '../api/authService.js'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    
    // Estado del formulario
    const formData = reactive({
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const successMessage = ref('')
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)

    // Computed property para validar si el formulario está completo y válido
    const isFormValid = computed(() => {
      return formData.username.trim() !== '' &&
             formData.firstName.trim() !== '' &&
             formData.lastName.trim() !== '' &&
             formData.email.trim() !== '' &&
             formData.password !== '' &&
             formData.confirmPassword !== '' &&
             formData.acceptTerms &&
             Object.keys(errors).length === 0
    })

    // Limpiar error de un campo específico
    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
      // Limpiar error general
      generalError.value = ''
    }

    // Validar un campo específico
    const validateField = (fieldName) => {
      // Limpiar error previo del campo
      clearFieldError(fieldName)

      switch (fieldName) {
        case 'username':
          if (!formData.username.trim()) {
            errors.username = 'El nombre de usuario es obligatorio'
          } else if (formData.username.length < 3) {
            errors.username = 'El nombre de usuario debe tener al menos 3 caracteres'
          } else if (formData.username.length > 20) {
            errors.username = 'El nombre de usuario no puede tener más de 20 caracteres'
          } else if (!/^[a-zA-Z0-9_]+$/.test(formData.username)) {
            errors.username = 'Solo se permiten letras, números y guiones bajos'
          }
          break

        case 'firstName':
          if (!formData.firstName.trim()) {
            errors.firstName = 'El nombre es obligatorio'
          } else if (formData.firstName.trim().length < 2) {
            errors.firstName = 'El nombre debe tener al menos 2 caracteres'
          } else if (formData.firstName.trim().length > 50) {
            errors.firstName = 'El nombre no puede tener más de 50 caracteres'
          }
          break

        case 'lastName':
          if (!formData.lastName.trim()) {
            errors.lastName = 'Los apellidos son obligatorios'
          } else if (formData.lastName.trim().length < 2) {
            errors.lastName = 'Los apellidos deben tener al menos 2 caracteres'
          } else if (formData.lastName.trim().length > 50) {
            errors.lastName = 'Los apellidos no pueden tener más de 50 caracteres'
          }
          break

        case 'email':
          if (!formData.email.trim()) {
            errors.email = 'El email es obligatorio'
          } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            errors.email = 'El formato del email no es válido'
          } else if (formData.email.length > 100) {
            errors.email = 'El email no puede tener más de 100 caracteres'
          }
          break

        case 'password':
          if (!formData.password) {
            errors.password = 'La contraseña es obligatoria'
          } else if (formData.password.length < 6) {
            errors.password = 'La contraseña debe tener al menos 6 caracteres'
          } else if (formData.password.length > 100) {
            errors.password = 'La contraseña no puede tener más de 100 caracteres'
          }
          
          // Re-validar confirmación si ya se llenó
          if (formData.confirmPassword) {
            validateField('confirmPassword')
          }
          break

        case 'confirmPassword':
          if (!formData.confirmPassword) {
            errors.confirmPassword = 'Debes confirmar la contraseña'
          } else if (formData.password !== formData.confirmPassword) {
            errors.confirmPassword = 'Las contraseñas no coinciden'
          }
          break

        case 'acceptTerms':
          if (!formData.acceptTerms) {
            errors.acceptTerms = 'Debes aceptar los términos y condiciones'
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
      validateField('firstName')
      validateField('lastName')
      validateField('email')
      validateField('password')
      validateField('confirmPassword')
      validateField('acceptTerms')

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
        const registrationData = {
          username: formData.username.trim(),
          email: formData.email.trim().toLowerCase(),
          password: formData.password,
          firstName: formData.firstName.trim(),
          lastName: formData.lastName.trim()
        }
        
        // Llamar directamente al servicio de registro
        const response = await authService.register(registrationData)
        
        console.log('Usuario registrado exitosamente:', response)
        
        // Mostrar mensaje de éxito
        successMessage.value = '¡Cuenta creada exitosamente! Se ha enviado un email de verificación.'
        
        // Esperar un momento para que el usuario vea el mensaje
        setTimeout(() => {
          // Redirigir a página de verificación con el email
          router.push({
            name: 'VerifyEmail',
            query: { email: formData.email }
          })
        }, 2000)
        
      } catch (error) {
        console.error('Error en registro:', error)
        generalError.value = error.message || 'Error al crear la cuenta. Inténtalo de nuevo.'
      } finally {
        isSubmitting.value = false
      }
    }

    // Función para volver a la home
    const goBack = () => {
      console.log('Navegando a la home...')
      router.push('/')
    }

    // Función para ir al login
    const goToLogin = () => {
      console.log('Navegando al login...')
      router.push('/login')
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      showPassword,
      showConfirmPassword,
      isFormValid,
      handleSubmit,
      validateField,
      clearFieldError,
      goBack,
      goToLogin
    }
  }
}
</script>

<style scoped>
.register {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* === FONDO (igual que en Home) === */
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
.register-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: var(--space-xl) var(--space-lg);
}

/* === FORMULARIO === */
.register-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.register-form {
  width: 100%;
}

.form-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  font-weight: 400;
  color: var(--white);
  text-align: center;
  margin-bottom: var(--space-2xl);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-group {
  margin-bottom: var(--space-lg);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
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
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--white);
  font-size: 1rem;
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

/* === CAMPOS DE CONTRASEÑA === */
.password-field {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: var(--space-md);
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--gray-light);
  cursor: pointer;
  font-size: 1.2rem;
  padding: var(--space-xs);
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

/* === CHECKBOX === */
.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: var(--space-md);
  cursor: pointer;
  line-height: 1.5;
}

.checkbox-input {
  display: none;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-sm);
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
  margin-top: 2px;
}

.checkbox-input:checked + .checkbox-custom {
  background: var(--gradient-primary);
  border-color: var(--primary);
}

.checkbox-input:checked + .checkbox-custom::after {
  content: '✓';
  color: var(--white);
  font-size: 0.8rem;
  font-weight: bold;
}

.checkbox-text {
  color: var(--gray-light);
  font-size: 0.9rem;
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

/* === BOTONES Y ACCIONES === */
.form-actions {
  margin-bottom: var(--space-lg);
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-height: 48px;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
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

.btn.disabled:hover {
  transform: none !important;
  box-shadow: var(--shadow-md) !important;
}

.form-footer {
  text-align: center;
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.login-link {
  color: var(--gray-light);
  font-size: 0.9rem;
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

.link-button:hover {
  color: var(--primary-light);
}

/* === BOTÓN VOLVER === */
.back-button-container {
  margin-top: var(--space-lg);
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
  .register-container {
    padding: var(--space-lg) var(--space-md);
  }

  .register-form-container {
    padding: var(--space-xl);
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: var(--space-md);
  }

  .register-form-container {
    padding: var(--space-lg);
  }
}
</style>