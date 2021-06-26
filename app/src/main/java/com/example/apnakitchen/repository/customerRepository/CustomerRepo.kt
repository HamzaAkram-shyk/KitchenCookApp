package com.example.apnakitchen.repository.customerRepository

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.model.customerModel.ListModel
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.repository.cookRepository.CookRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CustomerRepo {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // This function takes food category type and return the list of those dishes which has this food category .
    fun getDishItem(foodType: String): MutableLiveData<Resource<ListModel>> {
        val success = MutableLiveData<Resource<ListModel>>()
        val itemList = ArrayList<Dish>()
        itemList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE)
                .whereEqualTo(FOOD_TYPE, foodType)
                .get().addOnSuccessListener { list ->
                    if (list.size() > 0) {
                        for (document in list) {
                            val item = document.toObject(Dish::class.java)
                            itemList.add(item)
                        }
                        val item = ListModel(foodType, itemList as List<Dish>)
                        success.value = Resource.success("", item)
                    } else {
                        success.value = Resource.error("1", null)
                    }

                }
                .addOnFailureListener {
                    Resource.error("${it.message.toString()}", null)
                }
        }

        return success
    }


    // This function take user current Location as Parameter and return List of those cooks ids who are
    //  Near By this User Current Location
    fun getNearByCook(userLocation: Location): MutableLiveData<Resource<List<String>>> {
        val success = MutableLiveData<Resource<List<String>>>()
        val cookIds = ArrayList<String>()
        cookIds.clear()
        success.value = Resource.loading(emptyList())
        db.collection(COOK_TABLE)
            .whereNotEqualTo(ADDRESS, NULL)
            .whereEqualTo(Kitchen_Status, KitchenStatus.ONLINE)
            .get().addOnSuccessListener { list ->
                for (document in list) {
                    val item = document.toObject(Cook::class.java)
                    if (item.location.nearBy(userLocation)) {
                        cookIds.add(item.user.userId.toString())
                    }
                }
                success.value =
                    if (cookIds.isNotEmpty()) Resource.success("done", cookIds) else Resource.error(
                        "0", emptyList()
                    )
            }.addOnFailureListener {
                success.value = Resource.error(it.message.toString(), emptyList())
            }

        return success

    }


    // This function takes cookId as an argument and return the list of all dishes which has same cookId
    fun getCookDishes(cookId: String): MutableLiveData<Resource<List<Dish>>> {
        val success = MutableLiveData<Resource<List<Dish>>>()
        val dishList = ArrayList<Dish>()
        dishList.clear()
        success.value = Resource.loading(emptyList())
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE)
                .whereEqualTo(COOK_ID, cookId)
                .get()
                .addOnSuccessListener { dishes ->
                    if (!dishes.isEmpty) {
                        for (document in dishes) {
                            val dish = document.toObject(Dish::class.java)
                            dishList.add(dish)
                        }
                        success.value = Resource.success("1", dishList)
                    } else {
                        success.value = Resource.success("0", emptyList())
                    }
                }
                .addOnFailureListener {
                    success.value = Resource.error("${it.message.toString()}", emptyList())
                }

        }



        return success

    }

    // Customer Make an order and add order into order db
    fun placeOrder(order: Order): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE).document(order.orderId)
                .set(order).addOnSuccessListener {
                    success.value = Resource.success("", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Your Order Not Place Successfully", false)
                    Log.d(TAG, "OrderError = ${it.message.toString()}")
                }
        }


        return success

    }

    // This function Check Whether the user is eligible for place an order or not there are certain
    // Conditions are there such as if the user already have three orders in the queue then they
    // cannot place more Order if not then they can place an order ..
    fun isEligible(): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        val orderList = ArrayList<Order>()
        orderList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE)
                .whereEqualTo(BuyerId, auth.currentUser.uid)
                .whereEqualTo(DELIVER, false)
                .get()
                .addOnSuccessListener { orders ->
                    if (!orders.isEmpty) {
                        for (document in orders) {
                            val order = document.toObject(Order::class.java)
                            orderList.add(order)
                        }

                        success.value =
                            if (orderList.size < 3) Resource.success("1", true) else Resource.error(
                                "Sorry you have already three Orders in the Queue Currently you are not allowed",
                                true
                            )
                    } else {
                        success.value = Resource.success("1", true)
                    }
                }
                .addOnFailureListener {
                    success.value =
                        Resource.error("There is Server Issue Please Try Again Later", false)
                    Log.d(TAG, "Order Error= ${it.message.toString()}")
                }


        }

        return success
    }

    // This function return the list of those orders which is completed like user can get pending orders with the help of this
    // Function
    fun getCompletedOrder(
        userId: String,
        userType: String
    ): MutableLiveData<Resource<List<Order>>> {
        val success = MutableLiveData<Resource<List<Order>>>()
        success.value = Resource.loading(emptyList())
        val orderList = ArrayList<Order>()
        orderList.clear()
        val field = if (userType == COOK) COOK_ID else BuyerId
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE)
                .whereEqualTo(field, userId)
                .whereEqualTo(DELIVER, true)
                .orderBy(
                    TimeStamp, Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener { orders ->
                    if (!orders.isEmpty) {
                        for (document in orders) {
                            val order = document.toObject(Order::class.java)
                            orderList.add(order)
                        }
                        success.value = Resource.success("1", orderList)
                    } else {
                        success.value = Resource.error("0", emptyList())
                    }
                }
                .addOnFailureListener {
                    success.value =
                        Resource.error("There is Server Issue Please Try Again Later", emptyList())
                    Log.d(TAG, "Order Error= ${it.message.toString()}")
                }


        }

        return success
    }

    fun getRatingOrder(
        userId: String
    ): MutableLiveData<Resource<List<Order>>> {
        val success = MutableLiveData<Resource<List<Order>>>()
        success.value = Resource.loading(emptyList())
        val orderList = ArrayList<Order>()
        orderList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(RATING_TABLE)
                .whereEqualTo(BuyerId, userId)
                .orderBy(
                    TimeStamp, Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener { orders ->
                    if (!orders.isEmpty) {
                        for (document in orders) {
                            val order = document.toObject(Order::class.java)
                            orderList.add(order)
                        }
                        success.value = Resource.success("1", orderList)
                    } else {
                        success.value = Resource.error("0", emptyList())
                    }
                }
                .addOnFailureListener {
                    success.value =
                        Resource.error("There is Server Issue Please Try Again Later", emptyList())
                    Log.d(TAG, "Order Error= ${it.message.toString()}")
                }


        }

        return success
    }

    fun updateDishRating(dishId: String, rating: Float): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE).document(dishId)
                .update(RATING, rating)
                .addOnSuccessListener {
                    success.value = Resource.success("", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Rating Not Success", false)
                    Log.d(TAG, it.message.toString())
                }
        }

        return success
    }

    fun deQueueOrder(orderId: String): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(RATING_TABLE).document(orderId)
                .delete()
                .addOnSuccessListener {
                    success.value = Resource.success("", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error(" Not Success", false)
                    Log.d(TAG, it.message.toString())
                }
        }

        return success
    }

    // This Extension Function takes an argument of location object and return whether this location is nearby or not from the Calling location
    private fun Loc.nearBy(location: Location): Boolean {
        val cookLoc = Location("")
        cookLoc.latitude = lat
        cookLoc.longitude = lon
        val distance = (cookLoc.distanceTo(location)) / 1000
        Log.d(TAG, "distance = $distance Km")
        return (distance <= 4)
    }




}