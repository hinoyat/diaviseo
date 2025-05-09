package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.main.MyScreen

@Composable
fun MyHeaderSection(userName: String = "김디아") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$userName 님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF0066CC) // 파란색
            )
            Text(
                text = "오늘도 건강하세요!",
                fontSize = 16.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.charac_main_nontext), // 너희 다이아비서 캐릭터 이미지 res에 넣은 이름
            contentDescription = "다이아비서 캐릭터",
            modifier = Modifier
                .size(64.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MyHeaderSectionPreview() {
    MyHeaderSection()
}
