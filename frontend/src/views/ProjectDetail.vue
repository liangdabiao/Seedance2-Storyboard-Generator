<template>
  <div v-if="project">
    <!-- 项目头部 -->
    <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
      <div class="flex items-start justify-between">
        <div>
          <div class="flex items-center space-x-3 mb-2">
            <h1 class="text-3xl font-bold text-gray-800">{{ project.title }}</h1>
            <span 
              class="px-3 py-1 rounded-full text-sm"
              :class="getStatusClass(project.status)"
            >
              {{ getStatusText(project.status) }}
            </span>
          </div>
          
          <p class="text-gray-500 mb-4">{{ project.description || '暂无描述' }}</p>
          
          <div class="flex items-center space-x-6 text-sm text-gray-400">
            <span>创建时间：{{ formatDate(project.createdAt) }}</span>
            <span>更新时间：{{ formatDate(project.updatedAt) }}</span>
            <span>{{ project.episodes.length }} 集</span>
          </div>
        </div>
        
        <div class="flex items-center space-x-3">
          <button 
            @click="editProject"
            class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            编辑
          </button>
          
          <button 
            @click="deleteProject"
            class="px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg"
          >
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="grid grid-cols-5 gap-4 mb-6">
      <button 
        @click="openProjectFolder"
        class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow text-left"
      >
        <FolderIcon class="w-8 h-8 text-gray-500 mb-3" />
        <h3 class="font-semibold text-gray-800">项目文件夹</h3>
        <p class="text-sm text-gray-500">打开或复制文件夹路径</p>
      </button>

      <router-link 
        :to="`/projects/${project.id}/script`"
        class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
      >
        <DocumentTextIcon class="w-8 h-8 text-blue-500 mb-3" />
        <h3 class="font-semibold text-gray-800">剧本编辑</h3>
        <p class="text-sm text-gray-500">编写和修改剧本内容</p>
      </router-link>
      
      <router-link 
        :to="`/projects/${project.id}/storyboard`"
        class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
      >
        <FilmIcon class="w-8 h-8 text-purple-500 mb-3" />
        <h3 class="font-semibold text-gray-800">分镜制作</h3>
        <p class="text-sm text-gray-500">创建和编辑分镜脚本</p>
      </router-link>
      
      <router-link 
        :to="{ path: '/assets', query: { projectId: project.id, projectName: project.title } }"
        class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
      >
        <PhotoIcon class="w-8 h-8 text-green-500 mb-3" />
        <h3 class="font-semibold text-gray-800">素材管理</h3>
        <p class="text-sm text-gray-500">管理角色和场景素材</p>
      </router-link>
      
      <router-link 
        to="/tasks"
        class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
      >
        <VideoCameraIcon class="w-8 h-8 text-orange-500 mb-3" />
        <h3 class="font-semibold text-gray-800">视频任务</h3>
        <p class="text-sm text-gray-500">查看生成任务进度</p>
      </router-link>
    </div>

    <!-- 最近剧集 -->
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold">剧集列表</h2>
        
        <router-link 
          :to="`/projects/${project.id}/storyboard`"
          class="text-blue-600 hover:text-blue-700"
        >
          管理全部 →
        </router-link>
      </div>
      
      <div v-if="project.episodes.length === 0" class="text-center py-8 text-gray-500">
        暂无剧集，前往分镜编辑器创建
      </div>
      
      <div v-else class="space-y-3">
        <div 
          v-for="episode in project.episodes.slice(0, 5)" 
          :key="episode.id"
          class="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
        >
          <div class="flex items-center space-x-4">
            <span class="text-2xl font-bold text-gray-300">{{ String(episode.episodeNumber).padStart(2, '0') }}</span>
            
            <div>
              <p class="font-medium">{{ episode.title }}</p>
              <p class="text-sm text-gray-500">创建于 {{ formatDate(episode.createdAt) }}</p>
            </div>
          </div>
          
          <span 
            class="px-3 py-1 rounded-full text-sm"
            :class="getEpisodeStatusClass(episode.status)"
          >
            {{ getEpisodeStatusText(episode.status) }}
          </span>
        </div>
      </div>
    </div>
  </div>

  <!-- 项目不存在 -->
  <div v-else class="text-center py-12">
    <p class="text-gray-500">项目不存在或已被删除</p>
    <router-link to="/projects" class="text-blue-600 hover:underline mt-4 inline-block">
      返回项目列表
    </router-link>
  </div>

  <!-- 复制成功提示 -->
  <Transition
    enter-active-class="transition duration-300 ease-out"
    enter-from-class="transform translate-y-2 opacity-0"
    enter-to-class="transform translate-y-0 opacity-100"
    leave-active-class="transition duration-200 ease-in"
    leave-from-class="transform translate-y-0 opacity-100"
    leave-to-class="transform translate-y-2 opacity-0"
  >
    <div
      v-if="showCopyToast"
      class="fixed bottom-6 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white px-6 py-3 rounded-lg shadow-lg z-50"
    >
      <div class="flex items-center space-x-2">
        <svg class="w-5 h-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
        </svg>
        <span>文件夹路径已复制到剪贴板</span>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProjectStore } from '../stores'
import { projectApi } from '../api'
import { 
  DocumentTextIcon, 
  FilmIcon, 
  PhotoIcon, 
  VideoCameraIcon,
  FolderIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline'
import { format } from 'date-fns'
import { zhCN } from 'date-fns/locale'

const route = useRoute()
const router = useRouter()
const projectStore = useProjectStore()

const projectFolderPath = ref('')
const showCopyToast = ref(false)

// 加载项目文件夹路径
onMounted(async () => {
  if (project.value) {
    try {
      const response = await projectApi.getProjectFolder(project.value.id)
      if (response.data) {
        projectFolderPath.value = response.data.folderPath
      }
    } catch (error) {
      console.error('获取项目文件夹失败:', error)
    }
  }
})

// 打开项目文件夹（复制路径到剪贴板）
const openProjectFolder = async () => {
  if (projectFolderPath.value) {
    try {
      await navigator.clipboard.writeText(projectFolderPath.value)
      showCopyToast.value = true
      setTimeout(() => {
        showCopyToast.value = false
      }, 2000)
    } catch (error) {
      console.error('复制路径失败:', error)
      alert('项目文件夹路径: ' + projectFolderPath.value)
    }
  }
}

const project = computed(() => 
  projectStore.projects.find(p => p.id === route.params.id)
)

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

const getEpisodeStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-100 text-gray-600',
    generating: 'bg-yellow-100 text-yellow-600',
    completed: 'bg-green-100 text-green-600',
    failed: 'bg-red-100 text-red-600',
  }
  return classes[status] || classes.pending
}

const getEpisodeStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待生成',
    generating: '生成中',
    completed: '已完成',
    failed: '失败',
  }
  return texts[status] || status
}

const formatDate = (date: string) => {
  return format(new Date(date), 'yyyy-MM-dd', { locale: zhCN })
}

const editProject = () => {
  // 打开编辑弹窗
}

const deleteProject = () => {
  if (confirm('确定要删除这个项目吗？')) {
    projectStore.removeProject(project.value!.id)
    router.push('/projects')
  }
}
</script>
