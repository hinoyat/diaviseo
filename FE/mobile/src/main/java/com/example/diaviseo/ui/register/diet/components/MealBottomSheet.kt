package com.example.diaviseo.ui.register.diet.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*
import androidx.compose.ui.draw.clip
import com.example.diaviseo.ui.components.onboarding.MainButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSelectBottomSheet(
    selected: String,
    onSelect: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "기록할 끼니 선택",
                    style = bold18,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 끼니 카드 묶음
            Column(modifier = Modifier.fillMaxWidth()) {
                // [아침] [점심] [저녁] 중앙 정렬
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        listOf(
                            "아침" to R.drawable.morning,
                            "점심" to R.drawable.lunch,
                            "저녁" to R.drawable.night
                        ).forEach { (label, icon) ->
                            MealCard(
                                label = label,
                                iconRes = icon,
                                isSelected = selected == label,
                                onClick = { onSelect(label) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // [간식] 좌측 정렬
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp)
                ) {
                    MealCard(
                        label = "간식",
                        iconRes = R.drawable.apple,
                        isSelected = selected == "간식",
                        onClick = { onSelect("간식") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ 공통 메인 버튼 사용
            MainButton(
                text = "확인",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MealCard(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(105.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFE0F0FF) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = regular14,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
        )
    }
}
