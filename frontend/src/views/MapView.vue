<!-- src/views/MapView.vue -->
<template>
  <Layout
    title="探索非遗地图"
    :showBack="false"
    :showFooterQuote="!isMapExpanded"
    footerQuote="步履所至，皆成文迹"
  >
    <!-- 右上角个人中心 -->
    <template #header-actions>
      <button class="profile-btn" @click="goToProfile" aria-label="个人中心">
        <img v-if="userAvatar.type === 'image'" :src="userAvatar.url" class="header-avatar-img" />
        <svg v-else width="24" height="24" viewBox="0 0 100 100">
          <g v-for="(path, index) in selectedIconPaths" :key="index">
            <path v-if="path.d" :d="path.d" :fill="path.fill" :opacity="path.opacity || 1" />
            <ellipse v-else-if="path.type === 'ellipse'" :cx="path.cx" :cy="path.cy" :rx="path.rx" :ry="path.ry" :fill="path.fill" />
          </g>
        </svg>
      </button>
    </template>

    <!-- 地图区域：收起态为卡片，展开态为真实地图 -->
    <div
      v-if="!isMapExpanded"
      class="map-card"
      @click="expandMap"
    >
      <div class="map-card-content">
        <div class="map-card-title">中国非遗地图</div>
        <div class="map-card-hint">轻触展开，探索非遗足迹</div>
      </div>
    </div>

    <!-- 全屏地图（仅展开时渲染） -->
    <div v-else class="fullscreen-map-wrapper">
      <MapComponent @site-clicked="onSiteClicked" />
      <button class="close-map-btn" @click.stop="collapseMap" aria-label="关闭地图">
        ✕
      </button>
    </div>

    <!-- 用户状态（仅收起时显示） -->
    <div v-if="!isMapExpanded" class="user-stats">
      <!-- <div>💰 话费余额：{{ userStats.balance }}元</div> -->
      <div>🌟 经验值：{{ userStats.exp }}/{{ userStats.maxExp }}</div>
    </div>

    <!-- 地点详情弹窗 -->
    <Teleport to="body" v-if="selectedSite">
      <div class="modal-overlay active">
        <div class="modal-content">
          <div class="modal-header">
            <h2 class="modal-title">{{ selectedSite.name }}</h2>
            <button class="close-modal" @click="closeDetail">×</button>
          </div>
          <div class="modal-image">{{ selectedSite.category }}</div>
          <p class="modal-desc">
            类型：{{ getSiteTypeText(selectedSite.type) }}<br />
            状态：{{ getStatusText(selectedSite.status) }}<br />
            热度：{{ Math.round((selectedSite.heat_level || 0) * 100) }}%
          </p>
          <button class="ar-button" @click="startAR">
            <span>👓</span> AR虚拟体验
          </button>
        </div>
      </div>
    </Teleport>
  </Layout>
</template>

<script setup>
import { ref ,onMounted} from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import MapComponent from '@/views/MapComponent.vue' 
import { ElMessage } from 'element-plus' 
import { getCurrentUserInfo } from '@/api/user'
const router = useRouter()

// 状态
const isMapExpanded = ref(false)
const selectedSite = ref(null)

// 用户数据（初始数据）
const userStats = ref({
  exp:0,
  maxExp:100
});

const userAvatar = ref({ type: 'builtin', iconName: 'default' })
const selectedIconPaths = ref([])

// 默认头像路径 - 青瓷瓶
const defaultAvatarPaths = [
  { d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64', opacity: '0.9' },
  { type: 'ellipse', cx: 50, cy: 75, rx: 25, ry: 8, fill: '#8B7355' }
]

const setSelectedIconPaths = (iconName) => {
  const icons = {
    porcelain: [{ d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64' }],
    tea: [{ d: 'M30,40 Q50,20 70,40 L65,70 Q50,80 35,70 Z', fill: '#8B7355' }],
    scroll: [
      { d: 'M20,30 h60 v40 h-60 z', fill: '#A68A64', opacity: 0.9 },
      { d: 'M30,50 a8,8 0 1,0 0,0', fill: 'white' },
      { d: 'M70,50 a8,8 0 1,0 0,0', fill: 'white' }
    ],
    fan: [
      { d: 'M50,20 A30,30 0 0,1 80,50 A30,30 0 0,1 50,80 L50,20 Z', fill: '#A68A64' },
      { d: 'M50,20 L40,10 M50,20 L60,10', stroke: '#A68A64', strokeWidth: 4, fill: 'none' }
    ],
    architecture: [
      { d: 'M30,50 h40 v30 h-40 z', fill: '#A68A64' },
      { d: 'M20,40 h60 v10 h-60 z', fill: '#8B7355' },
      { d: 'M10,30 h80 v10 h-80 z', fill: '#A68A64' }
    ],
    lantern: [
      { type: 'ellipse', cx: 50, cy: 30, rx: 15, ry: 10, fill: '#A68A64' },
      { d: 'M35,30 h30 v40 h-30 z', rx: 6, fill: '#A68A64' },
      { type: 'ellipse', cx: 50, cy: 70, rx: 10, ry: 6, fill: '#8B7355' }
    ]
  };
  selectedIconPaths.value = icons[iconName] || defaultAvatarPaths;
}

//获取用户信息
const fetchUserStats = async () => {
  try {
    const response = await getCurrentUserInfo(); // 调用后端API
    if (response.code === 1) {
      // 假设后端返回的数据结构中包含经验值相关信息
      userStats.value = {
        exp: response.data.experience,
        maxExp: response.data.maxExp || 500
      };

      // 设置头像
      if (response.data.avatarUrl) {
        userAvatar.value = { type: 'image', url: response.data.avatarUrl };
      } else if (response.data.iconName) {
        userAvatar.value = { type: 'builtin', iconName: response.data.iconName };
        setSelectedIconPaths(response.data.iconName);
      } else {
        userAvatar.value = { type: 'builtin', iconName: 'default' };
        selectedIconPaths.value = defaultAvatarPaths;
      }
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    console.error('获取用户信息失败:', error);
    // 使用默认值
    userStats.value = {
      exp: 0,
      maxExp: 500
    };
  }
}

// 辅助函数（与 MapComponent 保持一致）
const getSiteTypeText = (type) => {
  const map = {
    handicraft: '手工艺',
    museum: '博物馆',
    archaeology: '考古遗址',
    architecture: '传统建筑',
    food_craft: '食品工艺',
    performance: '表演艺术'
  }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = {
    unlocked: '已解锁',
    locked: '未解锁',
    visited: '已访问'
  }
  return map[status] || status
}

// 方法
function goToProfile() {
  router.push('/user/home')
}

function expandMap() {
  isMapExpanded.value = true
  document.body.style.overflow = 'hidden'
}

function collapseMap() {
  isMapExpanded.value = false
  selectedSite.value = null
  document.body.style.overflow = ''
}

function onSiteClicked(site) {
  selectedSite.value = site
}

function closeDetail() {
  selectedSite.value = null
}

function startAR() {
  router.push('/user/ceramicAr');//后续完善成能够立体展示的ar
  closeDetail()
}
//组件挂载时获取用户信息
onMounted(() => {
  fetchUserStats();
})
</script>

<style scoped>
/* —— 卡片态 —— */
.map-card {
  width: 100%;
  max-width: 320px;
  height: 140px;
  background: linear-gradient(135deg, #e8f4f8 0%, #d1e8f0 100%);
  border-radius: 12px;
  margin: 0 auto 24px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.map-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  
  background-size: 60%;
  opacity: 0.1;
}

.map-card-content {
  position: absolute;
  bottom: 16px;
  left: 16px;
  color: #a68a64;
}

.map-card-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
}

.map-card-hint {
  font-size: 12px;
  opacity: 0.8;
}

/* —— 全屏地图 —— */
.fullscreen-map-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1000;
}

/* 👇 关键：强制 MapComponent 内部的地图容器占满 */
.fullscreen-map-wrapper :deep(.map-root-container) {
  width: 100% !important;
  height: 100% !important;
  border-radius: 0 !important;
}

/* 关闭按钮 */
.close-map-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #a68a64;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 2001;
  transition: all 0.2s;
}

.close-map-btn:hover {
  background: white;
  transform: scale(1.1);
}

/* 用户状态 */
.user-stats {
  display: flex;
  justify-content: center;
  gap: 24px;
  color: #8c7b6b;
  font-size: 14px;
  margin: 20px 0;
}

/* 弹窗（复用你原有样式） */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: #ffffff;
  width: 90%;
  max-width: 340px;
  border-radius: 16px 16px 0 0;
  padding: 24px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 22px;
  font-weight: 700;
  color: #3a3530;
}

.close-modal {
  background: none;
  border: none;
  font-size: 20px;
  color: #8c7b6b;
  cursor: pointer;
  width: 30px;
  height: 30px;
}

.modal-image {
  width: 100%;
  height: 100px;
  background: #efeae5;
  border-radius: 10px;
  margin: 12px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c7b6b;
  font-size: 14px;
}

.modal-desc {
  font-size: 15px;
  line-height: 1.6;
  color: #5a524a;
  margin: 16px 0;
}

.ar-button {
  background: linear-gradient(135deg, #a68a64, #8b7355);
  color: white;
  border: none;
  border-radius: 12px;
  width: 100%;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  font-family: "Noto Serif SC", serif;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: transform 0.2s;
}

.ar-button:hover {
  transform: scale(1.02);
}

/* 头像按钮 */
.profile-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #a68a64;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f0ede9;
  overflow: hidden;
}

.header-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-btn:hover {
  background-color: rgba(166, 138, 100, 0.1);
}
</style>