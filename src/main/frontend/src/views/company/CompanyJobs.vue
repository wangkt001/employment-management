<template>
  <div class="company-jobs-container">
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
        title="岗位管理"
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
              @click="openJobModal"
              class="add-job-btn"
            >
              <template #icon>
                <el-icon><i-ep-plus /></el-icon>
              </template>
              发布新岗位
            </el-button>
          </div>

          <!-- 右侧筛选 -->
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索岗位名称"
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
              v-model="activeTab"
              placeholder="状态筛选"
              size="default"
              class="status-select"
              @change="handleStatusFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="已上架" value="active" />
              <el-option label="已下架" value="inactive" />
            </el-select>
            <el-select
              v-model="locationFilter"
              placeholder="工作地点"
              size="default"
              class="location-select"
              @change="handleLocationFilter"
            >
              <el-option label="全部" value="all" />
              <el-option label="北京" value="北京" />
              <el-option label="上海" value="上海" />
              <el-option label="广州" value="广州" />
              <el-option label="深圳" value="深圳" />
            </el-select>
          </div>
        </div>

        <!-- 岗位列表和分页 -->
        <div class="jobs-container">
          <!-- 状态显示 -->
          <StatusStates
            :is-loading="isLoading"
            :error="error"
            :is-empty="!isLoading && !error && filteredJobs.length === 0"
            @retry="fetchJobs"
            @add-job="openJobModal"
          />

          <!-- 岗位表格 -->
          <el-table
            v-if="!isLoading && !error && filteredJobs.length > 0"
            :data="filteredJobs"
            style="width: 100%"
            class="jobs-table"
            stripe
            border
            :header-cell-style="{
              backgroundColor: '#f8f9fa',
              fontWeight: 'bold',
            }"
            :default-sort="{ prop: 'postedDate', order: 'descending' }"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="title" label="岗位名称" min-width="200">
              <template #default="scope">
                <div class="job-title-cell">
                  <span class="job-title">{{ scope.row.title }}</span>
                  <el-tag
                    v-if="scope.row.status === 'active'"
                    type="success"
                    size="small"
                    class="status-tag"
                  >
                    已上架
                  </el-tag>
                  <el-tag v-else type="info" size="small" class="status-tag">
                    已下架
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="salary" label="薪资范围" width="120" />
            <el-table-column prop="location" label="工作地点" width="150" />
            <el-table-column prop="experience" label="工作经验" width="120" />
            <el-table-column prop="education" label="学历要求" width="120" />
            <el-table-column prop="views" label="浏览量" width="80" />
            <el-table-column prop="applications" label="申请数" width="80" />
            <el-table-column prop="postedDate" label="发布日期" width="120" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                    size="small"
                    type="primary"
                    plain
                    @click="editJob(scope.row)"
                    class="action-btn edit-btn"
                  >
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    plain
                    @click="deleteJob(scope.row.id)"
                    class="action-btn delete-btn"
                  >
                    删除
                  </el-button>
                  <el-button
                    size="small"
                    :type="
                      scope.row.status === 'active' ? 'warning' : 'success'
                    "
                    plain
                    @click="
                      toggleJobStatus(
                        scope.row.id,
                        scope.row.status === 'active' ? 'inactive' : 'active',
                      )
                    "
                    class="action-btn"
                  >
                    {{ scope.row.status === "active" ? "下架" : "上架" }}
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <div class="pagination-info">共 {{ totalJobs }} 个岗位</div>
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalJobs"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="pagination"
            />
          </div>
        </div>
      </div>

      <!-- 发布/编辑岗位模态框 -->
      <JobModal
        :visible="showAddJobModal || showEditJobModal"
        :is-editing="showEditJobModal"
        :job-data="editingJob"
        @close="closeJobModal"
        @submit="submitJob"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";
import JobModal from "@/components/company/JobModal.vue";
import StatusStates from "@/components/company/StatusStates.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

const userId = ref(parseInt(localStorage.getItem("userId")) || null);
const username = ref(localStorage.getItem("username") || "企业");
const role = ref(localStorage.getItem("role") || "COMPANY");

const hasCompany = ref(false);
const activeTab = ref("all");
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const locationFilter = ref("all");

// 模态框状态
const showAddJobModal = ref(false);
const showEditJobModal = ref(false);
const editingJobId = ref(null);
const editingJob = ref({});

// 岗位数据
const jobs = ref([]);
const isLoading = ref(false);
const error = ref("");

// 获取岗位数据
const fetchJobs = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    // 从本地缓存获取companyId，并确保是数字类型
    const companyId = parseInt(localStorage.getItem("companyId")) || 4;

    // 构建查询参数
    let params = new URLSearchParams();
    params.append("companyId", companyId);

    // 添加搜索关键词
    if (searchKeyword.value) {
      params.append("title", searchKeyword.value);
    }

    // 添加地区筛选
    if (locationFilter.value !== "all") {
      params.append("location", locationFilter.value);
    }

    // 添加状态筛选
    if (activeTab.value === "active") {
      params.append("active", "true");
    } else if (activeTab.value === "inactive") {
      params.append("active", "false");
    }

    // 添加分页参数
    params.append("page", currentPage.value - 1); // 后端从0开始
    params.append("size", pageSize.value);

    const response = await fetch(
      `/employment/api/company/jobs/page?${params.toString()}`,
      {
        credentials: "include",
      },
    );

    if (response.ok) {
      const data = await response.json();
      console.log("分页查询结果:", data);

      // 转换后端数据格式以匹配前端需求
      jobs.value = data.content.map((job) => ({
        id: job.id,
        title: job.title,
        salary: job.salaryRange,
        location: job.workingLocation,
        experience: job.workExperience,
        education: job.educationLevel,
        industry: job.industry,
        description: job.responsibilities,
        requirements: job.requirements,
        tags: job.tags ? job.tags.split(",") : [], // 从后端获取标签
        views: 0, // 后端暂未支持浏览量
        applications: 0, // 后端暂未支持申请数
        postedDate: job.publishDate
          ? new Date(job.publishDate).toISOString().split("T")[0]
          : new Date().toISOString().split("T")[0],
        status: job.active ? "active" : "inactive",
      }));

      // 更新总岗位数
      totalJobs.value = data.totalElements;
    } else {
      error.value = "获取岗位数据失败";
    }
  } catch (err) {
    console.error("获取岗位数据失败:", err);
    error.value = "网络错误，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 筛选后的岗位（直接使用后端返回的数据，因为后端已经进行了筛选和分页）
const filteredJobs = computed(() => {
  return jobs.value;
});

// 总岗位数（从后端返回的数据中获取）
const totalJobs = ref(0);

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 退出登录
const logout = () => {
  localStorage.clear();
  router.push("/login");
};

// 编辑岗位
const editJob = (job) => {
  editingJobId.value = job.id;
  editingJob.value = job;
  showEditJobModal.value = true;
};

// 删除岗位
const deleteJob = async (jobId) => {
  if (confirm("确定要删除这个岗位吗？")) {
    try {
      // 从本地缓存获取userId和companyId
      const userId = parseInt(localStorage.getItem("userId")) || 2;
      const companyId = parseInt(localStorage.getItem("companyId")) || 4;

      const response = await fetch(
        `/employment/api/company/jobs/${jobId}?userId=${userId}&companyId=${companyId}`,
        {
          method: "DELETE",
          credentials: "include",
        },
      );
      if (response.ok) {
        // 从本地数据中移除
        jobs.value = jobs.value.filter((job) => job.id !== jobId);
        alert("岗位删除成功！");
      } else {
        alert("删除岗位失败，请稍后重试");
      }
    } catch (err) {
      console.error("删除岗位失败:", err);
      alert("网络错误，请稍后重试");
    }
  }
};

// 切换岗位状态
const toggleJobStatus = async (jobId, newStatus) => {
  try {
    // 从本地缓存获取userId和companyId
    const userId = parseInt(localStorage.getItem("userId")) || 2;
    const companyId = parseInt(localStorage.getItem("companyId")) || 4;

    const response = await fetch(
      `/employment/api/company/jobs/${jobId}/status?active=${newStatus === "active"}&userId=${userId}&companyId=${companyId}`,
      {
        method: "PATCH",
        credentials: "include",
      },
    );
    if (response.ok) {
      // 更新本地数据
      const job = jobs.value.find((job) => job.id === jobId);
      if (job) {
        job.status = newStatus;
      }
    } else {
      alert("更新岗位状态失败，请稍后重试");
    }
  } catch (err) {
    console.error("更新岗位状态失败:", err);
    alert("网络错误，请稍后重试");
  }
};

// 关闭岗位模态框
const closeJobModal = () => {
  showAddJobModal.value = false;
  showEditJobModal.value = false;
  editingJobId.value = null;
  editingJob.value = {};
};

// 提交岗位
const submitJob = async (formData) => {
  try {
    const jobData = {
      title: formData.title,
      salary: formData.salary,
      location: formData.location,
      experience: formData.experience,
      education: formData.education,
      industry: formData.industry,
      description: formData.description,
      requirements: formData.requirements,
      tagsInput: formData.tagsInput,
      companyId: formData.companyId,
      userId: formData.userId,
    };

    let response;
    if (showEditJobModal.value) {
      response = await fetch(
        `/employment/api/company/jobs/${editingJobId.value}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(jobData),
          credentials: "include",
        },
      );
    } else {
      response = await fetch("/employment/api/company/jobs", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(jobData),
        credentials: "include",
      });
    }

    const data = await response.json();

    if (response.ok && data.success) {
      await fetchJobs();
      closeJobModal();
      ElMessage.success(
        showEditJobModal.value ? "岗位修改成功！" : "岗位发布成功！",
      );
    } else {
      ElMessage.error(data.message || "操作失败，请稍后重试");
    }
  } catch (err) {
    console.error("提交岗位失败:", err);
    ElMessage.error("网络错误，请稍后重试");
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1; // 重置到第一页
  fetchJobs(); // 重新获取数据
};

// 状态筛选处理
const handleStatusFilter = () => {
  currentPage.value = 1; // 重置到第一页
  fetchJobs(); // 重新获取数据
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
  fetchJobs(); // 重新获取数据
};

// 当前页码变化处理
const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchJobs(); // 重新获取数据
};

// 地区筛选处理
const handleLocationFilter = () => {
  currentPage.value = 1;
  fetchJobs();
};

const checkCompanyStatus = async () => {
  if (!userId.value) return;
  try {
    const response = await fetch(
      `/employment/api/company/info?userId=${userId.value}`,
      { credentials: "include" },
    );
    const data = await response.json();
    if (data.success && data.hasCompany) {
      hasCompany.value = true;
      localStorage.setItem("companyId", data.id);
    } else {
      hasCompany.value = false;
      localStorage.removeItem("companyId");
    }
  } catch (err) {
    console.error("检查企业状态失败:", err);
  }
};

const openJobModal = () => {
  if (!hasCompany.value) {
    ElMessage.warning("请先完善企业信息后再发布岗位");
    router.push("/company/profile");
    return;
  }
  showAddJobModal.value = true;
};

onMounted(async () => {
  await checkCompanyStatus();
  fetchJobs();
});
</script>

<style scoped>
/* 全局样式 */
.company-jobs-container {
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

.status-select,
.location-select {
  width: 150px;
}

/* 岗位容器 */
.jobs-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
}

/* 岗位表格 */
.jobs-table {
  flex: 1;
  overflow-y: auto;
}

.job-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.job-title {
  font-weight: 500;
}

.status-tag {
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
.jobs-table::-webkit-scrollbar {
  width: 8px;
}

.jobs-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.jobs-table::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.jobs-table::-webkit-scrollbar-thumb:hover {
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

  .status-select,
  .location-select {
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

  .status-select,
  .location-select {
    width: 100%;
  }
}
</style>
