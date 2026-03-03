#!/bin/bash
# 视频合并脚本 - 将所有20集视频合并为完整版

set -e

cd /data/Seedance2-Storyboard-Generator/MiniStoryExample/videos

echo "======================================"
echo "   视频合并工具"
echo "   《茉莉花巷的小兔子》"
echo "======================================"
echo ""

# 检查视频数量
VIDEO_COUNT=$(ls -1 E*.mp4 2>/dev/null | wc -l)
echo "检测到视频文件: $VIDEO_COUNT 个"

if [ $VIDEO_COUNT -lt 20 ]; then
    echo "⚠️  警告: 只找到 $VIDEO_COUNT 个视频，预期应有20个"
    echo "请确认所有视频都已生成完成"
    read -p "是否继续合并现有视频? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "取消合并"
        exit 1
    fi
fi

echo ""
echo "=== 视频列表 ==="
ls -lh E*.mp4 | awk '{print $9, $5}'

echo ""
echo "=== 开始合并 ==="

# 清理旧的文件列表
rm -f filelist.txt

# 按照E01-E20顺序生成文件列表
for i in $(seq -w 01 20); do
    # 查找匹配的文件
    FILE=$(ls E${i}_*.mp4 2>/dev/null | head -1)
    if [ -n "$FILE" ]; then
        echo "file '$FILE'" >> filelist.txt
        echo "添加: $FILE"
    else
        echo "⚠️  缺失: E${i}"
    fi
done

echo ""
echo "=== 执行FFmpeg合并 ==="

# 检查ffmpeg是否安装
if ! command -v ffmpeg &> /dev/null; then
    echo "❌ 错误: 未安装FFmpeg"
    echo "请先安装: apt-get install ffmpeg"
    exit 1
fi

# 执行合并
OUTPUT_FILE="茉莉花巷的小兔子_完整版.mp4"
ffmpeg -f concat -safe 0 -i filelist.txt -c copy "$OUTPUT_FILE" -y

if [ -f "$OUTPUT_FILE" ]; then
    echo ""
    echo "======================================"
    echo "✅ 合并成功!"
    echo "======================================"
    echo "输出文件: $OUTPUT_FILE"
    ls -lh "$OUTPUT_FILE"
    
    # 显示视频信息
    echo ""
    echo "=== 视频信息 ==="
    ffprobe -v error -show_entries format=duration,size,bit_rate -of default=noprint_wrappers=1 "$OUTPUT_FILE" 2>/dev/null || echo "无法读取视频信息"
    
    echo ""
    echo "位置: $(pwd)/$OUTPUT_FILE"
else
    echo "❌ 合并失败"
    exit 1
fi

# 清理临时文件
rm -f filelist.txt

echo ""
echo "======================================"
echo "合并完成！"
echo "======================================"
