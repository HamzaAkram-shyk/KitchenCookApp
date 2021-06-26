package com.example.apnakitchen.model.cookModel

import android.location.Location
import com.example.apnakitchen.Utils.KitchenStatus
import com.example.apnakitchen.Utils.Loc
import com.example.apnakitchen.Utils.NULL
import com.example.apnakitchen.model.User

data class
Cook(
    override var user: User = User(),
    var rating: Int = 2,
    var about: String = NULL,
    var location:Loc=Loc(),
    var orderQueue:Int=0,
    var kitchenAddress: String = NULL,
    var kitchenStatus: KitchenStatus = KitchenStatus.ONLINE
) : Profile(user)


