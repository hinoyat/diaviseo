package com.example.diaviseo.ui.components.onboarding

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.diaviseo.ui.theme.DiaViseoColors
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.diaviseo.ui.theme.*

/**
 * [MainButton] - 디아비전 앱 전용 공통 버튼 컴포저블
 *
 * @param text 버튼에 표시할 텍스트
 * @param enabled 버튼 활성화 여부 (기본값: true)
 * @param modifier 버튼의 Modifier (크기, 패딩 등 커스터마이징)
 * @param onClick 버튼 클릭 시 수행할 동작
 *
 * 버튼 색상:
 * - 활성화: DiaViseoColors.Main1
 * - 비활성화: DiaViseoColors.Deactive
 */

@Composable
fun MainButton(
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick : () -> Unit
){
    val backgroundColor = if(enabled) DiaViseoColors.Main1 else DiaViseoColors.Deactive
    val textColor = Color.White

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = textColor
        )
    )
        {
            Text(
                text=text,
                style = regular16
            )
        }

}