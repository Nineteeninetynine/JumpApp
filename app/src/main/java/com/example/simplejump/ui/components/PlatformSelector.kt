package com.example.simplejump.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplejump.platform.SearchPlatform
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

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
//            value = "
//            {selectedPlatform.displayName}",
//                    onValueChange = {},
            value = selectedPlatform.displayName, // ✅ 直接使用属性值
            onValueChange = {},
            readOnly = true,
            label = { Text("选择搜索平台") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
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

@Preview(showBackground = true)
@Composable
fun PlatformSelectorPreview() {
    SimpleJumpAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PlatformSelector(
                selectedPlatform = SearchPlatform.XIAOHONGSHU,
                onPlatformSelected = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}