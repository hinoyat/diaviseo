package com.example.diaviseo.ui.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.medium14

@Composable
fun SelectableCategoryRow(
    categories: List<String>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = index == selectedIndex
            val backgroundColor = if (isSelected) Color(0xFF316BF3) else Color.White
            val borderColor = if (isSelected) Color.Transparent else Color(0xFFE0E0E0)
            val textColor = if (isSelected) Color.White else Color.Gray

            Box(
                modifier = Modifier
                    .height(36.dp)
                    .defaultMinSize(minWidth = 60.dp)
                    .padding(vertical = 4.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(backgroundColor, shape = RoundedCornerShape(20.dp))
                    .clickable { onCategorySelected(index) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = medium14,
                    color = textColor
                )
            }
        }
    }
}
