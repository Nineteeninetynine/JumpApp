package com.example.simplejump.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.SearchPlatform
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

@Composable
fun ActionButtons(
    searchText: String,
    selectedPlatform: SearchPlatform,
    isSearching: Boolean,
    onSearchClick: () -> Unit,
    onOpenSearchPageClick: () -> Unit,
    onCopyAndOpenClick: () -> Unit,
    onClearClick: () -> Unit,
    onOpenMainPageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 主要按钮：智能搜索
        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (searchText.isNotBlank())
                    selectedPlatform.primaryColor
                else
                    Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = searchText.isNotBlank() && !isSearching
        ) {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("智能搜索中...")
            } else {
                Text(
                    "🎯 ${selectedPlatform.displayName}智能搜索",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 次要按钮：打开搜索页面
        OutlinedButton(
            onClick = onOpenSearchPageClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSearching
        ) {
            Text(
                "🔍 打开${selectedPlatform.displayName}搜索",
                fontSize = 16.sp
            )
        }

        // 次要按钮：复制并打开
        OutlinedButton(
            onClick = onCopyAndOpenClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = searchText.isNotBlank() && !isSearching
        ) {
            Text(
                "📋 复制内容并打开${selectedPlatform.displayName}",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 工具按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenMainPageClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSearching
            ) {
                Text(
                    "打开${selectedPlatform.displayName}",
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = onClearClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("清空", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    // TODO: 分析应用功能
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("分析应用", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionButtonsPreview() {
    SimpleJumpAppTheme {
        ActionButtons(
            searchText = "美食推荐",
            selectedPlatform = SearchPlatform.XIAOHONGSHU,
            isSearching = false,
            onSearchClick = {},
            onOpenSearchPageClick = {},
            onCopyAndOpenClick = {},
            onClearClick = {},
            onOpenMainPageClick = {}
        )
    }
}