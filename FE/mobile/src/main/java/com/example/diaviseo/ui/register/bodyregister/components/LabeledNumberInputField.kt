package com.example.diaviseo.ui.register.bodyregister.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.*

@Composable
fun LabeledNumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Text(
            text = label,
            style = bold16,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = modifier
                .height(48.dp)
                .then(
                    if (onClick != null) Modifier.clickable { onClick() } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                textStyle = medium14,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxSize(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF516AF0),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    disabledBorderColor = Color(0xFFEEEEEE),
                    cursorColor = Color(0xFF516AF0)
                ),
                trailingIcon = {
                    unit?.let {
                        Text(
                            text = it,
                            style = medium14,
                            color = if (value.isBlank()) Color.Gray else Color.Black
                        )
                    }
                },
                placeholder = {
                    placeholder?.let {
                        Text(text = it, style = medium14, color = Color.Gray)
                    }
                },

                )
        }
    }
}
