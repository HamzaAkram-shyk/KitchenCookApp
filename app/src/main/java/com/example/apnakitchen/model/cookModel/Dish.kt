package com.example.apnakitchen.model.cookModel

import android.os.Parcelable
import com.google.android.material.animation.MotionTiming
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dish(
    var dishId: String = "",
    var cookId: String = "",
    var name: String = "",
    var foodType: String = "",
    var detail: String = "",
    var price: Int = 0,
    var CookTime: Int = 0,
    var firstImageUrl: String = "",
    var secondImageUrl: String = "",
    var firstImageName: String = "",
    var secondImageName: String = "",
    var salePercent: Int = -1,
    var sale: Boolean = false,
    var rating: Double = 0.0
) : Parcelable
