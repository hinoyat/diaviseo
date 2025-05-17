package com.example.diaviseo.ui.register.diet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.register.components.*
import com.example.diaviseo.ui.register.diet.components.*
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.ui.components.onboarding.MainButton
import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.diaviseo.utils.*

@Composable
fun DietAiRegisterScreen(navController: NavController) {
    var selectedMeal by remember { mutableStateOf("끼니 선택") }
    var showSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val mockFoodList = remember {
        mutableStateListOf(
            FoodItemUiModel("연어덮밥", "1인분 (350g)", "690kcal"),
            FoodItemUiModel("닭가슴살 샐러드", "1인분 (280g)", "690kcal"),
            FoodItemUiModel("곤약볶음밥", "1인분 (220g)", "580kcal"),
            FoodItemUiModel("김치볶음밥", "1인분 (350g)", "650kcal")
        )
    }

    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val activity = context as Activity
    val permission = getGalleryPermission()

    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberGalleryPickerLauncher {
        selectedImageUri.value = it
    }

    val permissionLauncher = rememberGalleryPermissionLauncher {
        galleryLauncher.launch("image/*")
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "AI 식단 등록",
                onLeftActionClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold 안쪽 padding 처리
                .verticalScroll(scrollState)
                .navigationBarsPadding() // 하단 소프트바 대응
        ) {
            // 이미지 업로드
            selectedImageUri.value?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "선택된 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 22.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } ?: PhotoUploadHintBox(
                onClick = {
                    when {
                        isGalleryPermissionGranted(context) -> {
                            galleryLauncher.launch("image/*")
                        }
                        shouldShowGalleryRationale(activity) -> {
                            Toast.makeText(context, "사진 등록을 위해 권한이 필요해요.", Toast.LENGTH_SHORT).show()
                            permissionLauncher.launch(permission)
                        }
                        else -> {
                            Toast.makeText(context, "설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                            openAppSettings(context)
                        }
                    }
                },
                isAiMode = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 음식 정보
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                FoodInfoSection(
                    foodName = "쌀과자",
                    totalCalorie = "300"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 영양소 바
            NutrientBar(
                nutrientInfo = NutrientInfo(
                    carbohydrate = 30f,
                    protein = 20f,
                    fat = 10f,
                    sugar = 5f
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            )

            Spacer(modifier = Modifier.height(25.dp))

            SectionDivider(height = 14.dp)

            Spacer(modifier = Modifier.height(25.dp))

            // 끼니 선택 + 식사시간
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MealSelector(
                    selectedMeal = selectedMeal,
                    onClick = { showSheet = true },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "식사시간 추가하기",
                    style = regular14,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        showTimePicker = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 음식 리스트
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                mockFoodList.forEachIndexed { index, item ->
                    SelectableFoodItem(
                        item = item,
                        onToggle = {
                            mockFoodList[index] = item.copy(isAdded = !item.isAdded)
                        },
                        showDivider = index < mockFoodList.lastIndex
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            NutrientInfoNotice(
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(27.dp))
            // ✅ 공통 메인 버튼 사용
            MainButton(
                text = "등록하기",
                onClick = { }, // TODO : 등록 로직 
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            )
        }
    }

    // 바텀시트
    if (showSheet) {
        MealSelectBottomSheet(
            selected = selectedMeal,
            onSelect = { selectedMeal = it },
            onConfirm = { showSheet = false },
            onDismiss = { showSheet = false }
        )
    }

    // 시간 선택 바텀시트
    if (showTimePicker) {
        MealTimePickerBottomSheet(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            }
        )
    }
}
