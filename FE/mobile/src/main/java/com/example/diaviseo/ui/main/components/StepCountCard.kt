package com.example.diaviseo.ui.main.components

import android.annotation.SuppressLint
import android.util.Log
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
    yesterdaySteps: Int,
//    viewModel: StepViewModel = viewModel()
) {
    // 현재 컴포넌트가 실행되고 있는 context를 가지고옴(mainactivity를 가지고옴)
    val activity = LocalContext.current as ComponentActivity
    val stepViewModel: StepViewModel = viewModel(activity)

    val todaySteps by stepViewModel.stepCount.collectAsState()
    Log.d("step card", "$todaySteps")

//    val diff = todaySteps - yesterdaySteps
    val diff = todaySteps
    val color = if (diff >= 0) Color(0xFFFF3434) else Color(0xFF1673FF)
    val arrow = if (diff >= 0) "▲" else "▼"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "현재 걸음 수",
                    fontSize = 14.sp,
                    color = Color(0xFF222222)
                )

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침",
                    tint = Color(0xFF222222),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            stepViewModel.refreshStepCount()
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${todaySteps} 걸음",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "어제보다 ${kotlin.math.abs(diff)} $arrow",
                fontSize = 13.sp,
                color = color
            )
        }
    }
}
