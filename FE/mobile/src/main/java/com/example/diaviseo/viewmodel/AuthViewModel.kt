package com.example.diaviseo.viewmodel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.diaviseo.datastore.TokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

//채현 추가
import com.example.diaviseo.network.GoogleLoginRequest
import com.example.diaviseo.network.RetrofitInstance
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.diaviseo.network.TestLoginRequest


class AuthViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _nickname = MutableStateFlow("s206")
    val nickname: StateFlow<String> = _nickname

    private val _gender = MutableStateFlow("F")
    val gender: StateFlow<String> = _gender

    private val _birthday = MutableStateFlow("1999-09-22")
    val birthday: StateFlow<String> = _birthday

    private val _phone = MutableStateFlow("01012345678")
    val phone: StateFlow<String> = _phone

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _provider = MutableStateFlow("")
    val provider: StateFlow<String> = _provider

    private val _consentPersonal = MutableStateFlow(true)
    val consentPersonal: StateFlow<Boolean> = _consentPersonal

    private val _locationPersonal = MutableStateFlow(true)
    val locationPersonal: StateFlow<Boolean> = _locationPersonal

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setProvider(provider: String) {
        _provider.value = provider
    }
    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setBirthday(birthday: String) {
        _birthday.value = birthday
    }

    fun setHeight(height: String) {
        _height.value = height
    }

    fun setWeight(weight: String) {
        _weight.value = weight
    }

    fun setConsentPersonal(consent: Boolean) {
        _consentPersonal.value = consent
    }

    fun setLocationPersonal(consent: Boolean) {
        _locationPersonal.value = consent
    }

    var isLoading by mutableStateOf(false)
        private set

    fun loginWithGoogle(idToken: String, activity: Activity, onResult: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true  // 💡 스피너 ON

            // 진짜 구글 로그인일 경우
//            val request = GoogleLoginRequest("google", idToken)
//            val response = RetrofitInstance.authApiService.loginWithGoogle(request)

            // 테스트 경우
            val request = TestLoginRequest("s12c1s206@gmail.com", "google")
            val response = RetrofitInstance.authApiService.loginWithTest(request)

            if (response.isSuccessful) {
                val body = response.body()
                val isNewUser = body?.data?.newUser ?: true
                val context = activity.applicationContext
                onResult(true, isNewUser)

                TokenDataStore.saveAccessToken(context, body?.data?.accessToken?:"")
                TokenDataStore.saveRefreshToken(context, body?.data?.refreshToken?:"") // 선택적 저장

                // 메인스레드에서 Toast 띄우 기
//                  withContext(Dispatchers.Main) {
//                      Toast.makeText(activity, "환영합니다, ${body.userId}님!", Toast.LENGTH_SHORT).show()
//                  }

            } else {
                onResult(false, false)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            isLoading = false // 💡 스피너 OFF
        }
    }
}
