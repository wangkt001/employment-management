<template>
  <el-header height="65px" class="top-nav">
    <div class="nav-left">
      <el-button
        type="primary"
        :icon="'i-ep-menu'"
        circle
        @click="toggleSidebar"
        size="small"
        class="menu-toggle"
      />
      <h1>{{ title }}</h1>
    </div>
    <div class="nav-right">
      <el-dropdown>
        <div class="user-info">
          <el-avatar size="small" icon="i-ep-user-filled" />
          <span class="username">{{ username }}</span>
          <el-tag size="small" :type="roleType" class="user-role">{{
            roleText
          }}</el-tag>
          <el-icon class="arrow-icon"><i-ep-arrow-down /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item divided @click="logout"
              >退出登录</el-dropdown-item
            >
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  title: {
    type: String,
    default: "岗位管理",
  },
  username: {
    type: String,
    default: () => localStorage.getItem("username") || "用户",
  },
  role: {
    type: String,
    default: () => localStorage.getItem("role") || "STUDENT",
  },
});

const emit = defineEmits(["toggle-sidebar"]);

const roleType = computed(() => {
  switch (props.role) {
    case "ADMIN":
      return "danger";
    case "STUDENT":
      return "primary";
    case "COMPANY":
      return "success";
    default:
      return "info";
  }
});

const roleText = computed(() => {
  switch (props.role) {
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

const toggleSidebar = () => {
  emit("toggle-sidebar");
};

const logout = () => {
  localStorage.clear();
  router.push("/login");
};
</script>

<style scoped>
/* 顶部导航栏样式 */
.top-nav {
  background: white;
  padding: 0 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 100;
}

.top-nav::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #2ecc71, #3498db);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.top-nav:hover::after {
  transform: scaleX(1);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-toggle {
  transition: all 0.3s ease;
}

.menu-toggle:hover {
  transform: rotate(90deg);
}

.nav-left h1 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  background: #f5f7fa;
  border-radius: 25px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-info:hover {
  background: #e8f5e8;
  box-shadow: 0 2px 8px rgba(46, 204, 113, 0.2);
}

.username {
  font-weight: 500;
  color: #333;
  transition: all 0.3s ease;
}

.arrow-icon {
  font-size: 12px;
  transition: all 0.3s ease;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-nav {
    padding: 0 20px;
  }

  .nav-left h1 {
    font-size: 18px;
  }

  .username {
    display: none;
  }
}
</style>
