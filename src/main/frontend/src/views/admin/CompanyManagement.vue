<template>
  <div class="company-management-container">
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
        title="企业管理"
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
              @click="showAddCompanyDialog = true"
              class="add-company-btn"
            >
              <template #icon>
                <el-icon><i-ep-plus /></el-icon>
              </template>
              添加企业
            </el-button>
          </div>

          <!-- 右侧筛选 -->
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索企业名称"
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
              v-model="statusFilter"
              placeholder="状态筛选"
              size="default"
              class="status-select"
              @change="handleStatusFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="已认证" value="verified" />
              <el-option label="未认证" value="unverified" />
            </el-select>
          </div>
        </div>

        <!-- 企业列表和分页 -->
        <div class="companies-container">
          <!-- 状态显示 -->
          <StatusStates
            :is-loading="isLoading"
            :error="error"
            :is-empty="
              !isLoading && !error && filteredCompanies.length === 0
            "
            type="companies"
            @retry="loadCompanies"
          />

          <!-- 企业表格 -->
          <el-table
            v-if="!isLoading && !error && filteredCompanies.length > 0"
            :data="filteredCompanies"
            style="width: 100%"
            class="companies-table"
            stripe
            border
            :header-cell-style="{
              backgroundColor: '#f8f9fa',
              fontWeight: 'bold',
            }"
            :default-sort="{ prop: 'id', order: 'ascending' }"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="企业ID" width="80" />
            <el-table-column prop="companyName" label="企业名称" min-width="200" />
            <el-table-column prop="industry" label="行业" width="120" />
            <el-table-column prop="scale" label="规模" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isVerified ? 'success' : 'warning'" size="small">
                  {{ scope.row.isVerified ? '已认证' : '未认证' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="user.name" label="联系人" width="120" />
            <el-table-column prop="user.phone" label="联系电话" width="150" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                    size="small"
                    type="primary"
                    plain
                    @click="editCompanyHandler(scope.row)"
                    class="action-btn edit-btn"
                  >
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    plain
                    @click="deleteCompany(scope.row.id)"
                    class="action-btn delete-btn"
                  >
                    删除
                  </el-button>
                  <el-button
                    size="small"
                    :type="scope.row.isVerified ? 'warning' : 'success'"
                    plain
                    @click="toggleVerification(scope.row.id, !scope.row.isVerified)"
                    class="action-btn"
                  >
                    {{ scope.row.isVerified ? '取消认证' : '认证' }}
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <div class="pagination-info">共 {{ totalCompanies }} 个企业</div>
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalCompanies"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="pagination"
            />
          </div>
        </div>
      </div>

      <!-- 添加企业对话框 -->
      <el-dialog
        v-model="showAddCompanyDialog"
        title="添加企业"
        width="600px"
        @close="closeAddCompanyDialog"
      >
        <form @submit.prevent="addCompany" class="company-form">
          <div class="form-group">
            <label for="new-company-name">企业名称</label>
            <el-input
              type="text"
              id="new-company-name"
              v-model="newCompany.companyName"
              required
              placeholder="请输入企业名称"
            />
          </div>
          <div class="form-group">
            <label for="new-industry">行业</label>
            <el-select
              id="new-industry"
              v-model="newCompany.industry"
              required
              placeholder="请选择行业"
            >
              <el-option label="互联网" value="互联网" />
              <el-option label="金融" value="金融" />
              <el-option label="教育" value="教育" />
              <el-option label="医疗" value="医疗" />
              <el-option label="制造" value="制造" />
              <el-option label="零售" value="零售" />
              <el-option label="其他" value="其他" />
            </el-select>
          </div>
          <div class="form-group">
            <label for="new-scale">规模</label>
            <el-select
              id="new-scale"
              v-model="newCompany.scale"
              required
              placeholder="请选择规模"
            >
              <el-option label="1-50人" value="1-50人" />
              <el-option label="50-100人" value="50-100人" />
              <el-option label="100-500人" value="100-500人" />
              <el-option label="500-1000人" value="500-1000人" />
              <el-option label="1000人以上" value="1000人以上" />
            </el-select>
          </div>
          <div class="form-group">
            <label for="new-business-license">营业执照</label>
            <el-input
              type="text"
              id="new-business-license"
              v-model="newCompany.businessLicense"
              required
              placeholder="请输入营业执照号"
            />
          </div>
          <div class="form-group">
            <label for="new-description">企业描述</label>
            <el-input
              type="textarea"
              id="new-description"
              v-model="newCompany.description"
              placeholder="请输入企业描述"
              rows="3"
            />
          </div>

        </form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="closeAddCompanyDialog">取消</el-button>
            <el-button type="primary" @click="addCompany">添加</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑企业对话框 -->
      <el-dialog
        v-model="showEditCompanyDialog"
        title="编辑企业"
        width="600px"
        @close="closeEditCompanyDialog"
      >
        <form @submit.prevent="updateCompany" class="company-form">
          <div class="form-group">
            <label for="edit-company-name">企业名称</label>
            <el-input
              type="text"
              id="edit-company-name"
              v-model="editCompany.companyName"
              required
              placeholder="请输入企业名称"
            />
          </div>
          <div class="form-group">
            <label for="edit-industry">行业</label>
            <el-select
              id="edit-industry"
              v-model="editCompany.industry"
              required
              placeholder="请选择行业"
            >
              <el-option label="互联网" value="互联网" />
              <el-option label="金融" value="金融" />
              <el-option label="教育" value="教育" />
              <el-option label="医疗" value="医疗" />
              <el-option label="制造" value="制造" />
              <el-option label="零售" value="零售" />
              <el-option label="其他" value="其他" />
            </el-select>
          </div>
          <div class="form-group">
            <label for="edit-scale">规模</label>
            <el-select
              id="edit-scale"
              v-model="editCompany.scale"
              required
              placeholder="请选择规模"
            >
              <el-option label="1-50人" value="1-50人" />
              <el-option label="50-100人" value="50-100人" />
              <el-option label="100-500人" value="100-500人" />
              <el-option label="500-1000人" value="500-1000人" />
              <el-option label="1000人以上" value="1000人以上" />
            </el-select>
          </div>
          <div class="form-group">
            <label for="edit-business-license">营业执照</label>
            <el-input
              type="text"
              id="edit-business-license"
              v-model="editCompany.businessLicense"
              required
              placeholder="请输入营业执照号"
            />
          </div>
          <div class="form-group">
            <label for="edit-description">企业描述</label>
            <el-input
              type="textarea"
              id="edit-description"
              v-model="editCompany.description"
              placeholder="请输入企业描述"
              rows="3"
            />
          </div>

          <div class="form-group">
            <label for="edit-verified">认证状态</label>
            <el-switch
              id="edit-verified"
              v-model="editCompany.isVerified"
              active-text="已认证"
              inactive-text="未认证"
              active-color="#2ecc71"
              inactive-color="#f39c12"
            />
          </div>
        </form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="closeEditCompanyDialog">取消</el-button>
            <el-button type="primary" @click="updateCompany">保存</el-button>
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
const statusFilter = ref("all");

// 模态框状态
const showAddCompanyDialog = ref(false);
const showEditCompanyDialog = ref(false);

// 企业数据
const companies = ref([]);
const totalCompanies = ref(0);
const isLoading = ref(false);
const error = ref("");

// 新企业表单数据
const newCompany = ref({
  companyName: "",
  industry: "",
  scale: "",
  businessLicense: "",
  description: "",
});

// 编辑企业表单数据
const editCompany = ref({
  id: null,
  companyName: "",
  industry: "",
  scale: "",
  businessLicense: "",
  description: "",
  isVerified: false,
});

// 加载企业数据
const loadCompanies = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    // 构建查询参数
    const params = new URLSearchParams();
    params.append('page', currentPage.value);
    params.append('size', pageSize.value);
    if (searchKeyword.value) {
      params.append('keyword', searchKeyword.value);
    }
    if (statusFilter.value !== 'all') {
      params.append('status', statusFilter.value);
    }
    
    const response = await fetch(`/employment/api/admin/companies?${params.toString()}`, {
      credentials: "include",
    });
    if (response.ok) {
      const data = await response.json();
      companies.value = data.companies;
      // 更新总企业数
      totalCompanies.value = data.total;
    } else {
      error.value = "获取企业数据失败";
    }
  } catch (err) {
    console.error("获取企业数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 筛选后的企业（直接使用后端返回的数据）
const filteredCompanies = computed(() => {
  return companies.value;
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

// 关闭添加企业对话框
const closeAddCompanyDialog = () => {
  showAddCompanyDialog.value = false;
  // 重置表单
  newCompany.value = {
    companyName: "",
    industry: "",
    scale: "",
    businessLicense: "",
    description: "",
  };
};

// 添加企业
const addCompany = async () => {
  try {
    // 构建添加数据，确保字段名与后端匹配
    const addData = {
      companyName: newCompany.value.companyName,
      industry: newCompany.value.industry,
      scale: newCompany.value.scale,
      businessLicense: newCompany.value.businessLicense,
      description: newCompany.value.description,
      verified: false
    };
    
    const response = await fetch("/employment/api/admin/companies", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(addData),
      credentials: "include",
    });

    if (response.ok) {
      // 重新加载企业列表
      await loadCompanies();
      // 关闭对话框
      closeAddCompanyDialog();
      // 显示成功提示
      alert("企业添加成功！");
    } else {
      const error = await response.text();
      alert("添加企业失败: " + error);
    }
  } catch (error) {
    console.error("添加企业失败:", error);
    alert("添加企业失败: " + error.message);
  }
};

// 编辑企业
const editCompanyHandler = (company) => {
  console.log("编辑企业:", company);
  // 填充编辑表单数据
  editCompany.value = {
    id: company.id,
    companyName: company.companyName,
    industry: company.industry,
    scale: company.scale,
    businessLicense: company.businessLicense,
    description: company.description,
    isVerified: company.isVerified,
  };
  // 打开编辑对话框
  showEditCompanyDialog.value = true;
};

// 关闭编辑企业对话框
const closeEditCompanyDialog = () => {
  showEditCompanyDialog.value = false;
  // 重置表单
  editCompany.value = {
    id: null,
    companyName: "",
    industry: "",
    scale: "",
    businessLicense: "",
    description: "",
    isVerified: false,
  };
};

// 更新企业
const updateCompany = async () => {
  try {
    // 构建更新数据，确保字段名与后端匹配
    const updateData = {
      companyName: editCompany.value.companyName,
      industry: editCompany.value.industry,
      scale: editCompany.value.scale,
      businessLicense: editCompany.value.businessLicense,
      description: editCompany.value.description,
      verified: editCompany.value.isVerified
    };
    
    const response = await fetch(`/employment/api/admin/companies/${editCompany.value.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updateData),
      credentials: "include",
    });

    if (response.ok) {
      // 重新加载企业列表
      await loadCompanies();
      // 关闭对话框
      closeEditCompanyDialog();
      // 显示成功提示
      alert("企业编辑成功！");
    } else {
      const error = await response.text();
      alert("编辑企业失败: " + error);
    }
  } catch (error) {
    console.error("编辑企业失败:", error);
    alert("编辑企业失败: " + error.message);
  }
};

// 删除企业
const deleteCompany = async (companyId) => {
  if (confirm("确定要删除这个企业吗？")) {
    try {
      const response = await fetch(`/employment/api/admin/companies/${companyId}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (response.ok) {
        // 重新加载企业列表
        await loadCompanies();
        alert("企业删除成功！");
      } else {
        const error = await response.text();
        alert("删除企业失败: " + error);
      }
    } catch (error) {
      console.error("删除企业失败:", error);
      alert("删除企业失败: " + error.message);
    }
  }
};

// 切换认证状态
const toggleVerification = async (companyId, newStatus) => {
  try {
    const response = await fetch(
      `/employment/api/admin/companies/${companyId}/verify?verified=${newStatus}`,
      {
        method: "PATCH",
        credentials: "include",
      },
    );

    if (response.ok) {
      // 重新加载企业列表
      await loadCompanies();
      alert(`企业已${newStatus ? '认证' : '取消认证'}！`);
    } else {
      const error = await response.text();
      alert("切换认证状态失败: " + error);
    }
  } catch (error) {
    console.error("切换认证状态失败:", error);
    alert("切换认证状态失败: " + error.message);
  }
};

// 搜索处理
const handleSearch = () => {
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
  console.log("Company Management mounted");
  // 加载企业数据
  await loadCompanies();
});
</script>

<style scoped>
/* 全局样式 */
.company-management-container {
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

.status-select {
  width: 150px;
}

/* 企业容器 */
.companies-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
}

/* 企业表格 */
.companies-table {
  flex: 1;
  overflow-y: auto;
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
.companies-table::-webkit-scrollbar {
  width: 8px;
}

.companies-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.companies-table::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.companies-table::-webkit-scrollbar-thumb:hover {
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

  .status-select {
    width: 100%;
  }
}

/* 表单样式 */
.company-form {
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