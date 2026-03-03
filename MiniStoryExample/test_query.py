#!/usr/bin/env python3
"""测试查询任务状态"""

import requests
import json
import time

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 创建一个简单任务
payload = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {
            "type": "text",
            "text": "测试：一只可爱的白兔子"
        }
    ],
    "duration": 5,
    "ratio": "16:9"
}

print("创建测试任务...")
response = requests.post(
    f"{BASE_URL}/contents/generations/tasks",
    headers=headers,
    json=payload
)
task_id = response.json()["id"]
print(f"Task ID: {task_id}\n")

# 等待并查询
print("等待任务完成...")
for i in range(30):
    time.sleep(10)
    
    response = requests.get(
        f"{BASE_URL}/contents/generations/tasks/{task_id}",
        headers=headers
    )
    data = response.json()
    
    print(f"\n=== 第{i+1}次查询 ===")
    print(json.dumps(data, indent=2, ensure_ascii=False))
    
    status = data.get("status")
    if status in ["succeeded", "failed"]:
        print(f"\n最终状态: {status}")
        break
