<template>
  <div class="edit-profile">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Header -->
    <div class="profile-header">
      <div class="container">
        <div class="header-content">
          <button class="btn btn-back" @click="goBack">
            ← Volver
          </button>
          <h1 class="title-hero">EDITAR PERFIL</h1>
          <div class="header-spacer"></div>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner">
        <span class="spinner"></span>
        <p>Cargando datos del perfil...</p>
      </div>
    </div>

    <!-- Contenido principal -->
    <div v-else class="main-content">
      <div class="container">
        
        <!-- Formulario de edición -->
        <div class="edit-form-container">
          <form @submit.prevent="handleSubmit" class="edit-form">
            
            <!-- Sección de foto de perfil -->
            <div class="avatar-section">
              <h3 class="section-title">📷 Foto de Perfil</h3>
              
              <div class="avatar-upload-container">
                <div class="avatar-preview">
                  <img 
                    v-if="formData.profileImageUrl" 
                    :src="formData.profileImageUrl" 
                    :alt="fullName || currentUser.username"
                    class="avatar-image"
                    @error="handleImageError"
                  />
                  <span v-else class="avatar-initials">{{ userInitials }}</span>
                </div>
                
                <div class="upload-actions">
                  <div class="username-display">
                    <h4 class="current-username">{{ currentUser.username }}</h4>
                  </div>
                  
                  <input
                    type="file"
                    ref="fileInput"
                    accept="image/*"
                    @change="handleFileSelect"
                    class="file-input"
                    hidden
                  />
                  <button 
                    type="button" 
                    class="btn btn-upload"
                    @click="$refs.fileInput.click()"
                    :disabled="isUploading"
                  >
                    <span v-if="isUploading">📤 Subiendo...</span>
                    <span v-else>📁 Subir Nueva Foto</span>
                  </button>
                  <button 
                    v-if="formData.profileImageUrl"
                    type="button" 
                    class="btn btn-remove"
                    @click="removePhoto"
                    :disabled="isUploading"
                  >
                    🗑️ Eliminar Foto
                  </button>
                </div>
              </div>
            </div>

            <!-- Separador -->
            <div class="section-divider"></div>

            <!-- Información personal -->
            <div class="personal-section">
              <h3 class="section-title">👤 Información Personal</h3>
              
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
            </div>

            <!-- Mensaje de error general -->
            <div v-if="generalError" class="error-banner">
              {{ generalError }}
            </div>

            <!-- Botón de guardar -->
            <div class="form-actions">
              <button
                type="submit"
                class="btn btn-primary btn-large"
                :disabled="isSubmitting || !isFormValid || isUploading"
                :class="{ 'loading': isSubmitting, 'disabled': !isFormValid || isUploading }"
              >
                <span v-if="isSubmitting">💾 Guardando...</span>
                <span v-else-if="!hasChanges">✅ Sin Cambios</span>
                <span v-else>💾 Guardar Cambios</span>
              </button>
            </div>

            <!-- Separador -->
            <div class="section-divider"></div>

            <!-- Acciones adicionales -->
            <div class="additional-actions">
              <h3 class="section-title">🔐 Seguridad</h3>
              
              <div class="security-buttons">
                <button 
                  type="button" 
                  class="btn btn-security"
                  @click="goToChangeEmail"
                >
                  📧 Cambiar Email
                </button>
                
                <button 
                  type="button" 
                  class="btn btn-security"
                  @click="goToChangePassword"
                >
                  🔑 Cambiar Contraseña
                </button>
              </div>
            </div>

          </form>
        </div>

      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotification" class="notification" :class="notificationType" @click="hideNotification">
      <div class="notification-icon">
        <span v-if="notificationType === 'success'">✅</span>
        <span v-else-if="notificationType === 'warning'">⚠️</span>
        <span v-else>❌</span>
      </div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth.js'
import { userService } from '../api/index.js'
import { useNavigation } from '../composables/useNavigation.js'

export default {
  name: 'EditProfileView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const { goBackWithAuth } = useNavigation()

    // Estado del formulario (datos que se pueden editar)
    const formData = reactive({
      firstName: '',
      lastName: '',
      profileImageUrl: null
    })

    // Estado original para comparar cambios
    const originalData = reactive({})

    // Datos del usuario actual (incluye username que no se puede editar)
    const currentUser = reactive({
      username: '',
      email: ''
    })

    // Estado de errores
    const errors = reactive({})
    
    // Estado general
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const isUploading = ref(false)
    const generalError = ref('')
    const showNotification = ref(false)
    const notificationType = ref('success')
    const notificationText = ref('')

    // Computed properties
    const fullName = computed(() => {
      return `${formData.firstName} ${formData.lastName}`.trim()
    })

    const userInitials = computed(() => {
      const first = formData.firstName ? formData.firstName[0] : ''
      const last = formData.lastName ? formData.lastName[0] : ''
      const username = currentUser.username ? currentUser.username[0] : ''
      
      if (first && last) {
        return `${first}${last}`.toUpperCase()
      } else if (username) {
        return username.toUpperCase()
      }
      
      return 'U'
    })

    const hasChanges = computed(() => {
      return formData.firstName !== originalData.firstName ||
             formData.lastName !== originalData.lastName ||
             formData.profileImageUrl !== originalData.profileImageUrl
    })

    const isFormValid = computed(() => {
      return formData.firstName.trim() !== '' &&
             formData.lastName.trim() !== '' &&
             Object.keys(errors).length === 0
    })

    // Cargar datos del usuario al montar el componente
    onMounted(async () => {
      await loadUserData()
    })

    // Funciones
    const loadUserData = async () => {
      isLoading.value = true
      generalError.value = ''

      try {
        console.log('🔄 Cargando datos del usuario para edición...')
        
        // Obtener el perfil completo del usuario
        const userProfile = await userService.getProfile()
        console.log('✅ Datos del usuario cargados:', userProfile)

        // Cargar datos en el formulario
        formData.firstName = userProfile.firstName || ''
        formData.lastName = userProfile.lastName || ''
        formData.profileImageUrl = userProfile.profileImageUrl || null

        // Guardar datos originales para comparar cambios
        originalData.firstName = userProfile.firstName || ''
        originalData.lastName = userProfile.lastName || ''
        originalData.profileImageUrl = userProfile.profileImageUrl || null

        // Cargar datos del usuario que no se pueden editar
        currentUser.username = userProfile.username
        currentUser.email = userProfile.email

        console.log('✅ Formulario inicializado con datos del usuario')

      } catch (error) {
        console.error('❌ Error al cargar datos del usuario:', error)
        generalError.value = error.message || 'Error al cargar los datos del perfil'
        
        if (error.message.includes('401') || error.message.includes('unauthorized')) {
          showFloatingNotification('error', 'Sesión expirada. Redirigiendo al login...')
          setTimeout(() => {
            authStore.logout()
            router.push('/login')
          }, 2000)
        }
      } finally {
        isLoading.value = false
      }
    }

    const clearFieldError = (fieldName) => {
      if (errors[fieldName]) {
        delete errors[fieldName]
      }
    }

    const validateField = (fieldName) => {
      clearFieldError(fieldName)

      switch (fieldName) {
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
      }
    }

    const validateForm = () => {
      Object.keys(errors).forEach(key => delete errors[key])
      validateField('firstName')
      validateField('lastName')
      return Object.keys(errors).length === 0
    }

    const handleFileSelect = async (event) => {
      const file = event.target.files[0]
      if (file) {
        // Validaciones existentes...
        if (!file.type.startsWith('image/')) {
          showFloatingNotification('error', 'Por favor selecciona una imagen válida')
          return
        }

        if (file.size > 5 * 1024 * 1024) {
          showFloatingNotification('error', 'La imagen no puede ser mayor a 5MB')
          return
        }

        try {
          isSubmitting.value = true
          showFloatingNotification('info', 'Subiendo imagen...')
          
          console.log('📁 Subiendo archivo:', {
            name: file.name,
            size: file.size,
            type: file.type
          })
          
          // Subir la imagen al servidor
          const uploadResult = await userService.uploadProfileImage(file)
          
          // 🔍 DEBUG: Ver exactamente qué devuelve el servidor
          console.log('📋 Respuesta completa del servidor:', uploadResult)
          console.log('📋 Tipo de respuesta:', typeof uploadResult)
          console.log('📋 Keys de la respuesta:', Object.keys(uploadResult || {}))
          
          // Intentar diferentes posibles campos donde puede estar la URL
          const possibleUrls = [
            uploadResult?.imageUrl,
            uploadResult?.url,
            uploadResult?.profileImageUrl,
            uploadResult?.message, // A veces viene en el message
            uploadResult?.data?.url,
            uploadResult?.data?.imageUrl
          ]
          
          console.log('🔍 URLs posibles encontradas:', possibleUrls.filter(Boolean))
          
          // Buscar la primera URL válida
          const imageUrl = possibleUrls.find(url => url && typeof url === 'string' && url.length > 0)
          
          if (imageUrl) {
            formData.profileImageUrl = imageUrl
            console.log('✅ URL de imagen establecida:', imageUrl)
            showFloatingNotification('success', 'Imagen subida correctamente')
          } else {
            // Mostrar toda la respuesta para debugging
            console.error('❌ No se encontró URL en la respuesta:', uploadResult)
            throw new Error(`No se encontró URL de imagen. Respuesta del servidor: ${JSON.stringify(uploadResult)}`)
          }
          
        } catch (error) {
          console.error('💥 Error completo al subir imagen:', error)
          showFloatingNotification('error', error.message || 'Error al subir la imagen')
          
          if (event.target) {
            event.target.value = ''
          }
        } finally {
          isSubmitting.value = false
        }
      }
    }

    const removePhoto = () => {
      formData.profileImageUrl = null
      showFloatingNotification('success', 'Foto eliminada')
      
      // Limpiar el input file
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const fileInput = ref(null)

    const handleImageError = (event) => {
      console.warn('Error al cargar imagen:', event.target.src)
      // Ocultar imagen rota y mostrar iniciales
      event.target.style.display = 'none'
    }

    const handleSubmit = async () => {
      console.log('📝 Intentando guardar cambios del perfil...')
      
      generalError.value = ''

      if (!validateForm()) {
        console.warn('⚠️ Formulario no válido')
        return
      }

      if (!hasChanges.value) {
        showFloatingNotification('warning', 'No hay cambios que guardar')
        return
      }

      isSubmitting.value = true

      try {
        // Preparar datos para enviar
        const updateData = {
          firstName: formData.firstName.trim(),
          lastName: formData.lastName.trim(),
          profileImageUrl: formData.profileImageUrl === null ? null : formData.profileImageUrl
        }

        console.log('📤 Enviando datos de actualización:', updateData)
        
        // Llamar al servicio de actualización
        const updatedUser = await userService.updateProfile(updateData)
        console.log('✅ Perfil actualizado exitosamente:', updatedUser)
        
        // Actualizar datos originales para evitar detectar cambios fantasma
        Object.assign(originalData, formData)
        
        // Actualizar también el store de autenticación
        if (authStore.user) {
          authStore.user.firstName = updatedUser.firstName
          authStore.user.lastName = updatedUser.lastName
          authStore.user.profileImageUrl = updatedUser.profileImageUrl
        }
        
        showFloatingNotification('success', '¡Perfil actualizado correctamente!')
        // 🆕 NUEVO: Log para confirmar el estado final
        console.log('✅ Estado final del usuario en store:', authStore.user)
        
      } catch (error) {
        console.error('❌ Error al actualizar perfil:', error)
        
        if (error.message.includes('401') || error.message.includes('unauthorized')) {
          generalError.value = 'Sesión expirada. Por favor, inicia sesión nuevamente.'
          setTimeout(() => {
            authStore.logout()
            router.push('/login')
          }, 2000)
        } else {
          generalError.value = error.message || 'Error al actualizar el perfil. Inténtalo de nuevo.'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    const goBack = () => {
      // Si hay cambios sin guardar, preguntar antes de salir
      if (hasChanges.value) {
        const confirmLeave = window.confirm(
          '¿Estás seguro de que quieres salir? Tienes cambios sin guardar que se perderán.'
        )
        if (!confirmLeave) {
          return
        }
      }

      goBackWithAuth(authStore.isAuthenticated)
    }

    const goToChangeEmail = () => {
      if (hasChanges.value) {
        const confirmLeave = window.confirm(
          '¿Quieres cambiar de página? Tienes cambios sin guardar que se perderán.'
        )
        if (!confirmLeave) {
          return
        }
      }
      router.push('/profile/change-email')
    }

    const goToChangePassword = () => {
      if (hasChanges.value) {
        const confirmLeave = window.confirm(
          '¿Quieres cambiar de página? Tienes cambios sin guardar que se perderán.'
        )
        if (!confirmLeave) {
          return
        }
      }
      router.push('/profile/change-password')
    }

    const showFloatingNotification = (type, text) => {
      notificationType.value = type
      notificationText.value = text
      showNotification.value = true
      
      setTimeout(() => {
        hideNotification()
      }, 4000)
    }

    const hideNotification = () => {
      showNotification.value = false
    }

    return {
      // Estado
      currentUser,
      formData,
      errors,
      isLoading,
      isSubmitting,
      isUploading,
      generalError,
      isFormValid,
      hasChanges,
      showNotification,
      notificationType,
      notificationText,
      fullName,
      userInitials,
      fileInput,
      
      // Funciones
      handleSubmit,
      validateField,
      clearFieldError,
      handleFileSelect,
      removePhoto,
      handleImageError,
      goBack,
      goToChangeEmail,
      goToChangePassword,
      hideNotification
    }
  }
}
</script>

<style scoped>
.edit-profile {
  position: relative;
  min-height: 100vh;
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
  filter: brightness(0.2) contrast(0.8) grayscale(0.3);
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

/* === HEADER === */
.profile-header {
  position: relative;
  z-index: 1;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-spacer {
  width: 120px;
}

.title-hero {
  font-family: var(--font-impact);
  font-size: 2.5rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

/* === LOADING === */
.loading-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-lg);
  color: var(--gray-light);
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 107, 53, 0.3);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* === BOTONES === */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-decoration: none;
  gap: var(--space-sm);
}

.btn-back {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.edit-form-container {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

/* === SECCIONES === */
.section-title {
  font-family: var(--font-impact);
  font-size: 1.4rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.section-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 107, 53, 0.3) 50%,
    transparent 100%
  );
  margin: var(--space-2xl) 0;
}

/* === AVATAR SECTION === */
.avatar-section {
  margin-bottom: var(--space-xl);
}

.avatar-upload-container {
  display: flex;
  align-items: center;
  gap: var(--space-xl);
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg), var(--shadow-glow);
  flex-shrink: 0;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-initials {
  font-size: 3rem;
  font-weight: bold;
  color: var(--white);
  font-family: var(--font-impact);
}

.upload-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.username-display {
  margin-bottom: var(--space-sm);
}

.current-username {
  font-family: var(--font-impact);
  font-size: 1.9rem;
  color: var(--primary);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  text-align: center;
  text-shadow: 
    1px 1px 0 rgba(0, 0, 0, 1),
    2px 2px 4px rgba(0, 0, 0, 0.8),
    0 0 8px rgba(255, 107, 53, 0.3);
}

.btn-upload {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  padding: var(--space-md) var(--space-lg);
}

.btn-upload:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.btn-upload:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-remove {
  background: transparent;
  color: var(--error);
  border: 2px solid var(--error);
  padding: var(--space-sm) var(--space-lg);
}

.btn-remove:hover:not(:disabled) {
  background: var(--error);
  color: var(--white);
}

.btn-remove:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* === FORMULARIO === */
.edit-form {
  width: 100%;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
}

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

/* === ACCIONES === */
.form-actions {
  margin-bottom: var(--space-xl);
}

.btn-large {
  width: 100%;
  padding: var(--space-lg);
  font-size: 1.2rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-height: 60px;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--white);
  border: none;
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn.loading,
.btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* === ACCIONES ADICIONALES === */
.additional-actions {
  margin-top: var(--space-xl);
}

.security-buttons {
  display: flex;
  gap: var(--space-lg);
  flex-wrap: wrap;
}

.btn-security {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
  padding: var(--space-md) var(--space-xl);
  flex: 1;
  min-width: 200px;
}

.btn-security:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

/* === NOTIFICACIÓN === */
.notification {
  position: fixed;
  top: var(--space-xl);
  right: var(--space-xl);
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  z-index: 1000;
  cursor: pointer;
  transform: translateX(100%);
  animation: slideIn 0.3s ease forwards;
  max-width: 350px;
}

.notification.success {
  border: 2px solid var(--success);
}

.notification.error {
  border: 2px solid var(--error);
}

.notification.warning {
  border: 2px solid var(--warning);
}

@keyframes slideIn {
  to {
    transform: translateX(0);
  }
}

.notification-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notification-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-md);
  }

  .header-content {
    flex-direction: column;
    gap: var(--space-md);
  }

  .header-spacer {
    display: none;
  }

  .edit-form-container {
    padding: var(--space-lg);
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .avatar-upload-container {
    flex-direction: column;
    text-align: center;
    align-items: center;
  }

  .upload-actions {
    align-items: center;
  }

  .security-buttons {
    flex-direction: column;
  }

  .btn-security {
    min-width: auto;
  }

  .notification {
    top: var(--space-md);
    right: var(--space-md);
    left: var(--space-md);
    max-width: none;
  }
}

@media (max-width: 480px) {
  .title-hero {
    font-size: 2rem;
  }

  .edit-form-container {
    padding: var(--space-md);
  }

  .avatar-preview {
    width: 100px;
    height: 100px;
  }

  .avatar-initials {
    font-size: 2.5rem;
  }
}
</style>