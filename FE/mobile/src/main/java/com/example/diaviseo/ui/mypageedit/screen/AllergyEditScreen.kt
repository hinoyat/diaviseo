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
import com.example.diaviseo.viewmodel.condition.AllergyViewModel

@Composable
fun AllergyEditScreen(
    navController: NavHostController? = null,
    viewModel: AllergyViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val allergyList = viewModel.allergyList
    val userAllergySet = viewModel.userAllergySet
    val initialAllergySet = viewModel.initialUserAllergySet

    var searchValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchMode by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val hasChanges = userAllergySet != initialAllergySet

    // 로딩
    LaunchedEffect(Unit) {
        viewModel.loadAllergyData()
    }

    // 뒤로가기 핸들링
    BackHandler {
        if (isSearchMode) {
            isSearchMode = false
            searchValue = TextFieldValue("")
        } else if (hasChanges) {
            showConfirmDialog = true
        } else {
            navController?.popBackStack()
        }
    }

    // 저장 확인 다이얼로그
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("변경사항 저장") },
            text = { Text("변경된 내용을 저장하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.commitChanges()
                        Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        showConfirmDialog = false
                        navController?.popBackStack()
                    }
                ) { Text("저장", color = DiaViseoColors.Main1) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.revertChanges()
                        showConfirmDialog = false
                        navController?.popBackStack()
                    }
                ) { Text("저장 안 함", color = DiaViseoColors.Unimportant) }
            },
            containerColor = Color.White
        )
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
                            color = Color(0xFF0066CC)
                        )
                        TextButton(
                            onClick = {
                                viewModel.revertChanges()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (userAllergySet == initialAllergySet && userAllergySet.isNotEmpty() && !isSearchMode) {
                Text(
                    text = "${userAllergySet.size}개의 알러지가 이미 선택되어 있습니다",
                    fontWeight = FontWeight.SemiBold,
                    color = DiaViseoColors.Basic,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
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
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 선택된 알러지
            if (userAllergySet.isNotEmpty()) {
                Text(
                    text = "선택된 알러지 (${userAllergySet.size}개)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    userAllergySet.forEach { id ->
                        allergyList.find { it.allergyId == id }?.let { allergy ->
                            SelectableTag(
                                text = allergy.allergyName,
                                isSelected = true,
                                onClick = { viewModel.toggleAllergy(id) }
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            val filteredList = if (isSearchMode) {
                allergyList.filter {
                    it.allergyName.contains(searchValue.text, ignoreCase = true)
                }
            } else allergyList

            Text(
                text = if (isSearchMode) {
                    if (filteredList.isEmpty()) "검색 결과가 없습니다" else "검색 결과"
                } else "알러지 목록",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredList.forEach { allergy ->
                    SelectableTag(
                        text = allergy.allergyName,
                        isSelected = viewModel.isSelected(allergy.allergyId),
                        onClick = { viewModel.toggleAllergy(allergy.allergyId) }
                    )
                }
            }

            if (isSearchMode && filteredList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("찾는 알러지가 목록에 없습니다")
                }
            }

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
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            isSearchMode = true
                            searchValue = TextFieldValue("")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "※ 입력한 정보가 필요한 알러지가 있는 경우 전문가에게 상담을 권장합니다.\n일부 데이터는 고려되지 않을 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MainButton(
                text = if (hasChanges)
                    "${userAllergySet.size}개 선택 저장하기"
                else
                    "${userAllergySet.size}개 선택 완료",
                onClick = {
                    if (hasChanges) {
                        viewModel.commitChanges()
                        Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    navController?.popBackStack()
                },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
