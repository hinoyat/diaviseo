package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun MyAlarmSettingCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "알림 설정",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DiaViseoColors.Basic
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DiaViseoColors.Main1,
                    checkedTrackColor = DiaViseoColors.Main1.copy(alpha = 0.5f),
                    uncheckedThumbColor = DiaViseoColors.Deactive,
                    uncheckedTrackColor = DiaViseoColors.Deactive.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAlarmSettingCardPreview() {
    var toggle by remember { mutableStateOf(true) }
    MyAlarmSettingCard(
        isEnabled = toggle,
        onToggle = { toggle = it }
    )
}
