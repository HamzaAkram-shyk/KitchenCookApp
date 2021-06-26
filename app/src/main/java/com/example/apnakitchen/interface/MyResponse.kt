package com.example.apnakitchen.`interface`

interface MyResponse<in T> {
  fun success(data:T,msg:String)
  fun onError(data:T,msg:String)

}