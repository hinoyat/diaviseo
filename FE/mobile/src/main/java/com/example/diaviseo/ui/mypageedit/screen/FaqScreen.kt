package com.example.diaviseo.ui.mypageedit.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.theme.DiaViseoColors

data class FaqItem(val question: String, val answer: String)

@Composable
fun FaqScreen(navController: NavHostController) {
    val faqList = listOf(
        FaqItem("앱 사용 방법은 어떻게 되나요?", "앱은 마이페이지에서 정보를 입력하고, 각 기능을 탐색하며 사용할 수 있습니다."),
        FaqItem("내 정보는 어디서 수정할 수 있나요?", "마이페이지 > 회원정보 수정에서 수정 가능합니다."),
        FaqItem("알림이 오지 않아요", "알림 설정이 켜져 있는지 확인해주세요. 설정 > 알림관리에서 확인할 수 있습니다."),
        FaqItem("운동 추천은 어떻게 받나요?", "신체 정보 및 선호 운동을 설정하면 AI가 추천해줍니다."),
        FaqItem("데이터는 어디에 저장되나요?", "모든 데이터는 안전하게 서버에 저장됩니다.")
    )

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "자주 묻는 질문",
                onLeftActionClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(faqList) { faq ->
                FaqExpandableItem(faq)
            }
        }
    }
}

@Composable
fun FaqExpandableItem(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = DiaViseoColors.Callout,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    fontSize = 16.sp,
                    color = DiaViseoColors.Basic,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "접기" else "펼치기",
                    tint = DiaViseoColors.Middle
                )
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = faq.answer,
                    fontSize = 14.sp,
                    color = DiaViseoColors.Unimportant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, end = 4.dp)
                )
            }
        }
    }
}