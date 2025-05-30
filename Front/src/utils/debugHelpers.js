// Utilidades para debugging durante el desarrollo

export const debugLeague = {
  // Logging detallado de datos de liga
  logLeagueData(league, context = 'League Data') {
    console.group(`🏆 ${context}`)
    console.log('Liga:', league)
    console.log('Tipo:', league?.type)
    console.log('Miembros:', league?.members?.length || league?.memberCount || 0)
    console.log('Presupuesto:', league?.initialBudget)
    console.groupEnd()
  },

  // Logging de clasificaciones
  logLeaderboard(leaderboard, context = 'Leaderboard') {
    console.group(`📊 ${context}`)
    console.log('Total participantes:', leaderboard?.length || 0)
    leaderboard?.slice(0, 3).forEach((member, index) => {
      console.log(`${index + 1}. ${member.username}: ${member.totalPoints || member.eventPoints || 0} pts`)
    })
    console.groupEnd()
  },

  // Validation de datos requeridos
  validateLeagueData(league) {
    const errors = []
    
    if (!league) {
      errors.push('Liga no definida')
      return errors
    }
    
    if (!league.id) errors.push('ID de liga faltante')
    if (!league.name) errors.push('Nombre de liga faltante')
    if (!league.type) errors.push('Tipo de liga faltante')
    if (!['PUBLIC', 'PRIVATE'].includes(league.type)) {
      errors.push('Tipo de liga inválido')
    }
    
    if (errors.length > 0) {
      console.error('❌ Errores en datos de liga:', errors)
    } else {
      console.log('✅ Datos de liga válidos')
    }
    
    return errors
  },

  // Helper para mock data durante desarrollo
  getMockLeague(type = 'PRIVATE') {
    return {
      id: 1,
      name: `Liga ${type === 'PUBLIC' ? 'Pública' : 'Privada'} Test`,
      type,
      description: 'Liga de prueba para desarrollo',
      memberCount: 5,
      initialBudget: 100000,
      maxFighters: 10,
      maxFightersEvent: 3,
      minFightersEvent: 1,
      createdAt: new Date().toISOString(),
      invitationCode: type === 'PRIVATE' ? 'TEST1234' : null,
      creator: {
        id: 1,
        username: 'test_user'
      },
      members: [
        { id: 1, username: 'usuario_prueba2' },
        { id: 2, username: 'fighter_pro' },
        { id: 3, username: 'mma_expert' }
      ]
    }
  },

  getMockLeaderboard() {
    return [
      { 
        id: 1, 
        username: 'usuario_prueba2', 
        totalPoints: 2890, 
        eventsParticipated: 10, 
        isCurrentUser: true 
      },
      { 
        id: 2, 
        username: 'fighter_pro', 
        totalPoints: 2750, 
        eventsParticipated: 10, 
        isCurrentUser: false 
      },
      { 
        id: 3, 
        username: 'mma_expert', 
        totalPoints: 2680, 
        eventsParticipated: 9, 
        isCurrentUser: false 
      }
    ]
  }
}

// Configuración de environment para debugging
export const isDevelopment = import.meta.env.MODE === 'development'

export const debugLog = (...args) => {
  if (isDevelopment) {
    console.log(...args)
  }
}

export const debugError = (...args) => {
  if (isDevelopment) {
    console.error(...args)
  }
}

export const debugWarn = (...args) => {
  if (isDevelopment) {
    console.warn(...args)
  }
}