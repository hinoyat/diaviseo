package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.regular12
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NutrientInfoNotice(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)) // 💡 둥근 테두리도 추가
            .padding(26.dp)
    ) {
        Text(
            text = "• 식품의 영양성분정보는 수확물의 품종, 발육, 생장환경 등에 따라 달라질 수 있으며, 조리법에 따라 달라질 수 있습니다. 계산된 칼로리 및 성분 정보는 평균적인 수치로 참고용으로 사용해야하며, 일부 정보에 오류가 있거나 누락이 있을 수 있습니다.",
            style = regular12,
            color = Color.Gray,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "• 위 음식은 국민영양통계를 기준으로 작성된 영양성분 입니다.",
            style = regular12,
            color = Color.Gray,
            textAlign = TextAlign.Justify
        )
    }
}
