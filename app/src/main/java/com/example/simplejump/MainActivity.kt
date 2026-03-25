package com.example.simplejump

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.simplejump.ui.MainScreen
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "SimpleJump"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate")

        try {
            setContent {
                SimpleJumpAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
            Log.d(TAG, "Content set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }
}
//com/example/simplejump/
//├── MainActivity.kt                           # 主Activity（简化）
//├── ui/
//│   ├── MainScreen.kt                        # 主界面组件
//│   ├── components/
//│   │   ├── PlatformSelector.kt              # 平台选择器
//│   │   ├── SearchInputField.kt              # 搜索输入框
//│   │   ├── ActionButtons.kt                 # 功能按钮组
//│   │   ├── HotSearchTags.kt                 # 热门搜索标签
//│   │   └── StatusCard.kt                    # 状态显示卡片
//│   └── theme/                               # 主题相关
//├── platform/
//│   ├── SearchPlatform.kt                    # 平台枚举定义
//│   ├── SearchProvider.kt                    # 搜索提供者接口
//│   ├── PlatformManager.kt                   # 平台管理器（调度中心）
//│   ├── xiaohongshu/
//│   │   ├── XhsSchemes.kt                    # 小红书URL配置
//│   │   └── XhsSearchProvider.kt             # 小红书搜索实现
//│   ├── zhihu/
//│   │   ├── ZhihuSchemes.kt                  # 知乎URL配置
//│   │   └── ZhihuSearchProvider.kt           # 知乎搜索实现
//│   └── douyin/                              # 预留抖音支持
//│       ├── DouyinSchemes.kt
//│       └── DouyinSearchProvider.kt
//├── data/
//│   ├── SearchResult.kt                      # 搜索结果数据类
//│   └── PlatformConfig.kt                    # 平台配置数据类
//└── utils/
//├── AppUtils.kt                          # 应用工具类
//├── ClipboardUtils.kt                    # 剪贴板工具类
//└── IntentUtils.kt                       # Intent工具类