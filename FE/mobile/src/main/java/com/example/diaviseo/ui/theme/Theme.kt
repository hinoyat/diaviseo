package com.example.diaviseo.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light 모드 컬러 (딱 필요한 것만)
private val LightColorScheme = lightColorScheme(
    background = Color.White, // 앱 전체 배경을 흰색으로 고정
)

// 앱 전체 테마 적용
@Composable
fun DiaViseoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme, // ✅Light 컬러만 적용
        content = content
    )
}
