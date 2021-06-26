package com.example.apnakitchen.commonModule.detailActivity

import android.icu.text.CaseMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.User
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.repository.cookRepository.CookRepo
import com.example.apnakitchen.repository.customerRepository.CustomerRepo
import com.google.protobuf.ListValue

class DetailActivityViewModel : ViewModel() {

    private var success = MutableLiveData<Resource<Boolean>>()
    private var _observer = MutableLiveData<Resource<User>>()


    fun updateSale(
        percentage: Int,
        saleStatus: Boolean,
        dishId: String
    ): LiveData<Resource<Boolean>> {

        success = CookRepo.updateSale(percentage, saleStatus, dishId)

        return success
    }

    fun getUser(userId: String): LiveData<Resource<User>> {

        _observer = AuthRepository.getOnlineUser(userId)

        return _observer
    }

    fun getActiveCook(cookId: String): LiveData<Resource<Cook>> {

        return CookRepo.getActiveCook(cookId)
    }

    fun userIsEligible(): LiveData<Resource<Boolean>> {

        return CustomerRepo.isEligible()
    }

    fun placeOrder(order: Order): LiveData<Resource<Boolean>> {

        return CustomerRepo.placeOrder(order)
    }

    fun updateOrderQueue(cookId: String, number: Int): LiveData<Resource<Boolean>> {

        return CookRepo.updateOrderQueue(cookId, number)
    }

    fun updateDish(
        title: String,
        price: Int,
        cookTime: Int,
        detail: String,
        foodType: String,
        dishId: String
    ): LiveData<Resource<Boolean>> {
        success = CookRepo.updateDish(title, price, cookTime, detail, foodType, dishId)
        return success
    }


}