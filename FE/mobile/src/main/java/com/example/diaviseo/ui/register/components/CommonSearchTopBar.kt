package com.example.diaviseo.ui.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.regular14

@Composable
fun CommonSearchTopBar(
    placeholder: String,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            )
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 검색 입력 영역
        Row(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(15.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = placeholder,
                style = regular14,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 취소 버튼
        Text(
            text = "취소",
            style = medium14,
            color = Color.Black,
            modifier = Modifier
                .clickable { navController.popBackStack() }
        )
    }
}
