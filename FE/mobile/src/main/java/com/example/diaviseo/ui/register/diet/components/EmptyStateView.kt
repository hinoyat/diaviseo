package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium18

@Composable
fun EmptyStateView(
    tabType: EmptyTabType,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp
) {
    val message = when (tabType) {
        EmptyTabType.RECENT_FOOD -> "최근 먹은 음식들이\n앞으로 채워질거예요!"
        EmptyTabType.FOOD_SET -> "나만의 루틴 세트들을 등록하고\n편하게 기록하세요!"
        EmptyTabType.FAVORITE -> "추가하신 즐겨찾기 메뉴들이\n앞으로 채워질거예요"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_recent_food),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = medium18,
            color = DiaViseoColors.Placeholder,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
