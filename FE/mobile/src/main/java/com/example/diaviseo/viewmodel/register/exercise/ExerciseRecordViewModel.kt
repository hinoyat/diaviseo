package com.example.diaviseo.viewmodel.register.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.mapper.toExercise
import com.example.diaviseo.model.exercise.Exercise
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.exercise.dto.req.ExercisePutRecordRequest
import com.example.diaviseo.network.exercise.dto.req.ExerciseRecordRequest
import com.example.diaviseo.network.exercise.dto.res.ExerciseDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExerciseRecordViewModel : ViewModel(){
    // 선택된 운동 정보
    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise = _selectedExercise.asStateFlow()

    // 수정, 선택된 운동 id
    private val _exerciseId = MutableStateFlow(0)
    val exerciseId = _exerciseId.asStateFlow()

    // 입력된 운동 시간 (분)
    private val _exerciseTime = MutableStateFlow(30)
    val exerciseTime = _exerciseTime.asStateFlow()

    // 시작 시간 (예: "08:30")
    private val _startTime = MutableStateFlow<String>("00:00")
    val startTime = _startTime.asStateFlow()

    // 등록 날짜 (예 "2025-03-23")
    private val _registerDate = MutableStateFlow(LocalDate.now().toString())
    val registerDate = _registerDate.asStateFlow()

    fun setExercise(exercise: Exercise) {
        _selectedExercise.value = exercise
    }

    fun setExerciseId(id: Int) {
        _exerciseId.value = id
    }

    fun setExerciseTime(time: Int) {
        _exerciseTime.value = time
    }

    fun setStartTime(hour: String, minute: String) {
        if (hour.isNotBlank() && minute.isNotBlank()) {
            _startTime.value = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
        }
    }

    fun setPutStartTime(time: String){
        _startTime.value = time
    }

    fun setRegisterDate(date: String){
        _registerDate.value = date
    }

    private val _recentExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val recentExercises = _recentExercises


    private val _favoriteExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val favoriteExercises = _favoriteExercises


    private fun toExerciseRecordRequest(): ExerciseRecordRequest? {
        val exercise = _selectedExercise.value ?: return null
        val time = _exerciseTime.value
        val startTime = _startTime.value

        val formattedDate = startTime.let {
            _registerDate.value + "T" + it.padStart(5, '0') // ex. 08:03 → 08:03
        }

        return ExerciseRecordRequest(
            exerciseNumber = exercise.id,
            exerciseDate = formattedDate,
            exerciseTime = time
        )
    }
    private val _exerciseDetail = MutableStateFlow<ExerciseDetailResponse?>(null)
    val exerciseDetail = _exerciseDetail.asStateFlow()


    fun fetchExerciseDetail(exerciseNumber: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitInstance.exerciseApiService.getExerciseDetail(exerciseNumber)
            }.onSuccess { response ->
                _exerciseDetail.value = response.data
            }.onFailure { error ->
                Log.e("ExerciseDetail", "상세 조회 실패", error)
            }
        }
    }


    fun toggleFavorite() {
        val current = _exerciseDetail.value ?: return
        viewModelScope.launch {
            runCatching {
                RetrofitInstance.exerciseApiService.toggleFavorite(current.exerciseNumber)
            }.onSuccess { response ->
                val updatedFavorite = response.data?.favorite ?: false
                _exerciseDetail.value = current.copy(isFavorite = updatedFavorite)

                // 즐겨찾기 목록 즉시 갱신
                fetchFavoriteExercises()
            }.onFailure {
                Log.e("FavoriteToggle", "토글 실패", it)
            }
        }
    }

    private fun toPutExercise(totalKcal: Int): ExercisePutRecordRequest? {
        val time = _exerciseTime.value
        val startTime = _startTime.value

        val formattedDate = startTime.let {
            _registerDate.value + "T" + it.padStart(5, '0') // ex. 08:03 → 08:03
        }

        return ExercisePutRecordRequest(
            exerciseCalorie = totalKcal,
            exerciseDate = formattedDate,
            exerciseTime = time
        )
    }

    fun fetchRecentExercises(){
        viewModelScope.launch {
            runCatching {
                RetrofitInstance.exerciseApiService.getRecentExercises()
            }.onSuccess { response ->
                _recentExercises.value = response.data.orEmpty().map{it.toExercise()}
            }.onFailure {
                _recentExercises.value = emptyList()
            }
        }
    }

    fun fetchFavoriteExercises() {
        viewModelScope.launch {
            runCatching {
                RetrofitInstance.exerciseApiService.getFavoriteExercises()
            }.onSuccess { response ->
                _favoriteExercises.value = response.data.orEmpty().map { it.toExercise() }
            }.onFailure {
                _favoriteExercises.value = emptyList()
            }
        }
    }
    fun submitExercise(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val request = toExerciseRecordRequest() ?: return
        Log.d("ExerciseSubmit", "submitExercise 진입")
        Log.d("ExerciseSubmit", "보내는 데이터: $request")
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.exerciseApiService.registerExercise(request)
                Log.d("ExerciseSubmit", "서버 응답: $response")
                if (response.status == "OK") {
                    fetchRecentExercises() // 최신 운동 목록 갱신
                    onSuccess()
                } else {
                    onError(Exception("서버 오류: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("ExerciseSubmit", "예외 발생", e)
                onError(e)
            }
        }
    }

    fun putExercise(totalKcal: Int, exerciseId: Int, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val request = toPutExercise(totalKcal) ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.exerciseApiService.putExercise(exerciseId = exerciseId, request = request)
                Log.d("Exercise put", "서버 응답: $response")
                if (response.status == "OK") {
                    onSuccess()
                } else {
                    onError(Exception("서버 오류: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("Exercise put", "예외 발생", e)
                onError(e)
            }
        }
    }

}