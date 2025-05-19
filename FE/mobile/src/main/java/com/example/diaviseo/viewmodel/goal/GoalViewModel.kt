package com.example.diaviseo.viewmodel.goal

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import org.json.JSONObject
import kotlin.collections.isNullOrEmpty


class GoalViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    private val _nutritionFeedback = MutableStateFlow("")
    val nutritionFeedback: StateFlow<String> = _nutritionFeedback

    private val _isNutriLoading = MutableStateFlow(false)
    val isNutriLoading: StateFlow<Boolean> = _isNutriLoading

    private val _workoutFeedback = MutableStateFlow("")
    val workoutFeedback: StateFlow<String> = _workoutFeedback

    private val _isWorkLoading = MutableStateFlow(false)
    val isWorkLoading: StateFlow<Boolean> = _isWorkLoading

    private val _weightFeedback = MutableStateFlow("")
    val weightFeedback: StateFlow<String> = _weightFeedback

    private val _isWeightLoading = MutableStateFlow(false)
    val isWeightLoading: StateFlow<Boolean> = _isWeightLoading

    fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDate.value = date
            _isLoading.value = false
        }
    }

    fun setShowDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
    }

    fun setWorkoutFeedback() {
        _workoutFeedback.value = ""
    }

    // 피드백 여부
    fun isThereFeedback(
        feedbackType: String,
        date: String
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.chatBotApiService.fetchFeedBack(feedbackType, date)
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()
                    if (feedbackType == "nutrition") {
                        _nutritionFeedback.value = message.toString()
                    } else if (feedbackType == "workout") {
                        _workoutFeedback.value = message.toString()
                    } else if (feedbackType == "weight_trend") {
                        _weightFeedback.value = message.toString()
                    }
                } else {
//                    val errorJson = response.errorBody()?.string()
//                    val detail = JSONObject(errorJson ?: "").optString("detail")

                    if (feedbackType == "nutrition") {
                        _nutritionFeedback.value = ""
                    } else if (feedbackType == "workout") {
                        _workoutFeedback.value = ""
                    } else if (feedbackType == "weight_trend") {
                        _weightFeedback.value = ""
                    }
//                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    fun createNutriFeedBack(date: String) {
        viewModelScope.launch {
            _isNutriLoading.value = true
            try {
                val response = RetrofitInstance.chatBotApiService.createNutriFeedBack(date)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val answer = response.body()?.get("feedback")
                    Log.d("API", "답변: $answer")
                    _nutritionFeedback.value = answer.toString()
                    _isNutriLoading.value = false
                } else {
                    val errorJson = response.errorBody()?.string()
                    val detail = JSONObject(errorJson ?: "").optString("feedback")
                    _nutritionFeedback.value = "오류가 발생했습니다 다시 한번 시도해주세요🥹"
                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }

            _isNutriLoading.value = false
        }
    }

    fun createWeightFeedBack(date: String) {
        viewModelScope.launch {
            _isWeightLoading.value = true
            try {
                val response = RetrofitInstance.chatBotApiService.createHomeFeedBack(date)
                if (response.isSuccessful) {
                    val answer = response.body()?.get("feedback")
                    Log.d("API", "답변: $answer")
                    _weightFeedback.value = answer.toString()
                    _isWeightLoading.value = false
                } else {
                    val errorJson = response.errorBody()?.string()
                    val detail = JSONObject(errorJson ?: "").optString("feedback")
                    _weightFeedback.value = "오류가 발생했습니다 다시 한번 시도해주세요🥹"
                    Log.d("AI feedback", "메세지 : $detail")
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }

            _isWeightLoading.value = false
        }
    }

    fun createHomeFeedBack() {
        viewModelScope.launch {
            _isWorkLoading.value = true
            try {
                val response = RetrofitInstance.chatBotApiService.createWorkFeedBack()
                if (response.isSuccessful) {
                    val body = response.body()
                    val predicted = body?.predicted_weight

                    if (predicted != null) {
                        // 데이터 저장을 해야하고
                        val message = generateWeightPredictionText(
                            projectedChange = predicted.projected_change,
                            daysTracked = predicted.days_tracked,
                            status = predicted.status
                        )
                        _workoutFeedback.value = message
                        _isWorkLoading.value = false
                    } else {
                        _workoutFeedback.value = "오류가 발생했습니다 다시 한번 시도해주세요🥹"
                        val error = body?.error
                        val message = body?.message
                        Log.d("AI feedback", "홈 디테일 쪽 $error : $message")
                    }
                }
            } catch (e: Exception) {
                // 네트워크 끊김, 타임아웃 등
                Log.e("AI feedback", "❌ 네트워크 오류: ${e.localizedMessage}")
            }

            _isWorkLoading.value = false
        }
    }

    @SuppressLint("DefaultLocale")
    fun generateWeightPredictionText(
        projectedChange: Double,
        daysTracked: Int,
        status: String
    ): String {
        val absChange = String.format("%.1f", kotlin.math.abs(projectedChange))
        val suggestionTarget = if (projectedChange < 0) "감량" else "증량"
        val isTooRapid = kotlin.math.abs(projectedChange) >= 4

        val intro = "이런 추이면 ${daysTracked}일 이내에 ${absChange}kg ${status}돼요."

        val advice = if (isTooRapid) {
            "하지만 ${daysTracked}일 이내 급격한 체중 변화는 좋지 않아요.\n" +
                    "조금 더 ${if (projectedChange < 0) "드시고" else "운동하고"} 건강하게 ${daysTracked}일 이내 3kg ${suggestionTarget}을 목표로 해보아요."
        } else {
            "아주 좋은 흐름이에요! 지금처럼만 하면 충분히 ${suggestionTarget}에 성공할 수 있어요 💪\n" +
                    "건강한 루틴을 꾸준히 이어가 볼까요?"
        }

        return "$intro\n$advice"
    }

}