#!/bin/bash
set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
BACK_DIR="$PROJECT_ROOT/back"
FRONT_DIR="$PROJECT_ROOT/front"

echo "=========================================="
echo "  拥抱妈妈·爱在平安 - 项目构建脚本"
echo "=========================================="

# ---------- 后端构建 ----------
echo ""
echo "[1/2] 构建后端 (Spring Boot)..."
cd "$BACK_DIR"
mvn clean package -DskipTests -q
echo "后端构建完成: $BACK_DIR/target/"

# ---------- 前端构建 ----------
echo ""
echo "[2/2] 构建前端 (微信小程序 TypeScript)..."
cd "$FRONT_DIR"

# 检查 Node.js 是否可用
if command -v node >/dev/null 2>&1 && command -v npx >/dev/null 2>&1; then
  # 确保依赖已安装
  if [ ! -d "node_modules" ]; then
    echo "  安装前端依赖..."
    npm install --silent
  fi
  npx tsc
  echo "前端构建完成"
else
  echo "  跳过前端编译 (需要 Node.js + npx)"
  echo "  请使用微信开发者工具打开 $FRONT_DIR 进行编译"
fi

# ---------- 完成 ----------
echo ""
echo "=========================================="
echo "  构建全部完成!"
echo "=========================================="
echo ""
echo "后端 JAR:  $BACK_DIR/target/"
echo "前端目录:  $FRONT_DIR (用微信开发者工具打开)"
