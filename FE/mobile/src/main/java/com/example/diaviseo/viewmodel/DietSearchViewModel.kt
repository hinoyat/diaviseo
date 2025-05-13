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
    var selectedMeal by mutableStateOf("점심")
        private set

    // 날짜 선택 처리
    fun onDateSelected(date: LocalDate) {
        selectedDate = date
    }

    // 식사 시간 선택 처리
    fun onTimeSelected(time: LocalTime) {
        selectedTime = time
    }

    // 끼니 선택 처리
    fun onMealSelected(meal: String) {
        selectedMeal = meal
    }

    // 서버 전송용 요청 객체로 변환
    fun toPostDietRequest(): PostDietRequest? {
        val time = selectedTime ?: return null

        return PostDietRequest(
            mealDate = selectedDate.toString(),
            isMeal = true,
            mealTimes = listOf(
                MealTimeRequest(
                    mealType = MealType.fromKorean(selectedMeal),
                    eatingTime = time.toString(),
                    foods = selectedItems.map { it.toRequest() } // 변환 함수 사용
                )
            )
        )
    }

    // 음식 추가 (기존 항목이 있으면 수량 갱신)
    fun addSelectedFood(food: FoodItem, quantity: Int) {
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
            addSelectedFood(food, quantity = 1)
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
                quantity = it.quantity.toInt()
            )
        }
    }
    // 최근 먹은 음식들
    var recentFoods by mutableStateOf<List<RecentFoodItemResponse>>(emptyList())
        private set

    var recentFetchedDate by mutableStateOf<String?>(null)
        private set

    // 최근 먹은 목록 불러오는 함수
    fun fetchRecnetFoods(){
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
    fun addRecentFood(item: RecentFoodItemResponse, quantity: Int = 1) {
        val updatedList = selectedItems.toMutableList()
        val exists = updatedList.any { it.foodId.toLong() == item.foodId }

        if (!exists) {
            updatedList.add(
                FoodWithQuantity(
                    foodId = item.foodId.toInt(),
                    foodName = item.foodName,
                    calorie = item.calorie,
                    carbohydrate = 0.0,
                    protein = 0.0,
                    fat = 0.0,
                    sweet = 0.0,
                    quantity = quantity
                )
            )
            selectedItems = updatedList
        } else {
            removeSelectedFood(item.foodId.toInt())
        }
    }

    var favoriteFoods by mutableStateOf<List<FoodItem>>(emptyList())
        private set

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
    var isFavoriteDirty by mutableStateOf(false)
        private set

    fun markFavoriteDirty() {
        isFavoriteDirty = true
    }
    fun clearFavoriteDirtyFlag() {
        isFavoriteDirty = false
    }

    fun clearDietState() {
        keyword = ""
        isSearching = false
        searchResults = emptyList()
        selectedItems = emptyList()
        selectedDate = LocalDate.now()
        selectedTime = null
        selectedMeal = "점심"
        recentFoods = emptyList()
        recentFetchedDate = null
    }

    // 식단 등록 API 호출
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
