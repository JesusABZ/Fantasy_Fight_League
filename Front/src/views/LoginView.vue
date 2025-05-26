<template>
  <FormContainer
    title="Iniciar Sesión"
    subtitle="¡Bienvenido de vuelta, fighter!"
    size="large"
    back-button-text="Volver a la Home"
    back-route="/"
  >
    <!-- Formulario -->
    <form @submit.prevent="handleSubmit" class="login-form">
      
      <!-- Campo de usuario -->
      <BaseInput
        v-model="formData.username"
        label="Usuario"
        placeholder="Ingresa tu usuario"
        :error="errors.username"
        size="large"
        required
        @blur="validateField('username')"
        @input="clearFieldError('username')"
      />

      <!-- Campo de contraseña -->
      <BaseInput
        v-model="formData.password"
        type="password"
        label="Contraseña"
        placeholder="Ingresa tu contraseña"
        :error="errors.password"
        size="large"
        required
        @blur="validateField('password')"
        @input="clearFieldError('password')"
      />

      <!-- Link olvidaste contraseña -->
      <div class="forgot-password-container">
        <router-link to="/forgot-password" class="link forgot-password">
          ¿Olvidaste tu contraseña?
        </router-link>
      </div>

      <!-- Mensaje de error general -->
      <div v-if="generalError" class="error-banner-large">
        {{ generalError }}
      </div>

      <!-- Mensaje de éxito -->
      <div v-if="successMessage" class="success-banner-large">
        {{ successMessage }}
      </div>

      <!-- Botón de envío -->
      <div class="form-actions-large">
        <BaseButton
          type="submit"
          variant="primary"
          size="large"
          :loading="isSubmitting"
          :disabled="!isFormValid"
          full-width
        >
          {{ isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
        </BaseButton>
      </div>

    </form>

    <!-- Footer con link de registro -->
    <template #footer>
      <p class="register-link">
        ¿No tienes cuenta? 
        <BaseButton
          tag="router-link"
          to="/register"
          variant="secondary"
          size="small"
        >
          Regístrate aquí
        </BaseButton>
      </p>
    </template>
  </FormContainer>

  <!-- Notificación -->
  <BaseNotification
    :show="showNotification"
    :type="notificationType"
    :message="notificationMessage"
    @close="hideNotification"
  />
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()

    // Estado del formulario
    const formData = reactive({
      username: '',
      password: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isSubmitting = ref(false)
    const generalError = ref('')
    const successMessage = ref('')
    
    // Notificaciones
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationMessage = ref('')

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
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('username')
      validateField('password')
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
        console.log('Datos del login:', formData)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // Mostrar notificación de éxito
        showFloatingNotification('success', '¡Inicio de sesión exitoso!')
        
        // Redirigir después de un breve delay
        setTimeout(() => {
          router.push('/dashboard')
        }, 1000)
        
      } catch (error) {
        generalError.value = error.message || 'Credenciales incorrectas. Verifica tu usuario y contraseña.'
        showFloatingNotification('error', 'Error al iniciar sesión')
      } finally {
        isSubmitting.value = false
      }
    }

    // Mostrar notificación
    const showFloatingNotification = (type, message) => {
      notificationType.value = type
      notificationMessage.value = message
      showNotification.value = true
    }

    const hideNotification = () => {
      showNotification.value = false
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      isFormValid,
      showNotification,
      notificationType,
      notificationMessage,
      handleSubmit,
      validateField,
      clearFieldError,
      hideNotification
    }
  }
}
</script>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.forgot-password-container {
  text-align: right;
  margin-bottom: var(--space-md);
}

.forgot-password {
  font-size: 1rem;
}

.register-link {
  color: var(--gray-light);
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .register-link {
    flex-direction: column;
    gap: var(--space-sm);
  }
}
</style>