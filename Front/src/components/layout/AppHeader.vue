<template>
  <header v-if="shouldShow" class="app-header" :class="headerClasses">
    <div class="header-container">
      <div class="header-content">
        
        <!-- Navegación izquierda -->
        <div v-if="hasLeftContent" class="header-nav" :class="navClasses">
          <!-- Botón volver -->
          <button 
            v-if="showBackButton"
            class="header-btn header-btn--back"
            @click="handleBack"
          >
            {{ backIcon }} {{ backText }}
          </button>
          
          <!-- Slot para navegación personalizada -->
          <slot name="nav-left" />
        </div>

        <!-- Contenido principal -->
        <div class="header-main" :class="mainClasses">
          <!-- Título principal -->
          <h1 v-if="title" class="header-title" :class="titleClasses">
            {{ title }}
          </h1>
          
          <!-- Subtítulo -->
          <p v-if="subtitle" class="header-subtitle" :class="subtitleClasses">
            {{ subtitle }}
          </p>
          
          <!-- Slot para contenido personalizado -->
          <slot name="title" />
          
          <!-- Metadata/badges -->
          <div v-if="hasMeta" class="header-meta" :class="metaClasses">
            <!-- Badges automáticos -->
            <span 
              v-for="badge in badges" 
              :key="badge.text"
              class="header-badge" 
              :class="`header-badge--${badge.type}`"
            >
              {{ badge.icon }} {{ badge.text }}
            </span>
            
            <!-- Slot para meta personalizada -->
            <slot name="meta" />
          </div>
        </div>

        <!-- Acciones derecha -->
        <div v-if="hasRightContent" class="header-actions" :class="actionsClasses">
          <!-- Información de usuario -->
          <div v-if="showUser && user" class="header-user">
            <div class="header-user-avatar">
              <img v-if="user.avatar" :src="user.avatar" :alt="user.name" />
              <span v-else class="header-user-initials">{{ userInitials }}</span>
            </div>
            <div v-if="showUserInfo" class="header-user-info">
              <span class="header-username">{{ user.name }}</span>
              <span v-if="user.subtitle" class="header-user-subtitle">{{ user.subtitle }}</span>
            </div>
          </div>
          
          <!-- Botones de acción -->
          <button 
            v-for="action in actions" 
            :key="action.text"
            class="header-btn" 
            :class="`header-btn--${action.variant}`"
            @click="handleAction(action)"
          >
            {{ action.icon }} {{ action.text }}
          </button>
          
          <!-- Slot para acciones personalizadas -->
          <slot name="actions" />
        </div>

      </div>
    </div>
  </header>
</template>

<script>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'AppHeader',
  
  props: {
    // Control de visibilidad
    show: {
      type: Boolean,
      default: null // null = automático
    },
    
    // Variante del header
    variant: {
      type: String,
      default: 'auto', // auto, public, app, page, league
      validator: (value) => ['auto', 'public', 'app', 'page', 'league'].includes(value)
    },
    
    // Contenido principal
    title: {
      type: String,
      default: ''
    },
    
    subtitle: {
      type: String,
      default: ''
    },
    
    // Tamaño del título
    titleSize: {
      type: String,
      default: 'large', // hero, large, medium, small
      validator: (value) => ['hero', 'large', 'medium', 'small'].includes(value)
    },
    
    // Navegación
    showBackButton: {
      type: Boolean,
      default: false
    },
    
    backText: {
      type: String,
      default: 'Volver'
    },
    
    backIcon: {
      type: String,
      default: '←'
    },
    
    backRoute: {
      type: String,
      default: ''
    },
    
    // Usuario
    showUser: {
      type: Boolean,
      default: false
    },
    
    showUserInfo: {
      type: Boolean,
      default: true
    },
    
    user: {
      type: Object,
      default: null
    },
    
    // Badges/metadata
    badges: {
      type: Array,
      default: () => []
    },
    
    // Acciones
    actions: {
      type: Array,
      default: () => []
    },
    
    // Layout
    centerTitle: {
      type: Boolean,
      default: true
    },
    
    centerMeta: {
      type: Boolean,
      default: true
    }
  },
  
  emits: ['back', 'action'],
  
  setup(props, { emit, slots }) {
    const route = useRoute()
    const router = useRouter()
    
    // Rutas públicas
    const publicRoutes = ['Home', 'Login', 'Register', 'VerifyEmail', 'ForgotPassword', 'EmailUnverified', 'Support', 'About']
    
    // Computed properties
    const shouldShow = computed(() => {
      if (props.show !== null) return props.show
      return publicRoutes.includes(route.name) || props.variant !== 'auto'
    })
    
    const headerVariant = computed(() => {
      if (props.variant !== 'auto') return props.variant
      return publicRoutes.includes(route.name) ? 'public' : 'app'
    })
    
    const headerClasses = computed(() => [
      `app-header--${headerVariant.value}`
    ])
    
    const hasLeftContent = computed(() => {
      return props.showBackButton || slots['nav-left']
    })
    
    const hasRightContent = computed(() => {
      return props.showUser || props.actions.length > 0 || slots.actions
    })
    
    const hasMeta = computed(() => {
      return props.badges.length > 0 || slots.meta
    })
    
    const navClasses = computed(() => [
      {
        'header-nav--centered': !hasRightContent.value
      }
    ])
    
    const mainClasses = computed(() => [
      {
        'header-main--left': !props.centerTitle && hasLeftContent.value,
        'header-main--right': !props.centerTitle && !hasLeftContent.value && hasRightContent.value
      }
    ])
    
    const titleClasses = computed(() => [
      `header-title--${props.titleSize}`
    ])
    
    const subtitleClasses = computed(() => [
      {
        'header-subtitle--large': props.titleSize === 'hero'
      }
    ])
    
    const metaClasses = computed(() => [
      {
        'header-meta--left': !props.centerMeta && hasLeftContent.value,
        'header-meta--right': !props.centerMeta && !hasLeftContent.value && hasRightContent.value
      }
    ])
    
    const actionsClasses = computed(() => [
      {
        'header-actions--centered': !hasLeftContent.value
      }
    ])
    
    const userInitials = computed(() => {
      if (!props.user?.name) return ''
      return props.user.name
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    })
    
    // Métodos
    const handleBack = () => {
      emit('back')
      
      // Si no se maneja, usar navegación automática
      if (!emit('back')) {
        if (props.backRoute) {
          router.push(props.backRoute)
        } else if (window.history.length > 1) {
          router.go(-1)
        } else {
          router.push('/')
        }
      }
    }
    
    const handleAction = (action) => {
      emit('action', action)
      
      if (action.handler) {
        action.handler()
      }
    }
    
    return {
      shouldShow,
      headerClasses,
      hasLeftContent,
      hasRightContent,
      hasMeta,
      navClasses,
      mainClasses,
      titleClasses,
      subtitleClasses,
      metaClasses,
      actionsClasses,
      userInitials,
      handleBack,
      handleAction
    }
  }
}
</script>

<style scoped>
/* Los estilos están en /assets/styles/layouts/header.css */
</style>