package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.register.components.PhotoUploadHintBox

@Composable
fun DietAiRegisterScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 패딩 적용된 상단 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            CommonTopBar(
                title = "AI 식단 등록",
                onLeftActionClick = { navController.popBackStack() }
            )
        }

        // 패딩 없는 하단 영역 (업로드 박스 등)
        PhotoUploadHintBox(
            onClick = {
                // TODO: 이미지 업로드 기능 연결
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp) // 선택적으로 필요
        )
    }
}
