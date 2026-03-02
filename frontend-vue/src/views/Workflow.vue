<template>
  <div>
    <div class="flex items-center justify-between mb-8">
      <h1 class="text-3xl font-bold text-gray-800">可视化工作流</h1>
      
      <div class="flex items-center space-x-3">
        <select 
          v-if="projects.length > 0"
          v-model="selectedProjectId"
          class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">选择项目</option>
          <option v-for="project in projects" :key="project.id" :value="project.id">
            {{ project.title }}
          </option>
        </select>
        
        <button 
          @click="resetWorkflow"
          class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          重置
        </button>
        
        <button 
          @click="runWorkflow"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          :disabled="isRunning"
        >
          {{ isRunning ? '运行中...' : '运行工作流' }}
        </button>
      </div>
    </div>

    <!-- 工作流可视化 -->
    <div class="bg-white rounded-xl shadow-sm p-8">
      <div class="flex flex-col items-center">
        <!-- 工作流步骤 -->
        <div class="w-full max-w-4xl">
          <div 
            v-for="(step, index) in workflowSteps" 
            :key="step.id"
            class="relative"
          >
            <!-- 连接线 -->
            <div 
              v-if="index < workflowSteps.length - 1"
              class="absolute left-8 top-16 w-0.5 h-12 bg-gray-200"
              :class="{ 'bg-blue-500': step.status === 'completed' }"
            ></div>
            
            <!-- 步骤卡片 -->
            <div 
              class="flex items-start space-x-4 p-4 rounded-xl transition-all cursor-pointer"
              :class="getStepCardClass(step.status)"
              @click="selectStep(step)"
            >
              <!-- 状态图标 -->
              <div 
                class="flex-shrink-0 w-16 h-16 rounded-full flex items-center justify-center text-2xl"
                :class="getStepIconClass(step.status)"
              >
                <span v-if="step.status === 'completed'">✓</span>
                <span v-else-if="step.status === 'active'">⚡</span>
                <span v-else-if="step.status === 'error'">✗</span>
                <span v-else>{{ index + 1 }}</span>
              </div>
              
              <!-- 步骤内容 -->
              <div class="flex-1">
                <h3 class="text-lg font-semibold mb-1">{{ step.name }}</h3>
                <p class="text-gray-500 text-sm">{{ step.description }}</p>
                
                <!-- 进度条 -->
                <div v-if="step.status === 'active'" class="mt-3">
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-blue-600 h-2 rounded-full transition-all duration-500"
                      :style="{ width: `${stepProgress[step.id] || 0}%` }"
                    ></div>
                  </div>
                  <p class="text-xs text-gray-500 mt-1">{{ stepProgress[step.id] || 0 }}%</p>
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="flex-shrink-0">
                <button 
                  v-if="step.status === 'error'"
                  @click.stop="retryStep(step)"
                  class="px-3 py-1 text-sm bg-red-100 text-red-600 rounded-lg hover:bg-red-200"
                >
                  重试
                </button>
                
                <button 
                  v-if="step.status === 'completed'"
                  @click.stop="viewResult(step)"
                  class="px-3 py-1 text-sm bg-green-100 text-green-600 rounded-lg hover:bg-green-200"
                >
                  查看结果
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 步骤详情面板 -->
    <div v-if="selectedStep" class="mt-6 bg-white rounded-xl shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-xl font-semibold">{{ selectedStep.name }} - 详情</h3>
        <button @click="selectedStep = null" class="p-2 hover:bg-gray-100 rounded-lg">
          <XMarkIcon class="w-5 h-5" />
        </button>
      </div>
      
      <div class="space-y-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">步骤描述</label>
          <p>{{ selectedStep.description }}</p>
        </div>
        
        <div>
          <label class="block text-sm text-gray-600 mb-1">状态</label>
          <span 
            class="px-3 py-1 rounded-full text-sm"
            :class="getStatusBadgeClass(selectedStep.status)"
          >
            {{ getStatusText(selectedStep.status) }}
          </span>
        </div>
        
        <div v-if="stepOutput[selectedStep.id]">
          <label class="block text-sm text-gray-600 mb-1">输出结果</label>
          <pre class="bg-gray-50 p-4 rounded-lg text-sm overflow-auto">{{ stepOutput[selectedStep.id] }}</pre>
        </div>
      </div>
    </div>

    <!-- 日志面板 -->
    <div class="mt-6 bg-gray-900 rounded-xl p-4">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-white font-semibold">运行日志</h3>
        <button @click="clearLogs" class="text-gray-400 hover:text-white">
          清空
        </button>
      </div>
      
      <div class="h-48 overflow-y-auto font-mono text-sm">
        <div 
          v-for="(log, index) in logs" 
          :key="index"
          class="mb-1"
        >
          <span class="text-gray-500">[{{ log.time }}]</span>
          <span 
            class="ml-2"
            :class="getLogClass(log.type)"
          >
            {{ log.message }}
          </span>
        </div>
        
        <div v-if="logs.length === 0" class="text-gray-500">
          暂无日志...
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useProjectStore } from '../stores'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import type { WorkflowStep } from '../types'

const projectStore = useProjectStore()
const projects = computed(() => projectStore.projects)

const selectedProjectId = ref('')
const isRunning = ref(false)
const selectedStep = ref<WorkflowStep | null>(null)

const stepProgress = reactive<Record<string, number>>({})
const stepOutput = reactive<Record<string, string>>({})

const logs = ref<Array<{ time: string; type: string; message: string }>>([])

const workflowSteps = ref<WorkflowStep[]>([
  {
    id: '1',
    name: '剧本创作',
    description: '使用 AI 创作或编辑剧本内容',
    status: 'pending',
    icon: 'DocumentTextIcon',
    order: 1,
  },
  {
    id: '2',
    name: '素材规划',
    description: '分析剧本并规划需要的角色、场景、道具素材',
    status: 'pending',
    icon: 'PhotoIcon',
    order: 2,
  },
  {
    id: '3',
    name: '图像生成',
    description: '使用 AI 生成角色、场景、道具图片素材',
    status: 'pending',
    icon: 'CameraIcon',
    order: 3,
  },
  {
    id: '4',
    name: '分镜脚本',
    description: '根据剧本和素材生成分镜脚本',
    status: 'pending',
    icon: 'FilmIcon',
    order: 4,
  },
  {
    id: '5',
    name: '视频生成',
    description: '使用 Seedance API 生成视频',
    status: 'pending',
    icon: 'VideoCameraIcon',
    order: 5,
  },
  {
    id: '6',
    name: '视频拼接',
    description: '将多集视频拼接成完整作品',
    status: 'pending',
    icon: 'ScissorsIcon',
    order: 6,
  },
])

const getStepCardClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-50 border border-gray-200',
    active: 'bg-blue-50 border-2 border-blue-500 shadow-md',
    completed: 'bg-green-50 border border-green-200',
    error: 'bg-red-50 border border-red-200',
  }
  return classes[status] || classes.pending
}

const getStepIconClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-200 text-gray-500',
    active: 'bg-blue-500 text-white animate-pulse',
    completed: 'bg-green-500 text-white',
    error: 'bg-red-500 text-white',
  }
  return classes[status] || classes.pending
}

const getStatusBadgeClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-100 text-gray-600',
    active: 'bg-blue-100 text-blue-600',
    completed: 'bg-green-100 text-green-600',
    error: 'bg-red-100 text-red-600',
  }
  return classes[status] || classes.pending
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '等待中',
    active: '进行中',
    completed: '已完成',
    error: '出错',
  }
  return texts[status] || status
}

const getLogClass = (type: string) => {
  const classes: Record<string, string> = {
    info: 'text-blue-400',
    success: 'text-green-400',
    warning: 'text-yellow-400',
    error: 'text-red-400',
  }
  return classes[type] || 'text-gray-300'
}

const addLog = (message: string, type = 'info') => {
  const time = new Date().toLocaleTimeString()
  logs.value.push({ time, type, message })
}

const selectStep = (step: WorkflowStep) => {
  selectedStep.value = step
}

const runWorkflow = async () => {
  if (!selectedProjectId.value) {
    alert('请先选择一个项目')
    return
  }
  
  isRunning.value = true
  addLog('开始运行工作流...', 'info')
  
  // 重置所有步骤
  workflowSteps.value.forEach(step => {
    step.status = 'pending'
    stepProgress[step.id] = 0
  })
  
  // 顺序执行每个步骤
  for (const step of workflowSteps.value) {
    step.status = 'active'
    addLog(`开始执行：${step.name}`, 'info')
    
    try {
      // 模拟步骤执行
      await runStep(step)
      
      step.status = 'completed'
      stepProgress[step.id] = 100
      addLog(`完成：${step.name}`, 'success')
    } catch (error) {
      step.status = 'error'
      addLog(`错误：${step.name} - ${error}`, 'error')
      break
    }
  }
  
  isRunning.value = false
  addLog('工作流运行结束', 'info')
}

const runStep = (step: WorkflowStep): Promise<void> => {
  return new Promise((resolve, reject) => {
    let progress = 0
    const interval = setInterval(() => {
      progress += 10
      stepProgress[step.id] = progress
      
      if (progress >= 100) {
        clearInterval(interval)
        
        // 模拟随机错误（10% 概率）
        if (Math.random() < 0.1) {
          reject('执行失败')
        } else {
          stepOutput[step.id] = `步骤 ${step.name} 的输出结果...`
          resolve()
        }
      }
    }, 200)
  })
}

const retryStep = (step: WorkflowStep) => {
  step.status = 'active'
  runStep(step).then(() => {
    step.status = 'completed'
    addLog(`重试成功：${step.name}`, 'success')
  }).catch(() => {
    step.status = 'error'
    addLog(`重试失败：${step.name}`, 'error')
  })
}

const viewResult = (step: WorkflowStep) => {
  alert(`查看 ${step.name} 的结果`)
}

const resetWorkflow = () => {
  workflowSteps.value.forEach(step => {
    step.status = 'pending'
    stepProgress[step.id] = 0
  })
  logs.value = []
  stepOutput.value = {}
}

const clearLogs = () => {
  logs.value = []
}
</script>
