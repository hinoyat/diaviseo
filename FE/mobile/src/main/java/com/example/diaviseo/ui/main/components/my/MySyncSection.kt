package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MySyncSection(
    onConnectClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "건강 데이터 연동",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "헬스 커넥트", fontSize = 14.sp)
                Text(
                    text = "연결하기",
                    fontSize = 14.sp,
                    color = Color(0xFF0066CC),
                    modifier = Modifier.clickable { onConnectClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MySyncSectionPreview() {
    MySyncSection()
}
