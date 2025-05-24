<template>
  <div class="email-unverified">
    <!-- Fondo estático con overlay -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenedor principal -->
    <div class="unverified-container">
      <div class="unverified-card">
        <!-- Icono de advertencia -->
        <div class="warning-icon">
          <div class="warning-circle">
            <span class="warning">⚠️</span>
          </div>
        </div>

        <!-- Título principal -->
        <h1 class="title-hero">¡ESPERA!</h1>
        
        <!-- Mensaje principal -->
        <div class="message-content">
          <h2 class="subtitle">Debes verificar tu email</h2>
          <p class="description">
            Tu cuenta existe pero no has verificado tu dirección de correo electrónico. 
            Por motivos de seguridad, necesitas confirmar tu email antes de poder iniciar sesión.
          </p>
          
          <!-- Info del usuario -->
          <div v-if="userEmail" class="user-info">
            <div class="user-icon">👤</div>
            <div class="user-details">
              <p><strong>Tu cuenta:</strong></p>
              <p class="user-email">{{ userEmail }}</p>
            </div>
          </div>

          <!-- Estado del email -->
          <div class="email-status">
            <div class="status-badge unverified">
              <span class="status-icon">❌</span>
              <span class="status-text">Email no verificado</span>
            </div>
          </div>

          <!-- Instrucciones -->
          <div class="instructions">
            <h3 class="instructions-title">¿Qué necesitas hacer?</h3>
            <div class="steps">
              <div class="step">
                <span class="step-number">1</span>
                <span class="step-text">Revisa tu bandeja de entrada y carpeta de spam</span>
              </div>
              <div class="step">
                <span class="step-number">2</span>
                <span class="step-text">Busca el email de verificación de Fantasy Fight League</span>
              </div>
              <div class="step">
                <span class="step-number">3</span>
                <span class="step-text">Haz clic en el enlace de verificación</span>
              </div>
              <div class="step">
                <span class="step-number">4</span>
                <span class="step-text">¡Regresa e inicia sesión normalmente!</span>
              </div>
            </div>
          </div>

          <!-- Acciones principales -->
          <div class="main-actions">
            <button 
              class="btn btn-primary btn-large"
              @click="resendVerificationEmail"
              :disabled="isResending"
            >
              <span v-if="isResending">Enviando...</span>
              <span v-else>📧 Reenviar Email de Verificación</span>
            </button>
          </div>

          <!-- Mensaje de reenvío -->
          <div v-if="resendMessage" class="resend-message" :class="resendSuccess ? 'success' : 'error'">
            {{ resendMessage }}
          </div>

          <!-- Opciones adicionales -->
          <div class="additional-actions">
            <div class="action-group">
              <p class="help-text">
                ¿Ya verificaste tu email? 
                <button 
                  type="button"
                  class="link-button" 
                  @click="tryLoginAgain"
                >
                  Intentar iniciar sesión de nuevo
                </button>
              </p>
            </div>

            <div class="action-group">
              <p class="help-text">
                ¿Problemas con el email? 
                <button 
                  type="button"
                  class="link-button" 
                  @click="contactSupport"
                >
                  Contactar soporte
                </button>
              </p>
            </div>

            <div class="action-group">
              <p class="help-text">
                ¿Email incorrecto? 
                <button 
                  type="button"
                  class="link-button" 
                  @click="createNewAccount"
                >
                  Crear nueva cuenta
                </button>
              </p>
            </div>
          </div>
        </div>

        <!-- Botones de navegación -->
        <div class="navigation-buttons">
          <button 
            class="btn btn-secondary"
            @click="goToLogin"
          >
            ← Volver al Login
          </button>
          
          <button 
            class="btn btn-back"
            @click="goToHome"
          >
            Ir a la Home
          </button>
        </div>

        <!-- Footer con información de seguridad -->
        <div class="security-footer">
          <div class="security-info">
            <span class="security-icon">🔒</span>
            <div class="security-text">
              <p><strong>¿Por qué verificamos emails?</strong></p>
              <p>La verificación de email nos ayuda a proteger tu cuenta y asegurar que puedas recuperar el acceso si olvidas tu contraseña.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'EmailUnverifiedView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const isResending = ref(false)
    const resendMessage = ref('')
    const resendSuccess = ref(false)
    const userEmail = ref('')

    // Obtener email del query parameter o storage
    onMounted(() => {
      // El email puede venir como query param desde el intento de login
      userEmail.value = route.query.email || 'tu correo registrado'
    })

    // Reenviar email de verificación
    const resendVerificationEmail = async () => {
      isResending.value = true
      resendMessage.value = ''
      resendSuccess.value = false

      try {
        // TODO: Conectar con el backend para reenviar email
        console.log('Reenviando email de verificación a:', userEmail.value)
        
        // Simular llamada al API
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        resendMessage.value = `✅ Email de verificación reenviado a ${userEmail.value}`
        resendSuccess.value = true
        
        // Limpiar mensaje después de 5 segundos
        setTimeout(() => {
          resendMessage.value = ''
        }, 5000)
        
      } catch (error) {
        resendMessage.value = '❌ Error al reenviar el email. Inténtalo de nuevo más tarde.'
        resendSuccess.value = false
      } finally {
        isResending.value = false
      }
    }

    // Intentar login de nuevo
    const tryLoginAgain = () => {
      console.log('Redirigiendo al login para intentar de nuevo')
      router.push('/login')
    }

    // Contactar soporte
    const contactSupport = () => {
      // TODO: Implementar sistema de soporte
      alert('Esta función se implementará próximamente. Por ahora puedes escribir a support@fantasyfightleague.com')
    }

    // Crear nueva cuenta
    const createNewAccount = () => {
      console.log('Redirigiendo al registro')
      router.push('/register')
    }

    // Navegación
    const goToLogin = () => {
      console.log('Volviendo al login')
      router.push('/login')
    }

    const goToHome = () => {
      console.log('Navegando a la home')
      router.push('/')
    }

    return {
      userEmail,
      isResending,
      resendMessage,
      resendSuccess,
      resendVerificationEmail,
      tryLoginAgain,
      contactSupport,
      createNewAccount,
      goToLogin,
      goToHome
    }
  }
}
</script>

<style scoped>
.email-unverified {
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
.unverified-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 750px;
  margin: 0 auto;
  padding: var(--space-xl) var(--space-lg);
}

.unverified-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 193, 7, 0.3);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-lg);
  text-align: center;
  position: relative;
}

/* === ICONO DE ADVERTENCIA === */
.warning-icon {
  margin-bottom: var(--space-xl);
}

.warning-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: var(--shadow-lg), 0 0 20px rgba(245, 158, 11, 0.3);
  animation: pulse-warning 2s infinite;
}

.warning {
  font-size: 3rem;
}

@keyframes pulse-warning {
  0% {
    box-shadow: var(--shadow-lg), 0 0 0 0 rgba(245, 158, 11, 0.4);
  }
  70% {
    box-shadow: var(--shadow-lg), 0 0 0 20px rgba(245, 158, 11, 0);
  }
  100% {
    box-shadow: var(--shadow-lg), 0 0 0 0 rgba(245, 158, 11, 0);
  }
}

/* === CONTENIDO === */
.title-hero {
  font-family: var(--font-impact);
  font-size: 3rem;
  font-weight: 400;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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
  max-width: 550px;
  margin-left: auto;
  margin-right: auto;
}

/* === INFO DEL USUARIO === */
.user-info {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  margin: var(--space-xl) 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-lg);
}

.user-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.user-details {
  text-align: left;
}

.user-details p {
  margin: 0;
  color: var(--white);
}

.user-email {
  color: var(--primary) !important;
  font-weight: 600;
  font-size: 1.1rem;
  word-break: break-all;
}

/* === ESTADO DEL EMAIL === */
.email-status {
  margin: var(--space-xl) 0;
  display: flex;
  justify-content: center;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-full);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-size: 0.9rem;
}

.status-badge.unverified {
  background: rgba(239, 68, 68, 0.2);
  border: 2px solid var(--error);
  color: var(--error);
}

.status-icon {
  font-size: 1.2rem;
}

/* === INSTRUCCIONES === */
.instructions {
  margin: var(--space-2xl) 0;
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
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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

/* === ACCIONES PRINCIPALES === */
.main-actions {
  margin: var(--space-2xl) 0;
}

.btn-large {
  width: 100%;
  max-width: 400px;
  padding: var(--space-lg);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 auto;
  display: block;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* === MENSAJE DE REENVÍO === */
.resend-message {
  margin: var(--space-lg) 0;
  padding: var(--space-md);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  font-weight: 500;
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

/* === ACCIONES ADICIONALES === */
.additional-actions {
  margin: var(--space-2xl) 0;
  padding: var(--space-lg) 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.action-group {
  margin-bottom: var(--space-lg);
}

.action-group:last-child {
  margin-bottom: 0;
}

.help-text {
  color: var(--gray-light);
  font-size: 0.9rem;
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

.link-button:hover {
  color: var(--primary-light);
}

/* === BOTONES DE NAVEGACIÓN === */
.navigation-buttons {
  display: flex;
  gap: var(--space-lg);
  justify-content: center;
  margin: var(--space-2xl) 0;
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

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-secondary {
  background: transparent;
  color: var(--warning);
  border: 2px solid var(--warning);
}

.btn-secondary:hover {
  background: var(--warning);
  color: var(--dark);
  transform: translateY(-2px);
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

/* === FOOTER DE SEGURIDAD === */
.security-footer {
  margin-top: var(--space-2xl);
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.security-info {
  display: flex;
  align-items: flex-start;
  gap: var(--space-md);
  text-align: left;
  background: rgba(255, 255, 255, 0.03);
  padding: var(--space-lg);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.security-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: var(--space-xs);
}

.security-text p {
  margin: 0;
  color: var(--gray-light);
  font-size: 0.9rem;
  line-height: 1.5;
}

.security-text p:first-child {
  color: var(--white);
  font-weight: 600;
  margin-bottom: var(--space-xs);
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .unverified-container {
    padding: var(--space-lg) var(--space-md);
  }

  .unverified-card {
    padding: var(--space-xl);
  }

  .title-hero {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.5rem;
  }

  .user-info {
    flex-direction: column;
    text-align: center;
  }

  .user-details {
    text-align: center;
  }

  .navigation-buttons {
    flex-direction: column;
    align-items: center;
  }

  .btn {
    width: 100%;
    max-width: 280px;
  }

  .security-info {
    flex-direction: column;
    text-align: center;
  }

  .security-text {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .unverified-container {
    padding: var(--space-md);
  }

  .unverified-card {
    padding: var(--space-lg);
  }

  .warning-circle {
    width: 80px;
    height: 80px;
  }

  .warning {
    font-size: 2.5rem;
  }
}
</style>