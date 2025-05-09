package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyAlarmSettingCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "알림 설정", fontSize = 14.sp)
            Switch(checked = isEnabled, onCheckedChange = onToggle)
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
