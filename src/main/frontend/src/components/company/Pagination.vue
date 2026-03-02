<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="localCurrentPage"
      :page-size="10"
      layout="prev, pager, next, jumper"
      :total="totalPages * 10"
      @current-change="handleCurrentChange"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['page-change'])

const localCurrentPage = ref(props.currentPage)

const handleCurrentChange = (page) => {
  localCurrentPage.value = page
  emit('page-change', page)
}

watch(() => props.currentPage, (newValue) => {
  localCurrentPage.value = newValue
})
</script>

<style scoped>
.pagination-container {
  margin: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.pagination-container:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination-container {
    margin: 20px;
    padding: 15px;
  }
}
</style>