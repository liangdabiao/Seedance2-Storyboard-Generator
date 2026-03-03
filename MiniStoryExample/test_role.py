#!/usr/bin/env python3
"""测试role参数的多图片输入"""

import requests
import json

API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {API_KEY}"
}

# 测试1: first_frame + 普通图
print("=== 测试1: first_frame + 无role图 ===")
payload1 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "first_frame"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}},
        {"type": "text", "text": "温暖水彩动画风格"}
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

# 测试2: first_frame + last_frame
print("=== 测试2: first_frame + last_frame ===")
payload2 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "first_frame"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C02.png"}, "role": "last_frame"},
        {"type": "text", "text": "温暖水彩动画风格"}
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

# 测试3: 多个reference_image
print("=== 测试3: 多个reference_image ===")
payload3 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S02.png"}, "role": "reference_image"},
        {"type": "text", "text": "温暖水彩动画风格"}
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

# 测试4: first_frame + 多个reference_image
print("=== 测试4: first_frame + 多个reference_image ===")
payload4 = {
    "model": "doubao-seedance-1-5-pro-251215",
    "content": [
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/C01.png"}, "role": "first_frame"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S01.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/S02.png"}, "role": "reference_image"},
        {"type": "image_url", "image_url": {"url": f"{GITHUB_RAW_BASE}/P01.png"}, "role": "reference_image"},
        {"type": "text", "text": "温暖水彩动画风格,小兔子在茉莉花巷的家中制作茉莉花糕"}
    ],
    "duration": 5,
    "ratio": "16:9"
}

try:
    response = requests.post(f"{BASE_URL}/contents/generations/tasks", headers=headers, json=payload4)
    print(f"状态码: {response.status_code}")
    print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}\n")
    if response.status_code == 200:
        print("✅ 成功！此组合可用于多图片输入")
except Exception as e:
    print(f"错误: {e}\n")
