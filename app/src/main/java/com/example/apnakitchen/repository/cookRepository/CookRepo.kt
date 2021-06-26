package com.example.apnakitchen.repository.cookRepository

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishFragment
import com.example.apnakitchen.imageupload.Cloud
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.User
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.customerRepository.CustomerRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CookRepo {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun addDish(
        data: HashMap<String, String>,
        images: HashMap<String, Uri>
    ): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        var dish = Dish()
        CoroutineScope(Dispatchers.IO).launch {
            Cloud.uploadImage(images[CreateDishFragment.firstImageKey]!!,
                data[CreateDishFragment.firstImageNameKey]!!,
                object : Cloud.CloudResponse {
                    override fun onSuccess(url: String) {
                        dish.firstImageName = data[CreateDishFragment.firstImageNameKey]!!
                        dish.firstImageUrl = url
                        Cloud.uploadImage(images[CreateDishFragment.secondImageKey]!!,
                            data[CreateDishFragment.secondImageNameKey]!!,
                            object : Cloud.CloudResponse {
                                override fun onSuccess(url: String) {
                                    dish.secondImageName =
                                        data[CreateDishFragment.secondImageNameKey]!!
                                    dish.secondImageUrl = url
                                    dish.CookTime =
                                        Integer.parseInt(data[CreateDishFragment.cookTimeKey])
                                    dish.price = Integer.parseInt(data[CreateDishFragment.priceKey])
                                    dish.detail = data[CreateDishFragment.dishDescKey]!!
                                    dish.foodType = data[CreateDishFragment.dishTypeKey]!!
                                    dish.name = data[CreateDishFragment.dishNameKey]!!
                                    dish.cookId = data[CreateDishFragment.userIdKey]!!
                                    dish.dishId = data[CreateDishFragment.dishIdKey]!!
                                    setDish(dish, object : Response {
                                        override fun onSuccess(value: String) {
                                            success.value = Resource.success(value, true)
                                            Log.d(TAG, "onSucces of dish")
                                        }

                                        override fun onError(value: String) {
                                            success.value = Resource.error(value, false)
                                            Log.d(TAG, "onError of dish")
                                        }

                                    })


                                }

                                override fun onError(error: String) {
                                    success.value = Resource.error(error, false)
                                }

                            }
                        )


                    }

                    override fun onError(error: String) {
                        success.value = Resource.error(error, false)
                    }

                }
            )


        }
        return success
    }

    private fun setDish(dish: Dish, response: Response) {
        db.collection(DISH_TABLE).document(dish.dishId)
            .set(dish)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    response.onSuccess()
                } else {
                    response.onError(it.exception.toString())
                }
            }
    }

    fun getDishes(): MutableLiveData<Resource<List<Dish>>> {
        val success = MutableLiveData<Resource<List<Dish>>>()
        success.value = Resource.loading(null)
        val dishList = ArrayList<Dish>()
        dishList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE)
                .whereEqualTo(COOK_ID, auth.currentUser.uid)
                .get()
                .addOnSuccessListener { list ->
                    for (document in list) {
                        val item = document.toObject(Dish::class.java)
                        dishList.add(item)
                    }
                    success.value = Resource.success("Success", dishList)

                }.addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }

        }


        return success

    }

    fun updateSale(
        percentage: Int,
        saleStatus: Boolean,
        dishId: String
    ): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE).document(dishId)
                .update(SALE, saleStatus, PERCENTAGE, percentage)
                .addOnSuccessListener {
                    success.value = Resource.success("$percentage", saleStatus)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), false)
                }
        }

        return success
    }

    fun updateDish(
        title: String,
        price: Int,
        cookTime: Int,
        detail: String,
        foodType: String,
        dishId: String
    ): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE).document(dishId)
                .update(
                    Title,
                    title,
                    PRICE,
                    price,
                    COOK_TIME,
                    cookTime,
                    DETAIL,
                    detail,
                    FOOD_TYPE,
                    foodType
                )
                .addOnSuccessListener {
                    success.value = Resource.success("success", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), false)
                }
        }
        return success
    }

    fun getSaleList(): MutableLiveData<Resource<List<Dish>>> {
        val success = MutableLiveData<Resource<List<Dish>>>()
        success.value = Resource.loading(null)
        val dishList = ArrayList<Dish>()
        dishList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(DISH_TABLE)
                .whereEqualTo(COOK_ID, auth.currentUser.uid)
                .whereEqualTo(SALE, true)
                .get()
                .addOnSuccessListener { list ->
                    for (document in list) {
                        val item = document.toObject(Dish::class.java)
                        dishList.add(item)
                    }
                    success.value = Resource.success("Success", dishList)

                }.addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }

        }

        return success
    }


    // Cook Update

    fun getCurrentCook(): MutableLiveData<Resource<Cook>> {
        val success = MutableLiveData<Resource<Cook>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(auth.currentUser.uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val doc = it.result!!
                        if (doc.exists()) {
                            val cook = doc.toObject(Cook::class.java)
                            success.value = Resource.success("1", cook)
                        } else {
                            success.value = Resource.error("0", null)
                        }
                    } else {
                        success.value = Resource.error("${it.exception.toString()}", null)
                    }

                }

        }

        return success
    }

    fun getActiveCook(cookId: String): MutableLiveData<Resource<Cook>> {
        val success = MutableLiveData<Resource<Cook>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(cookId)
                .get().addOnSuccessListener {
                    val cook: Cook = it.toObject(Cook::class.java)!!
                    if (cook.kitchenStatus == KitchenStatus.ONLINE && cook.orderQueue < 3) {
                        success.value = Resource.success("Success", cook)
                    } else {
                        success.value = Resource.error(
                            "Sorry Currently Cook is Not Available Please Try Again",
                            null
                        )
                    }
                }
                .addOnFailureListener {
                    success.value = Resource.error("Error = ${it.message.toString()}", null)
                }


        }

        return success
    }

    fun updateCook(cook: Cook): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        db.collection(COOK_TABLE).document(auth.currentUser.uid)
            .set(cook).addOnSuccessListener {
                success.value = Resource.success("", true)
            }
            .addOnFailureListener {
                success.value = Resource.error("${it.message.toString()}", false)
            }
        return success

    }


    fun addKitchenAddress(
        address: String,
        location: Loc
    ): MutableLiveData<Resource<String>> {
        val success = MutableLiveData<Resource<String>>()
        success.value = Resource.loading("")
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(auth.currentUser.uid)
                .update(ADDRESS, address, Location, location)
                .addOnSuccessListener {
                    success.value = Resource.success("", address)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), address)
                }

        }

        return success

    }

    fun setKitchenStatus(status: KitchenStatus): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(auth.currentUser.uid)
                .update(Kitchen_Status, status)
                .addOnSuccessListener {
                    success.value = Resource.success("", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), false)
                }
        }

        return success
    }

    fun updateOrderQueue(cookId: String, newNumber: Int): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(cookId)
                .update(Order_Queue, newNumber)
                .addOnSuccessListener {
                    success.value = Resource.success("", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), false)
                }
        }

        return success
    }

    fun getOrderQueue(cookId: String): MutableLiveData<Resource<Int>> {
        val success = MutableLiveData<Resource<Int>>()
        success.value = Resource.loading(0)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(COOK_TABLE).document(cookId)
                .get().addOnSuccessListener {
                    val cook: Cook = it.toObject(Cook::class.java)!!
                    success.value = Resource.success("", cook.orderQueue)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Please Try Again there is Server Issue", -1)
                }
        }
        return success
    }

    fun deliverOrder(orderId: String): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(false)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE).document(orderId)
                .update(DELIVER, true)
                .addOnSuccessListener {
                    success.value = Resource.success("Done", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Please Try Again there is Server Issue", false)
                    Log.d(TAG, it.message.toString())
                }
        }



        return success
    }

    fun addOrderIntoRating(order:Order):MutableLiveData<Resource<Boolean>>{
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(false)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(RATING_TABLE).document(order.orderId)
                .set(order)
                .addOnSuccessListener {
                    success.value = Resource.success("Done", true)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Please Try Again there is Server Issue", false)
                    Log.d(TAG, it.message.toString())
                }
        }



        return success
    }

}

interface Response {
    fun onSuccess(value: String = "Success")
    fun onError(value: String)
}