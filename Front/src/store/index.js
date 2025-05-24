// Configuración principal de Pinia
import { createPinia } from 'pinia'

export const pinia = createPinia()

// Exportar todos los stores
export { useAuthStore } from './auth.js'
// Aquí iremos agregando más stores:
// export { useUserStore } from './user.js'
// export { useEventsStore } from './events.js'