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
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <el-input
              :type="showPassword ? 'text' : 'password'"
              v-model="form.password"
              placeholder="请输入密码"
              size="large"
              :suffix-icon="showPassword ? View : Hide"
              @click="showPassword = !showPassword"
            />
          </div>
        </div>
        <div class="form-item remember-item">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
        </div>
        <div class="form-item" v-if="error">
          <el-alert :title="error" type="error" show-icon :closable="false" />
        </div>
        <div class="form-item">
          <el-button
            type="primary"
            @click="login"
            :loading="isLoading"
            size="large"
            style="width: 100%"
          >
            {{ isLoading ? "登录中..." : "登录" }}
          </el-button>
        </div>
      </div>

      <!-- 注册表单 -->
      <div v-else class="register-form">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
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
            <el-input
              :type="showRegisterPassword ? 'text' : 'password'"
              v-model="registerForm.password"
              placeholder="请输入密码"
              size="large"
              :suffix-icon="showRegisterPassword ? View : Hide"
              @click="showRegisterPassword = !showRegisterPassword"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">姓名</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.name"
              placeholder="请输入姓名"
              size="large"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">角色</label>
          <div class="input-wrapper">
            <el-select
              v-model="registerForm.role"
              placeholder="请选择角色"
              size="large"
              style="width: 100%"
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
            <el-input
              v-model="registerForm.studentId"
              placeholder="请输入学号"
              size="large"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">专业</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.major"
              placeholder="请输入专业"
              size="large"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">学历</label>
          <div class="input-wrapper">
            <el-select
              v-model="registerForm.education"
              placeholder="请选择学历"
              size="large"
              style="width: 100%"
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
            <el-input
              v-model="registerForm.school"
              placeholder="请输入毕业院校"
              size="large"
            />
          </div>
        </div>
        <div v-if="registerForm.role === 'STUDENT'" class="form-item">
          <label class="form-label">自我介绍</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.selfIntroduction"
              type="textarea"
              placeholder="请输入自我介绍"
              :rows="3"
            />
          </div>
        </div>

        <!-- 企业特有字段 -->
        <div v-if="registerForm.role === 'COMPANY'" class="form-item">
          <label class="form-label">企业ID</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.companyId"
              type="number"
              placeholder="请输入企业ID"
              size="large"
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">邮箱</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.email"
              type="email"
              placeholder="请输入邮箱"
              size="large"
            />
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">电话</label>
          <div class="input-wrapper">
            <el-input
              v-model="registerForm.phone"
              type="tel"
              placeholder="请输入电话"
              size="large"
            />
          </div>
        </div>
        <div class="form-item" v-if="error">
          <el-alert :title="error" type="error" show-icon :closable="false" />
        </div>
        <div class="form-item">
          <el-button
            type="primary"
            @click="register"
            :loading="isLoading"
            size="large"
            style="width: 100%"
          >
            {{ isLoading ? "注册中..." : "注册" }}
          </el-button>
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
import { View, Hide } from "@element-plus/icons-vue";

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
  studentId: "",
  major: "",
  education: "",
  school: "",
  selfIntroduction: "",
  companyId: null,
});
const error = ref("");
const isLoading = ref(false);
const showPassword = ref(false);
const showRegisterPassword = ref(false);
const usernameCheckResult = ref({ exists: false, message: "" });
const isCheckingUsername = ref(false);

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

    if (loginResponse.ok) {
      try {
        const loginResult = await loginResponse.json();
        if (loginResult.success) {
          const user = loginResult.user;
          if (user && user.username) {
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

            localStorage.setItem("username", user.username);
            localStorage.setItem("userId", user.id.toString());
            localStorage.setItem("role", user.role);
            localStorage.setItem("isAdmin", isAdmin.toString());
            localStorage.setItem("isStudent", isStudent.toString());
            localStorage.setItem("isCompany", isCompany.toString());

            if (isCompany && user.companyId) {
              localStorage.setItem("companyId", user.companyId.toString());
            }

            router.push("/dashboard");
          } else {
            error.value = "登录失败，无法获取用户信息";
          }
        } else {
          error.value = loginResult.message || "登录失败";
        }
      } catch (jsonError) {
        console.error("解析登录结果失败:", jsonError);
        error.value = "登录失败，无法解析响应";
      }
    } else {
      error.value = "登录失败，请稍后重试";
    }
  } catch (err) {
    console.error("登录请求失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

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

    if (usernameCheckResult.value.exists) {
      error.value = "用户名已存在";
      isLoading.value = false;
      return;
    }

    const registerResponse = await fetch("/employment/api/register", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(registerForm.value),
    });

    if (registerResponse.ok) {
      try {
        const registerResult = await registerResponse.json();
        if (registerResult.success) {
          const user = registerResult.user;
          if (user && user.username) {
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

            localStorage.setItem("username", user.username);
            localStorage.setItem("userId", user.id.toString());
            localStorage.setItem("role", user.role);
            localStorage.setItem("isAdmin", isAdmin.toString());
            localStorage.setItem("isStudent", isStudent.toString());
            localStorage.setItem("isCompany", isCompany.toString());

            router.push("/dashboard");
          } else {
            error.value = "注册失败，无法获取用户信息";
          }
        } else {
          error.value = registerResult.message || "注册失败";
        }
      } catch (jsonError) {
        console.error("解析注册结果失败:", jsonError);
        error.value = "注册失败，无法解析响应";
      }
    } else {
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

<style scoped>
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
  max-width: 450px;
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

.remember-item {
  text-align: left;
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

@media (max-width: 480px) {
  .login-card {
    margin: 20px;
  }

  .login-header h1 {
    font-size: 20px;
  }
}
</style>
