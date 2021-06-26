package com.example.apnakitchen.cookdashboard.ui.dishes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.cookRepository.CookRepo

class DishesViewModel : ViewModel() {

    var value = MutableLiveData<Resource<List<Dish>>>()


    fun getDishes(): LiveData<Resource<List<Dish>>> {

        value = CookRepo.getDishes()
        return value
    }

}