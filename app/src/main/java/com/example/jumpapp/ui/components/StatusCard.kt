package com.example.simplejump.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

@Composable
fun StatusCard(
    message: String,
    modifier: Modifier = Modifier,
    isInfo: Boolean = false
) {
    val backgroundColor = when {
        isInfo -> MaterialTheme.colorScheme.surfaceVariant
        message.contains("成功") || message.contains("✅") -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        message.contains("失败") || message.contains("❌") -> Color(0xFFf44336).copy(alpha = 0.1f)
        else -> Color(0xFFFF9800).copy(alpha = 0.1f)
    }

    val textColor = if (isInfo) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusCardPreview() {
    SimpleJumpAppTheme {
        StatusCard(
            message = "✅ 成功跳转搜索页面",
            modifier = Modifier.padding(16.dp)
        )
    }
}