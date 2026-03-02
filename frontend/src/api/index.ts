import axios from 'axios'
import type { 
  Project, 
  Episode, 
  Asset, 
  VideoTask, 
  VideoGenerationRequest,
  VolcengineConfig 
} from '../types'

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 火山引擎配置 API
export const configApi = {
  /**
   * 获取当前配置
   */
  async getConfig(): Promise<{ success: boolean; data: VolcengineConfig }> {
    const response = await apiClient.get('/config')
    return response.data
  },

  /**
   * 更新配置
   */
  async updateConfig(config: VolcengineConfig): Promise<{ success: boolean; message?: string; data?: VolcengineConfig }> {
    const response = await apiClient.post('/config', config)
    return response.data
  },

  /**
   * 测试连接
   */
  async testConnection(): Promise<{ success: boolean; message?: string }> {
    const response = await apiClient.post('/config/test')
    return response.data
  },
}

// 项目管理 API
export const projectApi = {
  async getProjects(): Promise<Project[]> {
    const response = await apiClient.get('/projects')
    return response.data.data
  },

  async getProject(id: string): Promise<Project> {
    const response = await apiClient.get(`/projects/${id}`)
    return response.data.data
  },

  async createProject(project: { id: string; title: string; description?: string }): Promise<Project> {
    const response = await apiClient.post('/projects', project)
    return response.data.data
  },

  async updateProject(id: string, project: Partial<Project>): Promise<Project> {
    const response = await apiClient.put(`/projects/${id}`, project)
    return response.data.data
  },

  async deleteProject(id: string): Promise<void> {
    const response = await apiClient.delete(`/projects/${id}`)
    if (!response.data.success) {
      throw new Error(response.data.message || '删除失败')
    }
  },
}

// 视频任务 API
export const videoTaskApi = {
  async getTasks(projectId?: string): Promise<VideoTask[]> {
    const params = projectId ? { projectId } : {}
    const response = await apiClient.get('/video-tasks', { params })
    return response.data.data
  },

  async createTask(episodeId: string, request: VideoGenerationRequest): Promise<VideoTask> {
    const response = await apiClient.post('/video-tasks', {
      episodeId,
      ...request,
    })
    return response.data.data
  },

  async getTask(taskId: string): Promise<VideoTask> {
    const response = await apiClient.get(`/video-tasks/${taskId}`)
    return response.data.data
  },

  async cancelTask(taskId: string): Promise<VideoTask> {
    const response = await apiClient.post(`/video-tasks/${taskId}/cancel`)
    return response.data.data
  },

  async deleteTask(taskId: string): Promise<void> {
    await apiClient.delete(`/video-tasks/${taskId}`)
  },
}

// 素材 API
export const assetApi = {
  async getAssets(projectId: string): Promise<Asset[]> {
    const response = await apiClient.get(`/projects/${projectId}/assets`)
    return response.data.data
  },

  async createAsset(asset: Partial<Asset>): Promise<Asset> {
    const response = await apiClient.post('/assets', asset)
    return response.data.data
  },

  async uploadImage(assetId: string, file: File): Promise<Asset> {
    const formData = new FormData()
    formData.append('image', file)
    
    const response = await apiClient.post(`/assets/${assetId}/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return response.data.data
  },
}

export default {
  config: configApi,
  project: projectApi,
  videoTask: videoTaskApi,
  asset: assetApi,
}
