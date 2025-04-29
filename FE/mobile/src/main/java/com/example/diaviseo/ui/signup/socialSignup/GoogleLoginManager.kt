package com.example.diaviseo.ui.signup.socialSignup

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.*
import com.example.diaviseo.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.*
import androidx.credentials.exceptions.GetCredentialException
import com.example.diaviseo.datastore.TokenDataStore
import com.example.diaviseo.network.RetrofitInstance

object GoogleLoginManager {

    fun performLogin(
        activity: Activity,
        onSuccess: (email: String, name: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val credentialManager = CredentialManager.create(activity)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
//            .setAutoSelectEnabled(true)   // 자동로그인
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity,
                )
                handleSignIn(activity, result, onSuccess)
            } catch (e : GetCredentialException) {
                onError(e)
            }
        }
    }

    private fun handleSignIn(
        activity: Activity,
        result: GetCredentialResponse,
        onSuccess: (email: String, name: String) -> Unit
    ) {
        val credential = result.credential
        val TAG = "GoogleLogin"

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val email = googleIdTokenCredential.id
                        val name = googleIdTokenCredential.displayName ?: ""
                        val idToken = googleIdTokenCredential.idToken
                        Log.d(TAG, "email: $email, name: $name, idToken: $idToken")
                        onSuccess(email, name)

                        // 백엔드 통신 로직 호출 넣기
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            // 백엔드 코드 만들어지면 loginWithGoogle에 idToken 넣어보내기
                            val response = RetrofitInstance.api.loginWithGoogle()

                            // response 성공적이고 response.body().isNewUser가 false일 때로 추후 변경
                            // 만약 이미 있는 회원이면
                            if (response.isSuccessful) {
                                val body = response.body()
                                val context = activity.applicationContext

                                if (body != null) {
                                    Log.d("LoginSuccess", "accessToken: ${body.userId}")
                                    Log.d("LoginSuccess", "email: ${body.title}")

//                                    TokenDataStore.saveAccessToken(context, body.accessToken)
//                                    TokenDataStore.saveRefreshToken(context, body.refreshToken)
                                    TokenDataStore.saveAccessToken(context, "1234")
                                    TokenDataStore.saveRefreshToken(context, "1234") // 선택적 저장

                                    // 메인스레드에서 Toast 띄우기
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(activity, "환영합니다, ${body.userId}님!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(activity, "응답 본문이 비어있습니다", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(activity, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Invalid google id token", e)
                        throw e
                    }
                } else {
                    throw IllegalStateException("Unexpected credential type")
                }
            }
            else -> {
                throw IllegalStateException("Unexpected credential type")
            }
        }
    }
}