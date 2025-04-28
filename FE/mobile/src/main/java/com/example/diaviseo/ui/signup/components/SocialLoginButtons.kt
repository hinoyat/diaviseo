package com.example.diaviseo.ui.signup.components

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.R
import androidx.compose.ui.platform.LocalContext

@Composable
fun SocialLoginButtons(
    onGoogleLoginClicked: (Activity) -> Unit,
    onKakaoLoginClicked: () -> Unit,
    onNaverLoginClicked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            SocialButton(R.drawable.kakao_icon) {
                onKakaoLoginClicked()
            }
            SocialButton(R.drawable.naver_icon) {
                onNaverLoginClicked()
            }
            SocialButton(R.drawable.google_icon) {
                onGoogleLoginClicked(activity)
            }
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
private fun SocialButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = "social icon",
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() }
    )
}
