package com.example.apnakitchen.userdashboard.ui.nearby

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.customerRepository.CustomerRepo

class NearByViewModel : ViewModel() {

    private var _ids = MutableLiveData<Resource<List<String>>>()
    private var _dishObserver = MutableLiveData<Resource<List<Dish>>>()
    var dishList = ArrayList<Dish>()

    init {
        dishList.clear()
    }

    fun getNearByCookIds(userLocation: Location): LiveData<Resource<List<String>>> {
        _ids = CustomerRepo.getNearByCook(userLocation)
        return _ids
    }

    fun getNearByDishes(cookId: String): LiveData<Resource<List<Dish>>> {
        _dishObserver = CustomerRepo.getCookDishes(cookId)
        return _dishObserver
    }

    fun appendList(list: List<Dish>) {
        dishList.addAll(list)
    }

    fun getFilterList(query: String): List<Dish> {
        val list = ArrayList<Dish>()
        list.clear()
        for (dish in dishList) {
            if (dish.name.contains(query, ignoreCase = true)) {
                list.add(dish)
            }
        }
        return list
    }


}