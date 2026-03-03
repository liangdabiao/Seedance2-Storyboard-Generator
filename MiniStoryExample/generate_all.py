#!/usr/bin/env python3
"""
Seedance 1.5 Pro 批量视频生成脚本
从 episode_configs_full.json 加载配置并生成所有20集视频
"""

import requests
import time
import json
import os
from typing import List, Dict, Optional
from dataclasses import dataclass

# API配置
API_KEY = "942c826a-dfc2-48ee-9c0f-e06c82d9dfee"
BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"
# MODEL_ID = "doubao-seedance-1-5-pro-251215"  # 旧模型:直接使用素材作为首尾帧
MODEL_ID = "doubao-seedance-1-0-lite-i2v-250428"  # 新模型:支持生成连贯的首尾帧

# GitHub Raw文件基础URL
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

@dataclass
class EpisodeConfig:
    """单集配置"""
    episode_id: str
    title: str
    prompt: str
    images: List[str]
    duration: int = 12
    ratio: str = "16:9"
    generate_audio: bool = True
    watermark: bool = False


class SeedanceAPI:
    """Seedance API 客户端"""
    
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = BASE_URL
        self.headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }
    
    def create_task(self, config: EpisodeConfig) -> Optional[str]:
        """创建视频生成任务"""
        # 构建请求内容
        content = [
            {"type": "text", "text": config.prompt}
        ]
        
        # 添加多张图片策略：
        # 使用 doubao-seedance-1-0-lite-i2v-250428 模型
        # 该模型使用 reference_image 角色,素材图片仅作为参考
        # AI会根据提示词生成连贯的首尾帧,而非直接使用素材
        
        if config.images:
            num_images = len(config.images)
            
            # 使用所有图片作为参考图(最多4张,API限制)
            for img_id in config.images[:4]:
                image_url = f"{GITHUB_RAW_BASE}/{img_id}.png"
                content.append({
                    "type": "image_url",
                    "image_url": {"url": image_url},
                    "role": "reference_image"
                })
            
            used_count = min(num_images, 4)
            print(f"  参考图片: {', '.join(config.images[:4])} (共{used_count}张)")
            
            if num_images > 4:
                skipped = num_images - 4
                skipped_list = config.images[4:]
                print(f"  注意: 已跳过 {skipped} 张图片 {skipped_list}（API限制最多4张）")
        
        # 构建请求payload
        payload = {
            "model": MODEL_ID,
            "content": content,
            "generate_audio": config.generate_audio,
            "ratio": config.ratio,
            "duration": config.duration,
            "watermark": config.watermark
        }
        
        try:
            response = requests.post(
                f"{self.base_url}/contents/generations/tasks",
                headers=self.headers,
                json=payload,
                timeout=30
            )
            response.raise_for_status()
            data = response.json()
            
            task_id = data.get("id")  # API返回的是 "id" 字段
            print(f"✓ 任务创建成功, Task ID: {task_id}")
            return task_id
            
        except requests.exceptions.HTTPError as e:
            print(f"✗ HTTP错误: {e}")
            try:
                error_detail = response.json()
                print(f"   错误详情: {json.dumps(error_detail, indent=2, ensure_ascii=False)}")
            except:
                print(f"   响应内容: {response.text}")
            return None
        except Exception as e:
            print(f"✗ 任务创建失败: {e}")
            return None
    
    def query_task(self, task_id: str) -> Optional[Dict]:
        """查询任务状态"""
        try:
            response = requests.get(
                f"{self.base_url}/contents/generations/tasks/{task_id}",
                headers=self.headers,
                timeout=30
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            print(f"✗ 查询失败: {e}")
            return None
    
    def wait_for_completion(self, episode_id: str, task_id: str, poll_interval: int = 10, timeout: int = 600) -> Optional[str]:
        """等待任务完成"""
        start_time = time.time()
        
        while True:
            elapsed = time.time() - start_time
            if elapsed > timeout:
                print(f"✗ [{episode_id}] 超时 ({timeout}秒)")
                return None
            
            result = self.query_task(task_id)
            if not result:
                time.sleep(poll_interval)
                continue
            
            status = result.get("status")
            print(f"  [{episode_id}] 状态: {status} (已等待 {int(elapsed)}秒)")
            
            if status == "succeeded":
                # 视频URL在content字段中
                content = result.get("content", {})
                video_url = content.get("video_url")
                if video_url:
                    print(f"✓ [{episode_id}] 生成成功!")
                    return video_url
                else:
                    print(f"✗ [{episode_id}] 未返回视频URL")
                    print(f"   响应: {json.dumps(result, indent=2, ensure_ascii=False)}")
                    return None
            
            elif status == "failed":
                error = result.get("error", "未知错误")
                print(f"✗ [{episode_id}] 生成失败: {error}")
                return None
            
            time.sleep(poll_interval)
    
    def download_video(self, video_url: str, save_path: str) -> bool:
        """下载视频"""
        try:
            print(f"  下载中: {save_path}")
            response = requests.get(video_url, stream=True, timeout=120)
            response.raise_for_status()
            
            with open(save_path, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)
            
            file_size = os.path.getsize(save_path) / (1024 * 1024)
            print(f"✓ 下载完成: {save_path} ({file_size:.2f} MB)")
            return True
            
        except Exception as e:
            print(f"✗ 下载失败: {e}")
            return False


def load_configs_from_json(json_file: str = "episode_configs_full.json") -> List[EpisodeConfig]:
    """从JSON文件加载所有配置"""
    if not os.path.exists(json_file):
        print(f"错误: 找不到配置文件 {json_file}")
        return []
    
    try:
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        configs = []
        for ep in data['episodes']:
            config = EpisodeConfig(
                episode_id=ep['id'],  # JSON中使用 'id' 字段
                title=ep['title'],
                prompt=ep['prompt'],
                images=ep['images'],
                duration=data['duration_per_episode']  # 统一使用12秒
            )
            configs.append(config)
        
        print(f"✓ 成功加载 {len(configs)} 个集数配置\n")
        return configs
    
    except Exception as e:
        print(f"错误: 加载配置文件失败 - {e}")
        return []


def generate_single_episode(api: SeedanceAPI, config: EpisodeConfig, output_dir: str = "./videos") -> bool:
    """生成单集视频"""
    print(f"\n{'='*60}")
    print(f"开始生成: [{config.episode_id}] {config.title}")
    print(f"{'='*60}")
    
    # 创建任务
    task_id = api.create_task(config)
    if not task_id:
        return False
    
    # 等待完成
    video_url = api.wait_for_completion(config.episode_id, task_id)
    if not video_url:
        return False
    
    # 下载视频
    os.makedirs(output_dir, exist_ok=True)
    save_path = os.path.join(output_dir, f"{config.episode_id}_{config.title}.mp4")
    success = api.download_video(video_url, save_path)
    
    if success:
        print(f"[{config.episode_id}] ✓ 全部完成!\n")
    
    return success


def generate_all_episodes(output_dir: str = "./videos", delay: int = 30):
    """批量生成所有集数"""
    print("""
╔══════════════════════════════════════════════════════════╗
║        Seedance 1.5 Pro 批量视频生成器                   ║
║        《茉莉花巷的小兔子》                               ║
╚══════════════════════════════════════════════════════════╝
""")
    
    # 初始化
    api = SeedanceAPI(API_KEY)
    configs = load_configs_from_json()
    
    if not configs:
        print("错误: 没有加载到配置")
        return
    
    # 统计
    results = []
    success_count = 0
    failed_count = 0
    
    # 逐个生成
    for i, config in enumerate(configs, 1):
        print(f"\n进度: {i}/{len(configs)}")
        
        success = generate_single_episode(api, config, output_dir)
        results.append({
            "episode_id": config.episode_id,
            "title": config.title,
            "success": success
        })
        
        if success:
            success_count += 1
        else:
            failed_count += 1
        
        # 延迟(避免频繁请求,最后一集不需要延迟)
        if i < len(configs):
            print(f"\n休息 {delay} 秒后继续...")
            time.sleep(delay)
    
    # 打印汇总
    print("\n" + "="*60)
    print("生成汇总:")
    print("="*60)
    for result in results:
        status = "✓" if result["success"] else "✗"
        print(f"{status} [{result['episode_id']}] {result['title']}")
    
    print(f"\n总计: 成功 {success_count} / 失败 {failed_count} / 总数 {len(configs)}")
    print(f"视频保存目录: {os.path.abspath(output_dir)}")


if __name__ == "__main__":
    generate_all_episodes()
