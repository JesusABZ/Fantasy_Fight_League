<template>
  <div id="app">
    <!-- Fondo de la aplicación -->
    <AppBackground variant="enhanced" />

    <!-- Header - Solo en páginas públicas -->
    <AppHeader>
      <template #actions>
        <!-- Aquí puedes agregar acciones del header si es necesario -->
      </template>
    </AppHeader>

    <!-- Contenido principal -->
    <main class="app-main" :class="{ 'no-header': !showPublicLayout }">
      <RouterView />
    </main>

    <!-- Footer - Solo en páginas públicas -->
    <AppFooter>
      <template #links>
        <!-- Aquí puedes agregar enlaces del footer si es necesario -->
      </template>
    </AppFooter>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const route = useRoute()
    
    // Rutas que usan el layout público (con header y footer)
    const publicRoutes = ['Home', 'Login', 'Register', 'VerifyEmail', 'ForgotPassword', 'EmailUnverified', 'Support', 'About']
    
    // Computed para determinar si mostrar el layout público
    const showPublicLayout = computed(() => {
      return publicRoutes.includes(route.name)
    })
    
    return {
      showPublicLayout
    }
  }
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.app-main {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: var(--space-2xl) 0;
}

.app-main.no-header {
  padding: 0;
}

/* Ajustes específicos para páginas sin layout público */
.no-header {
  min-height: 100vh;
}
</style>