package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.repository.cookRepository.CookRepo
import com.example.apnakitchen.repository.extraMealRepo.ExtraMealRepository

class ExtraMealListViewModel : ViewModel() {
    var value = MutableLiveData<Resource<List<ExtraMeal>>>()


    fun getMeals(): LiveData<Resource<List<ExtraMeal>>> {

        value = ExtraMealRepository.getExtraMeals()
        return value
    }
}