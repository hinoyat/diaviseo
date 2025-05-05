package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium16

@Composable
fun TabSelector(
    selectedTab: String,
    onTabChange: (String) -> Unit
) {
    val tabItems = listOf(
        "식단" to R.drawable.bottom_plus_meal,
        "운동" to R.drawable.bottom_plus_health,
        "체중" to R.drawable.bottom_plus_body
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tabItems) { (label, iconRes) ->
            val isSelected = label == selectedTab
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x40000000)
                    )
                    .background(
                        color = if (isSelected) Color.White else DiaViseoColors.Deactive,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = if (!isSelected) 1.dp else 0.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabChange(label) }
                    .padding(start = 16.dp, end = 20.dp, top = 6.dp, bottom = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "$label 아이콘",
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = medium16,
                        color = DiaViseoColors.Basic
                    )
                }
            }
        }
    }
}
