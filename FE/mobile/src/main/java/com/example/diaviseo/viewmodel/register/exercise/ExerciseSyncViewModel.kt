package com.example.diaviseo.viewmodel.register.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.network.RetrofitInstance
import com.example.diaviseo.network.exercise.dto.req.HealthSyncExerciseRequest
import com.example.diaviseo.network.exercise.dto.req.StepRecordRequest
import kotlinx.coroutines.launch

class ExerciseSyncViewModel : ViewModel() {

    /**
     * 운동 데이터 리스트를 서버에 동기화 요청
     *
     * @param requests ExerciseRecordSyncRequest 리스트
     * @param onSuccess 성공 콜백
     * @param onError 실패 콜백 (예외 객체 전달)
     */
    fun syncExerciseRecords(
        requests: List<HealthSyncExerciseRequest>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (requests.isEmpty()) {
            Log.d("ExerciseSync", "보낼 운동 데이터가 없습니다.")
            return
        }

        Log.d("ExerciseSync", "동기화 요청 데이터: $requests")

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.exerciseApiService.uploadHealthExercises(requests)
                Log.d("ExerciseSync", "서버 응답: $response")
                if (response.status == "OK") {
                    onSuccess()
                } else {
                    onError(Exception("서버 오류: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("ExerciseSync", "예외 발생", e)
                onError(e)
            }
        }
    }

    /**
     * 걸음 수 데이터 리스트를 서버에 동기화 요청
     */
    fun syncStepRecords(
        requests: List<StepRecordRequest>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (requests.isEmpty()) {
            Log.d("StepSync", "보낼 걸음 수 데이터가 없습니다.")
            return
        }

        Log.d("StepSync", "동기화 요청 데이터: $requests")

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.exerciseApiService.uploadStepRecords(requests)
                Log.d("StepSync", "서버 응답: $response")
                if (response.status == "OK") {
                    onSuccess()
                } else {
                    onError(Exception("서버 오류: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("StepSync", "예외 발생", e)
                onError(e)
            }
        }
    }
}
