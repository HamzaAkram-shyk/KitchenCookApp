package com.example.apnakitchen.userdashboard.ui.deliver

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.CUSTOMER
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.repository.customerRepository.CustomerRepo

class DeliverOrderViewModel : ViewModel() {

    var deliverOrder: LiveData<Resource<List<Order>>>? = null

    init {
        try {
            val buyerId = AuthRepository.getCurrentUser().uid
            deliverOrder = CustomerRepo.getCompletedOrder(buyerId, CUSTOMER)
            Log.d("Error", "$buyerId")
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
        }


    }

}