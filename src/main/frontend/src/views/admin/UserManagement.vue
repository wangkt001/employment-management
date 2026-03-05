<template>
  <div class="user-management-container">
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
        title="用户管理"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 操作和筛选区 -->
        <div class="action-filter-section">
          <!-- 左侧操作按钮 -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="default"
              @click="showAddUserDialog = true"
              class="add-user-btn"
            >
              <template #icon>
                <el-icon><i-ep-plus /></el-icon>
              </template>
              添加用户
            </el-button>
          </div>

          <!-- 右侧筛选 -->
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名、姓名或邮箱"
              clearable
              size="default"
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><i-ep-search /></el-icon>
              </template>
            </el-input>
            <el-select
              v-model="roleFilter"
              placeholder="角色筛选"
              size="default"
              class="role-select"
              @change="handleRoleFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="管理员" value="ADMIN" />
              <el-option label="学生" value="STUDENT" />
              <el-option label="企业" value="COMPANY" />
            </el-select>
            <el-select
              v-model="statusFilter"
              placeholder="状态筛选"
              size="default"
              class="status-select"
              @change="handleStatusFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="活跃" value="active" />
              <el-option label="禁用" value="inactive" />
            </el-select>
          </div>
        </div>

        <!-- 用户列表和分页 -->
        <div class="users-container">
          <!-- 状态显示 -->
          <StatusStates
            :is-loading="isLoading"
            :error="error"
            :is-empty="!isLoading && !error && filteredUsers.length === 0"
            @retry="loadUsers"
            @add-job="showAddUserDialog = true"
          />

          <!-- 用户表格 -->
          <el-table
            v-if="!isLoading && !error && filteredUsers.length > 0"
            :data="filteredUsers"
            style="width: 100%"
            class="users-table"
            stripe
            border
            :header-cell-style="{
              backgroundColor: '#f8f9fa',
              fontWeight: 'bold',
            }"
            :default-sort="{ prop: 'id', order: 'ascending' }"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" min-width="150">
              <template #default="scope">
                <div class="user-info-cell">
                  <span class="username-text">{{ scope.row.username }}</span>
                  <el-tag
                    :type="getRoleType(scope.row.role)"
                    size="small"
                    class="role-tag"
                  >
                    {{ getRoleText(scope.row.role) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名" min-width="120" />
            <el-table-column prop="email" label="邮箱" min-width="200" />
            <el-table-column prop="phone" label="电话" width="150" />
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <el-switch
                  v-model="scope.row.active"
                  @change="toggleUserStatus(scope.row.id, $event)"
                  :active-text="scope.row.active ? '活跃' : ''"
                  :inactive-text="!scope.row.active ? '禁用' : ''"
                  active-color="#2ecc71"
                  inactive-color="#e74c3c"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                    size="small"
                    type="primary"
                    plain
                    @click="editUserHandler(scope.row)"
                    class="action-btn edit-btn"
                  >
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    plain
                    @click="deleteUser(scope.row.id)"
                    class="action-btn delete-btn"
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <div class="pagination-info">共 {{ totalUsers }} 个用户</div>
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalUsers"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="pagination"
            />
          </div>
        </div>
      </div>

      <!-- 添加用户对话框 -->
      <el-dialog
        v-model="showAddUserDialog"
        title="添加用户"
        width="500px"
        @close="closeAddUserDialog"
      >
        <form @submit.prevent="addUser" class="user-form">
          <div class="form-group">
            <label for="new-username">用户名</label>
            <el-input
              type="text"
              id="new-username"
              v-model="newUser.username"
              required
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label for="new-name">姓名</label>
            <el-input
              type="text"
              id="new-name"
              v-model="newUser.name"
              required
              placeholder="请输入姓名"
            />
          </div>
          <div class="form-group">
            <label for="new-role">角色</label>
            <el-select
              id="new-role"
              v-model="newUser.role"
              required
              placeholder="请选择角色"
            >
              <el-option label="管理员" value="ADMIN"></el-option>
              <el-option label="学生" value="STUDENT"></el-option>
              <el-option label="企业" value="COMPANY"></el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="newUser.role === 'COMPANY'">
            <label for="new-company">关联企业</label>
            <el-select
              id="new-company"
              v-model="newUser.companyId"
              required
              placeholder="请选择企业"
              :loading="isLoadingCompanies"
            >
              <el-option
                v-for="company in companies"
                :key="company.id"
                :value="company.id"
                :label="company.companyName"
              >
                {{ company.companyName }}
              </el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="newUser.role === 'STUDENT'">
            <label for="new-student-id">学号</label>
            <el-input
              type="text"
              id="new-student-id"
              v-model="newUser.studentId"
              required
              placeholder="请输入学号"
            />
          </div>
          <div class="form-group" v-if="newUser.role === 'STUDENT'">
            <label for="new-major">专业</label>
            <el-input
              type="text"
              id="new-major"
              v-model="newUser.major"
              required
              placeholder="请输入专业"
            />
          </div>
          <div class="form-group" v-if="newUser.role === 'STUDENT'">
            <label for="new-education">学历</label>
            <el-select
              id="new-education"
              v-model="newUser.education"
              placeholder="请选择学历"
            >
              <el-option label="高中" value="高中"></el-option>
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="newUser.role === 'STUDENT'">
            <label for="new-school">毕业院校</label>
            <el-input
              type="text"
              id="new-school"
              v-model="newUser.school"
              placeholder="请输入毕业院校"
            />
          </div>
          <div class="form-group" v-if="newUser.role === 'STUDENT'">
            <label for="new-self-introduction">自我介绍</label>
            <el-input
              type="textarea"
              id="new-self-introduction"
              v-model="newUser.selfIntroduction"
              placeholder="请输入自我介绍"
              rows="3"
            />
          </div>
          <div class="form-group">
            <label for="new-email">邮箱</label>
            <el-input
              type="email"
              id="new-email"
              v-model="newUser.email"
              required
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label for="new-phone">电话</label>
            <el-input
              type="tel"
              id="new-phone"
              v-model="newUser.phone"
              required
              placeholder="请输入电话"
            />
          </div>
          <div class="form-group">
            <label for="new-password">密码</label>
            <el-input
              type="password"
              id="new-password"
              v-model="newUser.password"
              required
              placeholder="请输入密码"
              show-password
            />
          </div>
        </form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="closeAddUserDialog">取消</el-button>
            <el-button type="primary" @click="addUser">添加</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑用户对话框 -->
      <el-dialog
        v-model="showEditUserDialog"
        title="编辑用户"
        width="500px"
        @close="closeEditUserDialog"
      >
        <form @submit.prevent="updateUser" class="user-form">
          <div class="form-group">
            <label for="edit-username">用户名</label>
            <el-input
              type="text"
              id="edit-username"
              v-model="editUser.username"
              required
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label for="edit-name">姓名</label>
            <el-input
              type="text"
              id="edit-name"
              v-model="editUser.name"
              required
              placeholder="请输入姓名"
            />
          </div>
          <div class="form-group">
            <label for="edit-role">角色</label>
            <el-select
              id="edit-role"
              v-model="editUser.role"
              required
              placeholder="请选择角色"
            >
              <el-option label="管理员" value="ADMIN"></el-option>
              <el-option label="学生" value="STUDENT"></el-option>
              <el-option label="企业" value="COMPANY"></el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="editUser.role === 'COMPANY'">
            <label for="edit-company">关联企业</label>
            <el-select
              id="edit-company"
              v-model="editUser.companyId"
              required
              placeholder="请选择企业"
              :loading="isLoadingCompanies"
            >
              <el-option
                v-for="company in companies"
                :key="company.id"
                :value="company.id"
                :label="company.companyName"
              >
                {{ company.companyName }}
              </el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="editUser.role === 'STUDENT'">
            <label for="edit-student-id">学号</label>
            <el-input
              type="text"
              id="edit-student-id"
              v-model="editUser.studentId"
              required
              placeholder="请输入学号"
            />
          </div>
          <div class="form-group" v-if="editUser.role === 'STUDENT'">
            <label for="edit-major">专业</label>
            <el-input
              type="text"
              id="edit-major"
              v-model="editUser.major"
              required
              placeholder="请输入专业"
            />
          </div>
          <div class="form-group" v-if="editUser.role === 'STUDENT'">
            <label for="edit-education">学历</label>
            <el-select
              id="edit-education"
              v-model="editUser.education"
              placeholder="请选择学历"
            >
              <el-option label="高中" value="高中"></el-option>
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </div>
          <div class="form-group" v-if="editUser.role === 'STUDENT'">
            <label for="edit-school">毕业院校</label>
            <el-input
              type="text"
              id="edit-school"
              v-model="editUser.school"
              placeholder="请输入毕业院校"
            />
          </div>
          <div class="form-group" v-if="editUser.role === 'STUDENT'">
            <label for="edit-self-introduction">自我介绍</label>
            <el-input
              type="textarea"
              id="edit-self-introduction"
              v-model="editUser.selfIntroduction"
              placeholder="请输入自我介绍"
              rows="3"
            />
          </div>
          <div class="form-group">
            <label for="edit-email">邮箱</label>
            <el-input
              type="email"
              id="edit-email"
              v-model="editUser.email"
              required
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label for="edit-phone">电话</label>
            <el-input
              type="tel"
              id="edit-phone"
              v-model="editUser.phone"
              required
              placeholder="请输入电话"
            />
          </div>
          <div class="form-group">
            <label for="edit-password">密码（留空不修改）</label>
            <el-input
              type="password"
              id="edit-password"
              v-model="editUser.password"
              placeholder="请输入密码"
              show-password
            />
          </div>
        </form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="closeEditUserDialog">取消</el-button>
            <el-button type="primary" @click="updateUser">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";
import StatusStates from "@/components/company/StatusStates.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "管理员");
const role = ref(localStorage.getItem("role") || "ADMIN");

// 页面状态
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const roleFilter = ref("all");
const statusFilter = ref("all");

// 模态框状态
const showAddUserDialog = ref(false);
const showEditUserDialog = ref(false);

// 新用户表单数据
const newUser = ref({
  username: "",
  name: "",
  role: "STUDENT",
  email: "",
  phone: "",
  password: "",
  companyId: null,
  studentId: "",
  major: "",
  education: "",
  school: "",
  selfIntroduction: "",
});

// 编辑用户表单数据
const editUser = ref({
  id: null,
  username: "",
  name: "",
  role: "STUDENT",
  email: "",
  phone: "",
  password: "",
  companyId: null,
  studentId: "",
  major: "",
  education: "",
  school: "",
  selfIntroduction: "",
});

// 用户数据
const users = ref([]);
const isLoading = ref(false);
const error = ref("");

// 企业数据
const companies = ref([]);
const isLoadingCompanies = ref(false);

// 加载用户数据
const loadUsers = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    const response = await fetch("/employment/api/admin/users", {
      credentials: "include",
    });
    if (response.ok) {
      users.value = await response.json();
    } else {
      error.value = "获取用户数据失败";
    }
  } catch (err) {
    console.error("获取用户数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 加载企业数据
const loadCompanies = async () => {
  isLoadingCompanies.value = true;
  try {
    const response = await fetch(
      "/employment/api/admin/companies?page=1&size=100",
      {
        credentials: "include",
      },
    );
    if (response.ok) {
      const data = await response.json();
      companies.value = data.companies;
    } else {
      console.error("获取企业数据失败");
    }
  } catch (err) {
    console.error("获取企业数据失败:", err);
  } finally {
    isLoadingCompanies.value = false;
  }
};

// 筛选后的用户
const filteredUsers = computed(() => {
  let result = users.value.filter((user) => {
    // 角色筛选
    if (roleFilter.value !== "all") {
      if (user.role !== roleFilter.value) return false;
    }

    // 状态筛选
    if (statusFilter.value === "active") {
      if (!user.active) return false;
    } else if (statusFilter.value === "inactive") {
      if (user.active) return false;
    }

    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      if (
        !user.username.toLowerCase().includes(keyword) &&
        !user.name.toLowerCase().includes(keyword) &&
        !user.email.toLowerCase().includes(keyword)
      ) {
        return false;
      }
    }

    return true;
  });

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return result.slice(startIndex, endIndex);
});

// 总用户数
const totalUsers = computed(() => {
  return users.value.filter((user) => {
    // 角色筛选
    if (roleFilter.value !== "all") {
      if (user.role !== roleFilter.value) return false;
    }

    // 状态筛选
    if (statusFilter.value === "active") {
      if (!user.active) return false;
    } else if (statusFilter.value === "inactive") {
      if (user.active) return false;
    }

    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      if (
        !user.username.toLowerCase().includes(keyword) &&
        !user.name.toLowerCase().includes(keyword) &&
        !user.email.toLowerCase().includes(keyword)
      ) {
        return false;
      }
    }

    return true;
  }).length;
});

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 退出登录
const logout = () => {
  localStorage.clear();
  router.push("/login");
};

// 获取角色类型
const getRoleType = (role) => {
  switch (role) {
    case "ADMIN":
      return "danger";
    case "STUDENT":
      return "primary";
    case "COMPANY":
      return "success";
    default:
      return "info";
  }
};

// 获取角色文本
const getRoleText = (role) => {
  switch (role) {
    case "ADMIN":
      return "管理员";
    case "STUDENT":
      return "学生";
    case "COMPANY":
      return "企业";
    default:
      return role;
  }
};

// 关闭添加用户对话框
const closeAddUserDialog = () => {
  showAddUserDialog.value = false;
  // 重置表单
  newUser.value = {
    username: "",
    name: "",
    role: "STUDENT",
    email: "",
    phone: "",
    password: "",
    companyId: null,
    studentId: "",
    major: "",
    education: "",
    school: "",
    selfIntroduction: "",
  };
};

// 添加用户
const addUser = async () => {
  try {
    const response = await fetch("/employment/api/admin/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newUser.value),
      credentials: "include",
    });

    if (response.ok) {
      // 重新加载用户列表
      await loadUsers();
      // 关闭对话框
      closeAddUserDialog();
      // 显示成功提示
      alert("用户添加成功！");
    } else {
      const error = await response.text();
      alert("添加用户失败: " + error);
    }
  } catch (error) {
    console.error("添加用户失败:", error);
    alert("添加用户失败: " + error.message);
  }
};

// 编辑用户
const editUserHandler = (user) => {
  console.log("编辑用户:", user);
  // 填充编辑表单数据
  editUser.value = {
    id: user.id,
    username: user.username,
    name: user.name,
    role: user.role, // 保持原始值，el-select会根据value显示对应的label
    email: user.email,
    phone: user.phone,
    password: "", // 密码留空，不修改
    companyId: user.companyId || null,
    studentId: user.studentId || "",
    major: user.major || "",
    education: user.education || "",
    school: user.school || "",
    selfIntroduction: user.selfIntroduction || "",
  };
  // 打开编辑对话框
  showEditUserDialog.value = true;
};

// 关闭编辑用户对话框
const closeEditUserDialog = () => {
  showEditUserDialog.value = false;
  // 重置表单
  editUser.value = {
    id: null,
    username: "",
    name: "",
    role: "STUDENT",
    email: "",
    phone: "",
    password: "",
    companyId: null,
    studentId: "",
    major: "",
    education: "",
    school: "",
    selfIntroduction: "",
  };
};

// 更新用户
const updateUser = async () => {
  try {
    // 构建更新数据，排除空密码
    const updateData = {
      username: editUser.value.username,
      name: editUser.value.name,
      role: editUser.value.role,
      email: editUser.value.email,
      phone: editUser.value.phone,
      companyId: editUser.value.companyId,
      studentId: editUser.value.studentId,
      major: editUser.value.major,
      education: editUser.value.education,
      school: editUser.value.school,
      selfIntroduction: editUser.value.selfIntroduction,
    };

    // 如果密码不为空，则包含密码
    if (editUser.value.password) {
      updateData.password = editUser.value.password;
    }

    const response = await fetch(
      `/employment/api/admin/users/${editUser.value.id}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updateData),
        credentials: "include",
      },
    );

    if (response.ok) {
      // 重新加载用户列表
      await loadUsers();
      // 关闭对话框
      closeEditUserDialog();
      // 显示成功提示
      alert("用户编辑成功！");
    } else {
      const error = await response.text();
      alert("编辑用户失败: " + error);
    }
  } catch (error) {
    console.error("编辑用户失败:", error);
    alert("编辑用户失败: " + error.message);
  }
};

// 删除用户
const deleteUser = async (userId) => {
  if (confirm("确定要删除这个用户吗？")) {
    try {
      const response = await fetch(`/employment/api/admin/users/${userId}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (response.ok) {
        // 重新加载用户列表
        await loadUsers();
        alert("用户删除成功！");
      } else {
        const error = await response.text();
        alert("删除用户失败: " + error);
      }
    } catch (error) {
      console.error("删除用户失败:", error);
      alert("删除用户失败: " + error.message);
    }
  }
};

// 切换用户状态
const toggleUserStatus = async (userId, newStatus) => {
  try {
    const response = await fetch(
      `/employment/api/admin/users/${userId}/status?active=${newStatus}`,
      {
        method: "PATCH",
        credentials: "include",
      },
    );

    if (response.ok) {
      // 重新加载用户列表
      await loadUsers();
    } else {
      const error = await response.text();
      alert("切换用户状态失败: " + error);
    }
  } catch (error) {
    console.error("切换用户状态失败:", error);
    alert("切换用户状态失败: " + error.message);
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1; // 重置到第一页
};

// 角色筛选处理
const handleRoleFilter = () => {
  currentPage.value = 1; // 重置到第一页
};

// 状态筛选处理
const handleStatusFilter = () => {
  currentPage.value = 1; // 重置到第一页
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
};

// 当前页码变化处理
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

onMounted(async () => {
  // 页面加载时的初始化逻辑
  console.log("User Management mounted");
  // 加载用户数据和企业数据
  await Promise.all([loadUsers(), loadCompanies()]);
});
</script>

<style scoped>
/* 全局样式 */
.user-management-container {
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

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 操作和筛选区 */
.action-filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 280px;
}

.role-select,
.status-select {
  width: 150px;
}

/* 用户容器 */
.users-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
}

/* 用户表格 */
.users-table {
  flex: 1;
  overflow-y: auto;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username-text {
  font-weight: 500;
}

.role-tag {
  margin-left: auto;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 12px;
  font-size: 12px;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #ffffff;
}

.pagination-info {
  font-size: 14px;
  color: #666;
}

.pagination {
  display: flex;
  align-items: center;
}

/* 滚动条样式 */
.users-table::-webkit-scrollbar {
  width: 8px;
}

.users-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.users-table::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.users-table::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-content {
    padding: 20px;
  }

  .action-filter-section {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .action-buttons {
    justify-content: flex-start;
  }

  .filter-section {
    justify-content: flex-start;
  }

  .search-input {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .main-content {
    width: 100%;
  }

  .page-content {
    padding: 16px;
  }

  .filter-section {
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }

  .role-select,
  .status-select {
    width: calc(50% - 6px);
  }

  .pagination-container {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .pagination {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .action-buttons {
    flex-wrap: wrap;
  }

  .role-select,
  .status-select {
    width: 100%;
  }
}

/* 表单样式 */
.user-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
