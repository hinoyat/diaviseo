package com.example.diaviseo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues

/**
 * 공통 상단바 컴포넌트입니다.
 *
 * - 가운데 타이틀 (22sp, 기본 weight)
 * - 우측 X 아이콘은 화면 가장 끝에 밀착
 * - status bar 겹침 방지용 WindowInsets 적용
 *
 * @param title 가운데 표시할 문자열 (nullable)
 * @param onRightActionClick 닫기 버튼 클릭 이벤트 (nullable)
 */
@Composable
fun CommonTopBar(
    title: String? = null,
    onRightActionClick: (() -> Unit)? = null
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = statusBarPadding)
            .height(56.dp)
    ) {
        // 중앙 타이틀
        title?.let {
            Text(
                text = it,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }

        // X 버튼: Box + clickable로 끝까지 정렬
        onRightActionClick?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 8.dp)
                    .size(48.dp) // 터치 영역 확보
                    .clickable { it() }, // 클릭 액션
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "닫기",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
