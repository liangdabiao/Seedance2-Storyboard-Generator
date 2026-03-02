import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Project, Episode, Asset, VideoTask, WorkflowStep } from '../types'

export const useProjectStore = defineStore('project', () => {
  // State
  const projects = ref<Project[]>([])
  const currentProject = ref<Project | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const projectCount = computed(() => projects.value.length)
  const activeProjects = computed(() => 
    projects.value.filter(p => p.status === 'in_progress')
  )

  // Actions
  const setProjects = (data: Project[]) => {
    projects.value = data
  }

  const setCurrentProject = (project: Project | null) => {
    currentProject.value = project
  }

  const addProject = (project: Project) => {
    projects.value.push(project)
  }

  const updateProject = (id: string, updates: Partial<Project>) => {
    const index = projects.value.findIndex(p => p.id === id)
    if (index !== -1) {
      projects.value[index] = { ...projects.value[index], ...updates }
    }
  }

  return {
    projects,
    currentProject,
    loading,
    error,
    projectCount,
    activeProjects,
    setProjects,
    setCurrentProject,
    addProject,
    updateProject,
  }
})

export const useAssetStore = defineStore('asset', () => {
  const assets = ref<Asset[]>([])
  const loading = ref(false)

  const characterAssets = computed(() => 
    assets.value.filter(a => a.type === 'character')
  )
  const sceneAssets = computed(() => 
    assets.value.filter(a => a.type === 'scene')
  )
  const propAssets = computed(() => 
    assets.value.filter(a => a.type === 'prop')
  )

  const setAssets = (data: Asset[]) => {
    assets.value = data
  }

  const addAsset = (asset: Asset) => {
    assets.value.push(asset)
  }

  return {
    assets,
    loading,
    characterAssets,
    sceneAssets,
    propAssets,
    setAssets,
    addAsset,
  }
})

export const useTaskStore = defineStore('task', () => {
  const tasks = ref<VideoTask[]>([])
  const loading = ref(false)

  const pendingTasks = computed(() => 
    tasks.value.filter(t => t.status === 'pending' || t.status === 'processing')
  )
  const completedTasks = computed(() => 
    tasks.value.filter(t => t.status === 'succeeded')
  )

  const setTasks = (data: VideoTask[]) => {
    tasks.value = data
  }

  const updateTask = (id: string, updates: Partial<VideoTask>) => {
    const index = tasks.value.findIndex(t => t.id === id)
    if (index !== -1) {
      tasks.value[index] = { ...tasks.value[index], ...updates }
    }
  }

  return {
    tasks,
    loading,
    pendingTasks,
    completedTasks,
    setTasks,
    updateTask,
  }
})
