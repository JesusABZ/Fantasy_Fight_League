// composables/useValidation.js
import { reactive } from 'vue'

export function useValidation() {
  const errors = reactive({})

  // Limpiar error de un campo específico
  const clearFieldError = (fieldName) => {
    if (errors[fieldName]) {
      delete errors[fieldName]
    }
  }

  // Limpiar todos los errores
  const clearAllErrors = () => {
    Object.keys(errors).forEach(key => delete errors[key])
  }

  // Reglas de validación comunes
  const rules = {
    required: (value, fieldName) => {
      if (!value || (typeof value === 'string' && !value.trim())) {
        return `${fieldName} es obligatorio`
      }
      return null
    },

    email: (value) => {
      if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return 'El formato del email no es válido'
      }
      return null
    },

    minLength: (value, min, fieldName) => {
      if (value && value.length < min) {
        return `${fieldName} debe tener al menos ${min} caracteres`
      }
      return null
    },

    maxLength: (value, max, fieldName) => {
      if (value && value.length > max) {
        return `${fieldName} no puede tener más de ${max} caracteres`
      }
      return null
    },

    password: (value) => {
      if (!value) return 'La contraseña es obligatoria'
      if (value.length < 6) return 'La contraseña debe tener al menos 6 caracteres'
      if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
        return 'La contraseña debe contener al menos una letra y un número'
      }
      return null
    },

    confirmPassword: (password, confirmPassword) => {
      if (!confirmPassword) return 'Debes confirmar la contraseña'
      if (password !== confirmPassword) return 'Las contraseñas no coinciden'
      return null
    },

    username: (value) => {
      if (!value?.trim()) return 'El usuario es obligatorio'
      if (value.length < 3) return 'El usuario debe tener al menos 3 caracteres'
      if (!/^[a-zA-Z0-9_]+$/.test(value)) {
        return 'Solo se permiten letras, números y guiones bajos'
      }
      return null
    },

    checkbox: (value, message = 'Debes aceptar este campo') => {
      if (!value) return message
      return null
    }
  }

  // Validar un campo con múltiples reglas
  const validateField = (fieldName, value, validationRules) => {
    clearFieldError(fieldName)

    for (const rule of validationRules) {
      let errorMessage = null

      if (typeof rule === 'function') {
        errorMessage = rule(value)
      } else if (typeof rule === 'object') {
        const { type, params = [], message } = rule
        if (rules[type]) {
          errorMessage = rules[type](value, ...params) || message
        }
      }

      if (errorMessage) {
        errors[fieldName] = errorMessage
        break
      }
    }
  }

  // Validar múltiples campos
  const validateFields = (fieldDefinitions) => {
    clearAllErrors()

    for (const [fieldName, { value, rules: fieldRules }] of Object.entries(fieldDefinitions)) {
      validateField(fieldName, value, fieldRules)
    }

    return Object.keys(errors).length === 0
  }

  // Esquemas de validación predefinidos
  const schemas = {
    login: (formData) => validateFields({
      username: {
        value: formData.username,
        rules: [rules.required, rules.minLength]
      },
      password: {
        value: formData.password,
        rules: [rules.required]
      }
    }),

    register: (formData) => validateFields({
      username: {
        value: formData.username,
        rules: [rules.username]
      },
      firstName: {
        value: formData.firstName,
        rules: [
          (value) => rules.required(value, 'El nombre'),
          (value) => rules.minLength(value, 2, 'El nombre')
        ]
      },
      lastName: {
        value: formData.lastName,
        rules: [
          (value) => rules.required(value, 'Los apellidos'),
          (value) => rules.minLength(value, 2, 'Los apellidos')
        ]
      },
      email: {
        value: formData.email,
        rules: [
          (value) => rules.required(value, 'El email'),
          rules.email
        ]
      },
      password: {
        value: formData.password,
        rules: [rules.password]
      },
      confirmPassword: {
        value: formData.confirmPassword,
        rules: [(value) => rules.confirmPassword(formData.password, value)]
      },
      acceptTerms: {
        value: formData.acceptTerms,
        rules: [(value) => rules.checkbox(value, 'Debes aceptar los términos y condiciones')]
      }
    }),

    changePassword: (formData) => validateFields({
      currentPassword: {
        value: formData.currentPassword,
        rules: [
          (value) => rules.required(value, 'La contraseña actual'),
          (value) => rules.minLength(value, 6, 'La contraseña')
        ]
      },
      newPassword: {
        value: formData.newPassword,
        rules: [rules.password]
      },
      confirmPassword: {
        value: formData.confirmPassword,
        rules: [(value) => rules.confirmPassword(formData.newPassword, value)]
      }
    }),

    changeEmail: (formData) => validateFields({
      currentPassword: {
        value: formData.currentPassword,
        rules: [
          (value) => rules.required(value, 'La contraseña actual'),
          (value) => rules.minLength(value, 6, 'La contraseña')
        ]
      },
      newEmail: {
        value: formData.newEmail,
        rules: [
          (value) => rules.required(value, 'El nuevo email'),
          rules.email
        ]
      }
    }),

    profile: (formData) => validateFields({
      firstName: {
        value: formData.firstName,
        rules: [
          (value) => rules.required(value, 'El nombre'),
          (value) => rules.minLength(value, 2, 'El nombre')
        ]
      },
      lastName: {
        value: formData.lastName,
        rules: [
          (value) => rules.required(value, 'Los apellidos'),
          (value) => rules.minLength(value, 2, 'Los apellidos')
        ]
      }
    }),

    support: (formData) => validateFields({
      email: {
        value: formData.email,
        rules: [
          (value) => rules.required(value, 'El email'),
          rules.email
        ]
      },
      subject: {
        value: formData.subject,
        rules: [
          (value) => rules.required(value, 'El asunto'),
          (value) => rules.maxLength(value, 200, 'El asunto')
        ]
      },
      message: {
        value: formData.message,
        rules: [
          (value) => rules.required(value, 'El mensaje'),
          (value) => rules.maxLength(value, 2000, 'El mensaje')
        ]
      }
    }),

    forgotPassword: (formData) => validateFields({
      email: {
        value: formData.email,
        rules: [
          (value) => rules.required(value, 'El email'),
          rules.email
        ]
      }
    })
  }

  return {
    errors,
    clearFieldError,
    clearAllErrors,
    validateField,
    validateFields,
    schemas,
    rules
  }
}