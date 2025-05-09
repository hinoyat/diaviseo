package com.example.diaviseo.ui.mypageedit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectableTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF0066CC) else Color(0xFFF2F2F2)
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .clickable { onClick() }
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectableTagPreview() {
    Column(Modifier.padding(16.dp)) {
        SelectableTag(text = "계란", isSelected = false, onClick = {})
        Spacer(modifier = Modifier.height(8.dp))
        SelectableTag(text = "우유", isSelected = true, onClick = {})
    }
}
