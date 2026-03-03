#!/usr/bin/env python3
"""测试单集生成(E01)验证新模型配置"""

import sys
sys.path.insert(0, '/data/Seedance2-Storyboard-Generator/MiniStoryExample')

from generate_all import SeedanceAPI, EpisodeConfig

# 测试E01配置
test_config = EpisodeConfig(
    episode_id="E01_TEST",
    title="测试_茉莉花巷的小兔子",
    prompt="""温暖水彩动画风格,16:9横屏,12秒,明亮温馨的午后氛围。
航拍镜头缓缓降低,鸟瞰一条开满白色茉莉花的狭窄巷子,茉莉花藤蔓覆盖着古老的砖墙。
推镜头聚焦到一座被茉莉花环绕的温馨小木屋。
移镜头进入屋内,中景展示白色小兔子正在工作台前制作茉莉花糕。""",
    images=["C01", "S01", "S02", "P01", "P03"],
    duration=12
)

print("="*60)
print("测试新模型配置: doubao-seedance-1-0-lite-i2v-250428")
print("="*60)
print(f"集数: {test_config.episode_id}")
print(f"标题: {test_config.title}")
print(f"素材图片: {test_config.images}")
print(f"图片数量: {len(test_config.images)}")
print()

# 创建API客户端
api = SeedanceAPI("942c826a-dfc2-48ee-9c0f-e06c82d9dfee")

# 仅测试任务创建(不等待完成)
print("创建任务...")
task_id = api.create_task(test_config)

if task_id:
    print(f"\n✓ 测试成功!")
    print(f"Task ID: {task_id}")
    print(f"\n说明:")
    print(f"- 使用模型: doubao-seedance-1-0-lite-i2v-250428")
    print(f"- 素材图片将作为reference_image(参考)")
    print(f"- AI将生成连贯的首尾帧,而非直接使用素材")
else:
    print(f"\n✗ 测试失败")
    print(f"请检查错误信息")
