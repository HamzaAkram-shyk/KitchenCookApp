package com.example.apnakitchen.cookdashboard.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apnakitchen.Utils.COOK
import com.example.apnakitchen.Utils.DataStoreRepository
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.repository.cookRepository.CookRepo
import kotlinx.coroutines.launch

class CookOrderViewModel : ViewModel() {


    fun getCurrentOrders(cookId: String): LiveData<Resource<List<Order>>> {

        return AuthRepository.getCurrentOrders(cookId, COOK)
    }


    fun getOrderQueue(cookId: String): LiveData<Resource<Int>> {
        return CookRepo.getOrderQueue(cookId)
    }

    fun deQueueOrder(cookId: String, queueValue: Int): LiveData<Resource<Boolean>> {
        return CookRepo.updateOrderQueue(cookId, queueValue)
    }

    fun deliverOrder(orderId: String): LiveData<Resource<Boolean>> {
        return CookRepo.deliverOrder(orderId)
    }

    fun addIntoRating(order: Order): LiveData<Resource<Boolean>> {
        return CookRepo.addOrderIntoRating(order)
    }

}