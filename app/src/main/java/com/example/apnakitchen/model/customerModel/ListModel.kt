package com.example.apnakitchen.model.customerModel

import android.icu.text.CaseMap
import com.example.apnakitchen.Utils.NULL
import com.example.apnakitchen.model.cookModel.Dish

data class ListModel(var title: String= NULL, var list:List<Dish>?= emptyList())
