package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.DiaViseoTheme
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.regular12
import com.example.diaviseo.ui.theme.regular14
import com.example.diaviseo.ui.theme.semibold12
import com.example.diaviseo.ui.theme.semibold14

@Composable
fun AiTipBox(
    message: String? = null,
    onRequestFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        if (message != null) {
            // 피드백이 있는 경우 - 메시지와 캐릭터 + 재요청 버튼
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 메시지 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0x40000000)
                        )
                        .background(
                            color = DiaViseoColors.Callout,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = message,
                        color = DiaViseoColors.Basic,
                        style = regular14,
                        lineHeight = 20.sp
                    )
                }

                // 캐릭터를 박스 오른쪽 위에 배치
                Image(
                    painter = painterResource(id = R.drawable.charac_main_nontext),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-60).dp) // 박스 밖 오른쪽 위에 배치
                )

                // 말풍선 형태의 다시받기 버튼 (캐릭터 왼쪽에)
                Button(
                    onClick = onRequestFeedback,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-60).dp, y = (-45).dp) // 캐릭터 왼쪽에 배치
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .height(32.dp)
                        .wrapContentWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "다시받기",
                        color = DiaViseoColors.Main1,
                        style = semibold12,
                        fontSize = 10.sp
                    )
                }
            }
        } else {
            // 피드백이 없는 경우 - 빈 박스와 피드백 받기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(
                        width = 2.dp,
                        color = DiaViseoColors.BorderGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI 피드백을 받아보세요!",
                        color = DiaViseoColors.Middle,
                        style = regular14
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRequestFeedback,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .height(40.dp)
                            .wrapContentWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "피드백 받기",
                            color = DiaViseoColors.Main1,
                            style = semibold14,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}