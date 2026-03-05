<template>
  <div class="statistics-container">
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
        title="统计分析"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 统计分析内容 -->
      <div class="page-content">
        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>加载统计数据中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-container">
          <el-alert :title="error" type="error" show-icon :closable="false" />
          <el-button type="primary" @click="loadStats" style="margin-top: 20px">
            重试
          </el-button>
        </div>

        <!-- 统计数据 -->
        <div v-else>
          <!-- 核心统计指标 -->
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-icon users-icon">
                <i class="icon users-icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalUsers }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon jobs-icon">
                <i class="icon jobs-icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalJobs }}</div>
                <div class="stat-label">总岗位数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon applications-icon">
                <i class="icon applications-icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalApplications }}</div>
                <div class="stat-label">总申请数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon companies-icon">
                <i class="icon companies-icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalCompanies }}</div>
                <div class="stat-label">总企业数</div>
              </div>
            </div>
          </div>

          <!-- 简化的统计信息 -->
          <div class="summary-section">
            <div class="summary-card">
              <h3>系统概览</h3>
              <div class="summary-items">
                <div class="summary-item">
                  <span class="summary-label">学生用户</span>
                  <span class="summary-value">{{ studentCount }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">企业用户</span>
                  <span class="summary-value">{{ companyCount }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">岗位发布率</span>
                  <span class="summary-value">{{ jobPostingRate }}%</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">简历投递率</span>
                  <span class="summary-value">{{ applicationRate }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "管理员");
const role = ref(localStorage.getItem("role") || "ADMIN");

// 核心统计数据
const totalUsers = ref(0);
const totalJobs = ref(0);
const totalApplications = ref(0);
const totalCompanies = ref(0);

// 辅助统计数据
const studentCount = ref(0);
const companyCount = ref(0);
const jobPostingRate = ref(0);
const applicationRate = ref(0);

// 加载状态
const isLoading = ref(true);
const error = ref("");

// 加载统计数据
const loadStats = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    const response = await fetch("/employment/api/admin/dashboard", {
      credentials: "include",
    });
    if (response.ok) {
      const data = await response.json();
      totalUsers.value = data.totalUsers || 0;
      totalJobs.value = data.totalJobs || 0;
      totalApplications.value = data.totalApplications || 0;
      totalCompanies.value = data.totalCompanies || 0;
      studentCount.value = data.studentCount || 0;
      companyCount.value = data.companyCount || 0;
      jobPostingRate.value = data.jobPostingRate || 0;
      applicationRate.value = data.applicationRate || 0;
    } else {
      error.value = "获取统计数据失败";
    }
  } catch (err) {
    console.error("获取统计数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
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

onMounted(() => {
  // 页面加载时获取统计数据
  loadStats();
  console.log("Statistics mounted");
});
</script>

<style scoped>
.statistics-container {
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

/* 关键指标卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 20px;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.users-icon {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.stat-icon.jobs-icon {
  background: linear-gradient(135deg, #2ecc71, #27ae60);
}

.stat-icon.applications-icon {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.stat-icon.companies-icon {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 简化的统计信息 */
.summary-section {
  margin-bottom: 30px;
}

.summary-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.summary-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.summary-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-label {
  font-size: 14px;
  color: #666;
}

.summary-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
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

  .page-content {
    padding: 20px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .summary-items {
    grid-template-columns: 1fr;
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

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  gap: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-container p {
  font-size: 16px;
  color: #666;
}

/* 错误状态 */
.error-container {
  padding: 40px;
  text-align: center;
}

.stats-cards,
.summary-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card {
  animation: fadeInUp 0.3s ease-out;
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
</style>
