<template>
  <div class="student-profile-container">
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
        title="个人中心"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 个人信息卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="profile-avatar">
              <el-avatar size="100" :icon="'i-ep-user-filled'" class="avatar" />
              <h2 class="profile-name">{{ username }}</h2>
              <p class="profile-role">{{ roleText }}</p>
            </div>
            <div class="profile-actions">
              <el-button
                type="primary"
                size="default"
                @click="editProfile"
                class="edit-profile-btn"
              >
                <template #icon>
                  <el-icon><i-ep-edit /></el-icon>
                </template>
                编辑资料
              </el-button>
            </div>
          </div>

          <!-- 个人信息详情 -->
          <div class="profile-details">
            <div class="detail-section">
              <h3>基本信息</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">姓名：</span>
                  <span class="detail-value">{{ userInfo.name || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">性别：</span>
                  <span class="detail-value">{{ userInfo.gender || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">年龄：</span>
                  <span class="detail-value">{{ userInfo.age || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">出生日期：</span>
                  <span class="detail-value">{{ userInfo.birthDate || '未设置' }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>联系方式</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">邮箱：</span>
                  <span class="detail-value">{{ userInfo.email || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">电话：</span>
                  <span class="detail-value">{{ userInfo.phone || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">地址：</span>
                  <span class="detail-value">{{ userInfo.address || '未设置' }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>教育背景</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">学校：</span>
                  <span class="detail-value">{{ userInfo.school || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">专业：</span>
                  <span class="detail-value">{{ userInfo.major || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学历：</span>
                  <span class="detail-value">{{ userInfo.education || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">毕业时间：</span>
                  <span class="detail-value">{{ userInfo.graduationDate || '未设置' }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>求职意向</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">期望职位：</span>
                  <span class="detail-value">{{ userInfo.desiredJob || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">期望薪资：</span>
                  <span class="detail-value">{{ userInfo.desiredSalary || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">期望地点：</span>
                  <span class="detail-value">{{ userInfo.desiredLocation || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">工作类型：</span>
                  <span class="detail-value">{{ userInfo.jobType || '未设置' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 技能与证书 -->
        <div class="skills-certifications">
          <div class="section-card">
            <h3>技能</h3>
            <div class="skills-list">
              <el-tag
                v-for="skill in userInfo.skills"
                :key="skill"
                size="medium"
                class="skill-tag"
              >
                {{ skill }}
              </el-tag>
              <el-tag
                v-if="userInfo.skills.length === 0"
                size="medium"
                type="info"
                class="skill-tag"
              >
                暂无技能
              </el-tag>
            </div>
          </div>

          <div class="section-card">
            <h3>证书</h3>
            <div class="certifications-list">
              <div
                v-for="cert in userInfo.certifications"
                :key="cert.name"
                class="certification-item"
              >
                <h4>{{ cert.name }}</h4>
                <p>{{ cert.issuer }} - {{ cert.date }}</p>
              </div>
              <div v-if="userInfo.certifications.length === 0" class="empty-certifications">
                <p>暂无证书</p>
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
const username = ref(localStorage.getItem("username") || "学生");
const role = ref(localStorage.getItem("role") || "STUDENT");

// 计算用户角色文本
const roleText = computed(() => {
  switch (role.value) {
    case "ADMIN":
      return "管理员";
    case "STUDENT":
      return "学生";
    case "COMPANY":
      return "企业";
    default:
      return "用户";
  }
});

// 用户信息
const userInfo = ref({
  name: "张三",
  gender: "男",
  age: 22,
  birthDate: "2004-01-01",
  email: "zhangsan@example.com",
  phone: "13800138000",
  address: "北京市海淀区",
  school: "北京大学",
  major: "计算机科学与技术",
  education: "本科",
  graduationDate: "2026-06",
  desiredJob: "前端开发工程师",
  desiredSalary: "15K-20K",
  desiredLocation: "北京",
  jobType: "全职",
  skills: ["Vue.js", "React", "JavaScript", "HTML/CSS", "Node.js"],
  certifications: [
    {
      name: "计算机等级考试二级",
      issuer: "教育部考试中心",
      date: "2024-03"
    },
    {
      name: "英语四级",
      issuer: "教育部考试中心",
      date: "2023-12"
    }
  ]
});

// 编辑资料
const editProfile = () => {
  console.log("编辑资料");
  // 这里可以打开编辑资料对话框
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
  // 页面加载时的初始化逻辑
  console.log("Student Profile mounted");
});
</script>

<style scoped>
/* 全局样式 */
.student-profile-container {
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
  overflow-y: auto;
  padding: 30px;
}

/* 个人信息卡片 */
.profile-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 30px;
  margin-bottom: 30px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar {
  background: linear-gradient(135deg, #3498db, #2980b9);
  font-size: 40px;
}

.profile-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.profile-role {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.profile-actions {
  display: flex;
  gap: 12px;
}

/* 个人信息详情 */
.profile-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  background: #f9f9f9;
}

.detail-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e9ecef;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  min-width: 80px;
}

.detail-value {
  font-size: 14px;
  color: #333;
  flex: 1;
}

/* 技能与证书 */
.skills-certifications {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.section-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.section-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e9ecef;
}

.skills-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  margin-bottom: 8px;
}

.certifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.certification-item {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
  border-left: 3px solid #3498db;
}

.certification-item h4 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.certification-item p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.empty-certifications {
  padding: 20px;
  text-align: center;
  color: #999;
  background: #f9f9f9;
  border-radius: 6px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-content {
    padding: 20px;
  }

  .profile-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .skills-certifications {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .main-content {
    width: 100%;
  }

  .page-content {
    padding: 16px;
  }

  .profile-card {
    padding: 20px;
  }

  .detail-section {
    padding: 16px;
  }
}
</style>