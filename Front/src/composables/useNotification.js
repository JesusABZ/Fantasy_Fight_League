// composables/useNotifications.js
import { ref } from 'vue'

export function useNotifications() {
  const showNotification = ref(false)
  const notificationType = ref('success')
  const notificationText = ref('')
  const notificationDuration = ref(3000)

  // Mostrar notificación
  const notify = (type, message, duration = 3000) => {
    notificationType.value = type
    notificationText.value = message
    notificationDuration.value = duration
    showNotification.value = true

    // Auto-ocultar después del tiempo especificado
    setTimeout(() => {
      hideNotification()
    }, duration)
  }

  // Ocultar notificación
  const hideNotification = () => {
    showNotification.value = false
  }

  // Métodos específicos para cada tipo
  const success = (message, duration) => notify('success', message, duration)
  const error = (message, duration) => notify('error', message, duration)
  const warning = (message, duration) => notify('warning', message, duration)
  const info = (message, duration) => notify('info', message, duration)

  // Notificaciones comunes predefinidas
  const common = {
    // Auth
    loginSuccess: () => success('¡Inicio de sesión exitoso!'),
    loginError: () => error('Credenciales incorrectas. Verifica tu usuario y contraseña.'),
    registerSuccess: () => success('¡Cuenta creada correctamente! Verifica tu email.'),
    registerError: () => error('Error al crear la cuenta. Inténtalo de nuevo.'),
    logoutSuccess: () => success('Sesión cerrada correctamente'),
    
    // Profile
    profileUpdated: () => success('¡Perfil actualizado correctamente!'),
    profileError: () => error('Error al actualizar el perfil. Inténtalo de nuevo.'),
    passwordChanged: () => success('¡Contraseña cambiada correctamente!'),
    passwordError: () => error('Error al cambiar la contraseña. Verifica tu contraseña actual.'),
    emailChanged: () => success('¡Email cambiado correctamente! Verifica tu nuevo email.'),
    emailError: () => error('Error al cambiar el email. Verifica tu contraseña.'),
    
    // Support
    supportSent: () => success('¡Mensaje enviado correctamente!'),
    supportError: () => error('Error al enviar el mensaje. Inténtalo de nuevo más tarde.'),
    
    // Email verification
    emailResent: (email) => success(`Email de verificación reenviado a ${email}`),
    emailResendError: () => error('Error al reenviar el email. Inténtalo de nuevo.'),
    
    // Picks/League
    picksSaved: () => success('¡Picks guardados correctamente!'),
    picksError: () => error('Error al guardar los picks. Inténtalo de nuevo.'),
    fighterAdded: (name) => success(`${name} agregado a tus picks`),
    fighterRemoved: (name) => info(`${name} removido de tus picks`),
    maxFightersReached: (max) => error(`Solo puedes seleccionar ${max} luchadores`),
    insufficientBudget: () => error('No tienes suficiente presupuesto para este luchador'),
    leagueJoined: (name) => success(`¡Te has unido a ${name}!`),
    leagueJoinError: () => error('Error al unirse a la liga. Inténtalo de nuevo.'),
    
    // General
    saveSuccess: () => success('¡Cambios guardados correctamente!'),
    saveError: () => error('Error al guardar. Inténtalo de nuevo.'),
    loadError: () => error('Error al cargar los datos. Recarga la página.'),
    networkError: () => error('Error de conexión. Verifica tu internet.'),
    sessionExpired: () => warning('Tu sesión ha expirado. Inicia sesión de nuevo.'),
    
    // Form validation
    formIncomplete: () => warning('Por favor completa todos los campos obligatorios'),
    formInvalid: () => error('Hay errores en el formulario. Revisa los campos marcados.'),
    
    // File upload
    fileTooBig: (maxSize) => error(`El archivo es muy grande. Máximo ${maxSize}MB.`),
    fileInvalidType: () => error('Tipo de archivo no válido. Solo se permiten imágenes.'),
    uploadSuccess: () => success('Archivo subido correctamente'),
    uploadError: () => error('Error al subir el archivo. Inténtalo de nuevo.'),
    
    // Generic actions
    deleteSuccess: (item) => success(`${item} eliminado correctamente`),
    deleteError: () => error('Error al eliminar. Inténtalo de nuevo.'),
    copySuccess: () => success('Copiado al portapapeles'),
    actionNotAllowed: () => warning('No tienes permisos para realizar esta acción')
  }

  // Handler para errores HTTP con mensajes específicos
  const handleApiError = (error, defaultMessage = 'Ha ocurrido un error') => {
    let message = defaultMessage

    if (error?.response?.status) {
      switch (error.response.status) {
        case 400:
          message = 'Datos inválidos. Revisa la información enviada.'
          break
        case 401:
          message = 'No estás autorizado. Inicia sesión de nuevo.'
          break
        case 403:
          message = 'No tienes permisos para realizar esta acción.'
          break
        case 404:
          message = 'Recurso no encontrado.'
          break
        case 422:
          message = error.response.data?.message || 'Datos de entrada inválidos.'
          break
        case 429:
          message = 'Demasiadas solicitudes. Inténtalo más tarde.'
          break
        case 500:
          message = 'Error del servidor. Inténtalo más tarde.'
          break
        case 503:
          message = 'Servicio no disponible. Inténtalo más tarde.'
          break
        default:
          message = error.response.data?.message || defaultMessage
      }
    } else if (error?.message) {
      message = error.message
    }

    error(message)
  }

  // Confirmar acción con notificación
  const confirmAction = async (
    message,
    confirmText = 'Confirmar',
    cancelText = 'Cancelar'
  ) => {
    return new Promise((resolve) => {
      // Aquí podrías integrar con un modal de confirmación
      // Por ahora usamos confirm nativo
      const confirmed = confirm(message)
      resolve(confirmed)
    })
  }

  return {
    // Estado
    showNotification,
    notificationType,
    notificationText,
    notificationDuration,
    
    // Métodos básicos
    notify,
    hideNotification,
    success,
    error,
    warning,
    info,
    
    // Notificaciones predefinidas
    common,
    
    // Utilidades
    handleApiError,
    confirmAction
  }
}