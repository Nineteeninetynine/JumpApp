package com.example.simplejump.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 深色主题配色方案
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// 浅色主题配色方案
private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

/**
 * 应用主题组合函数
 */
@Composable
fun SimpleJumpAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 动态颜色在Android 12+上可用
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}