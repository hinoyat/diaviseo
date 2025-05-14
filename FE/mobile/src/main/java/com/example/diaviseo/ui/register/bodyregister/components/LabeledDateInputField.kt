package com.example.diaviseo.ui.register.bodyregister.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diaviseo.ui.theme.bold16
import com.example.diaviseo.ui.theme.medium14


@Composable
fun LabeledDateInputField(
    year: String,
    month: String,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Text(
            text = "측정일자",
            style = bold16,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .width(170.dp)
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFCCCCCC),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            DateInputField(
                value = year,
                onValueChange = onYearChange,
                modifier = Modifier.width(56.dp),
                placeholder = "YYYY"
            )
            Text(text = "/", style = medium14)
            DateInputField(
                value = month,
                onValueChange = onMonthChange,
                modifier = Modifier.width(30.dp),
                placeholder = "MM"
            )
            Text(text = "/", style = medium14)
            DateInputField(
                value = day,
                onValueChange = onDayChange,
                modifier = Modifier.width(30.dp),
                placeholder = "DD"
            )
        }
    }
}

@Composable
private fun DateInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        textAlign = TextAlign.Center
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = textStyle.copy(color = Color.Gray.copy(alpha = 0.5f)),
                textAlign = TextAlign.Center
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
