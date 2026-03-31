import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

const GUEST_AI_LIMIT = 3
const USER_AI_LIMIT = 5
const EXPERIENCE_COST_PER_AI = 50
const GUEST_AI_COUNT_KEY = 'wenji_guest_ai_count'
const GUEST_AI_DATE_KEY = 'wenji_guest_ai_date'
const USER_AI_COUNT_KEY = 'wenji_user_ai_count'
const USER_AI_DATE_KEY = 'wenji_user_ai_date'
const EXTRA_AI_COUNT_KEY = 'wenji_extra_ai_count'

const getTodayDate = () => {
  return new Date().toISOString().split('T')[0]
}

const generateUUID = () => {
  // Use cryptographically secure random values instead of Math.random
  const cryptoObj = (typeof window !== 'undefined' && window.crypto) || (typeof self !== 'undefined' && self.crypto)

  if (cryptoObj && cryptoObj.getRandomValues) {
    const bytes = cryptoObj.getRandomValues(new Uint8Array(16))

    // Per RFC 4122 section 4.4: set the version to 4
    bytes[6] = (bytes[6] & 0x0f) | 0x40
    // Per RFC 4122 section 4.4: set the variant to 10xxxxxx
    bytes[8] = (bytes[8] & 0x3f) | 0x80

    const byteToHex = []
    for (let i = 0; i < 256; i++) {
      byteToHex.push((i + 0x100).toString(16).substr(1))
    }

    return (
      byteToHex[bytes[0]] +
      byteToHex[bytes[1]] +
      byteToHex[bytes[2]] +
      byteToHex[bytes[3]] + '-' +
      byteToHex[bytes[4]] +
      byteToHex[bytes[5]] + '-' +
      byteToHex[bytes[6]] +
      byteToHex[bytes[7]] + '-' +
      byteToHex[bytes[8]] +
      byteToHex[bytes[9]] + '-' +
      byteToHex[bytes[10]] +
      byteToHex[bytes[11]] +
      byteToHex[bytes[12]] +
      byteToHex[bytes[13]] +
      byteToHex[bytes[14]] +
      byteToHex[bytes[15]]
    )
  }

  // Fallback: keep previous non-crypto behavior if crypto is unavailable
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

export const useAuthStore = defineStore('auth', () => {
  const guestId = ref(null)
  const isLoggedIn = ref(false)
  const user = ref(null)
  const token = ref(null)
  const showLoginModal = ref(false)
  const pendingAction = ref(null)
  const guestAiCount = ref(0)
  const userAiCount = ref(0)
  const extraAiCount = ref(0)

  const isGuest = computed(() => !isLoggedIn.value && guestId.value)
  
  const userType = computed(() => isLoggedIn.value ? 'USER' : 'GUEST')
  
  const userId = computed(() => {
    if (isLoggedIn.value && user.value) {
      return user.value.userId || user.value.id
    }
    return guestId.value
  })

  const aiLimit = computed(() => isLoggedIn.value ? USER_AI_LIMIT : GUEST_AI_LIMIT)

  const guestAiRemaining = computed(() => {
    return Math.max(0, GUEST_AI_LIMIT - guestAiCount.value)
  })

  const userAiRemaining = computed(() => {
    return Math.max(0, USER_AI_LIMIT - userAiCount.value)
  })

  const aiRemaining = computed(() => {
    const baseRemaining = isLoggedIn.value ? userAiRemaining.value : guestAiRemaining.value
    return baseRemaining + extraAiCount.value
  })

  const canUseAi = computed(() => {
    if (isLoggedIn.value) {
      return userAiCount.value < USER_AI_LIMIT || extraAiCount.value > 0
    }
    return guestAiCount.value < GUEST_AI_LIMIT || extraAiCount.value > 0
  })

  const checkAndResetDailyCount = () => {
    const today = getTodayDate()
    
    const guestDate = localStorage.getItem(GUEST_AI_DATE_KEY)
    if (guestDate !== today) {
      guestAiCount.value = 0
      localStorage.setItem(GUEST_AI_DATE_KEY, today)
      localStorage.setItem(GUEST_AI_COUNT_KEY, '0')
    }
    
    const userDate = localStorage.getItem(USER_AI_DATE_KEY)
    if (userDate !== today) {
      userAiCount.value = 0
      localStorage.setItem(USER_AI_DATE_KEY, today)
      localStorage.setItem(USER_AI_COUNT_KEY, '0')
    }
  }

  const initGuest = () => {
    const savedGuestId = localStorage.getItem('wenji_guest_id')
    if (savedGuestId) {
      guestId.value = savedGuestId
    } else {
      const newGuestId = `guest_${generateUUID()}`
      guestId.value = newGuestId
      localStorage.setItem('wenji_guest_id', newGuestId)
    }
    
    checkAndResetDailyCount()
    
    const savedGuestAiCount = localStorage.getItem(GUEST_AI_COUNT_KEY)
    if (savedGuestAiCount) {
      guestAiCount.value = parseInt(savedGuestAiCount, 10) || 0
    }
    
    const savedUserAiCount = localStorage.getItem(USER_AI_COUNT_KEY)
    if (savedUserAiCount) {
      userAiCount.value = parseInt(savedUserAiCount, 10) || 0
    }
    
    const savedExtraAiCount = localStorage.getItem(EXTRA_AI_COUNT_KEY)
    if (savedExtraAiCount) {
      extraAiCount.value = parseInt(savedExtraAiCount, 10) || 0
    }
  }

  const incrementAiCount = () => {
    if (extraAiCount.value > 0) {
      extraAiCount.value--
      localStorage.setItem(EXTRA_AI_COUNT_KEY, extraAiCount.value.toString())
    } else if (isLoggedIn.value) {
      userAiCount.value++
      localStorage.setItem(USER_AI_COUNT_KEY, userAiCount.value.toString())
    } else {
      guestAiCount.value++
      localStorage.setItem(GUEST_AI_COUNT_KEY, guestAiCount.value.toString())
    }
  }

  const exchangeExperienceForAi = (userStore, count = 1) => {
    const totalCost = EXPERIENCE_COST_PER_AI * count
    if (userStore.experience < totalCost) {
      return { 
        success: false, 
        message: `阅历不足，需要 ${totalCost} 阅历兑换 ${count} 次 AI 对话` 
      }
    }
    
    userStore.experience -= totalCost
    extraAiCount.value += count
    localStorage.setItem(EXTRA_AI_COUNT_KEY, extraAiCount.value.toString())
    userStore.saveToStorage()
    
    return { 
      success: true, 
      message: `成功兑换 ${count} 次 AI 对话`,
      costExperience: totalCost,
      extraAiCount: extraAiCount.value
    }
  }

  const getAuthHeaders = () => {
    const headers = {
      'X-User-Type': userType.value,
      'X-User-Id': userId.value
    }
    if (token.value) {
      headers['Authorization'] = `Bearer ${token.value}`
    }
    return headers
  }

  const requireLogin = (action = null) => {
    if (isLoggedIn.value) {
      return true
    }
    pendingAction.value = action
    showLoginModal.value = true
    return false
  }

  const login = async (loginData) => {
    try {
      const result = await userApi.login(loginData)
      
      if (result.success && result.data) {
        isLoggedIn.value = true
        user.value = {
          userId: result.data.userId
        }
        token.value = result.data.accessToken
        
        localStorage.setItem('wenji_auth', JSON.stringify({
          isLoggedIn: true,
          user: user.value,
          token: token.value,
          lastLoginTime: result.data.lastLoginTime
        }))
        
        showLoginModal.value = false
        
        if (pendingAction.value) {
          pendingAction.value()
          pendingAction.value = null
        }
        
        return { success: true }
      } else {
        return { success: false, error: result.message || '登录失败' }
      }
    } catch (error) {
      console.error('Login failed:', error)
      return { success: false, error: error.message || '网络错误' }
    }
  }

  const logout = async () => {
    try {
      if (token.value) {
        await userApi.logout()
      }
    } catch (error) {
      console.warn('Logout API call failed:', error)
    } finally {
      isLoggedIn.value = false
      user.value = null
      token.value = null
      localStorage.removeItem('wenji_auth')
      initGuest()
    }
  }

  const loadFromStorage = () => {
    initGuest()
    
    try {
      const savedAuth = localStorage.getItem('wenji_auth')
      if (savedAuth) {
        const data = JSON.parse(savedAuth)
        if (data.isLoggedIn && data.user && data.token) {
          isLoggedIn.value = true
          user.value = data.user
          token.value = data.token
        }
      }
    } catch (e) {
      console.warn('加载认证数据失败:', e)
    }
  }

  const closeLoginModal = () => {
    showLoginModal.value = false
    pendingAction.value = null
  }

  return {
    guestId,
    isLoggedIn,
    isGuest,
    user,
    token,
    userType,
    userId,
    showLoginModal,
    pendingAction,
    guestAiCount,
    userAiCount,
    extraAiCount,
    guestAiRemaining,
    userAiRemaining,
    aiRemaining,
    aiLimit,
    canUseAi,
    GUEST_AI_LIMIT,
    USER_AI_LIMIT,
    EXPERIENCE_COST_PER_AI,
    initGuest,
    incrementAiCount,
    exchangeExperienceForAi,
    checkAndResetDailyCount,
    getAuthHeaders,
    requireLogin,
    login,
    logout,
    loadFromStorage,
    closeLoginModal
  }
})
