package com.example.diaviseo.ui.main.components.home

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.viewmodel.ProfileViewModel
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainHeader(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val today = remember { Calendar.getInstance().time }
    val formatter = remember { SimpleDateFormat("M월 d일 E요일", Locale.KOREA) }
    val formattedDate = formatter.format(today)

    val myProfile by viewModel.myProfile.collectAsState()
    LaunchedEffect(myProfile) {
        Log.d("MainHeader", "nickname 값 변경 감지: ${myProfile?.nickname}")
    }

    val nickname by remember(myProfile) {
        mutableStateOf(myProfile?.nickname)
    }

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
//            if (userNickname != null) {
//            if (myProfile?.nickname != null) {
            if (nickname != null) {
                Text(
                    buildAnnotatedString {
                        append("어서오세요 ")
                        withStyle(
                            style = SpanStyle(
                                background = Color(0x661673FF), // 40% 불투명도 main1
                                fontWeight = FontWeight.Normal
                            )
                        ) {
//                            append(userNickname)
//                            append(myProfile?.nickname)
                            append(nickname)
                        }
                        append(" 님!")
                    },
                    style = medium20,
                    color = Color(0xFF222222)
                )
            } else {
                CircularProgressIndicator()
            }
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