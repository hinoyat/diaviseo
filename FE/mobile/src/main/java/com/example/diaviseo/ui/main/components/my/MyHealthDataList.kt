package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun MyHealthDataList(
    onEditAllergy: () -> Unit = {},
    onEditDisease: () -> Unit = {}
) {
    Column {
        Text(
            text = "나의 건강 데이터",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = DiaViseoColors.Basic,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 알러지 항목
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "알러지", fontSize = 14.sp, color = DiaViseoColors.Unimportant)
                Text(
                    text = "수정",
                    fontSize = 14.sp,
                    color = DiaViseoColors.Unimportant,
                    modifier = Modifier.clickable { onEditAllergy() }
                )
            }
        }

        // 기저질환 항목
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "기저질환", fontSize = 14.sp, color = DiaViseoColors.Unimportant)
                Text(
                    text = "수정",
                    fontSize = 14.sp,
                    color = DiaViseoColors.Unimportant,
                    modifier = Modifier.clickable { onEditDisease() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyHealthDataListPreview() {
    MyHealthDataList()
}
