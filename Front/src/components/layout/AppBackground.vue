<template>
  <div class="app-background" :class="backgroundClasses">
    <!-- Imagen de fondo -->
    <div class="background-image"></div>
    
    <!-- Overlay principal -->
    <div class="background-overlay"></div>
    
    <!-- Overlay adicional para mejores transiciones -->
    <div v-if="variant === 'enhanced'" class="background-overlay-extra"></div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'AppBackground',
  
  props: {
    // Variante del fondo
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'enhanced', 'simple'].includes(value)
    },
    
    // Imagen personalizada
    image: {
      type: String,
      default: '/images/carrusel3.jpg'
    },
    
    // Intensidad del overlay (0-1)
    overlayIntensity: {
      type: Number,
      default: 0.85,
      validator: (value) => value >= 0 && value <= 1
    },
    
    // Si el fondo es fijo o se desplaza
    fixed: {
      type: Boolean,
      default: true
    }
  },
  
  setup(props) {
    const backgroundClasses = computed(() => [
      `background-${props.variant}`,
      {
        'background-fixed': props.fixed,
        'background-scrollable': !props.fixed
      }
    ])
    
    return {
      backgroundClasses
    }
  }
}
</script>

<style scoped>
.app-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.background-scrollable {
  position: absolute;
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
  filter: brightness(0.3) contrast(0.8) grayscale(0.2);
}

/* Overlay principal */
.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(10, 10, 10, 0.95) 0%,
    rgba(26, 26, 26, 0.85) 30%,
    rgba(42, 42, 42, 0.75) 50%,
    rgba(26, 26, 26, 0.85) 70%,
    rgba(10, 10, 10, 0.95) 100%
  );
  backdrop-filter: blur(1px);
}

/* Overlay adicional para variant="enhanced" */
.background-overlay-extra {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(ellipse at center, transparent 20%, rgba(10, 10, 10, 0.3) 70%),
    linear-gradient(to bottom, rgba(10, 10, 10, 0.6) 0%, transparent 20%, transparent 80%, rgba(10, 10, 10, 0.6) 100%);
  z-index: 1;
}

/* Variantes */
.background-simple .background-overlay {
  background: rgba(10, 10, 10, 0.8);
}

.background-enhanced .background-image {
  filter: brightness(0.2) contrast(0.8) grayscale(0.3);
}
</style>