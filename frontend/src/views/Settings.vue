<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-8">设置</h1>
    
    <div class="max-w-2xl">
      <!-- API 配置 -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 class="text-xl font-semibold mb-6">API 配置</h2>
        
        <div class="space-y-4">
          <div>
            <label class="block text-gray-700 mb-2">API 基础地址</label>
            <input 
              v-model="settings.apiUrl"
              type="text"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="http://localhost:8080"
            />
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">API Key</label>
            <input 
              v-model="settings.apiKey"
              type="password"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="输入你的 API Key"
            />
          </div>
        </div>
      </div>

      <!-- 生成设置 -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 class="text-xl font-semibold mb-6">默认生成设置</h2>
        
        <div class="space-y-4">
          <div>
            <label class="block text-gray-700 mb-2">默认视频时长</label>
            <select v-model="settings.defaultDuration" class="w-full px-4 py-2 border rounded-lg">
              <option :value="5">5 秒</option>
              <option :value="10">10 秒</option>
              <option :value="15">15 秒</option>
            </select>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">默认分辨率</label>
            <select v-model="settings.defaultResolution" class="w-full px-4 py-2 border rounded-lg">
              <option value="720x1280">720x1280 (9:16 竖屏)</option>
              <option value="1280x720">1280x720 (16:9 横屏)</option>
            </select>
          </div>
          
          <div>
            <label class="block text-gray-700 mb-2">默认风格前缀</label>
            <textarea 
              v-model="settings.defaultStyle"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows="3"
              placeholder="Chinese ink wash painting style mixed with anime cel-shading"
            />
          </div>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="flex justify-end">
        <button 
          @click="saveSettings"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          保存设置
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const settings = ref({
  apiUrl: 'http://localhost:8080',
  apiKey: '',
  defaultDuration: 5,
  defaultResolution: '720x1280',
  defaultStyle: 'Chinese ink wash painting style mixed with anime cel-shading',
})

onMounted(() => {
  // 从 localStorage 加载设置
  const saved = localStorage.getItem('app_settings')
  if (saved) {
    settings.value = { ...settings.value, ...JSON.parse(saved) }
  }
})

const saveSettings = () => {
  localStorage.setItem('app_settings', JSON.stringify(settings.value))
  alert('设置已保存！')
}
</script>
