package com.example.diaviseo.ui.register.diet

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.register.components.PhotoUploadHintBox
import com.example.diaviseo.ui.register.components.SectionDivider
import com.example.diaviseo.ui.register.diet.components.*
import com.example.diaviseo.ui.theme.regular14
import com.example.diaviseo.viewmodel.DietSearchViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.diaviseo.utils.isGalleryPermissionGranted
import com.example.diaviseo.utils.rememberGalleryPermissionLauncher
import com.example.diaviseo.utils.rememberGalleryPickerLauncher
import com.example.diaviseo.utils.shouldShowGalleryRationale
import android.app.Activity
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.food.dto.res.FoodDetailResponse
import com.example.diaviseo.utils.getGalleryPermission
import com.example.diaviseo.utils.openAppSettings
import kotlinx.coroutines.launch

@Composable
fun DietConfirmScreen(
    navController: NavController,
    viewModel: DietSearchViewModel
) {
    val context = LocalContext.current
    val activity = context as Activity
    val permission = Manifest.permission.READ_MEDIA_IMAGES

    val selectedFoodDetail = remember { mutableStateOf<FoodDetailResponse?>(null) }
    val showFoodDetailSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var showSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedFoods = viewModel.selectedItems
    val selectedMeal by viewModel.selectedMeal.collectAsState()
    val selectedTime = viewModel.selectedTime

    val totalCalorie = selectedFoods.sumOf { it.calorie }
    val scrollState = rememberScrollState()

    val totalCarb = selectedFoods.sumOf { it.carbohydrate * it.quantity }
    val totalProtein = selectedFoods.sumOf { it.protein * it.quantity }
    val totalFat = selectedFoods.sumOf { it.fat * it.quantity }
    val totalSugar = selectedFoods.sumOf { it.sweet * it.quantity }


    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberGalleryPickerLauncher {
        selectedImageUri.value = it
    }
    val permissionLauncher = rememberGalleryPermissionLauncher {
        galleryLauncher.launch("image/*")
    }

    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.KOREAN)
    val time = LocalTime.parse("06:30 오전", formatter)
    val selectedQuantity = remember { mutableStateOf(1.0f) }


    Scaffold(
        topBar = {
            CommonTopBar(
                title = "식단 등록",
                onLeftActionClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // 이미지 선택
            selectedImageUri.value?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            // 이미지 다시 선택
                            galleryLauncher.launch("image/*")
                        }
                ) {
                    // 배경 이미지
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "선택된 이미지",
                        modifier = Modifier.fillMaxSize()
                    )

                    // 우측 상단 수정 아이콘 오버레이
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "사진 다시 선택",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
                ?: PhotoUploadHintBox(
                isAiMode = false,
                onClick = {
                    val permission = getGalleryPermission()
                    when {
                        isGalleryPermissionGranted(context) -> {
                            galleryLauncher.launch("image/*")
                        }

                        shouldShowGalleryRationale(activity) -> {
                            Toast.makeText(context, "사진 등록을 위해 권한이 필요해요.", Toast.LENGTH_SHORT).show()
                            permissionLauncher.launch(permission)
                        }

                        else -> {
                            // 다시 묻지 않기 등으로 차단된 상태
                            Toast.makeText(context, "설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                            openAppSettings(context)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
                FoodInfoSection(totalCalorie = "$totalCalorie")
            }

            Spacer(modifier = Modifier.height(10.dp))

            NutrientBar(
                nutrientInfo = NutrientInfo(
                    carbohydrate = totalCarb.toFloat(),
                    protein = totalProtein.toFloat(),
                    fat = totalFat.toFloat(),
                    sugar = totalSugar.toFloat()
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)
            )

            Spacer(modifier = Modifier.height(25.dp))
            SectionDivider(height = 14.dp)
            Spacer(modifier = Modifier.height(25.dp))

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
                    text = if (selectedTime != null) "선택됨: $selectedTime" else "식사시간 추가하기",
                    style = regular14,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        showTimePicker = true
                    }
                )
            }

            // 시간 바텀시트
            if (showTimePicker) {
                MealTimePickerBottomSheet(
                    onTimeSelected = { timeStr ->
                        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.KOREAN)
                        val parsed = LocalTime.parse(timeStr, formatter)
                        viewModel.onTimeSelected(parsed)
                        showTimePicker = false
                    },
                    onDismiss = { showTimePicker = false }
                )

            }

            // 끼니 바텀시트
            if (showSheet) {
                MealSelectBottomSheet(
                    selected = selectedMeal,
                    onSelect = { viewModel.onMealSelected(it) },
                    onConfirm = { showSheet = false },
                    onDismiss = { showSheet = false }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 선택된 음식 리스트
            SelectedFoodList(
                selectedItems = viewModel.selectedItems,
                onRemoveItem = { foodId -> viewModel.removeSelectedFood(foodId) },
                onItemClick = { foodItem ->
                    coroutineScope.launch {
                        try {
                            val response = RetrofitInstance.foodApiService.getFoodDetail(foodItem.foodId)
                            val detail = response.data
                            if (detail != null) {
                                // quantity는 기존 선택값 유지
                                selectedFoodDetail.value = detail
                                selectedQuantity.value = foodItem.quantity
                                showFoodDetailSheet.value = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            )
            if (showFoodDetailSheet.value && selectedFoodDetail.value != null) {
                val foodId = selectedFoodDetail.value!!.foodId
                val updatedQuantity = viewModel.selectedItems.find { it.foodId == foodId }?.quantity ?: 1f
                FoodDetailBottomSheet(
                    food = selectedFoodDetail.value!!,
                    initialQuantity = selectedQuantity.value,
                    onToggleFavorite = {
                        val foodId = selectedFoodDetail.value!!.foodId
                        viewModel.toggleFavorite(foodId) {
                            coroutineScope.launch {
                                val updated = RetrofitInstance.foodApiService.getFoodDetail(foodId).data
                                if (updated != null) {
                                    selectedFoodDetail.value = updated
                                    selectedQuantity.value = updatedQuantity
                                }
                            }
                        }
                    },
                    onAddClick = { newQuantity ->
                        viewModel.updateSelectedFoodQuantity(
                            foodId = selectedFoodDetail.value!!.foodId,
                            quantity = newQuantity
                        )
                        showFoodDetailSheet.value = false
                    },
                    onDismiss = { showFoodDetailSheet.value = false }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            NutrientInfoNotice(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(27.dp))

            // 등록 버튼
            MainButton(
                text = "등록하기",
                onClick = {
                    viewModel.submitDiet(
                        context = context,
                        imageUri = selectedImageUri.value,
                        onSuccess = {
                            Toast.makeText(context, "식단 등록이 완료되었어요!", Toast.LENGTH_SHORT).show()

                            viewModel.clearDietState()

                            navController.navigate("diet_register") {
                                popUpTo("diet_confirm") { inclusive = true }
                            }
                        },
                        onError = { message ->
                            Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            )
        }
    }
}
