package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.register.components.SelectableCategoryRow

@Composable
fun DietSuggestionScreen() {
    var selectedCategory by remember { mutableStateOf(0) }
    val categories = listOf("전체", "인기", "세트", "음식")

    Column(modifier = Modifier.fillMaxSize()) {
        SelectableCategoryRow(
            categories = categories,
            selectedIndex = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "선택된 카테고리: ${categories[selectedCategory]}")
        }
    }
}
