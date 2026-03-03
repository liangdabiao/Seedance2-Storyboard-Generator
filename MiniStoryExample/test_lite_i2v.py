#!/usr/bin/env python3
"""测试 doubao-seedance-1-0-lite-i2v-250428 模型"""

import requests
import json

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"
MODEL_ID = "doubao-seedance-1-0-lite-i2v-250428"
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 测试:使用多张 reference_image
print("=== 测试: doubao-seedance-1-0-lite-i2v-250428 + 多张reference_image ===")
payload = {
    "model": MODEL_ID,
    "content": [
        {"type": "text", "text": "温暖水彩动画风格,16:9横屏,12秒,明亮温馨的午后氛围。小兔子在茉莉花巷的家中制作茉莉花糕。"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/P01.png"}, "role": "reference_image"}
    ],
    "duration": 12,
    "ratio": "16:9",
    "generate_audio": True,
    "watermark": False
}

try:
    print(f"发送请求...")
    print(f"模型: {MODEL_ID}")
    print(f"参考图片: C01, S01, P01")
    
    response = requests.post(
        f"{BASE_URL}/contents/generations/tasks",
        headers=headers,
        json=payload,
        timeout=30
    )
    
    print(f"\n状态码: {response.status_code}")
    
    if response.status_code == 200:
        result = response.json()
        task_id = result.get("id")
        print(f"✓ 任务创建成功!")
        print(f"Task ID: {task_id}")
        print(f"\n完整响应:")
        print(json.dumps(result, indent=2, ensure_ascii=False))
    else:
        print(f"✗ 任务创建失败")
        print(f"响应: {response.text}")
        
except Exception as e:
    print(f"✗ 错误: {e}")
