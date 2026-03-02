<template>
  <div v-if="isLoading" class="status-container">
    <el-skeleton :rows="3" animated />
  </div>

  <div v-else-if="error" class="status-container">
    <el-result icon="error" title="加载失败" :sub-title="error">
      <template #extra>
        <el-button type="primary" @click="emit('retry')">
          <template #icon>
            <el-icon><i-ep-refresh /></el-icon>
          </template>
          重试
        </el-button>
      </template>
    </el-result>
  </div>

  <div v-else-if="isEmpty" class="status-container">
    <el-result
      icon="warning"
      :title="isEmptyTitle"
      :sub-title="isEmptySubtitle"
    >
      <template #extra>
        <el-button v-if="showAddButton" type="primary" @click="emit('add-job')">
          <template #icon>
            <el-icon><i-ep-plus /></el-icon>
          </template>
          发布第一个岗位
        </el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from "vue";

const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false,
  },
  error: {
    type: String,
    default: "",
  },
  isEmpty: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: "jobs", // jobs, applications
  },
});

const emit = defineEmits(["retry", "add-job"]);

const isEmptyTitle = computed(() => {
  return props.type === "applications" ? "暂无申请" : "暂无岗位数据";
});

const isEmptySubtitle = computed(() => {
  return props.type === "applications" ? "还没有申请任何岗位，快去浏览并申请心仪的岗位吧！" : "还没有发布任何岗位，快来发布第一个岗位吧！";
});

const showAddButton = computed(() => {
  return props.type !== "applications";
});
</script>

<style scoped>
.status-container {
  background: white;
  margin: 30px 0;
  padding: 80px 40px;
  position: relative;
  overflow: hidden;
}
</style>
