package com.example.diaviseo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddRemoveIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        CircleRemoveButton(
            onClick = onClick,
            modifier = modifier
        )
    } else {
        CircleAddButton(
            onClick = onClick,
            modifier = modifier
        )
    }
}
