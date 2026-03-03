#!/usr/bin/env python3
"""测试多图片输入"""

import requests
import json

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 测试1: 只有文本
print("=== 测试1: 纯文本 ===")
payload1 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "text", "text": "温暖水彩动画风格,小兔子在花园玩耍"}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload1)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
except Exception as e:
    print(f"错误: {e}\n")

# 测试2: 1张图片
print("=== 测试2: 1张图片 ===")
payload2 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "text", "text": "温暖水彩动画风格,小兔子在花园玩耍"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload2)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
except Exception as e:
    print(f"错误: {e}\n")

# 测试3: 2张图片（图片在前）
print("=== 测试3: 2张图片（图片在前）===")
payload3 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}},
        {"type": "text", "text": "温暖水彩动画风格,小兔子在花园玩耍"}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload3)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
except Exception as e:
    print(f"错误: {e}\n")

# 测试4: 3张图片
print("=== 测试4: 3张图片 ===")
payload4 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S02.png"}},
        {"type": "text", "text": "温暖水彩动画风格,小兔子在花园玩耍"}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload4)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
except Exception as e:
    print(f"错误: {e}\n")

# 测试5: 图片带role参数
print("=== 测试5: 图片带role参数 ===")
payload5 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "reference"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}, "role": "reference"},
        {"type": "text", "text": "温暖水彩动画风格,小兔子在花园玩耍"}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload5)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
except Exception as e:
    print(f"错误: {e}\n")
