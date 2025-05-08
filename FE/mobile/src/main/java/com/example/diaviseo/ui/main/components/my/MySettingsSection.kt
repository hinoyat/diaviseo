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
fun MySettingsSection(
    onAlarmClick: () -> Unit = {},
    onInquiryClick: () -> Unit = {},
    onFaqClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "설정관리",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CardListItem(title = "알림 설정", onClick = onAlarmClick)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "문의하기",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CardListItem(title = "1:1 문의하기", onClick = onInquiryClick)
        CardListItem(title = "자주 묻는 질문 (FAQ)", onClick = onFaqClick)
    }
}

@Composable
private fun CardListItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MySettingsSectionPreview() {
    MySettingsSection()
}
