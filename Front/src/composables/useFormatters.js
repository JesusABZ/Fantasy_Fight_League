// composables/useFormatters.js

export function useFormatters() {
  
  // Formatear dinero
  const formatMoney = (amount, currency = 'USD', locale = 'en-US') => {
    if (amount === null || amount === undefined) return '$0'
    
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount).replace('$', '') // Removemos el símbolo para usar nuestro formato
  }

  // Formatear números grandes con separadores
  const formatNumber = (number, locale = 'en-US') => {
    if (number === null || number === undefined) return '0'
    return new Intl.NumberFormat(locale).format(number)
  }

  // Formatear fechas
  const formatDate = (dateString, format = 'long', locale = 'es-ES') => {
    if (!dateString) return ''
    
    const date = new Date(dateString)
    
    const formats = {
      short: { 
        day: 'numeric', 
        month: 'short', 
        year: 'numeric' 
      },
      long: { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      },
      weekday: { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      },
      time: { 
        hour: '2-digit', 
        minute: '2-digit' 
      },
      datetime: { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit', 
        minute: '2-digit' 
      }
    }

    return date.toLocaleDateString(locale, formats[format] || formats.long)
  }

  // Formatear fecha relativa (ej: "hace 2 días")  
  const formatRelativeDate = (dateString) => {
    if (!dateString) return ''
    
    const date = new Date(dateString)
    const now = new Date()
    const diffInSeconds = Math.floor((now - date) / 1000)
    
    if (diffInSeconds < 60) return 'Ahora mismo'
    if (diffInSeconds < 3600) return `Hace ${Math.floor(diffInSeconds / 60)} minutos`
    if (diffInSeconds < 86400) return `Hace ${Math.floor(diffInSeconds / 3600)} horas`
    if (diffInSeconds < 2592000) return `Hace ${Math.floor(diffInSeconds / 86400)} días`
    if (diffInSeconds < 31536000) return `Hace ${Math.floor(diffInSeconds / 2592000)} meses`
    return `Hace ${Math.floor(diffInSeconds / 31536000)} años`
  }

  // Obtener iniciales de un nombre
  const getInitials = (name, maxInitials = 2) => {
    if (!name) return ''
    
    return name
      .split(' ')
      .filter(word => word.length > 0)
      .slice(0, maxInitials)
      .map(word => word[0])
      .join('')
      .toUpperCase()
  }

  // Truncar texto
  const truncateText = (text, maxLength = 100, suffix = '...') => {
    if (!text || text.length <= maxLength) return text
    return text.substring(0, maxLength - suffix.length) + suffix
  }

  // Formatear record de luchador (ej: "27-1-0" -> "27-1")
  const formatFighterRecord = (record) => {
    if (!record) return ''
    
    // Si ya está en formato correcto, devolverlo
    if (record.split('-').length === 2) return record
    
    // Si tiene formato "W-L-D", convertir a "W-L"
    const parts = record.split('-')
    if (parts.length >= 2) {
      return `${parts[0]}-${parts[1]}`
    }
    
    return record
  }

  // Formatear peso/categoría
  const formatWeightClass = (weightClass) => {
    if (!weightClass) return ''
    
    const weightClasses = {
      'strawweight': 'Peso Paja',
      'flyweight': 'Peso Mosca',
      'bantamweight': 'Peso Gallo',
      'featherweight': 'Peso Pluma',
      'lightweight': 'Peso Ligero',
      'welterweight': 'Peso Wélter',
      'middleweight': 'Peso Medio',
      'light_heavyweight': 'Peso Semicompleto',
      'heavyweight': 'Peso Pesado'
    }
    
    return weightClasses[weightClass.toLowerCase()] || weightClass
  }

  // Formatear posición en clasificación
  const formatPosition = (position) => {
    if (!position || position < 1) return ''
    
    if (position === 1) return '🥇 1º'
    if (position === 2) return '🥈 2º'  
    if (position === 3) return '🥉 3º'
    
    return `#${position}`
  }

  // Formatear porcentajes
  const formatPercentage = (value, decimals = 1) => {
    if (value === null || value === undefined) return '0%'
    return `${Number(value).toFixed(decimals)}%`
  }

  // Formatear puntos con color basado en valor
  const formatPoints = (points, showColor = false) => {
    if (points === null || points === undefined) return '0 pts'
    
    const formatted = `${formatNumber(points)} pts`
    
    if (!showColor) return formatted
    
    // Agregar clases CSS basadas en los puntos
    if (points >= 200) return { text: formatted, class: 'points-excellent' }
    if (points >= 150) return { text: formatted, class: 'points-good' }
    if (points >= 100) return { text: formatted, class: 'points-average' }
    if (points >= 50) return { text: formatted, class: 'points-low' }
    return { text: formatted, class: 'points-poor' }
  }

  // Formatear username para mostrar
  const formatUsername = (username, maxLength = 15) => {
    if (!username) return ''
    
    if (username.length <= maxLength) return username
    return username.substring(0, maxLength - 1) + '…'
  }

  // Formatear código de liga
  const formatLeagueCode = (code) => {
    if (!code) return ''
    return code.toUpperCase().replace(/(.{4})/g, '$1-').slice(0, -1)
  }

  // Formatear tamaño de archivo
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes'
    
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // Formatear duración (en segundos a mm:ss)
  const formatDuration = (seconds) => {
    if (!seconds || seconds < 0) return '0:00'
    
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
  }

  // Validar y formatear email para mostrar
  const formatEmailDisplay = (email, maxLength = 25) => {
    if (!email) return ''
    
    if (email.length <= maxLength) return email
    
    const [user, domain] = email.split('@')
    if (user.length > maxLength - domain.length - 4) {
      return `${user.substring(0, maxLength - domain.length - 4)}…@${domain}`
    }
    
    return email
  }

  // Formatear números de teléfono (básico)
  const formatPhone = (phone) => {
    if (!phone) return ''
    
    // Remover caracteres no numéricos
    const cleaned = phone.replace(/\D/g, '')
    
    // Si tiene 9 dígitos (España), formatear como XXX XXX XXX
    if (cleaned.length === 9) {
      return cleaned.replace(/(\d{3})(\d{3})(\d{3})/, '$1 $2 $3')
    }
    
    return phone
  }

  return {
    // Formateo de números y dinero
    formatMoney,
    formatNumber,
    formatPercentage,
    formatPoints,
    
    // Formateo de fechas
    formatDate,
    formatRelativeDate,
    
    // Formateo de texto
    getInitials,
    truncateText,
    formatUsername,
    formatEmailDisplay,
    
    // Formateo específico del dominio
    formatFighterRecord,
    formatWeightClass,
    formatPosition,
    formatLeagueCode,
    
    // Formateo de archivos y utilidades
    formatFileSize,
    formatDuration,
    formatPhone
  }
}