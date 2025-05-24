import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { pinia } from './store'

// Importar estilos
import './assets/main.css'

// Crear la aplicación
const app = createApp(App)

// Usar plugins
app.use(pinia)
app.use(router)

// Montar la aplicación
app.mount('#app')