# AI 视频制作工作流 - Vue 前端

基于 Vue 3 + TypeScript + Vite 构建的 AI 视频制作工作台。

## 功能模块

### 1. 项目管理
- 创建、编辑、删除项目
- 项目状态管理（草稿/进行中/已完成）
- 项目详情展示

### 2. 剧本编辑器
- Markdown 语法支持
- 实时预览
- 剧本模板快速插入
- 自动保存

### 3. 分镜编辑器
- 分镜脚本编写（时间轴格式）
- 素材引用管理
- 单集视频生成
- 批量生成

### 4. 素材管理
- 角色/场景/道具分类
- 编号自动生成（C01/S01/P01）
- 图片拖拽上传
- 提示词管理

### 5. 视频任务监控
- 实时任务状态跟踪
- 进度条展示
- 批量操作（取消/删除）
- 视频预览和下载

### 6. 可视化工作流
- 6步标准流程可视化
- 实时执行状态
- 步骤详情查看
- 运行日志

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript
- **Vite** - 下一代前端构建工具
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue 官方状态管理库
- **Tailwind CSS** - 实用优先的 CSS 框架
- **Heroicons** - 精美的 SVG 图标集
- **date-fns** - 现代 JavaScript 日期工具库
- **marked** - Markdown 解析器

## 快速开始

### 安装依赖

```bash
cd frontend-vue
npm install
```

### 开发环境运行

```bash
npm run dev
```

访问 http://localhost:3000

### 生产环境构建

```bash
npm run build
```

构建产物位于 `dist` 目录

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
frontend-vue/
├── src/
│   ├── api/              # API 接口
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   ├── types/            # TypeScript 类型定义
│   ├── views/            # 页面视图
│   │   ├── Dashboard.vue         # 工作台
│   │   ├── Projects.vue          # 项目管理
│   │   ├── ProjectDetail.vue     # 项目详情
│   │   ├── ScriptEditor.vue      # 剧本编辑器
│   │   ├── StoryboardEditor.vue  # 分镜编辑器
│   │   ├── Assets.vue            # 素材管理
│   │   ├── VideoTasks.vue        # 视频任务
│   │   ├── Workflow.vue          # 可视化工作流
│   │   └── Settings.vue          # 设置
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── style.css         # 全局样式
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

## API 集成

前端通过代理配置与后端 Java API 通信：

```javascript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

确保后端 Java 服务运行在 8080 端口。

## 配置说明

在设置页面可以配置：
- API 基础地址
- API Key
- 默认视频时长
- 默认分辨率
- 默认风格前缀

配置保存在浏览器 localStorage 中。

## 开发计划

- [x] 基础项目结构
- [x] 项目管理
- [x] 剧本编辑器
- [x] 分镜编辑器
- [x] 素材管理
- [x] 视频任务监控
- [x] 可视化工作流
- [ ] 后端 API 对接
- [ ] 用户认证
- [ ] 数据持久化
- [ ] 多语言支持

## 浏览器支持

- Chrome 80+
- Firefox 75+
- Safari 13.1+
- Edge 80+

## 许可证

MIT
