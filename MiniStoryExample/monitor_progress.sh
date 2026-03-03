#!/bin/bash
# 监控视频生成进度的脚本

echo "======================================"
echo "   视频生成进度监控"
echo "   $(date)"
echo "======================================"

# 检查进程是否还在运行
PID=$(ps aux | grep "[g]enerate_all.py" | awk '{print $2}')
if [ -z "$PID" ]; then
    echo "❌ 生成进程未运行"
else
    echo "✅ 生成进程运行中 (PID: $PID)"
fi

echo ""
echo "=== 最新日志 (最后30行) ==="
tail -n 30 /data/Seedance2-Storyboard-Generator/MiniStoryExample/generation_v2.log

echo ""
echo "=== 已生成的视频文件 ==="
if [ -d "/data/Seedance2-Storyboard-Generator/MiniStoryExample/videos" ]; then
    VIDEO_COUNT=$(ls -1 /data/Seedance2-Storyboard-Generator/MiniStoryExample/videos/*.mp4 2>/dev/null | wc -l)
    echo "已完成: $VIDEO_COUNT / 20"
    echo ""
    ls -lht /data/Seedance2-Storyboard-Generator/MiniStoryExample/videos/*.mp4 2>/dev/null | head -10
    echo ""
    du -sh /data/Seedance2-Storyboard-Generator/MiniStoryExample/videos/ 2>/dev/null
else
    echo "videos目录不存在"
fi

echo ""
echo "======================================"
echo "使用方法："
echo "  定期运行: bash monitor_progress.sh"
echo "  或持续监控: watch -n 60 bash monitor_progress.sh"
echo "======================================"
