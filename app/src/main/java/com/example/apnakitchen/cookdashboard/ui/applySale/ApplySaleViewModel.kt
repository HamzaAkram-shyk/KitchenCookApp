package com.example.apnakitchen.cookdashboard.ui.applySale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.cookRepository.CookRepo

class ApplySaleViewModel : ViewModel() {
    private var success = MutableLiveData<Resource<Boolean>>()


    fun updateDish(
        percentage: Int,
        saleStatus: Boolean,
        dishId: String
    ): LiveData<Resource<Boolean>> {
        success = CookRepo.updateSale(percentage, saleStatus, dishId)

        return success
    }
}