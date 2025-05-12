package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.diaviseo.R

@Composable
fun FoodSetCard(
    title: String,
    foodList: List<Pair<String, Int>>, // 음식 이름 + 칼로리
    onClickRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            // 상단 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 + 아이콘
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.food_set),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = title, style = semibold17)
                }

                // 음식 리스트
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    foodList.forEach { (name, kcal) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, style = regular15)
                            Text(text = "${kcal}kcal", style = regular15)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = DiaViseoColors.Main1.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .clickable { onClickRecord() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "이 조합으로 기록하기",
                    style = regular14,
                    color = Color.White
                )
            }
        }
    }
}
