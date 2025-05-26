<template>
  <div class="background-static" :class="backgroundClasses">
    <!-- Imagen de fondo -->
    <div 
      class="background-image" 
      :class="imageClasses"
      :style="customImageStyle"
    ></div>
    
    <!-- Overlay principal -->
    <div 
      class="background-overlay" 
      :class="overlayClasses"
      :style="customOverlayStyle"
    ></div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'AppBackground',
  
  props: {
    // Variante predefinida del fondo
    variant: {
      type: String,
      default: 'default',
      validator: (value) => [
        'default', 'auth', 'profile', 'league', 'dashboard', 
        'enhanced', 'radial', 'custom'
      ].includes(value)
    },
    
    // Imagen personalizada (opcional)
    image: {
      type: String,
      default: ''
    },
    
    // Si el fondo es fijo o se desplaza
    fixed: {
      type: Boolean,
      default: true
    },
    
    // Intensidad del blur (opcional)
    blur: {
      type: String,
      default: '',
      validator: (value) => ['', 'light', 'heavy'].includes(value)
    },
    
    // Filtros personalizados para la imagen
    imageFilter: {
      type: String,
      default: ''
    },
    
    // Override del overlay (para casos especiales)
    customOverlay: {
      type: String,
      default: ''
    },
    
    // Animación de entrada
    fadeIn: {
      type: Boolean,
      default: false
    }
  },
  
  setup(props) {
    // Classes computadas para el contenedor principal
    const backgroundClasses = computed(() => [
      {
        'background-scrollable': !props.fixed,
        [`background-blur-${props.blur}`]: props.blur,
        'background-fade-in': props.fadeIn
      }
    ])
    
    // Classes computadas para la imagen
    const imageClasses = computed(() => [
      props.variant === 'custom' ? '' : `background-image--${props.variant}`
    ])
    
    // Classes computadas para el overlay
    const overlayClasses = computed(() => [
      props.variant === 'custom' ? '' : `background-overlay--${props.variant}`
    ])
    
    // Estilo personalizado para la imagen
    const customImageStyle = computed(() => {
      const styles = {}
      
      if (props.image) {
        styles.backgroundImage = `url('${props.image}')`
      }
      
      if (props.imageFilter) {
        styles.filter = props.imageFilter
      }
      
      return styles
    })
    
    // Estilo personalizado para el overlay
    const customOverlayStyle = computed(() => {
      const styles = {}
      
      if (props.customOverlay) {
        styles.background = props.customOverlay
      }
      
      return styles
    })
    
    return {
      backgroundClasses,
      imageClasses,
      overlayClasses,
      customImageStyle,
      customOverlayStyle
    }
  }
}
</script>

<style scoped>
/* Los estilos están en /assets/styles/layouts/backgrounds.css */
</style>