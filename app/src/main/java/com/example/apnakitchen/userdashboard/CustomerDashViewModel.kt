package com.example.apnakitchen.userdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.repository.customerRepository.CustomerRepo

class CustomerDashViewModel : ViewModel() {
    private var observer = MutableLiveData<Resource<List<Order>>>()

    fun getCompletedOrder(customerId: String): LiveData<Resource<List<Order>>> {
        observer = CustomerRepo.getRatingOrder(customerId)
        return observer
    }

    fun rateDish(dishId: String, rating: Float): LiveData<Resource<Boolean>> {

        return CustomerRepo.updateDishRating(dishId, rating)
    }

    fun deleteOrder(orderId: String): LiveData<Resource<Boolean>> {

        return CustomerRepo.deQueueOrder(orderId)
    }

}