//导入request.js工具
import request from '@/utils/request'


// 从 token 解析用户 ID 的辅助函数
function getUserIdFromToken() {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 解析 JWT token
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.userId
    } catch (error) {
      console.error('解析 token 失败:', error)
      return null
    }
  }
  return null
}

// 获取验证码
export const getCaptcha = () => {
  return request.get('/user/captcha');
}

//调用注册接口的函数
export const userRegisterService = (registerData)=>{
    return request.post('/user/register',registerData);
}
//调用登录接口的函数
export const userLoginService = (loginData)=>{
    return request.post('/user/login',loginData);
}

//调用AI聊天接口的函数
export const chatService = (chatData)=>{
    return request.post('/ai/chat',chatData);
}
// 调用 AI 历史接口的函数 - 添加类型参数，默认为 'chat'
export const chatHistoryService = (type = 'chat') => {
    return request.get(`/ai/history/${type}/list`);
}

// 获取单个会话历史的函数
export const getSingleChatHistory = (type, chatId) => {
    return request.get(`/ai/history/${type}/${chatId}`)
}

// 调用更改密码的函数 - 使用当前登录用户（如果后端需要直接参数）
export const updatePassword = (oldPassword, newPassword, rePassword) => {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error('未找到用户ID');
  }
  
  // 发送原始数据而不是对象
  return request.put(`/user/updatePassword`, {
    oldPassword: oldPassword,
    newPassword: newPassword,
    rePassword: rePassword
  });
}

//调用获取用户信息的函数 - 使用当前登录用户
export const getCurrentUserInfo = () => {
  return request.get(`/user/currentUserInfo`);
}

// 添加更新联系方式的函数 - 使用当前登录用户
export const updateContact = (phone) => {
  return request.put(`/user/update`, {
    phone
  });
}

// 更新用户信息
export function updateUserInfo(data) {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error('未找到用户ID');
  }
  return request({
    url: `/user/updateUserInfo`,
    method: 'put', 
    data
  })
}

// 获取用户徽章
export const getUserBadges = () => {
  return request.get('/badge/myBadges');
}

// 获取我的旅游博客 (足迹)
export const getMyTravelBlogs = () => {
  return request.get('/travel/myBlogs');
}

// 发布旅游打卡
export const publishTravelBlog = (data) => {
  return request.post('/travel/publish', data);
}

// 上传文件接口
export const uploadFile = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/common/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

//删除用户信息
export function deleteUserAccount() {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error('未找到用户ID');
  }
  return request({
    url: `/user/deleteUserInfo`,
    method: 'delete' 
  })
}
