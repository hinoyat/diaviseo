package com.example.diaviseo.ui.register.exercise

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.ui.register.components.CommonSearchTopBar
import com.example.diaviseo.ui.register.components.CommonTabBar
import com.example.diaviseo.ui.register.components.SelectableCategoryRow
import com.example.diaviseo.ui.register.exercise.components.ExerciseRegisterBottomSheet
import com.example.diaviseo.ui.register.exercise.components.ExerciseSearchResultList
import com.example.diaviseo.viewmodel.ExerciseSearchViewModel
import com.example.diaviseo.viewmodel.register.exercise.ExerciseRecordViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseRegisterMainScreen(
    navController: NavController
) {
    val searchViewModel: ExerciseSearchViewModel = viewModel()
    val recordViewModel: ExerciseRecordViewModel = viewModel()

    val keyword by searchViewModel.keyword.collectAsState()
    val searchResults by searchViewModel.filteredExercises.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("나의 최근 운동", "즐겨찾기")
    var isSearchFocused by remember { mutableStateOf(false) }

    // 카테고리 상태
    val categories = listOf("전체", "일반", "유산소", "프리 웨이트", "아웃도어", "수상스포츠", "겨울 스포츠", "구기 종목")
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val selectedCategory = categories[selectedCategoryIndex].takeIf { it != "전체" }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // 바텀시트 상태
    val selectedExercise = remember { mutableStateOf<Exercise?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
    ) {
        CommonSearchTopBar(
            placeholder = "운동명으로 검색",
            navController = navController,
            keyword = keyword,
            onKeywordChange = { searchViewModel.onKeywordChanged(it) },
            onFocusChanged = { isSearchFocused = it },
            onCancelClick = {
                if (isSearchFocused) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                } else {
                    navController.popBackStack()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isSearchFocused) {
            if (keyword.isBlank()) {
                Column {
                    SelectableCategoryRow(
                        categories = categories,
                        selectedIndex = selectedCategoryIndex,
                        onCategorySelected = { selectedCategoryIndex = it }
                    )

                    ExerciseSearchResultList(
                        exercise = searchViewModel.getExercisesByCategory(selectedCategory),
                        onRegisterClick = { exercise ->
                            recordViewModel.setExercise(exercise)
                            selectedExercise.value = exercise
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        }
                    )
                }
            } else {
                ExerciseSearchResultList(
                    exercise = searchResults,
                    onRegisterClick = { exercise ->
                        recordViewModel.setExercise(exercise)
                        selectedExercise.value = exercise
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                )
            }
        } else {
            CommonTabBar(
                tabTitles = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> ExerciseRecommendationContent()
                1 -> RecentExerciseContent()
            }
        }
    }

    // ✅ 운동 등록 바텀시트
    selectedExercise.value?.let {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                selectedExercise.value = null
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            ExerciseRegisterBottomSheet(
                viewModel = recordViewModel,
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedExercise.value = null
                },
                onSuccess = {
                    Toast.makeText(context, "운동이 성공적으로 등록되었어요!", Toast.LENGTH_SHORT).show()
                    coroutineScope.launch { sheetState.hide() }
                    Log.d("ExerciseSubmit", "운동 등록 성공")
                }
            )
        }
    }
}

@Composable
fun ExerciseRecommendationContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "나의 최근 운동 탭 콘텐츠")
    }
}

@Composable
fun RecentExerciseContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "즐겨찾기 탭 콘텐츠")
    }
}
