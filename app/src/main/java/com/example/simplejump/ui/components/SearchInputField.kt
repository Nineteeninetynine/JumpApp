package com.example.simplejump.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplejump.ui.theme.SimpleJumpAppTheme

@Composable
fun SearchInputField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "例如：美食推荐、穿搭技巧..."
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        label = { Text("输入搜索内容") },
        placeholder = { Text(placeholder) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearchSubmit()
            }
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchInputFieldPreview() {
    SimpleJumpAppTheme {
        SearchInputField(
            searchText = "美食推荐",
            onSearchTextChange = {},
            onSearchSubmit = {}
        )
    }
}