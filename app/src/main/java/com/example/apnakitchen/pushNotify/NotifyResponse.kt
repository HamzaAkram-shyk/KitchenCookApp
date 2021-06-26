package com.example.apnakitchen.pushNotify

interface NotifyResponse {
    fun onDeliver(msg:String)
    fun onError(msg:String)
}