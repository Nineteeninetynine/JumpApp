# 🚀 JumpApp (SimpleJump)

基于 **Jetpack Compose** 的 Android 跨平台智能搜索跳转应用，支持在小红书、知乎、B站、微博、抖音之间快速搜索跳转，并集成 **DeepSeek AI** 提供智能搜索增强功能。

## ✨ 功能特性

### 🔍 基础搜索跳转
- 支持 **小红书、知乎、B站、微博、抖音** 五大平台
- 智能搜索 — 直接跳转到目标平台的搜索结果页
- 打开搜索页 — 打开目标平台的搜索输入界面
- 复制并打开 — 将关键词复制到剪贴板并打开应用
- 打开主页 — 直接打开目标应用主页
- 热门搜索标签 — 一键填入热搜关键词

### 🤖 AI 智能增强（DeepSeek 集成）

#### 方案一：智能搜索意图分析
输入自然语言描述（如"想看减肥食谱"），AI 自动分析并推荐最佳搜索平台和优化关键词，一键采纳。

#### 方案二：多平台搜索词优化
针对同一搜索词，AI 为每个平台生成最适合的优化关键词（如小红书→"减肥食谱 低卡餐"，知乎→"科学减肥方法"）。

#### 方案三：搜索历史分析与个性化推荐
基于 Room 数据库持久化搜索历史，当历史 ≥ 5 条时，AI 分析兴趣偏好并生成个性化推荐搜索词，替代静态热搜。

#### 方案四：对话式搜索助手
底部弹出的 AI 聊天窗口，支持多轮对话 + SSE 流式输出。对话中 AI 解析出跳转意图时展示「立即跳转」按钮，一键执行。

## ��️ 项目架构

```
app/src/main/java/com/example/simplejump/
├── ai/                          # AI 模块
│   ├── api/
│   │   ├── ApiKeyManager.kt     # API Key 加密存储（EncryptedSharedPreferences）
│   │   └── DeepSeekApiClient.kt # DeepSeek API 客户端（OkHttp + SSE）
│   ├── model/
│   │   └── DeepSeekModels.kt    # 请求/响应数据模型
│   ├── repository/
│   │   └── SearchHistoryRepository.kt  # 搜索历史仓库
│   ├── service/
│   │   ├── IntentAnalysisService.kt    # 方案一：意图分析
│   │   ├── KeywordOptimizeService.kt   # 方案二：关键词优化
│   │   ├── RecommendationService.kt    # 方案三：个性化推荐
│   │   └── ChatAssistantService.kt     # 方案四：对话助手
│   └── ui/
│       ├── ApiKeySettingsScreen.kt     # API Key 设置界面
│       ├── ChatAssistantSheet.kt       # 对话式助手 BottomSheet
│       └── ChatViewModel.kt           # 对话 ViewModel
├── data/
│   └── db/
│       ├── AppDatabase.kt       # Room 数据库
│       ├── SearchHistory.kt     # 搜索历史 Entity
│       └── SearchHistoryDao.kt  # 数据访问对象
├── platform/
│   ├── PlatformConfig.kt        # 平台统一配置（Scheme、包名等）
│   ├── PlatformManager.kt       # 平台管理器
│   ├── SearchPlatform.kt        # 平台枚举
│   └── providers/               # 各平台 Provider 实现
├── ui/
│   ├── MainScreen.kt            # 主界面（Compose）
│   ├── MainViewModel.kt         # 主 ViewModel
│   ├── components/              # UI 组件
│   └── theme/                   # Material3 主题
└── utils/
    ├── IntentUtils.kt           # Intent 跳转工具
    └── ClipboardUtils.kt        # 剪贴板工具
```

## 🛠️ 技术栈

| 技术 | 用途 |
|------|------|
| **Kotlin** | 开发语言 |
| **Jetpack Compose** | 声明式 UI |
| **Material 3** | UI 设计规范 |
| **ViewModel + StateFlow** | 状态管理 |
| **Room** | 本地搜索历史持久化 |
| **OkHttp** | HTTP 客户端 + SSE 流式请求 |
| **Gson** | JSON 序列化/反序列化 |
| **EncryptedSharedPreferences** | API Key 加密存储 |
| **DeepSeek API** | AI 大模型服务 |

## 📦 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK 34
- Minimum SDK: 24 (Android 7.0)

### 构建运行

```bash
# 克隆项目
git clone <repository-url>
cd JumpApp-main

# 使用 Gradle 构建
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

### 配置 AI 功能

1. 打开应用，点击右上角 **⚙️ 设置** 按钮
2. 在 API Key 设置对话框中输入你的 DeepSeek API Key
3. API Key 将通过 `EncryptedSharedPreferences` 安全加密存储在本地
4. 配置完成后即可使用所有 AI 增强功能

> ⚠️ **注意**：AI 功能需要有效的 DeepSeek API Key。未配置时，AI 相关按钮将自动隐藏/禁用，基础搜索跳转功能不受影响。

## 📱 使用说明

### 基础搜索
1. 在搜索框输入关键词
2. 选择目标平台（小红书/知乎/B站/微博/抖音）
3. 点击「搜索」按钮跳转

### AI 意图分析
1. 输入自然语言描述（如"想看美食视频"）
2. 点击「🤖 AI 分析」按钮
3. 查看 AI 推荐的平台和优化关键词
4. 点击「采纳」一键应用，或忽略继续手动操作

### AI 对话助手
1. 点击右下角「🤖 AI 助手」悬浮按钮
2. 在对话窗口中用自然语言描述需求
3. AI 理解意图后展示「立即跳转」按钮
4. 支持多轮对话和流式输出

## 📄 License

本项目仅供学习交流使用。
