<template>
  <div class="company-applications-container">
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
        title="申请管理"
        :username="username"
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
              @click="refreshApplications"
              class="refresh-btn"
            >
              <template #icon>
                <el-icon><i-ep-refresh /></el-icon>
              </template>
              刷新列表
            </el-button>
          </div>

          <!-- 右侧筛选 -->
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索申请人姓名或岗位"
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
              <el-option label="待处理" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </div>
        </div>

        <!-- 申请列表和分页 -->
        <div class="applications-container">
          <!-- 状态显示 -->
          <StatusStates
            :is-loading="isLoading"
            :error="error"
            :is-empty="!isLoading && !error && filteredApplications.length === 0"
            @retry="fetchApplications"
          />

          <!-- 申请表格 -->
          <el-table
            v-if="!isLoading && !error && filteredApplications.length > 0"
            :data="filteredApplications"
            style="width: 100%"
            class="applications-table"
            stripe
            border
            :header-cell-style="{
              backgroundColor: '#f8f9fa',
              fontWeight: 'bold',
            }"
            :default-sort="{ prop: 'appliedDate', order: 'descending' }"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="申请ID" width="80" />
            <el-table-column prop="applicantName" label="申请人" width="120" />
            <el-table-column prop="jobTitle" label="申请岗位" min-width="200" />
            <el-table-column prop="resumeUrl" label="简历" width="150">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  plain
                  @click="downloadResume(scope.row.resumeUrl)"
                >
                  下载简历
                </el-button>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag
                  :type="getStatusTagType(scope.row.status)"
                  size="small"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="appliedDate" label="申请日期" width="150" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                    v-if="scope.row.status === 'pending'"
                    size="small"
                    type="success"
                    plain
                    @click="approveApplication(scope.row.id)"
                    class="action-btn"
                  >
                    通过
                  </el-button>
                  <el-button
                    v-if="scope.row.status === 'pending'"
                    size="small"
                    type="danger"
                    plain
                    @click="rejectApplication(scope.row.id)"
                    class="action-btn"
                  >
                    拒绝
                  </el-button>
                  <el-button
                    size="small"
                    type="info"
                    plain
                    @click="viewApplicationDetails(scope.row)"
                    class="action-btn"
                  >
                    查看详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <div class="pagination-info">共 {{ totalApplications }} 个申请</div>
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalApplications"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="pagination"
            />
          </div>
        </div>
      </div>

      <!-- 申请详情模态框 -->
      <el-dialog
        v-model="showDetailModal"
        title="申请详情"
        width="70%"
        max-width="800px"
        @close="closeDetailModal"
      >
        <div v-if="selectedApplication" class="application-details">
          <el-descriptions title="申请信息" border>
            <el-descriptions-item label="申请ID">{{ selectedApplication.id }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ selectedApplication.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="申请岗位">{{ selectedApplication.jobTitle }}</el-descriptions-item>
            <el-descriptions-item label="申请日期">{{ selectedApplication.appliedDate }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ getStatusText(selectedApplication.status) }}</el-descriptions-item>
            <el-descriptions-item label="简历" span="2">
              <el-button
                type="primary"
                size="small"
                @click="downloadResume(selectedApplication.resumeUrl)"
              >
                下载简历
              </el-button>
            </el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <el-descriptions title="申请人信息" border>
            <el-descriptions-item label="姓名">{{ selectedApplication.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ selectedApplication.major || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="学历">{{ selectedApplication.education || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="毕业院校">{{ selectedApplication.school || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{ selectedApplication.contact || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="自我介绍" span="2">{{ selectedApplication.selfIntroduction || '未填写' }}</el-descriptions-item>
          </el-descriptions>
        </div>
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
const username = ref(localStorage.getItem("username") || "企业");

// 页面状态
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const statusFilter = ref("all");

// 模态框状态
const showDetailModal = ref(false);
const selectedApplication = ref(null);

// 申请数据
const applications = ref([]);
const isLoading = ref(false);
const error = ref("");

// 获取申请数据
const fetchApplications = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    // 模拟数据，实际应该从后端API获取
    // const response = await fetch("/api/company/applications", {
    //   credentials: "include",
    // });
    // if (response.ok) {
    //   const data = await response.json();
    //   applications.value = data;
    // } else {
    //   error.value = "获取申请数据失败";
    // }
    
    // 模拟数据
    applications.value = [
      {
        id: 1,
        applicantName: "张三",
        jobTitle: "前端开发工程师",
        resumeUrl: "#",
        status: "pending",
        appliedDate: "2026-02-28",
        major: "计算机科学与技术",
        education: "本科",
        school: "北京大学",
        contact: "13800138000",
        selfIntroduction: "我是一名计算机专业的应届毕业生，熟悉前端开发技术栈，希望能够加入贵公司。"
      },
      {
        id: 2,
        applicantName: "李四",
        jobTitle: "后端开发工程师",
        resumeUrl: "#",
        status: "approved",
        appliedDate: "2026-02-27",
        major: "软件工程",
        education: "硕士",
        school: "清华大学",
        contact: "13900139000",
        selfIntroduction: "我有3年后端开发经验，熟悉Java和Spring Boot框架。"
      },
      {
        id: 3,
        applicantName: "王五",
        jobTitle: "产品经理",
        resumeUrl: "#",
        status: "rejected",
        appliedDate: "2026-02-26",
        major: "市场营销",
        education: "本科",
        school: "复旦大学",
        contact: "13700137000",
        selfIntroduction: "我有2年产品经理经验，熟悉产品设计和用户研究。"
      }
    ];
  } catch (err) {
    console.error("获取申请数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 筛选后的申请
const filteredApplications = computed(() => {
  let result = applications.value.filter((app) => {
    // 状态筛选
    if (statusFilter.value !== "all") {
      if (app.status !== statusFilter.value) return false;
    }

    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      if (!app.applicantName.toLowerCase().includes(keyword) && !app.jobTitle.toLowerCase().includes(keyword)) {
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

// 总申请数
const totalApplications = computed(() => {
  return applications.value.filter((app) => {
    // 状态筛选
    if (statusFilter.value !== "all") {
      if (app.status !== statusFilter.value) return false;
    }

    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      if (!app.applicantName.toLowerCase().includes(keyword) && !app.jobTitle.toLowerCase().includes(keyword)) {
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

// 刷新申请列表
const refreshApplications = () => {
  fetchApplications();
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

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'pending':
      return 'warning';
    case 'approved':
      return 'success';
    case 'rejected':
      return 'danger';
    default:
      return 'info';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending':
      return '待处理';
    case 'approved':
      return '已通过';
    case 'rejected':
      return '已拒绝';
    default:
      return '未知状态';
  }
};

// 下载简历
const downloadResume = (resumeUrl) => {
  // 实际应该跳转到简历下载链接
  console.log('下载简历:', resumeUrl);
  alert('简历下载功能待实现');
};

// 查看申请详情
const viewApplicationDetails = (application) => {
  selectedApplication.value = application;
  showDetailModal.value = true;
};

// 关闭详情模态框
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedApplication.value = null;
};

// 批准申请
const approveApplication = (applicationId) => {
  if (confirm('确定要批准这个申请吗？')) {
    // 实际应该调用后端API
    console.log('批准申请:', applicationId);
    // 更新本地数据
    const application = applications.value.find(app => app.id === applicationId);
    if (application) {
      application.status = 'approved';
    }
    alert('申请已批准');
  }
};

// 拒绝申请
const rejectApplication = (applicationId) => {
  if (confirm('确定要拒绝这个申请吗？')) {
    // 实际应该调用后端API
    console.log('拒绝申请:', applicationId);
    // 更新本地数据
    const application = applications.value.find(app => app.id === applicationId);
    if (application) {
      application.status = 'rejected';
    }
    alert('申请已拒绝');
  }
};

onMounted(() => {
  // 页面加载时获取申请数据
  fetchApplications();
  console.log('Company Applications mounted');
});
</script>

<style scoped>
/* 全局样式 */
.company-applications-container {
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

/* 申请容器 */
.applications-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
}

/* 申请表格 */
.applications-table {
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

/* 申请详情 */
.application-details {
  padding: 10px;
}

/* 滚动条样式 */
.applications-table::-webkit-scrollbar {
  width: 8px;
}

.applications-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.applications-table::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.applications-table::-webkit-scrollbar-thumb:hover {
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
</style>