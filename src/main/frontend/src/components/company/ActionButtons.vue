<template>
  <el-card class="action-buttons-section" shadow="hover">
    <div class="action-content">
      <el-button 
        type="primary" 
        size="large" 
        @click="emit('add-job')"
        class="add-job-button"
      >
        <template #icon>
          <el-icon><i-ep-plus /></el-icon>
        </template>
        发布新岗位
      </el-button>
      <el-tabs v-model="localActiveTab" class="filter-tabs" @tab-click="handleTabClick">
        <el-tab-pane v-for="tab in filterTabs" :key="tab.value" :label="tab.label" :name="tab.value">
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-card>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  activeTab: {
    type: String,
    default: 'all'
  }
})

const emit = defineEmits(['add-job', 'filter-change'])

const localActiveTab = ref(props.activeTab)

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '已上架', value: 'active' },
  { label: '已下架', value: 'inactive' }
]

const handleTabClick = (tab) => {
  localActiveTab.value = tab.props.name
  emit('filter-change', localActiveTab.value)
}

watch(() => props.activeTab, (newValue) => {
  localActiveTab.value = newValue
})
</script>

<style scoped>
.action-buttons-section {
  margin: 24px 30px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  background: #ffffff;
}

.action-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
}

.add-job-button {
  background: linear-gradient(135deg, #2ecc71, #27ae60);
  border: none;
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  font-weight: 600;
  padding: 12px 24px;
}

.add-job-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(46, 204, 113, 0.4);
  background: linear-gradient(135deg, #27ae60, #229954);
}

.filter-tabs {
  flex: 1;
  max-width: 400px;
  margin-left: 24px;
}

.filter-tabs .el-tabs__nav {
  border-bottom: 1px solid #e9ecef;
}

.filter-tabs .el-tabs__item {
  padding: 0 16px;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
}

.filter-tabs .el-tabs__item:hover {
  color: #2ecc71;
}

.filter-tabs .el-tabs__item.is-active {
  color: #2ecc71;
  font-weight: 600;
}

.filter-tabs .el-tabs__active-bar {
  background-color: #2ecc71;
  height: 3px;
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .action-content {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .filter-tabs {
    margin-left: 0;
    max-width: none;
  }
  
  .add-job-button {
    align-self: flex-start;
  }
}

@media (max-width: 768px) {
  .action-buttons-section {
    margin: 20px;
  }
  
  .action-content {
    padding: 16px 20px;
  }
  
  .add-job-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .action-buttons-section {
    margin: 16px;
  }
  
  .action-content {
    padding: 12px 16px;
  }
}
</style>