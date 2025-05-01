package com.example.diaviseo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import com.example.diaviseo.ui.components.onboarding.MainButton

@Composable
fun BottomButtonSection(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column {
        MainButton(
            text = text,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        )
        Spacer(
            modifier = Modifier.height(
                WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding() + 40.dp
            )
        )
    }
}
