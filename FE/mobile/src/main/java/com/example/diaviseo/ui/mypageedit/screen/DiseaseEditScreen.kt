package com.example.diaviseo.ui.mypageedit.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun DiseaseEditScreen(
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    val diseaseList = listOf(
        "고혈압", "당뇨병", "고지혈증", "심장질환", "천식", "비염",
        "아토피", "골다공증", "갑상선 질환", "간질환", "신장질환", "위장장애"
    )

    // 상태 관리 - 빈 리스트로 시작 (실제 앱에서는 데이터를 불러와서 초기화)
    var selected by remember { mutableStateOf(listOf<String>()) }
    var initialSelected by remember { mutableStateOf(selected) } // 초기 선택 상태 저장
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchMode by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // 변경사항 감지
    val hasChanges = selected != initialSelected

    // 저장 확인 대화상자 상태
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 저장 확인 대화상자
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("변경사항 저장") },
            text = { Text("변경된 내용을 저장하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        initialSelected = selected.toList()
                        Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        showConfirmDialog = false
                        navController?.popBackStack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF1673FF) // ✅ 파란색
                    )
                ) {
                    Text("저장")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        navController?.popBackStack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray // ✅ 회색
                    )
                ) {
                    Text("저장 안 함")
                }
            },
            containerColor = Color.White
        )
    }
    BackHandler {
        // 핸드폰 뒤로가기까지 포함해서 처리
        if (isSearchMode) {
            isSearchMode = false
            searchValue = TextFieldValue("")
        } else if (hasChanges) {
            showConfirmDialog = true
        } else {
            navController?.popBackStack()
        }
    }
    Scaffold(
        topBar = {
            Column {
                CommonTopBar(
                    title = if (isSearchMode) "기저질환 검색" else "기저질환 선택",
                    onLeftActionClick = {
                        if (isSearchMode) {
                            isSearchMode = false
                            searchValue = TextFieldValue("")
                        } else if (hasChanges) {
                            // 변경사항이 있으면 저장 확인 대화상자 표시
                            showConfirmDialog = true
                        } else {
                            navController?.popBackStack()
                        }
                    }
                )

                // 변경사항 표시 배너
                AnimatedVisibility(
                    visible = hasChanges,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE6F7FF))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "변경사항이 있습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF0066CC)
                        )

                        TextButton(
                            onClick = {
                                selected = initialSelected.toList()
                                Toast.makeText(context, "변경사항이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF0066CC)
                            )
                        ) {
                            Text("취소")
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        // 단일 Column으로 모든 콘텐츠를 포함 (하단 버튼까지)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding() // 기기 네비게이션 바 고려
                .padding(bottom = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 초기 선택 알림 (첫 진입 시)
            if (selected == initialSelected && selected.isNotEmpty() && !isSearchMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selected.size}개의 기저질환이 이미 선택되어 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DiaViseoColors.Basic
                    )
                }
            }

            if (isSearchMode) {
                CommonSearchTopBar(
                    placeholder = "어떤 질환이 있으신가요?",
                    navController = navController ?: rememberNavController(),
                    keyword = searchValue.text,
                    onKeywordChange = { searchValue = TextFieldValue(it) }
                )
            } else {
                Text(
                    text = "현재 진단받은 기저질환이 있다면 선택해주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // 선택된 항목 (검색 중이든 아니든)
            if (selected.isNotEmpty()) {
                Text(
                    text = "선택된 기저질환 (${selected.size}개)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    selected.forEach { item ->
                        SelectableTag(
                            text = item,
                            isSelected = true,
                            onClick = { selected = selected - item }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // 알맞은 리스트 렌더링
            Text(
                text = if (isSearchMode) {
                    if (diseaseList.any { it.contains(searchValue.text, ignoreCase = true) })
                        "검색 결과"
                    else
                        "검색 결과가 없습니다"
                } else {
                    "기저질환 목록"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

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
                    val isItemSelected = item in selected
                    SelectableTag(
                        text = item,
                        isSelected = isItemSelected,
                        onClick = {
                            selected = if (isItemSelected) selected - item else selected + item
                        }
                    )
                }
            }

            // 검색 결과가 없을 때 메시지
            if (isSearchMode && filteredList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "찾는 기저질환이 목록에 없습니다",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!isSearchMode) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔍 찾는 기저질환이 없나요?",
                        color = DiaViseoColors.Unimportant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { isSearchMode = true }
                    )
                }
            }

            // 하단 영역을 메인 컬럼으로 이동
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "※ 기저질환에 따라 맞춤형 건강 정보 제공에 활용됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 완료 버튼 - 텍스트와 동작을 변경사항 유무에 따라 다르게 설정
            MainButton(
                text = if (hasChanges)
                    "${selected.size}개 선택 저장하기"
                else
                    "${selected.size}개 선택 완료",
                onClick = {
                    if (hasChanges) {
                        // 변경사항이 있으면 저장 로직 실행 후 화면 이동
                        initialSelected = selected.toList()
                        Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    // 화면 이동
//                    navController?.popBackStack()
                },
                enabled = selected.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DiseaseEditScreenPreview() {
    DiseaseEditScreen()
}