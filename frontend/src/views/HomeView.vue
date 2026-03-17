<template>
  <Layout title="文迹 - 探索文化遗产" :showBack="false">
    <div class="home-container">
      <!-- 用户头像 -->
      <div class="user-avatar" @click="goToProfile">
        <img v-if="userAvatar.type === 'image'" :src="userAvatar.url" class="avatar-img" />
        <svg v-else width="30" height="30" viewBox="0 0 100 100">
          <g v-for="(path, index) in selectedIconPaths" :key="index">
            <path v-if="path.d" :d="path.d" :fill="path.fill" :opacity="path.opacity || 1" />
            <ellipse v-else-if="path.type === 'ellipse'" :cx="path.cx" :cy="path.cy" :rx="path.rx" :ry="path.ry" :fill="path.fill" />
          </g>
        </svg>
      </div>

      <!-- 内容区域 -->
      <div class="content">
        <h1 class="main-title">欢迎探索非遗文化</h1>

        <!-- 功能卡片 -->
        <div v-for="(feature, index) in features" :key="index" class="feature-card" @click="handleFeatureClick(feature.id)">
          <h2>{{ feature.title }}</h2>
          <p>{{ feature.description }}</p>
        </div>

        <!-- 成就展示区 -->
        <div class="achievements">
          <div class="achievements-title">已解锁成就</div>
          <div 
            v-for="(achievement, index) in achievements" 
            :key="index" 
            class="achievement-item"
          >
            <h3>{{ achievement.title }}</h3>
            <p>{{ achievement.description }}</p>
          </div>
        </div>

        
      </div><!-- 底部分割线与引文 -->
        <div class="divider"></div>
    </div>
  </Layout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getCurrentUserInfo } from '@/api/user'

const router = useRouter()

const userAvatar = ref({ type: 'builtin', iconName: 'default' })
const selectedIconPaths = ref([])

// 默认头像路径 - 青瓷瓶
const defaultAvatarPaths = [
  { d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64', opacity: '0.9' },
  { type: 'ellipse', cx: 50, cy: 75, rx: 25, ry: 8, fill: '#8B7355' }
]

const loadUserInfo = async () => {
  try {
    const response = await getCurrentUserInfo();
    if (response.code === 1) {
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
    console.error('获取用户信息失败:', error);
    selectedIconPaths.value = defaultAvatarPaths;
  }
}

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

onMounted(() => {
  loadUserInfo();
})

const features = [
  {
    id: 'ai-guide',
    title: 'AI导游讲解',
    description: '通过智能导游，深入了解每一个文化景点背后的故事。'
  },
  {
    id: 'ar-display',
    title: 'AR虚拟展示',
    description: '利用增强现实技术，身临其境感受非遗文化的魅力。'
  },
  {
    id: 'map-record',
    title: '地图点亮记录',
    description: '每当你走过一个地方，地图上就会点亮一个新的地标。'
  }
]

const achievements = [
  {
    title: '文化使者',
    description: '成功完成三次AI导游讲解任务。'
  },
  {
    title: '历史探寻者',
    description: '参观了五个不同的历史文化景点。'
  }
]

function goToProfile() {
  // 跳转到用户个人资料页
  console.log('跳转到个人资料页');
  router.push('/user/profile');
}

function handleFeatureClick(featureId) {
  switch(featureId) {
    case 'ai-guide':
      // 跳转到AI导游页面
      console.log('跳转到AI导游页面');
      router.push('/user/aiguide');
      break;
    case 'ar-display':
      // 跳转到AR体验页面
      console.log('跳转到AR体验页面');
      router.push('/user/ceramicAr');
      break;
    case 'map-record':
      // 跳转到地图页面
      console.log('跳转到地图页面');
      router.push('/user/map');
      break;
    default:
      break;
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home-container {
  background-color: #F8F5F0;
  font-family: "Noto Serif SC", serif;
  color: #3A3530;
  width: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 用户头像 */
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex; /* 改为 flex 以便更好地处理图片和SVG */
  align-items: center;
  justify-content: center;
  background-color: #A68A64;
  cursor: pointer;
  align-self: flex-end;
  margin-bottom: -10px;
  overflow: hidden; /* 确保图片不超出圆角 */
  border: 2px solid #FFFFFF;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 内容区域 */
.content {
  width: 100%;
  padding: 20px 0;
}

.main-title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 20px;
}

/* 功能卡片 */
.feature-card {
  background-color: #FFFFFF;
  border: 1px solid #C4B8A8;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  transition: transform 0.2s ease-in-out;
  cursor: pointer;
}

.feature-card:hover {
  transform: scale(1.02);
}

.feature-card h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 10px;
}

.feature-card p {
  font-size: 16px;
  color: #8C7B6B;
}

/* 成就展示区 */
.achievements {
  margin-top: 40px;
}

.achievements-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 10px;
}

.achievement-item {
  background-color: #FFFFFF;
  border: 1px solid #C4B8A8;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
}

.achievement-item h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 5px;
}

.achievement-item p {
  font-size: 14px;
  color: #8C7B6B;
}

/* 底部分割线 */
.divider {
  width: 80%;
  height: 0.5px;
  background-color: #C4B8A8;
  margin: 20px auto;
}

</style>