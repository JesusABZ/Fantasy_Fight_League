<template>
  <div class="home">
    <!-- Fondo estático con overlay mejorado -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Contenido principal -->
    <div class="hero-section">
      <h2 class="title-section text-center">¡El futuro del fantasy está aquí!</h2>
      <p class="text-center hero-subtitle">
        Crea tu equipo de luchadores favoritos y compite en ligas épicas
      </p>
      
      <!-- Botones -->
      <div class="hero-buttons">
        <button @click="goToLogin" class="btn btn-primary">
          Iniciar Sesión
        </button>
        <button @click="goToRegister" class="btn btn-secondary">
          Crear Cuenta
        </button>
      </div>
    </div>

    <!-- Sección de características -->
    <div class="features-section">
      <div class="grid">
        <div class="card">
          <h3 class="title-card">Crea tu Liga</h3>
          <p class="card-text">
            Forma ligas públicas o privadas y compite contra otros fanáticos de la UFC
          </p>
        </div>
        
        <div class="card">
          <h3 class="title-card">Elige Luchadores</h3>
          <p class="card-text">
            Selecciona hasta 3 luchadores por evento con un presupuesto limitado
          </p>
        </div>
        
        <div class="card">
          <h3 class="title-card">Gana Puntos</h3>
          <p class="card-text">
            Obtén puntos basados en el rendimiento real de tus luchadores
          </p>
        </div>
      </div>
    </div>

    <!-- Sección del próximo evento UFC -->
    <div v-if="nextEvent" class="next-event-section">
      <h3 class="title-card text-center">Próximo Evento UFC</h3>
      <div class="event-card" @click="goToUFCEvents">
        <div class="event-image">
          <img 
            :src="nextEvent.imageUrl || '/images/ufc-default.jpg'" 
            :alt="nextEvent.name"
            @error="handleImageError"
          />
          <div class="event-overlay">
            <div class="event-badge">{{ eventBadge }}</div>
          </div>
        </div>
        <div class="event-content">
          <h4 class="event-title">{{ eventTitle }}</h4>
          <p class="event-subtitle">{{ nextEvent.description }}</p>
          <div class="event-date">
            <span class="date-text">{{ formattedDate }}</span>
            <span class="location-text">{{ nextEvent.location }}</span>
          </div>
          <button class="btn-event" @click.stop="goToUFCEvents">Ver Cartelera del Evento</button>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="isLoadingEvent" class="loading-section">
      <div class="loading-spinner">
        <span class="spinner"></span>
        <p>Cargando próximo evento...</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-if="eventError" class="error-section">
      <div class="error-message">
        <span class="error-icon">⚠️</span>
        <p>{{ eventError }}</p>
        <button @click="loadNextEvent" class="btn btn-secondary">Reintentar</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { eventsService } from '../api/index.js'
import { useDateFormatter } from '../composables/useDateFormatter.js'

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()
    const { formatEventDate } = useDateFormatter()

    // Estado reactivo
    const nextEvent = ref(null)
    const isLoadingEvent = ref(false)
    const eventError = ref(null)

    // Computed properties para procesar los datos del evento
    const eventBadge = computed(() => {
      if (!nextEvent.value?.name) return 'UFC'
      
      // Dividir el nombre por ":" y tomar la primera parte
      const parts = nextEvent.value.name.split(':')
      return parts[0].trim()
    })

    const eventTitle = computed(() => {
      if (!nextEvent.value?.name) return ''
      
      // Dividir el nombre por ":" y tomar la segunda parte
      const parts = nextEvent.value.name.split(':')
      return parts.length > 1 ? parts[1].trim() : parts[0].trim()
    })

    const formattedDate = computed(() => {
      if (!nextEvent.value?.startDate) return ''
      return formatEventDate(nextEvent.value.startDate)
    })

    // Función para cargar el próximo evento
    const loadNextEvent = async () => {
      isLoadingEvent.value = true
      eventError.value = null

      try {
        const event = await eventsService.getNextEvent()
        nextEvent.value = event
        
        if (!event) {
          console.log('No se encontraron eventos próximos')
        }
      } catch (error) {
        console.error('Error al cargar el próximo evento:', error)
        eventError.value = 'Error al cargar el próximo evento. Por favor, inténtalo de nuevo.'
      } finally {
        isLoadingEvent.value = false
      }
    }

    // Manejar error de imagen
    const handleImageError = (event) => {
      console.log('Error cargando imagen, usando imagen por defecto')
      event.target.src = '/images/ufc-default.jpg'
    }

    // Funciones para navegación
    const goToLogin = () => {
      router.push('/login')
    }

    const goToRegister = () => {
      router.push('/register')
    }

    const goToUFCEvents = () => {
      // Abrir la URL de UFC en una nueva pestaña
      window.open('https://www.ufc.com/events', '_blank')
    }

    // Cargar datos al montar el componente
    onMounted(() => {
      loadNextEvent()
    })

    return {
      // Estado
      nextEvent,
      isLoadingEvent,
      eventError,
      
      // Computed
      eventBadge,
      eventTitle,
      formattedDate,
      
      // Métodos
      loadNextEvent,
      handleImageError,
      goToLogin,
      goToRegister,
      goToUFCEvents
    }
  }
}
</script>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
}

/* === FONDO ESTÁTICO CON OVERLAY MEJORADO === */
.background-static {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/images/carrusel3.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  /* Filtro para oscurecer y reducir el contraste de la imagen */
  filter: brightness(0.3) contrast(0.8) grayscale(0.2);
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* Gradiente más intenso que se fusiona con el fondo oscuro */
  background: linear-gradient(
    135deg,
    rgba(10, 10, 10, 0.95) 0%,
    rgba(26, 26, 26, 0.85) 30%,
    rgba(42, 42, 42, 0.75) 50%,
    rgba(26, 26, 26, 0.85) 70%,
    rgba(10, 10, 10, 0.95) 100%
  );
  /* Overlay adicional para mantener la consistencia del tema */
  backdrop-filter: blur(1px);
}

/* Overlay adicional solo en los bordes para una mejor transición */
.background-static::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(ellipse at center, transparent 20%, rgba(10, 10, 10, 0.3) 70%),
    linear-gradient(to bottom, rgba(10, 10, 10, 0.6) 0%, transparent 20%, transparent 80%, rgba(10, 10, 10, 0.6) 100%);
  z-index: -1;
}

/* === HERO SECTION === */
.hero-section {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: var(--space-2xl) 0;
  margin-bottom: var(--space-lg); /* Reducido de 2xl a lg */
}

.hero-subtitle {
  font-size: 1.2rem;
  color: var(--gray-light);
  margin-bottom: var(--space-lg); /* Reducido de 2xl a lg */
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  /* Sombra de texto para mejor legibilidad */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.hero-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-lg);
  margin-bottom: var(--space-lg); /* Reducido de 2xl a lg */
}

/* Mejorar la legibilidad del título principal */
.title-section {
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.9);
}

/* === SECCIÓN DEL PRÓXIMO EVENTO UFC === */
.next-event-section {
  position: relative;
  z-index: 1;
  margin: var(--space-2xl) 0;
  padding: 0 var(--space-lg);
}

.event-card {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  box-shadow: var(--shadow-lg);
  max-width: 800px;
  margin: 0 auto;
}

.event-card:hover {
  transform: translateY(-8px);
  border-color: var(--primary);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.event-image {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.event-card:hover .event-image img {
  transform: scale(1.05);
}

.event-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    45deg,
    rgba(0,0,0,0.7) 0%,
    rgba(0,0,0,0.3) 50%,
    rgba(255,107,53,0.2) 100%
  );
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: var(--space-lg);
}

.event-badge {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-full);
  font-weight: 700;
  font-size: 0.9rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.event-content {
  padding: var(--space-xl);
  text-align: center;
}

.event-title {
  font-family: var(--font-impact); /* Aplicar Bebas Neue */
  font-size: 2rem;
  font-weight: 400; /* Bebas Neue no necesita font-weight alto */
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.event-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
  margin-bottom: var(--space-lg);
  line-height: 1.5;
}

.event-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-lg);
}

.date-text {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--primary);
}

.location-text {
  font-size: 1rem;
  color: var(--gray-light);
}

.btn-event {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
  padding: var(--space-sm) var(--space-xl);
  border-radius: var(--radius-lg);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.btn-event:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
}

/* === SECCIÓN DE CARACTERÍSTICAS === */
.features-section {
  position: relative;
  z-index: 1;
  margin-top: 0; /* Eliminado el margen superior */
  padding: var(--space-xl) 0;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-lg);
}

.card {
  /* Mantener el estilo original de las tarjetas sin cambios */
  background: var(--gradient-card);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.card-text {
  color: var(--gray-light);
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .event-image {
    height: 200px;
  }

  .event-title {
    font-size: 1.5rem; /* Responsive para móvil */
  }

  .event-content {
    padding: var(--space-lg);
  }

  .event-date {
    flex-direction: column;
    gap: var(--space-xs);
  }

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