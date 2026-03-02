<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 侧边栏 -->
    <aside 
      class="fixed left-0 top-0 h-full w-64 bg-white shadow-lg transition-transform duration-300"
      :class="{ '-translate-x-full': !sidebarOpen }"
    >
      <div class="p-6">
        <h1 class="text-2xl font-bold text-gray-800 mb-8">AI 视频制作</h1>
        
        <nav class="space-y-2">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="flex items-center px-4 py-3 rounded-lg transition-colors"
            :class="$route.path === item.path ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:bg-gray-50'"
          >
            <component :is="item.icon" class="w-5 h-5 mr-3" />
            {{ item.name }}
          </router-link>
        </nav>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="transition-all duration-300" :class="sidebarOpen ? 'ml-64' : 'ml-0'">
      <!-- 顶部栏 -->
      <header class="bg-white shadow-sm px-6 py-4 flex items-center justify-between">
        <button
          @click="sidebarOpen = !sidebarOpen"
          class="p-2 rounded-lg hover:bg-gray-100"
        >
          <Bars3Icon class="w-6 h-6 text-gray-600" />
        </button>
        
        <div class="flex items-center space-x-4">
          <span class="text-gray-600">{{ userName }}</span>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="p-6">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { 
  HomeIcon, 
  FolderIcon, 
  PhotoIcon, 
  VideoCameraIcon,
  ArrowPathIcon,
  Bars3Icon,
  Cog6ToothIcon 
} from '@heroicons/vue/24/outline'

const sidebarOpen = ref(true)
const userName = ref('用户')

const menuItems = [
  { name: '工作台', path: '/', icon: HomeIcon },
  { name: '项目管理', path: '/projects', icon: FolderIcon },
  { name: '素材管理', path: '/assets', icon: PhotoIcon },
  { name: '视频任务', path: '/tasks', icon: VideoCameraIcon },
  { name: '工作流', path: '/workflow', icon: ArrowPathIcon },
  { name: '设置', path: '/settings', icon: Cog6ToothIcon },
]
</script>
