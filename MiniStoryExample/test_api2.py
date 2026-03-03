#!/usr/bin/env python3
"""测试Seedance 1.5 Pro API"""

import requests
import json

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 测试简单的文生视频
payload1 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {
            "type": "text",
            "text": "温暖水彩动画风格,一只可爱的白色小兔子在花园里玩耍"
        }
    ],
    "duration": 5,
    "ratio": "16:9"
}

print("=== 测试1: 纯文本生成 ===")
try:
    response = requests.post(
        f"{BASE_URL}/contents/generations/tasks",
        headers=headers,
        json=payload1,
        timeout=30
    )
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
except Exception as e:
    print(f"错误: {e}")

# 测试图生视频
print("\n\n=== 测试2: 图生视频 ===")
payload2 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {
            "type": "text",
            "text": "温暖水彩动画风格,一只可爱的白色小兔子在花园里玩耍"
        },
        {
            "type": "image_url",
            "image_url": {
                "url": f"{GITHUB_RAW_BASE}/C01.png"
            }
        }
    ],
    "duration": 5,
    "ratio": "16:9",
    "generate_audio": True,
    "watermark": False
}

try:
    response = requests.post(
        f"{BASE_URL}/contents/generations/tasks",
        headers=headers,
        json=payload2,
        timeout=30
    )
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
except Exception as e:
    print(f"错误: {e}")
