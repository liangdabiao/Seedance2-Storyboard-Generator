#!/usr/bin/env python3
"""
Seedance 1.5 Pro 视频生成脚本
用于《茉莉花巷的小兔子》项目的批量视频生成
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
# MODEL_ID = "doubao-seedance-1-5-pro-251215"
MODEL_ID = "doubao-seedance-1-0-lite-i2v-250428"

# GitHub Raw文件基础URL
GITHUB_RAW_BASE = "https://raw.githubusercontent.com/guest1024/Seedance2-Storyboard-Generator/minitest/MiniStoryExample/artifacts"

@dataclass
class EpisodeConfig:
    """单集配置"""
    episode_id: str  # E01, E02, etc.
    title: str
    prompt: str
    images: List[str]  # 图片编号列表,如 ["C01", "S01"]
    duration: int = 12  # Seedance 1.5 Pro 最大12秒
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
        """
        创建视频生成任务
        
        Returns:
            task_id: 任务ID,失败返回None
        """
        # 构建content数组
        content = [
            {
                "type": "text",
                "text": config.prompt
            }
        ]
        
        # 添加图片参考
        for img_id in config.images[:4]:
            image_url = f"{GITHUB_RAW_BASE}/{img_id}.png"
            content.append({
                "type": "image_url",
                "image_url": {
                    "url": image_url
                },
                "role": "reference_image"
            })
        
        # 构建请求体
        payload = {
            "model": MODEL_ID,
            "content": content,
            "generate_audio": config.generate_audio,
            "ratio": config.ratio,
            "duration": config.duration,
            "watermark": config.watermark
        }
        
        try:
            print(f"[{config.episode_id}] 创建任务...")
            print(f"  提示词: {config.prompt[:100]}...")
            print(f"  图片参考: {', '.join(config.images)}")
            
            response = requests.post(
                f"{self.base_url}/contents/generations/tasks",
                headers=self.headers,
                json=payload,
                timeout=30
            )
            
            if response.status_code == 200:
                result = response.json()
                task_id = result.get("id")
                print(f"[{config.episode_id}] ✓ 任务创建成功: {task_id}")
                return task_id
            else:
                print(f"[{config.episode_id}] ✗ 任务创建失败:")
                print(f"  状态码: {response.status_code}")
                print(f"  响应: {response.text}")
                return None
                
        except Exception as e:
            print(f"[{config.episode_id}] ✗ 请求异常: {e}")
            return None
    
    def query_task(self, task_id: str) -> Optional[Dict]:
        """
        查询任务状态
        
        Returns:
            任务信息字典,失败返回None
        """
        try:
            response = requests.get(
                f"{self.base_url}/contents/generations/tasks/{task_id}",
                headers=self.headers,
                timeout=30
            )
            
            if response.status_code == 200:
                return response.json()
            else:
                print(f"  查询失败: {response.status_code} - {response.text}")
                return None
                
        except Exception as e:
            print(f"  查询异常: {e}")
            return None
    
    def wait_for_completion(
        self, 
        episode_id: str,
        task_id: str, 
        poll_interval: int = 10,
        timeout: int = 600
    ) -> Optional[str]:
        """
        轮询等待任务完成
        
        Args:
            episode_id: 集数ID(用于日志)
            task_id: 任务ID
            poll_interval: 轮询间隔(秒)
            timeout: 超时时间(秒)
        
        Returns:
            video_url: 视频下载链接,失败返回None
        """
        start_time = time.time()
        print(f"[{episode_id}] 等待任务完成...")
        
        while True:
            elapsed = time.time() - start_time
            if elapsed > timeout:
                print(f"[{episode_id}] ✗ 任务超时({timeout}秒)")
                return None
            
            task_info = self.query_task(task_id)
            if not task_info:
                time.sleep(poll_interval)
                continue
            
            status = task_info.get("status")
            print(f"[{episode_id}] 状态: {status} (已等待 {int(elapsed)}s)")
            
            if status == "succeeded":
                video_url = task_info.get("content", {}).get("video_url")
                if video_url:
                    print(f"[{episode_id}] ✓ 生成成功!")
                    return video_url
                else:
                    print(f"[{episode_id}] ✗ 未找到视频链接")
                    return None
            
            elif status == "failed":
                error = task_info.get("error", "未知错误")
                print(f"[{episode_id}] ✗ 任务失败: {error}")
                return None
            
            elif status in ["queued", "running"]:
                # 继续等待
                time.sleep(poll_interval)
            
            else:
                print(f"[{episode_id}] ✗ 未知状态: {status}")
                return None
    
    def download_video(self, video_url: str, save_path: str) -> bool:
        """
        下载视频文件
        
        Args:
            video_url: 视频下载链接
            save_path: 保存路径
        
        Returns:
            是否成功
        """
        try:
            print(f"  下载视频到: {save_path}")
            response = requests.get(video_url, stream=True, timeout=300)
            
            if response.status_code == 200:
                with open(save_path, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                print(f"  ✓ 下载完成")
                return True
            else:
                print(f"  ✗ 下载失败: {response.status_code}")
                return False
                
        except Exception as e:
            print(f"  ✗ 下载异常: {e}")
            return False


def load_episode_configs() -> List[EpisodeConfig]:
    """
    加载所有集数的配置
    
    注意: Seedance 1.5 Pro 最大支持12秒,因此将原15秒的分镜调整为12秒
    """
    configs = [
        # ===== 第一幕:起 (E01-E05) =====
        EpisodeConfig(
            episode_id="E01",
            title="茉莉花巷的小兔子",
            prompt="""温暖水彩动画风格,16:9横屏,12秒,明亮温馨的午后氛围。

0-3s: 航拍镜头缓缓降低,鸟瞰一条开满白色茉莉花的狭窄巷子,茉莉花藤蔓覆盖着古老的砖墙,白色花瓣散落在鹅卵石小路上,阳光透过花朵洒下斑驳光影。柔和的画笔质感,奶油色调和淡金色阳光。

3-5s: 推镜头聚焦到一座被茉莉花环绕的温馨小木屋,木门框上爬满茉莉花,暖黄色的灯光从窗户透出。门前小花园里摆放着精致的茉莉花糕和冰豆浆。

5-8s: 移镜头进入屋内,中景展示白色小兔子正在工作台前制作茉莉花糕,穿着淡黄色裙子,手腕戴着茉莉花手环。阳光从窗外洒在她身上,专注而温柔的表情。

8-10s: 特写切换,展示茉莉花糕上细腻的花瓣纹理、冰镇豆浆杯壁上的水珠、手工编织的茉莉花手环。

10-12s: 近景回到小兔子,她戴上新编的茉莉花手环,满足地微笑,镜头缓缓推近她的脸庞。背景虚化,茉莉花在前景轻轻摇曳。""",
            images=["C01", "S01", "S02", "P01", "P03"],
            duration=12
        ),
        
        EpisodeConfig(
            episode_id="E02",
            title="晴朗的午后",
            prompt="""延续E01温暖水彩动画风格,小兔子走出家门享受晴朗午后:

0-3s: 小兔子推开木门走出家门,全景展示她走在洒满阳光的小路上。穿着淡黄色裙子,茉莉花瓣随风飘落在她身边,她轻快地哼着歌,衣服上仿佛有阳光的味道。

3-5s: 跟随镜头,小兔子走向一棵巨大的老树,镜头从背后跟拍她的脚步。阳光透过树叶形成光斑,落在她身上,她的耳朵随着步伐轻轻摆动。

5-8s: 中景切换,小兔子蹲在大树底下,双手撑着膝盖,专注地观察地面上的蚂蚁搬家。推镜头缓缓靠近她好奇的表情,阳光在她脸上留下斑驳的光影。

8-10s: 俯拍特写地面,蚂蚁队伍井然有序地搬运食物。镜头再缓缓摇起看向小兔子,她眼中闪烁着童真的好奇光芒。

10-12s: 远景,小兔子站起身,拍拍裙子,转身走向远处树荫下的长石凳。镜头拉远,展示她小小的背影在阳光下。""",
            images=["C01", "S03", "S04"],
            duration=12
        ),
        
        EpisodeConfig(
            episode_id="E03",
            title="意外的野果",
            prompt="""延续温暖水彩画风,情绪从宁静转为惊吓:

0-2s: 小兔子躺在长石凳上,双手枕在脑后,表情安详宁静。镜头缓慢推近她的脸,眼睛闭上,睫毛轻颤,嘴角带着浅浅的笑意。

2-4s: 梦境画面(柔焦特效,边缘发光晕染),温馨的厨房场景,兔妈妈在炒菜,炉灶上冒着热气。画面中浮现金黄色的胡萝卜和翠绿的卷心菜。

4-6s: 突然镜头切回现实,仰拍视角看向树冠,一簇野果从树枝上松动。慢动作:野果开始掉落,镜头跟随野果下坠。

6-9s: 快速切换到小兔子脸部特写,野果"咚"的一声砸在她额头正中,瞬间红肿起一大片。她眼睛猛地睁开,表情从安详变为震惊疼痛,画面微微震动。

9-12s: 中景,小兔子从石凳上坐起来,一只手捂着红肿的额头,小嘴一撇,眼泪在眼眶里打转。镜头缓缓推近,委屈而可怜的表情。""",
            images=["C01", "C03", "S04", "P05"],
            duration=12
        ),
        
        EpisodeConfig(
            episode_id="E04",
            title="道歉的小狐狸",
            prompt="""延续画风,引入新角色小狐狸,情绪从伤心转为心动:

0-3s: 小兔子还在哭泣,捂着额头,委屈的表情。镜头固定在中景,她的肩膀轻轻抽动,眼泪顺着脸颊滑落。背景大树后方,隐约有橙红色的身影在移动。

3-5s: 镜头切换到大树后方,小狐狸从树干后探出头,表情满脸愧疚,脸颊微微泛红,耳朵耷拉下来。他看着正在哭泣的小兔子,眼神紧张不安。

5-7s: 全景,小狐狸鼓起勇气从树后走出来,双手在身前不好意思地搓动,脚步有些犹豫。推镜头跟随他走向小兔子。

7-10s: 中近景,小狐狸站在小兔子面前,红着脸低头道歉。他的嘴巴动着说话:"小兔子,对不起。是我不好,刚刚摘野果子的时候不小心砸到你了。"声音温柔好听,镜头缓慢推近他的脸。

10-12s: 特写切换到小狐狸的侧脸,阳光勾勒出他精致的轮廓线,眉眼弯弯,睫毛在脸上投下浅浅的影子。画面微微柔焦,背景是温暖的光晕。""",
            images=["C01", "C05", "C07", "S04"],
            duration=12
        ),
        
        EpisodeConfig(
            episode_id="E05",
            title="好听的声音",
            prompt="""延续画风,表现小兔子被小狐狸的声音和外表打动,首次心动:

0-2s: 镜头切换到小兔子的视角,仰视看向小狐狸。她的眼睛从泪眼模糊慢慢变得清晰,眼神专注地看着小狐狸。擦掉脸上的泪水,嘴巴微微张开。

2-4s: 特写小狐狸的眼睛,琥珀色的眼瞳温柔而清澈,眉眼弯成新月形状。镜头极其细腻,睫毛根根分明,眼中有细碎的光点闪烁。

4-7s: 创意特效镜头,小狐狸的声音可视化:温柔的声波化作淡金色和浅蓝色的水流波纹,从小狐狸的方向流向小兔子,环绕在她周围。小兔子的表情从悲伤彻底转为惊讶和心动。

7-10s: 双人中景,小狐狸温柔地看着小兔子,小兔子呆呆地看着小狐狸,两人之间有微妙的情感张力。背景开始虚化,浮现出淡淡的粉色光晕。

10-12s: 特写小兔子的脸,她的心跳加速(视觉化:淡粉色心形光晕从胸口轻轻向外扩散),脸颊微微泛红,眼神有些躲闪但又忍不住偷看小狐狸。""",
            images=["C01", "C05", "FX02", "FX01"],
            duration=12
        ),
    ]
    
    return configs


def generate_single_episode(api: SeedanceAPI, config: EpisodeConfig, output_dir: str = "./videos") -> bool:
    """
    生成单集视频
    
    Args:
        api: Seedance API客户端
        config: 集数配置
        output_dir: 输出目录
    
    Returns:
        是否成功
    """
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


def main():
    """主函数"""
    print("""
╔══════════════════════════════════════════════════════════╗
║        Seedance 1.5 Pro 视频生成器                       ║
║        《茉莉花巷的小兔子》                               ║
╚══════════════════════════════════════════════════════════╝
""")
    
    # 初始化API客户端
    api = SeedanceAPI(API_KEY)
    
    # 加载配置
    configs = load_episode_configs()
    print(f"已加载 {len(configs)} 个集数配置\n")
    
    # 测试模式:仅生成第一集
    print("【测试模式】仅生成 E01 用于测试\n")
    test_config = configs[0]
    
    success = generate_single_episode(api, test_config)
    
    if success:
        print("\n✓ 测试成功! 视频已保存到 ./videos/ 目录")
        print("\n若要生成所有集数,请修改代码调用 generate_all_episodes()")
    else:
        print("\n✗ 测试失败,请检查错误信息")


def generate_all_episodes():
    """生成所有集数(批量模式)"""
    api = SeedanceAPI(API_KEY)
    configs = load_episode_configs()
    
    results = []
    for config in configs:
        success = generate_single_episode(api, config)
        results.append({
            "episode_id": config.episode_id,
            "title": config.title,
            "success": success
        })
        
        # 避免频繁请求,休息30秒
        if success and config != configs[-1]:
            print("休息30秒后继续下一集...")
            time.sleep(30)
    
    # 打印汇总
    print("\n" + "="*60)
    print("生成汇总:")
    print("="*60)
    for result in results:
        status = "✓" if result["success"] else "✗"
        print(f"{status} [{result['episode_id']}] {result['title']}")


if __name__ == "__main__":
    main()
