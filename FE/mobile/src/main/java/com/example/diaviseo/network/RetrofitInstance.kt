package com.example.diaviseo.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diaviseo.BuildConfig
import okhttp3.OkHttpClient

object RetrofitInstance {

    // RetrofitInstance.api.어떤api요청() 형식으로 사용 가능
    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private val retro: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)// 공통 base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer token")  // 공통 헤더
                    .build()
                val response = chain.proceed(newRequest)

//                // 401 오류가 나면 자동으로 토큰 갱신 요청 => 우왕 나중에 써야지
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
            }.build())
            .build()
    }

    // 기능별 API 서비스 (아래와 같은 형식으로)
//    val homeApiService: HomeApiService by lazy {
//        retro.create(HomeApiService::class.java)
//    }
    // val homeResponse = homeApiService.getHomeData() 라고 사용하면 됩니다
}