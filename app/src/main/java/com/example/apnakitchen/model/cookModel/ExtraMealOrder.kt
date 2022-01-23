package com.example.apnakitchen.model.cookModel

import com.example.apnakitchen.Utils.NULL

data class ExtraMealOrder(
    var orderId: String = NULL,
    var buyerId: String = NULL,
    var cookId: String = NULL,
    var buyerToken: String = NULL,
    var cookToken: String = NULL,
    var payment: Int = -1,
    var meal: ExtraMeal = ExtraMeal(),
    var deliver: Boolean = false,
    )
