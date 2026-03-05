<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <div class="login-header">
        <h1>大学生就业信息管理系统</h1>
        <p>{{ isRegister ? "请注册以访问系统" : "请登录以访问系统" }}</p>
      </div>

      <!-- 登录表单 -->
      <div v-if="!isRegister" class="login-form">
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

      <!-- 注册表单 -->
      <div v-else class="register-form">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-user"></i>
            <input
              type="text"
              v-model="registerForm.username"
              placeholder="请输入用户名"
              class="custom-input"
              @input="checkUsername"
            />
          </div>
          <div
            v-if="usernameCheckResult.message"
            class="username-check-result"
            :class="usernameCheckResult.exists ? 'error' : 'success'"
          >
            {{ isCheckingUsername ? "检查中..." : usernameCheckResult.message }}
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-lock"></i>
            <input
              :type="showRegisterPassword ? 'text' : 'password'"
              v-model="registerForm.password"
              placeholder="请输入密码"
              class="custom-input"
            />
            <i
              :class="[
                'password-toggle',
                showRegisterPassword ? 'i-ep-eye-off' : 'i-ep-eye',
              ]"
              @click="showRegisterPassword = !showRegisterPassword"
            ></i>
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">姓名</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-user-filled"></i>
            <input
              type="text"
              v-model="registerForm.name"
              placeholder="请输入姓名"
              class="custom-input"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">角色</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-avatar"></i>
            <el-select
              v-model="registerForm.role"
              placeholder="请选择角色"
              class="role-select"
            >
              <el-option label="学生" value="STUDENT"></el-option>
              <el-option label="企业" value="COMPANY"></el-option>
            </el-select>
          </div>
        </div>

        <!-- 学生特有字段 -->
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">学号</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-document"></i>
            <input
              type="text"
              v-model="registerForm.studentId"
              placeholder="请输入学号"
              class="custom-input"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">专业</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-book"></i>
            <input
              type="text"
              v-model="registerForm.major"
              placeholder="请输入专业"
              class="custom-input"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">学历</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-education"></i>
            <el-select
              v-model="registerForm.education"
              placeholder="请选择学历"
              class="role-select"
            >
              <el-option label="高中" value="高中"></el-option>
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">毕业院校</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-school"></i>
            <input
              type="text"
              v-model="registerForm.school"
              placeholder="请输入毕业院校"
              class="custom-input"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">自我介绍</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-chat-dot-round"></i>
            <textarea
              v-model="registerForm.selfIntroduction"
              placeholder="请输入自我介绍"
              class="custom-input"
              rows="3"
            ></textarea>
          </div>
        </div>

        <!-- 企业特有字段 -->
        <div v-if="registerForm.role === 'COMPANY'" class="form-item">
          <label class="form-label">企业ID</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-office-building"></i>
            <input
              type="number"
              v-model="registerForm.companyId"
              placeholder="请输入企业ID"
              class="custom-input"
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">邮箱</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-message"></i>
            <input
              type="email"
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              class="custom-input"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">电话</label>
          <div class="input-wrapper">
            <i class="input-icon i-ep-phone"></i>
            <input
              type="tel"
              v-model="registerForm.phone"
              placeholder="请输入电话"
              class="custom-input"
            />
          </div>
        </div>
        <div class="form-item" v-if="error">
          <el-alert :title="error" type="error" show-icon :closable="false" />
        </div>
        <div class="form-item">
          <button
            type="button"
            @click="register"
            :disabled="isLoading"
            class="login-button"
          >
            {{ isLoading ? "注册中..." : "注册" }}
          </button>
        </div>
      </div>

      <div class="login-footer">
        <p v-if="!isRegister">
          还没有账号？<a href="#" @click.prevent="isRegister = true"
            >点击注册</a
          >
        </p>
        <p v-else>
          已有账号？<a href="#" @click.prevent="isRegister = false">点击登录</a>
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const isRegister = ref(false);
const form = ref({
  username: "",
  password: "",
  remember: false,
});
const registerForm = ref({
  username: "",
  password: "",
  name: "",
  role: "STUDENT",
  email: "",
  phone: "",
  // 学生特有字段
  studentId: "",
  major: "",
  education: "",
  school: "",
  selfIntroduction: "",
  // 企业特有字段
  companyId: null,
});
const error = ref("");
const isLoading = ref(false);
const showPassword = ref(false);
const showRegisterPassword = ref(false);
const usernameCheckResult = ref({ exists: false, message: "" });
const isCheckingUsername = ref(false);

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
    // 使用自定义的登录接口
    const loginResponse = await fetch("/employment/api/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: form.value.username,
        password: form.value.password,
      }),
    });

    // 检查是否登录成功
    if (loginResponse.ok) {
      try {
        const loginResult = await loginResponse.json();
        if (loginResult.success) {
          const user = loginResult.user;
          if (user && user.username) {
            // 根据用户角色设置权限
            const isAdmin =
              user.role === "ADMIN" ||
              user.role === "admin" ||
              user.role === "Admin";
            const isStudent =
              user.role === "STUDENT" ||
              user.role === "student" ||
              user.role === "Student";
            const isCompany =
              user.role === "COMPANY" ||
              user.role === "company" ||
              user.role === "Company";

            // 存储用户信息
            localStorage.setItem("username", user.username);
            localStorage.setItem("userId", user.id.toString());
            localStorage.setItem("role", user.role);
            localStorage.setItem("isAdmin", isAdmin.toString());
            localStorage.setItem("isStudent", isStudent.toString());
            localStorage.setItem("isCompany", isCompany.toString());

            // 如果是公司用户，存储公司ID
            if (isCompany && user.companyId) {
              localStorage.setItem("companyId", user.companyId.toString());
            }

            console.log("登录成功，localStorage存储:", {
              username: localStorage.getItem("username"),
              userId: localStorage.getItem("userId"),
              companyId: localStorage.getItem("companyId"),
              isAdmin: localStorage.getItem("isAdmin"),
              isStudent: localStorage.getItem("isStudent"),
              isCompany: localStorage.getItem("isCompany"),
            });

            // 登录成功，跳转到仪表盘
            router.push("/dashboard");
          } else {
            // 用户信息为空，登录失败
            error.value = "登录失败，无法获取用户信息";
          }
        } else {
          // 登录失败
          error.value = loginResult.message || "登录失败";
        }
      } catch (jsonError) {
        console.error("解析登录结果失败:", jsonError);
        error.value = "登录失败，无法解析响应";
      }
    } else {
      // 登录请求失败
      error.value = "登录失败，请稍后重试";
    }
  } catch (err) {
    console.error("登录请求失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 检查用户名是否存在
const checkUsername = async () => {
  const username = registerForm.value.username;
  if (!username || username.length < 2) {
    usernameCheckResult.value = { exists: false, message: "" };
    return;
  }

  isCheckingUsername.value = true;
  try {
    const response = await fetch(
      `/employment/api/check-username?username=${encodeURIComponent(username)}`,
      {
        credentials: "include",
      },
    );
    if (response.ok) {
      const result = await response.json();
      if (result.success) {
        usernameCheckResult.value = {
          exists: result.exists,
          message: result.message,
        };
      }
    }
  } catch (err) {
    console.error("检查用户名失败:", err);
  } finally {
    isCheckingUsername.value = false;
  }
};

const register = async () => {
  error.value = "";
  isLoading.value = true;

  try {
    // 验证表单
    if (
      !registerForm.value.username ||
      !registerForm.value.password ||
      !registerForm.value.name ||
      !registerForm.value.email ||
      !registerForm.value.phone
    ) {
      error.value = "请填写所有必填字段";
      isLoading.value = false;
      return;
    }

    // 检查用户名是否已存在
    if (usernameCheckResult.value.exists) {
      error.value = "用户名已存在";
      isLoading.value = false;
      return;
    }

    // 调用注册接口
    const registerResponse = await fetch("/employment/api/register", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(registerForm.value),
    });

    // 检查是否注册成功
    if (registerResponse.ok) {
      try {
        const registerResult = await registerResponse.json();
        if (registerResult.success) {
          const user = registerResult.user;
          if (user && user.username) {
            // 根据用户角色设置权限
            const isAdmin =
              user.role === "ADMIN" ||
              user.role === "admin" ||
              user.role === "Admin";
            const isStudent =
              user.role === "STUDENT" ||
              user.role === "student" ||
              user.role === "Student";
            const isCompany =
              user.role === "COMPANY" ||
              user.role === "company" ||
              user.role === "Company";

            // 存储用户信息
            localStorage.setItem("username", user.username);
            localStorage.setItem("userId", user.id.toString());
            localStorage.setItem("role", user.role);
            localStorage.setItem("isAdmin", isAdmin.toString());
            localStorage.setItem("isStudent", isStudent.toString());
            localStorage.setItem("isCompany", isCompany.toString());

            console.log("注册成功，localStorage存储:", {
              username: localStorage.getItem("username"),
              userId: localStorage.getItem("userId"),
              isAdmin: localStorage.getItem("isAdmin"),
              isStudent: localStorage.getItem("isStudent"),
              isCompany: localStorage.getItem("isCompany"),
            });

            // 注册成功，跳转到仪表盘
            router.push("/dashboard");
          } else {
            // 用户信息为空，注册失败
            error.value = "注册失败，无法获取用户信息";
          }
        } else {
          // 注册失败
          error.value = registerResult.message || "注册失败";
        }
      } catch (jsonError) {
        console.error("解析注册结果失败:", jsonError);
        error.value = "注册失败，无法解析响应";
      }
    } else {
      // 注册请求失败
      error.value = "注册失败，请稍后重试";
    }
  } catch (err) {
    console.error("注册请求失败:", err);
    error.value = "网络错误，请稍后重试";
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
  border: 1px solid #333;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
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
  border: none;
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
  outline: none;
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

.role-select {
  width: 100%;
  padding: 12px 40px 12px 36px;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  background: transparent;
  transition: all 0.3s ease;
}

.role-select:focus {
  outline: none;
}

.register-form {
  padding: 0 20px 20px;
}

.username-check-result {
  font-size: 12px;
  margin-top: 5px;
  padding: 5px 10px;
  border-radius: 4px;
}

.username-check-result.error {
  color: #f56c6c;
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
}

.username-check-result.success {
  color: #67c23a;
  background-color: #f0f9eb;
  border: 1px solid #c2e7b0;
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
