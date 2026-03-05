<template>
  <div class="resume-template">
    <div class="resume-header">
      <h1>{{ studentName }}</h1>
      <div class="contact-info">
        <p>电话: {{ phone }}</p>
        <p>邮箱: {{ email }}</p>
        <p>学历: {{ education }}</p>
        <p>毕业院校: {{ school }}</p>
        <p>专业: {{ major }}</p>
      </div>
    </div>

    <div class="resume-section">
      <h2>个人简介</h2>
      <p>{{ selfIntroduction || "暂无个人简介" }}</p>
    </div>

    <div class="resume-section">
      <h2>工作经历</h2>
      <div class="experience-item">
        <div class="experience-header">
          <h3>软件开发工程师</h3>
          <span>2024.03 - 至今</span>
        </div>
        <p class="company-name">科技有限公司</p>
        <ul class="experience-details">
          <li>参与公司核心产品的开发与维护，使用Vue.js和Spring Boot技术栈</li>
          <li>负责前端组件的设计与实现，优化用户界面和用户体验</li>
          <li>与后端团队协作，实现前后端数据交互和接口对接</li>
          <li>参与代码评审和技术方案讨论，持续改进代码质量</li>
        </ul>
      </div>

      <div class="experience-item">
        <div class="experience-header">
          <h3>前端开发实习生</h3>
          <span>2023.06 - 2023.12</span>
        </div>
        <p class="company-name">互联网科技公司</p>
        <ul class="experience-details">
          <li>协助开发公司官网和内部管理系统的前端部分</li>
          <li>学习并应用Vue.js、Element Plus等前端技术</li>
          <li>参与前端页面的测试和bug修复</li>
          <li>文档编写和技术支持工作</li>
        </ul>
      </div>
    </div>

    <div class="resume-section">
      <h2>技能专长</h2>
      <div class="skills">
        <span class="skill-tag">Vue.js</span>
        <span class="skill-tag">JavaScript</span>
        <span class="skill-tag">HTML/CSS</span>
        <span class="skill-tag">Spring Boot</span>
        <span class="skill-tag">MySQL</span>
        <span class="skill-tag">Git</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  studentName: {
    type: String,
    default: "未知",
  },
  phone: {
    type: String,
    default: "未知",
  },
  email: {
    type: String,
    default: "未知",
  },
  education: {
    type: String,
    default: "未知",
  },
  school: {
    type: String,
    default: "未知",
  },
  major: {
    type: String,
    default: "未知",
  },
  selfIntroduction: {
    type: String,
    default: "",
  },
});
</script>

<style scoped>
.resume-template {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.resume-header {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #333;
}

.resume-header h1 {
  font-size: 32px;
  margin-bottom: 10px;
  color: #333;
}

.contact-info {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.resume-section {
  margin-bottom: 30px;
}

.resume-section h2 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
  border-bottom: 1px solid #ddd;
  padding-bottom: 5px;
}

.experience-item {
  margin-bottom: 20px;
}

.experience-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.experience-header h3 {
  font-size: 16px;
  margin: 0;
  color: #333;
}

.experience-header span {
  font-size: 14px;
  color: #666;
}

.company-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.experience-details {
  margin: 0;
  padding-left: 20px;
  font-size: 14px;
  line-height: 1.5;
}

.experience-details li {
  margin-bottom: 5px;
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  background: #f0f0f0;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 14px;
  color: #666;
}

@media print {
  .resume-template {
    box-shadow: none;
    padding: 0;
  }
}
</style>
