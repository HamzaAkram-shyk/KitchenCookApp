package com.example.apnakitchen.userdashboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.customerModel.ListModel
import com.example.apnakitchen.repository.cookRepository.CookRepo
import com.example.apnakitchen.repository.customerRepository.CustomerRepo
import com.squareup.okhttp.Response

class HomeViewModel : ViewModel() {
    private var _value = MutableLiveData<Resource<ListModel>>()

    fun getItem(foodType: String): LiveData<Resource<ListModel>> {
        _value = CustomerRepo.getDishItem(foodType)
        return _value
    }

}