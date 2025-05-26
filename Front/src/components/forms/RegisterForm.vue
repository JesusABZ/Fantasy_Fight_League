<template>
  <form @submit.prevent="handleSubmit" class="register-form">
    <!-- Nombre de usuario -->
    <BaseInput
      v-model="formData.username"
      label="Nombre de Usuario"
      placeholder="Elige tu nombre de usuario"
      :error="errors.username"
      :size="size"
      help="Solo letras, números y guiones bajos"
      required
      @blur="validateField('username')"
      @input="clearFieldError('username')"
    />

    <!-- Nombre y apellidos -->
    <div class="form-row">
      <BaseInput
        v-model="formData.firstName"
        label="Nombre"
        placeholder="Tu nombre"
        :error="errors.firstName"
        :size="size"
        required
        @blur="validateField('firstName')"
        @input="clearFieldError('firstName')"
      />

      <BaseInput
        v-model="formData.lastName"
        label="Apellidos"
        placeholder="Tus apellidos"
        :error="errors.lastName"
        :size="size"
        required
        @blur="validateField('lastName')"
        @input="clearFieldError('lastName')"
      />
    </div>

    <!-- Email -->
    <BaseInput
      v-model="formData.email"
      type="email"
      label="Correo Electrónico"
      placeholder="tu.email@ejemplo.com"
      :error="errors.email"
      :size="size"
      required
      @blur="validateField('email')"
      @input="clearFieldError('email')"
    />

    <!-- Contraseña -->
    <PasswordField
      v-model="formData.password"
      label="Contraseña"
      placeholder="Mínimo 6 caracteres"
      :error="errors.password"
      :size="size"
      :show-strength="showPasswordStrength"
      :show-criteria="showPasswordCriteria"
      :require-special="requireSpecialChars"
      required
      @blur="validateField('password')"
      @input="clearFieldError('password')"
      @strength-change="handlePasswordStrength"
    />

    <!-- Confirmar contraseña -->
    <PasswordField
      v-model="formData.confirmPassword"
      label="Confirmar Contraseña"
      placeholder="Repite tu contraseña"
      :error="errors.confirmPassword"
      :size="size"
      required
      @blur="validateField('confirmPassword')"
      @input="clearFieldError('confirmPassword')"
    />

    <!-- Términos y condiciones -->
    <div class="checkbox-group">
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
          <a href="#" class="link" @click.prevent="showTerms">Términos y Condiciones</a> 
          y la 
          <a href="#" class="link" @click.prevent="showPrivacy">Política de Privacidad</a>
        </span>
      </label>
      <span v-if="errors.acceptTerms" class="error-message">{{ errors.acceptTerms }}</span>
    </div>

    <!-- Mensaje de error general -->
    <div v-if="generalError" class="error-banner" :class="{ 'error-banner-large': size === 'large' }">
      {{ generalError }}
    </div>

    <!-- Mensaje de éxito -->
    <div v-if="successMessage" class="success-banner" :class="{ 'success-banner-large': size === 'large' }">
      {{ successMessage }}
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
        {{ isSubmitting ? 'Creando cuenta...' : 'Crear Cuenta' }}
      </BaseButton>
    </div>

    <!-- Footer con link de login -->
    <div class="form-footer">
      <p class="login-link">
        ¿Ya tienes cuenta? 
        <BaseButton
          tag="router-link"
          to="/login"
          variant="secondary"
          size="small"
        >
          Inicia sesión aquí
        </BaseButton>
      </p>
    </div>
  </form>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useForm } from '@/composables/useForm'
import { useAuth } from '@/composables/useAuth'
import PasswordField from './PasswordField.vue'

export default {
  name: 'RegisterForm',
  
  components: {
    PasswordField
  },
  
  props: {
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['medium', 'large'].includes(value)
    },
    
    showPasswordStrength: {
      type: Boolean,
      default: true
    },
    
    showPasswordCriteria: {
      type: Boolean,
      default: true
    },
    
    requireSpecialChars: {
      type: Boolean,
      default: false
    },
    
    redirectTo: {
      type: String,
      default: '/verify-email'
    },
    
    showNotifications: {
      type: Boolean,
      default: true
    }
  },
  
  emits: ['success', 'error', 'show-terms', 'show-privacy'],
  
  setup(props, { emit }) {
    const router = useRouter()
    const { handleRegister } = useAuth()
    
    const successMessage = ref('')
    const passwordStrengthData = ref(null)
    
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
      {
        username: '',
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        acceptTerms: false
      },
      {
        validationRules: {
          username: [
            (value) => value.trim() ? null : 'El nombre de usuario es obligatorio',
            (value) => value.length >= 3 ? null : 'El nombre de usuario debe tener al menos 3 caracteres',
            (value) => /^[a-zA-Z0-9_]+$/.test(value) ? null : 'Solo se permiten letras, números y guiones bajos'
          ],
          firstName: [
            (value) => value.trim() ? null : 'El nombre es obligatorio',
            (value) => value.trim().length >= 2 ? null : 'El nombre debe tener al menos 2 caracteres'
          ],
          lastName: [
            (value) => value.trim() ? null : 'Los apellidos son obligatorios',
            (value) => value.trim().length >= 2 ? null : 'Los apellidos deben tener al menos 2 caracteres'
          ],
          email: [
            (value) => value.trim() ? null : 'El email es obligatorio',
            (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? null : 'El formato del email no es válido'
          ],
          password: [
            (value) => value ? null : 'La contraseña es obligatoria',
            (value) => value.length >= 6 ? null : 'La contraseña debe tener al menos 6 caracteres',
            (value) => {
              if (passwordStrengthData.value && !passwordStrengthData.value.isValid) {
                return 'La contraseña no cumple los criterios de seguridad'
              }
              return null
            }
          ],
          confirmPassword: [
            (value) => value ? null : 'Debes confirmar la contraseña',
            (value) => value === formData.password ? null : 'Las contraseñas no coinciden'
          ],
          acceptTerms: [
            (value) => value ? null : 'Debes aceptar los términos y condiciones'
          ]
        },
        showNotifications: props.showNotifications
      }
    )

    // Manejar fuerza de contraseña
    const handlePasswordStrength = (data) => {
      passwordStrengthData.value = data
      // Re-validar contraseña si es necesaria
      if (formData.password) {
        validateField('password')
      }
    }

    // Manejar envío del formulario
    const handleSubmit = async () => {
      const result = await submitForm(async (data) => {
        try {
          await handleRegister(data)
          successMessage.value = '¡Cuenta creada exitosamente! Redirigiendo...'
          emit('success', data)
          
          setTimeout(() => {
            router.push(props.redirectTo)
          }, 1500)
          
          return { success: true }
        } catch (error) {
          emit('error', error)
          throw error
        }
      })
    }

    // Mostrar términos y privacidad
    const showTerms = () => {
      emit('show-terms')
    }

    const showPrivacy = () => {
      emit('show-privacy')
    }

    return {
      formData,
      errors,
      isSubmitting,
      generalError,
      successMessage,
      canSubmit,
      validateField,
      clearFieldError,
      handleSubmit,
      handlePasswordStrength,
      showTerms,
      showPrivacy
    }
  }
}
</script>

<style scoped>
.register-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  width: 100%;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
}

.checkbox-group {
  margin-bottom: var(--space-md);
}

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

.login-link {
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
  .form-row {
    grid-template-columns: 1fr;
  }

  .login-link {
    flex-direction: column;
    gap: var(--space-sm);
  }
}
</style>