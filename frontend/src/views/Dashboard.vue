<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-8">工作台</h1>
    
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div v-for="stat in stats" :key="stat.title" class="bg-white rounded-xl shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">{{ stat.title }}</p>
            <p class="text-3xl font-bold text-gray-800">{{ stat.value }}</p>
          </div>
          <component :is="stat.icon" class="w-10 h-10 text-blue-500" />
        </div>
      </div>
    </div>

    <!-- 最近项目 -->
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-semibold text-gray-800">最近项目</h2>
        <router-link to="/projects" class="text-blue-600 hover:text-blue-700">查看全部 →</router-link>
      </div>
      
      <div v-if="projects.length === 0" class="text-center py-12 text-gray-500">
        暂无项目，<router-link to="/projects" class="text-blue-600">创建新项目</router-link>
      </div>
      
      <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div 
          v-for="project in recentProjects" 
          :key="project.id"
          class="border rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
          @click="$router.push(`/projects/${project.id}`)"
        >
          <h3 class="font-semibold text-gray-800 mb-2">{{ project.title }}</h3>
          <p class="text-gray-500 text-sm mb-4 line-clamp-2">{{ project.description }}</p>
          <div class="flex items-center justify-between">
            <span 
              class="px-2 py-1 rounded-full text-xs"
              :class="getStatusClass(project.status)"
            >
              {{ getStatusText(project.status) }}
            </span>
            <span class="text-gray-400 text-sm">{{ formatDate(project.updatedAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useProjectStore } from '../stores'
import { projectApi } from '../api'
import { 
  FolderIcon, 
  VideoCameraIcon, 
  PhotoIcon, 
  ClockIcon 
} from '@heroicons/vue/24/outline'
import { format } from 'date-fns'
import { zhCN } from 'date-fns/locale'

const projectStore = useProjectStore()
const projects = computed(() => projectStore.projects)
const recentProjects = computed(() => projects.value.slice(0, 3))
const loading = ref(false)

// 加载项目数据
onMounted(async () => {
  loading.value = true
  try {
    const projects = await projectApi.getProjects()
    projectStore.setProjects(projects)
  } catch (error) {
    console.error('加载项目失败:', error)
  } finally {
    loading.value = false
  }
})

const stats = [
  { title: '项目总数', value: projects.value.length, icon: FolderIcon },
  { title: '视频任务', value: 0, icon: VideoCameraIcon },
  { title: '素材数量', value: 0, icon: PhotoIcon },
  { title: '进行中', value: projectStore.activeProjects.length, icon: ClockIcon },
]

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600',
    in_progress: 'bg-blue-100 text-blue-600',
    completed: 'bg-green-100 text-green-600',
  }
  return classes[status] || classes.draft
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    draft: '草稿',
    in_progress: '进行中',
    completed: '已完成',
  }
  return texts[status] || status
}

const formatDate = (date: string) => {
  return format(new Date(date), 'MM-dd', { locale: zhCN })
}
</script>
