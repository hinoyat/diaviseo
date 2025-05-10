package com.example.diaviseo.ui.mypageedit.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.register.components.CommonSearchTopBar

@Composable
fun AllergyEditScreen(
    navController: NavHostController? = null
) {
    val allergyList = listOf(
        "계란", "우유", "땅콩", "복숭아", "게", "새우", "고등어",
        "꽃가루", "밀", "대두", "유당분해물", "MSG 민감", "카페인 민감"
    )
    var selected by remember { mutableStateOf(listOf<String>()) }
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchMode by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = if (isSearchMode) "알러지 검색" else "알러지 선택",
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
                    .padding(bottom = 100.dp) // 하단 고정 영역 피해서
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (isSearchMode) {
                    CommonSearchTopBar(
                        placeholder = "어떤 알러지가 있으신가요?",
                        navController = navController ?: rememberNavController(),
                        keyword = "바보바보바보 allergyeditscreen 입니다",
                        onKeywordChange = {/*컴포넌트 만들고 새롭게 추가한 파라미터인가 보구만...*/}
                    )
                } else {
                    Text(
                        text = "섭취 시 알러지가 반응이 일어나는 알러지를 선택해주세요",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

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

                val filteredList = if (isSearchMode) {
                    allergyList.filter { it.contains(searchValue.text, ignoreCase = true) }
                } else {
                    allergyList
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
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🔍 찾는 알러지가 없나요?",
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { isSearchMode = true }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // 하단 고정 버튼 + 안내 문구
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
                    text = "※ 입력한 정보가 필요한 알러지가 있는 경우 전문가에게 상담을 권장합니다.\n일부 데이터는 고려되지 않을 수 있습니다.",
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
fun AllergyEditScreenPreview() {
    AllergyEditScreen()
}
