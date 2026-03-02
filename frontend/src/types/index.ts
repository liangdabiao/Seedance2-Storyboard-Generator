// 火山引擎配置
export interface VolcengineConfig {
  baseUrl: string
  apiKey: string
  endpoint: string
  isConfigured?: boolean
}

// 项目类型
export interface Project {
  id: string
  title: string
  description: string
  status: 'draft' | 'in_progress' | 'completed'
  createdAt: string
  updatedAt: string
  scriptContent?: string
  episodes: Episode[]
}

// 剧集
export interface Episode {
  id: string
  projectId: string
  episodeNumber: number
  title: string
  scriptContent: string
  storyboardContent?: string
  status: 'pending' | 'generating' | 'completed' | 'failed'
  videoUrl?: string
  createdAt: string
}

// 素材类型
export interface Asset {
  id: string
  projectId: string
  name: string
  type: 'character' | 'scene' | 'prop'
  code: string
  prompt: string
  imageUrl?: string
  createdAt: string
}

// 视频生成任务
export interface VideoTask {
  id: string
  episodeId: string
  projectId: string
  prompt: string
  status: 'pending' | 'processing' | 'succeeded' | 'failed' | 'cancelled'
  videoUrl?: string
  coverUrl?: string
  duration?: number
  progress?: number
  createdAt: string
  updatedAt: string
}

// 工作流步骤
export interface WorkflowStep {
  id: string
  name: string
  description: string
  status: 'pending' | 'active' | 'completed' | 'error'
  icon: string
  order: number
}
