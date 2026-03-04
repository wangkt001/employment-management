<template>
  <div class="company-profile-container">
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
        title="企业资料"
        :username="username"
        :role="role"
        @toggle-sidebar="toggleSidebar"
      />

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 企业资料卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <h2>企业基本信息</h2>
            <el-button
              type="primary"
              @click="isEditing = !isEditing"
              class="edit-btn"
            >
              {{ isEditing ? "取消" : "编辑资料" }}
            </el-button>
          </div>

          <!-- 企业资料表单 -->
          <el-form
            :model="companyForm"
            label-position="top"
            class="profile-form"
            :disabled="!isEditing"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="企业名称" required>
                  <el-input
                    v-model="companyForm.companyName"
                    placeholder="请输入企业名称"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属行业" required>
                  <el-select
                    v-model="companyForm.industry"
                    placeholder="请选择所属行业"
                  >
                    <el-option label="互联网" value="互联网" />
                    <el-option label="金融" value="金融" />
                    <el-option label="教育" value="教育" />
                    <el-option label="医疗" value="医疗" />
                    <el-option label="制造业" value="制造业" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="企业规模" required>
                  <el-select
                    v-model="companyForm.scale"
                    placeholder="请选择企业规模"
                  >
                    <el-option label="1-50人" value="1-50人" />
                    <el-option label="51-200人" value="51-200人" />
                    <el-option label="201-500人" value="201-500人" />
                    <el-option label="501-1000人" value="501-1000人" />
                    <el-option label="1000人以上" value="1000人以上" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业性质" required>
                  <el-select
                    v-model="companyForm.nature"
                    placeholder="请选择企业性质"
                  >
                    <el-option label="国有企业" value="国有企业" />
                    <el-option label="民营企业" value="民营企业" />
                    <el-option label="外资企业" value="外资企业" />
                    <el-option label="合资企业" value="合资企业" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="企业简介" required>
              <el-input
                v-model="companyForm.description"
                type="textarea"
                placeholder="请输入企业简介"
                :rows="4"
              />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="联系人" required>
                  <el-input
                    v-model="companyForm.contactPerson"
                    placeholder="请输入联系人姓名"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" required>
                  <el-input
                    v-model="companyForm.contactPhone"
                    placeholder="请输入联系电话"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="联系邮箱">
              <el-input
                v-model="companyForm.contactEmail"
                placeholder="请输入联系邮箱"
              />
            </el-form-item>

            <el-form-item label="企业地址">
              <el-input
                v-model="companyForm.address"
                placeholder="请输入企业地址"
              />
            </el-form-item>

            <el-form-item label="企业网站">
              <el-input
                v-model="companyForm.website"
                placeholder="请输入企业网站"
              />
            </el-form-item>

            <el-form-item label="营业执照">
              <el-upload
                class="upload-demo"
                action="#"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                multiple
                :limit="1"
                :on-exceed="handleExceed"
                :file-list="fileList"
              >
                <el-button type="primary" :disabled="!isEditing">
                  <el-icon><i-ep-upload /></el-icon>
                  上传营业执照
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 JPG/PNG 文件，且不超过 2MB
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <!-- 编辑模式下显示保存按钮 -->
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="saveProfile" class="save-btn">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 认证状态卡片 -->
        <div class="verification-card">
          <h3>认证状态</h3>
          <div class="verification-status">
            <el-tag :type="companyForm.verified ? 'success' : 'warning'">
              {{ companyForm.verified ? "已认证" : "待认证" }}
            </el-tag>
            <p v-if="!companyForm.verified" class="verification-tip">
              提交营业执照后，我们将在 1-3 个工作日内完成审核
            </p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "@/components/company/Sidebar.vue";
import TopNav from "@/components/company/TopNav.vue";

const router = useRouter();
const sidebarCollapsed = ref(false);

// 从本地存储获取用户信息
const username = ref(localStorage.getItem("username") || "企业");
const role = ref(localStorage.getItem("role") || "COMPANY");

// 编辑状态
const isEditing = ref(false);

// 企业表单数据
const companyForm = ref();

// 文件上传相关
const fileList = ref([
  {
    name: "营业执照.jpg",
    url: "#",
  },
]);

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 退出登录
const logout = () => {
  localStorage.clear();
  router.push("/login");
};

// 保存企业资料
const saveProfile = () => {
  // 实际应该调用后端API
  console.log("保存企业资料:", companyForm.value);
  // 模拟保存成功
  alert("企业资料保存成功！");
  isEditing.value = false;
};

// 文件上传相关方法
const handlePreview = (file) => {
  console.log("预览文件:", file);
};

const handleRemove = (file) => {
  console.log("删除文件:", file);
  const index = fileList.value.findIndex((item) => item.name === file.name);
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
};

const beforeRemove = (file) => {
  return confirm(`确定要删除 ${file.name} 吗？`);
};

const handleExceed = (files, fileList) => {
  alert("只能上传一个文件");
};

// 获取企业资料
const fetchCompanyProfile = async () => {
  try {
    // 实际应该从后端API获取
    // const response = await fetch("/api/company/profile", {
    //   credentials: "include",
    // });
    // if (response.ok) {
    //   const data = await response.json();
    //   companyForm.value = data;
    // }

    // 模拟数据已经在初始化时设置
    console.log("获取企业资料成功");
  } catch (err) {
    console.error("获取企业资料失败:", err);
    alert("获取企业资料失败，请稍后重试");
  }
};

onMounted(() => {
  // 页面加载时获取企业资料
  fetchCompanyProfile();
  console.log("Company Profile mounted");
});
</script>

<style scoped>
/* 全局样式 */
.company-profile-container {
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
  gap: 24px;
}

/* 资料卡片 */
.profile-card,
.verification-card {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 资料卡片头部 */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.profile-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 表单样式 */
.profile-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .el-form-item__label {
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
  }

  .el-input,
  .el-select,
  .el-textarea {
    border-radius: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .el-input:focus-within,
  .el-select:focus-within,
  .el-textarea:focus-within {
    box-shadow: 0 0 0 4px rgba(46, 204, 113, 0.1);
  }

  .el-select:focus-within .el-input__wrapper {
    box-shadow: 0 0 0 4px rgba(46, 204, 113, 0.1);
  }

  .el-textarea__inner:focus {
    box-shadow: 0 0 0 4px rgba(46, 204, 113, 0.1);
  }
}

/* 保存按钮 */
.save-btn {
  margin-top: 10px;
}

/* 认证状态卡片 */
.verification-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.verification-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.verification-tip {
  color: #666;
  margin: 0;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-content {
    padding: 20px;
  }

  .profile-card,
  .verification-card {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .main-content {
    width: 100%;
  }

  .page-content {
    padding: 16px;
  }

  .profile-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .verification-status {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
