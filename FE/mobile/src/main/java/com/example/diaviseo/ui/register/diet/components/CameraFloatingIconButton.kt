package com.example.diaviseo.ui.register.diet.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp

@Composable
fun CameraFloatingIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(75.dp)
            .background(color = Color(0xFF316BF3), shape = CircleShape)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "사진으로 등록",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "AI",
                color = Color.White,
                fontSize = 13.sp,
            )
        }
    }
}
