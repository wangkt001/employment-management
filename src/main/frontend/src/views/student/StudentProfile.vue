<template>
  <div class="student-profile-container">
    <Sidebar
      :collapsed="sidebarCollapsed"
      @toggle-sidebar="toggleSidebar"
      @logout="logout"
    />

    <main class="main-content">
      <TopNav
        title="我的简历"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <div class="page-content" v-loading="loading">
        <div class="profile-card">
          <div class="profile-header">
            <div class="profile-avatar">
              <el-avatar :size="88" class="avatar">
                {{ (profile.name || username).slice(0, 1) }}
              </el-avatar>
              <div>
                <h2 class="profile-name">{{ profile.name || username }}</h2>
                <p class="profile-role">{{ roleText }}</p>
              </div>
            </div>
            <div class="profile-actions">
              <el-button @click="openEditDialog">编辑简历</el-button>
              <el-button type="primary" @click="fetchResume">刷新</el-button>
            </div>
          </div>

          <div class="profile-details">
            <div class="detail-section">
              <h3>基本信息</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">姓名</span>
                  <span class="detail-value">{{
                    displayValue(profile.name)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学号</span>
                  <span class="detail-value">{{
                    displayValue(profile.studentId)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">邮箱</span>
                  <span class="detail-value">{{
                    displayValue(profile.email)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">电话</span>
                  <span class="detail-value">{{
                    displayValue(profile.phone)
                  }}</span>
                </div>
                <div class="detail-item full-width">
                  <span class="detail-label">地址</span>
                  <span class="detail-value">{{
                    displayValue(profile.address)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>教育背景</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">学校</span>
                  <span class="detail-value">{{
                    displayValue(profile.school)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">专业</span>
                  <span class="detail-value">{{
                    displayValue(profile.major)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">院系</span>
                  <span class="detail-value">{{
                    displayValue(profile.department)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">年级</span>
                  <span class="detail-value">{{
                    displayValue(profile.grade)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学历</span>
                  <span class="detail-value">{{
                    displayValue(profile.education)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>求职意向</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">职业方向</span>
                  <span class="detail-value">{{
                    displayValue(profile.careerDirection)
                  }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">期望薪资</span>
                  <span class="detail-value">{{
                    displayValue(profile.expectedSalary)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h3>自我介绍</h3>
              <p class="introduction-text">
                {{ profile.selfIntroduction || "暂未填写自我介绍" }}
              </p>
            </div>

            <div
              class="detail-section"
              v-if="
                profile.workExperiences && profile.workExperiences.length > 0
              "
            >
              <h3>工作经历</h3>
              <div class="work-experience-list">
                <div
                  v-for="(exp, index) in profile.workExperiences"
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
            </div>
          </div>
        </div>

        <div class="profile-card">
          <div class="section-title">
            <h3>简历预览与导出</h3>
            <span>编辑后可直接生成简历</span>
          </div>
          <ResumeGenerator :student-info="profile" />
        </div>
      </div>
    </main>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑简历"
      width="720px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="88px" class="edit-form">
        <div class="form-section">
          <h4>基本信息</h4>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="姓名">
                <el-input v-model="editForm.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学号" required>
                <el-input
                  v-model="editForm.studentId"
                  placeholder="请输入学号"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="editForm.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="电话">
                <el-input v-model="editForm.phone" placeholder="请输入电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="地址">
            <el-input v-model="editForm.address" placeholder="请输入地址" />
          </el-form-item>
        </div>

        <div class="form-section">
          <h4>教育背景</h4>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="学校">
                <el-input v-model="editForm.school" placeholder="请输入学校" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" required>
                <el-input v-model="editForm.major" placeholder="请输入专业" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="院系">
                <el-input
                  v-model="editForm.department"
                  placeholder="请输入院系"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年级">
                <el-input v-model="editForm.grade" placeholder="请输入年级" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学历">
                <el-select
                  v-model="editForm.education"
                  placeholder="请选择学历"
                  style="width: 100%"
                >
                  <el-option label="大专" value="大专" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4>求职意向</h4>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="职业方向">
                <el-input
                  v-model="editForm.careerDirection"
                  placeholder="如：Java开发工程师"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="期望薪资">
                <el-input
                  v-model="editForm.expectedSalary"
                  placeholder="如：8k-12k"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4>自我介绍</h4>
          <el-form-item label-width="0">
            <el-input
              v-model="editForm.selfIntroduction"
              type="textarea"
              :rows="5"
              placeholder="请输入自我介绍"
            />
          </el-form-item>
        </div>

        <div class="form-section">
          <div class="section-header">
            <h4>工作经历</h4>
            <el-button type="primary" size="small" @click="addWorkExperience">
              + 添加经历
            </el-button>
          </div>
          <div
            v-for="(exp, index) in editForm.workExperiences"
            :key="index"
            class="work-experience-form-item"
          >
            <div class="exp-form-header">
              <span>经历 {{ index + 1 }}</span>
              <el-button
                type="danger"
                size="small"
                text
                @click="removeWorkExperience(index)"
              >
                删除
              </el-button>
            </div>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="公司名称" required>
                  <el-input
                    v-model="exp.companyName"
                    placeholder="请输入公司名称"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位" required>
                  <el-input v-model="exp.position" placeholder="请输入职位" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门">
                  <el-input v-model="exp.department" placeholder="请输入部门" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入职时间" required>
                  <el-date-picker
                    v-model="exp.startDate"
                    type="month"
                    placeholder="选择月份"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="离职时间">
                  <el-date-picker
                    v-model="exp.endDate"
                    type="month"
                    placeholder="选择月份"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    style="width: 100%"
                    :disabled="exp.currentJob"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="至今">
                  <el-checkbox v-model="exp.currentJob"
                    >目前仍在此公司</el-checkbox
                  >
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="工作描述">
              <el-input
                v-model="exp.description"
                type="textarea"
                :rows="3"
                placeholder="描述你的工作职责和内容"
              />
            </el-form-item>
            <el-form-item label="主要业绩">
              <el-input
                v-model="exp.achievements"
                type="textarea"
                :rows="3"
                placeholder="描述你的工作成果和业绩"
              />
            </el-form-item>
          </div>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveResume">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";
import ResumeGenerator from "@/components/resume/ResumeGenerator.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);
const username = ref(localStorage.getItem("username") || "学生");
const role = ref(localStorage.getItem("role") || "STUDENT");
const userId = localStorage.getItem("userId");

const loading = ref(false);
const saving = ref(false);
const editDialogVisible = ref(false);

const createEmptyProfile = () => ({
  name: "",
  username: "",
  email: "",
  phone: "",
  address: "",
  studentId: "",
  major: "",
  department: "",
  grade: "",
  education: "",
  school: "",
  expectedSalary: "",
  careerDirection: "",
  selfIntroduction: "",
  resumeUrl: "",
  workExperiences: [],
});

const profile = ref(createEmptyProfile());
const editForm = ref(createEmptyProfile());

const roleText = computed(() => {
  if (role.value === "STUDENT") {
    return "学生简历";
  }
  return "用户";
});

const displayValue = (value) => value || "未设置";

const syncProfile = (data) => {
  profile.value = {
    ...createEmptyProfile(),
    ...data,
  };
  username.value =
    profile.value.name || localStorage.getItem("username") || "学生";
};

const fetchResume = async () => {
  if (!userId) {
    ElMessage.error("未获取到当前用户信息，请重新登录");
    return;
  }

  loading.value = true;
  try {
    const response = await fetch(`/employment/api/students/${userId}/resume`, {
      credentials: "include",
    });
    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || "获取简历信息失败");
    }

    syncProfile(data);
  } catch (error) {
    console.error("获取简历信息失败:", error);
    ElMessage.error(error.message || "获取简历信息失败");
  } finally {
    loading.value = false;
  }
};

const openEditDialog = () => {
  editForm.value = {
    ...profile.value,
    workExperiences: profile.value.workExperiences
      ? profile.value.workExperiences.map((exp) => ({ ...exp }))
      : [],
  };
  editDialogVisible.value = true;
};

const addWorkExperience = () => {
  editForm.value.workExperiences.push({
    id: null,
    companyName: "",
    position: "",
    department: "",
    startDate: "",
    endDate: "",
    currentJob: false,
    description: "",
    achievements: "",
  });
};

const removeWorkExperience = (index) => {
  editForm.value.workExperiences.splice(index, 1);
};

const saveResume = async () => {
  if (!editForm.value.studentId || !editForm.value.major) {
    ElMessage.warning("请至少填写学号和专业");
    return;
  }

  saving.value = true;
  try {
    const response = await fetch(`/employment/api/students/${userId}/resume`, {
      method: "PUT",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(editForm.value),
    });
    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || "保存简历失败");
    }

    syncProfile(data);
    localStorage.setItem(
      "username",
      data.username || localStorage.getItem("username") || "",
    );
    editDialogVisible.value = false;
    ElMessage.success("简历保存成功");
  } catch (error) {
    console.error("保存简历失败:", error);
    ElMessage.error(error.message || "保存简历失败");
  } finally {
    saving.value = false;
  }
};

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const logout = () => {
  localStorage.clear();
  router.push("/login");
};

onMounted(() => {
  fetchResume();
});
</script>

<style scoped>
.student-profile-container {
  display: flex;
  min-height: 100vh;
  background: #f8f9fa;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #ffffff;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.05);
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.profile-avatar {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: #fff;
  font-size: 28px;
  font-weight: 600;
}

.profile-name {
  margin: 0 0 6px;
  font-size: 24px;
  color: #333;
}

.profile-role {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.profile-actions {
  display: flex;
  gap: 12px;
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  border: 1px solid #edf0f2;
  border-radius: 10px;
  padding: 20px;
  background: #fafbfc;
}

.detail-section h3,
.section-title h3,
.form-section h4 {
  margin: 0 0 16px;
  color: #333;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 13px;
  color: #666;
}

.detail-value {
  font-size: 14px;
  color: #222;
  word-break: break-word;
}

.introduction-text {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.7;
  color: #333;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.section-title span {
  color: #666;
  font-size: 13px;
}

.form-section + .form-section {
  margin-top: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h4 {
  margin: 0;
}

.work-experience-form-item {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background: #fafbfc;
}

.exp-form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #333;
}

.work-experience-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.work-experience-item {
  border-left: 3px solid #3498db;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 0 8px 8px 0;
}

.exp-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.exp-company {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.exp-period {
  font-size: 13px;
  color: #666;
}

.exp-position {
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

.exp-description {
  margin: 0 0 8px;
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

@media (max-width: 768px) {
  .page-content {
    padding: 16px;
  }

  .profile-card {
    padding: 18px;
  }

  .profile-header,
  .section-title {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile-avatar {
    align-items: flex-start;
  }

  .profile-actions {
    width: 100%;
  }
}
</style>
