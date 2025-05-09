package com.example.diaviseo.ui.mypageedit.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.mypageedit.component.SelectableTag
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.register.components.CommonSearchTopBar

@Composable
fun DiseaseEditScreen(
    navController: NavHostController? = null
) {
    val diseaseList = listOf(
        "고혈압", "당뇨병", "고지혈증", "심장질환", "천식", "비염",
        "아토피", "골다공증", "갑상선 질환", "간질환", "신장질환", "위장장애"
    )
    var selected by remember { mutableStateOf(listOf<String>()) }
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchMode by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = if (isSearchMode) "기저질환 검색" else "기저질환 선택",
                onLeftActionClick = {
                    if (isSearchMode) {
                        isSearchMode = false
                        searchValue = TextFieldValue("")
                    } else {
                        navController?.popBackStack()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp) // 바텀 버튼 공간 확보
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (isSearchMode) {
                    // ✅ 기존 OutlinedTextField → CommonSearchTopBar 로 교체
                    CommonSearchTopBar(
                        placeholder = "어떤 질환이 있으신가요?",
                        navController = navController ?: rememberNavController()
                    )
                } else {
                    Text(
                        text = "현재 진단받은 기저질환이 있다면 선택해주세요",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // ✅ 선택된 항목 (검색 중이든 아니든)
                if (selected.isNotEmpty() && isSearchMode) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        selected.forEach { item ->
                            SelectableTag(
                                text = item,
                                isSelected = true,
                                onClick = { selected = selected - item }
                            )
                        }
                    }
                }

                // ✅ 알맞은 리스트 렌더링
                val filteredList = if (isSearchMode) {
                    diseaseList.filter { it.contains(searchValue.text, ignoreCase = true) }
                } else {
                    diseaseList
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredList.forEach { item ->
                        val isSelected = item in selected
                        SelectableTag(
                            text = item,
                            isSelected = isSelected,
                            onClick = {
                                selected = if (isSelected) selected - item else selected + item
                            }
                        )
                    }
                }

                if (!isSearchMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🔍 찾는 기저질환이 없나요?",
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { isSearchMode = true }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // ✅ 하단 고정 영역
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "※ 기저질환에 따라 맞춤형 건강 정보 제공에 활용됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                MainButton(
                    text = "${selected.size}개 선택 완료",
                    onClick = { navController?.popBackStack() },
                    enabled = selected.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DiseaseEditScreenPreview() {
    DiseaseEditScreen()
}
