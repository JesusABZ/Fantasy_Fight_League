import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { pinia } from './store'
import { useAuthStore } from './store/auth.js'

// Importar estilos
import './assets/main.css'

// Función asíncrona para inicializar la aplicación
async function initializeApp() {
  console.log('🚀 Inicializando aplicación...')
  
  try {
    // Crear la aplicación
    const app = createApp(App)

    // Usar plugins
    app.use(pinia)
    app.use(router)

    // ✅ INICIALIZAR AUTENTICACIÓN con mejor manejo de errores
    const authStore = useAuthStore()
    
    console.log('🔐 Inicializando autenticación...')
    
    try {
      await authStore.initializeAuth()
      console.log('✅ Autenticación inicializada correctamente')
    } catch (authError) {
      console.error('❌ Error al inicializar autenticación:', authError)
      // No fallar la app por errores de auth, continuar sin usuario
    }

    // Event listener para cambios en localStorage desde otras pestañas
    window.addEventListener('storage', (event) => {
      if (event.key === 'ffl_token') {
        if (!event.newValue) {
          console.log('🔄 Token removido desde otra pestaña, actualizando estado...')
          authStore.user = null
          // Solo redirigir si estamos en una ruta protegida
          const currentRoute = router.currentRoute.value
          if (currentRoute.meta?.requiresAuth) {
            router.push('/')
          }
        }
      }
    })

    // Montar la aplicación
    app.mount('#app')
    console.log('✅ Aplicación montada correctamente')
    
  } catch (error) {
    console.error('💥 Error crítico al inicializar la aplicación:', error)
    throw error // Re-lanzar para que se maneje en el catch externo
  }
}

// Inicializar la aplicación
initializeApp().catch(error => {
  console.error('💥 Error al inicializar la aplicación:', error)
  
  // En caso de error crítico, mostrar mensaje básico
  document.body.innerHTML = `
    <div style="
      display: flex; 
      align-items: center; 
      justify-content: center; 
      height: 100vh; 
      font-family: Arial, sans-serif;
      background: #0a0a0a;
      color: white;
      text-align: center;
    ">
      <div>
        <h1>Error al cargar la aplicación</h1>
        <p>Por favor, recarga la página</p>
        <button onclick="window.location.reload()" style="
          background: #ff6b35; 
          color: white; 
          border: none; 
          padding: 10px 20px; 
          border-radius: 5px; 
          cursor: pointer;
          margin-top: 20px;
        ">
          Recargar
        </button>
      </div>
    </div>
  `
})