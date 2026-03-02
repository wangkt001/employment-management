<template>
  <el-card class="job-card" shadow="hover">
    <div class="job-card-header">
      <h3 class="job-title">{{ job.title }}</h3>
      <el-tag size="large" type="danger" effect="dark">{{ job.salary }}</el-tag>
    </div>
    <div class="job-card-body">
      <div class="job-meta">
        <el-tag size="small" effect="plain" class="meta-tag">
          <el-icon><i-ep-location /></el-icon>
          {{ job.location }}
        </el-tag>
        <el-tag size="small" effect="plain" class="meta-tag">
          <el-icon><i-ep-timer /></el-icon>
          {{ job.experience }}
        </el-tag>
        <el-tag size="small" effect="plain" class="meta-tag">
          <el-icon><i-ep-reading /></el-icon>
          {{ job.education }}
        </el-tag>
      </div>
      <el-collapse class="job-description">
        <el-collapse-item title="岗位描述" name="1">
          <p>{{ job.description }}</p>
        </el-collapse-item>
      </el-collapse>
      <div class="job-stats">
        <el-tag size="small" effect="plain" class="stat-tag">
          <el-icon><i-ep-view /></el-icon>
          {{ job.views }} 浏览
        </el-tag>
        <el-tag size="small" effect="plain" class="stat-tag">
          <el-icon><i-ep-document /></el-icon>
          {{ job.applications }} 申请
        </el-tag>
        <el-tag size="small" effect="plain" class="stat-tag">
          <el-icon><i-ep-calendar /></el-icon>
          {{ job.postedDate }}
        </el-tag>
      </div>
    </div>
    <div class="job-card-footer">
      <el-tag :type="job.status === 'active' ? 'success' : 'info'" size="small">
        {{ getStatusText(job.status) }}
      </el-tag>
      <div class="job-actions">
        <el-button size="small" type="primary" plain @click="editJob(job)">
          <template #icon>
            <el-icon><i-ep-edit /></el-icon>
          </template>
          编辑
        </el-button>
        <el-button size="small" type="danger" plain @click="deleteJob(job.id)">
          <template #icon>
            <el-icon><i-ep-delete /></el-icon>
          </template>
          删除
        </el-button>
        <el-button 
          size="small" 
          :type="job.status === 'active' ? 'warning' : 'success'" 
          plain
          @click="toggleJobStatus(job.id, job.status === 'active' ? 'inactive' : 'active')"
        >
          <template #icon>
            <el-icon v-if="job.status === 'active'">
              <i-ep-switch-button />
            </el-icon>
            <el-icon v-else>
              <i-ep-check />
            </el-icon>
          </template>
          {{ job.status === 'active' ? '下架' : '上架' }}
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  job: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit-job', 'delete-job', 'toggle-status'])

const getStatusText = (status) => {
  return status === 'active' ? '已上架' : '已下架'
}

const editJob = (job) => {
  emit('edit-job', job)
}

const deleteJob = (jobId) => {
  emit('delete-job', jobId)
}

const toggleJobStatus = (jobId, newStatus) => {
  emit('toggle-status', jobId, newStatus)
}
</script>

<style scoped>
.job-card {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e9ecef;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.5s ease-out;
  background: #ffffff;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.job-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  border-color: #2ecc71;
}

.job-card .el-card__body {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.job-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  transition: all 0.3s ease;
  flex: 1;
  margin-right: 16px;
  line-height: 1.4;
}

.job-card:hover .job-title {
  color: #2ecc71;
}

.job-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding: 6px 12px;
  background: #f8f9fa !important;
  transition: all 0.3s ease;
  border-radius: 16px;
}

.meta-tag:hover {
  background: #e8f5e8 !important;
  color: #2ecc71 !important;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(46, 204, 113, 0.15);
}

.job-description {
  margin-bottom: 16px;
  flex: 1;
}

.job-description .el-collapse {
  border: none !important;
}

.job-description .el-collapse-item {
  border: none !important;
}

.job-description .el-collapse-item__header {
  padding: 0 !important;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  border: none !important;
}

.job-description .el-collapse-item__content {
  padding: 12px 0 !important;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}

.job-stats {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
}

.stat-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
  transition: all 0.3s ease;
}

.stat-tag:hover {
  color: #2ecc71 !important;
  transform: translateY(-2px);
}

.job-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.job-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.job-actions .el-button {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.job-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .job-card .el-card__body {
    padding: 20px;
  }
  
  .job-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .job-card-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .job-actions {
    justify-content: center;
  }
  
  .job-title {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .job-card .el-card__body {
    padding: 16px;
  }
  
  .job-card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .job-title {
    margin-right: 0;
  }
}

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
</style>