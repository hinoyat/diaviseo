package com.example.diaviseo.ui.signup.socialSignup

import android.app.Activity
import android.util.Log
import androidx.credentials.*
import com.example.diaviseo.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.*
import androidx.credentials.exceptions.GetCredentialException

object GoogleLoginManager {
    fun performLogin(
        activity: Activity,
        onSuccess: (email: String, name: String, idToken: String, activity: Activity) -> Unit,
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
        onSuccess: (email: String, name: String, idToken: String, activity: Activity) -> Unit
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
                        onSuccess(email, name, idToken, activity)

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