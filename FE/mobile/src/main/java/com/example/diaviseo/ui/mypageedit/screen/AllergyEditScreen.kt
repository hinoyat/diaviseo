package com.example.diaviseo.ui.mypageedit.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun AllergyEditScreen(
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    val allergyList = listOf(
        "계란", "우유", "땅콩", "복숭아", "게", "새우", "고등어",
        "꽃가루", "밀", "대두", "유당분해물", "MSG 민감", "카페인 민감"
    )

    // 상태 관리 - 더미 데이터로 시작
    var selected by remember { mutableStateOf(listOf("우유", "땅콩", "대두")) }
    var initialSelected by remember { mutableStateOf(selected) }
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
                    title = if (isSearchMode) "알러지 검색" else "알러지 선택",
                    onLeftActionClick = {
                        if (isSearchMode) {
                            isSearchMode = false
                            searchValue = TextFieldValue("")
                        } else if (hasChanges) {
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
                        text = "${selected.size}개의 알러지가 이미 선택되어 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DiaViseoColors.Basic
                    )
                }
            }

            if (isSearchMode) {
                CommonSearchTopBar(
                    placeholder = "어떤 알러지가 있으신가요?",
                    navController = navController ?: rememberNavController(),
                    keyword = searchValue.text,
                    onKeywordChange = {
                        searchValue = TextFieldValue(it)
                    }
                )
            } else {
                Text(
                    text = "섭취 시 알러지가 반응이 일어나는 알러지를 선택해주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 선택된 알러지 섹션
            if (selected.isNotEmpty()) {
                Text(
                    text = "선택된 알러지 (${selected.size}개)",
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // 필터링된 알러지 목록
            val filteredList = if (isSearchMode) {
                allergyList.filter { it.contains(searchValue.text, ignoreCase = true) }
            } else {
                allergyList
            }

            Text(
                text = if (isSearchMode) {
                    if (filteredList.isEmpty()) "검색 결과가 없습니다" else "검색 결과"
                } else {
                    "알러지 목록"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

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
                        text = "찾는 알러지가 목록에 없습니다",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // "찾는 알러지가 없나요?" 메시지
            if (!isSearchMode) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔍 찾는 알러지가 없나요?",
                        color = DiaViseoColors.Unimportant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            isSearchMode = true
                            searchValue = TextFieldValue("")
                        }
                    )
                }
            }

            // 버튼 영역 - 메인 컬럼 내부로 이동
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "※ 입력한 정보가 필요한 알러지가 있는 경우 전문가에게 상담을 권장합니다.\n일부 데이터는 고려되지 않을 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 완료 버튼
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
fun AllergyEditScreenPreview() {
    AllergyEditScreen()
}