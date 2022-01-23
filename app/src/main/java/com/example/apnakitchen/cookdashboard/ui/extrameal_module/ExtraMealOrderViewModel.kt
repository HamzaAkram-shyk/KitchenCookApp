package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import com.example.apnakitchen.repository.extraMealRepo.ExtraMealRepository

class ExtraMealOrderViewModel : ViewModel() {
    var value = MutableLiveData<Resource<List<ExtraMealOrder>>>()


    fun getOrders(): LiveData<Resource<List<ExtraMealOrder>>> {
        value=ExtraMealRepository.getOrders()
        return value
    }
}