// composables/useForm.js
import { reactive, ref, computed } from 'vue'
import { useValidation } from './useValidation'
import { useNotifications } from './useNotifications'

export function useForm(initialData = {}, options = {}) {
  const {
    validationSchema = null,
    customValidations = {},
    showNotifications = true,
    resetOnSuccess = false,
    submitEndpoint = null,
    onSuccess = null,
    onError = null
  } = options

  // Composables
  const { errors, clearFieldError, clearAllErrors, schemas } = useValidation()
  const { common: notifications, handleApiError } = useNotifications()

  // Estado del formulario
  const formData = reactive({ ...initialData })
  const originalData = reactive({ ...initialData })
  
  // Estados de control
  const isSubmitting = ref(false)
  const isValidating = ref(false)
  const hasBeenSubmitted = ref(false)
  const generalError = ref('')
  const successMessage = ref('')

  // Computed properties
  const isValid = computed(() => Object.keys(errors).length === 0)
  
  const hasChanges = computed(() => {
    return JSON.stringify(formData) !== JSON.stringify(originalData)
  })
  
  const isFormReady = computed(() => {
    return isValid.value && !isSubmitting.value
  })

  const canSubmit = computed(() => {
    return isFormReady.value && hasChanges.value
  })

  // Métodos de validación
  const validateField = async (fieldName, showNotification = false) => {
    clearFieldError(fieldName)
    
    const value = formData[fieldName]
    let validationError = null

    // Validación con schema predefinido
    if (validationSchema && schemas[validationSchema]) {
      const tempData = { [fieldName]: value }
      const isValid = schemas[validationSchema](tempData)
      if (!isValid && errors[fieldName]) {
        validationError = errors[fieldName]
      }
    }

    // Validaciones personalizadas
    if (!validationError && customValidations[fieldName]) {
      try {
        const result = await customValidations[fieldName](value, formData)
        if (result !== true && typeof result === 'string') {
          validationError = result
          errors[fieldName] = result
        }
      } catch (error) {
        validationError = error.message
        errors[fieldName] = error.message
      }
    }

    if (showNotification && validationError && showNotifications) {
      notifications.error(validationError)
    }

    return !validationError
  }

  const validateForm = async (showNotification = true) => {
    isValidating.value = true
    clearAllErrors()
    generalError.value = ''

    try {
      // Validar con schema si existe
      if (validationSchema && schemas[validationSchema]) {
        const isSchemaValid = schemas[validationSchema](formData)
        if (!isSchemaValid) {
          if (showNotification && showNotifications) {
            notifications.formInvalid()
          }
          return false
        }
      }

      // Validar campos personalizados
      const fieldValidations = await Promise.all(
        Object.keys(customValidations).map(field => validateField(field, false))
      )

      const allValid = fieldValidations.every(result => result === true)
      
      if (!allValid && showNotification && showNotifications) {
        notifications.formInvalid()
      }

      return allValid
    } finally {
      isValidating.value = false
    }
  }

  // Métodos de datos
  const setFieldValue = (fieldName, value) => {
    formData[fieldName] = value
    
    // Limpiar error del campo al cambiar valor
    clearFieldError(fieldName)
  }

  const setData = (newData) => {
    Object.assign(formData, newData)
    clearAllErrors()
  }

  const resetForm = () => {
    Object.assign(formData, originalData)
    clearAllErrors()
    generalError.value = ''
    successMessage.value = ''
    hasBeenSubmitted.value = false
  }

  const updateOriginalData = (newData = null) => {
    const dataToUpdate = newData || formData
    Object.assign(originalData, dataToUpdate)
  }

  // Método de envío
  const submitForm = async (customSubmitFunction = null) => {
    hasBeenSubmitted.value = true
    
    // Validar antes de enviar
    const isFormValid = await validateForm()
    if (!isFormValid) {
      return { success: false, error: 'Formulario inválido' }
    }

    isSubmitting.value = true
    generalError.value = ''
    successMessage.value = ''

    try {
      let result

      // Usar función personalizada o endpoint por defecto
      if (customSubmitFunction) {
        result = await customSubmitFunction(formData)
      } else if (submitEndpoint) {
        // Aquí podrías integrar con tu servicio HTTP
        throw new Error('Endpoint submission no implementado todavía')
      } else {
        throw new Error('No se proporcionó función de envío ni endpoint')
      }

      // Manejar éxito
      if (showNotifications) {
        notifications.saveSuccess()
      }
      
      if (resetOnSuccess) {
        resetForm()
      } else {
        updateOriginalData()
      }

      if (onSuccess) {
        await onSuccess(result, formData)
      }

      return { success: true, data: result }

    } catch (error) {
      // Manejar error
      const errorMessage = error.message || 'Error al enviar el formulario'
      generalError.value = errorMessage

      if (showNotifications) {
        handleApiError(error, 'Error al guardar')
      }

      if (onError) {
        await onError(error, formData)
      }

      return { success: false, error: errorMessage }
    } finally {
      isSubmitting.value = false
    }
  }

  // Utilidades para formularios específicos
  const createLoginForm = () => {
    return useForm(
      { username: '', password: '' },
      {
        validationSchema: 'login',
        showNotifications: true
      }
    )
  }

  const createRegisterForm = () => {
    return useForm(
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
        validationSchema: 'register',
        showNotifications: true
      }
    )
  }

  const createProfileForm = (userData = {}) => {
    return useForm(
      {
        firstName: userData.firstName || '',
        lastName: userData.lastName || '',
        profileImageUrl: userData.profileImageUrl || null
      },
      {
        validationSchema: 'profile',
        showNotifications: true
      }
    )
  }

  const createPasswordForm = () => {
    return useForm(
      {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      {
        validationSchema: 'changePassword',
        showNotifications: true
      }
    )
  }

  const createEmailForm = (currentEmail = '') => {
    return useForm(
      {
        currentPassword: '',
        newEmail: ''
      },
      {
        validationSchema: 'changeEmail',
        customValidations: {
          newEmail: (value) => {
            if (value === currentEmail) {
              return 'El nuevo email debe ser diferente al actual'
            }
            return true
          }
        },
        showNotifications: true
      }
    )
  }

  const createSupportForm = () => {
    return useForm(
      {
        email: '',
        subject: '',
        message: ''
      },
      {
        validationSchema: 'support',
        showNotifications: true
      }
    )
  }

  return {
    // Estado del formulario
    formData,
    originalData,
    errors,
    
    // Estados de control
    isSubmitting,
    isValidating,
    hasBeenSubmitted,
    isValid,
    hasChanges,
    isFormReady,
    canSubmit,
    generalError,
    successMessage,
    
    // Métodos de validación
    validateField,
    validateForm,
    clearFieldError,
    clearAllErrors,
    
    // Métodos de datos
    setFieldValue,
    setData,
    resetForm,
    updateOriginalData,
    
    // Método de envío
    submitForm,
    
    // Utilidades para formularios específicos
    createLoginForm,
    createRegisterForm,
    createProfileForm,
    createPasswordForm,
    createEmailForm,
    createSupportForm
  }
}

// Funciones helper para formularios específicos
export const useLoginForm = () => useForm().createLoginForm()
export const useRegisterForm = () => useForm().createRegisterForm()
export const useProfileForm = (userData) => useForm().createProfileForm(userData)
export const usePasswordForm = () => useForm().createPasswordForm()
export const useEmailForm = (currentEmail) => useForm().createEmailForm(currentEmail)
export const useSupportForm = () => useForm().createSupportForm()