package com.example.diaviseo.ui.detail.components.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.*

@Composable
fun BodyInfoCard(
    skeletalMuscle: Double?,
    bodyFat: Double?,
    onSkeletalEdit: () -> Unit,
    onBodyFatEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ✅ Row 안에 있으므로 weight() 정상 사용 가능
        InfoBox(
            modifier = Modifier.weight(1f),
            title = "골격근량",
            value = skeletalMuscle?: 0.00,
            onEditClick = onSkeletalEdit
        )
        InfoBox(
            modifier = Modifier.weight(1f),
            title = "체지방량",
            value = bodyFat?: 0.00,
            onEditClick = onBodyFatEdit
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun InfoBox(
    modifier: Modifier = Modifier,
    title: String,
    value: Double?,
    onEditClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(90.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(
                color = DiaViseoColors.Callout,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = medium16,
                color = DiaViseoColors.Basic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남는 공간에 중앙정렬
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", value),
                    style = medium20,
                    color = DiaViseoColors.Basic
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.edit),
            contentDescription = "edit",
            tint = DiaViseoColors.Basic,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(18.dp)
                .clickable { onEditClick() }
        )
    }
}
