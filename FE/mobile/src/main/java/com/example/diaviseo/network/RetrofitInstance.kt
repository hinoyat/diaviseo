package com.example.diaviseo.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.diaviseo.BuildConfig

object RetrofitInstance {

    // RetrofitInstance.api.어떤api요청() 형식으로 사용 가능
    val api: AuthApiService by lazy {
        Retrofit.Builder()
//            .baseUrl(BuildConfig.BASE_URL)
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}