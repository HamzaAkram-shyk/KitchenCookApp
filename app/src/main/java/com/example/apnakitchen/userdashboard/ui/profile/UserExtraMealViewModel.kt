package com.example.apnakitchen.userdashboard.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import com.example.apnakitchen.repository.extraMealRepo.ExtraMealRepository
import java.util.*

class UserExtraMealViewModel : ViewModel() {
    var value = MutableLiveData<Resource<List<ExtraMeal>>>()
    var request = MutableLiveData<Resource<Boolean>>()
    var calendar: Calendar = Calendar.getInstance()
    val hour: Int = calendar.get(Calendar.HOUR)
    val minutes: Int = calendar.get(Calendar.MINUTE)

    fun getValidMeals(): LiveData<Resource<List<ExtraMeal>>> {
        value = ExtraMealRepository.getValidMeals(hour, minutes)
        return value
    }

    fun placeOrder(order: ExtraMealOrder): LiveData<Resource<Boolean>> {
        request = ExtraMealRepository.placeOrder(order)

        return request
    }

}