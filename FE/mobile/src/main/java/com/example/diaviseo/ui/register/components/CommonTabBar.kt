package com.example.diaviseo.ui.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.ui.theme.bold16


@Composable
fun CommonTabBar(
    tabTitles: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            tabTitles.forEachIndexed { index, title ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = title,
                        style = bold16,
                        color = if (index == selectedIndex) Color.Black else Color.Gray
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    // 회색 밑줄 + 파란 강조선 중첩
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .fillMaxWidth(1f)
                            .background(Color(0xFFEAEAEA)) // 기본 회색
                    ) {
                        if (index == selectedIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = Color(0xFF316BF3),
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
