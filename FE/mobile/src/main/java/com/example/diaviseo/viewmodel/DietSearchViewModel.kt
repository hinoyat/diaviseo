package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.food.dto.res.FoodItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import android.util.Log
import com.example.diaviseo.network.food.dto.req.MealTimeRequest
import com.example.diaviseo.network.food.dto.req.MealType
import com.example.diaviseo.network.meal.dto.req.PostDietRequest
import com.example.diaviseo.model.diet.FoodWithQuantity           // ✅ UI용 데이터 모델
import com.example.diaviseo.model.diet.toRequest                  // ✅ 변환 확장 함수
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

    // 식단 등록 API 호출
    fun submitDiet(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = toPostDietRequest() ?: run {
            onError("식사시간이 선택되지 않았습니다.")
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.mealApiService.postDiet(request)
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
}
