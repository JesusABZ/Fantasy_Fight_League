<template>
  <form @submit.prevent="handleSubmit" class="login-form">
    <!-- Campo de usuario -->
    <BaseInput
      v-model="formData.username"
      label="Usuario"
      placeholder="Ingresa tu usuario"
      :error="errors.username"
      :size="size"
      show-validation-icon
      required
      @blur="validateField('username')"
      @input="clearFieldError('username')"
    />

    <!-- Campo de contraseña -->
    <PasswordField
      v-model="formData.password"
      label="Contraseña"
      placeholder="Ingresa tu contraseña"
      :error="errors.password"
      :size="size"
      help="Mínimo 6 caracteres"
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
    <div v-if="generalError" class="error-banner" :class="{ 'error-banner-large': size === 'large' }">
      {{ generalError }}
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
        {{ isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
      </BaseButton>
    </div>

    <!-- Footer con link de registro -->
    <div class="form-footer">
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
    </div>
  </form>
</template>

<script>
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useForm } from '@/composables/useForm'
import { useAuth } from '@/composables/useAuth'
import PasswordField from './PasswordField.vue'

export default {
  name: 'LoginForm',
  
  components: {
    PasswordField
  },
  
  props: {
    size: {
      type: String,
      default: 'large',
      validator: (value) => ['medium', 'large'].includes(value)
    },
    
    redirectTo: {
      type: String,
      default: '/dashboard'
    },
    
    showNotifications: {
      type: Boolean,
      default: true
    }
  },
  
  emits: ['success', 'error'],
  
  setup(props, { emit }) {
    const router = useRouter()
    const { handleLogin } = useAuth()
    
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
      { username: '', password: '' },
      {
        validationRules: {
          username: [
            (value) => value.trim() ? null : 'El usuario es obligatorio',
            (value) => value.length >= 3 ? null : 'El usuario debe tener al menos 3 caracteres'
          ],
          password: [
            (value) => value ? null : 'La contraseña es obligatoria',
            (value) => value.length >= 6 ? null : 'La contraseña debe tener al menos 6 caracteres'
          ]
        },
        showNotifications: props.showNotifications
      }
    )

    // Manejar envío del formulario
    const handleSubmit = async () => {
      const result = await submitForm(async (data) => {
        try {
          await handleLogin(data, props.redirectTo, props.showNotifications)
          emit('success', data)
          return { success: true }
        } catch (error) {
          emit('error', error)
          throw error
        }
      })

      if (result.success) {
        router.push(props.redirectTo)
      }
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      canSubmit,
      validateField,
      clearFieldError,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  width: 100%;
}

.forgot-password-container {
  text-align: right;
  margin-bottom: var(--space-md);
}

.forgot-password {
  font-size: 1rem;
}

.form-actions {
  margin-bottom: var(--space-lg);
}

.form-actions-large {
  margin-bottom: var(--space-xl);
}

.form-footer {
  text-align: center;
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
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

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .register-link {
    flex-direction: column;
    gap: var(--space-sm);
  }
}
</style>