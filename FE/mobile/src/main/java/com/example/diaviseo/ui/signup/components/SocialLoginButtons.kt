// SocialLoginButtons.kt
package com.example.diaviseo.ui.signup.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import com.example.diaviseo.R

@Composable
fun SocialLoginButtons() {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            SocialButton(R.drawable.kakao_icon) { /* TODO: Kakao Login */ }
            SocialButton(R.drawable.naver_icon) { /* TODO: Naver Login */ }
            SocialButton(R.drawable.google_icon) { /* TODO: Google Login */ }
        }

        Text(
            modifier = Modifier.padding(top = 18.dp),
            textAlign = TextAlign.Center,
            text = "내 손 안에 똑똑한 건강관리\n디아비서 시작하기 🌟",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun SocialButton(iconRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = "social icon",
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() }
    )
}
