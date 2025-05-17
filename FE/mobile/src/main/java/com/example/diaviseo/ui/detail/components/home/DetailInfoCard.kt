package com.example.diaviseo.ui.detail.components.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.bold20
import com.example.diaviseo.ui.theme.medium16
import com.example.diaviseo.ui.theme.medium20

@SuppressLint("DefaultLocale")
@Composable
fun DetailInfoCard(
    nickname: String,
    height: Double?,
    weight: Double?,
    bmr: Double?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 닉네임 제목
            Text(
                text = "$nickname 님의 신체 정보",
                style = bold20,
                color = DiaViseoColors.Basic,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 캐릭터 + 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.charac_info),
                    contentDescription = "캐릭터",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoStrokeBox(label = "키 (cm)", value = String.format("%.1f", height))
                    InfoStrokeBox(label = "몸무게 (kg)", value = String.format("%.1f", weight))
                    InfoStrokeBox(label = "기초대사량 (kcal)", value = String.format("%.0f", bmr))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 수정하기 버튼
            MainButton(
                text = "수정하기",
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoStrokeBox(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, DiaViseoColors.Placeholder)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = medium16,
                color = DiaViseoColors.Basic
            )
            Text(
                text = value,
                style = medium20,
                color = DiaViseoColors.Basic
            )
        }
    }
}
