<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-4">
        <button 
          @click="$router.back()"
          class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          <ArrowLeftIcon class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-2xl font-bold text-gray-800">分镜编辑器</h1>
          <p v-if="project" class="text-gray-500">{{ project.title }}</p>
        </div>
      </div>
      
      <div class="flex space-x-3">
        <button 
          @click="addEpisode"
          class="px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg"
        >
          + 添加剧集
        </button>
        
        <button 
          @click="generateAllVideos"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          批量生成视频
        </button>
      </div>
    </div>

    <!-- 分镜列表 -->
    <div class="space-y-6">
      <div 
        v-for="(episode, index) in episodes" 
        :key="episode.id"
        class="bg-white rounded-xl shadow-sm p-6"
      >
        <!-- 剧集头部 -->
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center space-x-4">
            <span class="text-lg font-bold text-gray-400">{{ String(index + 1).padStart(2, '0') }}</span>
            <input 
              v-model="episode.title"
              type="text"
              class="text-lg font-semibold border-b border-transparent hover:border-gray-300 focus:border-blue-500 focus:outline-none px-2 py-1"
              placeholder="剧集标题"
            />
          </div>
          
          <div class="flex items-center space-x-2">
            <span 
              class="px-3 py-1 rounded-full text-sm"
              :class="getStatusClass(episode.status)"
            >
              {{ getStatusText(episode.status) }}
            </span>
            
            <button 
              @click="deleteEpisode(episode.id)"
              class="p-2 text-gray-400 hover:text-red-500"
            >
              <TrashIcon class="w-4 h-4" />
            </button>
          </div>
        </div>

        <!-- 分镜内容 -->
        <div class="grid grid-cols-12 gap-4">
          <!-- 时间轴 -->
          <div class="col-span-8">
            <div class="mb-4">
              <label class="block text-sm text-gray-600 mb-2">分镜脚本（时间轴格式）</label>
              <textarea 
                v-model="episode.storyboardContent"
                class="w-full h-48 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                placeholder="0-3秒：场景建立...
3-6秒：主体引入...
6-9秒：情节发展...
9-12秒：高潮时刻...
12-15秒：结尾..."
              />
            </div>
            
            <div>
              <label class="block text-sm text-gray-600 mb-2">完整提示词</label>
              <textarea 
                v-model="episode.scriptContent"
                class="w-full h-32 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="输入 Seedance 提示词..."
              />
            </div>
          </div>

          <!-- 参考素材 -->
          <div class="col-span-4">
            <div class="mb-4">
              <label class="block text-sm text-gray-600 mb-2">参考图片（最多9张）</label>
              <div class="grid grid-cols-3 gap-2">
                <div 
                  v-for="n in 9" 
                  :key="n"
                  class="aspect-square border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center cursor-pointer hover:border-blue-400 hover:bg-blue-50"
                  @click="selectAsset(episode, n - 1)"
                >
                  <PlusIcon v-if="!episode.assets?.[n - 1]" class="w-6 h-6 text-gray-400" />
                  <img 
                    v-else 
                    :src="episode.assets[n - 1]" 
                    class="w-full h-full object-cover rounded-lg"
                  />
                </div>
              </div>
            </div>
            
            <div>
              <label class="block text-sm text-gray-600 mb-2">参数设置</label>
              <div class="space-y-3">
                <div class="flex items-center space-x-2">
                  <span class="text-sm text-gray-500 w-16">时长：</span>
                  <select class="flex-1 px-3 py-2 border rounded-lg text-sm">
                    <option value="5">5秒</option>
                    <option value="10">10秒</option>
                  </select>
                </div>
                
                <div class="flex items-center space-x-2">
                  <span class="text-sm text-gray-500 w-16">比例：</span>
                  <select class="flex-1 px-3 py-2 border rounded-lg text-sm">
                    <option value="9:16">9:16 竖屏</option>
                    <option value="16:9">16:9 横屏</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="mt-6 flex justify-end space-x-3">
          <button 
            @click="saveEpisode(episode)"
            class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            保存
          </button>
          
          <button 
            @click="generateVideo(episode)"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            :disabled="episode.status === 'generating'"
          >
            {{ episode.status === 'generating' ? '生成中...' : '生成视频' }}
          </button>
        </div>

        <!-- 生成结果 -->
        <div v-if="episode.videoUrl" class="mt-6 p-4 bg-gray-50 rounded-lg">
          <p class="text-sm text-gray-600 mb-2">生成结果：</p>
          <video :src="episode.videoUrl" controls class="w-full max-w-md rounded-lg"></video>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="episodes.length === 0" class="text-center py-12">
      <div class="text-gray-400 mb-4">
        <FilmIcon class="w-16 h-16 mx-auto" />
      </div>
      <p class="text-gray-500 mb-4">还没有分镜，开始创建第一集吧</p>
      <button 
        @click="addEpisode"
        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        添加剧集
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectStore } from '../stores'
import { 
  ArrowLeftIcon, 
  TrashIcon, 
  PlusIcon,
  FilmIcon 
} from '@heroicons/vue/24/outline'
import type { Episode } from '../types'

const route = useRoute()
const projectStore = useProjectStore()

const project = computed(() => 
  projectStore.projects.find(p => p.id === route.params.id)
)

const episodes = ref<Episode[]>(project.value?.episodes || [])

const addEpisode = () => {
  const newEpisode: Episode = {
    id: Date.now().toString(),
    projectId: route.params.id as string,
    episodeNumber: episodes.value.length + 1,
    title: `第${episodes.value.length + 1}集`,
    scriptContent: '',
    storyboardContent: '',
    status: 'pending',
    createdAt: new Date().toISOString(),
  }
  episodes.value.push(newEpisode)
}

const deleteEpisode = (id: string) => {
  if (confirm('确定要删除这集吗？')) {
    episodes.value = episodes.value.filter(e => e.id !== id)
  }
}

const saveEpisode = (episode: Episode) => {
  // 保存到 store
  if (project.value) {
    const updatedEpisodes = episodes.value.map(e => 
      e.id === episode.id ? episode : e
    )
    projectStore.updateProject(project.value.id, {
      episodes: updatedEpisodes,
    })
  }
  alert('保存成功！')
}

const generateVideo = (episode: Episode) => {
  episode.status = 'generating'
  // 调用 API 生成视频
  console.log('生成视频:', episode)
}

const generateAllVideos = () => {
  // 批量生成
  episodes.value.forEach(episode => {
    if (episode.status === 'pending') {
      generateVideo(episode)
    }
  })
}

const selectAsset = (episode: Episode, index: number) => {
  // 打开素材选择器
  console.log('选择素材:', episode.id, index)
}

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    pending: 'bg-gray-100 text-gray-600',
    generating: 'bg-yellow-100 text-yellow-600',
    completed: 'bg-green-100 text-green-600',
    failed: 'bg-red-100 text-red-600',
  }
  return classes[status] || classes.pending
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待生成',
    generating: '生成中',
    completed: '已完成',
    failed: '失败',
  }
  return texts[status] || status
}
</script>
