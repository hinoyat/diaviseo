package com.example.diaviseo.viewmodel

import android.app.Activity
import android.app.Application
import android.hardware.camera2.CaptureFailure
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.diaviseo.datastore.TokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

import com.example.diaviseo.network.RetrofitInstance
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.diaviseo.AppContextHolder
import com.example.diaviseo.network.auth.dto.req.GoogleLoginRequest
import com.example.diaviseo.network.user.UserApiService
import com.example.diaviseo.network.user.dto.req.PhoneAuthConfirmRequest
import com.example.diaviseo.network.user.dto.req.PhoneAuthTryRequest
import com.example.diaviseo.network.user.dto.req.SignUpWithDiaRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel (application: Application) : AndroidViewModel(application) {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _idToken = MutableStateFlow("")
    val idToken: StateFlow<String> = _idToken

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _authCode = MutableStateFlow("")
    val authCode: StateFlow<String> = _authCode

    private val _isPhoneAuth = MutableStateFlow(false)
    val isPhoneAuth: StateFlow<Boolean> = _isPhoneAuth

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _provider = MutableStateFlow("")
    val provider: StateFlow<String> = _provider

    private val _goal = MutableStateFlow("") // "감량", "유지", "증량"
    val goal: StateFlow<String> = _goal

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage


    fun setEmail(email: String) {
        _email.value = email
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setIdToken(idToken: String) {
        _idToken.value = idToken
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setPhone(phone: String) {
        _phone.value = phone
    }

    fun setauthCode(authCode: String) {
        _authCode.value = authCode
    }

    fun setProvider(provider: String) {
        _provider.value = provider
    }

    fun setGoal(goal: String) {
        _goal.value = goal
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

    fun setToastMessage(msg : String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    fun loginWithGoogle(idToken: String, activity: Activity, onResult: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true  // 💡 스피너 ON

            try {
                val request = GoogleLoginRequest("google", idToken)
                val response = RetrofitInstance.authApiService.loginWithGoogle(request)

                val isNewUser = response.data?.newUser ?: true
                val context = AppContextHolder.appContext
                onResult(true, isNewUser)

                // 기존 회원이면 토큰 넣을테고 아니면 안 넣을테고
                TokenDataStore.saveAccessToken(context, response.data?.accessToken ?: "")
                TokenDataStore.saveRefreshToken(context, response.data?.refreshToken ?: "") // 선택적 저장

                // 메인스레드에서 Toast 띄우 기
//                  withContext(Dispatchers.Main) {
//                      Toast.makeText(activity, "환영합니다, ${body.userId}님!", Toast.LENGTH_SHORT).show()
//                  }
            } catch (e: HttpException) {
                // HTTP 오류 코드(4xx,5xx) 처리
                onResult(false, false)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "로그인 실패: ${e}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: IOException) {
                // 네트워크 오류 처리
                onResult(false, false)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "로그인 실패: ${e}", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            isLoading = false // 스피너 OFF
        }
    }

    fun phoneAuthTry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = PhoneAuthTryRequest(phone.value)
                val response = RetrofitInstance.userApiService.phoneAuthTry(request)

                val msg = response.message
                // 메시지를 흘려보냄
                _toastMessage.emit(msg)

                // HTTP 응답 자체가 “성공”이라면
                onSuccess()
            } catch (e: HttpException) {
                // HTTP 에러 코드 + 바디 파싱
                val errorJson = e.response()?.errorBody()?.string()
                val errorMsg = errorJson?.let {
                    runCatching {
                        JSONObject(it).optString("message")
                            .takeIf { msg -> msg.isNotBlank() }
                            ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                    }.getOrDefault("인증 요청에 실패했습니다 (HTTP ${e.code()})")
                } ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                _toastMessage.emit(errorMsg)
            } catch (e: IOException) {
                // 네트워크 오류 등
                _toastMessage.emit("네트워크 오류가 발생했습니다: ${e.message}")
            } catch (e: Exception) {
                // 그 외 예외
                _toastMessage.emit("알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun phoneAuthConfirm() {
        viewModelScope.launch {
            try {
                val request = PhoneAuthConfirmRequest(phone.value, authCode.value)
                val response = RetrofitInstance.userApiService.phoneAuthConfirm(request)

                val msg = response.message
                // 메시지를 흘려보냄
                _toastMessage.emit(msg)
                _isPhoneAuth.value = true
            } catch (e: HttpException) {
                // HTTP 에러 코드 + 바디 파싱
                val errorJson = e.response()?.errorBody()?.string()
                val errorMsg = errorJson?.let {
                    runCatching {
                        JSONObject(it).optString("message")
                            .takeIf { msg -> msg.isNotBlank() }
                            ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                    }.getOrDefault("인증 요청에 실패했습니다 (HTTP ${e.code()})")
                } ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                _toastMessage.emit(errorMsg)
            } catch (e: IOException) {
                // 네트워크 오류 등
                _toastMessage.emit("네트워크 오류가 발생했습니다: ${e.message}")
            } catch (e: Exception) {
                // 그 외 예외
                _toastMessage.emit("알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

//    뷰모델 새로 켜질때마다 확인할 수 있을까?
    fun printAllState () {
        viewModelScope.launch {
            Log.d("authviewmodel all state", "email : ${_email.value}")
            Log.d("authviewmodel all state", "name : ${_name.value}")
            Log.d("authviewmodel all state", "phone : ${_phone.value}")
            Log.d("authviewmodel all state", "nickname : ${_nickname.value}")
            Log.d("authviewmodel all state", "gender : ${_gender.value}")
            Log.d("authviewmodel all state", "birthday : ${_birthday.value}")
            Log.d("authviewmodel all state", "provider : ${_provider.value}")
            Log.d("authviewmodel all state", "height : ${_height.value}")
            Log.d("authviewmodel all state", "weight : ${_weight.value}")
            Log.d("authviewmodel all state", "goal : ${_goal.value}")
        }
    }

    fun signUpWithDia (tempGoal: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext

            try {
                val request = SignUpWithDiaRequest(
                    name.value,
                    nickname.value,
                    gender.value,
                    tempGoal,
                    birthday.value,
                    height.value.toFloat(),
                    weight.value.toFloat(),
                    phone.value,
                    email.value,
                    provider.value,
                    true,
                    true
                )
                val response = RetrofitInstance.userApiService.signUpWithDia(request)

                if (response.status == "CREATED"){
                    val requestGoogle = GoogleLoginRequest("google", idToken.value)
                    val responseGoogle = RetrofitInstance.authApiService.loginWithGoogle(requestGoogle)

                    TokenDataStore.saveAccessToken(context, responseGoogle.data?.accessToken ?: "")
                    TokenDataStore.saveRefreshToken(context, responseGoogle.data?.refreshToken ?: "")
                    _idToken.value = ""   // 성공하면 구글 idtoken 삭제

                    // 로그인 후 FCM 토큰 서버 전송
                    val fcmToken = com.example.diaviseo.datastore.FcmTokenManager.getToken(context)
                    if (!fcmToken.isNullOrBlank()) {
                        com.example.diaviseo.utils.FcmTokenSender.sendTokenToServer(fcmToken)
                        Log.d("FCM", "✅ 로그인 후 FCM 토큰 서버 전송 시도")
                    } else {
                        Log.d("FCM", "⚠️ FCM 토큰이 비어 있어 서버 전송 생략")
                    }

                }

            } catch (e: HttpException) {
                // HTTP 에러 코드 + 바디 파싱
                val errorJson = e.response()?.errorBody()?.string()
                val errorMsg = errorJson?.let {
                    runCatching {
                        JSONObject(it).optString("message")
                            .takeIf { msg -> msg.isNotBlank() }
                            ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                    }.getOrDefault("인증 요청에 실패했습니다 (HTTP ${e.code()})")
                } ?: "인증 요청에 실패했습니다 (HTTP ${e.code()})"
                _toastMessage.emit(errorMsg)
            } catch (e: IOException) {
                // 네트워크 오류 등
                _toastMessage.emit("네트워크 오류가 발생했습니다: ${e.message}")
            } catch (e: Exception) {
                // 그 외 예외
                _toastMessage.emit("알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun logout(onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            val context = getApplication<Application>().applicationContext

            try {
                val accessToken = TokenDataStore.getAccessToken(context).firstOrNull()
                val refreshToken = TokenDataStore.getRefreshToken(context).firstOrNull()

                if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                    val errorMsg = "토큰이 유효하지 않습니다."
                    _toastMessage.emit(errorMsg)
                    onFailure(errorMsg)
                    isLoading = false
                    return@launch
                }

                val response = RetrofitInstance.authApiService.logoutWithTokens(
                    accessToken = "Bearer $accessToken",
                    refreshToken = "Bearer $refreshToken"
                )

                if (response.status == "OK") {
                    TokenDataStore.clearAccessToken(context)
                    _toastMessage.emit("정상적으로 로그아웃되었습니다.")
                    onSuccess()
                } else {
                    val errorMsg = response.message.ifBlank { "로그아웃 실패" }
                    _toastMessage.emit(errorMsg)
                    onFailure(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "네트워크 오류 또는 예외 발생: ${e.localizedMessage}"
                Log.e("LOGOUT", errorMsg)
                _toastMessage.emit(errorMsg)
                onFailure(errorMsg)
            }

            isLoading = false
        }
    }
    fun deleteUser(onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            val context = getApplication<Application>().applicationContext

            try {
                val response = RetrofitInstance.userApiService.deleteUser()

                if (response.status == "OK") {
                    TokenDataStore.clearAccessToken(context)
                    _toastMessage.emit("회원 탈퇴가 완료되었습니다.")
                    onSuccess()
                } else {
                    val errorMsg = response.message.ifBlank { "회원 탈퇴 실패" }
                    _toastMessage.emit(errorMsg)
                    onFailure(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "네트워크 오류 또는 예외 발생: ${e.localizedMessage}"
                Log.e("DELETE_USER", errorMsg)
                _toastMessage.emit(errorMsg)
                onFailure(errorMsg)
            }

            isLoading = false
        }
    }

}