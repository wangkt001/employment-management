<template>
  <div class="dashboard-container">
    <!-- 侧边栏 -->
    <Sidebar
      :collapsed="sidebarCollapsed"
      @toggle-sidebar="toggleSidebar"
      @logout="logout"
    />

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航栏 -->
      <TopNav
        title="仪表盘"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 仪表盘内容 -->
      <div class="page-content">
        <div class="welcome-section">
          <h2>欢迎回来，{{ username }}！</h2>
          <p>这是您的系统仪表盘，包含关键数据概览。</p>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon users-icon">
              <i class="icon users-icon"></i>
            </div>
            <div class="stat-content">
              <h3>总用户数</h3>
              <p class="stat-value">{{ stats.totalUsers || 0 }}</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon briefcase-icon">
              <i class="icon briefcase-icon"></i>
            </div>
            <div class="stat-content">
              <h3>就业岗位</h3>
              <p class="stat-value">{{ stats.totalJobs || 0 }}</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon file-icon">
              <i class="icon file-icon"></i>
            </div>
            <div class="stat-content">
              <h3>投递记录</h3>
              <p class="stat-value">{{ stats.totalApplications || 0 }}</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon company-icon">
              <i class="icon company-icon"></i>
            </div>
            <div class="stat-content">
              <h3>企业数</h3>
              <p class="stat-value">{{ stats.totalCompanies || 0 }}</p>
            </div>
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="recent-activity">
          <h3>最近活动</h3>
          <div class="activity-list">
            <div v-if="isLoading" class="loading-state">
              <el-loading type="spinner" text="加载中..." />
            </div>
            <div v-else-if="activities.length === 0" class="empty-state">
              <el-empty description="暂无活动记录" />
            </div>
            <div
              v-else
              v-for="activity in activities"
              :key="activity.id"
              class="activity-item"
            >
              <div :class="['activity-icon', `${activity.type}-icon`]">
                <i :class="['icon', `${activity.type}-icon`]"></i>
              </div>
              <div class="activity-content">
                <p>{{ activity.message }}</p>
                <span class="activity-time">{{ activity.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "用户");
const isAdmin = ref(localStorage.getItem("isAdmin") === "true");
const isStudent = ref(localStorage.getItem("isStudent") === "true");
const isCompany = ref(localStorage.getItem("isCompany") === "true");
const role = ref(localStorage.getItem("role") || "STUDENT");

// 活动数据
const activities = ref([]);
const isLoading = ref(false);

// 统计数据
const stats = ref({
  totalUsers: 0,
  totalJobs: 0,
  totalApplications: 0,
  totalCompanies: 0
});

// 计算用户角色文本和样式
const roleText = computed(() => {
  if (isAdmin.value) return "管理员";
  if (isStudent.value) return "学生";
  if (isCompany.value) return "企业";
  return "用户";
});

const roleClass = computed(() => {
  if (isAdmin.value) return "role-admin";
  if (isStudent.value) return "role-student";
  if (isCompany.value) return "role-company";
  return "";
});

// 生成模拟活动数据
const generateActivities = () => {
  const now = new Date();
  const activitiesList = [];

  if (isAdmin.value) {
    // 管理员活动
    activitiesList.push(
      {
        id: 1,
        type: "success",
        message: "您成功登录系统",
        time: formatTime(now),
      },
      {
        id: 2,
        type: "info",
        message: '新用户 "张三" 注册成功',
        time: formatTime(new Date(now.getTime() - 3600000)), // 1小时前
      },
      {
        id: 3,
        type: "info",
        message: '企业 "阿里巴巴" 发布了 3 个新岗位',
        time: formatTime(new Date(now.getTime() - 7200000)), // 2小时前
      },
      {
        id: 4,
        type: "warning",
        message: '用户 "李四" 账号已被禁用',
        time: formatTime(new Date(now.getTime() - 86400000)), // 1天前
      },
    );
  } else if (isCompany.value) {
    // 企业活动
    activitiesList.push(
      {
        id: 1,
        type: "success",
        message: "您成功登录系统",
        time: formatTime(now),
      },
      {
        id: 2,
        type: "info",
        message: "收到 2 份新的简历投递",
        time: formatTime(new Date(now.getTime() - 1800000)), // 30分钟前
      },
      {
        id: 3,
        type: "success",
        message: '岗位 "前端开发工程师" 已成功发布',
        time: formatTime(new Date(now.getTime() - 3600000)), // 1小时前
      },
      {
        id: 4,
        type: "warning",
        message: '岗位 "后端开发工程师" 已过期',
        time: formatTime(new Date(now.getTime() - 86400000)), // 1天前
      },
    );
  } else if (isStudent.value) {
    // 学生活动
    activitiesList.push(
      {
        id: 1,
        type: "success",
        message: "您成功登录系统",
        time: formatTime(now),
      },
      {
        id: 2,
        type: "info",
        message: '您投递的岗位 "前端开发工程师" 已被查看',
        time: formatTime(new Date(now.getTime() - 1800000)), // 30分钟前
      },
      {
        id: 3,
        type: "success",
        message: '您收到了 "阿里巴巴" 的面试邀请',
        time: formatTime(new Date(now.getTime() - 7200000)), // 2小时前
      },
      {
        id: 4,
        type: "info",
        message: "为您推荐了 5 个匹配的岗位",
        time: formatTime(new Date(now.getTime() - 86400000)), // 1天前
      },
    );
  } else {
    // 通用活动
    activitiesList.push(
      {
        id: 1,
        type: "success",
        message: "您成功登录系统",
        time: formatTime(now),
      },
      {
        id: 2,
        type: "info",
        message: "系统已更新至最新版本",
        time: formatTime(new Date(now.getTime() - 3600000)), // 1小时前
      },
      {
        id: 3,
        type: "warning",
        message: "您有 2 条未读通知",
        time: formatTime(new Date(now.getTime() - 86400000)), // 1天前
      },
    );
  }

  return activitiesList;
};

// 格式化时间
const formatTime = (date) => {
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days === 1) return "昨天";
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 退出登录
const logout = () => {
  localStorage.clear();
  router.push("/login");
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await fetch("/employment/api/admin/dashboard", {
      credentials: "include",
    });
    if (response.ok) {
      const data = await response.json();
      stats.value = data;
    }
  } catch (error) {
    console.error("获取统计数据失败:", error);
  }
};

onMounted(async () => {
  // 页面加载时的初始化逻辑
  console.log("Dashboard mounted");
  // 生成活动数据
  activities.value = generateActivities();
  // 获取统计数据
  await fetchStats();
});
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background: #f8f9fa;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #ffffff;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.05);
}

/* 页面内容 */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 30px;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.welcome-section p {
  color: #666;
  font-size: 16px;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 15px;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.users-icon {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.briefcase-icon {
  background: linear-gradient(135deg, #2ecc71, #27ae60);
}

.file-icon {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.company-icon {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
}

.stat-content h3 {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 最近活动样式 */
.recent-activity {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.recent-activity h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background: #f9f9f9;
  transition: background 0.3s ease;
}

.activity-item:hover {
  background: #f0f0f0;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.success-icon {
  background: #2ecc71;
}

.info-icon {
  background: #3498db;
}

.warning-icon {
  background: #f39c12;
}

.danger-icon {
  background: #e74c3c;
}

.activity-content p {
  color: #333;
  font-size: 14px;
  margin-bottom: 5px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

/* 加载和空状态 */
.loading-state {
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 200;
    transform: translateX(-100%);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .page-content {
    padding: 20px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card,
.activity-item {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(2) {
  animation-delay: 0.1s;
}

.stat-card:nth-child(3) {
  animation-delay: 0.2s;
}

.stat-card:nth-child(4) {
  animation-delay: 0.3s;
}

.activity-item:nth-child(2) {
  animation-delay: 0.1s;
}

.activity-item:nth-child(3) {
  animation-delay: 0.2s;
}
</style>
