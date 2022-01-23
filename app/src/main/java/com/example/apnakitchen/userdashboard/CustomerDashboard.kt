package com.example.apnakitchen.userdashboard

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.authentication.SigIn_SignUp
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.pushNotify.FirebaseService
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_customer_dashboard.*
import kotlinx.android.synthetic.main.cook_profile.*
import kotlinx.android.synthetic.main.cook_rating_dailog.*
import kotlinx.android.synthetic.main.update_dailog.*
import kotlinx.coroutines.launch

class CustomerDashboard : AppCompatActivity() {

    companion object {
        const val Key = "mykey"
        var isRunningOnForeground: Boolean = false
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Notification_broadcast -> {
                    val title = intent.getStringExtra(FirebaseService._titleKey)!!
                    val message = intent.getStringExtra(FirebaseService._messageKey)!!
                    Reuse.customDialog(
                        this@CustomerDashboard,
                        R.drawable.chef_icon,
                        title,
                        message
                    ) {
                        if (it) {
                            navController.navigate(R.id.navigation_deliverOrder)
                        }

                    }
                }
            }
        }

    }
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var dataStore: DataStoreRepository
    private lateinit var viewModel: CustomerDashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)
        bottomNavigation = findViewById(R.id.customer_bottomBar)
        navController = findNavController(R.id.customer_fragment)
        bottomNavigation.setupWithNavController(navController)
        dataStore = DataStoreRepository.getInstance(this)
        viewModel = ViewModelProvider(this).get(CustomerDashViewModel::class.java)
        isRunningOnForeground = true
        intent.let {
            val navigation = it.getIntExtra(Key, -1)
            if (navigation == 1) {
                navController.navigate(R.id.navigation_deliverOrder)
                return
            } else if (navigation == 0) {
                navController.navigate(R.id.navigation_currentOrder)
                return
            }
        }
        lifecycleScope.launch {
            if (dataStore.getOrderQueue()!!) {
                ratingDialog(dataStore.getUserId()!!)
                Log.d(TAG, "Working... ${dataStore.getOrderQueue()!!}")
            }
        }

        logoutIcon.setOnClickListener {
            AuthRepository.signOut().observe(this, Observer {
                when (it) {
                    true -> {
                        lifecycleScope.launch {
                            DataStoreRepository.getInstance(this@CustomerDashboard).setUserLogin(false)
                            startActivity(Intent(this@CustomerDashboard, SigIn_SignUp::class.java))
                            finish()
                        }

                    }
                    false -> {
                        Toast.makeText(this, "Failed In Logout", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(Notification_broadcast))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningOnForeground = false
    }

    private fun ratingDialog(customerId: String) {
        viewModel.getCompletedOrder(customerId).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val order: Order = it.data!![0]
                    showRatingBar(order)
                }

            }
        })
    }


    private fun showRatingBar(order: Order) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.cook_rating_dailog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.messageField.text =
            "How was your last ${order.dish.name} do you want to rate our chef ?"
        dialog.show()
        var rating: Float = 0.0f
        dialog.closeBtn.setOnClickListener {
            removeOrder(order.orderId)
            dialog.cancel()
        }
        dialog.rateBtn.setOnClickListener {
            rating = dialog.ratingBar.rating
            if (rating > 0) {
                viewModel.rateDish(order.dish.dishId, rating).observe(this, Observer {
                    when (it.status) {
                        Status.ERROR -> {
                            dialog.cancel()
                            Toast.makeText(this, "${it.msg}", Toast.LENGTH_SHORT).show()
                        }
                        Status.SUCCESS -> {
                            dialog.cancel()
                            removeOrder(order.orderId)
                        }
                    }

                })


            } else {
                Toast.makeText(this, "Please Select Rating", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun removeOrder(orderId: String) {
        viewModel.deleteOrder(orderId).observe(this, Observer {
            when (it.status) {

                Status.LOADING -> {
                    Reuse.startLoading(loading)
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(loading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "This Order Not Remove", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}