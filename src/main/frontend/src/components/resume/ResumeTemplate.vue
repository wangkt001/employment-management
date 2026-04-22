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
        <p v-if="address">地址: {{ address }}</p>
      </div>
    </div>

    <div class="resume-section">
      <h2>个人简介</h2>
      <p>{{ selfIntroduction || "暂无个人简介" }}</p>
    </div>

    <div class="resume-section">
      <h2>求职意向</h2>
      <div class="intention-grid">
        <div class="intention-item">
          <span class="intention-label">职业方向</span>
          <span>{{ careerDirection || "待补充" }}</span>
        </div>
        <div class="intention-item">
          <span class="intention-label">期望薪资</span>
          <span>{{ expectedSalary || "待补充" }}</span>
        </div>
      </div>
    </div>

    <div class="resume-section">
      <h2>教育背景</h2>
      <p>{{ school || "待补充学校信息" }} / {{ major || "待补充专业信息" }}</p>
      <p>{{ education || "待补充学历信息" }}</p>
    </div>

    <div
      class="resume-section"
      v-if="workExperiences && workExperiences.length > 0"
    >
      <h2>工作经历</h2>
      <div
        v-for="(exp, index) in workExperiences"
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
</template>

<script setup>
defineProps({
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
  address: {
    type: String,
    default: "",
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
  careerDirection: {
    type: String,
    default: "",
  },
  expectedSalary: {
    type: String,
    default: "",
  },
  selfIntroduction: {
    type: String,
    default: "",
  },
  workExperiences: {
    type: Array,
    default: () => [],
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

.intention-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.intention-item {
  min-width: 220px;
  background: #f7f8fa;
  border-radius: 10px;
  padding: 12px 16px;
  font-size: 14px;
  color: #333;
}

.intention-label {
  display: block;
  margin-bottom: 6px;
  color: #666;
  font-size: 12px;
}

.work-experience-item {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #ddd;
}

.work-experience-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.exp-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
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

@media print {
  .resume-template {
    box-shadow: none;
    padding: 0;
  }
}
</style>
