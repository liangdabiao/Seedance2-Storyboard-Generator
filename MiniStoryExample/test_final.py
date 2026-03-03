#!/usr/bin/env python3
"""测试最终的多图片方案"""

import sys
sys.path.insert(0, '/data/Seedance2-Storyboard-Generator/MiniStoryExample')

from generate_all import SeedanceAPI, EpisodeConfig, API_KEY

# 创建API客户端
api = SeedanceAPI(API_KEY)

# 测试配置1: 5张图片（E01的配置）
test_config = EpisodeConfig(
    episode_id="TEST",
    title="测试多图片",
    prompt="温暖水彩动画风格,小兔子在茉莉花巷的家中,窗外茉莉花盛开,阳光洒进屋内",
    images=["C01", "S01", "S02", "P01", "P03"],  # 5张图
    duration=5
)

print("=== 测试E01配置（5张图片）===")
print(f"图片列表: {test_config.images}")
print()

task_id = api.create_task(test_config)
if task_id:
    print(f"\n✅ 任务创建成功! Task ID: {task_id}")
    print("多图片输入方案验证成功！")
else:
    print("\n❌ 任务创建失败")
