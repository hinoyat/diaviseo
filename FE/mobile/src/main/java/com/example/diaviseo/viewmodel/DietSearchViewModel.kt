package com.example.diaviseo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.food.dto.res.FoodItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import android.util.Log
import androidx.core.net.toFile
import com.example.diaviseo.mapper.toFoodItem
import com.example.diaviseo.network.food.dto.req.MealTimeRequest
import com.example.diaviseo.network.food.dto.req.MealType
import com.example.diaviseo.network.meal.dto.req.PostDietRequest
import com.example.diaviseo.model.diet.FoodWithQuantity           // ✅ UI용 데이터 모델
import com.example.diaviseo.model.diet.toRequest                  // ✅ 변환 확장 함수
import com.example.diaviseo.network.food.dto.res.RecentFoodItemResponse
import com.example.diaviseo.network.foodset.dto.req.FoodIdWithQuantity
import com.example.diaviseo.network.foodset.dto.req.FoodSetRegisterRequest
import com.example.diaviseo.network.foodset.dto.res.FoodSetResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class DietSearchViewModel : ViewModel() {
    // 음식 검색어
    var keyword by mutableStateOf("")
        private set

    // 음식 검색 결과 목록
    var searchResults by mutableStateOf<List<FoodItem>>(emptyList())
        private set

    // 검색 중 여부
    var isSearching by mutableStateOf(false)
        private set

    // 선택된 음식 목록 (수량 포함)
    var selectedItems by mutableStateOf<List<FoodWithQuantity>>(emptyList())
        private set

    // 선택된 날짜 (기본값: 오늘)
    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    // 선택된 식사 시간
    var selectedTime by mutableStateOf<LocalTime?>(null)
        private set

    // 선택된 끼니 ("아침", "점심", "저녁")
    private val _selectedMeal = MutableStateFlow("점심")
    val selectedMeal: StateFlow<String> = _selectedMeal

    // 날짜 선택 처리
    fun onDateSelected(date: LocalDate) {
        selectedDate = date
    }

    // 식사 시간 선택 처리
    fun onTimeSelected(time: LocalTime?) {
        selectedTime = time
    }

    // 끼니 선택 처리
    fun onMealSelected(meal: String) {
        _selectedMeal.value = meal
    }

    // 끼니 스킵처리
    fun skipSelectedItems() {
        selectedItems = emptyList()
    }

    // 서버 전송용 요청 객체로 변환
    fun toPostDietRequest(): PostDietRequest? {
        val time = selectedTime ?: return null

        return PostDietRequest(
            mealDate = selectedDate.toString(),
            isMeal = true,
            mealTimes = listOf(
                MealTimeRequest(
                    mealType = MealType.fromKorean(_selectedMeal.value),
                    eatingTime = time.toString(),
                    foods = selectedItems.map { it.toRequest() } // 변환 함수 사용
                )
            )
        )
    }

    // 음식 추가 (기존 항목이 있으면 수량 갱신)
    fun addSelectedFood(food: FoodItem, quantity: Float) {
        val updatedList = selectedItems.toMutableList()
        val index = updatedList.indexOfFirst { it.foodId == food.foodId }

        if (index >= 0) {
            updatedList[index] = updatedList[index].copy(quantity = quantity)
        } else {
            updatedList.add(
                FoodWithQuantity(
                    foodId = food.foodId,
                    foodName = food.foodName,
                    calorie = food.calorie,
                    carbohydrate = food.carbohydrate,
                    protein = food.protein,
                    fat = food.fat,
                    sweet = food.sweet,
                    quantity = quantity
                )
            )
        }

        selectedItems = updatedList
    }

    // 담은 음식 삭제
    fun removeSelectedFood(foodId: Int) {
        selectedItems = selectedItems.filterNot { it.foodId == foodId }
    }

    // 음식 선택 여부 토글
    fun onToggleSelect(food: FoodItem) {
        val exists = selectedItems.any { it.foodId == food.foodId }
        if (exists) {
            removeSelectedFood(food.foodId)
        } else {
            addSelectedFood(food, quantity = 1f)
        }
    }
    // 검색어 변경 및 검색 실행
    fun onKeywordChange(newKeyword: String) {
        keyword = newKeyword
        isSearching = newKeyword.isNotBlank()
        searchFoods()
    }

    // 검색 취소
    fun cancelSearch() {
        keyword = ""
        isSearching = false
    }
    // 파일 변환 함수
    fun Uri.toFile(context: Context): File {
        val inputStream = context.contentResolver.openInputStream(this)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
    // 음식 세트 목록 상태
    var foodSets by mutableStateOf<List<FoodSetResponse>>(emptyList())
        private set

    // 음식 세트 목록 조회 함수
    fun fetchFoodSets() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.foodSetApiService.getFoodSets()
                foodSets = response.data ?: emptyList()
            } catch (e: Exception) {
                Log.e("DietSearchVM", "음식 세트 조회 실패: ${e.message}")
            }
        }
    }

    // 음식 세트 선택 시 현재 선택된 음식 목록으로 반영하는 함수
    fun applyFoodSet(set: FoodSetResponse) {
        selectedItems = set.foods.map {
            FoodWithQuantity(
                foodId = it.foodId,
                foodName = it.foodName,
                calorie = it.calorie,
                carbohydrate = it.carbohydrate,
                protein = it.protein,
                fat = it.fat,
                sweet = it.sweet,
                quantity = it.quantity.toFloat()
            )
        }
    }
    // 최근 먹은 음식들
    var recentFoods by mutableStateOf<List<RecentFoodItemResponse>>(emptyList())
        private set

    var recentFetchedDate by mutableStateOf<String?>(null)
        private set

    // 최근 먹은 목록 불러오는 함수
    fun fetchRecentFoods(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.foodApiService.getRecentFoods()
                recentFoods = response.data ?: emptyList()
                recentFetchedDate = response.timestamp?.substring(0, 10) ?: ""
            } catch (e: Exception) {
                Log.e("DietSearchViewModel", "최근 음식 불러오기 실패", e)
            }
        }
    }

    // 최근 음식 선택 → 선택 항목에 추가하거나 제거
    fun addRecentFood(item: RecentFoodItemResponse, quantity: Float = 1f) {
        val updatedList = selectedItems.toMutableList()
        val exists = updatedList.any { it.foodId.toLong() == item.foodId }

        if (!exists) {
            updatedList.add(
                FoodWithQuantity(
                    foodId = item.foodId.toInt(),
                    foodName = item.foodName,
                    calorie = item.calorie,
                    carbohydrate = item.carbohydrate,
                    protein = item.protein,
                    fat = item.fat,
                    sweet = item.sweet,
                    quantity = quantity
                )
            )
            selectedItems = updatedList
        } else {
            removeSelectedFood(item.foodId.toInt())
        }
    }

    // 즐겨찾기 음식 목록 상태
    var favoriteFoods by mutableStateOf<List<FoodItem>>(emptyList())
        private set

    // 즐겨찾기 음식 조회
    fun fetchFavoriteFoods() {
        viewModelScope.launch {
            Log.d("DietVM", "⭐ 즐겨찾기 API 호출 시작됨") // 이거 추가

            try {
                val response = RetrofitInstance.foodApiService.getFavoriteFoods()
                favoriteFoods = response.data?.map { it.toFoodItem() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("DietSearchVM", "즐겨찾기 조회 실패: ${e.message}")
            }
        }
    }

    // 즐겨찾기 상태 변경 플래그 (데이터 새로고침 필요 여부)
    var isFavoriteDirty by mutableStateOf(false)
        private set

    // 즐겨찾기 상태 dirty 표시
    fun markFavoriteDirty() {
        isFavoriteDirty = true
    }

    // 즐겨찾기 상태 초기화
    fun clearFavoriteDirtyFlag() {
        isFavoriteDirty = false
    }

    // 식단 상태 초기화 (탭 전환 등 시 호출)
    fun clearDietState() {
        keyword = ""
        isSearching = false
        searchResults = emptyList()
        selectedItems = emptyList()
        selectedDate = LocalDate.now()
        selectedTime = null
        _selectedMeal.value = "점심"
        recentFoods = emptyList()
        recentFetchedDate = null
    }

    // 선택된 음식의 수량만 업데이트
    fun updateSelectedFoodQuantity(foodId: Int, quantity: Float) {
        selectedItems = selectedItems.map { item ->
            if (item.foodId == foodId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
    }

    // 식단 등록 API 호출 (Multipart 요청: mealData + image)
    fun submitDiet(
        context: Context,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val request = toPostDietRequest() ?: run {
            onError("식사시간이 선택되지 않았습니다.")
            return
        }
        val gson = com.google.gson.Gson()
        val json = gson.toJson(request)
        val mealDataBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        // 이미지가 있을 경우 Multipart 파트로 생성
        val imagePart = imageUri?.let {
            val file = it.toFile(context) // 아래 확장 함수 필요
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", file.name, requestFile)
        }
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.mealApiService.postDiet(
                    mealData = mealDataBody,
                    images = imagePart
                )
                if (response.status == "OK" || response.status == "CREATED") {
                    onSuccess()
                } else {
                    onError("등록 실패: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError("네트워크 오류: ${e.message}")
            }
        }
    }
    // 음식 검색 API 호출
    private fun searchFoods() {
        if (keyword.isBlank()) {
            Log.d("SearchVM", "빈 검색어로 인해 검색 안 함")
            searchResults = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                Log.d("SearchVM", "API 요청: $keyword")
                val response = RetrofitInstance.dietApiService.searchFoodByName(keyword)
                searchResults = response.data ?: emptyList()
            } catch (e: Exception) {
                Log.e("SearchVM", "검색 실패: ${e.message}")
                e.printStackTrace()
                searchResults = emptyList()
            }
        }
    }

    // 음식 세트 등록
    fun registerFoodSet(
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val foods = selectedItems.map {
            FoodIdWithQuantity(
                foodId = it.foodId,
                quantity = it.quantity.toDouble()
            )
        }

        val request = FoodSetRegisterRequest(name = name, foods = foods)

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.foodSetApiService.postFoodSet(request)
                if (response.status == "CREATED") {
                    onSuccess()
                } else {
                    onError("등록 실패: ${response.message}")
                }
            } catch (e: Exception) {
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 즐겨찾기 토글 변경 api 호출 함수
    fun toggleFavorite(foodId: Int, onResult:(Boolean)-> Unit){
        viewModelScope.launch {
            try{
                val response = RetrofitInstance.foodApiService.toggleFavoriteFood(foodId)
                val result = response.data?.isFavorite ?:false
                if (response.status == "OK") {
                    // ✅ 즐겨찾기 목록 다시 불러와야 함
                    markFavoriteDirty()
                }
                onResult(result)
            } catch (e:Exception){
                Log.e("DietVM","즐겨찾기 토글 실패: ${e.message}")
                onResult(false)
            }
        }
    }
}
