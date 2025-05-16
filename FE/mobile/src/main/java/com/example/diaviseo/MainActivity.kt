package com.example.diaviseo

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.diaviseo.ui.splash.SplashScreen
import com.example.diaviseo.ui.signup.signupNavGraph
import com.example.diaviseo.ui.main.MainScreen
import androidx.compose.runtime.SideEffect


import com.example.diaviseo.ui.components.TransparentStatusBar
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color

import android.util.Log
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import com.example.diaviseo.ui.theme.DiaViseoTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

import android.os.Bundle
import com.example.diaviseo.viewmodel.StepViewModel

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts // 최신 방식 권한 요청
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import androidx.activity.compose.setContent
import androidx.work.*
import com.example.diaviseo.ui.theme.DiaViseoTheme
import com.example.diaviseo.utils.FcmTokenSender
import com.example.diaviseo.worker.StepResetWorker
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope   // 테스트용 지우지 말기
import kotlinx.coroutines.Dispatchers   // 테스트용 지우지 말기
import kotlinx.coroutines.flow.firstOrNull
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    val testViewModel = TestViewModel()
    private lateinit var stepViewModel: StepViewModel // ViewModel 인스턴스 가져오기

    // 최신 권한 요청 방식 사용
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Permission", "ACTIVITY_RECOGNITION permission granted.")
                // 권한이 부여되었으므로 ViewModel에 센서 시작 요청
                stepViewModel.startListening()
            } else {
                Log.w("Permission", "ACTIVITY_RECOGNITION permission denied.")
                // 권한 거부 처리 (예: 사용자에게 알림)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepViewModel = ViewModelProvider(this).get(StepViewModel::class.java)

        // 앱 켤 때마다 토큰 초기화 (테스트용)
//        val context = this.applicationContext
//        CoroutineScope(Dispatchers.IO).launch {
//            com.example.diaviseo.datastore.TokenDataStore.clearAccessToken(context)
//        }

        // WorkManager에 자정 스케줄 예약
        scheduleMidnightWorker()

        setContent {
            DiaViseoTheme {
                val systemUiController = rememberSystemUiController()
                val navController = rememberNavController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = true // 글씨와 아이콘을 검은색으로
                    )
                }
                TransparentStatusBar(window) // setContent {} 안에서 호출
                // 회원가입 & 로그인 로직 구현 이후
                // 로그인, 회원가입된 사용자 -> MainScreen으로
                // 회원가입해야하는 신규 유저 -> SignupNavGraph로 이동하도록 수정 필요

                testViewModel.printAccessToken(this)

                NavHost(navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    signupNavGraph(navController)
                    composable("main") { MainScreen() }
                }
            }
        }

        // FCM 토큰 발급 및 저장
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("FCM", "✅ FCM 토큰 발급됨: $token")

            CoroutineScope(Dispatchers.IO).launch {
                com.example.diaviseo.datastore.FcmTokenManager.saveToken(applicationContext, token)
                Log.d("FCM", "✅ FCM 토큰 저장 완료")
            }
        }.addOnFailureListener {
            Log.e("FCM", "❌ FCM 토큰 발급 실패", it)
        }



        // 권한 체크 및 센서 리스너 시작
        checkAndRequestPermission()

        // stepViewModel의 stepCount 관찰 (UI 업데이트 등)
        // lifecycleScope.launch {
        //     viewModel.stepCount.collect { steps ->
        //         // UI 업데이트
        //     }
        // }
    }

    // 자정 직후에 한 번, 그리고 24시간 주기로 실행되도록 설정
    private fun scheduleMidnightWorker() {
        val now = LocalDateTime.now()
        val tomorrowMidnight = now.toLocalDate().atTime(LocalTime.MIDNIGHT).plusDays(1)
        val initialDelay = Duration.between(now, tomorrowMidnight).toMinutes()

        val work = PeriodicWorkRequestBuilder<StepResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StepReset",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, // Activity/Fragment의 Context 사용
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 있음
                Log.d("Permission", "ACTIVITY_RECOGNITION permission already granted.")
                stepViewModel.startListening() // ViewModel에 센서 시작 요청
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                // 사용자에게 권한이 필요한 이유 설명 (예: AlertDialog)
                Log.d("Permission", "Showing rationale for ACTIVITY_RECOGNITION.")
                // 설명 후 권한 요청
                // showRationaleDialog { requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) }
                // 간단히 바로 요청
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            else -> {
                // 권한 요청
                Log.d("Permission", "Requesting ACTIVITY_RECOGNITION permission.")
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 필요하다면 onResume에서 다시 권한 확인 후 센서 재시작
        // (예: 사용자가 설정에서 권한을 껐다가 다시 켠 경우)
        // checkAndRequestPermission() -> 이미 onCreate에서 호출했다면 중복될 수 있으니 로직 확인 필요
        // 또는 ViewModel에서 리스너 등록 상태를 관리하고 필요 시 재등록
    }

    override fun onPause() {
        super.onPause()
        // Activity가 보이지 않을 때 센서 리스너 해제 (배터리 절약)
        stepViewModel.stopListening()
    }
}

class TestViewModel : ViewModel() {
    fun printAccessToken(context: Context) {
        viewModelScope.launch {
            val token = TokenDataStore.getAccessToken(context).first() // 🔥 바로 첫 번째 데이터만 읽기
            Log.d("TestViewModel", "저장된 accessToken: $token")
        }
    }
}
