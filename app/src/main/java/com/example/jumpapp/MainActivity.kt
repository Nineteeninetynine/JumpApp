package com.example.simplejump

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.ui.theme.SimpleJumpAppTheme
import java.net.URLEncoder
import java.io.UnsupportedEncodingException
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // 用于存储搜索内容的状态
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var lastSearchResult by remember { mutableStateOf("") }

    //  状态变量
    var selectedPlatform by remember { mutableStateOf(SearchPlatform.XIAOHONGSHU) }
    var isPlatformMenuExpanded by remember { mutableStateOf(false) }
    // 用于重置搜索状态的效果
    LaunchedEffect(isSearching) {
        if (isSearching) {
            Handler(Looper.getMainLooper()).postDelayed({
                isSearching = false
            }, 3000)
        }
    }

    Log.d("SimpleJump", "MainScreen composing")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Emoji图标 - 保持不变
        Text(text = "🔍", fontSize = 60.sp, modifier = Modifier.padding(bottom = 24.dp))

        // 🔄 修改标题 - 动态显示当前平台
        Text(
            text = "${selectedPlatform.displayName}搜索助手",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

//        // 主标题
//        Text(
//            text = "小红书搜索助手",
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )

        // 副标题
        Text(
            text = "输入搜索内容，尝试直接跳转到搜索结果",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        // 平台选择器
        PlatformSelector(
            selectedPlatform = selectedPlatform,
            onPlatformSelected = { selectedPlatform = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        // 显示上次搜索结果
        if (lastSearchResult.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        lastSearchResult.contains("成功") -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        lastSearchResult.contains("失败") -> Color(0xFFf44336).copy(alpha = 0.1f)
                        else -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    }
                )
            ) {
                Text(
                    text = lastSearchResult,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // 搜索输入框
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("输入搜索内容") },
            placeholder = { Text("例如：美食推荐、穿搭技巧...") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    if (searchText.isNotBlank()) {
                        isSearching = true
                        // 🔄 修改调用
                        val result = searchWithAutoPaste(context, selectedPlatform, searchText)
                        lastSearchResult = result
                    } else {
                        Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
                    }
                }
            ),
            singleLine = true
        )

        // 🔄 修改按钮区域 - 动态文本
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 按钮1：智能搜索
            Button(
                onClick = {
                    Log.d("SimpleJump", "Direct search clicked with text: $searchText")
                    keyboardController?.hide()
                    if (searchText.isNotBlank()) {
                        isSearching = true
                        // 🔄 修改调用
                        val result = searchWithAutoPaste(context, selectedPlatform, searchText)
                        lastSearchResult = result
                    } else {
                        Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (searchText.isNotBlank()) selectedPlatform.primaryColor else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = searchText.isNotBlank() && !isSearching
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("智能搜索中...")
                } else {
                    Text("🎯 ${selectedPlatform.displayName}智能搜索", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 按钮2：打开搜索页面
            OutlinedButton(
                onClick = {
                    // 🔄 修改调用
                    val result = openSearchPage(context, selectedPlatform)
                    lastSearchResult = result
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSearching
            ) {
                Text("🔍 打开${selectedPlatform.displayName}搜索", fontSize = 16.sp)
            }

            // 按钮3：复制并打开
            OutlinedButton(
                onClick = {
                    if (searchText.isNotBlank()) {
                        // 🔄 修改调用
                        val result = copyToClipboardAndOpen(context, selectedPlatform, searchText)
                        lastSearchResult = result
                    } else {
                        Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = searchText.isNotBlank() && !isSearching
            ) {
                Text("📋 复制内容并打开${selectedPlatform.displayName}", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔄 修改热门搜索 - 根据平台显示不同标签
        Text(
            text = "${selectedPlatform.displayName}热门搜索：",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            selectedPlatform.hotSearches.forEach { tag ->
                FilterChip(
                    onClick = {
                        searchText = tag
                        val result = searchWithAutoPaste(context, selectedPlatform, tag)
                        lastSearchResult = result
                    },
                    label = { Text(tag, fontSize = 12.sp) },
                    selected = false,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔄 修改工具按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    // 🔄 修改调用
                    val result = openMainPage(context, selectedPlatform)
                    lastSearchResult = if (result) "✅ 成功打开${selectedPlatform.displayName}" else "❌ 打开失败，可能未安装${selectedPlatform.displayName}"
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("打开${selectedPlatform.displayName}", fontSize = 12.sp)
            }

            // 其他按钮保持不变
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = {
                    searchText = ""
                    lastSearchResult = ""
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) { Text("清空", fontSize = 12.sp) }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = {
                    analyzeXhsApp(context)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            { Text("分析应用", fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 修改提示文字
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "💡 支持${SearchPlatform.values().joinToString("、") { it.displayName }}多平台搜索。选择平台后可直接跳转到搜索结果。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
// 平台选择器
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformSelector(
    selectedPlatform: SearchPlatform,
    onPlatformSelected: (SearchPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${selectedPlatform.icon} ${selectedPlatform.displayName}",
            onValueChange = {},
            readOnly = true,
            label = { Text("选择搜索平台") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SearchPlatform.values().forEach { platform ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = platform.icon, fontSize = 18.sp)
                            Text(text = platform.displayName)
                        }
                    },
                    onClick = {
                        onPlatformSelected(platform)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 多平台搜索调度函数
fun searchWithAutoPaste(context: android.content.Context, platform: SearchPlatform, searchQuery: String): String {
    return when (platform) {
        SearchPlatform.XIAOHONGSHU -> searchInXhsWithAutoPaste(context, searchQuery)
        SearchPlatform.ZHIHU -> searchInZhihu(context, searchQuery)
    }
}

// 多平台搜索页面打开
fun openSearchPage(context: android.content.Context, platform: SearchPlatform): String {
    return when (platform) {
        SearchPlatform.XIAOHONGSHU -> openXhsSearchPageFixed(context)
        SearchPlatform.ZHIHU -> openZhihuSearchPage(context)
    }
}

// 多平台复制并打开
fun copyToClipboardAndOpen(context: android.content.Context, platform: SearchPlatform, searchText: String): String {
    return when (platform) {
        SearchPlatform.XIAOHONGSHU -> copyToClipboardAndOpenXhsFixed(context, searchText)
        SearchPlatform.ZHIHU -> copyToClipboardAndOpenZhihu(context, searchText)
    }
}

// 多平台主页打开
fun openMainPage(context: android.content.Context, platform: SearchPlatform): Boolean {
    return when (platform) {
        SearchPlatform.XIAOHONGSHU -> jumpToXhsMainPage(context)
        SearchPlatform.ZHIHU -> jumpToZhihuMainPage(context)
    }
}

// 知乎搜索实现
fun searchInZhihu(context: android.content.Context, searchQuery: String): String {
    try {
        Log.d("SimpleJump", "Starting Zhihu search for: $searchQuery")

        // 检查知乎是否已安装
        if (!isAppInstalled(context, "com.zhihu.android")) {
            return jumpToAppStore(context, "com.zhihu.android")
        }

        val encodedQuery = try {
            URLEncoder.encode(searchQuery, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Uri.encode(searchQuery)
        }

        // 使用已验证的知乎搜索方案
        val zhihuSchemes = listOf(
            "zhihu://search?q=$encodedQuery",
            "zhihu://search?keyword=$encodedQuery",
            "zhihu://search?query=$encodedQuery"
        )

        for ((index, scheme) in zhihuSchemes.withIndex()) {
            try {
                Log.d("SimpleJump", "Testing Zhihu scheme ${index + 1}: $scheme")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    setPackage("com.zhihu.android")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    Log.d("SimpleJump", "✅ Successfully opened Zhihu search")
                    return "✅ 成功跳转知乎搜索「$searchQuery」"
                }

            } catch (e: Exception) {
                Log.w("SimpleJump", "Zhihu scheme ${index + 1} failed: ${e.message}")
                continue
            }
        }

        return "❌ 知乎搜索失败，无法打开搜索页面"

    } catch (e: Exception) {
        Log.e("SimpleJump", "Zhihu search failed", e)
        return "❌ 知乎搜索失败：${e.message}"
    }
}

// 知乎搜索页面打开
fun openZhihuSearchPage(context: android.content.Context): String {
    try {
        if (!isAppInstalled(context, "com.zhihu.android")) {
            return jumpToAppStore(context, "com.zhihu.android")
        }

        val searchPageSchemes = listOf(
            "zhihu://search",
            "zhihu://explore/search",
            "zhihu://main/search"
        )

        for ((index, scheme) in searchPageSchemes.withIndex()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    setPackage("com.zhihu.android")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    return "✅ 成功打开知乎搜索页面"
                }

            } catch (e: Exception) {
                continue
            }
        }

        return "❌ 无法打开知乎搜索页面"
    } catch (e: Exception) {
        return "❌ 打开知乎搜索页面失败：${e.message}"
    }
}

// 知乎复制并打开
fun copyToClipboardAndOpenZhihu(context: android.content.Context, searchText: String): String {
    try {
        if (!isAppInstalled(context, "com.zhihu.android")) {
            return jumpToAppStore(context, "com.zhihu.android")
        }

        // 复制到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("搜索内容", searchText)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "已复制「$searchText」到剪贴板", Toast.LENGTH_SHORT).show()

        val result = openZhihuSearchPage(context)
        if (result.contains("成功")) {
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(context, "请在知乎搜索框中粘贴「$searchText」", Toast.LENGTH_LONG).show()
            }, 1500)
            return "✅ 已复制内容并打开知乎搜索页面"
        }

        return result
    } catch (e: Exception) {
        return "❌ 复制并打开知乎失败：${e.message}"
    }
}

// 知乎主页打开
fun jumpToZhihuMainPage(context: android.content.Context): Boolean {
    try {
        if (!isAppInstalled(context, "com.zhihu.android")) {
            jumpToAppStore(context, "com.zhihu.android")
            return false
        }

        val mainPageSchemes = listOf(
            null, // 使用默认启动Intent
            "zhihu://",
            "zhihu://main",
            "zhihu://home"
        )

        for (scheme in mainPageSchemes) {
            try {
                val intent = if (scheme == null) {
                    context.packageManager.getLaunchIntentForPackage("com.zhihu.android")
                } else {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(scheme)
                        setPackage("com.zhihu.android")
                    }
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    if (context.packageManager.resolveActivity(intent, 0) != null) {
                        context.startActivity(intent)
                        return true
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }

        return false
    } catch (e: Exception) {
        return false
    }
}

// 通用应用安装检查
fun isAppInstalled(context: android.content.Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

/**
 * 最终版本：尝试在小红书中直接搜索
 */
/**
 * 修正版本：基于ADB验证的搜索方法
 */
/**
 * 增强版搜索：自动跳转+自动粘贴+可选自动搜索
 */
fun searchInXhsWithAutoPaste(context: android.content.Context, searchQuery: String, autoSearch: Boolean = true): String {
    try {
        Log.d("SimpleJump", "Starting auto-paste search for: $searchQuery")

        // 检查小红书是否已安装
        if (!isXhsInstalled(context)) {
            return jumpToAppStore(context, "com.xingin.xhs")
        }

        // 第一步：复制搜索内容到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("搜索内容", searchQuery)
        clipboard.setPrimaryClip(clip)

        Log.d("SimpleJump", "✅ Copied to clipboard: $searchQuery")

//        val encodedQuery = Uri.encode(searchQuery)
        val encodedQuery = try {
            URLEncoder.encode(searchQuery, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Uri.encode(searchQuery)
        }

        // 使用已验证成功的搜索方案
        val workingSchemes = listOf(
            "xhsdiscover://search/result?keyword=$encodedQuery",  // ADB验证成功的方案
            "xhsdiscover://search/result?q=$encodedQuery",
            "xhsdiscover://search/recommend?keyword=$encodedQuery",
            "xhsdiscover://search/recommend?q=$encodedQuery",
            "xhsdiscover://search?keyword=$encodedQuery",
            "xhsdiscover://search?q=$encodedQuery"
        )

        // 逐一尝试每个方案
        for ((index, scheme) in workingSchemes.withIndex()) {
            try {
                Log.d("SimpleJump", "Testing scheme ${index + 1}: $scheme")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                val activities = context.packageManager.queryIntentActivities(intent, 0)
                val xhsActivity = activities.find {
                    it.activityInfo.packageName == "com.xingin.xhs"
                }

                if (xhsActivity != null) {
                    intent.setClassName("com.xingin.xhs", xhsActivity.activityInfo.name)
                    context.startActivity(intent)

                    Log.d("SimpleJump", "✅ Successfully opened search page")

                    // 第二步：延迟执行自动粘贴
                    Handler(Looper.getMainLooper()).postDelayed({
                        performAutoPasteSequence(context, searchQuery, autoSearch)
                    }, 1500) // 等待页面加载完成

                    return "✅ 成功跳转搜索页面，正在自动粘贴「$searchQuery」..."
                }

            } catch (e: Exception) {
                Log.w("SimpleJump", "Scheme ${index + 1} failed: ${e.message}")
                continue
            }
        }

        // 如果带参数失败，使用不带参数的方案
        return tryOpenSearchPageAndAutoPaste(context, searchQuery, autoSearch)

    } catch (e: Exception) {
        Log.e("SimpleJump", "Auto-paste search failed", e)
        return "❌ 自动粘贴搜索失败：${e.message}"
    }
}

/**
 * 执行自动粘贴序列
 */
private fun performAutoPasteSequence(context: android.content.Context, searchQuery: String, autoSearch: Boolean) {
    try {
        Log.d("SimpleJump", "Performing auto-paste sequence")

        // 方法1: 尝试使用系统剪贴板广播（某些系统支持）
        val success1 = trySystemPaste(context)

        if (!success1) {
            // 方法2: 尝试使用模拟按键
            val success2 = trySimulateKeyPaste(context)

            if (!success2) {
                // 方法3: 尝试使用Intent广播
                val success3 = tryIntentPaste(context, searchQuery)

                if (!success3) {
                    // 最后提示用户手动粘贴
                    showPasteInstructions(context, searchQuery)
                }
            }
        }

        // 如果启用了自动搜索，在粘贴后自动触发搜索
        if (autoSearch) {
            Handler(Looper.getMainLooper()).postDelayed({
                tryAutoSearch(context)
            }, 1000)
        }

    } catch (e: Exception) {
        Log.e("SimpleJump", "Auto-paste sequence failed", e)
        showPasteInstructions(context, searchQuery)
    }
}

/**
 * 方法1：尝试系统级粘贴
 */
private fun trySystemPaste(context: android.content.Context): Boolean {
    return try {
        // 发送粘贴广播（某些定制系统支持）
        val intent = Intent("android.intent.action.PASTE").apply {
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        context.sendBroadcast(intent)

        Log.d("SimpleJump", "✅ Sent system paste broadcast")
        true
    } catch (e: Exception) {
        Log.w("SimpleJump", "System paste failed: ${e.message}")
        false
    }
}

/**
 * 方法2：尝试模拟按键粘贴（需要特殊权限）
 */
private fun trySimulateKeyPaste(context: android.content.Context): Boolean {
    return try {
        // 尝试通过Runtime执行按键命令（需要系统权限或ROOT）
        val commands = arrayOf(
            "input keyevent 279", // KEYCODE_PASTE
            "input keyevent KEYCODE_CTRL_LEFT && input keyevent KEYCODE_V" // Ctrl+V
        )

        for (command in commands) {
            try {
                val process = Runtime.getRuntime().exec(command)
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    Log.d("SimpleJump", "✅ Simulate key paste successful: $command")
                    return true
                }
            } catch (e: Exception) {
                continue
            }
        }

        false
    } catch (e: Exception) {
        Log.w("SimpleJump", "Simulate key paste failed: ${e.message}")
        false
    }
}

/**
 * 方法3：尝试Intent方式粘贴
 */
private fun tryIntentPaste(context: android.content.Context, searchQuery: String): Boolean {
    return try {
        // 尝试发送文本到小红书
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, searchQuery)
            setPackage("com.xingin.xhs")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (context.packageManager.resolveActivity(intent, 0) != null) {
            context.startActivity(intent)
            Log.d("SimpleJump", "✅ Intent paste successful")
            return true
        }

        false
    } catch (e: Exception) {
        Log.w("SimpleJump", "Intent paste failed: ${e.message}")
        false
    }
}

/**
 * 显示粘贴指导
 */
private fun showPasteInstructions(context: android.content.Context, searchQuery: String) {
    // 第一步：立即提示
    Toast.makeText(
        context,
        "✅ 已复制「$searchQuery」，请在搜索框中长按粘贴",
        Toast.LENGTH_LONG
    ).show()

    // 第二步：延迟提示快捷键
    Handler(Looper.getMainLooper()).postDelayed({
        Toast.makeText(
            context,
            "💡 提示：也可以尝试 Ctrl+V 快捷键粘贴",
            Toast.LENGTH_SHORT
        ).show()
    }, 2000)
}

/**
 * 尝试自动触发搜索
 */
private fun tryAutoSearch(context: android.content.Context) {
    try {
        Log.d("SimpleJump", "Attempting auto-search")

        // 方法1：模拟回车键
        val enterCommands = arrayOf(
            "input keyevent 66",  // KEYCODE_ENTER
            "input keyevent 84"   // KEYCODE_SEARCH
        )

        for (command in enterCommands) {
            try {
                val process = Runtime.getRuntime().exec(command)
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    Log.d("SimpleJump", "✅ Auto-search successful: $command")
                    return
                }
            } catch (e: Exception) {
                continue
            }
        }

        // 如果自动搜索失败，提示用户手动点击搜索
        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(context, "请点击搜索按钮或按回车键搜索", Toast.LENGTH_SHORT).show()
        }, 500)

    } catch (e: Exception) {
        Log.e("SimpleJump", "Auto-search failed", e)
    }
}

/**
 * 备用方案：打开搜索页面并自动粘贴
 */
private fun tryOpenSearchPageAndAutoPaste(context: android.content.Context, searchQuery: String, autoSearch: Boolean): String {
    try {
        // 复制到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("搜索内容", searchQuery)
        clipboard.setPrimaryClip(clip)

        // 使用已知可以工作的搜索页面方案
        val searchPageSchemes = listOf(
            "xhsdiscover://search/recommend",
            "xhsdiscover://search",
            "xhsdiscover://global_search",
            "xhsdiscover://discover"
        )

        for ((index, scheme) in searchPageSchemes.withIndex()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                val activities = context.packageManager.queryIntentActivities(intent, 0)
                val xhsActivity = activities.find {
                    it.activityInfo.packageName == "com.xingin.xhs"
                }

                if (xhsActivity != null) {
                    intent.setClassName("com.xingin.xhs", xhsActivity.activityInfo.name)
                    context.startActivity(intent)

                    // 延迟执行自动粘贴
                    Handler(Looper.getMainLooper()).postDelayed({
                        performAutoPasteSequence(context, searchQuery, autoSearch)
                    }, 2000)

                    return "✅ 已打开搜索页面，正在自动粘贴「$searchQuery」..."
                }

            } catch (e: Exception) {
                continue
            }
        }

        return "❌ 无法打开搜索页面"

    } catch (e: Exception) {
        Log.e("SimpleJump", "Backup auto-paste failed", e)
        return "❌ 备用自动粘贴方案失败：${e.message}"
    }
}

/**
 * 修复版：直接打开小红书搜索页面
 */
fun openXhsSearchPageFixed(context: android.content.Context): String {
    try {
        if (!isXhsInstalled(context)) {
            return jumpToAppStore(context, "com.xingin.xhs")
        }

        // 使用与第一个按钮相同的成功方案
        val workingSchemes = listOf(
            "xhsdiscover://search/recommend",
            "xhsdiscover://search",
            "xhsdiscover://global_search",
            "xhsdiscover://discover"
        )

        for ((index, scheme) in workingSchemes.withIndex()) {
            try {
                Log.d("SimpleJump", "Trying search page scheme ${index + 1}: $scheme")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                // 使用与第一个按钮相同的方式查找activity
                val activities = context.packageManager.queryIntentActivities(intent, 0)
                val xhsActivity = activities.find {
                    it.activityInfo.packageName == "com.xingin.xhs"
                }

                if (xhsActivity != null) {
                    intent.setClassName("com.xingin.xhs", xhsActivity.activityInfo.name)
                    context.startActivity(intent)
                    Log.d("SimpleJump", "✅ Successfully opened search page: $scheme")
                    return "✅ 成功打开搜索页面 (方案${index + 1})"
                }

            } catch (e: Exception) {
                Log.w("SimpleJump", "Search page scheme ${index + 1} failed: ${e.message}")
                continue
            }
        }

        return "❌ 无法打开搜索页面"
    } catch (e: Exception) {
        Log.e("SimpleJump", "Open search page failed", e)
        return "❌ 打开搜索页面失败：${e.message}"
    }
}

/**
 * 修复版：复制搜索内容并打开搜索页面
 */
fun copyToClipboardAndOpenXhsFixed(context: android.content.Context, searchText: String): String {
    try {
        if (!isXhsInstalled(context)) {
            return jumpToAppStore(context, "com.xingin.xhs")
        }

        // 复制到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("搜索内容", searchText)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "已复制「$searchText」到剪贴板", Toast.LENGTH_SHORT).show()

        // 使用与第一个按钮相同的成功方案打开搜索页面
        val workingSchemes = listOf(
            "xhsdiscover://search/recommend",
            "xhsdiscover://search",
            "xhsdiscover://global_search",
            "xhsdiscover://discover"
        )

        for ((index, scheme) in workingSchemes.withIndex()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                val activities = context.packageManager.queryIntentActivities(intent, 0)
                val xhsActivity = activities.find {
                    it.activityInfo.packageName == "com.xingin.xhs"
                }

                if (xhsActivity != null) {
                    intent.setClassName("com.xingin.xhs", xhsActivity.activityInfo.name)
                    context.startActivity(intent)

                    // 延迟提示用户粘贴
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(
                            context,
                            "请在搜索框中长按粘贴「$searchText」",
                            Toast.LENGTH_LONG
                        ).show()
                    }, 1500)

                    return "✅ 已复制内容并打开搜索页面，请手动粘贴"
                }

            } catch (e: Exception) {
                continue
            }
        }

        return "❌ 无法打开搜索页面，但已复制内容到剪贴板"

    } catch (e: Exception) {
        Log.e("SimpleJump", "Copy and open failed", e)
        return "❌ 复制并打开失败：${e.message}"
    }
}

/**
 * 简化版本的scheme测试函数
 */
fun testSingleScheme(context: android.content.Context, scheme: String): Boolean {
    return try {
        Log.d("SimpleJump", "Testing single scheme: $scheme")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(scheme)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // 查找能处理这个intent的小红书activity
        val activities = context.packageManager.queryIntentActivities(intent, 0)
        val xhsActivity = activities.find {
            it.activityInfo.packageName == "com.xingin.xhs"
        }

        if (xhsActivity != null) {
            intent.setClassName("com.xingin.xhs", xhsActivity.activityInfo.name)
            context.startActivity(intent)
            Log.d("SimpleJump", "✅ Single scheme test successful: $scheme")
            return true
        } else {
            Log.d("SimpleJump", "❌ No XHS activity found for scheme: $scheme")
            return false
        }

    } catch (e: Exception) {
        Log.w("SimpleJump", "Single scheme test failed: $scheme - ${e.message}")
        return false
    }
}
/**
 * 备用搜索方案：先打开搜索页面，再通过其他方式输入
 */
private fun tryFallbackSearch(context: android.content.Context, searchQuery: String, encodedQuery: String): String {
    try {
        // 备用方案1：尝试通过Activity直接启动搜索
        val searchActivities = listOf(
            "com.xingin.xhs.search.SearchActivity",
            "com.xingin.alioth.search.GlobalSearchActivity",
            "com.xingin.matrix.search.SearchActivity",
            "com.xingin.xhs.activity.SearchActivity",
            "com.xingin.xhs.ui.activity.SearchActivity",
            "com.xingin.discover.search.SearchActivity"
        )

        for (activityName in searchActivities) {
            try {
                Log.d("SimpleJump", "Trying search activity: $activityName")

                val intent = Intent().apply {
                    component = ComponentName("com.xingin.xhs", activityName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    // 尝试所有可能的参数
                    putExtra("query", searchQuery)
                    putExtra("keyword", searchQuery)
                    putExtra("search_query", searchQuery)
                    putExtra("q", searchQuery)
                    putExtra("searchText", searchQuery)
                    putExtra("search_keyword", searchQuery)
                    putExtra("search_content", searchQuery)

                    // 通过 data 传递
                    data = Uri.parse("search://$encodedQuery")

                    // 通过 action 传递
                    action = Intent.ACTION_SEARCH
                    putExtra("android.intent.extra.QUERY", searchQuery)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    Log.d("SimpleJump", "Successfully used activity: $activityName")
                    return "✅ 成功：通过Activity跳转搜索"
                }
            } catch (e: Exception) {
                Log.w("SimpleJump", "Failed to start search $activityName: ${e.message}")
                continue
            }
        }

        // 备用方案2：打开基础搜索页面
        val fallbackSchemes = listOf(
            "xhsdiscover://search",
            "xhsdiscover://search/recommend",
            "xhsdiscover://global_search",
            "xhsdiscover://discover/search",
            "xiaohongshu://search",
            "xhs://search"
        )

        for (scheme in fallbackSchemes) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    setPackage("com.xingin.xhs")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)

                    // 提示用户手动输入
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(context, "已打开搜索页面，请手动输入：$searchQuery", Toast.LENGTH_LONG).show()
                    }, 1000)

                    Log.d("SimpleJump", "Opened search page, user needs to input: $searchQuery")
                    return "⚠️ 部分成功：已打开搜索页面，请手动输入「$searchQuery」"
                }
            } catch (e: Exception) {
                continue
            }
        }

        // 最后的备用方案：打开主页
        val mainOpened = jumpToXhsMainPage(context)
        if (mainOpened) {
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(context, "已打开小红书，请手动点击搜索并输入：$searchQuery", Toast.LENGTH_LONG).show()
            }, 1000)
            return "⚠️ 已打开小红书主页，请手动搜索：$searchQuery"
        }

        return "❌ 无法打开小红书搜索功能"

    } catch (e: Exception) {
        Log.e("SimpleJump", "Fallback search failed", e)
        return "❌ 所有搜索方案均失败：${e.message}"
    }
}

/**
 * 检查小红书是否已安装
 */
fun isXhsInstalled(context: android.content.Context): Boolean {
    return try {
        context.packageManager.getPackageInfo("com.xingin.xhs", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

/**
 * 跳转到应用商店下载小红书
 */
fun jumpToAppStore(context: android.content.Context, packageName: String): String {
    try {
        Log.d("SimpleJump", "Attempting to open app store for package: $packageName")

        // 应用商店URL列表，按优先级排序
        val appStoreUrls = listOf(
            // Google Play Store
            "market://details?id=$packageName",
            "https://play.google.com/store/apps/details?id=$packageName",

            // 华为应用市场
            "appmarket://details?id=$packageName",
            "https://appgallery.huawei.com/app/$packageName",

            // 小米应用商店
            "mimarket://details?id=$packageName",

            // OPPO软件商店
            "oppomarket://details?id=$packageName",

            // vivo应用商店
            "vivomarket://details?id=$packageName",

            // 应用宝
            "qqbrowser://url/search?src=3&q=$packageName",
            "https://sj.qq.com/myapp/detail.htm?apkName=$packageName",

            // 豌豆荚
            "https://www.wandoujia.com/apps/$packageName",

            // 360手机助手
            "https://zhushou.360.cn/detail/index/soft_id/$packageName"
        )

        for ((index, url) in appStoreUrls.withIndex()) {
            try {
                Log.d("SimpleJump", "Trying app store URL ${index + 1}: $url")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                // 检查是否有应用可以处理这个intent
                val resolveInfo = context.packageManager.resolveActivity(intent, 0)
                if (resolveInfo != null) {
                    context.startActivity(intent)
                    Log.d("SimpleJump", "✅ Successfully opened app store with URL: $url")

                    val storeName = when {
                        url.contains("play.google.com") || url.contains("market://") -> "Google Play"
                        url.contains("appgallery.huawei.com") || url.contains("appmarket://") -> "华为应用市场"
                        url.contains("mimarket://") -> "小米应用商店"
                        url.contains("oppomarket://") -> "OPPO软件商店"
                        url.contains("vivomarket://") -> "vivo应用商店"
                        url.contains("sj.qq.com") || url.contains("qqbrowser://") -> "应用宝"
                        url.contains("wandoujia.com") -> "豌豆荚"
                        url.contains("360.cn") -> "360手机助手"
                        else -> "应用商店"
                    }

                    return "🏪 小红书未安装，已跳转到${storeName}下载页面"
                }
            } catch (e: Exception) {
                Log.w("SimpleJump", "App store URL ${index + 1} failed: ${e.message}")
                continue
            }
        }

        // 如果所有应用商店都无法打开，提供手动下载提示
        Toast.makeText(context, "请手动前往应用商店搜索「小红书」进行下载", Toast.LENGTH_LONG).show()
        return "❌ 小红书未安装，请手动前往应用商店下载"

    } catch (e: Exception) {
        Log.e("SimpleJump", "Jump to app store failed", e)
        return "❌ 跳转应用商店失败：${e.message}"
    }
}

/**
 * 尝试直接打开小红书搜索页面
 */
fun openXhsSearchPage(context: android.content.Context): String {
    try {
        // 检查小红书是否已安装
        if (!isXhsInstalled(context)) {
            return jumpToAppStore(context, "com.xingin.xhs")
        }

        // 使用已验证的搜索页面scheme（不带关键词）
        val searchPageSchemes = listOf(
            "xhsdiscover://search",              // 基础搜索页面
            "xhsdiscover://search/recommend",    // 推荐搜索页面
            "xhsdiscover://global_search",       // 全局搜索
            "xhsdiscover://discover/search",     // 发现页面搜索
            "xhsdiscover://discover",            // 发现页面
            "xiaohongshu://search",              // 备用scheme
            "xhs://search",                      // 简化scheme
            "xhsdiscover://home"                 // 主页（最后备选）
        )

        for ((index, scheme) in searchPageSchemes.withIndex()) {
            try {
                Log.d("SimpleJump", "Trying search page scheme ${index + 1}: $scheme")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(scheme)
                    setPackage("com.xingin.xhs")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    Log.d("SimpleJump", "✅ Successfully opened search page with scheme: $scheme")
                    return "✅ 成功打开搜索页面 (方案${index + 1})"
                }
            } catch (e: Exception) {
                Log.w("SimpleJump", "Search page scheme ${index + 1} failed: ${e.message}")
                continue
            }
        }

        // 如果无法打开搜索页面，尝试打开主页
        val mainOpened = jumpToXhsMainPage(context)
        if (mainOpened) {
            return "⚠️ 无法直接打开搜索页面，已打开小红书主页"
        }

        return "❌ 无法打开小红书搜索页面"
    } catch (e: Exception) {
        Log.e("SimpleJump", "Open search page failed", e)
        return "❌ 打开搜索页面失败：${e.message}"
    }
}

/**
 * 复制搜索内容到剪贴板并打开小红书
 */
fun copyToClipboardAndOpenXhs(context: android.content.Context, searchText: String) {
    try {
        // 检查小红书是否已安装
        if (!isXhsInstalled(context)) {
            jumpToAppStore(context, "com.xingin.xhs")
            return
        }

        // 复制到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("搜索内容", searchText)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "已复制「$searchText」到剪贴板", Toast.LENGTH_SHORT).show()

        // 延迟一下再打开小红书
        Handler(Looper.getMainLooper()).postDelayed({
            val opened = jumpToXhsMainPage(context)
            if (opened) {
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(context, "请点击搜索框并粘贴内容搜索", Toast.LENGTH_LONG).show()
                }, 1500)
            }
        }, 500)

    } catch (e: Exception) {
        Toast.makeText(context, "复制失败：${e.message}", Toast.LENGTH_SHORT).show()
        Log.e("SimpleJump", "Copy to clipboard failed", e)
    }
}

/**
 * 分析小红书应用的可用组件
 */
fun analyzeXhsApp(context: android.content.Context) {
    try {
        if (!isXhsInstalled(context)) {
            Toast.makeText(context, "小红书未安装，无法分析", Toast.LENGTH_SHORT).show()
            return
        }

        val packageManager = context.packageManager
        val packageName = "com.xingin.xhs"

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            Log.d("SimpleJump", "XHS Activities found: ${packageInfo.activities?.size}")

            val searchRelatedActivities = packageInfo.activities?.filter {
                it.name.contains("search", ignoreCase = true) ||
                        it.name.contains("Search", ignoreCase = false)
            }

            val message = if (searchRelatedActivities?.isNotEmpty() == true) {
                "找到 ${searchRelatedActivities.size} 个搜索相关Activity:\n" +
                        searchRelatedActivities.take(5).joinToString("\n") { "• ${it.name.substringAfterLast('.')}" } +
                        if (searchRelatedActivities.size > 5) "\n... 还有 ${searchRelatedActivities.size - 5} 个" else ""
            } else {
                "未找到明显的搜索相关Activity\n小红书版本可能较新，组件名称已变更"
            }

            // 同时分析其他有用信息
            val allActivities = packageInfo.activities
            val totalActivities = allActivities?.size ?: 0

            // 寻找主要功能Activity
            val mainActivities = allActivities?.filter { activity ->
                val name = activity.name.lowercase()
                name.contains("main") || name.contains("home") ||
                        name.contains("discover") || name.contains("feed")
            }?.take(3)

            val fullMessage = buildString {
                append(message)
                append("\n\n📊 应用分析:")
                append("\n• 总Activity数: $totalActivities")
                if (mainActivities?.isNotEmpty() == true) {
                    append("\n• 主要Activity:")
                    mainActivities.forEach {
                        append("\n  - ${it.name.substringAfterLast('.')}")
                    }
                }
            }

            Toast.makeText(context, fullMessage, Toast.LENGTH_LONG).show()
            Log.d("SimpleJump", "XHS App Analysis: $fullMessage")

        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(context, "小红书未安装", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        val errorMessage = "分析失败：${e.message}"
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("SimpleJump", "Analyze XHS app failed", e)
    }
}

/**
 * 跳转到小红书主页面 - 增强版本
 */
fun jumpToXhsMainPage(context: android.content.Context): Boolean {
    try {
        // 检查小红书是否已安装
        if (!isXhsInstalled(context)) {
            jumpToAppStore(context, "com.xingin.xhs")
            return false
        }

        // 尝试多种方式打开小红书主页
        val mainPageSchemes = listOf(
            // 方式1：使用包管理器获取启动Intent（最可靠）
            null, // 占位符，表示使用默认启动方式

            // 方式2：使用自定义scheme
            "xhsdiscover://",
            "xhsdiscover://home",
            "xhsdiscover://main",
            "xhsdiscover://discover",
            "xiaohongshu://",
            "xiaohongshu://home",
            "xhs://",
            "xhs://home"
        )

        for ((index, scheme) in mainPageSchemes.withIndex()) {
            try {
                val intent = if (scheme == null) {
                    // 使用默认启动方式
                    context.packageManager.getLaunchIntentForPackage("com.xingin.xhs")
                } else {
                    // 使用自定义scheme
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(scheme)
                        setPackage("com.xingin.xhs")
                    }
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    // 检查intent是否可以被处理
                    val resolveInfo = context.packageManager.resolveActivity(intent, 0)
                    if (resolveInfo != null) {
                        context.startActivity(intent)
                        Log.d("SimpleJump", "✅ Successfully opened XHS main page using method ${index + 1}")
                        return true
                    }
                }
            } catch (e: Exception) {
                Log.w("SimpleJump", "Main page method ${index + 1} failed: ${e.message}")
                continue
            }
        }

        Log.w("SimpleJump", "All methods to open XHS main page failed")
        return false

    } catch (e: Exception) {
        Log.e("SimpleJump", "Error jumping to XHS main page", e)
        return false
    }
}

/**
 * 支持不同搜索模式的增强搜索函数
 */
enum class SearchMode(val displayName: String, val paramValue: String) {
    ALL("全部", "all"),
    NOTE("笔记", "note"),
    GOODS("商品", "goods"),
    USER("用户", "user"),
    VIDEO("视频", "video"),
    LIVE("直播", "live")
}

/**
 * 带搜索模式的搜索函数
 */
fun searchInXhsWithMode(
    context: android.content.Context,
    searchQuery: String,
    mode: SearchMode = SearchMode.ALL
): String {
    try {
        Log.d("SimpleJump", "Searching with mode: ${mode.displayName}, query: $searchQuery")

        // 检查小红书是否已安装
        if (!isXhsInstalled(context)) {
            return jumpToAppStore(context, "com.xingin.xhs")
        }

        val encodedQuery = Uri.encode(searchQuery)
        val modeParam = mode.paramValue

        // 带模式的搜索schemes
        val modeSearchSchemes = listOf(
            "xhsdiscover://search/result?keyword=$encodedQuery&mode=$modeParam",  
            "xhsdiscover://search/result?q=$encodedQuery&type=$modeParam",        
            "xhsdiscover://search_result?keyword=$encodedQuery&mode=$modeParam",
            "xhsdiscover://search_result?q=$encodedQuery&type=$modeParam",
            "xhsdiscover://search?keyword=$encodedQuery&mode=$modeParam",
            "xhsdiscover://search?q=$encodedQuery&type=$modeParam",
            "xhsdiscover://search/recommend?keyword=$encodedQuery&mode=$modeParam",
            "xhsdiscover://global_search?q=$encodedQuery&type=$modeParam",
            "xiaohongshu://search?keyword=$encodedQuery&mode=$modeParam"
        )

        // 先尝试带模式的搜索
        for ((index, scheme) in modeSearchSchemes.withIndex()) {
            if (tryLaunchScheme(context, scheme)) {
                return "✅ 成功：${mode.displayName}模式搜索「$searchQuery」(方案${index + 1})"
            }
        }

        // 如果模式搜索失败，回退到基础搜索
        Log.d("SimpleJump", "Mode search failed, falling back to basic search")
        val basicResult = searchInXhsWithAutoPaste(context, searchQuery)
        return if (basicResult.contains("成功")) {
            "⚠️ ${mode.displayName}模式搜索失败，已使用基础搜索"
        } else {
            basicResult
        }

    } catch (e: Exception) {
        Log.e("SimpleJump", "Mode search failed", e)
        return searchInXhsWithAutoPaste(context, searchQuery)
    }
}

/**
 * 尝试启动指定的scheme
 */
private fun tryLaunchScheme(context: android.content.Context, scheme: String): Boolean {
    return try {
        Log.d("SimpleJump", "Trying scheme: $scheme")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(scheme)
            setPackage("com.xingin.xhs")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        if (resolveInfo != null) {
            context.startActivity(intent)
            Log.d("SimpleJump", "✅ Scheme worked: $scheme")
            true
        } else {
            Log.d("SimpleJump", "❌ Scheme not supported: $scheme")
            false
        }
    } catch (e: Exception) {
        Log.w("SimpleJump", "Scheme failed: $scheme - ${e.message}")
        false
    }
}

/**
 * 测试所有搜索方案的调试函数
 */
fun testAllSearchMethods(context: android.content.Context, testQuery: String = "测试") {
    Log.d("SimpleJump", "=== 开始测试所有搜索方案 ===")
    // 优先测试ADB验证成功的方案
    val encodedQuery = URLEncoder.encode(testQuery, "UTF-8")
    val adbVerifiedScheme = "xhsdiscover://search/result?keyword=$encodedQuery"
    val adbResult = tryLaunchScheme(context, adbVerifiedScheme)
    Log.d("SimpleJump", "ADB验证方案结果: $adbResult")
    // 测试基础搜索
    val basicResult = searchInXhsWithAutoPaste(context, testQuery)
    Log.d("SimpleJump", "基础搜索结果: $basicResult")

    // 等待一下再测试其他方案
    Handler(Looper.getMainLooper()).postDelayed({
        // 测试不同模式搜索
        SearchMode.values().forEach { mode ->
            Handler(Looper.getMainLooper()).postDelayed({
                val modeResult = searchInXhsWithMode(context, testQuery, mode)
                Log.d("SimpleJump", "${mode.displayName}模式搜索结果: $modeResult")
            }, 2000L)
        }
    }, 3000L)
}

/**
 * 获取设备信息，用于调试不同设备上的兼容性
 */
fun getDeviceInfo(): String {
    return try {
        buildString {
            append("设备信息:\n")
            append("• 制造商: ${android.os.Build.MANUFACTURER}\n")
            append("• 型号: ${android.os.Build.MODEL}\n")
            append("• Android版本: ${android.os.Build.VERSION.RELEASE}\n")
            append("• API级别: ${android.os.Build.VERSION.SDK_INT}\n")
            append("• 品牌: ${android.os.Build.BRAND}")
        }
    } catch (e: Exception) {
        "获取设备信息失败: ${e.message}"
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SimpleJumpAppTheme {
        MainScreen()
    }
}
