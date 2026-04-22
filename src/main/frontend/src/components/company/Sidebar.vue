<template>
  <el-menu
    :default-active="activeIndex"
    class="sidebar-menu"
    :collapse="sidebarCollapsed"
    background-color="#2c3e50"
    text-color="rgba(255, 255, 255, 0.8)"
    active-text-color="#2ecc71"
    router
  >
    <div class="sidebar-header">
      <h2 v-show="!sidebarCollapsed">就业信息管理系统</h2>
    </div>

    <!-- 公共菜单 -->
    <el-menu-item index="/dashboard">
      <template #icon>
        <el-icon><i-ep-home /></el-icon>
      </template>
      <span>首页</span>
    </el-menu-item>

    <!-- 管理员菜单 -->
    <template v-if="isAdmin">
      <el-menu-item index="/admin/users">
        <template #icon>
          <el-icon><i-ep-user /></el-icon>
        </template>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/companies">
        <template #icon>
          <el-icon><i-ep-office-building /></el-icon>
        </template>
        <span>企业管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/statistics">
        <template #icon>
          <el-icon><i-ep-data-analysis /></el-icon>
        </template>
        <span>统计分析</span>
      </el-menu-item>
    </template>

    <!-- 企业菜单 -->
    <template v-if="isCompany">
      <el-menu-item index="/company/jobs">
        <template #icon>
          <el-icon><i-ep-briefcase /></el-icon>
        </template>
        <span>岗位管理</span>
      </el-menu-item>
      <el-menu-item index="/company/applications">
        <template #icon>
          <el-icon><i-ep-document /></el-icon>
        </template>
        <span>申请管理</span>
      </el-menu-item>
      <el-menu-item index="/company/profile">
        <template #icon>
          <el-icon><i-ep-office-building /></el-icon>
        </template>
        <span>企业资料</span>
      </el-menu-item>
    </template>

    <!-- 学生菜单 -->
    <template v-if="isStudent">
      <el-menu-item index="/student/jobs">
        <template #icon>
          <el-icon><i-ep-briefcase /></el-icon>
        </template>
        <span>就业岗位</span>
      </el-menu-item>
      <el-menu-item index="/student/applications">
        <template #icon>
          <el-icon><i-ep-document /></el-icon>
        </template>
        <span>我的申请</span>
      </el-menu-item>
      <el-menu-item index="/student/profile">
        <template #icon>
          <el-icon><i-ep-user /></el-icon>
        </template>
        <span>我的简历</span>
      </el-menu-item>
    </template>

    <!-- 退出登录 -->
    <el-menu-item index="logout" @click="logout">
      <template #icon>
        <el-icon><i-ep-switch-button /></el-icon>
      </template>
      <span>退出登录</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["toggle-sidebar", "logout"]);

const sidebarCollapsed = ref(props.collapsed);

// 获取用户角色
const isAdmin = ref(localStorage.getItem("isAdmin") === "true");
const isStudent = ref(localStorage.getItem("isStudent") === "true");
const isCompany = ref(localStorage.getItem("isCompany") === "true");

const activeIndex = computed(() => {
  return route.path || "/dashboard";
});

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
  emit("toggle-sidebar");
};

const logout = () => {
  // 清除本地存储
  localStorage.clear();
  sessionStorage.clear();

  // 跳转到登录页
  router.push("/login");
  emit("logout");
};
</script>

<style scoped>
.sidebar-menu {
  height: 100vh;
  border-right: none;
  background: linear-gradient(180deg, #2c3e50 0%, #1a252f 100%);
  box-shadow: 2px 0 15px rgba(0, 0, 0, 0.15);
  position: relative;
}

.sidebar-menu::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #2ecc71, #3498db);
}

.sidebar-header {
  padding: 25px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.sidebar-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: white;
  transition: all 0.3s ease;
}

.el-menu-item {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-left: 3px solid transparent;
  margin: 0 10px;
  border-radius: 4px;
  height: 50px;
  line-height: 50px;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  transform: translateX(5px);
}

.el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.15) !important;
  border-left-color: #2ecc71;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-menu {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 200;
    width: 260px;
  }
}
</style>
