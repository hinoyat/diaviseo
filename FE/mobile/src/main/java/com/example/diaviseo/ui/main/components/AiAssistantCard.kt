package com.example.diaviseo.ui.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.medium16

@Composable
fun AiAssistantCard(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable {
//                navController.navigate("ai_chat")
            }
            .padding(16.dp)
    ) {
        // 전체 레이아웃
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI 디아비서에게 물어보러 가기",
                    style = medium16,
                    color = Color(0xFF222222),
                    modifier = Modifier.weight(1f)
                )

//                Icon(
//                    imageVector = Icons.Default.ChevronRight,
//                    contentDescription = "이동",
//                    tint = Color(0xFF1673FF)
//                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.V
            ) {
                // 왼쪽 blur 이미지 (예시 텍스트 느낌)
                Image(
                    painter = painterResource(id = R.drawable.main_ai_chat),
                    contentDescription = "AI 예시",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.charac_main),
                        contentDescription = "디아비서 캐릭터",
                        modifier = Modifier.size(84.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))


                }
            }
        }
    }
}