<template>
  <div>
    <div class="flex items-center justify-between mb-8">
      <h1 class="text-3xl font-bold text-gray-800">视频生成任务</h1>
      
      <div class="flex items-center space-x-4">
        <button 
          @click="refreshTasks"
          class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          :class="{ 'animate-spin': loading }"
        >
          <ArrowPathIcon class="w-5 h-5" />
        </button>
        
        <select 
          v-model="filterStatus"
          class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="all">全部状态</option>
          <option value="pending">等待中</option>
          <option value="processing">生成中</option>
          <option value="succeeded">已完成</option>
          <option value="failed">失败</option>
        </select>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-8">
      <div class="bg-white rounded-xl shadow-sm p-4">
        <p class="text-gray-500 text-sm">等待中</p>
        <p class="text-2xl font-bold text-gray-600">{{ statusCounts.pending }}</p>
      </div>
      
      <div class="bg-white rounded-xl shadow-sm p-4">
        <p class="text-gray-500 text-sm">生成中</p>
        <p class="text-2xl font-bold text-yellow-600">{{ statusCounts.processing }}</p>
      </div>
      
      <div class="bg-white rounded-xl shadow-sm p-4">
        <p class="text-gray-500 text-sm">已完成</p>
        <p class="text-2xl font-bold text-green-600">{{ statusCounts.succeeded }}</p>
      </div>
      
      <div class="bg-white rounded-xl shadow-sm p-4">
        <p class="text-gray-500 text-sm">失败</p>
        <p class="text-2xl font-bold text-red-600">{{ statusCounts.failed }}</p>
      </div>
    </div>

    <!-- 任务列表 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">任务</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">项目</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">进度</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创建时间</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
          </tr>
        </thead>
        
        <tbody class="divide-y divide-gray-200">
          <tr v-for="task in filteredTasks" :key="task.id" class="hover:bg-gray-50">
            <td class="px-6 py-4">
              <div class="flex items-center">
                <VideoCameraIcon class="w-5 h-5 text-gray-400 mr-3" />
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ task.prompt.substring(0, 30) }}...</p>
                  <p class="text-xs text-gray-500">{{ task.duration }}秒</p>
                </div>
              </div>
            </td>
            
            <td class="px-6 py-4">
              <span class="text-sm text-gray-900">{{ getProjectName(task.projectId) }}</span>
            </td>
            
            <td class="px-6 py-4">
              <span 
                class="px-2 py-1 text-xs rounded-full"
                :class="getStatusClass(task.status)"
              >
                {{ getStatusText(task.status) }}
              </span>
            </td>
            
            <td class="px-6 py-4">
              <div v-if="task.status === 'processing'">
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div 
                    class="bg-blue-600 h-2 rounded-full transition-all duration-500"
                    :style="{ width: `${task.progress || 0}%` }"
                  ></div>
                </div>
                <p class="text-xs text-gray-500 mt-1">{{ task.progress || 0 }}%</p>
              </div>
              
              <span v-else class="text-sm text-gray-500">-</span>
            </td>
            
            <td class="px-6 py-4">
              <span class="text-sm text-gray-500">{{ formatDate(task.createdAt) }}</span>
            </td>
            
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end space-x-2">
                <button 
                  v-if="task.status === 'processing'"
                  @click="cancelTask(task.id)"
                  class="p-2 text-gray-400 hover:text-yellow-600"
                  title="取消"
                >
                  <StopIcon class="w-4 h-4" />
                </button>
                
                <button 
                  v-if="task.videoUrl"
                  @click="downloadVideo(task.videoUrl)"
                  class="p-2 text-gray-400 hover:text-blue-600"
                  title="下载"
                >
                  <ArrowDownTrayIcon class="w-4 h-4" />
                </button>
                
                <button 
                  v-if="task.status === 'succeeded'"
                  @click="previewVideo(task)"
                  class="p-2 text-gray-400 hover:text-green-600"
                  title="预览"
                >
                  <EyeIcon class="w-4 h-4" />
                </button>
                
                <button 
                  @click="deleteTask(task.id)"
                  class="p-2 text-gray-400 hover:text-red-600"
                  title="删除"
                >
                  <TrashIcon class="w-4 h-4" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredTasks.length === 0" class="text-center py-12 text-gray-500">
        暂无任务
      </div>
    </div>

    <!-- 视频预览弹窗 -->
    <div v-if="previewTask" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl max-w-4xl w-full mx-4">
        <div class="p-4 border-b flex items-center justify-between">
          <h3 class="text-xl font-semibold">视频预览</h3>
          <button @click="previewTask = null" class="p-2 hover:bg-gray-100 rounded-lg">
            <XMarkIcon class="w-6 h-6" />
          </button>
        </div>
        
        <div class="p-6">
          <video 
            :src="previewTask.videoUrl" 
            controls 
            class="w-full rounded-lg"
            autoplay
          ></video>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useTaskStore, useProjectStore } from '../stores'
import { 
  VideoCameraIcon, 
  ArrowPathIcon, 
  StopIcon,
  ArrowDownTrayIcon,
  EyeIcon,
  TrashIcon,
  XMarkIcon 
} from '@heroicons/vue/24/outline'
import { format } from 'date-fns'
import { zhCN } from 'date-fns/locale'
import type { VideoTask } from '../types'

const taskStore = useTaskStore()
const projectStore = useProjectStore()

const tasks = computed(() => taskStore.tasks)
const loading = ref(false)
const filterStatus = ref('all')
const previewTask = ref<VideoTask | null>(null)

let refreshInterval: number | null = null

const filteredTasks = computed(() => {
  if (filterStatus.value === 'all') return tasks.value
  return tasks.value.filter(t => t.status === filterStatus.value)
})

const statusCounts = computed(() => ({
  pending: tasks.value.filter(t => t.status === 'pending').length,
  processing: tasks.value.filter(t => t.status === 'processing').length,
  succeeded: tasks.value.filter(t => t.status === 'succeeded').length,
  failed: tasks.value.filter(t => t.status === 'failed').length,
}))

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-100 text-gray-600',
    processing: 'bg-yellow-100 text-yellow-600',
    succeeded: 'bg-green-100 text-green-600',
    failed: 'bg-red-100 text-red-600',
    cancelled: 'bg-gray-100 text-gray-400',
  }
  return classes[status] || classes.pending
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '等待中',
    processing: '生成中',
    succeeded: '已完成',
    failed: '失败',
    cancelled: '已取消',
  }
  return texts[status] || status
}

const getProjectName = (projectId: string) => {
  const project = projectStore.projects.find(p => p.id === projectId)
  return project?.title || '未知项目'
}

const formatDate = (date: string) => {
  return format(new Date(date), 'MM-dd HH:mm', { locale: zhCN })
}

const refreshTasks = async () => {
  loading.value = true
  // 这里应该调用 API 刷新任务列表
  loading.value = false
}

const cancelTask = (taskId: string) => {
  if (confirm('确定要取消这个任务吗？')) {
    // 调用 API 取消任务
    taskStore.updateTask(taskId, { status: 'cancelled' })
  }
}

const deleteTask = (taskId: string) => {
  if (confirm('确定要删除这个任务吗？')) {
    // taskStore.removeTask(taskId)
  }
}

const downloadVideo = (url: string) => {
  const a = document.createElement('a')
  a.href = url
  a.download = `video_${Date.now()}.mp4`
  a.click()
}

const previewVideo = (task: VideoTask) => {
  previewTask.value = task
}

// 自动刷新
onMounted(() => {
  refreshInterval = window.setInterval(() => {
    // 刷新进行中的任务状态
  }, 5000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>
