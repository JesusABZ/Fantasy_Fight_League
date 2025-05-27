<template>
  <div class="email-confirmation">
    <!-- Fondo estático -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="confirmation-container">
      <div class="confirmation-card">
        
        <!-- Estado de carga -->
        <div v-if="isLoading" class="loading-state">
          <div class="loading-spinner">
            <div class="spinner"></div>
          </div>
          <h2 class="loading-title">Verificando tu email...</h2>
          <p class="loading-description">Por favor espera mientras confirmamos tu dirección de correo electrónico.</p>
        </div>

        <!-- Estado de éxito -->
        <div v-else-if="isSuccess" class="success-state">
          <div class="success-icon">
            <div class="check-circle">
              <span class="check">✓</span>
            </div>
          </div>
          <h1 class="title-hero">¡EMAIL VERIFICADO!</h1>
          <h2 class="subtitle">Tu cuenta está lista</h2>
          <p class="description">
            ¡Excelente! Tu dirección de correo electrónico ha sido verificada correctamente. 
            Ahora puedes iniciar sesión y disfrutar de todas las funcionalidades de Fantasy Fight League.
          </p>
          
          <div class="action-buttons">
            <button 
              class="btn btn-primary btn-large"
              @click="goToLogin"
            >
              Iniciar Sesión
            </button>
            
            <button 
              class="btn btn-secondary"
              @click="goToHome"
            >
              Ir a la Home
            </button>
          </div>
        </div>

        <!-- Estado de error -->
        <div v-else class="error-state">
          <div class="error-icon">
            <div class="error-circle">
              <span class="error-x">✗</span>
            </div>
          </div>
          <h1 class="title-hero error-title">Error de Verificación</h1>
          <h2 class="subtitle">No pudimos verificar tu email</h2>
          <p class="description">
            {{ errorMessage }}
          </p>
          
          <!-- Detalles del error según el tipo -->
          <div class="error-details">
            <div v-if="errorType === 'expired'" class="error-suggestion">
              <h3>¿Qué puedes hacer?</h3>
              <ul>
                <li>Solicita un nuevo email de verificación</li>
                <li>Verifica que el enlace esté completo</li>
                <li>Contacta con soporte si el problema persiste</li>
              </ul>
            </div>
            
            <div v-else-if="errorType === 'invalid'" class="error-suggestion">
              <h3>¿Qué puedes hacer?</h3>
              <ul>
                <li>Verifica que hayas copiado el enlace completo</li>
                <li>Solicita un nuevo email de verificación</li>
                <li>Intenta acceder desde el email original</li>
              </ul>
            </div>
            
            <div v-else class="error-suggestion">
              <h3>¿Qué puedes hacer?</h3>
              <ul>
                <li>Intenta nuevamente en unos minutos</li>
                <li>Verifica tu conexión a internet</li>
                <li>Contacta con soporte técnico</li>
              </ul>
            </div>
          </div>
          
          <div class="action-buttons">
            <button 
              class="btn btn-primary"
              @click="goToVerifyEmail"
            >
              Solicitar Nuevo Email
            </button>
            
            <button 
              class="btn btn-secondary"
              @click="goToHome"
            >
              Volver a la Home
            </button>
          </div>
        </div>

        <!-- Footer -->
        <div class="footer-info">
          <p class="help-text">
            ¿Necesitas ayuda? 
            <router-link to="/support" class="link">Contacta con soporte</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

export default {
  name: 'EmailConfirmationView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const { confirmEmail, clearError } = useAuth()
    
    // Estado local
    const isLoading = ref(true)
    const isSuccess = ref(false)
    const errorMessage = ref('')
    const errorType = ref('')

    // Función para procesar la confirmación
    const processConfirmation = async () => {
      const token = route.query.token
      
      if (!token) {
        handleError('No se proporcionó un token de verificación válido.', 'invalid')
        return
      }

      console.log('Procesando confirmación con token:', token)

      try {
        // Limpiar errores previos
        clearError()
        
        // Llamar al servicio de confirmación
        const response = await confirmEmail(token)
        
        console.log('Confirmación exitosa:', response)
        
        // Marcar como exitoso
        isSuccess.value = true
        
      } catch (error) {
        console.error('Error en confirmación:', error)
        
        // Determinar el tipo de error y mensaje apropiado
        if (error.message.includes('expirado') || error.message.includes('expired')) {
          handleError('El enlace de verificación ha expirado. Por favor, solicita uno nuevo.', 'expired')
        } else if (error.message.includes('inválido') || error.message.includes('invalid')) {
          handleError('El enlace de verificación no es válido. Verifica que hayas copiado la URL completa.', 'invalid')
        } else {
          handleError(error.message || 'Ocurrió un error al verificar tu email. Inténtalo de nuevo.', 'general')
        }
      } finally {
        isLoading.value = false
      }
    }

    // Manejar errores
    const handleError = (message, type) => {
      isSuccess.value = false
      errorMessage.value = message
      errorType.value = type
      isLoading.value = false
    }

    // Navegación
    const goToLogin = () => {
      console.log('Navegando al login...')
      router.push('/login')
    }

    const goToHome = () => {
      console.log('Navegando a la home...')
      router.push('/')
    }

    const goToVerifyEmail = () => {
      console.log('Navegando a verificar email...')
      router.push('/verify-email')
    }

    // Ejecutar al montar el componente
    onMounted(() => {
      processConfirmation()
    })

    return {
      isLoading,
      isSuccess,
      errorMessage,
      errorType,
      goToLogin,
      goToHome,
      goToVerifyEmail
    }
  }
}
</script>

<style scoped>
.email-confirmation {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* === CONTENEDOR === */
.confirmation-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: var(--space-xl) var(--space-lg);
}

.confirmation-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
  text-align: center;
}

/* === ESTADO DE CARGA === */
.loading-state {
  padding: var(--space-xl) 0;
}

.loading-spinner {
  margin-bottom: var(--space-xl);
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 107, 53, 0.3);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.loading-description {
  color: var(--gray-light);
  font-size: 1.1rem;
  line-height: 1.6;
}

/* === ESTADO DE ÉXITO === */
.success-icon {
  margin-bottom: var(--space-xl);
}

.check-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: var(--shadow-lg), var(--shadow-glow);
  animation: bounce 0.6s ease-out;
}

.check {
  font-size: 3rem;
  color: var(--white);
  font-weight: bold;
}

@keyframes bounce {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* === ESTADO DE ERROR === */
.error-icon {
  margin-bottom: var(--space-xl);
}

.error-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--error) 0%, #dc2626 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: var(--shadow-lg), 0 0 20px rgba(239, 68, 68, 0.3);
  animation: shake 0.6s ease-out;
}

.error-x {
  font-size: 3rem;
  color: var(--white);
  font-weight: bold;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

.error-title {
  color: var(--error) !important;
  -webkit-text-fill-color: var(--error) !important;
  background: none !important;
}

/* === CONTENIDO GENERAL === */
.title-hero {
  font-family: var(--font-impact);
  font-size: 3rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.subtitle {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.description {
  color: var(--gray-light);
  font-size: 1.1rem;
  line-height: 1.6;
  margin-bottom: var(--space-xl);
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

/* === DETALLES DE ERROR === */
.error-details {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  margin: var(--space-xl) 0;
  text-align: left;
}

.error-suggestion h3 {
  color: var(--white);
  font-family: var(--font-impact);
  font-size: 1.2rem;
  font-weight: 400;
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.error-suggestion ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.error-suggestion li {
  color: var(--gray-light);
  padding: var(--space-sm) 0;
  padding-left: var(--space-lg);
  position: relative;
}

.error-suggestion li::before {
  content: '•';
  color: var(--error);
  font-weight: bold;
  position: absolute;
  left: 0;
}

/* === BOTONES === */
.action-buttons {
  display: flex;
  gap: var(--space-lg);
  justify-content: center;
  margin: var(--space-xl) 0;
  flex-wrap: wrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-md) var(--space-xl);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 48px;
  font-size: 1rem;
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

.btn-secondary {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-secondary:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

.btn-large {
  padding: var(--space-lg) var(--space-2xl);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* === FOOTER === */
.footer-info {
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.help-text {
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

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .confirmation-container {
    padding: var(--space-lg) var(--space-md);
  }

  .confirmation-card {
    padding: var(--space-xl);
  }

  .title-hero {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.5rem;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .btn {
    width: 100%;
    max-width: 280px;
  }
}

@media (max-width: 480px) {
  .confirmation-container {
    padding: var(--space-md);
  }

  .confirmation-card {
    padding: var(--space-lg);
  }

  .check-circle,
  .error-circle {
    width: 80px;
    height: 80px;
  }

  .check,
  .error-x {
    font-size: 2.5rem;
  }
}
</style>