package com.example.apnakitchen.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.repository.authRepository.AuthRepository

class AuthViewModel : ViewModel() {
    private lateinit var success: MutableLiveData<Resource<String>>
   private lateinit var googleAuthObserver: MutableLiveData<Resource<String>>


    fun signIn(email: String, password: String,userType: String): LiveData<Resource<String>> {
        success = AuthRepository.signIn(email, password,userType)
        return success
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        userType: String,
        imageUrl: String
    ): LiveData<Resource<String>> {
        success = AuthRepository.signUp(name, email, password, userType, imageUrl)
        return success
    }

    fun googleSigIn(
        userId: String,
        name: String,
        email: String,
        photoUrl: String,
        userType: String
    ): LiveData<Resource<String>> {

        googleAuthObserver = AuthRepository.googleSignIn(userId, name, email, photoUrl, userType)
        return googleAuthObserver
    }

}