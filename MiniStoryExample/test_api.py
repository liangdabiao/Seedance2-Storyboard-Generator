#!/usr/bin/env python3
"""快速测试Seedance API连接"""

import requests
import json

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 测试最简单的请求
payload = {
    "model": "doubao-seedance-1-0-lite-i2v-250428",
    "content": [
        {
            "type": "text",
            "text": "一只可爱的白色小兔子在花园里玩耍,水彩动画风格"
        }
    ],
    "duration": 5,
    "ratio": "16:9"
}

print("正在测试API连接...")
print(f"URL: {BASE_URL}/contents/generations/tasks")
print(f"Model: {payload['model']}")

try:
    response = requests.post(
        f"{BASE_URL}/contents/generations/tasks",
        headers=headers,
        json=payload,
        timeout=30
    )
    
    print(f"\n状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    
except Exception as e:
    print(f"\n错误: {e}")
    import traceback
    traceback.print_exc()
