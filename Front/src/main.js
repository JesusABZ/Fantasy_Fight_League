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
  
  // Crear la aplicación
  const app = createApp(App)

  // Usar plugins
  app.use(pinia)
  app.use(router)

  // ✅ CRÍTICO: Inicializar autenticación ANTES de montar la app
  const authStore = useAuthStore()
  
  console.log('🔐 Inicializando autenticación...')
  await authStore.initializeAuth()
  console.log('✅ Autenticación inicializada')

  // ✅ Agregar guard adicional para casos edge
  // Esto maneja casos donde el usuario manipula directamente el localStorage
  window.addEventListener('storage', (event) => {
    if (event.key === 'ffl_token') {
      if (!event.newValue) {
        // Token removido desde otra pestaña
        console.log('🔄 Token removido desde otra pestaña, limpiando estado...')
        authStore.user = null
        router.push('/')
      }
    }
  })

  // Montar la aplicación
  app.mount('#app')
  console.log('✅ Aplicación montada correctamente')
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