package com.example.apnakitchen.userdashboard.ui.currentorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.CUSTOMER
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.repository.authRepository.AuthRepository

class CustomerOrderViewModel : ViewModel() {


    fun getPendingOrders(customerId:String):LiveData<Resource<List<Order>>>{
        return AuthRepository.getCurrentOrders(customerId, CUSTOMER)
    }


}