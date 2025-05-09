package com.example.diaviseo.network

import android.util.Log
import com.example.diaviseo.AppContextHolder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diaviseo.BuildConfig
import kotlinx.coroutines.runBlocking
import com.example.diaviseo.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor

object RetrofitInstance {
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
        val accessToken = runBlocking {
            TokenDataStore.getAccessToken(context).first() ?: ""
        }

        Log.d("Network", "accesstoken 불러오기 : $accessToken")
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val response = chain.proceed(newRequest)
        // 401 오류가 나면 자동으로 토큰 갱신 요청 => 우왕 나중에 써야지
//                if (response.code == 401) {
//                    val newToken = refreshAuthToken() // 새로운 토큰을 갱신하는 함수
//                    val newRequest = request.newBuilder()
//                        .addHeader("Authorization", "Bearer $newToken")
//                        .build()
//
//                    // 새 토큰으로 요청 다시 보내기
//                    return@addInterceptor chain.proceed(newRequest)
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

    val profileApiService: ProfileApiService by lazy {
        retro.create(ProfileApiService::class.java)
    }
}