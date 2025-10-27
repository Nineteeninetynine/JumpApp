package com.example.simplejump.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.platform.SearchPlatform
import com.example.simplejump.platform.PlatformManager
import com.example.simplejump.ui.components.*
import com.example.simplejump.ui.theme.SimpleJumpAppTheme
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val platformManager = remember { PlatformManager.getInstance() }

    // 状态管理
    var searchText by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(SearchPlatform.XIAOHONGSHU) }
    var lastSearchResult by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // ✅ 执行搜索的函数
    fun performSearch() {
        if (searchText.isBlank()) return

        isSearching = true
        try {
            val result = runBlocking {
                platformManager.search(context, selectedPlatform, searchText)
            }
            lastSearchResult = result.message
        } catch (e: Exception) {
            lastSearchResult = "❌ 搜索失败: ${e.message}"
        } finally {
            isSearching = false
        }
    }

    // ✅ 打开搜索页面的函数
    fun openSearchPage() {
        isSearching = true
        try {
            val result = runBlocking {
                platformManager.openSearchPage(context, selectedPlatform)
            }
            lastSearchResult = result.message
        } catch (e: Exception) {
            lastSearchResult = "❌ 操作失败: ${e.message}"
        } finally {
            isSearching = false
        }
    }

    // ✅ 复制并打开的函数 - 修正方法名
    fun copyAndOpen() {
        if (searchText.isBlank()) return

        isSearching = true
        try {
            val result = runBlocking {
                // 注意：这里使用 copyAndOpenSearch 而不是 copyToClipboardAndOpen
                platformManager.copyAndOpenSearch(context, selectedPlatform, searchText)
            }
            lastSearchResult = result.message
        } catch (e: Exception) {
            lastSearchResult = "❌ 操作失败: ${e.message}"
        } finally {
            isSearching = false
        }
    }

    // ✅ 打开主页的函数
    fun openMainPage() {
        isSearching = true
        try {
            val result = runBlocking {
                platformManager.openMainPage(context, selectedPlatform)
            }
            lastSearchResult = result.message
        } catch (e: Exception) {
            lastSearchResult = "❌ 操作失败: ${e.message}"
        } finally {
            isSearching = false
        }
    }

    Log.d("SimpleJump", "MainScreen composing")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 应用图标和标题
        Text(
            text = "🔍",
            fontSize = 60.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "${selectedPlatform.displayName}搜索助手",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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

        // 状态显示卡片
        if (lastSearchResult.isNotEmpty()) {
            StatusCard(
                message = lastSearchResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // 搜索输入框 - ✅ 实现搜索提交
        SearchInputField(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onSearchSubmit = { performSearch() }, // ✅ 调用实际搜索
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // 功能按钮组 - ✅ 实现所有功能
        ActionButtons(
            searchText = searchText,
            selectedPlatform = selectedPlatform,
            isSearching = isSearching,
            onSearchClick = { performSearch() }, // ✅ 智能搜索
            onOpenSearchPageClick = { openSearchPage() }, // ✅ 打开搜索页面
            onCopyAndOpenClick = { copyAndOpen() }, // ✅ 复制并打开
            onClearClick = {
                searchText = ""
                lastSearchResult = ""
            },
            onOpenMainPageClick = { openMainPage() }, // ✅ 打开主页
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 热门搜索标签 - ✅ 实现自动搜索
        HotSearchTags(
            platform = selectedPlatform,
            onTagClick = { tag ->
                searchText = tag
                performSearch() // ✅ 自动执行搜索
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 底部提示卡片
        StatusCard(
            message = "💡 支持${SearchPlatform.values().joinToString("、") { it.displayName }}多平台搜索。选择平台后可直接跳转到搜索结果。",
            isInfo = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SimpleJumpAppTheme {
        MainScreen()
    }
}
