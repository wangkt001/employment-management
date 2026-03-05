<template>
  <div class="resume-generator">
    <el-button type="primary" @click="generateAndDownloadResume">
      生成并下载简历
    </el-button>
    
    <!-- 简历预览 -->
    <div v-if="showPreview" class="resume-preview">
      <h2>简历预览</h2>
      <div ref="resumeContent">
        <ResumeTemplate
          :studentName="studentInfo.name"
          :phone="studentInfo.phone"
          :email="studentInfo.email"
          :education="studentInfo.education"
          :school="studentInfo.school"
          :major="studentInfo.major"
          :selfIntroduction="studentInfo.selfIntroduction"
        />
      </div>
      <el-button type="success" @click="downloadResume">
        下载简历
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue';
import ResumeTemplate from './ResumeTemplate.vue';

const props = defineProps({
  studentInfo: {
    type: Object,
    default: () => ({
      name: '未知',
      phone: '未知',
      email: '未知',
      education: '未知',
      school: '未知',
      major: '未知',
      selfIntroduction: ''
    })
  }
});

const showPreview = ref(false);
const resumeContent = ref(null);

const generateAndDownloadResume = () => {
  showPreview.value = true;
  // 延迟执行，确保DOM已经渲染
  setTimeout(() => {
    downloadResume();
  }, 500);
};

const downloadResume = () => {
  if (!resumeContent.value) return;
  
  // 使用浏览器打印功能
  const printWindow = window.open('', '_blank');
  printWindow.document.write('<html><head><title>简历</title>');
  printWindow.document.write('<style>@media print { body { margin: 0; } }</style>');
  printWindow.document.write('</head><body>');
  printWindow.document.write(resumeContent.value.innerHTML);
  printWindow.document.write('</body></html>');
  printWindow.document.close();
  printWindow.print();
};
</script>

<style scoped>
.resume-generator {
  margin: 20px 0;
}

.resume-preview {
  margin-top: 20px;
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
  background: #f9f9f9;
}

.resume-preview h2 {
  margin-bottom: 20px;
  color: #333;
}

.resume-content {
  margin-bottom: 20px;
}
</style>