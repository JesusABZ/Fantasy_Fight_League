// Composable para formatear fechas
export function useDateFormatter() {
  
  /**
   * Formatea una fecha a formato legible en español
   * @param {string|Date} dateString - Fecha en formato ISO o objeto Date
   * @returns {string} Fecha formateada (ej: "1 de Junio, 2025")
   */
  const formatEventDate = (dateString) => {
    if (!dateString) return ''
    
    try {
      const date = new Date(dateString)
      
      // Opciones para el formato de fecha en español
      const options = {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      }
      
      return date.toLocaleDateString('es-ES', options)
    } catch (error) {
      console.error('Error al formatear fecha:', error)
      return ''
    }
  }
  
  /**
   * Formatea una fecha a formato corto
   * @param {string|Date} dateString - Fecha en formato ISO o objeto Date
   * @returns {string} Fecha formateada (ej: "01/06/2025")
   */
  const formatShortDate = (dateString) => {
    if (!dateString) return ''
    
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString('es-ES')
    } catch (error) {
      console.error('Error al formatear fecha corta:', error)
      return ''
    }
  }
  
  /**
   * Verifica si una fecha es futura
   * @param {string|Date} dateString - Fecha a verificar
   * @returns {boolean} true si la fecha es futura
   */
  const isFutureDate = (dateString) => {
    if (!dateString) return false
    
    try {
      const date = new Date(dateString)
      const now = new Date()
      return date > now
    } catch (error) {
      console.error('Error al verificar fecha futura:', error)
      return false
    }
  }
  /**
   * Formatea una fecha a formato de hora en español
   * @param {string|Date} dateString - Fecha en formato ISO o objeto Date
   * @returns {string} Hora formateada (ej: "20:00" o "8:00 PM")
   */
  const formatEventTime = (dateString) => {
    if (!dateString) return ''
    
    try {
      const date = new Date(dateString)
      
      return date.toLocaleTimeString('es-ES', {
        hour: '2-digit',
        minute: '2-digit',
        timeZone: 'UTC'
      })
    } catch (error) {
      console.error('Error al formatear hora:', error)
      return ''
    }
  }

  /**
   * Formatea una fecha a formato de fecha y hora en español
   * @param {string|Date}  
  /**
   * Calcula los días restantes hasta una fecha
   * @param {string|Date} dateString - Fecha objetivo
   * @returns {number} Número de días restantes (negativo si ya pasó)
   */
  const getDaysUntil = (dateString) => {
    if (!dateString) return 0
    
    try {
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = date - now
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      return diffDays
    } catch (error) {
      console.error('Error al calcular días restantes:', error)
      return 0
    }
  }
  
  return {
    formatEventDate,
    formatEventTime,
    formatShortDate,
    isFutureDate,
    getDaysUntil
  }
}