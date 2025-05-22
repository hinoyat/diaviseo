package com.example.diaviseo.ui.mypageedit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.DiaViseoColors

// 🔹 더미 ExerciseCategory 모델 (임시)
data class ExerciseCategory(
    val id: Int,
    val name: String
)

@Composable
fun SelectableCategory(
    categories: List<ExerciseCategory>,
    selectedCategoryId: Int?,
    onSelectCategory: (Int?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategoryId

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) DiaViseoColors.Main1 else DiaViseoColors.Callout,
                modifier = Modifier
                    .clickable { onSelectCategory(category.id) }
            ) {
                Text(
                    text = category.name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else DiaViseoColors.Basic,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectableCategoryPreview() {
    val categories = listOf(
        ExerciseCategory(1, "유산소"),
        ExerciseCategory(2, "근력"),
        ExerciseCategory(3, "수영"),
        ExerciseCategory(4, "기타")
    )
    SelectableCategory(
        categories = categories,
        selectedCategoryId = 2,
        onSelectCategory = {}
    )
}
