import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Projects from '../views/Projects.vue'
import ProjectDetail from '../views/ProjectDetail.vue'
import ScriptEditor from '../views/ScriptEditor.vue'
import StoryboardEditor from '../views/StoryboardEditor.vue'
import Assets from '../views/Assets.vue'
import VideoTasks from '../views/VideoTasks.vue'
import Workflow from '../views/Workflow.vue'
import Settings from '../views/Settings.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/projects', name: 'Projects', component: Projects },
  { path: '/projects/:id', name: 'ProjectDetail', component: ProjectDetail },
  { path: '/projects/:id/script', name: 'ScriptEditor', component: ScriptEditor },
  { path: '/projects/:id/storyboard', name: 'StoryboardEditor', component: StoryboardEditor },
  { path: '/assets', name: 'Assets', component: Assets },
  { path: '/tasks', name: 'VideoTasks', component: VideoTasks },
  { path: '/workflow', name: 'Workflow', component: Workflow },
  { path: '/settings', name: 'Settings', component: Settings },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
