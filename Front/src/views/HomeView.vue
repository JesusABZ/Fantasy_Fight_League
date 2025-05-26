<template>
  <div class="home">
    <!-- Contenido principal -->
    <div class="hero-section">
      <h2 class="title-section text-center">¡El futuro del fantasy está aquí!</h2>
      <p class="text-center hero-subtitle">
        Crea tu equipo de luchadores favoritos y compite en ligas épicas
      </p>
      
      <!-- Botones usando BaseButton -->
      <div class="hero-buttons">
        <BaseButton
          variant="primary"
          size="large"
          @click="goToLogin"
        >
          Iniciar Sesión
        </BaseButton>
        
        <BaseButton
          variant="secondary"
          size="large"
          @click="goToRegister"
        >
          Crear Cuenta
        </BaseButton>
      </div>
    </div>

    <!-- Sección de características usando BaseCard -->
    <div class="features-section">
      <div class="cards-grid">
        <BaseCard
          variant="default"
          title="Crea tu Liga"
        >
          <p class="card-text">
            Forma ligas públicas o privadas y compite contra otros fanáticos de la UFC
          </p>
        </BaseCard>
        
        <BaseCard
          variant="default"
          title="Elige Luchadores"
        >
          <p class="card-text">
            Selecciona hasta 3 luchadores por evento con un presupuesto limitado
          </p>
        </BaseCard>
        
        <BaseCard
          variant="default"
          title="Gana Puntos"
        >
          <p class="card-text">
            Obtén puntos basados en el rendimiento real de tus luchadores
          </p>
        </BaseCard>
      </div>
    </div>

    <!-- Sección del próximo evento UFC -->
    <div class="next-event-section">
      <h3 class="title-card text-center">Próximo Evento UFC</h3>
      
      <BaseCard
        variant="featured"
        clickable
        @click="goToUFCEvents"
      >
        <div class="event-image">
          <img src="/images/ufc-event-vegas-107.jpg" alt="UFC Vegas 107" />
          <div class="event-overlay">
            <div class="event-badge">UFC VEGAS 107</div>
          </div>
        </div>
        <div class="event-content">
          <h4 class="event-title">BLANCHFIELD VS BARBER</h4>
          <p class="event-subtitle">El top de peso mosca femenil tendrá movimiento el 31 de mayo</p>
          <div class="event-date">
            <span class="date-text">31 de Mayo, 2025</span>
            <span class="location-text">Las Vegas, Nevada</span>
          </div>
          
          <BaseButton
            variant="event"
            @click="goToUFCEvents"
          >
            Ver Cartelera del Evento
          </BaseButton>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()

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

    return {
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
}

/* === HERO SECTION === */
.hero-section {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.hero-subtitle {
  font-size: 1.2rem;
  color: var(--gray-light);
  margin-bottom: var(--space-2xl);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
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
  margin-bottom: var(--space-2xl);
}

/* === PRÓXIMO EVENTO === */
.next-event-section {
  margin: var(--space-2xl) 0;
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
  font-family: var(--font-impact);
  font-size: 2rem;
  font-weight: 400;
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

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .event-image {
    height: 200px;
  }

  .event-title {
    font-size: 1.5rem;
  }

  .event-content {
    padding: var(--space-lg);
  }

  .hero-buttons {
    flex-direction: column;
    gap: var(--space-md);
  }
}
</style>