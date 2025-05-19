package com.example.diaviseo.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember

@Composable
fun FabOverlayMenu(
    onDismiss: () -> Unit,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){

            }
    ) {
        // FAB 메뉴 버튼들
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val iconRes = when (index) {
                        0 -> R.drawable.bottom_plus_body
                        1 -> R.drawable.bottom_plus_meal
                        else -> R.drawable.bottom_plus_health
                    }

                    val label = when (index) {
                        0 -> "체중/체성분"
                        1 -> "식단"
                        else -> "운동"
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(90.dp)
                                .clickable {
                                    when (index) {
                                        0 -> {
                                            onDismiss()
                                            navController.navigate("body_register") // 체성분 등록
                                        }
                                        1 -> {
                                            onDismiss()
                                            navController.navigate("diet_register") // 식단 등록
                                        }
                                        2 -> {
                                            onDismiss()
                                            navController.navigate("exercise_register") // 운동 등록
                                        }
                                    }
                                }
                        )
                        {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // 아이콘 화질 깨짐 -> TODO: 다른 아이콘으로 변경
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 중앙 하단 FAB 위치에 고정된 X 버튼
        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-68).dp)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close_button),
                contentDescription = "닫기",
                tint = Color.Unspecified,
                modifier = Modifier.size(55.dp)
            )
        }
    }
}
