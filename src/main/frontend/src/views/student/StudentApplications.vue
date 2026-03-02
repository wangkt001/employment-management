<template>
  <div class="student-applications-container">
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
        title="我的申请"
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
            <el-select
              v-model="statusFilter"
              placeholder="状态筛选"
              size="default"
              class="status-select"
              @change="handleStatusFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="已查看" value="REVIEWED" />
              <el-option label="面试" value="INTERVIEW" />
              <el-option label="录用" value="OFFER" />
              <el-option label="拒绝" value="REJECTED" />
            </el-select>
          </div>
        </div>

        <!-- 申请列表和分页 -->
        <div class="applications-container">
          <!-- 状态显示 -->
          <StatusStates
            :is-loading="isLoading"
            :error="error"
            :is-empty="
              !isLoading && !error && filteredApplications.length === 0
            "
            type="applications"
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
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="jobTitle" label="岗位名称" min-width="200" />
            <el-table-column
              prop="companyName"
              label="公司名称"
              min-width="150"
            />
            <el-table-column label="申请状态" width="120">
              <template #default="scope">
                <el-tag
                  :type="getStatusType(scope.row.status)"
                  size="small"
                  class="status-tag"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="appliedDate" label="申请日期" width="150" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                    size="small"
                    type="primary"
                    plain
                    @click="viewApplication(scope.row)"
                    class="action-btn view-btn"
                  >
                    查看
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    plain
                    @click="cancelApplication(scope.row.id)"
                    class="action-btn cancel-btn"
                  >
                    取消
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
const username = ref(localStorage.getItem("username") || "学生");
const role = ref(localStorage.getItem("role") || "STUDENT");

// 页面状态
const currentPage = ref(1);
const pageSize = ref(10);
const statusFilter = ref("all");

// 申请数据
const applications = ref([]);
const isLoading = ref(false);
const error = ref("");

// 加载申请数据
const fetchApplications = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    const response = await fetch("/employment/api/applications/student", {
      credentials: "include",
    });
    if (response.ok) {
      const data = await response.json();
      // 转换后端数据格式以匹配前端需求
      applications.value = data.map((application) => ({
        id: application.id,
        jobTitle: application.job?.title || "未知岗位",
        companyName: application.job?.company?.companyName || "未知公司",
        status: application.status,
        appliedDate: application.applyDate
          ? new Date(application.applyDate).toISOString().split("T")[0]
          : new Date().toISOString().split("T")[0],
      }));
    } else {
      error.value = "获取申请数据失败";
    }
  } catch (err) {
    console.error("获取申请数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 刷新申请列表
const refreshApplications = () => {
  currentPage.value = 1;
  fetchApplications();
};

// 筛选后的申请
const filteredApplications = computed(() => {
  let result = applications.value.filter((app) => {
    // 状态筛选
    if (statusFilter.value !== "all") {
      if (app.status !== statusFilter.value) return false;
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
    return true;
  }).length;
});

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case "PENDING":
      return "info";
    case "REVIEWED":
      return "primary";
    case "INTERVIEW":
      return "warning";
    case "OFFER":
      return "success";
    case "REJECTED":
      return "danger";
    default:
      return "info";
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case "PENDING":
      return "待处理";
    case "REVIEWED":
      return "已查看";
    case "INTERVIEW":
      return "面试";
    case "OFFER":
      return "录用";
    case "REJECTED":
      return "拒绝";
    default:
      return status;
  }
};

// 查看申请详情
const viewApplication = (application) => {
  console.log("查看申请:", application);
  // 这里可以跳转到申请详情页面
};

// 取消申请
const cancelApplication = async (applicationId) => {
  if (confirm("确定要取消这个申请吗？")) {
    try {
      // 模拟API调用
      await new Promise((resolve) => setTimeout(resolve, 500));

      // 从本地数据中移除
      applications.value = applications.value.filter(
        (app) => app.id !== applicationId,
      );
      alert("申请已取消！");
    } catch (error) {
      console.error("取消申请失败:", error);
      alert("取消申请失败，请稍后重试");
    }
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
  // 页面加载时获取申请数据
  await fetchApplications();
});
</script>

<style scoped>
/* 全局样式 */
.student-applications-container {
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

  .status-select {
    width: 100%;
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
</style>
