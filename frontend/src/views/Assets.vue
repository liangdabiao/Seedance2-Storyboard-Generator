<template>
  <div>
    <div class="flex items-center justify-between mb-8">
      <h1 class="text-3xl font-bold text-gray-800">素材管理</h1>
      
      <button 
        @click="showUploadModal = true"
        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        + 上传素材
      </button>
    </div>

    <!-- 素材分类标签 -->
    <div class="flex space-x-2 mb-6">
      <button 
        v-for="tab in tabs" 
        :key="tab.value"
        @click="activeTab = tab.value"
        class="px-4 py-2 rounded-lg transition-colors"
        :class="activeTab === tab.value ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50'"
      >
        {{ tab.label }}
        <span class="ml-2 text-xs opacity-75">({{ getAssetCount(tab.value) }})</span>
      </button>
    </div>

    <!-- 素材网格 -->
    <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
      <div 
        v-for="asset in filteredAssets" 
        :key="asset.id"
        class="group relative bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow"
      >
        <!-- 图片预览 -->
        <div class="aspect-square bg-gray-100 relative">
          <img 
            v-if="asset.imageUrl"
            :src="asset.imageUrl" 
            class="w-full h-full object-cover"
            @click="previewAsset(asset)"
          />
          <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
            <PhotoIcon class="w-12 h-12" />
          </div>
          
          <!-- 悬停操作 -->
          <div class="absolute inset-0 bg-black bg-opacity-50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center space-x-2">
            <button 
              @click="previewAsset(asset)"
              class="p-2 bg-white rounded-full text-gray-700 hover:text-blue-600"
            >
              <EyeIcon class="w-5 h-5" />
            </button>
            
            <button 
              @click="deleteAsset(asset.id)"
              class="p-2 bg-white rounded-full text-gray-700 hover:text-red-600"
            >
              <TrashIcon class="w-5 h-5" />
            </button>
          </div>
        </div>
        
        <!-- 素材信息 -->
        <div class="p-3">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs font-mono text-gray-500">{{ asset.code }}</span>
            <span 
              class="text-xs px-2 py-0.5 rounded-full"
              :class="getTypeClass(asset.type)"
            >
              {{ getTypeLabel(asset.type) }}
            </span>
          </div>
          
          <p class="font-medium text-gray-800 text-sm truncate">{{ asset.name }}</p>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredAssets.length === 0" class="text-center py-12">
      <PhotoIcon class="w-16 h-16 mx-auto text-gray-300 mb-4" />
      <p class="text-gray-500">暂无素材，点击右上角上传</p>
    </div>

    <!-- 上传弹窗 -->
    <div v-if="showUploadModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold mb-6">上传素材</h2>
        
        <form @submit.prevent="uploadAsset">
          <!-- 素材类型 -->
          <div class="mb-4">
            <label class="block text-gray-700 mb-2">素材类型</label>
            <div class="flex space-x-3">
              <label 
                v-for="type in assetTypes" 
                :key="type.value"
                class="flex items-center px-4 py-2 border rounded-lg cursor-pointer hover:bg-gray-50"
                :class="newAsset.type === type.value ? 'border-blue-500 bg-blue-50' : ''"
              >
                <input 
                  v-model="newAsset.type"
                  type="radio"
                  :value="type.value"
                  class="mr-2"
                />
                {{ type.label }}
              </label>
            </div>
          </div>

          <!-- 素材编号 -->
          <div class="mb-4">
            <label class="block text-gray-700 mb-2">素材编号</label>
            <div class="flex items-center space-x-2">
              <span class="text-gray-500">{{ getTypePrefix(newAsset.type) }}</span>
              <input 
                v-model="newAsset.codeNumber"
                type="text"
                class="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="01"
                required
              />
            </div>
          </div>

          <!-- 素材名称 -->
          <div class="mb-4">
            <label class="block text-gray-700 mb-2">素材名称</label>
            <input 
              v-model="newAsset.name"
              type="text"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="例如：林冲正面全身"
              required
            />
          </div>

          <!-- 生成提示词 -->
          <div class="mb-4">
            <label class="block text-gray-700 mb-2">生成提示词（Prompt）</label>
            <textarea 
              v-model="newAsset.prompt"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows="4"
              placeholder="输入用于生成此素材的英文提示词..."
              required
            />
          </div>

          <!-- 图片上传 -->
          <div class="mb-6">
            <label class="block text-gray-700 mb-2">上传图片</label>
            <div 
              class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-400 transition-colors cursor-pointer"
              @click="$refs.fileInput.click()"
              @drop.prevent="handleDrop"
              @dragover.prevent
            >
              <input 
                ref="fileInput"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleFileSelect"
              />
              
              <div v-if="!selectedFile">
                <CloudArrowUpIcon class="w-12 h-12 mx-auto text-gray-400 mb-2" />
                <p class="text-gray-500">点击或拖拽图片到此处</p>
                <p class="text-sm text-gray-400 mt-1">支持 JPG、PNG 格式，最大 10MB</p>
              </div>
              
              <div v-else class="flex items-center justify-center">
                <img :src="previewUrl" class="max-h-48 rounded-lg" />
              </div>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="showUploadModal = false"
              class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
            >
              取消
            </button>
            
            <button 
              type="submit"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              :disabled="uploading"
            >
              {{ uploading ? '上传中...' : '确认上传' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 预览弹窗 -->
    <div v-if="previewAssetData" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl max-w-4xl max-h-[90vh] overflow-auto">
        <div class="p-4 border-b flex items-center justify-between">
          <h3 class="text-xl font-semibold">{{ previewAssetData.name }}</h3>
          <button @click="previewAssetData = null" class="p-2 hover:bg-gray-100 rounded-lg">
            <XMarkIcon class="w-6 h-6" />
          </button>
        </div>
        
        <div class="p-6">
          <img 
            :src="previewAssetData.imageUrl" 
            class="max-w-full max-h-[60vh] mx-auto rounded-lg mb-4"
          />
          
          <div class="space-y-2 text-sm">
            <p><span class="text-gray-500">编号：</span>{{ previewAssetData.code }}</p>
            <p><span class="text-gray-500">类型：</span>{{ getTypeLabel(previewAssetData.type) }}</p>
            <p><span class="text-gray-500">提示词：</span></p>
            <pre class="bg-gray-100 p-3 rounded-lg whitespace-pre-wrap">{{ previewAssetData.prompt }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAssetStore } from '../stores'
import { 
  PhotoIcon, 
  EyeIcon, 
  TrashIcon, 
  CloudArrowUpIcon,
  XMarkIcon 
} from '@heroicons/vue/24/outline'
import type { Asset } from '../types'

const assetStore = useAssetStore()
const assets = computed(() => assetStore.assets)

const activeTab = ref('all')
const showUploadModal = ref(false)
const uploading = ref(false)
const selectedFile = ref<File | null>(null)
const previewUrl = ref('')
const previewAssetData = ref<Asset | null>(null)

const tabs = [
  { label: '全部', value: 'all' },
  { label: '角色', value: 'character' },
  { label: '场景', value: 'scene' },
  { label: '道具', value: 'prop' },
]

const assetTypes = [
  { label: '角色', value: 'character' },
  { label: '场景', value: 'scene' },
  { label: '道具', value: 'prop' },
]

const newAsset = ref({
  type: 'character' as const,
  codeNumber: '',
  name: '',
  prompt: '',
})

const filteredAssets = computed(() => {
  if (activeTab.value === 'all') return assets.value
  return assets.value.filter(a => a.type === activeTab.value)
})

const getAssetCount = (type: string) => {
  if (type === 'all') return assets.value.length
  return assets.value.filter(a => a.type === type).length
}

const getTypePrefix = (type: string) => {
  const prefixes: Record<string, string> = {
    character: 'C',
    scene: 'S',
    prop: 'P',
  }
  return prefixes[type] || 'X'
}

const getTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    character: '角色',
    scene: '场景',
    prop: '道具',
  }
  return labels[type] || type
}

const getTypeClass = (type: string) => {
  const classes: Record<string, string> = {
    character: 'bg-purple-100 text-purple-600',
    scene: 'bg-green-100 text-green-600',
    prop: 'bg-orange-100 text-orange-600',
  }
  return classes[type] || 'bg-gray-100'
}

const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files?.length) {
    selectedFile.value = input.files[0]
    previewUrl.value = URL.createObjectURL(input.files[0])
  }
}

const handleDrop = (event: DragEvent) => {
  const files = event.dataTransfer?.files
  if (files?.length) {
    selectedFile.value = files[0]
    previewUrl.value = URL.createObjectURL(files[0])
  }
}

const uploadAsset = async () => {
  uploading.value = true
  
  const asset: Asset = {
    id: Date.now().toString(),
    projectId: '',
    name: newAsset.value.name,
    type: newAsset.value.type,
    code: `${getTypePrefix(newAsset.value.type)}${newAsset.value.codeNumber.padStart(2, '0')}`,
    prompt: newAsset.value.prompt,
    imageUrl: previewUrl.value,
    createdAt: new Date().toISOString(),
  }
  
  assetStore.addAsset(asset)
  
  uploading.value = false
  showUploadModal.value = false
  newAsset.value = { type: 'character', codeNumber: '', name: '', prompt: '' }
  selectedFile.value = null
  previewUrl.value = ''
}

const previewAsset = (asset: Asset) => {
  previewAssetData.value = asset
}

const deleteAsset = (id: string) => {
  if (confirm('确定要删除这个素材吗？')) {
    // assetStore.removeAsset(id)
  }
}
</script>
