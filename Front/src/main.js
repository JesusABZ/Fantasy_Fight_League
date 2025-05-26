import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { pinia } from './store'

// Importar estilos
import './assets/styles/main.css'

// Importar componentes base
import BaseButton from './components/common/BaseButton.vue'
import BaseInput from './components/common/BaseInput.vue'
import BaseCard from './components/common/BaseCard.vue'

// Importar componentes de layout
import AppBackground from './components/layout/AppBackground.vue'
import AppHeader from './components/layout/AppHeader.vue'
import AppFooter from './components/layout/AppFooter.vue'

// Importar más componentes
import BaseModal from './components/common/BaseModal.vue'
import BaseNotification from './components/common/BaseNotification.vue'
import FormContainer from './components/forms/FormContainer.vue'

// Crear la aplicación
const app = createApp(App)

// Registrar componentes base globalmente
app.component('BaseButton', BaseButton)
app.component('BaseInput', BaseInput)
app.component('BaseCard', BaseCard)

// Registrar componentes de layout globalmente
app.component('AppBackground', AppBackground)
app.component('AppHeader', AppHeader)
app.component('AppFooter', AppFooter)

// Registrar más componentes
app.component('BaseModal', BaseModal)
app.component('BaseNotification', BaseNotification)
app.component('FormContainer', FormContainer)

// Usar plugins
app.use(pinia)
app.use(router)

// Montar la aplicación
app.mount('#app')