# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

This is a Chinese-language repository for AI video production workflow combining Claude Code (script and storyboard generation), GPT-Image-2、Seedream 、Nana Banana Pro 等 (asset generation), and Seedance 2.0 (video generation). The workflow transforms stories and novels into multi-episode AI video series with consistent visual style and character design.

## Core Workflow

The production process follows these steps:

1. **Script Development** - Converting source material into four-act structure scripts
2. **Asset Generation Plan** - Creating numbered prompts for characters (C01-C99), scenes (S01-S99), and props (P01-P99)
3. **Image Generation** - Using GPT-Image-2、Seedream 、Nana Banana Pro 等 to generate visual assets with consistent style prefixes
4. **Storyboard Script Generation** - Creating Seedance 2.0 prompts in time-axis format (0-3s, 3-6s, 6-9s, 9-12s, 12-15s)
5. **Video Generation** - Using Seedance 2.0 platform with video extension feature for episode chaining

## Project Structure

```
写剧本和分镜/
├── .claude/skills/seedance-storyboard-generator/  # Custom skill for video script generation
├── docs/                           # Documentation and reference materials
│   ├── 剧本和分镜.md                 # Main workflow documentation
│   ├── 流程.md                      # Process overview
│   ├── structured-prompt.md        # Seedance 2.0 prompt manual
│   └── 🎬 即梦 Seedance 2.0 使用手册.docx  # Official platform manual
├── 林冲项目/                        # Example project: Lin Chong
│   ├── 林教头风雪山神庙_剧本.md     # Complete script
│   ├── 林教头风雪山神庙_素材清单.md # Asset generation prompts
│   ├── 林教头风雪山神庙_E0X_分镜.md # Individual episode scripts
│   └── 使用指南.md                  # Project-specific guide
├── 聂风项目/                        # Example project: Nie Feng
├── 项链项目/                        # Example project: The Necklace
└── 素材/                            # Generated image assets
```

## Key File Patterns

- `[Title]_剧本.md` - Four-act script with episode breakdown (起承转合 structure)
- `[Title]_素材清单.md` - Numbered asset generation prompts with English style prefixes
- `[Title]_E[XX]_分镜.md` - Individual episode storyboard scripts for Seedance 2.0

## Asset Numbering Convention

| Prefix | Range | Type | Example |
|--------|-------|------|---------|
| C | C01-C99 | Characters (multiple angles per character) | C01 林冲·正面全身 |
| S | S01-S99 | Scenes/Locations | S01 沧州草料场·雪景 |
| P | P01-P99 | Props/Objects | P01 长枪 |

## Seedance 2.0 Prompt Structure

Each episode script contains:

1. **素材上传清单** - Table mapping asset IDs to upload slots
2. **Seedance Prompt** - Time-axis format description:
   - Style and atmosphere specification
   - 0-3s: Scene establishment
   - 3-6s: Subject introduction
   - 6-9s: Development/conflict
   - 9-12s: Climax/transition
   - 12-15s: Conclusion
   - Sound design (music + SFX + dialogue)
   - Asset references (@图片X syntax)
3. **尾帧描述** - Final frame description for next episode continuity

## Video Extension (Episode Chaining)

For episodes 2+, use video extension to maintain continuity:
- Upload previous episode video as @视频1
- Start prompt with: `将@视频1延长15s`
- This creates seamless transitions between episodes

## Style Consistency

All asset generation prompts must begin with a unified style prefix. Example:
```
Chinese ink wash painting style mixed with anime cel-shading
```

Character differentiation uses distinct color schemes and visual markers for recognition in the chosen art style.

## Important Constraints

- **Max 9 images** per Seedance 2.0 generation
- **Max 3 videos** (total 15s) as reference
- **Sensitive words** may cause generation failures (e.g., "江湖人士")
- **Video editing** capability is limited - regeneration required for most changes
- **Instruction following** can be inconsistent with complex prompts (300+ words)

## Camera Movement Keywords (Chinese)

- 推镜头/拉镜头 (Push/Pull)
- 摇镜头 (Pan)
- 移镜头 (Truck)
- 跟镜头 (Follow)
- 环绕镜头/360度旋转 (Orbit/360°)
- 升降镜头 (Crane)
- 希区柯克变焦 (Hitchcock zoom)
- 一镜到底 (One shot)
- 手持晃动 (Handheld shake)

## Skill Usage

Invoke the custom skill for video script generation:
```
/skill seedance-storyboard-generator
```

The skill handles: story analysis, four-act script structure, asset planning, and Seedance 2.0 formatted prompts.

## Reference Documentation

- `.claude/skills/seedance-storyboard-generator/SKILL.md` - Skill definition and workflow
- `.claude/skills/seedance-storyboard-generator/references/seedance-manual.md` - Complete Seedance 2.0 manual
- `.claude/skills/seedance-storyboard-generator/references/故事转视频脚本-转换工具.md` - Story adaptation template
