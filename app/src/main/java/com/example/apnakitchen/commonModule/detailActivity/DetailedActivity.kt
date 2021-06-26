package com.example.apnakitchen.commonModule.detailActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.cookdashboard.ui.applySale.ApplySale
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishFragment
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.pushNotify.*
import com.example.apnakitchen.userdashboard.CustomerDashboard
import com.example.apnakitchen.viewpagerAdapter.ImageSwipeAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity(), MyResponse<Int> {

    companion object {
        const val dataKey: String = "data"
        const val typeKey: String = "type"
    }

    private lateinit var selectedDish: Dish
    private lateinit var viewModel: DetailActivityViewModel
    private var isCheck = 1
    private var isCook: Boolean = true
    private lateinit var activeCook: Cook
    private lateinit var store: DataStoreRepository
    private lateinit var order: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_detailed)

        selectedDish = intent.getParcelableExtra<Dish>(dataKey)!!
        isCook = intent.let {
            it.getBooleanExtra(typeKey, true)
        }
        viewModel = ViewModelProvider(this).get(DetailActivityViewModel::class.java)
        store = DataStoreRepository.getInstance(this)
        if (!isCook) {
            sale_switch.visibility = View.INVISIBLE
            edit_Btn.visibility = View.GONE
            bookNowBtn.visibility = View.VISIBLE
        }
        val list = mutableListOf<String>()
        list.add(selectedDish.firstImageUrl)
        list.add(selectedDish.secondImageUrl)
        val adapter = ImageSwipeAdapter(list)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

        }.attach()

        setContent()

        sale_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val applySale = ApplySale(this)
                applySale.isCancelable = false
                applySale.show(supportFragmentManager, "Sale")
            } else if (isCheck == 1) {
                saleOff(-1, false)
            }

        }
        edit_Btn.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(CreateDishFragment.modeTypeKey, selectedDish)
            val sheet = CreateDishFragment()
            sheet.isCancelable = false
            sheet.arguments = bundle
            sheet.show(supportFragmentManager, "Edit Mode")
        }


        bookNowBtn.setOnClickListener {
            makeOrderRequest()
        }


    }

    private fun setContent() {
        title_tag.text = selectedDish.name
        price_tag.text = "${selectedDish.price} Pkr"
        food_type_tag.text = selectedDish.foodType
        cook_time_tag.text = "${selectedDish.CookTime} minutes"
        detail_tag.text = "Details: \n\n${selectedDish.detail}"
        when (selectedDish.sale) {
            true -> {
                sale_switch.text = "${selectedDish.salePercent}% off "
                sale_switch.isChecked = true
            }

            false -> {
                sale_switch.text = "Apply Sale "
                sale_switch.isChecked = false
            }

        }

    }

    override fun success(data: Int, msg: String) {
        saleOff(data, true)

    }

    override fun onError(data: Int, msg: String) {
        isCheck = data
        sale_switch.isChecked = false
        if (data != 0) {
            Toast.makeText(this, "$msg", Toast.LENGTH_SHORT).show()
        }


    }

    private fun saleOff(percentage: Int, status: Boolean) {
        viewModel.updateSale(percentage, status, selectedDish.dishId).observe(this, Observer {

            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(loading)


                }

                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Log.d(com.example.apnakitchen.Utils.TAG, "Error= ${it.msg}")
                    Toast.makeText(this, "Sale Not Off Successfully Try Again ", Toast.LENGTH_SHORT)
                        .show()
                }

                Status.SUCCESS -> {
                    Reuse.stopLoading(loading)
                    selectedDish.sale = it.data!!
                    selectedDish.salePercent = Integer.parseInt(it.msg!!)
                    setContent()
                }


            }
        })
    }

    private fun makeOrderRequest() {
        checkEligibility()
    }

//    private fun myNotify() {
//        viewModel.getUser(selectedDish.cookId).observe(this, Observer {
//            when (it.status) {
//                Status.LOADING -> {
//                    Reuse.startLoading(loading)
//                }
//                Status.ERROR -> {
//                    Reuse.stopLoading(loading)
//                    Toast.makeText(this, "${it.msg}", Toast.LENGTH_SHORT).show()
//                }
//                Status.SUCCESS -> {
//                    val user = it.data
//                    val notification = PushNotification(
//                        NotificationData(
//                            "Congrets", "Hy ${user?.name} you got new Order",
//                            CUSTOMER
//                        ), user?.token.toString()
//                    )
//                    lifecycleScope.launch {
//                        send(notification)
//                    }
//
//                }
//            }
//        })
//    }

    private fun checkEligibility() {
        viewModel.userIsEligible().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(loading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "Error: ${it.msg}", Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    checkCookActive()
                }
            }
        })
    }

    private fun checkCookActive() {
        viewModel.getActiveCook(selectedDish.cookId).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    activeCook = it.data!!
                    order = Order()
                    order.timeStamp = Reuse.getTimeStamp()
                    order.dish = selectedDish
                    order.cookId = selectedDish.cookId
                    order.payment = selectedDish.getPrice()
                    checkIsCookSigIn(selectedDish.cookId)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "Error: ${it.msg}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun checkIsCookSigIn(userId: String) {
        viewModel.getUser(userId).observe(this, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "${it.msg}", Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    if (it.data?.userType != COOK) {
                        order.buyerToken = it.data?.token.toString()
                        placeOrder(order)

                    } else {
                        order.cookToken = it.data?.token.toString()
                        lifecycleScope.launch {
                            val userId = store.getUserId()
                            order.buyerId = userId.toString()
                            order.orderId = Reuse.getUniqueId(userId.toString())
                            checkIsCookSigIn(userId!!)
                        }

                    }
                }
            }
        })
    }

    private fun placeOrder(order: Order) {
        viewModel.placeOrder(order).observe(this, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "Error: ${it.msg}", Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    var orderNo = activeCook.orderQueue
                    orderNo++
                    updateOrderQueue(selectedDish.cookId, orderNo)
                }

            }
        })
    }

    private fun updateOrderQueue(cookId: String, number: Int) {
        viewModel.updateOrderQueue(cookId, number).observe(this, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    Toast.makeText(this, "Error: ${it.msg}", Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    lifecycleScope.launch {
                        sendNotification(getNotification())
                    }

                }
            }
        })
    }


    private suspend fun sendNotification(notification: PushNotification) {
        Reuse.stopLoading(loading)
        try {
            val response = RetrofitInstance.api.sendNotification(notification)
            if (response.isSuccessful) {
                store.setOrderQueue(true)
                val intent = Intent(this, CustomerDashboard::class.java)
                intent.putExtra(CustomerDashboard.Key, 0)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "There is Something Wrong Please Try Again Later",
                    Toast.LENGTH_SHORT
                )
                    .show()
                //  Log.d(com.example.apnakitchen.Utils.TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Exception ${e.message.toString()}", Toast.LENGTH_SHORT).show()
            //  Log.d("Error", e.message.toString())

        }
    }


    private fun getNotification(): PushNotification {
        return PushNotification(
            NotificationData(
                "Congrats ${activeCook.user.name}",
                "Hy you got an order please make your delicious ${selectedDish.name} for our client",
                CUSTOMER
            ), order.cookToken
        )
    }

    private fun Dish.getPrice(): Int {
        return if (salePercent > -1) {
            val cutOff = (price / 100) * salePercent
            price - cutOff
        } else {
            price
        }
    }


}

