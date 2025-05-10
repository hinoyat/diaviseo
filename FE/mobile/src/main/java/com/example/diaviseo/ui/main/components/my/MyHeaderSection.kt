package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun MyHeaderSection(userName: String = "김디아") {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$userName 님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DiaViseoColors.Main1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "오늘도 건강하세요!",
                fontSize = 16.sp,
                color = DiaViseoColors.Unimportant
            )
        }

        Image(
            painter = painterResource(id = R.drawable.charac_main_nontext),
            contentDescription = "다이아비서 캐릭터",
            modifier = Modifier.size(64.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyHeaderSectionPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        MyHeaderSection()
    }
}
