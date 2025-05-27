<template>
  <div class="verify-email">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="verify-container">
      <div class="verify-card">
        <!-- Icono de éxito -->
        <div class="success-icon">
          <div class="check-circle">
            <span class="check">✓</span>
          </div>
        </div>

        <!-- Título principal -->
        <h1 class="title-hero">¡CUENTA CREADA!</h1>
        
        <!-- Mensaje principal -->
        <div class="message-content">
          <h2 class="subtitle">Verificación de Email Requerida</h2>
          <p class="description">
            Tu cuenta ha sido creada exitosamente, pero necesitas verificar tu dirección de correo electrónico para poder iniciar sesión.
          </p>
          
          <!-- Email info -->
          <div class="email-info">
            <div class="email-icon">📧</div>
            <div class="email-text">
              <p><strong>Revisa tu bandeja de entrada</strong></p>
              <p class="email-detail">
                Hemos enviado un enlace de verificación a:
                <strong v-if="userEmail">{{ userEmail }}</strong>
              </p>
            </div>
          </div>

          <!-- Instrucciones -->
          <div class="instructions">
            <h3 class="instructions-title">¿Qué hacer ahora?</h3>
            <div class="steps">
              <div class="step">
                <span class="step-number">1</span>
                <span class="step-text">Revisa tu bandeja de entrada (y la carpeta de spam)</span>
              </div>
              <div class="step">
                <span class="step-number">2</span>
                <span class="step-text">Haz clic en el enlace de verificación del email</span>
              </div>
              <div class="step">
                <span class="step-number">3</span>
                <span class="step-text">¡Regresa aquí e inicia sesión!</span>
              </div>
            </div>
          </div>

          <!-- Acciones adicionales -->
          <div class="additional-actions">
            <p class="help-text">
              ¿No recibiste el email? 
              <button 
                class="link-button" 
                @click="resendEmail"
                :disabled="isResending || !userEmail"
              >
                <span v-if="isResending">Enviando...</span>
                <span v-else>Reenviar email de verificación</span>
              </button>
            </p>
            
            <!-- Mensaje de reenvío -->
            <div v-if="resendMessage" class="resend-message" :class="messageClass">
              {{ resendMessage }}
            </div>
          </div>
        </div>

        <!-- Botones de acción -->
        <div class="action-buttons">
          <button 
            class="btn btn-primary"
            @click="goToLogin"
          >
            Ir al Login
          </button>
          
          <button 
            class="btn btn-back"
            @click="goToHome"
          >
            Volver a la Home
          </button>
        </div>

        <!-- Footer informativo -->
        <div class="footer-info">
          <p class="security-note">
            🔒 Este paso es importante para la seguridad de tu cuenta
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authService } from '../api/authService.js'

export default {
  name: 'VerifyEmailView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // Estado local
    const isResending = ref(false)
    const resendMessage = ref('')
    const resendSuccess = ref(false)
    
    // Obtener email desde query params
    const userEmail = ref('')
    
    // Obtener el email de los query params si está disponible
    onMounted(() => {
      userEmail.value = route.query.email || ''
      console.log('📧 Email del usuario:', userEmail.value)
      
      // 🔍 VERIFICAR QUE authService TENGA EL MÉTODO
      console.log('🔍 Verificando authService:', authService)
      console.log('🔍 ¿Existe resendVerificationEmail?:', typeof authService.resendVerificationEmail)
    })
    
    // Clase CSS para el mensaje según el tipo
    const messageClass = computed(() => ({
      'success': resendSuccess.value,
      'error': !resendSuccess.value && resendMessage.value
    }))

    // Función para reenviar email usando authService
    const resendEmail = async () => {
      if (!userEmail.value) {
        resendMessage.value = '❌ No se pudo determinar la dirección de email'
        resendSuccess.value = false
        return
      }

      // 🔍 VERIFICAR EL MÉTODO ANTES DE USARLO
      if (typeof authService.resendVerificationEmail !== 'function') {
        console.error('❌ authService.resendVerificationEmail no es una función:', authService.resendVerificationEmail)
        resendMessage.value = '❌ Error: Método no disponible'
        resendSuccess.value = false
        return
      }

      isResending.value = true
      resendMessage.value = ''
      resendSuccess.value = false

      try {
        console.log('📤 Reenviando email de verificación a:', userEmail.value)
        console.log('🔧 Usando authService.resendVerificationEmail')
        
        // ✅ USAR authService
        const response = await authService.resendVerificationEmail(userEmail.value)
        
        console.log('✅ Respuesta exitosa:', response)
        
        resendMessage.value = '✅ Email de verificación reenviado correctamente'
        resendSuccess.value = true
        
        // Limpiar mensaje después de 8 segundos
        setTimeout(() => {
          resendMessage.value = ''
          resendSuccess.value = false
        }, 8000)
        
      } catch (err) {
        console.error('❌ Error al reenviar email:', err)
        resendMessage.value = err.message || '❌ Error al reenviar el email. Inténtalo de nuevo.'
        resendSuccess.value = false
        
        // Limpiar mensaje de error después de 8 segundos
        setTimeout(() => {
          resendMessage.value = ''
        }, 8000)
      } finally {
        isResending.value = false
      }
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

    return {
      userEmail,
      isResending,
      resendMessage,
      messageClass,
      resendEmail,
      goToLogin,
      goToHome
    }
  }
}
</script>

<style scoped>
.verify-email {
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
.verify-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: var(--space-xl) var(--space-lg);
}

.verify-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.2);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
  text-align: center;
}

/* === ICONO DE ÉXITO === */
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
  animation: pulse 2s infinite;
}

.check {
  font-size: 3rem;
  color: var(--white);
  font-weight: bold;
}

@keyframes pulse {
  0% {
    box-shadow: var(--shadow-lg), 0 0 0 0 rgba(255, 107, 53, 0.4);
  }
  70% {
    box-shadow: var(--shadow-lg), 0 0 0 20px rgba(255, 107, 53, 0);
  }
  100% {
    box-shadow: var(--shadow-lg), 0 0 0 0 rgba(255, 107, 53, 0);
  }
}

/* === CONTENIDO === */
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

/* === INFO DEL EMAIL === */
.email-info {
  background: rgba(255, 107, 53, 0.1);
  border: 1px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  margin: var(--space-xl) 0;
  display: flex;
  align-items: center;
  gap: var(--space-lg);
}

.email-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.email-text {
  text-align: left;
}

.email-text p {
  margin: 0;
  color: var(--white);
}

.email-detail {
  color: var(--gray-light) !important;
  font-size: 0.9rem;
  margin-top: var(--space-xs) !important;
}

/* === INSTRUCCIONES === */
.instructions {
  margin: var(--space-xl) 0;
  text-align: left;
}

.instructions-title {
  font-family: var(--font-impact);
  font-size: 1.4rem;
  font-weight: 400;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  letter-spacing: 0.02em;
  text-align: center;
}

.steps {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.step {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  transition: background 0.3s ease;
}

.step:hover {
  background: rgba(255, 255, 255, 0.08);
}

.step-number {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  color: var(--white);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.step-text {
  color: var(--gray-light);
  font-size: 1rem;
}

/* === ACCIONES ADICIONALES === */
.additional-actions {
  margin: var(--space-xl) 0;
}

.help-text {
  color: var(--gray-light);
  margin-bottom: var(--space-md);
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

.link-button:hover:not(:disabled) {
  color: var(--primary-light);
}

.link-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* === MENSAJES DE REENVÍO === */
.resend-message {
  margin-top: var(--space-md);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  animation: fadeIn 0.3s ease-in;
}

.resend-message.success {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid var(--success);
  color: var(--success);
}

.resend-message.error {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid var(--error);
  color: var(--error);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

.btn-back {
  background: transparent;
  color: var(--gray-light);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-1px);
}

/* === FOOTER === */
.footer-info {
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.security-note {
  color: var(--gray-light);
  font-size: 0.9rem;
  font-style: italic;
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .verify-container {
    padding: var(--space-lg) var(--space-md);
  }

  .verify-card {
    padding: var(--space-xl);
  }

  .title-hero {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.5rem;
  }

  .email-info {
    flex-direction: column;
    text-align: center;
  }

  .email-text {
    text-align: center;
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
  .verify-container {
    padding: var(--space-md);
  }

  .verify-card {
    padding: var(--space-lg);
  }

  .check-circle {
    width: 80px;
    height: 80px;
  }

  .check {
    font-size: 2.5rem;
  }
}
</style>