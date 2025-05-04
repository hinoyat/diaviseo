package com.example.diaviseo.ui.main.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaviseo.viewmodel.StepViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun StepCountCard(
    yesterdaySteps: Int = 0,  // MainActivity나 다른 곳에서 어제 값 전달 가능
) {
//    val activity = LocalContext.current as ComponentActivity
//    val stepViewModel = viewModel<StepViewModel>(activity)
    val stepViewModel: StepViewModel = viewModel()


    // ViewModel의 오늘/어제 걸음 수 StateFlow 구독
    val today by stepViewModel.todaySteps.collectAsState()
    val yesterday by stepViewModel.yesterdaySteps.collectAsState()

    val diff = today - yesterday
    val arrow = if (diff >= 0) "▲" else "▼"
    val color = if (diff >= 0) Color(0xFFFF3434) else Color(0xFF1673FF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(12.dp), spotColor = Color(0x26000000))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            // 제목 + 새로고침 버튼
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("현재 걸음 수", fontSize = 14.sp, color = Color(0xFF222222))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침",
                    tint = Color(0xFF222222),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { stepViewModel.startListening() }
                )
            }
            Spacer(Modifier.height(8.dp))

            // 오늘 걸음 수
            Text("$today 걸음", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(4.dp))
            // 어제 대비 증감
            Text(
                "어제보다 ${kotlin.math.abs(diff)} $arrow",
                fontSize = 13.sp,
                color = color
            )
        }
    }
}