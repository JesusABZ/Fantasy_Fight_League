<template>
  <div class="home">
    <!-- Carrusel de fondo -->
    <div class="background-carousel">
      <div 
        v-for="(image, index) in carouselImages" 
        :key="index"
        class="carousel-slide"
        :class="{ active: currentSlide === index }"
        :style="{ backgroundImage: `url(${image})` }"
      ></div>
      <!-- Overlay para mantener la estética -->
      <div class="carousel-overlay"></div>
    </div>

    <!-- Contenido principal -->
    <div class="hero-section">
      <h2 class="title-section text-center">¡El futuro del fantasy está aquí!</h2>
      <p class="text-center hero-subtitle">
        Crea tu equipo de luchadores favoritos y compite en ligas épicas
      </p>
      
      <!-- Botones actualizados -->
      <div class="hero-buttons">
        <button @click="goToLogin" class="btn btn-primary">
          Iniciar Sesión
        </button>
        <button @click="goToRegister" class="btn btn-secondary">
          Crear Cuenta
        </button>
      </div>
    </div>

    <!-- Sección de características (mantenemos las tarjetas) -->
    <div class="features-section">
      <div class="grid">
        <div class="card">
          <h3 class="title-card">🏆 Crea tu Liga</h3>
          <p class="card-text">
            Forma ligas públicas o privadas y compite contra otros fanáticos de la UFC
          </p>
        </div>
        
        <div class="card">
          <h3 class="title-card">⚔️ Elige Luchadores</h3>
          <p class="card-text">
            Selecciona hasta 3 luchadores por evento con un presupuesto limitado
          </p>
        </div>
        
        <div class="card">
          <h3 class="title-card">📊 Gana Puntos</h3>
          <p class="card-text">
            Obtén puntos basados en el rendimiento real de tus luchadores
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'

export default {
  name: 'HomeView',
  setup() {
    const currentSlide = ref(0)
    let slideInterval = null

    // URLs de imágenes de UFC (puedes cambiarlas por las que prefieras)
    const carouselImages = [
      '/images/carrusel1.jpg',
      '/images/carrusel2.jpg',
      '/images/carrusel3.jpg'
    ]

    // Funciones para navegación (las implementaremos después)
    const goToLogin = () => {
      console.log('Ir a login')
      // TODO: Implementar navegación al login
    }

    const goToRegister = () => {
      console.log('Ir a registro')
      // TODO: Implementar navegación al registro
    }

    // Lógica del carrusel
    const nextSlide = () => {
      currentSlide.value = (currentSlide.value + 1) % carouselImages.length
    }

    onMounted(() => {
      // Iniciar el carrusel automático
      slideInterval = setInterval(nextSlide, 5000)
    })

    onUnmounted(() => {
      // Limpiar el intervalo al desmontar el componente
      if (slideInterval) {
        clearInterval(slideInterval)
      }
    })

    return {
      currentSlide,
      carouselImages,
      goToLogin,
      goToRegister
    }
  }
}
</script>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
}

/* === CARRUSEL DE FONDO === */
.background-carousel {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 1s ease-in-out;
}

.carousel-slide.active {
  opacity: 1;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(10, 10, 10, 0.85) 0%,
    rgba(26, 26, 26, 0.90) 50%,
    rgba(10, 10, 10, 0.85) 100%
  );
  z-index: -1;
}

/* === HERO SECTION === */
.hero-section {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: var(--space-2xl) 0;
  margin-bottom: var(--space-2xl);
}

.hero-subtitle {
  font-size: 1.2rem;
  color: var(--gray-light);
  margin-bottom: var(--space-2xl);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-lg);
  margin-bottom: var(--space-2xl);
}

/* === SECCIÓN DE CARACTERÍSTICAS === */
.features-section {
  position: relative;
  z-index: 1;
  margin-top: var(--space-2xl);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-lg);
}

.card-text {
  color: var(--gray-light);
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .hero-buttons {
    flex-direction: column;
    gap: var(--space-md);
  }

  .btn {
    width: 100%;
    max-width: 280px;
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>