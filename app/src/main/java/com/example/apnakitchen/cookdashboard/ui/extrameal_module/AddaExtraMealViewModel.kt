package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.repository.extraMealRepo.ExtraMealRepository

class AddaExtraMealViewModel : ViewModel() {
    private var success = MutableLiveData<Resource<Boolean>>()

    fun addDish(meal: ExtraMeal): LiveData<Resource<Boolean>> {

        success = ExtraMealRepository.addMeal(meal)

        return success
    }


}