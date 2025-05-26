<template>
  <AuthLayout container-size="medium">
    <FormContainer
      title="Iniciar Sesión"
      subtitle="¡Bienvenido de vuelta, fighter!"
      icon="🔐"
      size="large"
      :show-back-button="false"
    >
      <!-- Formulario -->
      <form @submit.prevent="handleSubmit" class="login-form">
        
        <!-- Campo de usuario -->
        <BaseInput
          v-model="formData.username"
          label="Usuario"
          placeholder="Ingresa tu usuario"
          validation-schema="username"
          :error="errors.username"
          size="large"
          show-validation-icon
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
          validation-schema="required"
          :error="errors.password"
          size="large"
          show-validation-icon
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

        <!-- Botón de envío -->
        <div class="form-actions-large">
          <BaseButton
            type="submit"
            variant="primary"
            size="large"
            :loading="isSubmitting"
            :disabled="!canSubmit"
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
  </AuthLayout>
</template>

<script>
import { useRouter } from 'vue-router'
import { useForm } from '@/composables/useForm'
import { useAuth } from '@/composables/useAuth'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const { handleLogin } = useAuth()
    
    // Crear formulario de login con validación automática
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
      { username: '', password: '' },
      {
        validationSchema: 'login',
        showNotifications: true
      }
    )

    // Manejar envío del formulario
    const handleSubmit = async () => {
      const result = await submitForm(async (data) => {
        // Usar el auth service
        await handleLogin(data, '/dashboard', false) // false = no mostrar notificación aquí
        return { success: true }
      })

      if (result.success) {
        router.push('/dashboard')
      }
    }

    return {
      // Estado del formulario
      formData,
      errors,
      isSubmitting,
      generalError,
      canSubmit,
      
      // Métodos
      handleSubmit,
      validateField,
      clearFieldError
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