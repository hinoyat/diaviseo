package com.example.diaviseo

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.appContext = applicationContext
    }
}

// 앱 전체에서 context에 접근할 수 있도록 보관
object AppContextHolder {
    lateinit var appContext: Context
}
