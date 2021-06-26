package com.example.apnakitchen.model

import android.os.Parcelable
import com.example.apnakitchen.Utils.NULL
import com.example.apnakitchen.model.cookModel.Dish
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    var orderId: String = NULL,
    var buyerId: String = NULL,
    var cookId: String = NULL,
    var buyerToken: String = NULL,
    var cookToken: String = NULL,
    var payment: Int = -1,
    var dish: Dish = Dish(),
    var deliver: Boolean = false,
    var timeStamp: Long = -1
): Parcelable
