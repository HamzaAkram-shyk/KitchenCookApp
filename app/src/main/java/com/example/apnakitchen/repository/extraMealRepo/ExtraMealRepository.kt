package com.example.apnakitchen.repository.extraMealRepo

import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.COOK_ID
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExtraMealRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    const val extraMealDishDb = "extramealdb"
    const val extraMealOrderDb="ExtraMealOrderDb"

    fun addMeal(meal: ExtraMeal): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        db.collection(extraMealDishDb).document(meal.dishId)
            .set(meal)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    success.value = Resource.success("Uploaded Successfully", true)
                } else {
                    success.value = Resource.error(it.exception.toString(), false)

                }
            }
        return success
    }

    fun getExtraMeals(): MutableLiveData<Resource<List<ExtraMeal>>> {
        val success = MutableLiveData<Resource<List<ExtraMeal>>>()
        success.value = Resource.loading(null)
        val dishList = ArrayList<ExtraMeal>()
        dishList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(extraMealDishDb)
                .whereEqualTo(COOK_ID, auth.currentUser.uid)
                .get()
                .addOnSuccessListener { list ->
                    for (document in list) {
                        val item = document.toObject(ExtraMeal::class.java)
                        dishList.add(item)
                    }
                    success.value = Resource.success("Success", dishList)

                }.addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }

        }


        return success

    }

    fun getValidMeals(currentHour: Int, currentMinute: Int): MutableLiveData<Resource<List<ExtraMeal>>> {
        val success = MutableLiveData<Resource<List<ExtraMeal>>>()
        success.value = Resource.loading(null)
        val dishList = ArrayList<ExtraMeal>()
        dishList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(extraMealDishDb)
                .get()
                .addOnSuccessListener { list ->
                    for (document in list) {
                        val item = document.toObject(ExtraMeal::class.java)
                        if (item.preOrderTime - currentHour >= 1) {
                            dishList.add(item)
                        } else if (item.minute - currentMinute >= 20) {
                            dishList.add(item)
                        }

                    }
                    success.value = Resource.success("Success", dishList)

                }.addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }

        }


        return success

    }

    fun placeOrder(order: ExtraMealOrder): MutableLiveData<Resource<Boolean>> {
        val success = MutableLiveData<Resource<Boolean>>()
        success.value = Resource.loading(true)
        db.collection(extraMealOrderDb).document(order.orderId)
            .set(order)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    success.value = Resource.success("Uploaded Successfully", true)
                } else {
                    success.value = Resource.error(it.exception.toString(), false)

                }
            }
        return success
    }

    fun getOrders(): MutableLiveData<Resource<List<ExtraMealOrder>>> {
        val success = MutableLiveData<Resource<List<ExtraMealOrder>>>()
        success.value = Resource.loading(null)
        val dishList = ArrayList<ExtraMealOrder>()
        dishList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(extraMealOrderDb)
                .whereEqualTo(COOK_ID, auth.currentUser.uid)
                .get()
                .addOnSuccessListener { list ->
                    for (document in list) {
                        val item = document.toObject(ExtraMealOrder::class.java)
                        dishList.add(item)
                    }
                    success.value = Resource.success("Success", dishList)

                }.addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }

        }


        return success

    }

}