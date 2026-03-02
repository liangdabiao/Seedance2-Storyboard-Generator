<template>
  <div>
    <div class="flex items-center justify-between mb-8">
      <h1 class="text-3xl font-bold text-gray-800">项目管理</h1>
      <button 
        @click="showCreateModal = true"
        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        + 新建项目
      </button>
    </div>

    <!-- 项目列表 -->
    <div class="bg-white rounded-xl shadow-sm">
      <div class="divide-y">
        <div 
          v-for="project in projects" 
          :key="project.id"
          class="p-6 hover:bg-gray-50 transition-colors cursor-pointer"
          @click="$router.push(`/projects/${project.id}`)"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <h3 class="text-xl font-semibold text-gray-800 mb-2">{{ project.title }}</h3>
              <p class="text-gray-500 mb-4">{{ project.description || '暂无描述' }}</p>
              
              <div class="flex items-center space-x-4">
                <span 
                  class="px-3 py-1 rounded-full text-sm"
                  :class="getStatusClass(project.status)"
                >
                  {{ getStatusText(project.status) }}
                </span>
                <span class="text-gray-400 text-sm">
                  {{ project.episodes.length }} 集
                </span>
                <span class="text-gray-400 text-sm">
                  更新于 {{ formatDate(project.updatedAt) }}
                </span>
              </div>
            </div>
            
            <button 
              @click.stop="deleteProject(project.id)"
              class="p-2 text-gray-400 hover:text-red-500 transition-colors"
            >
              <TrashIcon class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="projects.length === 0" class="text-center py-12 text-gray-500">
        暂无项目，点击右上角按钮创建新项目
      </div>
    </div>

    <!-- 创建项目弹窗 -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <h2 class="text-2xl font-bold mb-6">新建项目</h2>
        
        <form @submit.prevent="createProject">
          <div class="mb-4">
            <label class="block text-gray-700 mb-2">项目名称</label>
            <input 
              v-model="newProject.title"
              type="text"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入项目名称"
              required
            />
          </div>
          
          <div class="mb-6">
            <label class="block text-gray-700 mb-2">项目描述</label>
            <textarea 
              v-model="newProject.description"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows="3"
              placeholder="输入项目描述（可选）"
            />
          </div>
          
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="showCreateModal = false"
              class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
            >
              取消
            </button>
            <button 
              type="submit"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              创建
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useProjectStore } from '../stores'
import { projectApi } from '../api'
import { TrashIcon } from '@heroicons/vue/24/outline'
import { format } from 'date-fns'
import { zhCN } from 'date-fns/locale'
import type { Project } from '../types'

const projectStore = useProjectStore()
const projects = computed(() => projectStore.projects)
const loading = ref(false)

// 加载项目列表
onMounted(async () => {
  await loadProjects()
})

const loadProjects = async () => {
  loading.value = true
  try {
    const data = await projectApi.getProjects()
    projectStore.setProjects(data)
  } catch (error) {
    console.error('加载项目失败:', error)
    alert('加载项目失败，请检查后端服务是否运行')
  } finally {
    loading.value = false
  }
}

const showCreateModal = ref(false)
const newProject = ref({
  title: '',
  description: '',
})

const createProject = async () => {
  // 使用标题作为文件夹名（去除特殊字符）
  const projectId = newProject.value.title
    .replace(/[^\w\s\u4e00-\u9fa5]/g, '')
    .replace(/\s+/g, '_')
    .substring(0, 50)

  if (!projectId) {
    alert('项目名称无效')
    return
  }

  loading.value = true
  try {
    const project = await projectApi.createProject({
      id: projectId,
      title: newProject.value.title,
      description: newProject.value.description,
    })
    projectStore.addProject(project)
    showCreateModal.value = false
    newProject.value = { title: '', description: '' }
    alert('项目创建成功！')
  } catch (error) {
    console.error('创建项目失败:', error)
    alert('创建项目失败: ' + (error as Error).message)
  } finally {
    loading.value = false
  }
}

const deleteProject = async (id: string) => {
  if (confirm('确定要删除这个项目吗？这将删除项目文件夹及其所有内容！')) {
    loading.value = true
    try {
      await projectApi.deleteProject(id)
      await loadProjects() // 重新加载列表
      alert('项目删除成功')
    } catch (error) {
      console.error('删除项目失败:', error)
      alert('删除项目失败: ' + (error as Error).message)
    } finally {
      loading.value = false
    }
  }
}

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
  return format(new Date(date), 'yyyy-MM-dd HH:mm', { locale: zhCN })
}
</script>
