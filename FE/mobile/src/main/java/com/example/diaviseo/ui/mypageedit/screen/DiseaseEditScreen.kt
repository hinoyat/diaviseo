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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.mypageedit.component.SelectableTag
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.condition.DiseaseViewModel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun DiseaseEditScreen(
    navController: NavHostController? = null,
    viewModel: DiseaseViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val diseaseList = viewModel.diseaseList
    val userDiseaseSet = viewModel.userDiseaseSet
    val initialDiseaseSet = viewModel.initialUserDiseaseSet

    var searchValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchMode by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val hasChanges = userDiseaseSet != initialDiseaseSet

    LaunchedEffect(Unit) {
        viewModel.loadDiseaseData()
    }

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
                    title = if (isSearchMode) "기저질환 검색" else "기저질환 선택",
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

            if (userDiseaseSet == initialDiseaseSet && userDiseaseSet.isNotEmpty() && !isSearchMode) {
                Text(
                    text = "${userDiseaseSet.size}개의 기저질환이 이미 선택되어 있습니다",
                    fontWeight = FontWeight.SemiBold,
                    color = DiaViseoColors.Basic,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            if (isSearchMode) {
                CommonSearchTopBar(
                    placeholder = "어떤 질환이 있으신가요?",
                    navController = navController ?: rememberNavController(),
                    keyword = searchValue.text,
                    onKeywordChange = {
                        searchValue = TextFieldValue(it)
                    }
                )
            } else {
                Text(
                    text = "현재 진단받은 기저질환이 있다면 선택해주세요",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 선택된 질환
            if (userDiseaseSet.isNotEmpty()) {
                Text(
                    text = "선택된 기저질환 (${userDiseaseSet.size}개)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    userDiseaseSet.forEach { id ->
                        diseaseList.find { it.diseaseId == id }?.let { disease ->
                            SelectableTag(
                                text = disease.diseaseName,
                                isSelected = true,
                                onClick = { viewModel.toggleDisease(id) }
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            val filteredList = if (isSearchMode) {
                diseaseList.filter {
                    it.diseaseName.contains(searchValue.text, ignoreCase = true)
                }
            } else diseaseList

            Text(
                text = if (isSearchMode) {
                    if (filteredList.isEmpty()) "검색 결과가 없습니다" else "검색 결과"
                } else "기저질환 목록",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredList.forEach { disease ->
                    SelectableTag(
                        text = disease.diseaseName,
                        isSelected = viewModel.isSelected(disease.diseaseId),
                        onClick = { viewModel.toggleDisease(disease.diseaseId) }
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
                    Text("찾는 기저질환이 목록에 없습니다")
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
                        text = "🔍 찾는 기저질환이 없나요?",
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
                text = "※ 입력한 기저질환 정보는 맞춤형 건강 정보 제공에 활용됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = DiaViseoColors.Unimportant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MainButton(
                text = if (hasChanges)
                    "${userDiseaseSet.size}개 선택 저장하기"
                else
                    "${userDiseaseSet.size}개 선택 완료",
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
