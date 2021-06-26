package com.example.apnakitchen.repository.authRepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.User
import com.example.apnakitchen.model.cookModel.Dish
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuthRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userToken = NO_TOKEN


    fun signIn(
        email: String,
        password: String,
        userType: String
    ): MutableLiveData<Resource<String>> {
        val success = MutableLiveData<Resource<String>>()
        success.value = Resource.loading("loading...")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    db.collection(USER_TABLE).document(user.uid)
                        .get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                val doc = it.result
                                if (doc!!.exists()) {
                                    val type = doc.getString(USER_TYPE)
                                    val token = doc.getString(TOKEN)
                                    if (type.equals(userType) && token == NO_TOKEN) {
                                        generateToken(object : GenerateToken {
                                            override fun onNewToken(token: String) {
                                                db.collection(USER_TABLE).document(user.uid)
                                                    .update(TOKEN, token)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            success.value =
                                                                Resource.success(user.uid, userType)
                                                        } else {
                                                            success.value = Resource.error(
                                                                it.exception.toString(), ""
                                                            )
                                                        }

                                                    }

                                            }

                                            override fun onError(error: String) {
                                                success.value = Resource.error(
                                                    error,
                                                    error
                                                )
                                            }

                                        })


                                    } else {
                                        val message =
                                            if (token == NO_TOKEN) "Your account Registered as ${type} Please Select Correct" else "This Account is already login into other device"
                                        success.value = Resource.error(
                                            message,
                                            userType
                                        )

                                    }
                                } else {
                                    success.value = Resource.error(
                                        "Document Not Exist ",
                                        userType
                                    )
                                }

                            } else {
                                success.value = Resource.error(it.exception.toString(), "Error")
                            }

                        }
                } else {
                    success.value = Resource.error(it.exception.toString(), "Error")
                }


            }

        return success
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        userType: String,
        imageUrl: String
    ): MutableLiveData<Resource<String>> {
        val success = MutableLiveData<Resource<String>>()
        success.value = Resource.loading("Loading...")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val user = User(
                        auth.uid.toString(), name, email, password, userType, imageUrl,
                        NO_TOKEN
                    )
                    Log.d(TAG, "1")
                    db.collection(USER_TABLE)
                        .document(user.userId!!)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d(TAG, "2")
                            generateToken(object : GenerateToken {
                                override fun onNewToken(token: String) {
                                    db.collection(USER_TABLE).document(user.userId)
                                        .update(TOKEN, token)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                success.value =
                                                    Resource.success(user.userId, userType)
                                            } else {
                                                success.value = Resource.error(
                                                    it.exception.toString(), ""
                                                )
                                            }

                                        }

                                }

                                override fun onError(error: String) {
                                    success.value = Resource.error(
                                        error,
                                        error
                                    )
                                }

                            })


                        }.addOnFailureListener {
                            Log.d(TAG, "3")
                            success.value = Resource.error(it.message.toString(), "Error")
                        }

                } else {
                    Log.d(TAG, "4: ${it.exception.toString()}")
                    success.value = Resource.error(it.exception.toString(), "Error")
                }


            }
        return success
    }

    fun signOut(): LiveData<Boolean> {
        val value = MutableLiveData<Boolean>()
        val user = getCurrentUser()
        db.collection(USER_TABLE).document(user.uid)
            .update(TOKEN, NO_TOKEN)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    Log.d(TAG, "SignOut")
                    value.value = true
                } else {
                    value.value = false
                }

            }


        return value
    }

    fun googleSignIn(
        userId: String,
        name: String,
        email: String,
        photoUrl: String,
        userType: String
    ): MutableLiveData<Resource<String>> {
        val success = MutableLiveData<Resource<String>>()
        success.value = Resource.loading("Loading.....")
        db.collection(USER_TABLE).document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val doc = it.result
                    if (doc!!.exists()) {
                        val type = doc.getString(USER_TYPE)
                        if (type.equals(userType)) {
                            generateToken(object : GenerateToken {
                                override fun onNewToken(token: String) {
                                    db.collection(USER_TABLE).document(userId).update(TOKEN, token)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                success.value = Resource.success(userId, userType)
                                            } else {
                                                success.value = Resource.error(
                                                    it.exception.toString(), ""
                                                )
                                            }

                                        }

                                }

                                override fun onError(error: String) {
                                    success.value = Resource.error(
                                        error,
                                        error
                                    )
                                }

                            })

                        } else {
                            success.value = Resource.error(
                                "Your account Registered as ${type} Please Select Correct",
                                userType
                            )
                        }

                    } else {
                        generateToken(object : GenerateToken {
                            override fun onNewToken(token: String) {
                                var user: User = User(
                                    userId, name, email, "123", userType, photoUrl,
                                    token
                                )
                                Log.d(TAG, "1")

                                db.collection("Users")
                                    .document(user.userId!!)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "2")
                                        success.value =
                                            Resource.success(user.userId!!, user.userType)

                                    }.addOnFailureListener { it ->
                                        Log.d(TAG, "3")
                                        success.value = Resource.error(it.message.toString(), "")
                                    }
                            }

                            override fun onError(error: String) {
                                success.value = Resource.error(error, "")
                            }

                        })


                    }
                } else {
                    success.value = Resource.error(it.exception.toString(), "")
                }
            }

        return success

    }

    fun getCurrentUser(): FirebaseUser {
        return auth.currentUser
    }

    fun getUser(userId: String): MutableLiveData<Resource<User>> {
        var success = MutableLiveData<Resource<User>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(USER_TABLE).document(userId)
                .get()
                .addOnSuccessListener {
                    val user: User? = it.toObject(User::class.java)
                    success.value = Resource.success("1", user)
                }
                .addOnFailureListener {
                    success.value = Resource.error("${it.message.toString()}", null)

                }
        }
        return success

    }

    private fun generateToken(callback: GenerateToken) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
                userToken = NO_TOKEN
                callback.onError(task.exception.toString())
            }


            callback.onNewToken(task.result!!)
        })
    }

    fun getOnlineUser(userId: String): MutableLiveData<Resource<User>> {
        var success = MutableLiveData<Resource<User>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(USER_TABLE).document(userId)
                .get()
                .addOnSuccessListener {
                    val user: User? = it.toObject(User::class.java)
                    if (user?.token != NO_TOKEN) {
                        success.value = Resource.success("1", user)
                    } else {
                        success.value =
                            Resource.error("Currently Cook Cannot Accept your Order", null)
                    }

                }
                .addOnFailureListener {
                    success.value = Resource.error("${it.message.toString()}", null)

                }
        }
        return success

    }


    fun getCurrentOrders(userId: String, userType: String): MutableLiveData<Resource<List<Order>>> {
        val success = MutableLiveData<Resource<List<Order>>>()
        success.value = Resource.loading(emptyList())
        val orderList = ArrayList<Order>()
        orderList.clear()
        val field = if (userType == COOK) COOK_ID else BuyerId
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE)
                .whereEqualTo(field, userId)
                .whereEqualTo(DELIVER, false)
                .orderBy(TimeStamp, Query.Direction.ASCENDING
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


}

interface GenerateToken {
    fun onNewToken(token: String)
    fun onError(error: String)
}