package com.example.diaviseo.ui.main.components

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.diaviseo.ui.theme.*

@Composable
fun MainHeader(userNickname: String, navController: NavHostController) {
    val today = remember { Calendar.getInstance().time }
    val formatter = remember { SimpleDateFormat("M월 d일 E요일", java.util.Locale.KOREA) }
    val formattedDate = formatter.format(today)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = formattedDate,  // 오늘 날짜
                style = bold20,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // “어서오세요 김디아 님!”
            Text(
                buildAnnotatedString {
                    append("어서오세요 ")
                    withStyle(
                        style = SpanStyle(
                            background = Color(0x661673FF), // 40% 불투명도 main1
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append(userNickname)
                    }
                    append(" 님!")
                },
                style = medium20,
                color = Color(0xFF222222)
            )
        }

        // 알림 버튼
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "알림",
            tint = Color(0xFF1673FF),
            modifier = Modifier
                .size(28.dp)
                .padding(top = 4.dp)
                .clickable {
//                    navController.navigate("notification")
                }
        )
    }
}