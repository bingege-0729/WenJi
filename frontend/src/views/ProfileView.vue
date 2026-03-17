<template>
  <Layout title="个人资料" :showBack="true">
    <div class="page">
      <!-- 用户身份 -->
      <div class="user-section">
        <button class="edit-btn" @click="goToEditProfile">✏️</button>    
        <!-- 用户头像 -->
        <div class="avatar">
          <img 
            v-if="userAvatar && userAvatar.type === 'image'" 
            :src="userAvatar.url" 
            alt="头像" 
            class="avatar-image"
          />
          <svg
            v-else
            width="100%"
            height="100%"
            viewBox="0 0 100 100"
          >
            <!-- 根据用户选择的内置图标显示 -->
            <g v-for="(path, index) in selectedIconPaths" :key="index">
              <path v-if="path.d" :d="path.d" :fill="path.fill" />
              <ellipse v-else-if="path.type === 'ellipse'" 
                       :cx="path.cx" :cy="path.cy" :rx="path.rx" :ry="path.ry" :fill="path.fill" />
            </g>
          </svg>
        </div>
        
        <div class="username">{{ username }}</div>
        <div class="level">{{ level }}</div>
        <div class="points">💰 积分: {{ points }}</div>
      </div>

      <!-- 文艺权益 -->
      <div class="benefits">
        <div class="benefit-title">🎁 已兑权益</div>
        <div 
          v-for="(voucher, index) in vouchers" 
          :key="index" 
          class="voucher-item"
        >
          <div class="voucher-name">{{ voucher.name }}</div>
          <div class="voucher-desc">{{ voucher.description }}</div>
        </div>
        <a href="#" class="go-to-shop" @click.prevent="goToShop">→ 前往文迹商城兑换更多</a>
      </div>

      <!-- 已点亮地点 (改为足迹博客展示) -->
      <div class="section-title">🔥 我的足迹（{{ travelBlogs.length }} 次打卡）</div>
      <div class="travel-blogs">
        <div 
          v-for="(blog, index) in travelBlogs" 
          :key="index" 
          class="blog-card"
        >
          <div class="blog-header">
            <span class="blog-site">📍 {{ blog.siteId }}</span>
            <span class="blog-date">{{ formatDate(blog.createTime) }}</span>
          </div>
          <div class="blog-title">{{ blog.title }}</div>
          <div class="blog-content">{{ blog.content }}</div>
          <div v-if="blog.images" class="blog-images">
             <!-- 解析 JSON 字符串显示图片 -->
             <img v-for="(img, idx) in parseImages(blog.images)" :key="idx" :src="img" class="blog-img" />
          </div>
        </div>
      </div>

      <!-- 徽章（圆形瓦当） -->
      <div class="section-title">🏆 我的徽章</div>
      <div class="badges">
        <div 
          v-for="(badge, index) in badges" 
          :key="index" 
          class="badge-item"
        >
          <div class="badge-icon">🏅</div>
          <div class="badge-name">{{ badge.name }}</div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { onActivated, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import Layout from '@/components/Layout.vue'
import { ElMessage } from 'element-plus';
import { getCurrentUserInfo, getUserBadges, getMyTravelBlogs } from '@/api/user';
const router = useRouter();

const username = ref('');
const level = ref('');
const points = ref(0);
const userAvatar = ref(null);
const selectedIconPaths = ref([]);
const badges = ref([]);
const travelBlogs = ref([]);

// 默认头像路径 - 青瓷瓶
const defaultAvatarPaths = [
  { d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64', opacity: '0.9' },
  { type: 'ellipse', cx: 50, cy: 75, rx: 25, ry: 8, fill: '#8B7355' }
];

const goToEditProfile = () => {
  router.push('/user/userinfo');
}

const goToShop = () => {
  ElMessage.info('暂未开放');
}

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString();
};

const parseImages = (jsonStr) => {
  try {
    return JSON.parse(jsonStr);
  } catch (e) {
    return [];
  }
};

const loadProfileData = async () => {
  try {
    const [userRes, badgeRes, blogRes] = await Promise.all([
      getCurrentUserInfo(),
      getUserBadges(),
      getMyTravelBlogs()
    ]);

    if (userRes.code === 1) {
      username.value = userRes.data.username || '...';
      level.value = userRes.data.level || '普通用户';
      points.value = userRes.data.points || 0;
      
      // 处理头像数据
      if (userRes.data.avatarUrl) {
        userAvatar.value = {
          type: 'image',
          url: userRes.data.avatarUrl
        };
      } else if (userRes.data.iconName) {
        userAvatar.value = {
          type: 'builtin',
          iconName: userRes.data.iconName
        };
        setSelectedIconPaths(userRes.data.iconName);
      } else {
        userAvatar.value = {
          type: 'builtin',
          iconName: 'default'
        };
        selectedIconPaths.value = defaultAvatarPaths;
      }
    }
    if (badgeRes.code === 1) badges.value = badgeRes.data;
    if (blogRes.code === 1) travelBlogs.value = blogRes.data;
  } catch (error) {
    console.error('加载个人主页数据失败:', error);
  }
};

onMounted(() => {
  loadProfileData();
});

onActivated(() => {
  loadProfileData();
});

// 根据图标名称设置路径
const setSelectedIconPaths = (iconName) => {
  // 这里可以根据不同的内置图标名称返回对应的路径
  const icons = {
    porcelain: [
      { d: 'M50,10 C30,10 20,30 20,50 L20,70 C20,85 30,90 50,90 C70,90 80,85 80,70 L80,50 C80,30 70,10 50,10 Z', fill: '#A68A64' }
    ],
    tea: [
      { d: 'M30,40 Q50,20 70,40 L65,70 Q50,80 35,70 Z', fill: '#8B7355' }
    ],
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

  if (icons[iconName]) {
    selectedIconPaths.value = icons[iconName];
  } else {
    // 如果找不到对应图标，使用默认图标
    selectedIconPaths.value = defaultAvatarPaths;
  }
  console.log('设置图标路径:', iconName, selectedIconPaths.value);
}

const vouchers = ref([
  {
    name: '唐三彩陶马周边',
    description: '景德镇博物馆文创中心领取'
  },
  {
    name: '古窑遗址体验券',
    description: '凭码至古窑民俗博览区核销'
  }
]);

const totalPlaces = ref(36);
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.page {
  width: 100%;
  max-width: 390px;
  height: 830px;
  background: white;
  padding: 28px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

/* —————— 用户身份 —————— */
.user-section {
  text-align: center;
  margin-bottom: 28px;
  position: relative;
}

.edit-btn {
  position: absolute;
  top: 0;
  right: 0;
  background: none;
  border: none;
  color: #A68A64;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
}

.avatar {
  width: 68px;
  height: 68px;
  margin-bottom: 12px;
  display: inline-block;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #a68a64;
  background: #efeae5;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
}

.level {
  font-size: 14px;
  color: #8C7B6B;
}

/* —————— 权益区 —————— */
.benefits {
  margin-bottom: 24px;
}

.benefit-title {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #5A524A;
}

.voucher-item {
  background: #FAF8F5;
  border-left: 3px solid #A68A64;
  padding: 10px 12px;
  margin-bottom: 8px;
  font-size: 15px;
  color: #3A3530;
  border-radius: 0 6px 6px 0;
}

.voucher-name {
  font-weight: 600;
}

.voucher-desc {
  font-size: 13px;
  color: #8C7B6B;
  margin-top: 4px;
}

.go-to-shop {
  display: block;
  text-align: center;
  color: #A68A64;
  font-size: 14px;
  margin-top: 12px;
  text-decoration: none;
}

/* —————— 地点 —————— */
.section-title {
  font-size: 17px;
  font-weight: 600;
  margin: 20px 0 12px;
  color: #5A524A;
}

.places {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.place-tag {
  background: #EFEAE5;
  color: #8C7B6B;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 替换圆点为小篆"迹"或墨点 */
.place-icon {
  font-size: 12px;
  color: #A68A64;
}

/* —————— 徽章（瓦当风格） —————— */
.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.badge-item {
  width: 68px;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.badge-icon {
  width: 50px;
  height: 50px;
  background: #FAF8F5;
  border: 1px solid #C4B8A8;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.badge-name {
  font-size: 11px;
  color: #8C7B6B;
  text-align: center;
}

/* —————— 足迹博客 —————— */
.travel-blogs {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.blog-card {
  background: #FAF8F5;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #EFEAE5;
}

.blog-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
}

.blog-site {
  color: #A68A64;
  font-weight: 600;
}

.blog-date {
  color: #999;
}

.blog-title {
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 6px;
  color: #3A3530;
}

.blog-content {
  font-size: 13px;
  color: #5A524A;
  line-height: 1.5;
  margin-bottom: 8px;
}

.blog-images {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.blog-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
</style>