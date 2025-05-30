// Archivo principal que exporta todos los servicios de la API
export { authService } from './authService.js'
export { userService } from './userService.js'
export { httpService, endpoints } from './http.js'
export { API_BASE_URL, apiConfig } from './config.js'


// Aquí iremos agregando más servicios cuando los creemos:
// export { eventsService } from './eventsService.js'
// export { leaguesService } from './leaguesService.js'
// export { picksService } from './picksService.js'
export { eventsService } from './eventsService.js'
export { supportService } from './supportService.js'
export { passwordResetService } from './passwordResetService.js'
export { leaguesService } from './leaguesService.js'
export { leaderboardService } from './leaderboardService.js'
export { leagueDetailService } from './LeagueDetailService.js'