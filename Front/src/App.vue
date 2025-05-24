<template>
  <div id="app">
    <!-- Header solo para páginas públicas -->
    <header v-if="showPublicLayout" class="app-header">
      <div class="container">
        <h1 class="title-hero">FANTASY FIGHT LEAGUE</h1>
        <p class="text-center" style="color: var(--gray-light); font-size: 1.2rem;">
          ¡Bienvenido a la plataforma de fantasy más emocionante de MMA!
        </p>
      </div>
    </header>

    <!-- Contenido principal -->
    <main class="app-main" :class="{ 'no-header': !showPublicLayout }">
      <div v-if="showPublicLayout" class="container">
        <RouterView />
      </div>
      <div v-else>
        <!-- Sin container para vistas autenticadas que manejan su propio layout -->
        <RouterView />
      </div>
    </main>

    <!-- Footer solo para páginas públicas -->
    <footer v-if="showPublicLayout" class="app-footer">
      <div class="container">
        <p class="text-center" style="color: var(--gray-light);">
          © 2025 Fantasy Fight League - Desarrollado por Jesús Álvarez
        </p>
      </div>
    </footer>
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
.app-header {
  padding: var(--space-2xl) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.app-main {
  min-height: calc(100vh - 200px);
  padding: var(--space-2xl) 0;
}

.app-main.no-header {
  min-height: 100vh;
  padding: 0;
}

.app-footer {
  padding: var(--space-xl) 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: auto;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>