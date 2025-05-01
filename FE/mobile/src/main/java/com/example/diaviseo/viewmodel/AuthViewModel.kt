package com.example.diaviseo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

}
