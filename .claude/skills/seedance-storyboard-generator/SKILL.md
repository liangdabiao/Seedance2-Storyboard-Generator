---
name: seedance-storyboard-generator
description: 专业的Seedance 2.0平台AI视频脚本和分镜生成器。当用户要求：(1) 将文章/故事转换为视频脚本，(2) 生成Seedance 2.0分镜提示词，(3) 规划多集AI视频系列，(4) 为Nana Banana Pro等图像模型创建角色/场景/道具生成提示词时使用。输入可以是完整的小说、文章或简短的故事大纲。输出包括标准脚本格式的完整剧本（△镜头描述+对白+OS/VO+闪回+字幕）、剧集分解、资产生成提示词和Seedance 2.0格式的分镜脚本。
---

# Seedance 分镜生成器

专业的AI脚本和分镜生成系统，用于在Seedance 2.0平台上创建专业的AI视频系列。

## Workflow

按照以下顺序流程将源材料转换为可用于制作的视频脚本：

### 1. 分析输入

**确定输入类型：**
- **完整文本**：需要改编和分集的完整小说/文章
- **大纲**：需要完整脚本开发的简短故事概念

**提取核心元素：**
- 主角和关键角色
- 核心冲突和叙事弧线
- 场景/世界观元素
- 关键情节和情感节拍
- 核心梗/卖点

如果输入模糊或不完整，请提出澄清问题。

### 2. 确认制作参数

**需要询问的基本问题：**

1. **视觉风格**：什么视觉风格？（写实/动画/水墨/科幻/复古/电影感/其他）
2. **时长**：总运行时间？（标准：短剧5集×15秒≈75秒；长剧20集×15秒≈5分钟）
3. **目标平台**：画幅比例？（16:9横屏 / 9:16竖屏 / 2.35:1电影宽屏）
4. **基调**：整体情感基调？（史诗/温馨/悬疑/欢快/忧伤等）
5. **核心梗**：一句话卖点是什么？（绝境反杀/复仇爽剧/治愈温馨/悬疑惊悚等）

记录这些参数，确保在整个过程中一致应用。

### 3. 生成完整剧本结构

**重要：输出必须严格遵循以下格式，符合"好剧本.md"的质量标准。**

#### 3.1 Output Format - Part 1: Story Analysis

```
# [Title] - 剧本

一、核心梗
[Two-word or short phrase core hook, e.g., 绝境反杀、经典武侠、复仇爽剧]

二、故事梗概
故事背景：[Time period, location, initial situation]
开场冲突：[What breaks the status quo, inciting incident]
主角画像：[Who is the protagonist, their core trait, current state]
主线事件：[Key plot progression → climax → resolution]
结局：[Final outcome, character transformation]

三、一句话卖点
[Punchy marketing hook with emotion, e.g., 忍无可忍，无须再忍！风雪夜，豹子头林冲长枪出鞘，血溅山神庙！]

四、人物小传
[For EACH main character include:]

**角色名**
视觉形象：[Age, appearance details, clothing, key visual markers]
身份背景：[Social role, background, relationship to protagonist]
核心标签：[2-4 character traits, e.g., 隐忍不发、悲情英雄、杀神降临]
性格特点：[Detailed personality description, arc transformation]
金句：[Memorable line that captures their essence]

五、剧本大纲
前期（起）：[Setup - establish world, protagonist, status quo]
中期（承/转）：[Conflict development, complications, turning point]
后期（高潮）：[Climax, confrontation, emotional peak]
尾声（收尾）：[Resolution, aftermath, thematic closure]

六、剧本正文
```

#### 3.2 Output Format - Part 2: Script Body (Each Episode)

**CRITICAL: Each episode must use the EXACT format below:**

```
第X集
X-X [日/夜] [内/外] [场景名称]
道具：[List key props]
出场人物：[List characters in scene]

△ 【空镜/开场镜头】[Detailed shot description with camera movement, composition, lighting, atmosphere]
△ [Shot 2 description with specific camera movement: 推/拉/摇/移/跟/环绕/升降/特写/远景]
△ [Shot 3 description - continue for each beat]
角色名（os）：[Interior monologue/voiceover]
△ [Reaction shot description]
角色名（对白/动作描述）：[Spoken dialogue]
△ [Action/sequence description]
【字幕：xxx】[When on-screen text is needed]
△ [Scene continuation]
△ 【闪回】[Flashback scene description]
[Flashback content with △ shots]
【闪回结束】
△ [Return to present, reaction]
角色名（vo）：[Voiceover from off-screen character]
△ [Climactic action sequence]
△ 【空镜】[Closing atmospheric shot]
【字幕：xxx】
```

**Script Format Rules:**

1. **Every shot starts with `△ `** (triangle + space)
2. **Camera language must be specific:**
   - 景别：远景/全景/中景/近景/特写/大特写
   - 运镜：推镜头/拉镜头/摇镜头/移镜头/跟镜头/环绕镜头/升降镜头/希区柯克变焦/一镜到底/手持晃动
   - 组合：`中景推近`、`特写环绕`、`远景俯拍`、`快速推近`、`缓慢拉远`

3. **Dialogue notation:**
   - `角色名（os）` - 画外音/内心独白 (off-screen sound/interior monologue)
   - `角色名（vo）` - 画外音，人物不在画面中 (voiceover)
   - `角色名（惊/怒/喜）` - 带情绪的对白
   - `角色名` - 普通对白

4. **Special elements:**
   - `【空镜】` - Establishing/atmospheric shot without characters
   - `【闪回】` / `【闪回结束】` - Flashback sequence
   - `【字幕：xxx】` - On-screen text/titles

5. **Action description format:**
   - Use `→` for action sequences: `林冲一枪刺穿差拨胸膛 → 鲜血喷溅 → 雪地染红`
   - Use emotive language: `杀气冲天`、`眼中怒火燃烧`、`浑身颤抖`

6. **Sensory details:**
   - Visual: 颜色、光影、构图、氛围
   - Auditory: 声音描述融入镜头中
   - Tactile: 寒冷、疼痛、质感

### 4. 创建资产生成计划

**对所有视觉资产进行分类和编号：**

| 类别 | 前缀 | 示例 | 描述 |
|----------|--------|---------|-------------|
| 角色 | C01-C99 | C01 林冲·正面全身 | 每个角色多个角度 |
| 场景 | S01-S99 | S01 沧州草料场·雪景 | 关键位置 |
| 道具 | P01-P99 | P01 长枪 | 重要物品 |

**资产生成提示词格式：**
```
### [编号] — [名称]

[风格前缀]，[详细视觉描述（中文）]，[技术规格]

**风格前缀示例：**
- Chinese ink wash painting style mixed with anime cel-shading
- Cinematic photorealistic style with dramatic lighting
- 3D Pixar-style animation rendering
- Sci-fi cyberpunk aesthetic with neon lighting

**角色区分：** 为每个角色使用不同的配色方案和视觉标记，确保在所选艺术风格中能够识别。
```

**输出格式：** 带有唯一ID的组织列表，适合复制粘贴到图像生成器中。

### 5. 生成 Seedance 2.0 分镜脚本

**为每集生成：**

**a. 素材上传列表**
```
| 上传位置 | 素材ID | 素材描述 |
| 图片1 | C01 | 林冲正面全身（角色一致性参考） |
| 图片2 | S01 | 草料场场景参考 |
```

**b. Seedance Prompt (时间轴格式，使用中文)**
```
[风格描述]，15秒，9:16竖屏，[整体氛围]

0-3秒：[场景建立] - [镜头运动]，[详细画面描述]，[氛围营造]

3-6秒：[主体引入/情节发展] - [镜头运动]，[具体动作]，[情绪表达]

6-9秒：[发展/冲突] - [镜头运动]，[关键动作]，[细节特写]

9-12秒：[高潮/转折] - [镜头运动]，[情绪爆发]，[视觉冲击]

12-15秒：[结尾/落版] - [镜头运动]，[最终画面]，[余韵]

【声音】[配乐风格] + [音效] + [对白/旁白]

【参考】@图片1 [用途说明]，@图片2 [用途说明]，@图片3 [用途说明]...
```

**c. Ending Frame Description**
- 记录最后一帧内容，确保下一集的连续性
- 包含：主体、背景、光线、构图、氛围

**Camera movement keywords:** 推镜头/拉镜头/摇镜头/移镜头/跟镜头/环绕镜头/升降镜头/希区柯克变焦/一镜到底/手持晃动

**For episode chaining (Ep 2+):** 开始提示词使用 `将@视频1延长15s` 并上传上一集作为视频参考。

## Output Files

生成以下交付文件：

1. **[Title]_剧本.md** - 完整剧本，包含：
   - 核心梗
   - 故事梗概（背景/冲突/主角/主线/结局）
   - 一句话卖点
   - 人物小传（视觉形象/身份/标签/性格/金句）
   - 剧本大纲（起承转合）
   - 剧本正文（△镜头格式 + 对白 + OS/VO + 闪回 + 字幕）

2. **[Title]_素材清单.md** - 所有角色/场景/道具生成提示词

3. **[Title]_E[XX]_分镜.md** - 单集分镜脚本，包含时间轴格式

## 脚本写作指南

### 15秒剧集的节奏

| 剧集类型 | 镜头数量 | 节奏 |
|--------------|-----------------|--------|
| 对话/情感 | 3-4个镜头 | 每个镜头4-5秒 |
| 动作/战斗 | 5-7个镜头 | 每个镜头2-3秒 |
| 蒙太奇/序列 | 6-8个镜头 | 每个镜头2秒 |

### 情感 progression

每个15秒剧集应包含：
- **开场 (0-3s)**：建立情感/氛围
- **发展 (3-9s)**：紧张感上升或情节发展
- **高潮 (9-12s)**：情感高峰或关键揭示
- **释放 (12-15s)**：解决或悬念

### 视觉一致性

- 在所有资产提示词中保持一致的艺术风格
- 为每个角色使用独特的视觉标记（颜色、轮廓、配件）
- 在场景描述中参考光线和一天中的时间

## 质量保证

**最终确定前：**
- ✅ 验证所有资产引用（@图片X）在资产列表中有对应的ID
- ✅ 检查剧集间的连续性（结尾帧 → 开场帧）
- ✅ 确保时间轴覆盖完整的15秒
- ✅ 验证相机运动是可行且逻辑顺序的
- ✅ 确认脚本使用正确的△格式和具体的相机语言
- ✅ 检查所有对话都正确标记（os/vo/情感）
- ✅ 包含感官细节（视觉、听觉、氛围）
- ✅ 验证每个剧集都有情感弧线（开场 → 发展 → 高潮 → 释放）

## 参考材料

有关详细的Seedance 2.0提示词模式、模板和最佳实践，请参阅 [references/seedance-manual.md](references/seedance-manual.md)。

关键参考部分：
- 不同视频类型的模板1-16（叙事/产品/角色/风景/战争/等）
- 相机运动快速参考
- 氛围关键词库
- 多模态引用语法（@图片X, @视频X, @音频X）

## 常见陷阱避免

1. **敏感词**：Seedance可能会拒绝包含某些术语的内容。避免常见触发器或使用替代措辞。
2. **过于复杂的提示词**：长提示词（300+字）可能会导致指令遵循不一致。优先考虑清晰度而非冗长。
3. **缺少连续性**：始终记录结尾帧并验证下一集以匹配的场景开始。
4. **风格不一致**：对所有资产生成提示词应用相同的视觉风格前缀。
5. **模糊的相机语言**：避免使用通用描述如"镜头移动" - 使用具体术语如"推镜头"、"环绕镜头"。
6. **缺少感官细节**：脚本应唤起不仅是视觉，还有声音、温度、质感。
7. **平淡的情感弧线**：每个剧集需要情感进展，而不仅仅是情节点。

## 示例对比

**❌ 不好（模糊大纲）：**
```
关键情节：
- 林冲听到门外有人说话
- 得知高球要烧死自己的阴谋
- 冲出去报仇
```

**✅ 好（标准脚本格式）：**
```
△ 林冲屏住呼吸，悄无声息地起身，贴在门缝边偷听。
△ 门外的声音透过风雪传来，清晰可辨。
陆谦（vo）：这把火下去，管教他林冲死无葬身之地！
差拨（vo）：陆大人放心，就算烧不死他，失了草料场也是死罪！
△ 林冲听到"高太尉"三字，浑身一震，眼中原本的隐忍瞬间化为熊熊怒火。
△ 【闪回】高衙内调戏妻子、自己误入白虎堂、刺配沧州的画面如走马灯般闪过。
【闪回结束】
△ 林冲死死抓着枪杆，指节因用力而泛白，牙齿咬得咯咯作响。
林冲（os）：陆谦！我待你不薄，你却要置我于死地！
△ 门外传来泼油的声音，火光瞬间映红了门缝。
△ 林冲猛地抬头，杀气冲天，原本的颓废一扫而空。
```
