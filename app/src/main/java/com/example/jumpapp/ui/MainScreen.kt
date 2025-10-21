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
import com.example.simplejump.ui.components.*
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 状态管理
    var searchText by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(SearchPlatform.XIAOHONGSHU) }
    var lastSearchResult by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

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

        // 搜索输入框
        SearchInputField(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onSearchSubmit = {
                if (searchText.isNotBlank()) {
                    isSearching = true
                    // TODO: 调用搜索功能
                    lastSearchResult = "搜索功能开发中..."
                    isSearching = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // 功能按钮组
        ActionButtons(
            searchText = searchText,
            selectedPlatform = selectedPlatform,
            isSearching = isSearching,
            onSearchClick = {
                if (searchText.isNotBlank()) {
                    isSearching = true
                    // TODO: 调用智能搜索
                    lastSearchResult = "智能搜索功能开发中..."
                    isSearching = false
                }
            },
            onOpenSearchPageClick = {
                // TODO: 调用打开搜索页面
                lastSearchResult = "打开搜索页面功能开发中..."
            },
            onCopyAndOpenClick = {
                if (searchText.isNotBlank()) {
                    // TODO: 调用复制并打开
                    lastSearchResult = "复制并打开功能开发中..."
                }
            },
            onClearClick = {
                searchText = ""
                lastSearchResult = ""
            },
            onOpenMainPageClick = {
                // TODO: 调用打开主页
                lastSearchResult = "打开主页功能开发中..."
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 热门搜索标签
        HotSearchTags(
            platform = selectedPlatform,
            onTagClick = { tag ->
                searchText = tag
                // TODO: 自动执行搜索
                lastSearchResult = "自动搜索「$tag」功能开发中..."
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