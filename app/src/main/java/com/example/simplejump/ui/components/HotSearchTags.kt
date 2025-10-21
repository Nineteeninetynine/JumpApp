package com.example.simplejump.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.platform.SearchPlatform
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HotSearchTags(
    platform: SearchPlatform,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${platform.displayName}热门搜索：",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            platform.hotSearches.forEach { tag ->
                FilterChip(
                    onClick = { onTagClick(tag) },
                    label = {
                        Text(
                            text = tag,
                            fontSize = 12.sp
                        )
                    },
                    selected = false,
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotSearchTagsPreview() {
    SimpleJumpAppTheme {
        HotSearchTags(
            platform = SearchPlatform.XIAOHONGSHU,
            onTagClick = {}
        )
    }
}