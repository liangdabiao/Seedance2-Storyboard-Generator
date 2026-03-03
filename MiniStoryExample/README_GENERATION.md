# 《茉莉花巷的小兔子》视频生成指南

## 📋 项目概述

本项目使用 **Seedance 1.5 Pro API** 生成动画短片《茉莉花巷的小兔子》,共20集,每集12秒,总时长约4分钟。

### 技术栈
- **视频生成:** Seedance 1.5 Pro API (火山引擎)
- **素材来源:** GitHub Raw (远程图片URL)
- **脚本语言:** Python 3
- **视频处理:** FFmpeg (用于拼接)

---

## 🚀 快速开始

### 1. 环境准备

```bash
# 确认Python环境
python3 --version  # 需要 Python 3.7+

# 安装依赖
pip3 install requests

# 检查FFmpeg (用于后期拼接)
ffmpeg -version
```

### 2. API配置

API Key 已预配置在脚本中:
```python
API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
```

### 3. 运行测试

```bash
cd /data/Seedance2-Storyboard-Generator/MiniStoryExample

# 测试生成 E01 (第一集)
python3 seedance_generator.py
```

测试成功后,视频将保存到 `./videos/E01_茉莉花巷的小兔子.mp4`

---

## 📁 文件结构

```
MiniStoryExample/
├── seedance_generator.py          # 主生成脚本
├── episode_configs_full.json      # 所有20集的完整配置
├── videos/                         # 生成的视频输出目录
│   ├── E01_茉莉花巷的小兔子.mp4
│   ├── E02_晴朗的午后.mp4
│   └── ...
├── artifacts/                      # 素材图片(需上传到GitHub)
│   ├── C01.png  # 小兔子
│   ├── C05.png  # 小狐狸
│   ├── S01.png  # 茉莉花巷
│   └── ...
└── README_GENERATION.md           # 本文档
```

---

## 🎬 生成流程

### 阶段1: 测试单集 (已完成)

```python
# 文件: seedance_generator.py
# 默认执行测试模式,生成E01

python3 seedance_generator.py
```

**预期输出:**
```
╔══════════════════════════════════════════════════════════╗
║        Seedance 1.5 Pro 视频生成器                       ║
║        《茉莉花巷的小兔子》                               ║
╚══════════════════════════════════════════════════════════╝

已加载 5 个集数配置

【测试模式】仅生成 E01 用于测试

============================================================
开始生成: [E01] 茉莉花巷的小兔子
============================================================
[E01] 创建任务...
  提示词: 温暖水彩动画风格,16:9横屏,12秒...
  图片参考: C01, S01, S02, P01, P03
[E01] ✓ 任务创建成功: cgt-2025****
[E01] 等待任务完成...
[E01] 状态: running (已等待 10s)
[E01] 状态: running (已等待 20s)
...
[E01] 状态: succeeded (已等待 120s)
[E01] ✓ 生成成功!
  下载视频到: ./videos/E01_茉莉花巷的小兔子.mp4
  ✓ 下载完成
[E01] ✓ 全部完成!

✓ 测试成功! 视频已保存到 ./videos/ 目录
```

### 阶段2: 批量生成所有集数

修改 `seedance_generator.py` 的 `main()` 函数:

```python
def main():
    """主函数"""
    # ... 省略初始化代码 ...
    
    # 方式1: 逐个生成(推荐,便于调试)
    configs = load_episode_configs()
    for i, config in enumerate(configs, 1):
        print(f"\n进度: {i}/{len(configs)}")
        success = generate_single_episode(api, config)
        if not success:
            print(f"✗ {config.episode_id} 生成失败,停止")
            break
        if i < len(configs):
            print("休息30秒...")
            time.sleep(30)
    
    # 方式2: 调用批量函数
    # generate_all_episodes()
```

或直接在命令行执行:

```python
# Python交互式
python3
>>> from seedance_generator import *
>>> generate_all_episodes()
```

### 阶段3: 视频拼接

所有集数生成完成后,使用FFmpeg拼接:

```bash
# 创建文件列表
cd videos
for f in E*.mp4; do echo "file '$f'" >> filelist.txt; done

# 拼接视频
ffmpeg -f concat -safe 0 -i filelist.txt -c copy 茉莉花巷的小兔子_完整版.mp4

# 清理临时文件
rm filelist.txt
```

---

## ⚙️ API说明

### 关键参数

| 参数 | 值 | 说明 |
|------|------|------|
| `model` | `doubao-seedance-1-5-pro-251215` | 模型ID |
| `duration` | `12` | 视频时长(秒),最大12秒 |
| `ratio` | `"16:9"` | 画幅比例 |
| `generate_audio` | `true` | 生成音频 |
| `watermark` | `false` | 无水印 |

### 素材URL格式

```
https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts/{素材ID}.png
```

示例:
- C01: `https://raw.githubusercontent.com/.../artifacts/C01.png`
- S01: `https://raw.githubusercontent.com/.../artifacts/S01.png`

---

## 📊 生成状态

### 任务状态说明

| 状态 | 说明 | 处理 |
|------|------|------|
| `queued` | 排队中 | 继续等待 |
| `running` | 生成中 | 继续等待 |
| `succeeded` | 成功 | 下载视频 |
| `failed` | 失败 | 查看错误信息 |

### 预估时间

- **单集生成:** 约2-5分钟 (取决于服务器负载)
- **全部20集:** 约1-2小时 (含休息间隔)
- **下载视频:** 每个约30秒

---

## 🛠️ 故障排查

### 问题1: API Key无效

```
✗ 任务创建失败: 401 Unauthorized
```

**解决:** 检查API Key是否正确,是否已开通Seedance 1.5 Pro服务

### 问题2: 图片URL无法访问

```
✗ 任务创建失败: 400 Bad Request - Invalid image URL
```

**解决:** 
1. 确认artifacts文件夹的图片已上传到GitHub
2. 检查GitHub仓库分支是否为`minitest`
3. 尝试在浏览器直接访问图片URL

### 问题3: 生成超时

```
✗ 任务超时(600秒)
```

**解决:**
1. 增加超时时间: `wait_for_completion(..., timeout=1200)`
2. 检查API服务状态
3. 尝试减少同时提交的任务数

### 问题4: 视频质量不满意

**解决:**
1. 优化提示词描述,增加细节
2. 调整图片参考的数量和质量
3. 尝试不同的prompt表达方式

---

## 📝 提示词编写技巧

### 1. 时间轴清晰

```
0-3s: [镜头] + [场景] + [动作]
3-6s: [镜头] + [内容] + [情绪]
...
```

### 2. 镜头运动

- **推镜头:** 推近主体,强调情绪
- **拉镜头:** 展现环境,营造氛围
- **跟随镜头:** 跟随主体移动
- **特写:** 捕捉细节和表情

### 3. 视觉风格一致

每个提示词开头都包含:
```
温暖水彩动画风格,16:9横屏,12秒,[情绪氛围]
```

### 4. 图片参考策略

- **角色:** C01(小兔子), C05(小狐狸)
- **场景:** S01-S11(根据场景选择)
- **道具:** P01-P10(按需添加)
- **特效:** FX01-FX05(心跳、声波等)

---

## 🎯 优化建议

### 性能优化

1. **并发控制:** 避免同时提交过多任务,建议间隔30秒
2. **重试机制:** 失败任务自动重试2-3次
3. **断点续传:** 记录已完成的集数,支持中断恢复

### 成本优化

1. **测试先行:** 先测试1-2集,确认效果后再批量生成
2. **素材复用:** 同一角色/场景的素材可以在多集中复用
3. **时长控制:** 12秒是最大值,可适当减少以降低成本

---

## 📚 参考资源

- [Seedance 1.5 Pro 官方文档](https://www.volcengine.com/docs/82379/1520757)
- [火山方舟控制台](https://console.volcengine.com/ark/)
- [FFmpeg 文档](https://ffmpeg.org/documentation.html)

---

## ✅ 检查清单

生成前确认:

- [ ] Python环境已安装 (3.7+)
- [ ] requests库已安装
- [ ] API Key已配置
- [ ] artifacts图片已上传到GitHub
- [ ] 图片URL可访问(浏览器测试)
- [ ] 输出目录`./videos/`已创建

生成后确认:

- [ ] 所有20集视频已生成
- [ ] 视频时长正确(12秒)
- [ ] 画面连贯性良好
- [ ] 音频质量满意
- [ ] 无明显错误或瑕疵

---

## 🎉 完成后

1. 将所有视频拼接成完整版
2. 添加片头片尾字幕(可选)
3. 调整音量和色彩(可选)
4. 导出最终成片

**祝生成顺利! 🌸**
