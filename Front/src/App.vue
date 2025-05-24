<template>
  <div id="app">
    <!-- Header temporal para probar -->
    <header class="app-header">
      <div class="container">
        <h1 class="title-hero">FANTASY FIGHT LEAGUE</h1>
        <p class="text-center" style="color: var(--gray-light); font-size: 1.2rem;">
          ¡Bienvenido a la plataforma de fantasy más emocionante de MMA!
        </p>
      </div>
    </header>

    <!-- Contenido principal -->
    <main class="app-main">
      <div class="container">
        <!-- Aquí irá el router cuando lo configuremos -->
        <RouterView />
      </div>
    </main>

    <!-- Footer temporal -->
    <footer class="app-footer">
      <div class="container">
        <p class="text-center" style="color: var(--gray-light);">
          © 2025 Fantasy Fight League - Desarrollado por Jesús Álvarez
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
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