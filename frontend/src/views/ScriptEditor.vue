<template>
  <div class="h-[calc(100vh-100px)]">
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-4">
        <button 
          @click="$router.back()"
          class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          <ArrowLeftIcon class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-2xl font-bold text-gray-800">剧本编辑器</h1>
          <p v-if="project" class="text-gray-500">{{ project.title }}</p>
        </div>
      </div>
      
      <div class="flex space-x-3">
        <button 
          @click="saveScript"
          class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          :disabled="!hasChanges"
        >
          保存草稿
        </button>
        <button 
          @click="generateStoryboard"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          生成分镜
        </button>
      </div>
    </div>

    <!-- 编辑器主体 -->
    <div class="grid grid-cols-2 gap-6 h-[calc(100%-80px)]">
      <!-- 左侧：编辑区 -->
      <div class="bg-white rounded-xl shadow-sm flex flex-col">
        <div class="p-4 border-b">
          <div class="flex items-center space-x-2">
            <button 
              v-for="template in templates" 
              :key="template.name"
              @click="insertTemplate(template.content)"
              class="px-3 py-1 text-sm bg-gray-100 hover:bg-gray-200 rounded"
            >
              {{ template.name }}
            </button>
          </div>
        </div>
        
        <textarea 
          v-model="scriptContent"
          class="flex-1 w-full p-6 resize-none focus:outline-none font-mono text-gray-700 leading-relaxed"
          placeholder="在此输入剧本内容...

建议格式：
## 第一幕：起
场景描述...

## 第二幕：承
场景描述...

## 第三幕：转
场景描述...

## 第四幕：合
场景描述..."
        />
      </div>

      <!-- 右侧：预览区 -->
      <div class="bg-white rounded-xl shadow-sm flex flex-col">
        <div class="p-4 border-b">
          <h3 class="font-semibold text-gray-700">实时预览</h3>
        </div>
        
        <div class="flex-1 p-6 overflow-auto">
          <div v-html="renderedContent" class="prose max-w-none"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectStore } from '../stores'
import { ArrowLeftIcon } from '@heroicons/vue/24/outline'
import { marked } from 'marked'

const route = useRoute()
const projectStore = useProjectStore()

const project = computed(() => 
  projectStore.projects.find(p => p.id === route.params.id)
)

const scriptContent = ref(project.value?.scriptContent || '')
const originalContent = ref(project.value?.scriptContent || '')
const hasChanges = computed(() => scriptContent.value !== originalContent.value)

const templates = [
  { name: '四幕结构', content: '## 第一幕：起\n\n## 第二幕：承\n\n## 第三幕：转\n\n## 第四幕：合\n' },
  { name: '场景模板', content: '### 场景 [序号]\n\n**地点：**\n**时间：**\n**人物：**\n\n**剧情：**\n' },
  { name: '人物介绍', content: '**姓名：**\n**年龄：**\n**性格：**\n**外貌：**\n**背景：**\n' },
]

const renderedContent = computed(() => {
  return marked(scriptContent.value)
})

const insertTemplate = (content: string) => {
  scriptContent.value += '\n' + content + '\n'
}

const saveScript = () => {
  if (project.value) {
    projectStore.updateProject(project.value.id, {
      scriptContent: scriptContent.value,
      updatedAt: new Date().toISOString(),
    })
    originalContent.value = scriptContent.value
    alert('保存成功！')
  }
}

const generateStoryboard = () => {
  if (!scriptContent.value.trim()) {
    alert('请先编写剧本内容')
    return
  }
  // 跳转到分镜编辑器
  // $router.push(`/projects/${route.params.id}/storyboard`)
}

// 监听项目变化
watch(() => project.value, (newProject) => {
  if (newProject) {
    scriptContent.value = newProject.scriptContent || ''
    originalContent.value = newProject.scriptContent || ''
  }
})
</script>

<style scoped>
.prose :deep(h1) { @apply text-3xl font-bold mb-4; }
.prose :deep(h2) { @apply text-2xl font-bold mb-3 mt-6; }
.prose :deep(h3) { @apply text-xl font-bold mb-2 mt-4; }
.prose :deep(p) { @apply mb-4 leading-relaxed; }
.prose :deep(ul) { @apply list-disc ml-6 mb-4; }
.prose :deep(ol) { @apply list-decimal ml-6 mb-4; }
</style>
