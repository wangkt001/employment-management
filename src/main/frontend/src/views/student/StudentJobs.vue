<template>
  <div class="student-jobs-container">
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
        title="就业岗位"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 岗位搜索和筛选 -->
        <div class="search-and-filter">
          <div class="search-box">
            <input
              type="text"
              v-model="searchKeyword"
              placeholder="搜索岗位名称、企业或关键词"
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <button class="search-button" @click="handleSearch">
              <i class="icon search-icon"></i>
            </button>
          </div>
          <div class="filter-section">
            <div class="filter-item">
              <label>行业</label>
              <select v-model="selectedIndustry" class="filter-select">
                <option value="">全部行业</option>
                <option value="互联网">互联网</option>
                <option value="金融">金融</option>
                <option value="教育">教育</option>
                <option value="医疗">医疗</option>
                <option value="制造业">制造业</option>
              </select>
            </div>
            <div class="filter-item">
              <label>薪资范围</label>
              <select v-model="selectedSalary" class="filter-select">
                <option value="">全部薪资</option>
                <option value="0-10">10K以下</option>
                <option value="10-20">10K-20K</option>
                <option value="20-30">20K-30K</option>
                <option value="30+">30K以上</option>
              </select>
            </div>
            <div class="filter-item">
              <label>工作经验</label>
              <select v-model="selectedExperience" class="filter-select">
                <option value="">全部经验</option>
                <option value="0">应届毕业生</option>
                <option value="1-3">1-3年</option>
                <option value="3-5">3-5年</option>
                <option value="5+">5年以上</option>
              </select>
            </div>
            <button class="filter-reset" @click="resetFilters">重置筛选</button>
          </div>
        </div>

        <!-- 岗位列表 -->
        <div class="jobs-list">
          <div v-for="job in filteredJobs" :key="job.id" class="job-card">
            <div class="job-card-header">
              <h3 class="job-title">{{ job.title }}</h3>
              <span class="job-salary">{{ job.salary }}</span>
            </div>
            <div class="job-card-body">
              <div class="job-company">
                <span class="company-name">{{ job.company }}</span>
                <span class="company-industry">{{ job.industry }}</span>
              </div>
              <div class="job-meta">
                <span class="job-meta-item">
                  <i class="icon location-icon"></i>
                  {{ job.location }}
                </span>
                <span class="job-meta-item">
                  <i class="icon experience-icon"></i>
                  {{ job.experience }}
                </span>
                <span class="job-meta-item">
                  <i class="icon education-icon"></i>
                  {{ job.education }}
                </span>
              </div>
              <div class="job-description">
                {{ job.description }}
              </div>
              <div class="job-tags">
                <span v-for="tag in job.tags" :key="tag" class="job-tag">
                  {{ tag }}
                </span>
              </div>
            </div>
            <div class="job-card-footer">
              <span class="job-posted-date"
                >发布时间：{{ job.postedDate }}</span
              >
              <div class="job-actions">
                <button
                  class="action-button view-button"
                  @click="viewJobDetail(job)"
                >
                  查看详情
                </button>
                <button
                  class="action-button apply-button"
                  @click="applyForJob(job.id)"
                >
                  立即申请
                </button>
              </div>
            </div>
          </div>
          <div v-if="filteredJobs.length === 0" class="empty-state">
            <i class="icon jobs-icon"></i>
            <p>暂无符合条件的岗位</p>
            <button class="reset-button" @click="resetFilters">
              重置筛选条件
            </button>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <button
            class="pagination-button"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            <i class="icon prev-icon"></i>
          </button>
          <span class="pagination-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </span>
          <button
            class="pagination-button"
            :disabled="currentPage === totalPages"
            @click="currentPage++"
          >
            <i class="icon next-icon"></i>
          </button>
        </div>

        <!-- 岗位详情模态框 -->
        <div v-if="showJobDetail" class="modal-overlay" @click="closeJobDetail">
          <div class="modal-content" @click.stop>
            <div class="modal-header">
              <h3>{{ selectedJob?.title }}</h3>
              <button class="close-button" @click="closeJobDetail">
                <i class="icon close-icon"></i>
              </button>
            </div>
            <div class="modal-body">
              <div class="job-detail-header">
                <div class="job-detail-salary">{{ selectedJob?.salary }}</div>
                <div class="job-detail-company">{{ selectedJob?.company }}</div>
                <div class="job-detail-industry">
                  {{ selectedJob?.industry }}
                </div>
              </div>
              <div class="job-detail-meta">
                <span class="job-meta-item">
                  <i class="icon location-icon"></i>
                  {{ selectedJob?.location }}
                </span>
                <span class="job-meta-item">
                  <i class="icon experience-icon"></i>
                  {{ selectedJob?.experience }}
                </span>
                <span class="job-meta-item">
                  <i class="icon education-icon"></i>
                  {{ selectedJob?.education }}
                </span>
                <span class="job-meta-item">
                  <i class="icon posted-icon"></i>
                  {{ selectedJob?.postedDate }}
                </span>
              </div>
              <div class="job-detail-section">
                <h4>岗位描述</h4>
                <p>{{ selectedJob?.description }}</p>
              </div>
              <div class="job-detail-section">
                <h4>岗位要求</h4>
                <p>{{ selectedJob?.requirements }}</p>
              </div>
              <div class="job-detail-section">
                <h4>公司简介</h4>
                <p>{{ selectedJob?.companyProfile }}</p>
              </div>
              <div class="job-tags">
                <span
                  v-for="tag in selectedJob?.tags"
                  :key="tag"
                  class="job-tag"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
            <div class="modal-footer">
              <button
                class="action-button apply-button"
                @click="applyForJob(selectedJob?.id)"
              >
                立即申请
              </button>
              <button
                class="action-button cancel-button"
                @click="closeJobDetail"
              >
                关闭
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "学生");
const role = ref(localStorage.getItem("role") || "STUDENT");

// 搜索和筛选
const searchKeyword = ref("");
const selectedIndustry = ref("");
const selectedSalary = ref("");
const selectedExperience = ref("");

// 分页
const currentPage = ref(1);
const pageSize = ref(10);

// 岗位详情
const showJobDetail = ref(false);
const selectedJob = ref(null);

// 岗位数据
const jobs = ref([]);
const isLoading = ref(false);
const error = ref("");

// 获取岗位数据
const fetchJobs = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    // 构建查询参数
    const params = new URLSearchParams();
    if (selectedIndustry.value) {
      params.append("industry", selectedIndustry.value);
    }
    if (searchKeyword.value) {
      params.append("keyword", searchKeyword.value);
    }
    if (selectedSalary.value) {
      params.append("salary", selectedSalary.value);
    }
    if (selectedExperience.value) {
      params.append("experience", selectedExperience.value);
    }

    const response = await fetch(`/employment/api/jobs?${params.toString()}`, {
      credentials: "include",
    });
    if (response.ok) {
      const data = await response.json();
      // 调试：打印后端返回的数据结构
      console.log("后端返回的数据:", data);
      // 转换后端数据格式以匹配前端需求
      jobs.value = data.map((job) => {
        // 调试：打印每个岗位的详细信息
        console.log("岗位数据:", job);
        return {
          id: job.id,
          title: job.title,
          company: job.company?.companyName || "未知企业",
          industry: job.industry || job.industryField || "未知行业",
          salary: job.salaryRange,
          location: job.workingLocation,
          experience: job.workExperience,
          education: job.educationLevel,
          description: job.responsibilities,
          requirements: job.requirements,
          companyProfile: job.company?.description || "暂无公司简介",
          tags: job.tags ? job.tags.split(",") : [],
          postedDate: job.publishDate
            ? new Date(job.publishDate).toISOString().split("T")[0]
            : new Date().toISOString().split("T")[0],
        };
      });
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

// 筛选后的岗位
const filteredJobs = computed(() => {
  let result = jobs.value.filter((job) => {
    const matchesKeyword =
      job.title.includes(searchKeyword.value) ||
      job.company.includes(searchKeyword.value) ||
      job.description.includes(searchKeyword.value);
    const matchesIndustry =
      !selectedIndustry.value || job.industry === selectedIndustry.value;
    const matchesSalary =
      !selectedSalary.value ||
      {
        "0-10": job.salary.includes("10K以下"),
        "10-20": job.salary.includes("10K-20K"),
        "20-30": job.salary.includes("20K-30K"),
        "30+": job.salary.includes("30K以上"),
      }[selectedSalary.value];
    const matchesExperience =
      !selectedExperience.value ||
      {
        0: job.experience.includes("应届毕业生"),
        "1-3": job.experience.includes("1-3年"),
        "3-5": job.experience.includes("3-5年"),
        "5+": job.experience.includes("5年以上"),
      }[selectedExperience.value];
    return (
      matchesKeyword && matchesIndustry && matchesSalary && matchesExperience
    );
  });

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return result.slice(startIndex, endIndex);
});

// 总页数
const totalPages = computed(() => {
  const filteredCount = jobs.value.filter((job) => {
    const matchesKeyword =
      job.title.includes(searchKeyword.value) ||
      job.company.includes(searchKeyword.value) ||
      job.description.includes(searchKeyword.value);
    const matchesIndustry =
      !selectedIndustry.value || job.industry === selectedIndustry.value;
    const matchesSalary =
      !selectedSalary.value ||
      {
        "0-10": job.salary.includes("10K以下"),
        "10-20": job.salary.includes("10K-20K"),
        "20-30": job.salary.includes("20K-30K"),
        "30+": job.salary.includes("30K以上"),
      }[selectedSalary.value];
    const matchesExperience =
      !selectedExperience.value ||
      {
        0: job.experience.includes("应届毕业生"),
        "1-3": job.experience.includes("1-3年"),
        "3-5": job.experience.includes("3-5年"),
        "5+": job.experience.includes("5年以上"),
      }[selectedExperience.value];
    return (
      matchesKeyword && matchesIndustry && matchesSalary && matchesExperience
    );
  }).length;
  return Math.ceil(filteredCount / pageSize.value);
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

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = "";
  selectedIndustry.value = "";
  selectedSalary.value = "";
  selectedExperience.value = "";
  currentPage.value = 1;
};

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1;
  await fetchJobs();
};

// 监听筛选条件变化
watch(
  [searchKeyword, selectedIndustry, selectedSalary, selectedExperience],
  async () => {
    currentPage.value = 1;
    await fetchJobs();
  },
  { deep: true },
);

// 查看岗位详情
const viewJobDetail = (job) => {
  selectedJob.value = job;
  showJobDetail.value = true;
};

// 关闭岗位详情
const closeJobDetail = () => {
  showJobDetail.value = false;
  selectedJob.value = null;
};

// 申请岗位
const applyForJob = async (jobId) => {
  if (!jobId) {
    alert("岗位信息有误，无法申请");
    return;
  }

  console.log("申请岗位参数:", { jobId });

  if (confirm("确定要申请这个岗位吗？")) {
    try {
      const response = await fetch(
        `/employment/api/applications/job/${jobId}`,
        {
          method: "POST",
          credentials: "include",
        },
      );
      if (response.ok) {
        alert("申请成功！");
        closeJobDetail();
      } else {
        alert("申请失败，请稍后重试");
      }
    } catch (err) {
      console.error("申请岗位失败:", err);
      alert("网络错误，请稍后重试");
    }
  }
};

onMounted(async () => {
  // 页面加载时获取岗位数据
  await fetchJobs();
  console.log("Student Jobs mounted");
});
</script>

<style scoped>
.student-jobs-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
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
  padding: 20px;
}

/* 搜索和筛选 */
.search-and-filter {
  background: white;
  padding: 20px;
  margin: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.search-box {
  position: relative;
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  padding: 15px 60px 15px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.search-button {
  position: absolute;
  right: 4px;
  top: 50%;
  transform: translateY(-50%);
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 6px;
  width: 52px;
  height: 44px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.search-button:hover {
  background: linear-gradient(135deg, #2980b9, #1f618d);
}

.filter-section {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.filter-item {
  flex: 1;
  min-width: 200px;
}

.filter-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.filter-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: white;
  transition: border-color 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.filter-reset {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.filter-reset:hover {
  border-color: #3498db;
  color: #3498db;
}

/* 岗位列表 */
.jobs-list {
  flex: 1;
  padding: 0 20px 20px;
  overflow-y: auto;
}

.job-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.job-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.job-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.job-salary {
  font-size: 16px;
  font-weight: 700;
  color: #e74c3c;
}

.job-card-body {
  margin-bottom: 20px;
}

.job-company {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.company-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.company-industry {
  font-size: 12px;
  color: #666;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 10px;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.job-meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.job-description {
  font-size: 14px;
  line-height: 1.5;
  color: #666;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.job-tag {
  font-size: 12px;
  padding: 4px 12px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 12px;
}

.job-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.job-posted-date {
  font-size: 12px;
  color: #999;
}

.job-actions {
  display: flex;
  gap: 10px;
}

.action-button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-button {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.view-button:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.apply-button {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.apply-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  color: #999;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 16px;
}

.reset-button {
  padding: 10px 20px;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reset-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: white;
  margin: 0 20px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.pagination-button {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.pagination-button:hover:not(:disabled) {
  border-color: #3498db;
  color: #3498db;
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: 14px;
  color: #666;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s ease;
}

.close-button:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.job-detail-header {
  margin-bottom: 20px;
}

.job-detail-salary {
  font-size: 20px;
  font-weight: 700;
  color: #e74c3c;
  margin-bottom: 10px;
}

.job-detail-company {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.job-detail-industry {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.job-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.job-detail-section {
  margin-bottom: 20px;
}

.job-detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.job-detail-section p {
  font-size: 14px;
  line-height: 1.5;
  color: #666;
  margin: 0;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  background: #f8f9fa;
  border-radius: 0 0 10px 10px;
}

.cancel-button {
  padding: 10px 20px;
  background: white;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-button:hover {
  background: #f8f9fa;
  border-color: #adb5bd;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-item {
    min-width: 100%;
  }
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 200;
    transform: translateX(-100%);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .search-and-filter {
    margin: 10px;
    padding: 15px;
  }

  .jobs-list {
    padding: 0 10px 10px;
  }

  .job-card {
    padding: 20px;
    margin-bottom: 15px;
  }

  .pagination {
    margin: 0 10px 10px;
    padding: 15px;
  }

  .modal-content {
    width: 95%;
    max-height: 90vh;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 15px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.job-card {
  animation: fadeInUp 0.5s ease-out;
}

.job-card:nth-child(2) {
  animation-delay: 0.1s;
}

.job-card:nth-child(3) {
  animation-delay: 0.2s;
}

.job-card:nth-child(4) {
  animation-delay: 0.3s;
}

.job-card:nth-child(5) {
  animation-delay: 0.4s;
}

.modal-content {
  animation: fadeInUp 0.3s ease-out;
}
</style>
