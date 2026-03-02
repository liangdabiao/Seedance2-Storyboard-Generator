<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-8">设置</h1>
    
    <div class="max-w-2xl">
      <!-- 火山引擎 API 配置 -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-semibold">火山引擎 API 配置</h2>
          <span 
            v-if="configStatus.isConfigured" 
            class="px-3 py-1 bg-green-100 text-green-600 rounded-full text-sm"
          >
            已配置
          </span>
          <span 
            v-else 
            class="px-3 py-1 bg-yellow-100 text-yellow-600 rounded-full text-sm"
          >
            未配置
          </span>
        </div>
        
        <div class="space-y-4">
          <div>
            <label class="block text-gray-700 mb-2">
              API 基础地址 <span class="text-red-500">*</span>
            </label>
            <input 
              v-model="config.baseUrl"
              type="text"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="https://ark.cn-beijing.volces.com"
            />
            <p class="text-sm text-gray-500 mt-1">火山引擎方舟平台的 API 地址</p>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">
              API Key <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input 
                v-model="config.apiKey"
                :type="showApiKey ? 'text' : 'password'"
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 pr-10"
                placeholder="输入你的 API Key"
              />
              <button 
                @click="showApiKey = !showApiKey"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <EyeIcon v-if="!showApiKey" class="w-5 h-5" />
                <EyeSlashIcon v-else class="w-5 h-5" />
              </button>
            </div>
            <p class="text-sm text-gray-500 mt-1">从火山引擎控制台获取的 API Key</p>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">
              Endpoint ID <span class="text-red-500">*</span>
            </label>
            <input 
              v-model="config.endpoint"
              type="text"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="ep-xxxxxxxxx"
            />
            <p class="text-sm text-gray-500 mt-1">推理接入点 ID，格式：ep-xxxxxxxxxx</p>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex justify-end space-x-3 mt-6">
          <button 
            @click="testConnection"
            class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
            :disabled="testing"
          >
            {{ testing ? '测试中...' : '测试连接' }}
          </button>
          
          <button 
            @click="saveConfig"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
            :disabled="saving || !isFormValid"
          >
            {{ saving ? '保存中...' : '保存配置' }}
          </button>
        </div>
        
        <!-- 状态消息 -->
        <div 
          v-if="message.text" 
          class="mt-4 p-3 rounded-lg"
          :class="message.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'"
        >
          {{ message.text }}
        </div>
      </div>

      <!-- 默认生成设置 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h2 class="text-xl font-semibold mb-6">默认生成设置</h2>
        
        <div class="space-y-4">
          <div>
            <label class="block text-gray-700 mb-2">默认视频时长</label>
            <select v-model="defaultSettings.duration" class="w-full px-4 py-2 border rounded-lg">
              <option :value="5">5 秒</option>
              <option :value="10">10 秒</option>
              <option :value="15">15 秒</option>
            </select>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">默认分辨率</label>
            <select v-model="defaultSettings.resolution" class="w-full px-4 py-2 border rounded-lg">
              <option value="720x1280">720x1280 (9:16 竖屏)</option>
              <option value="1280x720">1280x720 (16:9 横屏)</option>
            </select>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">默认风格前缀</label>
            <textarea 
              v-model="defaultSettings.style"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows="3"
              placeholder="Chinese ink wash painting style mixed with anime cel-shading"
            />
          </div>
        </div>
        
        <div class="flex justify-end mt-6">
          <button 
            @click="saveDefaultSettings"
            class="px-6 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
          >
            保存默认设置
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'

// 配置状态
const config = ref({
  baseUrl: 'https://ark.cn-beijing.volces.com',
  apiKey: '',
  endpoint: '',
})

const configStatus = ref({
  isConfigured: false,
})

const defaultSettings = ref({
  duration: 5,
  resolution: '720x1280',
  style: 'Chinese ink wash painting style mixed with anime cel-shading',
})

const showApiKey = ref(false)
const saving = ref(false)
const testing = ref(false)
const message = ref({ type: '', text: '' })

// 表单验证
const isFormValid = computed(() => {
  return config.value.baseUrl?.trim() && 
         config.value.apiKey?.trim() && 
         config.value.endpoint?.trim()
})

// 加载配置
onMounted(() => {
  loadConfig()
  loadDefaultSettings()
})

const loadConfig = async () => {
  try {
    const response = await fetch('/api/config')
    const data = await response.json()
    
    if (data.success) {
      config.value.baseUrl = data.data.baseUrl || config.value.baseUrl
      config.value.endpoint = data.data.endpoint || ''
      // API Key 是脱敏的，不覆盖输入框
      configStatus.value.isConfigured = data.data.isConfigured
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

const loadDefaultSettings = () => {
  const saved = localStorage.getItem('default_settings')
  if (saved) {
    defaultSettings.value = { ...defaultSettings.value, ...JSON.parse(saved) }
  }
}

// 保存配置
const saveConfig = async () => {
  saving.value = true
  message.value = { type: '', text: '' }
  
  try {
    const response = await fetch('/api/config', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(config.value),
    })
    
    const data = await response.json()
    
    if (data.success) {
      message.value = { type: 'success', text: '配置保存成功！' }
      configStatus.value.isConfigured = true
      // 更新显示的 API Key（脱敏）
      config.value.apiKey = data.data.apiKey
    } else {
      message.value = { type: 'error', text: data.message || '保存失败' }
    }
  } catch (error) {
    message.value = { type: 'error', text: '网络错误，请稍后重试' }
  } finally {
    saving.value = false
  }
}

// 测试连接
const testConnection = async () => {
  testing.value = true
  message.value = { type: '', text: '' }
  
  try {
    const response = await fetch('/api/config/test', {
      method: 'POST',
    })
    
    const data = await response.json()
    
    if (data.success) {
      message.value = { type: 'success', text: '连接测试成功！' }
    } else {
      message.value = { type: 'error', text: data.message || '连接测试失败' }
    }
  } catch (error) {
    message.value = { type: 'error', text: '网络错误，请稍后重试' }
  } finally {
    testing.value = false
  }
}

// 保存默认设置
const saveDefaultSettings = () => {
  localStorage.setItem('default_settings', JSON.stringify(defaultSettings.value))
  alert('默认设置已保存！')
}
</script>
