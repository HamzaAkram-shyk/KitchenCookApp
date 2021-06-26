package com.example.apnakitchen.model
// userType define type of the user who is going to used application either cook or
// user who needs food

data class User(
    val userId: String?="",
    var name: String?="",
    val email: String?="",
    val password: String?="",
    val userType: String?="",
    var photoUrl: String?="",
    val token: String?=""
)