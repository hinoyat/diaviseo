package com.example.diaviseo.ui.main.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.theme.*

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    iconResId: Int,
    current: Int,
    goal: Int,
    goalExceeded: Boolean,
    destinationRoute: String,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x26000000),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable {
//                navController.navigate(destinationRoute)
            }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "아이콘",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(36.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = medium16,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$current kcal",
                style = bold20,
                color = if (goalExceeded) Color(0xFFFF3434) else Color(0xFF1673FF)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "/ $goal kcal",
                style = regular12,
                color = Color(0xFF999999)
            )
        }
    }
}
