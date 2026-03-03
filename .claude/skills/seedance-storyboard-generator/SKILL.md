---
name: seedance-storyboard-generator
description: Professional AI video script and storyboard generator for Seedance 2.0 platform. Use when user asks to: (1) Convert articles/stories into video scripts, (2) Generate Seedance 2.0 storyboard prompts, (3) Plan multi-episode AI video series, (4) Create character/scene/prop generation prompts for image models like Nana Banana Pro. Input can be full novels, articles, or brief story outlines. Output includes four-act script structure, episode breakdown, asset generation prompts, and Seedance 2.0 formatted storyboard scripts.
---

# Seedance Storyboard Generator

Expert AI script and storyboard generation system for creating professional AI video series on Seedance 2.0 platform.

## Core Concept: 首帧+尾帧 & 视频延长 混合模式

Seedance 2.0 每次生成视频支持两种核心方式：

### 方式一：首帧图片生成（用于片段1）
- 上传 **@图片1（首帧）** + 可选 **@图片2（参考）**
- 用结构化 prompt 描述从首帧到尾帧的变化过程

### 方式二：视频延长（用于片段2+，连贯性更好）
- 上传前一段视频为 **@视频1**，可选 **@图片1（参考）**
- 使用 `将@视频1延长` 开头 + 简短结构化描述
- 角色/场景自动保持一致，无接缝

### 推荐拆分策略：3段×5秒

每集15秒拆为**3个片段**（每段约5秒），比5段×3秒更优：
- 仅2个接缝点（vs 4个），连贯性更好
- 每段有足够叙事空间
- 片段2-3优先用视频延长，失败时备用KF首帧独立生成

## Workflow

### 1. Analyze Input

**Determine input type:**
- **Full text**: Complete novel/article requiring adaptation and episode segmentation
- **Outline**: Brief story concept requiring full script development

**Extract core elements:**
- Protagonist(s) and key characters
- Central conflict and narrative arc
- Setting/world-building elements
- Key plot points and emotional beats

Ask clarifying questions if input is ambiguous or incomplete.

### 2. Confirm Production Parameters

**Essential questions to ask:**

1. **Visual Style**: What visual style? (写实/动画/水墨/科幻/复古/电影感/其他)
2. **Duration**: Total runtime? (Standard: 20 episodes × 15s each ≈ 5 minutes)
3. **Target Platform**: Aspect ratio? (16:9横屏 / 9:16竖屏 / 2.35:1电影宽屏)
4. **Tone**: Overall emotional tone? (史诗/温馨/悬疑/欢快/忧伤等)

Document these parameters for consistent application throughout.

### 3. Generate Four-Act Script Structure

**Structure:**
- **Act 1 (起)**: Episodes 1-5 - Introduction and inciting incident
- **Act 2 (承)**: Episodes 6-10 - Rising action and complications
- **Act 3 (转)**: Episodes 11-15 - Climax and confrontation
- **Act 4 (合)**: Episodes 16-20 - Resolution and conclusion

**For each episode include:**
- Episode number and title
- Duration (standard: 15 seconds)
- Emotional tone/mood
- Key plot points
- Beginning/ending frame description (for continuity)

**Output format:** Markdown document with clear section headers.

### 4. Create Asset Generation Plan

**Four categories of visual assets:**

| Category | Prefix | Range | Description | Example |
|----------|--------|-------|-------------|---------|
| Characters | C | C01-C99 | 角色多角度参考 | C01 天机师·正面全身 |
| Scenes | S | S01-S99 | 场景/地点 | S01 仙山云雾·悬崖古松 |
| Props | P | P01-P99 | 道具/物件 | P01 天机盘 |
| **Key Frames** | **KF** | **KF01-KF99** | **片段首帧图片** | **KF01 首帧·雾山幽光** |

> **关键帧 (KF)** 是核心素材类型。片段1必须使用KF作为首帧上传；片段2-3优先用视频延长，KF作为备用方案。

**Asset generation prompt format:**
```
### [编号] — [名称]

[Style prefix], [detailed visual description in English], [technical specs]
```

**Style Prefix Examples:**
- Chinese ink wash painting style mixed with anime cel-shading
- Cinematic photorealistic style with dramatic lighting
- 3D Pixar-style animation rendering
- Sci-fi cyberpunk aesthetic with neon lighting

**Character differentiation:** Use distinct color schemes and visual markers for each character to ensure recognition in the chosen art style.

### 5. Generate Seedance 2.0 Storyboard Scripts

**将每集拆分为3个片段**（每段约5秒），采用混合生成策略。

---

#### 片段1：首帧图片生成（独立生成）

**上传素材表：**

| 槽位 | 素材编号 | 说明 |
|------|----------|------|
| @图片1（首帧） | **KFXX** | 关键帧首帧图片 |
| @图片2（参考） | **CXX/SXX/PXX** | 角色/场景/道具参考 |

**Prompt 格式 — 结构化关键词：**

```
[风格]，[画幅]
@图片1首帧，@图片2[用途]参考

【镜头】[起始镜头] → [结束镜头]
【主体】[人物/物体状态和位置]
【变化】[状态A] → [状态B] → [状态C]
【光影】[光源和氛围变化]
【声音】[配乐] + [音效]
```

**Prompt 要求：**
- 使用【】分类标签，每个维度独立清晰
- 用 → 箭头表示状态变化过程
- 总字数控制在 **120字以内**
- 每个标签1行，不写长句

**【声音】标签规范：**
每个片段的 prompt 必须包含【声音】标签，格式为：
```
【声音】[配乐风格] + [环境音效] + [动作音效] + [对白/旁白]（如有）
```
- **配乐**：描述音乐风格和情绪走向（如"空灵古琴渐强"、"紧张弦乐"）
- **环境音**：场景自然声音（如"山风呼啸"、"雨声淅沥"）
- **动作音效**：与画面动作对应的声音（如"剑鸣"、"脚步声渐远"）
- **对白/旁白**：如有台词需标注语气和内容
- 音效应与画面【变化】节奏匹配（如光芒消退时配乐渐弱）
- Seedance 支持上传 @音频X 作为配乐/音效参考（≤3个，总时长≤15s）

---

#### 片段2-3：视频延长（优先方案）

**上传素材表：**

| 槽位 | 素材编号 | 说明 |
|------|----------|------|
| @视频1 | 上一片段视频 | 延长源 |
| @图片1（参考） | **CXX/SXX/PXX** | 可选，角色/道具参考 |

**Prompt 格式：**

```
将@视频1延长，[风格]，[画幅]
@图片1[用途]参考

【镜头】[镜头变化]
【主体】[人物动作发展]
【变化】[从当前状态] → [新状态]
【光影】[光影变化]
【声音】[配乐] + [音效]
```

**声音延续：** 视频延长时声音设计应与上一片段衔接，保持配乐连贯性，音效随画面变化自然过渡。

**如果视频延长效果不好，使用备用方案：**

| 槽位 | 素材编号 | 说明 |
|------|----------|------|
| @图片1（首帧） | **KFXX** | 备用关键帧首帧 |
| @图片2（参考） | **CXX/SXX/PXX** | 角色/场景/道具参考 |

备用 prompt 与片段1格式相同。

---

### 6. Storyboard Templates

#### Template A：首帧生成片段（片段1）

```markdown
## 片段1：[名称]（0-5s）

### 上传素材
| 槽位 | 素材编号 | 内容 |
|------|----------|------|
| @图片1（首帧） | **KF01** | [名称] — [画面简述] |
| @图片2（参考） | **CXX/SXX** | [名称] — [参考用途] |

### Seedance Prompt
｜```
[风格]，[画幅]
@图片1首帧，@图片2[用途]参考

【镜头】[起始] → [结束]
【主体】[描述]
【变化】[A] → [B] → [C]
【光影】[描述]
【声音】[配乐] + [音效]
｜```

### 尾帧描述
[精确的尾帧画面状态]
```

#### Template B：视频延长片段（片段2-3）

```markdown
## 片段2：[名称]（5-10s）

### 方案A：视频延长（优先）
| 槽位 | 素材编号 | 内容 |
|------|----------|------|
| @视频1 | 片段1视频 | 延长源 |
| @图片1（参考） | **CXX/PXX** | [名称] — [参考用途] |

#### Seedance Prompt
｜```
将@视频1延长，[风格]，[画幅]
@图片1[用途]参考

【镜头】[变化]
【主体】[动作发展]
【变化】[当前] → [新状态]
【声音】[配乐] + [音效]
｜```

### 方案B：独立生成（备用）
| 槽位 | 素材编号 | 内容 |
|------|----------|------|
| @图片1（首帧） | **KF02** | [名称] — [画面简述] |
| @图片2（参考） | **CXX/SXX** | [名称] — [参考用途] |

#### Seedance Prompt
｜```
[风格]，[画幅]
@图片1首帧，@图片2[用途]参考

【镜头】[起始] → [结束]
【主体】[描述]
【变化】[A] → [B] → [C]
【光影】[描述]
【声音】[配乐] + [音效]
｜```

### 尾帧描述
[精确的尾帧画面状态]
```

## Output Files

Generate these deliverable files, saved to `projects/[ProjectName]/`:

1. **[Title]_剧本.md** - Complete four-act script with episode breakdown
2. **[Title]_素材清单.md** - All assets organized by four categories:
   - Characters (C01-C99)
   - Scenes (S01-S99)
   - Props (P01-P99)
   - Key Frames (KF01-KF99) — 片段1必须的首帧 + 片段2-3的备用首帧
3. **[Title]_E[XX]_分镜.md** - Individual episode storyboard, containing:
   - 制作流程概览表（片段 / 生成方式 / 素材 / 镜头概要）
   - 每个片段提供方案A（视频延长）和方案B（独立首帧）
   - 尾帧描述和衔接提示
   - 导演备注

## 生图顺序建议

1. **角色 (C)** — 先锁定角色形象一致性
2. **场景 (S) + 道具 (P)** — 确定环境和道具风格
3. **关键帧 (KF)** — 最后按片段顺序生成，用 C/S/P 素材做参考

## Quality Assurance

**Before finalizing:**
- 片段1的 prompt 中明确写出 `@图片1首帧，@图片2为XX参考`
- 片段2-3的方案A以 `将@视频1延长` 开头
- 片段2-3同时提供方案B备用（独立KF首帧）
- 每个 prompt 使用【】结构化标签
- 每个 prompt 控制在 **120字以内**
- **每个片段必须包含【声音】标签**，配乐/音效/对白缺一不可（无对白时可省略对白项）
- 片段间声音设计应连贯过渡，不出现突兀断裂
- 验证无敏感词
- 前一片段尾帧描述与下一片段首帧/延长内容吻合

## Reference Material

For detailed Seedance 2.0 prompt patterns, templates, and best practices, see [references/seedance-manual.md](references/seedance-manual.md).

## Common Pitfalls to Avoid

1. **不要写散文长句**：使用【镜头】【主体】【变化】【光影】【声音】结构化标签，每行一个维度
2. **不要使用时间轴格式**：不写 `0-3s画面` 分段，使用 → 箭头表示状态变化
3. **Prompt 不超过120字**：超长 prompt 指令跟随不稳定
4. **片段2-3优先视频延长**：连贯性远优于独立首帧生成
5. **Sensitive words**: 避免敏感词触发审核
6. **必须标注素材编号**：@图片1/@图片2/@视频1 对应哪个素材编号
7. **Style consistency**: 所有素材生成 prompt 使用相同风格前缀
