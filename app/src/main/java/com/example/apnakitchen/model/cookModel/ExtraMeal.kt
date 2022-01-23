package com.example.apnakitchen.model.cookModel

data class ExtraMeal(
    var dishId: String = "",
    var cookId: String = "",
    var name: String = "",
    var preOrderTime: Int = -1,
    var minute: Int = 0,
    var noOfMeal: Int = -1,
    var detail: String = "",
    var price: Int = 0,
    var CookTime: Int = 0,
    var imageUrl: String = "",
)
