package com.example.apnakitchen.cookdashboard.ui.createDish

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.repository.cookRepository.CookRepo
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateDishViewModel : ViewModel() {

    private var success = MutableLiveData<Resource<Boolean>>()


    fun uploadDish(
        hashMap: HashMap<String, String>,
        imageMap: HashMap<String, Uri>
    ): LiveData<Resource<Boolean>> {


        success = CookRepo.addDish(hashMap, imageMap)





        return success



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