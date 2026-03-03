---
name: seedance-storyboard-generator
description: Professional AI video script and storyboard generator for Seedance 2.0 platform. Use when user asks to: (1) Convert articles/stories into video scripts, (2) Generate Seedance 2.0 storyboard prompts, (3) Plan multi-episode AI video series, (4) Create character/scene/prop generation prompts for image models like Nana Banana Pro. Input can be full novels, articles, or brief story outlines. Output includes four-act script structure, episode breakdown, asset generation prompts, and Seedance 2.0 formatted storyboard scripts.
---

# Seedance Storyboard Generator

Expert AI script and storyboard generation system for creating professional AI video series on Seedance 2.0 platform.

## Core Concept: 首帧+尾帧 Format

Seedance 2.0 每次生成视频只支持 **首帧图片(@图片1) + 尾帧/参考图片(@图片2)** 的格式。因此：

- 每集（15秒）需拆分为**多个片段**（通常5段，每段约3秒）
- 每个片段需要一张**首帧图片**上传为 @图片1
- 可选上传一张**尾帧参考或道具/角色参考**作为 @图片2
- 前一片段的尾帧 = 下一片段的首帧（保证连贯性）
- 片段之间可使用 `将@视频1延长` 接续，效果更好

## Workflow

Follow this sequential process to convert source material into production-ready video scripts:

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
| **Key Frames** | **KF** | **KF01-KF99** | **每个片段的首帧图片** | **KF01 首帧·雾山幽光** |

> **关键帧 (KF)** 是新增的核心素材类型。每个视频片段都需要一张 KF 图片作为首帧上传。KF 图片是角色+场景+道具的合成画面，描述该片段开始时的精确画面状态。

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

**Output format:** Organized list with unique IDs, suitable for copy-pasting into image generators.

### 5. Generate Seedance 2.0 Storyboard Scripts (首帧+尾帧 Format)

**将每集拆分为多个片段**，通常 5 段（每段约 3 秒）。每个片段生成以下内容：

**a. 上传素材表 — 明确标注 @图片1 和 @图片2 对应的素材编号**

```
| 槽位 | 素材编号 | 内容 |
|------|----------|------|
| @图片1（首帧） | **KF01** | [首帧画面描述] |
| @图片2（参考） | **C01/S01/P01** | [参考图用途说明] |
```

- **@图片1** 必须是关键帧 (KF) 素材，作为视频起始画面
- **@图片2** 用于角色一致性、场景风格或道具细节参考

**b. Seedance Prompt — 首帧+尾帧格式**

```
[风格描述]，[画幅比例]

以@图片1为首帧，@图片2为[用途]参考

[首帧画面描述]，[镜头运动]，[动作/变化过程描述]，[尾帧画面描述]
```

**Prompt 要求：**
- 开头必须写明风格和画幅
- 紧接着声明 @图片1 和 @图片2 的用途
- 正文描述从首帧状态到尾帧状态的**完整运动过程**
- 控制在 200 字以内，避免过长导致指令跟随不一致
- **不使用时间轴分段格式**（如 0-3s、3-6s），因为平台不支持

**c. 尾帧描述**
- 文字描述该片段结尾的精确画面状态
- 用于确认片段间衔接、以及生成下一片段的 KF 首帧图片

**d. 片段间衔接**
- 优先使用 `将@视频1延长` 接续上一段视频（连贯性更好）
- 若效果不好，用该片段的 KF 首帧图片独立生成

**Camera movement keywords:** 推镜头/拉镜头/摇镜头/移镜头/跟镜头/环绕镜头/升降镜头/希区柯克变焦/一镜到底/手持晃动

**For episode chaining (Ep 2+):** Start prompt with `将@视频1延长` and upload previous episode's last clip as video reference.

### 6. Storyboard Template (Single Clip)

```markdown
## 片段X：[名称]（Xs-Ys）

### 上传素材
| 槽位 | 素材编号 | 内容 |
|------|----------|------|
| @图片1（首帧） | **KFXX** | [首帧名称] — [画面简述] |
| @图片2（参考） | **CXX/SXX/PXX** | [素材名称] — [参考用途] |

### Seedance Prompt
​```
[风格]，[画幅]

以@图片1为首帧，@图片2为[用途]参考

[完整的运动过程描述，从首帧画面到尾帧画面]
​```

### 尾帧描述
[精确的尾帧画面状态文字描述]
```

## Output Files

Generate these deliverable files, saved to `projects/[ProjectName]/`:

1. **[Title]_剧本.md** - Complete four-act script with episode breakdown
2. **[Title]_素材清单.md** - All assets organized by four categories:
   - Characters (C01-C99)
   - Scenes (S01-S99)
   - Props (P01-P99)
   - Key Frames (KF01-KF99) — 每个片段的首帧图片
3. **[Title]_E[XX]_分镜.md** - Individual episode storyboard, containing:
   - 制作流程概览表（片段 / @图片1 / @图片2 / 镜头概要）
   - 每个片段的完整 Seedance Prompt（首帧+尾帧格式）
   - 尾帧描述和衔接提示
   - 导演备注

## 生图顺序建议

在素材清单末尾提供推荐的生图顺序：

1. **角色 (C)** — 先锁定角色形象一致性
2. **场景 (S) + 道具 (P)** — 确定环境和道具风格
3. **关键帧 (KF)** — 最后按片段顺序生成，用前面的 C/S/P 素材做参考，确保画面衔接

## Quality Assurance

**Before finalizing:**
- 每个片段的 Prompt 中必须明确写出 `以@图片1为首帧，@图片2为XX参考`
- 上传素材表必须标注 @图片1 和 @图片2 对应的素材编号
- 前一片段的尾帧描述必须与下一片段的 KF 首帧内容吻合
- 每集的关键帧 (KF) 数量 = 片段数量（通常 5 个）
- 每个 Prompt 控制在 200 字以内
- 验证无敏感词

## Reference Material

For detailed Seedance 2.0 prompt patterns, templates, and best practices, see [references/seedance-manual.md](references/seedance-manual.md).

Key reference sections:
- Templates 1-16 for different video types (叙事/产品/角色/风景/战争/等)
- Camera movement quick reference
- Atmosphere keyword library
- Multimodal reference syntax (@图片X, @视频X, @音频X)

## Common Pitfalls to Avoid

1. **不要使用时间轴格式**：Seedance 不支持 `0-3s画面` 这种分段格式，必须使用首帧+尾帧格式
2. **Sensitive words**: Seedance may reject content with certain terms. Avoid common triggers or use alternative phrasing.
3. **Over-complex prompts**: Keep prompts under 200 words. Long prompts (300+ words) have inconsistent instruction following.
4. **Missing @图片 mapping**: Every prompt must explicitly state which asset is @图片1 and @图片2.
5. **Missing key frames**: Every clip needs a KF asset. Total KF count per episode = number of clips.
6. **Missing continuity**: Clip N's ending frame must match Clip N+1's KF starting frame.
7. **Inconsistent style**: Apply same visual style prefix to all asset generation prompts.
