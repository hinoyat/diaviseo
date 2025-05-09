package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.FoodItem
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import android.util.Log

class DietSearchViewModel : ViewModel() {

    // 🔍 사용자가 입력한 검색어
    var keyword by mutableStateOf("")
        private set

    // 🔍 검색 결과 목록
    var searchResults by mutableStateOf<List<FoodItem>>(emptyList())
        private set

    // 🔄 검색어가 바뀔 때 호출되는 함수
    fun onKeywordChange(newKeyword: String) {
        keyword = newKeyword
        searchFoods()  // 입력이 바뀌면 검색 실행
    }

    // 🌐 Retrofit을 이용한 검색 API 호출
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
                searchResults = response.data
            } catch (e: Exception) {
                Log.e("SearchVM", "검색 실패: ${e.message}")
                e.printStackTrace()  // 전체 스택 찍기

                // 로그 찍고 실패 시에도 빈 리스트로
                e.printStackTrace()
                searchResults = emptyList()
            }
        }
    }
}
