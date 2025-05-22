package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.diaviseo.ui.theme.*

@Composable
fun MyPhysicalInfoCard(
    height: Double,
    weight: Double,
    onEditClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "나의 신체정보",
            style = semibold16
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditClick() }
            ,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "키", style = medium14, color = DiaViseoColors.Unimportant)
                    Text(
                        text = String.format("%.1f cm", height),
                        style = regular16
                    )
                }

                Divider(
                    color = DiaViseoColors.BorderGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "몸무게",style = medium14, color = DiaViseoColors.Unimportant)
                    Text(
                        text = String.format("%.1f kg", weight),
                        style = regular16
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPhysicalInfoCardPreview() {
    MyPhysicalInfoCard(
        height = 172.3,
        weight = 63.5,
        onEditClick = {}
    )
}
