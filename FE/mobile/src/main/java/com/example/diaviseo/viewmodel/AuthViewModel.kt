package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setName(name: String) {
        _name.value = name
    }
}
