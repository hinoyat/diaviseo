package com.example.diaviseo.ui.mypageedit.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.Exercise
import com.example.diaviseo.ui.components.ExerciseCategory
import com.example.diaviseo.ui.components.SelectableCategory
import com.example.diaviseo.ui.components.SelectableExerciseItem
import com.example.diaviseo.ui.components.onboarding.MainButton

@Composable
fun ExerciseSearchBottomSheetContent(
    allExercises: List<Exercise>,
    categories: List<ExerciseCategory>,
    selectedExercises: List<Exercise>,
    onSelectExercise: (Exercise) -> Unit,
    onRemoveExercise: (Exercise) -> Unit,
    onDone: () -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // ✅ 다이얼로그용 상태
    var pendingExercise by remember { mutableStateOf<Exercise?>(null) }
    var pendingAction by remember { mutableStateOf<String?>(null) } // "add" or "remove"

    val filteredExercises = allExercises
        .filter {
            (selectedCategoryId == null || it.categoryId == selectedCategoryId) &&
                    it.name.contains(searchQuery, ignoreCase = true)
        }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.92f)
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("운동명을 검색하세요") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        SelectableCategory(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onSelectCategory = { selectedCategoryId = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredExercises) { exercise ->
                val isSelected = exercise in selectedExercises
                SelectableExerciseItem(
                    exercise = exercise,
                    isSelected = isSelected,
                    onClick = {
                        pendingExercise = exercise
                        pendingAction = if (isSelected) "remove" else "add"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        MainButton(
            text = "${selectedExercises.size}개 선택 완료",
            onClick = onDone,
            enabled = selectedExercises.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    // ✅ 다이얼로그
    if (pendingExercise != null && pendingAction != null) {
        AlertDialog(
            onDismissRequest = {
                pendingExercise = null
                pendingAction = null
            },
            title = { Text(text = if (pendingAction == "add") "운동 추가" else "운동 삭제") },
            text = {
                Text(
                    text = if (pendingAction == "add")
                        "${pendingExercise!!.name} 운동을 추가하시겠습니까?"
                    else
                        "${pendingExercise!!.name} 운동을 삭제하시겠습니까?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // ✅ TODO 자리: 여기서 API 연동 가능
                        if (pendingAction == "add") {
                            // TODO: 운동 추가 API 호출
                            onSelectExercise(pendingExercise!!)
                        } else {
                            // TODO: 운동 삭제 API 호출
                            onRemoveExercise(pendingExercise!!)
                        }

                        pendingExercise = null
                        pendingAction = null
                    }
                ) {
                    Text("예")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingExercise = null
                        pendingAction = null
                    }
                ) {
                    Text("아니오")
                }
            }
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseSearchBottomSheetContentPreview() {
    val dummyExercises = listOf(
        Exercise(1, "걷기", R.drawable.charac_main_nontext, categoryId = 2),
        Exercise(2, "달리기", R.drawable.charac_main_nontext, categoryId = 2),
        Exercise(3, "자전거타기", R.drawable.charac_main_nontext, categoryId = 5),
        Exercise(4, "수영", R.drawable.charac_main_nontext, categoryId = 6),
        Exercise(5, "복싱", R.drawable.charac_main_nontext, categoryId = 3)
    )

    val dummyCategories = listOf(
        ExerciseCategory(2, "유산소"),
        ExerciseCategory(3, "프리 웨이트"),
        ExerciseCategory(5, "아웃도어"),
        ExerciseCategory(6, "수상스포츠")
    )

    var selected by remember { mutableStateOf<List<Exercise>>(listOf(dummyExercises[0])) }

    ExerciseSearchBottomSheetContent(
        allExercises = dummyExercises,
        categories = dummyCategories,
        selectedExercises = selected,
        onSelectExercise = { selected = selected + it },
        onRemoveExercise = { selected = selected - it },
        onDone = {}
    )
}
