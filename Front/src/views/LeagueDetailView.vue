<template>
  <div class="league-detail">
    <!-- Fondo estático -->
    <div class="background-static">
      <div class="background-image"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Loading Principal -->
    <div v-if="isLoadingLeague" class="main-loading">
      <div class="loading-container">
        <div class="loading-spinner-large">
          <div class="spinner"></div>
        </div>
        <h3 class="loading-title">Cargando Liga...</h3>
        <p class="loading-subtitle">Obteniendo datos de la liga</p>
      </div>
    </div>

    <!-- Contenido Principal -->
    <template v-else-if="currentLeague">
      <!-- Header de la Liga -->
      <div class="league-header">
        <div class="container">
          <div class="header-content">
            <button class="btn btn-back" @click="goBackToDashboard">
              ← Volver a Ligas
            </button>
            
            <div class="league-info">
              <h1 class="league-name">{{ currentLeague.name }}</h1>
              <div class="league-meta">
                <span class="league-type" :class="currentLeague.type.toLowerCase()">
                  {{ currentLeague.type === 'PUBLIC' ? '🌍 Pública' : '🔒 Privada' }}
                </span>
                <span class="member-count">👥 {{ currentLeague.memberCount || currentLeague.members?.length || 0 }} miembros</span>
              </div>
            </div>
            
            <div class="league-actions">
              <button class="btn btn-info" @click="showLeagueInfo = true">
                ℹ️ Info
              </button>
              <button 
                v-if="canLeaveLeague" 
                class="btn btn-leave" 
                @click="showLeaveLeagueConfirmation"
                :disabled="isLeavingLeague"
              >
                🚪 {{ isLeavingLeague ? 'Saliendo...' : 'Salir' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Navegación por pestañas -->
      <div class="tabs-navigation">
        <div class="container">
          <div class="tabs">
            <!-- Para ligas privadas: Global, Evento Actual, Evento Anterior -->
            <template v-if="isPrivateLeague">
              <button 
                class="tab-button" 
                :class="{ 'active': activeTab === 'global' }"
                @click="setActiveTab('global')"
              >
                🏆 Clasificación Global
              </button>
              <button 
                class="tab-button" 
                :class="{ 'active': activeTab === 'current' }"
                @click="setActiveTab('current')"
                v-if="currentEvent"
              >
                ⚡ Evento Actual
              </button>
              <button 
                class="tab-button" 
                :class="{ 'active': activeTab === 'previous' }"
                @click="setActiveTab('previous')"
              >
                📊 Evento Anterior
              </button>
            </template>
            
            <!-- Para ligas públicas: Solo Evento Actual -->
            <template v-else>
              <button 
                class="tab-button active"
                @click="setActiveTab('current')"
                v-if="currentEvent"
              >
                ⚡ {{ currentEvent.name }}
              </button>
            </template>
          </div>
        </div>
      </div>

      <!-- Contenido de las pestañas -->
      <div class="main-content">
        <div class="container">
          
          <!-- Pestaña: Clasificación Global (Solo para ligas privadas) -->
          <div v-if="activeTab === 'global' && isPrivateLeague" class="tab-content">
            <div class="content-header">
              <h2 class="content-title">🏆 Clasificación Global de la Liga</h2>
              <p class="content-subtitle">Puntuación acumulada de todos los eventos</p>
            </div>

            <div v-if="isLoadingGlobal" class="loading-section">
              <div class="loading-spinner">
                <span class="spinner"></span>
                <p>Cargando clasificación global...</p>
              </div>
            </div>

            <div v-else class="leaderboard-container">
              <div class="leaderboard">
                <div 
                  v-for="(member, index) in globalLeaderboard" 
                  :key="member.id"
                  class="leaderboard-item enhanced"
                  :class="{ 'is-you': member.isCurrentUser, 'top-three': index < 3 }"
                >
                  <!-- Posición y Medalla -->
                  <div class="position">
                    <span class="position-number" :class="getPositionClass(index + 1)">
                      #{{ index + 1 }}
                    </span>
                    <div v-if="index < 3" class="medal">
                      {{ index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉' }}
                    </div>
                  </div>
                  
                  <!-- Info del Miembro -->
                  <div class="member-info">
                    <div class="member-avatar">
                      <img 
                        v-if="member.profileImageUrl" 
                        :src="member.profileImageUrl" 
                        :alt="member.username"
                        class="avatar-image"
                        @error="handleImageError"
                      />
                      <span v-else class="avatar-initials">
                        {{ getUserInitials(member) }}
                      </span>
                    </div>
                    <div class="member-details">
                      <h4 class="member-name">
                        {{ member.username }}
                        <span v-if="member.isCurrentUser" class="you-badge">TÚ</span>
                      </h4>
                      <p class="member-subtitle">
                        <span v-if="member.firstName && member.lastName">
                          {{ member.firstName }} {{ member.lastName }}
                        </span>
                        <span v-else>{{ member.eventsParticipated || 0 }} eventos participados</span>
                      </p>
                    </div>
                  </div>
                  
                  <!-- Estadísticas Completas -->
                  <div class="member-stats enhanced">
                    <div class="stat-column">
                      <div class="stat-item primary">
                        <span class="stat-value">{{ member.totalPoints || 0 }}</span>
                        <span class="stat-label">Total Puntos</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.lastEventPoints || 0 }}</span>
                        <span class="stat-label">Último Evento</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.eventsParticipated || 0 }}</span>
                        <span class="stat-label">Eventos</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ formatAverage(member.averagePointsPerEvent) }}</span>
                        <span class="stat-label">Promedio</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Estado vacío -->
                <div v-if="globalLeaderboard.length === 0" class="empty-leaderboard">
                  <div class="empty-icon">🏆</div>
                  <h3 class="empty-title">No hay participantes aún</h3>
                  <p class="empty-description">Los miembros aparecerán cuando participen en eventos</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Pestaña: Evento Actual -->
          <div v-if="activeTab === 'current' && currentEvent" class="tab-content">
            <div class="content-header">
              <h2 class="content-title">⚡ {{ currentEvent.name }}</h2>
              <p class="content-subtitle">
                {{ formattedEventDate }} 
                <span v-if="currentEvent.location">• {{ currentEvent.location }}</span>
              </p>
              <div class="event-status">
                <span class="status-badge" :class="getEventStatusClass(currentEvent.status)">
                  {{ getEventStatusText(currentEvent.status) }}
                </span>
              </div>
            </div>

            <!-- Botón para elegir picks -->
            <div v-if="canMakePicks" class="picks-action-section">
              <div class="picks-call-to-action">
                <div class="cta-content">
                  <h3 class="cta-title">🎯 ¡Elige tu Lineup!</h3>
                  <p class="cta-description">
                    {{ currentUserPicks.length > 0 ? 'Modifica tu lineup antes de que comience el evento' : 'Selecciona hasta 3 luchadores para este evento' }}
                  </p>
                </div>
                <button class="btn btn-picks" @click="goToPicksSelection">
                  {{ currentUserPicks.length > 0 ? '✏️ Modificar Picks' : '⚔️ Elegir Luchadores' }}
                </button>
              </div>
            </div>

            <!-- Tu lineup para este evento -->
            <div v-if="currentUserPicks.length > 0" class="your-picks-section">
              <h3 class="section-title">👤 Tu Lineup para este Evento</h3>
              <div class="picks-grid">
                <div 
                  v-for="pick in currentUserPicks" 
                  :key="pick.id"
                  class="fighter-pick"
                  @click="showFighterDetails(pick)"
                >
                  <div class="fighter-avatar">
                    <img v-if="pick.imageUrl" :src="pick.imageUrl" :alt="pick.name" @error="handleImageError" />
                    <span v-else class="fighter-initials">{{ getFighterInitials(pick.name) }}</span>
                  </div>
                  <div class="fighter-info">
                    <h4 class="fighter-name">{{ pick.name }}</h4>
                    <p class="fighter-record">{{ pick.record || 'N/A' }}</p>
                    <div class="fighter-points">
                      <span class="points">{{ pick.points || 0 }} pts</span>
                      <span class="cost">${{ formatCurrency(pick.cost) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Clasificación del evento actual -->
            <div class="event-leaderboard-section">
              <h3 class="section-title">📊 Clasificación del Evento</h3>
              
              <div v-if="isLoadingCurrentEvent" class="loading-section">
                <div class="loading-spinner">
                  <span class="spinner"></span>
                  <p>Cargando clasificación del evento...</p>
                </div>
              </div>

              <div v-else class="leaderboard">
                <div 
                  v-for="(member, index) in currentEventLeaderboard" 
                  :key="member.id"
                  class="leaderboard-item event-item enhanced"
                  :class="{ 'is-you': member.isCurrentUser, 'top-three': index < 3 }"
                >
                  <!-- Posición -->
                  <div class="position">
                    <span class="position-number" :class="getPositionClass(index + 1)">
                      #{{ index + 1 }}
                    </span>
                    <div v-if="index < 3" class="medal">
                      {{ index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉' }}
                    </div>
                  </div>
                  
                  <!-- Info del Miembro -->
                  <div class="member-info">
                    <div class="member-avatar">
                      <img 
                        v-if="member.profileImageUrl" 
                        :src="member.profileImageUrl" 
                        :alt="member.username"
                        class="avatar-image"
                        @error="handleImageError"
                      />
                      <span v-else class="avatar-initials">
                        {{ getUserInitials(member) }}
                      </span>
                    </div>
                    <div class="member-details">
                      <h4 class="member-name">
                        {{ member.username }}
                        <span v-if="member.isCurrentUser" class="you-badge">TÚ</span>
                      </h4>
                      <p class="member-subtitle">
                        <span v-if="member.firstName && member.lastName">
                          {{ member.firstName }} {{ member.lastName }}
                        </span>
                        <span v-else>{{ member.fightersSelected || 0 }} luchadores seleccionados</span>
                      </p>
                    </div>
                  </div>
                  
                  <!-- Estadísticas del Evento -->
                  <div class="member-stats enhanced">
                    <div class="stat-column">
                      <div class="stat-item primary">
                        <span class="stat-value">{{ member.eventPoints || 0 }}</span>
                        <span class="stat-label">Puntos Evento</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.fightersSelected || 0 }}</span>
                        <span class="stat-label">Luchadores</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.totalCost || 0 }}</span>
                        <span class="stat-label">Costo Total</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.remainingBudget || 0 }}</span>
                        <span class="stat-label">Restante</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Estado vacío -->
                <div v-if="currentEventLeaderboard.length === 0" class="empty-leaderboard">
                  <div class="empty-icon">📊</div>
                  <h3 class="empty-title">No hay participantes aún</h3>
                  <p class="empty-description">Los participantes aparecerán cuando hagan sus picks</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Pestaña: Evento Anterior (Solo para ligas privadas) -->
          <div v-if="activeTab === 'previous' && isPrivateLeague" class="tab-content">
            <div class="content-header">
              <h2 class="content-title">📊 Evento Anterior</h2>
              <p class="content-subtitle" v-if="previousEvent">
                {{ formattedPreviousEventDate }}
                <span v-if="previousEvent.location">• {{ previousEvent.location }}</span>
              </p>
              <p class="content-subtitle" v-else>
                No hay eventos anteriores disponibles
              </p>
            </div>

            <div v-if="isLoadingPreviousEvent" class="loading-section">
              <div class="loading-spinner">
                <span class="spinner"></span>
                <p>Cargando evento anterior...</p>
              </div>
            </div>

            <div v-else-if="previousEvent" class="event-leaderboard-section">
              <div class="leaderboard">
                <div 
                  v-for="(member, index) in previousEventLeaderboard" 
                  :key="member.id"
                  class="leaderboard-item event-item enhanced"
                  :class="{ 'is-you': member.isCurrentUser, 'top-three': index < 3 }"
                >
                  <!-- Posición -->
                  <div class="position">
                    <span class="position-number" :class="getPositionClass(index + 1)">
                      #{{ index + 1 }}
                    </span>
                    <div v-if="index < 3" class="medal">
                      {{ index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉' }}
                    </div>
                  </div>
                  
                  <!-- Info del Miembro -->
                  <div class="member-info">
                    <div class="member-avatar">
                      <img 
                        v-if="member.profileImageUrl" 
                        :src="member.profileImageUrl" 
                        :alt="member.username"
                        class="avatar-image"
                        @error="handleImageError"
                      />
                      <span v-else class="avatar-initials">
                        {{ getUserInitials(member) }}
                      </span>
                    </div>
                    <div class="member-details">
                      <h4 class="member-name">
                        {{ member.username }}
                        <span v-if="member.isCurrentUser" class="you-badge">TÚ</span>
                      </h4>
                      <p class="member-subtitle">
                        <span v-if="member.firstName && member.lastName">
                          {{ member.firstName }} {{ member.lastName }}
                        </span>
                        <span v-else>{{ member.fightersSelected || 0 }} luchadores seleccionados</span>
                      </p>
                    </div>
                  </div>
                  
                  <!-- Estadísticas del Evento Anterior -->
                  <div class="member-stats enhanced">
                    <div class="stat-column">
                      <div class="stat-item primary">
                        <span class="stat-value">{{ member.eventPoints || 0 }}</span>
                        <span class="stat-label">Puntos Evento</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.fightersSelected || 0 }}</span>
                        <span class="stat-label">Luchadores</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.totalCost || 0 }}</span>
                        <span class="stat-label">Costo Total</span>
                      </div>
                    </div>
                    
                    <div class="stat-column">
                      <div class="stat-item">
                        <span class="stat-value">{{ member.remainingBudget || 0 }}</span>
                        <span class="stat-label">Restante</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Estado vacío para evento anterior -->
                <div v-if="previousEventLeaderboard.length === 0" class="empty-leaderboard">
                  <div class="empty-icon">📊</div>
                  <h3 class="empty-title">No hay datos del evento anterior</h3>
                  <p class="empty-description">No se encontraron participantes para el evento anterior</p>
                </div>
              </div>
            </div>

            <!-- No hay evento anterior -->
            <div v-else class="empty-leaderboard">
              <div class="empty-icon">📅</div>
              <h3 class="empty-title">No hay evento anterior</h3>
              <p class="empty-description">Esta es la primera participación de la liga o no se han completado eventos anteriores</p>
            </div>
          </div>

        </div>
      </div>
    </template>

    <!-- Error State -->
    <div v-else class="error-state">
      <div class="error-container">
        <div class="error-icon">❌</div>
        <h3 class="error-title">Error al cargar la liga</h3>
        <p class="error-description">No se pudieron obtener los datos de la liga</p>
        <button class="btn btn-retry" @click="loadAllData">
          🔄 Intentar de nuevo
        </button>
        <button class="btn btn-back" @click="goBackToDashboard">
          ← Volver al Dashboard
        </button>
      </div>
    </div>

    <!-- Modal: Confirmación para salir de la liga -->
    <div v-if="showLeaveConfirmation" class="modal-overlay" @click="hideLeaveConfirmation">
      <div class="modal-content confirmation-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">🚪 Salir de la Liga</h3>
          <button class="modal-close" @click="hideLeaveConfirmation">✕</button>
        </div>
        
        <div class="confirmation-content">
          <div class="warning-icon">⚠️</div>
          <h4 class="warning-title">¿Estás seguro?</h4>
          <p class="warning-message">
            Vas a salir de la liga "<strong>{{ currentLeague.name }}</strong>". 
            Perderás acceso a todas las clasificaciones y picks de esta liga.
          </p>
          
          <div class="confirmation-actions">
            <button 
              class="btn btn-cancel" 
              @click="hideLeaveConfirmation"
              :disabled="isLeavingLeague"
            >
              Cancelar
            </button>
            <button 
              class="btn btn-confirm-leave" 
              @click="leaveLeague"
              :disabled="isLeavingLeague"
            >
              {{ isLeavingLeague ? '⏳ Saliendo...' : '🚪 Sí, Salir' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Info de la Liga -->
    <div v-if="showLeagueInfo && currentLeague" class="modal-overlay" @click="showLeagueInfo = false">
      <div class="modal-content info-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">📋 Información de la Liga</h3>
          <button class="modal-close" @click="showLeagueInfo = false">✕</button>
        </div>
        
        <div class="league-info-content">
          <div class="info-section">
            <h4 class="info-title">Detalles de la Liga</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Nombre:</span>
                <span class="info-value">{{ currentLeague.name }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Tipo:</span>
                <span class="info-value">{{ currentLeague.type === 'PUBLIC' ? 'Pública' : 'Privada' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Miembros:</span>
                <span class="info-value">{{ currentLeague.memberCount || currentLeague.members?.length || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Creada:</span>
                <span class="info-value">{{ formatDate(currentLeague.createdAt) }}</span>
              </div>
              <!-- Código de invitación para ligas privadas -->
              <div class="info-item full-width" v-if="isPrivateLeague && currentLeague.invitationCode">
                <span class="info-label">Código de Invitación:</span>
                <div class="invitation-code-container">
                  <span class="invitation-code">{{ currentLeague.invitationCode }}</span>
                  <button class="btn btn-copy" @click="copyInvitationCode" title="Copiar código">
                    📋
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="info-section">
            <h4 class="info-title">Reglas</h4>
            <ul class="rules-list">
              <li>Presupuesto inicial: ${{ formatCurrency(currentLeague.initialBudget || 100000) }}</li>
              <li>Máximo {{ currentLeague.maxFightersEvent || 3 }} luchadores por evento</li>
              <li>Mínimo {{ currentLeague.minFightersEvent || 1 }} luchador por evento</li>
              <li>Los puntos se calculan automáticamente tras cada evento</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Detalles del Luchador -->
    <div v-if="selectedFighter" class="modal-overlay" @click="selectedFighter = null">
      <div class="modal-content fighter-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">{{ selectedFighter.name }}</h3>
          <button class="modal-close" @click="selectedFighter = null">✕</button>
        </div>
        
        <div class="fighter-details">
          <div class="fighter-image">
            <img v-if="selectedFighter.imageUrl" :src="selectedFighter.imageUrl" :alt="selectedFighter.name" @error="handleImageError" />
            <div v-else class="fighter-placeholder">
              {{ getFighterInitials(selectedFighter.name) }}
            </div>
          </div>
          
          <div class="fighter-basic-stats">
            <div class="stat-row">
              <span class="stat-label">Record:</span>
              <span class="stat-value">{{ selectedFighter.record || 'N/A' }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">Categoría:</span>
              <span class="stat-value">{{ selectedFighter.weightClass || 'N/A' }}</span>
            </div>
            <!-- 🆕 NUEVA: Nacionalidad -->
            <div class="stat-row">
              <span class="stat-label">Nacionalidad:</span>
              <span class="stat-value">🌍 {{ selectedFighter.nationality || 'Unknown' }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">Costo:</span>
              <span class="stat-value">${{ formatCurrency(selectedFighter.cost || selectedFighter.price || 0) }}</span>
            </div>
          </div>
          
          <div class="points-breakdown">
            <h4 class="breakdown-title">📊 Desglose de Puntos</h4>
            <div class="points-list">
              <div class="points-item">
                <span class="points-category">Victoria/Derrota:</span>
                <span class="points-value">{{ getWinLossPoints(selectedFighter) }} pts</span>
              </div>
              
              <!-- 🆕 NUEVAS: Estadísticas de golpes -->
              <div class="points-item">
                <span class="points-category">Golpes Significantes:</span>
                <span class="points-value">{{ getSignificantStrikesPoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ selectedFighter.significantStrikes || 0 }} golpes)</span>
              </div>
              
              <div class="points-item">
                <span class="points-category">Total de Golpes:</span>
                <span class="points-value">{{ getTotalStrikesPoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ selectedFighter.totalStrikes || 0 }} golpes)</span>
              </div>
              
              <div class="points-item">
                <span class="points-category">Takedowns:</span>
                <span class="points-value">{{ getTakedownPoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ selectedFighter.takedowns || 0 }} takedowns)</span>
              </div>
              
              <div class="points-item">
                <span class="points-category">Knockdowns:</span>
                <span class="points-value">{{ getKnockdownPoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ selectedFighter.knockdowns || 0 }} knockdowns)</span>
              </div>
              
              <div class="points-item">
                <span class="points-category">Sumisiones:</span>
                <span class="points-value">{{ getSubmissionPoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ selectedFighter.submissions || 0 }} intentos)</span>
              </div>
              
              <!-- 🆕 NUEVA: Minutos luchados -->
              <div class="points-item">
                <span class="points-category">Tiempo de Pelea:</span>
                <span class="points-value">{{ getTimePoints(selectedFighter) }} pts</span>
                <span class="points-detail">({{ formatFightTime(selectedFighter.minutesFought) }})</span>
              </div>
              
              <div class="points-item total">
                <span class="points-category">Total:</span>
                <span class="points-value">{{ selectedFighter.finalPoints || selectedFighter.points || 0 }} pts</span>
              </div>
            </div>
          </div>
          
          <!-- 🆕 NUEVA: Sección de estadísticas detalladas -->
          <div v-if="hasDetailedStats(selectedFighter)" class="detailed-stats">
            <h4 class="stats-title">📈 Estadísticas Detalladas</h4>
            <div class="stats-grid">
              <div class="stat-box">
                <div class="stat-number">{{ selectedFighter.significantStrikes || 0 }}</div>
                <div class="stat-name">Golpes Significantes</div>
              </div>
              <div class="stat-box">
                <div class="stat-number">{{ selectedFighter.totalStrikes || 0 }}</div>
                <div class="stat-name">Total Golpes</div>
              </div>
              <div class="stat-box">
                <div class="stat-number">{{ selectedFighter.takedowns || 0 }}</div>
                <div class="stat-name">Takedowns</div>
              </div>
              <div class="stat-box">
                <div class="stat-number">{{ selectedFighter.knockdowns || 0 }}</div>
                <div class="stat-name">Knockdowns</div>
              </div>
              <div class="stat-box">
                <div class="stat-number">{{ selectedFighter.submissions || 0 }}</div>
                <div class="stat-name">Sumisiones</div>
              </div>
              <div class="stat-box">
                <div class="stat-number">{{ formatFightTime(selectedFighter.minutesFought) }}</div>
                <div class="stat-name">Tiempo de Pelea</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Notificación flotante -->
    <div v-if="showNotificationModal" class="notification" @click="hideNotification">
      <div class="notification-icon">{{ notificationText.includes('Error') ? '❌' : '✅' }}</div>
      <div class="notification-text">{{ notificationText }}</div>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useLeagueDetail } from '../composables/useLeagueDetail.js'
import { useDateFormatter } from '../composables/useDateFormatter.js'

export default {
  name: 'LeagueDetailView',
  setup() {
    const route = useRoute()
    const leagueId = route.params.id
    const { formatEventDate } = useDateFormatter()
    
    // Usar el composable con la lógica
    const {
      // Estados
      currentLeague,
      currentEvent,
      previousEvent,
      globalLeaderboard,
      currentEventLeaderboard,
      previousEventLeaderboard,
      currentUserPicks,
      myPosition,
      
      // Estados de carga
      isLoadingLeague,
      isLoadingGlobal,
      isLoadingCurrentEvent,
      isLoadingPreviousEvent,
      isLeavingLeague,
      
      // Estados UI
      activeTab,
      selectedFighter,
      showLeagueInfo,
      showNotificationModal,
      notificationText,
      showLeaveConfirmation,
      
      // Computed
      isPublicLeague,
      isPrivateLeague,
      canMakePicks,
      canLeaveLeague,
      formattedEventDate,
      formattedPreviousEventDate,
      
      // Funciones
      loadAllData,
      refreshTabData,
      leaveLeague,
      copyInvitationCode,
      getPositionClass,
      getFighterInitials,
      getUserInitials,
      showFighterDetails,
      hideNotification,
      goBackToDashboard,
      goToPicksSelection,
      showLeaveLeagueConfirmation,
      hideLeaveConfirmation
    } = useLeagueDetail(leagueId)

    // Funciones adicionales del componente
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('es-ES', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        })
      } catch (error) {
        return 'N/A'
      }
    }
    
    // Funciones para calcular puntos por categoría
    const getWinLossPoints = (fighter) => {
      // Lógica basada en si ganó o perdió (20 pts por victoria, 0 por derrota)
      return fighter.won ? 20 : 0
    }

    const getSignificantStrikesPoints = (fighter) => {
      // 0.3 puntos por golpe significante
      const strikes = fighter.significantStrikes || 0
      return Math.round(strikes * 0.3)
    }

    const getTotalStrikesPoints = (fighter) => {
      // Los golpes totales no suelen dar puntos directos, pero podemos mostrar info
      return 0 // O implementar lógica específica si lo deseas
    }

    const getTakedownPoints = (fighter) => {
      // 3 puntos por takedown
      const takedowns = fighter.takedowns || 0
      return takedowns * 3
    }

    const getKnockdownPoints = (fighter) => {
      // 8 puntos por knockdown
      const knockdowns = fighter.knockdowns || 0
      return knockdowns * 8
    }

    const getSubmissionPoints = (fighter) => {
      // 2 puntos por intento de sumisión
      const submissions = fighter.submissions || 0
      return submissions * 2
    }

    const getTimePoints = (fighter) => {
      // Puntos por tiempo luchado (puedes ajustar la lógica)
      const minutes = fighter.minutesFought || 0
      return Math.round(minutes * 0.5) // 0.5 puntos por minuto
    }

    // Función para formatear el tiempo de pelea
    const formatFightTime = (minutes) => {
      if (!minutes || minutes === 0) return '0:00'
      
      const mins = Math.floor(minutes)
      const secs = Math.round((minutes - mins) * 60)
      
      return `${mins}:${secs.toString().padStart(2, '0')}`
    }

    // Función para verificar si tiene estadísticas detalladas
    const hasDetailedStats = (fighter) => {
      return fighter.significantStrikes || fighter.totalStrikes || fighter.takedowns || 
            fighter.knockdowns || fighter.submissions || fighter.minutesFought
    }


    const formatCurrency = (amount) => {
      if (!amount) return '0'
      return new Intl.NumberFormat('es-ES').format(amount)
    }

    const formatAverage = (average) => {
      if (!average || average === 0) return '0.0'
      return Number(average).toFixed(1)
    }

    const getEventStatusClass = (status) => {
      switch(status) {
        case 'UPCOMING': return 'upcoming'
        case 'LIVE': 
        case 'IN_PROGRESS': return 'current'
        case 'COMPLETED': return 'completed'
        default: return 'upcoming'
      }
    }

    const getEventStatusText = (status) => {
      switch(status) {
        case 'UPCOMING': return '⏳ Próximamente'
        case 'LIVE': 
        case 'IN_PROGRESS': return '🔴 En Vivo'
        case 'COMPLETED': return '✅ Finalizado'
        default: return '⏳ Próximamente'
      }
    }

    const setActiveTab = async (tabName) => {
      if (activeTab.value === tabName) return
      
      activeTab.value = tabName
      
      // Cargar datos de la pestaña si es necesario
      await refreshTabData(tabName)
    }

    // Manejo de errores de imágenes
    const handleImageError = (event) => {
      console.warn('Error cargando imagen:', event.target.src)
      event.target.style.display = 'none'
      // Mostrar el avatar con iniciales en su lugar
      const avatarContainer = event.target.parentElement
      if (avatarContainer && avatarContainer.querySelector('.avatar-initials')) {
        avatarContainer.querySelector('.avatar-initials').style.display = 'flex'
      }
    }

    // Cargar todos los datos al montar el componente
    onMounted(async () => {
      console.log('🚀 Montando LeagueDetailView para liga:', leagueId)
      
      if (!leagueId) {
        console.error('❌ No se proporcionó ID de liga')
        goBackToDashboard()
        return
      }
      
      try {
        await loadAllData()
      } catch (error) {
        console.error('💥 Error crítico al cargar la liga:', error)
      }
    })

    return {
      // Estados del composable
      currentLeague,
      currentEvent,
      previousEvent,
      globalLeaderboard,
      currentEventLeaderboard,
      previousEventLeaderboard,
      currentUserPicks,
      myPosition,

      // 🆕 Nuevas funciones para el modal
      getWinLossPoints,
      getSignificantStrikesPoints,
      getTotalStrikesPoints,
      getTakedownPoints,
      getKnockdownPoints,
      getSubmissionPoints,
      getTimePoints,
      formatFightTime,
      hasDetailedStats,
      
      // Estados de carga
      isLoadingLeague,
      isLoadingGlobal,
      isLoadingCurrentEvent,
      isLoadingPreviousEvent,
      isLeavingLeague,
      
      // Estados UI
      activeTab,
      selectedFighter,
      showLeagueInfo,
      showNotificationModal,
      notificationText,
      showLeaveConfirmation,
      
      // Computed
      isPublicLeague,
      isPrivateLeague,
      canMakePicks,
      canLeaveLeague,
      formattedEventDate,
      formattedPreviousEventDate,
      
      // Funciones del composable
      loadAllData,
      leaveLeague,
      copyInvitationCode,
      getPositionClass,
      getFighterInitials,
      getUserInitials,
      showFighterDetails,
      hideNotification,
      goBackToDashboard,
      goToPicksSelection,
      showLeaveLeagueConfirmation,
      hideLeaveConfirmation,
      
      // Funciones del componente
      formatDate,
      formatCurrency,
      formatAverage,
      getEventStatusClass,
      getEventStatusText,
      setActiveTab,
      handleImageError
    }
  }
}
</script>

<style scoped>
/* === VARIABLES Y RESET === */
.league-detail {
  position: relative;
  min-height: 100vh;
}

/* === FONDO === */
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
  filter: brightness(0.2) contrast(0.8) grayscale(0.3);
}

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

/* === LOADING Y ERROR STATES === */
.main-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--gradient-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-container {
  text-align: center;
  color: var(--white);
}

.loading-spinner-large {
  margin-bottom: var(--space-xl);
}

.loading-spinner-large .spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 107, 53, 0.3);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-title {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  margin-bottom: var(--space-sm);
  color: var(--primary);
  text-transform: uppercase;
}

.loading-subtitle {
  color: var(--gray-light);
  font-size: 1rem;
}

.error-state {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--gradient-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.error-container {
  text-align: center;
  color: var(--white);
  max-width: 500px;
  padding: var(--space-xl);
}

.error-icon {
  font-size: 4rem;
  margin-bottom: var(--space-xl);
}

.error-title {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  margin-bottom: var(--space-md);
  color: var(--error);
  text-transform: uppercase;
}

.error-description {
  color: var(--gray-light);
  font-size: 1rem;
  margin-bottom: var(--space-xl);
  line-height: 1.5;
}

/* === HEADER === */
.league-header {
  position: relative;
  z-index: 1;
  padding: var(--space-lg) 0;
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.league-info {
  flex: 1;
  text-align: center;
}

.league-name {
  font-family: var(--font-impact);
  font-size: 2.5rem;
  font-weight: 400;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  margin-bottom: var(--space-sm);
}

.league-meta {
  display: flex;
  justify-content: center;
  gap: var(--space-lg);
  flex-wrap: wrap;
}

.league-type,
.member-count {
  background: rgba(255, 255, 255, 0.1);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-full);
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--white);
}

.league-type.public {
  border: 1px solid var(--info);
  color: var(--info);
}

.league-type.private {
  border: 1px solid var(--warning);
  color: var(--warning);
}

.league-actions {
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

/* === BOTONES === */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-sm) var(--space-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-family: var(--font-primary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-decoration: none;
  gap: var(--space-sm);
}

.btn-back {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
}

.btn-back:hover {
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

.btn-info {
  background: transparent;
  border: 1px solid var(--info);
  color: var(--info);
}

.btn-info:hover {
  background: var(--info);
  color: var(--white);
}

.btn-leave {
  background: transparent;
  border: 2px solid var(--error);
  color: var(--error);
}

.btn-leave:hover:not(:disabled) {
  background: var(--error);
  color: var(--white);
  transform: translateY(-2px);
}

.btn-leave:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-picks {
  background: var(--gradient-primary);
  color: var(--white);
  padding: var(--space-lg) var(--space-xl);
  font-size: 1.1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  min-height: 50px;
}

.btn-picks:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.btn-retry {
  background: var(--gradient-primary);
  color: var(--white);
  margin-right: var(--space-md);
  margin-bottom: var(--space-md);
}

.btn-cancel {
  background: transparent;
  border: 2px solid var(--gray);
  color: var(--gray-light);
}

.btn-cancel:hover:not(:disabled) {
  border-color: var(--white);
  color: var(--white);
}

.btn-confirm-leave {
  background: var(--error);
  border: 2px solid var(--error);
  color: var(--white);
}

.btn-confirm-leave:hover:not(:disabled) {
  background: #dc2626;
  border-color: #dc2626;
  transform: translateY(-2px);
}

.btn-copy {
  background: var(--gradient-primary);
  border: none;
  color: var(--white);
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  font-size: 1rem;
  cursor: pointer;
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-copy:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 107, 53, 0.3);
}

/* === NAVEGACIÓN POR PESTAÑAS === */
.tabs-navigation {
  position: relative;
  z-index: 1;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 107, 53, 0.2);
}

.tabs {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
  padding: var(--space-md) 0;
}

.tab-button {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.2);
  color: var(--gray-light);
  padding: var(--space-md) var(--space-xl);
  border-radius: var(--radius-lg);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.tab-button:hover {
  border-color: rgba(255, 107, 53, 0.5);
  color: var(--white);
}

.tab-button.active {
  background: var(--gradient-primary);
  border-color: var(--primary);
  color: var(--white);
  box-shadow: var(--shadow-md);
}

/* === CONTENIDO PRINCIPAL === */
.main-content {
  position: relative;
  z-index: 1;
  padding: var(--space-xl) 0;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.content-header {
  text-align: center;
  margin-bottom: var(--space-2xl);
}

.content-title {
  font-family: var(--font-impact);
  font-size: 2rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.content-subtitle {
  color: var(--gray-light);
  font-size: 1.1rem;
  margin-bottom: var(--space-md);
}

.event-status {
  display: flex;
  justify-content: center;
}

.status-badge {
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-full);
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.status-badge.upcoming {
  background: rgba(59, 130, 246, 0.2);
  border: 1px solid var(--info);
  color: var(--info);
}

.status-badge.current {
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid var(--error);
  color: var(--error);
  animation: pulse 2s infinite;
}

.status-badge.completed {
  background: rgba(16, 185, 129, 0.2);
  border: 1px solid var(--success);
  color: var(--success);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

/* === SECCIÓN DE PICKS === */
.picks-action-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  margin-bottom: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.picks-call-to-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-lg);
}

.cta-content {
  flex: 1;
}

.cta-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.cta-description {
  color: var(--gray-light);
  font-size: 1rem;
  line-height: 1.5;
}

/* === CLASIFICACIONES === */
.leaderboard-container,
.event-leaderboard-section,
.your-picks-section {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  margin-bottom: var(--space-2xl);
  box-shadow: var(--shadow-lg);
}

.your-picks-section {
  border: 2px solid rgba(255, 107, 53, 0.3);
}

.section-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.leaderboard {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.leaderboard-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  transition: all 0.3s ease;
}

.leaderboard-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.leaderboard-item.enhanced {
  padding: var(--space-lg) var(--space-xl);
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.08) 0%, 
    rgba(255, 255, 255, 0.04) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.leaderboard-item.enhanced:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 
    var(--shadow-lg), 
    0 0 30px rgba(255, 107, 53, 0.15);
  border-color: rgba(255, 107, 53, 0.4);
}

.leaderboard-item.is-you {
  border-color: var(--primary);
  background: rgba(255, 107, 53, 0.1);
}

.leaderboard-item.top-three {
  border-color: var(--warning);
  background: rgba(245, 158, 11, 0.1);
}

.position {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  min-width: 80px;
}

.position-number {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--white);
}

.position-number.gold { color: #ffd700; }
.position-number.silver { color: #c0c0c0; }
.position-number.bronze { color: #cd7f32; }

.medal {
  font-size: 1.5rem;
}

.member-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex: 1;
}

.member-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  border: 3px solid transparent;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-initials {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  font-weight: bold;
}

.member-details {
  flex: 1;
}

.member-name {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.you-badge {
  background: var(--gradient-primary);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: bold;
}

.member-subtitle {
  color: var(--gray-light);
  font-size: 0.9rem;
}

.member-stats {
  text-align: right;
}

.member-stats.enhanced {
  display: flex;
  gap: var(--space-lg);
  align-items: center;
}

.stat-column {
  text-align: center;
  min-width: 80px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.stat-item.primary .stat-value {
  font-family: var(--font-impact);
  font-size: 1.8rem;
  color: var(--primary);
  line-height: 1;
}

.stat-value {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  line-height: 1;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--gray-light);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

/* === PICKS DEL USUARIO === */
.picks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--space-lg);
}

.fighter-pick {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  cursor: pointer;
  transition: all 0.3s ease;
}

.fighter-pick:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.fighter-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.fighter-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fighter-initials {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
}

.fighter-info {
  flex: 1;
}

.fighter-name {
  font-family: var(--font-impact);
  font-size: 1.1rem;
  color: var(--white);
  margin-bottom: var(--space-xs);
  text-transform: uppercase;
}

.fighter-record {
  color: var(--gray-light);
  font-size: 0.9rem;
  margin-bottom: var(--space-sm);
}

.fighter-points {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--primary);
}

.cost {
  color: var(--gray-light);
  font-size: 0.9rem;
}

/* === ESTADOS VACÍOS === */
.empty-leaderboard {
  text-align: center;
  padding: var(--space-2xl) var(--space-lg);
  color: var(--gray-light);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: var(--space-lg);
}

.empty-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
}

.empty-description {
  font-size: 0.9rem;
  line-height: 1.5;
}

/* === LOADING SECCIONES === */
.loading-section {
  padding: var(--space-2xl);
  text-align: center;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-md);
  color: var(--gray-light);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 107, 53, 0.3);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* === MODALES === */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-lg);
}

.modal-content {
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 107, 53, 0.3);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  text-transform: uppercase;
}

.modal-close {
  background: none;
  border: none;
  color: var(--gray-light);
  font-size: 1.5rem;
  cursor: pointer;
  padding: var(--space-sm);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}

.modal-close:hover {
  color: var(--white);
  background: rgba(255, 255, 255, 0.1);
}

/* === CONFIRMACIÓN === */
.confirmation-modal {
  max-width: 500px;
}

.confirmation-content {
  padding: var(--space-xl);
  text-align: center;
}

.warning-icon {
  font-size: 3rem;
  margin-bottom: var(--space-lg);
}

.warning-title {
  font-family: var(--font-impact);
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
}

.warning-message {
  color: var(--gray-light);
  line-height: 1.6;
  margin-bottom: var(--space-xl);
}

.confirmation-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
}

/* === INFO DE LA LIGA === */
.league-info-content {
  padding: var(--space-xl);
}

.info-section {
  margin-bottom: var(--space-xl);
}

.info-title {
  font-family: var(--font-impact);
  font-size: 1.2rem;
  color: var(--white);
  margin-bottom: var(--space-md);
  text-transform: uppercase;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  color: var(--gray-light);
  font-size: 0.9rem;
  font-weight: 600;
}

.info-value {
  color: var(--white);
  font-weight: bold;
}

.invitation-code-container {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.invitation-code {
  font-family: var(--font-display);
  font-size: 1.2rem;
  color: var(--primary) !important;
  font-weight: bold;
  letter-spacing: 0.15em;
  flex: 1;
}

.rules-list {
  color: var(--gray-light);
  margin: 0;
  padding-left: var(--space-lg);
}

.rules-list li {
  margin-bottom: var(--space-sm);
  line-height: 1.5;
}

/* === DETALLES DEL LUCHADOR === */
.fighter-details {
  padding: var(--space-xl);
}

.fighter-image {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.fighter-image img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary);
}

.fighter-placeholder {
  width: 120px;
  height: 120px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  font-family: var(--font-impact);
  font-size: 2.5rem;
  color: var(--white);
}

.fighter-stats {
  margin-bottom: var(--space-xl);
}

.stat-row {
  display: flex;
  justify-content: space-between;
  padding: var(--space-md) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-row .stat-label {
  color: var(--gray-light);
  font-weight: 600;
}

.stat-row .stat-value {
  color: var(--white);
  font-weight: bold;
}

.breakdown-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
}

.points-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.points-item {
  display: flex;
  justify-content: space-between;
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
}

.points-item.total {
  background: rgba(255, 107, 53, 0.2);
  border: 1px solid var(--primary);
  font-weight: bold;
}

.points-category {
  color: var(--gray-light);
}

.points-value {
  color: var(--primary);
  font-weight: bold;
}

/* === NOTIFICACIÓN === */
.notification {
  position: fixed;
  top: var(--space-xl);
  right: var(--space-xl);
  background: var(--gradient-card);
  backdrop-filter: blur(15px);
  border: 2px solid var(--success);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  z-index: 1000;
  cursor: pointer;
  transform: translateX(100%);
  animation: slideIn 0.3s ease forwards;
  max-width: 350px;
}

@keyframes slideIn {
  to { transform: translateX(0); }
}

.notification-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notification-text {
  color: var(--white);
  font-size: 0.9rem;
  line-height: 1.4;
}

/* === RESPONSIVE === */
@media (max-width: 1200px) {
  .picks-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .picks-call-to-action {
    flex-direction: column;
    text-align: center;
    gap: var(--space-lg);
  }

  .member-stats.enhanced {
    flex-direction: column;
    gap: var(--space-sm);
    align-items: flex-end;
  }
  
  .stat-column {
    min-width: auto;
  }
  
  .stat-item.primary .stat-value {
    font-size: 1.5rem;
  }
  
  .stat-value {
    font-size: 1.1rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-md);
  }

  .header-content {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }

  .league-meta {
    flex-direction: column;
    gap: var(--space-sm);
  }

  .league-actions {
    display: flex;
    flex-direction: column;
    gap: var(--space-sm);
    width: 100%;
  }
  
  .btn-leave,
  .btn-info {
    width: 100%;
    justify-content: center;
  }

  .tabs {
    flex-direction: column;
    align-items: center;
  }

  .tab-button {
    width: 100%;
    max-width: 280px;
  }

  .league-name {
    font-size: 2rem;
  }

  .leaderboard-item {
    flex-direction: column;
    text-align: center;
    gap: var(--space-md);
  }

  .leaderboard-item.enhanced {
    padding: var(--space-lg);
  }

  .member-info {
    flex-direction: column;
    text-align: center;
  }

  .member-stats {
    text-align: center;
  }

  .member-stats.enhanced {
    flex-direction: row;
    justify-content: space-around;
    gap: var(--space-md);
  }

  .fighter-pick {
    flex-direction: column;
    text-align: center;
  }

  .fighter-points {
    justify-content: center;
    gap: var(--space-lg);
  }

  .confirmation-actions {
    flex-direction: column;
  }
  
  .btn-cancel,
  .btn-confirm-leave {
    width: 100%;
  }
  
  .invitation-code-container {
    flex-direction: column;
    gap: var(--space-sm);
    text-align: center;
  }
}

@media (max-width: 480px) {
  .league-name {
    font-size: 1.8rem;
  }

  .your-picks-section,
  .event-leaderboard-section,
  .leaderboard-container,
  .picks-action-section {
    padding: var(--space-lg);
  }

  .modal-content {
    margin: var(--space-md);
    max-width: none;
  }
}

/* === MODAL MEJORADO === */
.fighter-modal {
  max-width: 700px; /* Aumentar ancho para más contenido */
}

.fighter-basic-stats {
  margin-bottom: var(--space-xl);
}

.points-detail {
  font-size: 0.8rem;
  color: var(--gray-light);
  margin-left: var(--space-sm);
  font-style: italic;
}

.points-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-xs);
}

.points-item:last-child {
  margin-bottom: 0;
}

.points-item.total {
  background: rgba(255, 107, 53, 0.2);
  border: 1px solid var(--primary);
  font-weight: bold;
  margin-top: var(--space-md);
}

.points-category {
  color: var(--gray-light);
  flex: 1;
}

.points-value {
  color: var(--primary);
  font-weight: bold;
  margin-right: var(--space-sm);
}

/* === ESTADÍSTICAS DETALLADAS === */
.detailed-stats {
  margin-top: var(--space-xl);
  padding-top: var(--space-xl);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.stats-title {
  font-family: var(--font-impact);
  font-size: 1.3rem;
  color: var(--white);
  margin-bottom: var(--space-lg);
  text-transform: uppercase;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
}

.stat-box {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  text-align: center;
  transition: all 0.3s ease;
}

.stat-box:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 107, 53, 0.3);
  transform: translateY(-2px);
}

.stat-number {
  font-family: var(--font-impact);
  font-size: 2rem;
  color: var(--primary);
  margin-bottom: var(--space-sm);
  line-height: 1;
}

.stat-name {
  font-size: 0.9rem;
  color: var(--gray-light);
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.05em;
}

/* === RESPONSIVE PARA EL MODAL === */
@media (max-width: 768px) {
  .fighter-modal {
    max-width: 95vw;
    margin: var(--space-md);
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-sm);
  }
  
  .stat-box {
    padding: var(--space-md);
  }
  
  .stat-number {
    font-size: 1.5rem;
  }
  
  .points-item {
    flex-direction: column;
    text-align: center;
    gap: var(--space-xs);
  }
  
  .points-detail {
    margin-left: 0;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

</style>