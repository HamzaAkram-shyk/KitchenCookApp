package com.example.apnakitchen.cookdashboard.ui.profile

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.KitchenStatus
import com.example.apnakitchen.Utils.Loc
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.User
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.repository.cookRepository.CookRepo

class ProfileViewModel : ViewModel() {
    private var success = MutableLiveData<Resource<Cook>>()
    private var updateObserver = MutableLiveData<Resource<Boolean>>()
    private var userObserver = MutableLiveData<Resource<User>>()
    private var addressObserver = MutableLiveData<Resource<String>>()


    fun getCurrentCook(): LiveData<Resource<Cook>> {
        success = CookRepo.getCurrentCook()
        return success

    }

    fun updateCook(cook: Cook): LiveData<Resource<Boolean>> {
        updateObserver = CookRepo.updateCook(cook)
        return updateObserver
    }

    fun getUser(): LiveData<Resource<User>> {
        val user = AuthRepository.getCurrentUser()
        userObserver = AuthRepository.getUser(user.uid)
        return userObserver
    }

    fun addAddress(address: String, location: Loc): LiveData<Resource<String>> {
        addressObserver = CookRepo.addKitchenAddress(address, location)
        return addressObserver
    }

    fun changeStatus(status: KitchenStatus): LiveData<Resource<Boolean>> {
        updateObserver = CookRepo.setKitchenStatus(status)

        return updateObserver
    }
}