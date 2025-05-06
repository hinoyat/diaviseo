package com.example.diaviseo.ui.main.components.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.regular12
import com.example.diaviseo.ui.theme.regular14

@Composable
fun AiTipBox(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0x60000000)
                )
                .background(
                    color = DiaViseoColors.Callout,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = DiaViseoColors.Basic,
                style = regular14,
                lineHeight = 20.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.charac_main_nontext),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(80.dp)
                // BoxScope에서 우상단 기준
                .align(Alignment.TopEnd)
                // X축은 0dp(우측 끝), Y축은 -40dp(위로 반만큼)
                .offset(x = 0.dp, y = (-64).dp)
        )
    }
}
