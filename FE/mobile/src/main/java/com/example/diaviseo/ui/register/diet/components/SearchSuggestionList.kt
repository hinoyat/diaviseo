package com.example.diaviseo.ui.register.diet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.diaviseo.network.FoodItem

@Composable
fun SearchSuggestionList(
    results: List<FoodItem>,
    modifier: Modifier = Modifier,
    onItemClick: (FoodItem) -> Unit
) {
    Surface(
        modifier = modifier
            .zIndex(10f),
//        shadowElevation = 6.dp,
        shape = RoundedCornerShape(15.dp),
        color = Color(0xFFF5F5F5)
    ) {
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "검색 결과가 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(results) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
//                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                }
            }
        }
    }
}
