# API 批量执行指南（可选，"资"和"视"两个阶段的自动化）

> 本 Skill 的主输出是**提示词**，这一点不变。这份文档解决的是提示词生成完之后的问题：
> 一部 5 集短剧要出十几张资产图 + 5 条分镜视频，手动一张张贴进平台很痛苦。
> 这里给一条 REST 批量执行的路，供用户明确要求"直接生成"时使用。

**执行前必须做的事**：把「模型 + 张数/条数 + 分辨率 + 时长 + 预估花费」告诉用户，等确认再跑。
默认仍然只交付提示词，不主动执行。

---

## 1. 为什么需要这条路

| 阶段 | 本 Skill 现在的产出 | 手动执行的痛点 |
|:--|:--|:--|
| **资** | 角色/场景/道具提示词列表 | 一部短剧十几到几十张图，逐张贴平台 |
| **视** | 每集一条 Seedance 2.0 分镜提示词 | 多集连拍要反复切参数，尾帧→首帧还要手动传图 |

用 API 可以把「最终输出清单」里的第 2/3/4 项和第 5 项直接跑成文件，一次跑完一整集。

## 2. 前置

```bash
# key 在 https://www.atlascloud.ai/console 申请
export ATLASCLOUD_API_KEY=<atlascloud-api-key>

# 看余额（key 失效会直接 401）
curl -sS https://api.atlascloud.ai/public/v1/balance \
  -H "Authorization: Bearer $ATLASCLOUD_API_KEY"
```

**不要把 key 写进任何提交的文件**，只放环境变量。

## 3. "资"阶段：批量出资产图

本 Skill §4 里点名的三个图像模型都能直接调，提示词原样传，不用改写：

| 本 Skill 的说法 | API model | 参考单价 |
|:--|:--|:--|
| GPT-Image-2 | `openai/gpt-image-2/text-to-image` | $0.009 |
| Seedream | `bytedance/seedream-v4` / `bytedance/seedream-v4.5` | $0.027 / $0.036 |
| Nano Banana Pro | `google/nano-banana-pro/text-to-image` | $0.084 |

§4.4「出图比例建议」对应的 `size`（**`宽*高`，星号分隔，不是 `2048x1152`**）：

| 资产类型 | 推荐比例 | 2K 档 `size` | 1K 档 `size` |
|:--|:--|:--|:--|
| 角色正面全身照 | 9:16 | `1152*2048` | `576*1024` |
| 角色三视图/四视图 | 16:9 | `2048*1152` | `1024*576` |
| 角色三视图/四视图 | 21:9 | `2560*1088` | `1280*544` |
| 场景图 | 16:9 | `2048*1152` | `1024*576` |
| 道具图 | 1:1 | `2048*2048` | `1024*1024` |

提交一张：

```bash
curl -sS -X POST https://api.atlascloud.ai/api/v1/model/generateImage \
  -H "Authorization: Bearer $ATLASCLOUD_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "google/nano-banana-pro/text-to-image",
    "prompt": "<这里放本 Skill 生成的资产提示词原文>",
    "size": "1152*2048"
  }'
```

返回 `data.id`，然后轮询（见 §5）。图片通常 10 秒内出。

> **风格一致性**：§4.2 说的「提示词要描述画风风格」在 API 上同样成立，而且更要紧——批量跑的时候
> 一个模型跑完整部剧的资产，中途别换模型，否则同一部剧里画风会漂。

## 4. "视"阶段：跑分镜视频

| 场景 | model | 参考单价 |
|:--|:--|:--|
| 纯提示词直出 | `bytedance/seedance-2.0/text-to-video` | $0.096 / 秒 |
| 省钱档 | `bytedance/seedance-2.0-fast/text-to-video` | $0.076 / 秒 |
| 带首帧图（尾帧→首帧衔接） | `bytedance/seedance-2.0/image-to-video` | $0.096 / 秒 |
| 带角色/场景参考图 | `bytedance/seedance-2.0/reference-to-video` | $0.096 / 秒 |

```bash
curl -sS -X POST https://api.atlascloud.ai/api/v1/model/generateVideo \
  -H "Authorization: Bearer $ATLASCLOUD_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedance/seedance-2.0/text-to-video",
    "prompt": "<这里放本 Skill 生成的时间轴格式分镜提示词原文>",
    "shot_type": "multi",
    "size": "1080*1920",
    "duration": 15,
    "generate_audio": false
  }'
```

参数对照本 Skill 的既有约定：

| 本 Skill 的说法 | API 字段 | 备注 |
|:--|:--|:--|
| 每集 13-15 秒 | `duration` | 秒 |
| 一集 3-7 个镜头的时间轴提示词 | `shot_type` | 多镜头填 `multi`，单镜头填 `single`；**传空串会报错** |
| 上一集尾帧图 | `images` | 一行一个 URL 或 `data:image/png;base64,...`，多张用**换行**分隔 |
| 竖屏短剧 | `size` | 分辨率标签取**短边**：`1080*1920` 是竖屏 1080p，别写反 |

> §5.3 提到的真人审核限制在 API 侧同样存在，且策略更严：命中会让整个任务 `failed`，
> 处理思路和文档里写的一致——改提示词，不要硬试。

## 5. 轮询与取结果

```bash
curl -sS https://api.atlascloud.ai/api/v1/model/prediction/<id> \
  -H "Authorization: Bearer $ATLASCLOUD_API_KEY"
```

- `data.status`：`processing` → `completed`，失败为 `failed`，原因看 `data.error`
- 图约 10 秒，视频约 1-3 分钟；**建议每 5-6 秒轮一次**
- `data.outputs[0]` 是结果地址，**有效期约 24 小时，跑完立刻下载转存**——批量跑一整集尤其要注意，
  别等全部跑完才回头下载

## 6. 踩坑清单（实测）

| 现象 | 原因 | 处理 |
|:--|:--|:--|
| `400 invalid size` | 写成了 `2048x1152` | 改成 `2048*1152`（星号） |
| 视频任务 `failed`，`error_code 1012009` 提示音频版权 | Seedance 2.0 默认生成同步音频，配乐撞版权校验 | 加 `"generate_audio": false`。这条正好对上本 Skill 的技术自检项「只生成音效，不生成音乐」 |
| `shot_type` 报错 | 传了空串 | 多镜头 `multi`，单镜头 `single` |
| `403 error code 1010` | 脚本用了默认 User-Agent（如 Python urllib 的 `Python-urllib/3.x`） | 显式设一个 User-Agent |
| `401` | key 没配或已失效 | 重新申请；别把 key 写进提交文件 |
| 同一部剧画风漂 | 中途换了图像模型 | 一部剧的资产固定一个模型跑完 |

## 7. 建议的批量顺序

1. 先跑**角色正面全身照**，人工挑一版定妆 → 这一版定下来才继续
2. 用定妆图跑**四视图**和其余角色
3. 跑**场景**和**道具**
4. 按集跑**分镜视频**，第 N 集用第 N-1 集的尾帧图作 `images` 首帧
5. 每步跑完立刻下载转存，别攒到最后

失败连续 3 次就停下来问用户「是提示词的问题还是模型的问题」，不要无脑烧钱重试。
