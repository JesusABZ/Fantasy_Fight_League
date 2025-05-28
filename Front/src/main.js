import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { pinia } from './store'
import { useAuthStore } from './store/auth.js'

// Importar estilos
import './assets/main.css'

// Crear la aplicación
const app = createApp(App)

// Usar plugins
app.use(pinia)
app.use(router)

// Inicializar autenticación después de configurar Pinia
const authStore = useAuthStore()
authStore.initializeAuth()

// Montar la aplicación
app.mount('#app')