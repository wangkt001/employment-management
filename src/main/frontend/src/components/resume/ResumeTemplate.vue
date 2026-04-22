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

@media print {
  .resume-template {
    box-shadow: none;
    padding: 0;
  }
}
</style>
