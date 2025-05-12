package com.example.diaviseo.network

import android.util.Log
import com.example.diaviseo.AppContextHolder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diaviseo.BuildConfig
import kotlinx.coroutines.runBlocking
import com.example.diaviseo.datastore.TokenDataStore
import com.example.diaviseo.network.auth.AuthApiService
import com.example.diaviseo.network.user.UserApiService
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor
import com.example.diaviseo.network.exercise.ExerciseApiService
import com.example.diaviseo.network.meal.MealApiService
import com.example.diaviseo.network.food.FoodApiService
import com.example.diaviseo.network.foodset.FoodSetApiService

object RetrofitInstance {
    // 재발급 동기화를 위한 lock 객체
    private val refreshLock = Any()

    // 1. 로그용 Interceptor
    val logging = HttpLoggingInterceptor { message ->
        Log.d("RetrofitLog", message)  // 로그 태그 설정 가능
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY // JSON 본문까지 다 출력
    }

    // 2. 헤더/응답코드 확인용 Interceptor
    val authInterceptor = Interceptor { chain ->
        val context = AppContextHolder.appContext

        // DataStore의 Flow에서 값 1개만 꺼내기
        var accessToken = runBlocking {
            TokenDataStore.getAccessToken(context).first().orEmpty()
        }

        var request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        var response = chain.proceed(request)

        // 401 오류가 나면 자동으로 토큰 갱신 요청
        if (response.code == 401) {
            // 기존 응답 닫아주고
            response.close()

            synchronized(refreshLock) {
                // (중요) 다른 스레드가 이미 토큰을 갱신했는지 다시 확인
                val latestToken = runBlocking {
                    TokenDataStore.getAccessToken(context).first().orEmpty()
                }
                if (latestToken != accessToken) {
                    // 이미 갱신된 토큰이므로 그걸 사용
                    accessToken = latestToken
                } else {
                    // 진짜 첫 스레드만 이 지점에서 재발급 수행
                    val newResp = runBlocking { refreshService.refreshAuthToken() }
                    val newAccess = newResp.data?.accessToken.orEmpty()
                    val newRefresh = newResp.data?.refreshToken.orEmpty()

                    runBlocking {
                        TokenDataStore.saveAccessToken(context, newAccess)
                        TokenDataStore.saveRefreshToken(context, newRefresh)
                    }
                    accessToken = newAccess
                }
            }

            // 4) 갱신된 토큰으로 원래 요청 재구성 & 재시도
            request = chain.request().newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            response = chain.proceed(request)
        }
        Log.d("Network", "Response Code: ${response.code}")
        response
    }

    // 3. OkHttpClient 구성
    val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging) // 이 순서도 중요함!
        .build()

    // RetrofitInstance.api.어떤api요청() 형식으로 사용 가능
    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(logging)
                .build())
            .build()
            .create(AuthApiService::class.java)
    }

    // AuthApiService 중 refreshtoken 쪽만
    val refreshService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val context = AppContextHolder.appContext

                    // DataStore의 Flow에서 값 1개만 꺼내기
                    val refreshToken = runBlocking {
                        TokenDataStore.getRefreshToken(context).first() ?: ""
                    }

                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $refreshToken")
                        .build()
                    val response = chain.proceed(newRequest)
                    Log.d("Network", "Response Code: ${response.code}")
                    response
                }
                .build())
            .build()
            .create(AuthApiService::class.java)
    }

    private val retro: Retrofit by lazy {
        // 4. Retrofit 구성
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // 기능별 API 서비스 (아래와 같은 형식으로)
//    val homeApiService: HomeApiService by lazy {
//        retro.create(HomeApiService::class.java)
//    }
    // val homeResponse = homeApiService.getHomeData() 라고 사용하면 됩니다

    val userApiService: UserApiService by lazy {
        retro.create(UserApiService::class.java)
    }

    // 식단 (등록) 관련
    val dietApiService: FoodApiService by lazy {
        retro.create(FoodApiService::class.java)
    }

    val exerciseApiService: ExerciseApiService by lazy {
        retro.create(ExerciseApiService::class.java)
    }

    val mealApiService: MealApiService by lazy {
        retro.create(MealApiService::class.java)
    }

    val foodApiService: FoodApiService by lazy {
        retro.create(FoodApiService::class.java)
    }

    val foodSetApiService: FoodSetApiService by lazy {
        retro.create(FoodSetApiService::class.java)
    }
}