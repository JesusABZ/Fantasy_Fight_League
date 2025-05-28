// Configuración base para las llamadas al backend
const API_BASE_URL = 'http://localhost:8080/api'

// Configuración de axios (lo instalaremos después)
const apiConfig = {
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
}

// URLs de los endpoints de tu backend
export const endpoints = {
  // Auth
  auth: {
    login: '/auth/signin',
    register: '/auth/signup',
    logout: '/auth/logout',
    confirm: '/auth/confirm',
    resendVerification: '/auth/resend-verification', // 🆕 NUEVO endpoint
    forgotPassword: '/auth/forgot-password',
    validateResetToken: '/auth/validate-reset-token',
    resetPassword: '/auth/reset-password'
  },
  
  // User
  user: {
    profile: '/user/profile',
    updateProfile: '/user/profile',
    changePassword: '/user/change-password',
    changeEmail: '/user/change-email'
  },
  
  // Events
  events: {
    all: '/events',
    upcoming: '/events/upcoming',
    next: '/events/next', // 🆕 AÑADIDO endpoint del próximo evento
    byId: (id) => `/events/${id}`,
    fighters: (id) => `/events/${id}/fighters`
  },
  
  // Leagues
  leagues: {
    public: '/leagues/public',
    myLeagues: '/leagues/my-leagues',
    join: (id) => `/leagues/${id}/join`,
    joinPrivate: '/leagues/join-private',
    createPrivate: '/leagues/private'
  },
  
  // Picks
  picks: {
    create: '/picks',
    myPick: '/picks/my-pick',
    myPicks: (leagueId) => `/picks/my-picks/${leagueId}`,
    leaderboard: '/picks/leaderboard'
  },

  // 🆕 SOPORTE
  support: {
    ticket: '/support/ticket',
    categories: '/support/categories',
    status: '/support/status',
    contactInfo: '/support/contact-info'
  },

  // Leagues
  leagues: {
    public: '/leagues/public',
    myLeagues: '/leagues/my-leagues',
    join: (id) => `/leagues/${id}/join`,
    joinPrivate: '/leagues/join-private',
    createPrivate: '/leagues/private',
    leave: (id) => `/leagues/${id}/leave`,
    byId: (id) => `/leagues/${id}`
  },
  
  // Leaderboard
  leaderboard: {
    myPosition: (leagueId) => `/leaderboard/my-position/${leagueId}`,
    global: (leagueId) => `/leaderboard/global/${leagueId}`,
    event: (leagueId, eventId) => `/leaderboard/event/${leagueId}/${eventId}`,
    myHistory: (leagueId) => `/leaderboard/my-history/${leagueId}`
  },
  
  // Admin (solo para administradores)
  admin: {
    events: '/admin/events',
    fighters: '/admin/active-fighters',
    importFighters: '/admin/import-fighters'
  }
}

export { API_BASE_URL, apiConfig }