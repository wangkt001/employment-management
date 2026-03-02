<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <div class="login-header">
        <h1>大学生就业信息管理系统</h1>
        <p>请登录以访问系统</p>
      </div>
      <div class="login-form">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-user"></i>
            <input
              type="text"
              v-model="form.username"
              placeholder="请输入用户名"
              class="custom-input"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-lock"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="form.password"
              placeholder="请输入密码"
              class="custom-input"
            />
            <i
              :class="[
                'password-toggle',
                showPassword ? 'i-ep-eye-off' : 'i-ep-eye',
              ]"
              @click="showPassword = !showPassword"
            ></i>
          </div>
        </div>
        <div class="form-item remember-item">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
        </div>
        <div class="form-item" v-if="error">
          <el-alert :title="error" type="error" show-icon :closable="false" />
        </div>
        <div class="form-item">
          <button
            type="button"
            @click="login"
            :disabled="isLoading"
            class="login-button"
          >
            {{ isLoading ? "登录中..." : "登录" }}
          </button>
        </div>
      </div>
      <div class="login-footer">
        <p>还没有账号？<a href="#">点击注册</a></p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const form = ref({
  username: "",
  password: "",
  remember: false,
});
const error = ref("");
const isLoading = ref(false);
const showPassword = ref(false);

// 处理URL中的错误参数
onMounted(() => {
  const errorParam = route.query.error;
  if (errorParam) {
    if (errorParam === "session-expired") {
      error.value = "会话已过期，请重新登录";
    } else {
      error.value = decodeURIComponent(errorParam);
    }
  }
});

const login = async () => {
  error.value = "";
  isLoading.value = true;

  try {
    // 尝试获取用户信息
    const userInfoResponse = await fetch("/employment/api/admin/users", {
      credentials: "include",
    });

    let isAdmin = false;
    let isStudent = false;
    let isCompany = false;

    if (userInfoResponse.ok) {
      const users = await userInfoResponse.json();
      console.log("获取到的用户列表:", users);
      const currentUser = users.find(
        (user) => user.username === form.value.username,
      );
      console.log("当前用户信息:", currentUser);

      if (currentUser) {
        // 根据用户角色设置权限
        isAdmin =
          currentUser.role === "ADMIN" ||
          currentUser.role === "admin" ||
          currentUser.role === "Admin";
        isStudent =
          currentUser.role === "STUDENT" ||
          currentUser.role === "student" ||
          currentUser.role === "Student";
        isCompany =
          currentUser.role === "COMPANY" ||
          currentUser.role === "company" ||
          currentUser.role === "Company";
      } else {
        // 如果没有找到用户，使用默认逻辑
        isAdmin = form.value.username === "admin";
        isStudent = form.value.username === "student";
        isCompany =
          form.value.username === "company" || form.value.username === "yunsi";
      }
    } else {
      // 如果获取用户信息失败，使用默认逻辑
      isAdmin = form.value.username === "admin";
      isStudent = form.value.username === "student";
      isCompany =
        form.value.username === "company" || form.value.username === "yunsi";
    }

    // 存储用户信息
    localStorage.setItem("token", "mock-token");
    localStorage.setItem("username", form.value.username);
    localStorage.setItem("isAdmin", isAdmin.toString());
    localStorage.setItem("isStudent", isStudent.toString());
    localStorage.setItem("isCompany", isCompany.toString());

    console.log("登录成功，localStorage存储:", {
      token: localStorage.getItem("token"),
      username: localStorage.getItem("username"),
      isAdmin: localStorage.getItem("isAdmin"),
      isStudent: localStorage.getItem("isStudent"),
      isCompany: localStorage.getItem("isCompany"),
    });

    // 无论后端登录是否成功，都跳转到仪表盘
    router.push("/dashboard");
  } catch (err) {
    console.error("登录请求失败:", err);

    // 即使发生错误，也使用默认逻辑设置用户角色
    const isAdmin = form.value.username === "admin";
    const isStudent = form.value.username === "student";
    const isCompany =
      form.value.username === "company" || form.value.username === "yunsi";

    // 存储用户信息
    localStorage.setItem("token", "mock-token");
    localStorage.setItem("username", form.value.username);
    localStorage.setItem("isAdmin", isAdmin.toString());
    localStorage.setItem("isStudent", isStudent.toString());
    localStorage.setItem("isCompany", isCompany.toString());

    console.log("模拟登录成功，localStorage存储:", {
      token: localStorage.getItem("token"),
      username: localStorage.getItem("username"),
      isAdmin: localStorage.getItem("isAdmin"),
      isStudent: localStorage.getItem("isStudent"),
      isCompany: localStorage.getItem("isCompany"),
    });

    // 跳转到仪表盘
    router.push("/dashboard");
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 10px;
  overflow: hidden;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 0 20px;
}

.login-header h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.login-form {
  padding: 0 20px 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  color: #666;
  font-size: 16px;
  z-index: 1;
}

.password-toggle {
  position: absolute;
  right: 12px;
  color: #666;
  font-size: 16px;
  cursor: pointer;
  z-index: 1;
  transition: color 0.3s ease;
}

.password-toggle:hover {
  color: #667eea;
}

.custom-input {
  width: 100%;
  padding: 12px 40px 12px 36px;
  border: 1px solid #333;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: transparent;
  outline: none;
  transition: all 0.3s ease;

  /* 关键：使用box-shadow技巧解决自动填充问题 */
  box-shadow: 0 0 0 30px rgba(255, 255, 255, 0) inset;
  -webkit-box-shadow: 0 0 0 30px rgba(255, 255, 255, 0) inset;
  -moz-box-shadow: 0 0 0 30px rgba(255, 255, 255, 0) inset;
}

.custom-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.custom-input::placeholder {
  color: #999;
}

.remember-item {
  text-align: left;
}

.login-button {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  padding: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
  padding: 0 20px 20px;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.login-footer a:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    margin: 20px;
  }

  .login-header h1 {
    font-size: 20px;
  }
}

/* 全局样式覆盖，解决自动填充问题 */
:global(input:-webkit-autofill),
:global(input:-webkit-autofill:hover),
:global(input:-webkit-autofill:focus),
:global(input:-webkit-autofill:active) {
  background-color: transparent !important;
  -webkit-text-fill-color: #333 !important;
  transition: background-color 5000s ease-in-out 0s !important;
  box-shadow: 0 0 0 30px rgba(255, 255, 255, 0) inset !important;
}

:global(input:-moz-autofill),
:global(input:-moz-autofill:hover),
:global(input:-moz-autofill:focus),
:global(input:-moz-autofill:active) {
  background-color: transparent !important;
  color: #333 !important;
  box-shadow: 0 0 0 30px rgba(255, 255, 255, 0) inset !important;
}

:global(input:-internal-autofill-selected) {
  background-color: transparent !important;
  background-image: none !important;
  color: #333 !important;
}
</style>
