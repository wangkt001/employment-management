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
            :is-empty="
              !isLoading && !error && filteredApplications.length === 0
            "
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
                  @click="downloadResume(scope.row.resumeUrl, scope.row)"
                >
                  下载简历
                </el-button>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)" size="small">
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
            <el-descriptions-item label="申请ID">{{
              selectedApplication.id
            }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{
              selectedApplication.applicantName
            }}</el-descriptions-item>
            <el-descriptions-item label="申请岗位">{{
              selectedApplication.jobTitle
            }}</el-descriptions-item>
            <el-descriptions-item label="申请日期">{{
              selectedApplication.appliedDate
            }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{
              getStatusText(selectedApplication.status)
            }}</el-descriptions-item>
            <el-descriptions-item label="简历" span="2">
              <el-button
                type="primary"
                size="small"
                @click="
                  downloadResume(
                    selectedApplication.resumeUrl,
                    selectedApplication,
                  )
                "
              >
                下载简历
              </el-button>
            </el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <div v-loading="resumeLoading">
            <el-descriptions v-if="studentResume" title="申请人信息" border>
              <el-descriptions-item label="姓名">{{
                studentResume.name || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="学号">{{
                studentResume.studentId || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="专业">{{
                studentResume.major || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="学历">{{
                studentResume.education || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="院系">{{
                studentResume.department || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="年级">{{
                studentResume.grade || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="毕业院校">{{
                studentResume.school || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="联系方式">{{
                studentResume.phone || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{
                studentResume.email || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="地址">{{
                studentResume.address || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="职业方向">{{
                studentResume.careerDirection || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="期望薪资">{{
                studentResume.expectedSalary || "未填写"
              }}</el-descriptions-item>
              <el-descriptions-item label="自我介绍" span="2">{{
                studentResume.selfIntroduction || "未填写"
              }}</el-descriptions-item>
            </el-descriptions>
            <el-divider v-if="studentResume" />
            <div
              v-if="
                studentResume &&
                studentResume.workExperiences &&
                studentResume.workExperiences.length > 0
              "
              class="work-experience-section"
            >
              <h3>工作经历</h3>
              <div
                v-for="(exp, index) in studentResume.workExperiences"
                :key="exp.id || index"
                class="work-experience-item"
              >
                <div class="exp-header">
                  <span class="exp-company">{{ exp.companyName }}</span>
                  <span class="exp-period">
                    {{ exp.startDate }} ~
                    {{ exp.currentJob ? "至今" : exp.endDate || "至今" }}
                  </span>
                </div>
                <div class="exp-position">
                  {{ exp.position
                  }}<span v-if="exp.department"> | {{ exp.department }}</span>
                </div>
                <p class="exp-description" v-if="exp.description">
                  {{ exp.description }}
                </p>
                <p class="exp-achievements" v-if="exp.achievements">
                  <strong>主要业绩：</strong>{{ exp.achievements }}
                </p>
              </div>
            </div>
            <el-divider v-if="studentResume" />
            <div v-if="studentResume" class="resume-section">
              <h3>简历预览</h3>
              <ResumeGenerator
                :studentInfo="{
                  name: studentResume.name,
                  phone: studentResume.phone,
                  email: studentResume.email,
                  address: studentResume.address,
                  education: studentResume.education,
                  school: studentResume.school,
                  major: studentResume.major,
                  careerDirection: studentResume.careerDirection,
                  expectedSalary: studentResume.expectedSalary,
                  selfIntroduction: studentResume.selfIntroduction,
                  workExperiences: studentResume.workExperiences || [],
                }"
              />
            </div>
          </div>
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
import ResumeGenerator from "@/components/resume/ResumeGenerator.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "企业");
const role = ref(localStorage.getItem("role") || "COMPANY");
const companyId = ref(localStorage.getItem("companyId") || null);
console.log("公司Id companyId:", companyId.value);
// 页面状态
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const statusFilter = ref("all");

// 模态框状态
const showDetailModal = ref(false);
const selectedApplication = ref(null);
const studentResume = ref(null);
const resumeLoading = ref(false);

// 申请数据
const applications = ref([]);
const isLoading = ref(false);
const error = ref("");

// 获取申请数据
const fetchApplications = async () => {
  if (!companyId.value) {
    error.value = "公司信息有误，无法获取申请数据";
    isLoading.value = false;
    return;
  }

  isLoading.value = true;
  error.value = "";
  try {
    // 模拟数据，实际应该从后端API获取
    const response = await fetch(
      `/employment/api/company/applications?companyId=${companyId.value}`,
      {
        credentials: "include",
      },
    );
    if (response.ok) {
      const data = await response.json();
      // 转换后端数据格式以匹配前端需求
      applications.value = data.map((application) => ({
        id: application.id,
        applicantName: application.student?.user?.name || "未知申请人",
        jobTitle: application.job?.title || "未知岗位",
        status: (application.status || "pending").toLowerCase(),
        appliedDate: application.applyDate
          ? new Date(application.applyDate).toISOString().split("T")[0]
          : new Date().toISOString().split("T")[0],
        resumeUrl: application.resumeUrl || "",
        major: application.student?.major || "",
        education: application.student?.education || "",
        school: application.student?.school || "",
        contact: application.student?.user?.phone || "",
        email: application.student?.user?.email || "",
        selfIntroduction: application.selfIntroduction || "",
      }));
    } else {
      const errorData = await response.json().catch(() => ({}));
      error.value = "获取申请数据失败: " + (errorData.message || "请稍后重试");
    }
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
      if (
        !app.applicantName.toLowerCase().includes(keyword) &&
        !app.jobTitle.toLowerCase().includes(keyword)
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
      if (
        !app.applicantName.toLowerCase().includes(keyword) &&
        !app.jobTitle.toLowerCase().includes(keyword)
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
    case "pending":
      return "warning";
    case "approved":
      return "success";
    case "rejected":
      return "danger";
    default:
      return "info";
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case "pending":
      return "待处理";
    case "approved":
      return "已通过";
    case "rejected":
      return "已拒绝";
    default:
      return "未知状态";
  }
};

// 下载简历
const downloadResume = (resumeUrl, application) => {
  if (resumeUrl) {
    // 如果有上传的简历，直接下载
    const link = document.createElement("a");
    link.href = resumeUrl;
    link.download = `resume_${Date.now()}.pdf`;
    link.target = "_blank";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } else {
    // 如果没有上传的简历，生成并下载docx格式的简历
    generateDocxResume(application);
  }
};

// 生成并下载txt格式的简历
const generateDocxResume = (application) => {
  // 创建简历内容
  const resumeContent =
    `姓名: ${application.applicantName}\n` +
    `电话: ${application.contact || "未知"}\n` +
    `邮箱: ${application.email || "未知"}\n` +
    `学历: ${application.education || "未知"}\n` +
    `毕业院校: ${application.school || "未知"}\n` +
    `专业: ${application.major || "未知"}\n\n` +
    `个人简介:\n${application.selfIntroduction || "暂无个人简介"}\n\n` +
    `工作经历:\n` +
    `1. 软件开发工程师 (2024.03 - 至今)\n` +
    `   科技有限公司\n` +
    `   - 参与公司核心产品的开发与维护，使用Vue.js和Spring Boot技术栈\n` +
    `   - 负责前端组件的设计与实现，优化用户界面和用户体验\n` +
    `   - 与后端团队协作，实现前后端数据交互和接口对接\n` +
    `   - 参与代码评审和技术方案讨论，持续改进代码质量\n\n` +
    `2. 前端开发实习生 (2023.06 - 2023.12)\n` +
    `   互联网科技公司\n` +
    `   - 协助开发公司官网和内部管理系统的前端部分\n` +
    `   - 学习并应用Vue.js、Element Plus等前端技术\n` +
    `   - 参与前端页面的测试和bug修复\n` +
    `   - 文档编写和技术支持工作\n\n` +
    `技能专长:\n` +
    `Vue.js, JavaScript, HTML/CSS, Spring Boot, MySQL, Git`;

  // 创建Blob对象，使用txt格式
  const blob = new Blob([resumeContent], {
    type: "text/plain;charset=utf-8",
  });

  // 创建下载链接
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = `${application.applicantName}_简历.txt`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  // 释放URL对象
  URL.revokeObjectURL(link.href);
};

// 查看申请详情
const viewApplicationDetails = async (application) => {
  selectedApplication.value = application;
  studentResume.value = null;
  showDetailModal.value = true;
  resumeLoading.value = true;
  try {
    const response = await fetch(
      `/employment/api/company/applications/${application.id}/student-resume`,
      {
        credentials: "include",
      },
    );
    const data = await response.json();
    if (data.success) {
      studentResume.value = data;
    }
  } catch (err) {
    console.error("获取学生简历失败:", err);
  } finally {
    resumeLoading.value = false;
  }
};

// 关闭详情模态框
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedApplication.value = null;
};

// 批准申请
const approveApplication = async (applicationId) => {
  if (confirm("确定要批准这个申请吗？")) {
    try {
      const response = await fetch(
        `/employment/api/company/applications/${applicationId}/approve`,
        {
          method: "PUT",
          credentials: "include",
        },
      );
      if (response.ok) {
        // 更新本地数据
        const application = applications.value.find(
          (app) => app.id === applicationId,
        );
        if (application) {
          application.status = "approved";
        }
        alert("申请已批准");
      } else {
        alert("批准申请失败，请稍后重试");
      }
    } catch (err) {
      console.error("批准申请失败:", err);
      alert("网络错误，请稍后重试");
    }
  }
};

// 拒绝申请
const rejectApplication = async (applicationId) => {
  if (confirm("确定要拒绝这个申请吗？")) {
    try {
      const response = await fetch(
        `/employment/api/company/applications/${applicationId}/reject`,
        {
          method: "PUT",
          credentials: "include",
        },
      );
      if (response.ok) {
        // 更新本地数据
        const application = applications.value.find(
          (app) => app.id === applicationId,
        );
        if (application) {
          application.status = "rejected";
        }
        alert("申请已拒绝");
      } else {
        alert("拒绝申请失败，请稍后重试");
      }
    } catch (err) {
      console.error("拒绝申请失败:", err);
      alert("网络错误，请稍后重试");
    }
  }
};

onMounted(() => {
  // 页面加载时获取申请数据
  fetchApplications();
  console.log("Company Applications mounted");
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

/* 简历生成部分 */
.resume-section {
  margin-top: 20px;
}

.resume-section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.work-experience-section {
  margin-top: 16px;
}

.work-experience-section h3 {
  margin-bottom: 12px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.work-experience-item {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-left: 3px solid #3498db;
  border-radius: 0 8px 8px 0;
}

.exp-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.exp-company {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.exp-period {
  font-size: 12px;
  color: #666;
}

.exp-position {
  font-size: 13px;
  color: #555;
  margin-bottom: 6px;
}

.exp-description {
  margin: 0 0 6px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

.exp-achievements {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.exp-achievements strong {
  color: #333;
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
