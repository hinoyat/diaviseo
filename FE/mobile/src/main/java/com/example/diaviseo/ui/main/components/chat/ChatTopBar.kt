package com.example.diaviseo.ui.main.components.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String = "디아비서 챗봇",
    onBackClick: () -> Unit,
    onExitClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = DiaViseoColors.Basic,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = DiaViseoColors.Basic
                )
            }
        },
        actions = {
            IconButton(onClick = onExitClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "대화 종료",
                    tint = DiaViseoColors.Red
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewChatTopBar() {
    ChatTopBar(
        onBackClick = {},
        onExitClick = {}
    )
}
