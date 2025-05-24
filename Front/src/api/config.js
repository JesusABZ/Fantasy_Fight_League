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
    confirm: '/auth/confirm'
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
  
  // Admin (solo para administradores)
  admin: {
    events: '/admin/events',
    fighters: '/admin/active-fighters',
    importFighters: '/admin/import-fighters'
  }
}

export { API_BASE_URL, apiConfig }