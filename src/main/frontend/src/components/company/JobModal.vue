<template>
  <el-dialog
    v-model="localVisible"
    :title="isEditing ? '编辑岗位' : '发布新岗位'"
    width="90%"
    max-width="600px"
    @close="closeModal"
    class="job-modal"
  >
    <el-form :model="formData" label-position="top" class="job-form">
      <el-form-item label="岗位名称" required>
        <el-input v-model="formData.title" placeholder="请输入岗位名称" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="薪资范围" required>
            <el-select v-model="formData.salary" placeholder="请选择薪资范围">
              <el-option value="10K以下" label="10K以下" />
              <el-option value="10K-20K" label="10K-20K" />
              <el-option value="20K-30K" label="20K-30K" />
              <el-option value="30K以上" label="30K以上" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工作地点" required>
            <el-select v-model="formData.location" placeholder="请选择工作地点">
              <el-option label="北京" value="北京" />
              <el-option label="上海" value="上海" />
              <el-option label="广州" value="广州" />
              <el-option label="深圳" value="深圳" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工作经验" required>
            <el-select
              v-model="formData.experience"
              placeholder="请选择工作经验"
            >
              <el-option value="应届毕业生" label="应届毕业生" />
              <el-option value="1-3年" label="1-3年" />
              <el-option value="3-5年" label="3-5年" />
              <el-option value="5年以上" label="5年以上" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学历要求" required>
            <el-select
              v-model="formData.education"
              placeholder="请选择学历要求"
            >
              <el-option value="高中及以上" label="高中及以上" />
              <el-option value="大专及以上" label="大专及以上" />
              <el-option value="本科及以上" label="本科及以上" />
              <el-option value="硕士及以上" label="硕士及以上" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="行业" required>
            <el-select v-model="formData.industry" placeholder="请选择行业">
              <el-option value="互联网" label="互联网" />
              <el-option value="金融" label="金融" />
              <el-option value="教育" label="教育" />
              <el-option value="医疗" label="医疗" />
              <el-option value="制造业" label="制造业" />
              <el-option value="其他" label="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="岗位描述" required>
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="请输入岗位描述"
          :rows="4"
        />
      </el-form-item>
      <el-form-item label="岗位要求" required>
        <el-input
          v-model="formData.requirements"
          type="textarea"
          placeholder="请输入岗位要求"
          :rows="4"
        />
      </el-form-item>
      <el-form-item label="岗位标签">
        <el-input
          v-model="formData.tagsInput"
          placeholder="请输入标签，用逗号分隔"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="submitForm">
          {{ isEditing ? "保存修改" : "发布岗位" }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch } from "vue";

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  },
  isEditing: {
    type: Boolean,
    default: false,
  },
  jobData: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(["close", "submit"]);

const formData = ref({
  title: "",
  salary: "",
  location: "",
  experience: "",
  education: "",
  industry: "",
  description: "",
  requirements: "",
  tagsInput: "",
  companyId: localStorage.getItem("companyId") ? parseInt(localStorage.getItem("companyId")) : 4, // 从本地缓存获取公司ID，默认4
  userId: localStorage.getItem("userId") ? parseInt(localStorage.getItem("userId")) : 2, // 从本地缓存获取用户ID，默认2
});

const localVisible = ref(props.visible);

watch(
  () => props.visible,
  (newValue) => {
    localVisible.value = newValue;
  },
);

watch(localVisible, (newValue) => {
  if (!newValue) {
    emit("close");
  }
});

watch(
  () => props.jobData,
  (newJobData) => {
    if (newJobData) {
      formData.value = {
        title: newJobData.title || "",
        salary: newJobData.salary || "",
        location: newJobData.location || "",
        experience: newJobData.experience || "",
        education: newJobData.education || "",
        industry: newJobData.industry || "",
        description: newJobData.description || "",
        requirements: newJobData.requirements || "",
        tagsInput: newJobData.tags ? newJobData.tags.join(", ") : "",
        companyId: newJobData.companyId || parseInt(localStorage.getItem("companyId") || "0"),
        userId: parseInt(localStorage.getItem("userId") || "0"),
      };
    }
  },
  { immediate: true },
);

const closeModal = () => {
  localVisible.value = false;
  emit("close");
};

const submitForm = () => {
  emit("submit", formData.value);
};
</script>

<style scoped>
.job-modal {
  .el-dialog__header {
    background: #f8f9fa;
    border-bottom: 1px solid #f0f0f0;
    border-radius: 12px 12px 0 0;
  }

  .el-dialog__title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }

  .el-dialog__body {
    padding: 30px;
  }

  .el-dialog__footer {
    background: #f8f9fa;
    border-top: 1px solid #f0f0f0;
    border-radius: 0 0 12px 12px;
    padding: 25px 30px;
  }
}

.job-form {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .job-modal {
    .el-dialog__body {
      padding: 20px;
    }

    .el-dialog__footer {
      padding: 20px;
    }
  }
}
</style>
<style lang="scss"></style>
