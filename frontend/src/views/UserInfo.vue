<template>
  <Layout title="重要个人信息" :showBack="true">
    <!-- 头像 -->
    <div class="avatar-section">
      <label class="avatar-label">头像</label>
      <div class="avatar-preview">
        <img v-if="avatar.type === 'image'" :src="avatar.url" alt="头像" />
        <svg
          v-else
          width="40"
          height="40"
          viewBox="0 0 100 100"
        >
          <!-- 根据当前选中的图标绘制SVG -->
          <g v-for="(path, index) in selectedIcon.paths" :key="index">
            <path v-if="path.d" :d="path.d" :fill="path.fill" />
            <ellipse v-else-if="path.type === 'ellipse'" 
                     :cx="path.cx" :cy="path.cy" :rx="path.rx" :ry="path.ry" :fill="path.fill" />
          </g>
        </svg>
      </div>

      <div class="avatar-mode">
        <button
          class="mode-btn"
          :class="{ active: avatarMode === 'upload' }"
          @click="setAvatarMode('upload')"
        >
          上传
        </button>
        <button
          class="mode-btn"
          :class="{ active: avatarMode === 'select' }"
          @click="setAvatarMode('select')"
        >
          选择
        </button>
      </div>

      <input
        type="file"
        id="avatar-upload"
        ref="fileInput"
        accept="image/*"
        @change="handleFileUpload"
        style="display: none"
      />
      <button
        v-if="avatarMode === 'upload'"
        class="trigger-btn"
        @click="$refs.fileInput.click()"
      >
        📷 从相册选择
      </button>

      <div v-if="avatarMode === 'select'" class="builtin-avatars">
        <div
          v-for="(icon, index) in builtinIcons"
          :key="index"
          class="builtin-item"
          :class="{ selected: selectedIcon.name === icon.name }"
          @click="selectBuiltinIcon(icon)"
        >
          <svg width="24" height="24" viewBox="0 0 100 100">
            <g v-for="(path, pathIndex) in icon.paths" :key="pathIndex">
              <path v-if="path.d" :d="path.d" :fill="path.fill" />
              <ellipse v-else-if="path.type === 'ellipse'" 
                       :cx="path.cx" :cy="path.cy" :rx="path.rx" :ry="path.ry" :fill="path.fill" />
            </g>
          </svg>
        </div>
      </div>
    </div>

    <!-- 雅号 -->
    <div class="form-group">
      <label class="form-label">雅号</label>
      <input
        v-model="elegantName"
        type="text"
        class="form-input"
        :placeholder="'当前雅号: ' + (elegantName || '未设置')"
      />
    </div>

    <!-- 所在地 -->
    <div class="form-group">
      <label class="form-label">常驻之地</label>
      <input
        v-model="location"
        type="text"
        class="form-input"
        :placeholder="'当前地址: ' + (location || '未设置')"
        id="location-input"
      />
      <button class="locate-btn" @click="getLocation">📍 自动获取当前位置</button>
    </div>

    <!-- 跳转安全设置 -->
    <a href="#" class="security-link" @click.prevent="goToSecurity">
      → 账户与安全
    </a>

    <button class="save-btn" @click="saveInfo">保存修改</button>
  </Layout>
</template>

<script setup>
import Layout from '@/components/Layout.vue'
import { ElMessage } from 'element-plus'
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUserInfo, updateUserInfo, uploadFile } from '@/api/user' // 导入更新用户信息的API
import { mapApi } from '@/api/mapApi' // 导入mapApi

const router = useRouter()

// ========== 头像相关 ==========
const avatarMode = ref('upload') // 'upload' | 'select'
const selectedFile = ref(null) // 新增：保存选择的文件对象

const builtinIcons = [
  {
    name: 'porcelain',
    paths: [
      { d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64' }
    ]
  },
  {
    name: 'tea',
    paths: [
      { d: 'M30,40 Q50,20 70,40 L65,70 Q50,80 35,70 Z', fill: '#8B7355' }
    ]
  },
  {
    name: 'scroll',
    paths: [
      { d: 'M20,30 h60 v40 h-60 z', fill: '#A68A64', opacity: 0.9 },
      { d: 'M30,50 a8,8 0 1,0 0,0', fill: 'white' },
      { d: 'M70,50 a8,8 0 1,0 0,0', fill: 'white' }
    ]
  },
  {
    name: 'fan',
    paths: [
      { d: 'M50,20 A30,30 0 0,1 80,50 A30,30 0 0,1 50,80 L50,20 Z', fill: '#A68A64' },
      { d: 'M50,20 L40,10 M50,20 L60,10', stroke: '#A68A64', strokeWidth: 4, fill: 'none' }
    ]
  },
  {
    name: 'architecture',
    paths: [
      { d: 'M30,50 h40 v30 h-40 z', fill: '#A68A64' },
      { d: 'M20,40 h60 v10 h-60 z', fill: '#8B7355' },
      { d: 'M10,30 h80 v10 h-80 z', fill: '#A68A64' }
    ]
  },
  
  {
    name: 'lantern',
    paths: [
      { type: 'ellipse', cx: 50, cy: 30, rx: 15, ry: 10, fill: '#A68A64' },
      { d: 'M35,30 h30 v40 h-30 z', rx: 6, fill: '#A68A64' },
      { type: 'ellipse', cx: 50, cy: 70, rx: 10, ry: 6, fill: '#8B7355' }
    ]
  }
]

const selectedIcon = ref(builtinIcons[0])

const avatar = reactive({
  type: 'builtin', // 'builtin' | 'image'
  url: '',
})

const fileInput = ref(null)

function setAvatarMode(mode) {
  avatarMode.value = mode
}

function handleFileUpload(event) {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file // 保存文件对象
    const url = URL.createObjectURL(file)
    avatar.type = 'image'
    avatar.url = url
  }
}

function selectBuiltinIcon(icon) {
  selectedIcon.value = icon
  avatar.type = 'builtin'
}

// ========== 表单数据 ==========
const elegantName = ref('')
const location = ref('')

// 从后端获取用户信息
const loadUserInfo = async () => {
  try {
    const response = await getCurrentUserInfo();
    if (response.code === 1) {
      // 使用后端返回的用户名和位置信息
      elegantName.value = response.data.username || '林砚之'
      
      // 检查后端是否返回了位置信息
      if (response.data.location) {
        location.value = response.data.location;
      } else if (response.data.address) {
        location.value = response.data.address;
      } else {
        // 如果后端没有位置信息，则自动尝试获取当前物理位置
        await getLocation();
      }
      
      // 加载头像信息
      if (response.data.avatarUrl) {
        avatar.type = 'image';
        avatar.url = response.data.avatarUrl;
      } else if (response.data.iconName) {
        avatar.type = 'builtin';
        // 查找匹配的内置图标
        const foundIcon = builtinIcons.find(icon => icon.name === response.data.iconName);
        if (foundIcon) {
          selectedIcon.value = foundIcon;
        }
      } else {
        // 如果后端没有头像信息，使用默认头像
        avatar.type = 'builtin';
        selectedIcon.value = builtinIcons[0]; // 使用第一个作为默认图标
      }
    } else {
      // 如果API调用失败，使用默认值
      elegantName.value = '林砚之'
      location.value = '景德镇'
      avatar.type = 'builtin'; // 默认使用内置图标
      selectedIcon.value = builtinIcons[0];
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 出错时使用默认值
    elegantName.value = '林砚之'
    location.value = '景德镇'
    avatar.type = 'builtin'; // 默认使用内置图标
    selectedIcon.value = builtinIcons[0];
  }
}

// 根据经纬度获取城市信息（反向地理编码）
async function reverseGeocode(lat, lng) {
  try {
    // 这里使用一个公开的反向地理编码API，如OpenStreetMap Nominatim
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&accept-language=zh-CN`
    );
    const data = await response.json();
    
    // 尝试从响应中提取城市信息
    if (data && data.address) {
      // 优先级：城市 > 镇 > 区 > 县
      return data.address.city || 
             data.address.town || 
             data.address.suburb || 
             data.address.county || 
             data.display_name || 
             `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
    }
    return `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
  } catch (error) {
    console.error('反向地理编码失败:', error);
    return `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
  }
}

// 获取当前位置
async function getLocation() {
  try {
    ElMessage.info('正在尝试获取当前位置...')
    // 调用地图API获取实际位置 (使用 HTML5 Geolocation)
    const locationData = await mapApi.getUserLocation();
    
    // 如果获取到的是非默认的有效经纬度
    if (locationData && locationData.lat !== 35.8617) {
      // 使用反向地理编码获取城市名称
      const city = await reverseGeocode(locationData.lat, locationData.lng);
      location.value = city;
      ElMessage.success(`位置已自动识别为: ${city}`)
    } else {
      // 如果定位失败或返回默认值，则默认设置为景德镇
      if (!location.value) {
        location.value = '景德镇'
      }
    }
  } catch (error) {
    console.error('获取地理位置失败:', error)
    if (!location.value) {
      location.value = '景德镇'
    }
  }
}

// ========== 导航与保存 ==========
function goToSecurity() {
  ElMessage.success('跳转至【账户与安全】页面')
  router.push('/user/accountsecurity')
}

// 保存用户信息到后端
async function saveInfo() {
  const loadingMsg = ElMessage({
    message: '正在保存信息...',
    type: 'info',
    duration: 0 
  });
  
  try {
    // 准备要更新的数据
    const updateData = {
      username: elegantName.value,
      location: location.value
    };

    // 处理头像数据
    if (avatar.type === 'image') {
      // 如果有新选择的文件，先上传
      if (selectedFile.value) {
        const uploadRes = await uploadFile(selectedFile.value);
        if (uploadRes.code === 1) {
          updateData.avatarUrl = uploadRes.data;
        } else {
          throw new Error(uploadRes.msg || '头像上传失败');
        }
      } else {
        // 如果没有新文件，使用原有的 URL
        updateData.avatarUrl = avatar.url;
      }
    } else {
      // 选择内置图标模式，清空 avatarUrl 并设置 iconName
      updateData.avatarUrl = null;
      updateData.iconName = selectedIcon.value.name;
    }

    // 调用API更新用户信息
    const response = await updateUserInfo(updateData);
    
    loadingMsg.close();
    
    if (response.code === 1) {
      ElMessage.success(response.msg || '个人信息保存成功！');
      router.replace('/user/profile')
    } else {
      ElMessage.error(response.msg || '保存失败，请稍后重试');
    }
  } catch (error) {
    console.error('保存用户信息失败:', error);
    loadingMsg.close();
    ElMessage.error('保存失败：' + (error.message || '网络错误'));
  }
}

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserInfo();
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 头像区 */
.avatar-section {
  margin-bottom: 24px;
  text-align: center;
}
.avatar-label {
  display: block;
  font-weight: 600;
  margin-bottom: 12px;
  font-size: 16px;
}

.avatar-preview {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #efeae5;
  margin: 0 auto 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #a68a64;
}
.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-preview svg {
  width: 40px;
  height: 40px;
  fill: currentColor;
}

.avatar-mode {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}
.mode-btn {
  flex: 1;
  padding: 6px 0;
  background: #efeae5;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  color: #8c7b6b;
  cursor: pointer;
}
.mode-btn.active {
  background: #a68a64;
  color: white;
}

.trigger-btn {
  display: block;
  margin: 8px auto;
  background: none;
  border: none;
  color: #a68a64;
  font-size: 14px;
  cursor: pointer;
}

.builtin-avatars {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 10px;
}
.builtin-item {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #faf8f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 1px solid #c4b8a8;
}
.builtin-item.selected {
  border-color: #a68a64;
}

/* 表单 */
.form-group {
  margin-bottom: 20px;
}
.form-label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 16px;
  color: #5a524a;
}
.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #c4b8a8;
  border-radius: 8px;
  background: #faf8f5;
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  color: #3a3530;
}
.form-input:focus {
  outline: none;
  border-color: #a68a64;
}

.locate-btn {
  background: none;
  border: none;
  color: #a68a64;
  font-size: 14px;
  padding: 4px 0;
  cursor: pointer;
}

/* 跳转安全页 */
.security-link {
  display: block;
  text-align: center;
  color: #a68a64;
  font-size: 15px;
  margin: 24px 0;
  text-decoration: none;
  padding: 10px;
  border-top: 1px solid #efeae5;
  border-bottom: 1px solid #efeae5;
}
.security-link:hover {
  text-decoration: underline;
}

/* 保存 */
.save-btn {
  width: 100%;
  background: #a68a64;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 14px;
  font-size: 17px;
  font-weight: 600;
  font-family: 'Noto Serif SC', serif;
  cursor: pointer;
}
</style>