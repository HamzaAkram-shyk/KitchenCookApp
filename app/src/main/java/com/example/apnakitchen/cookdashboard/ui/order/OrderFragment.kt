package com.example.apnakitchen.cookdashboard.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.OrderAdapter
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.pushNotify.NotificationData
import com.example.apnakitchen.pushNotify.PushNotification
import com.example.apnakitchen.pushNotify.RetrofitInstance
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.android.synthetic.main.customer_current_order.view.loading
import kotlinx.android.synthetic.main.customer_current_order.view.orderRecyclerView
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.loading
import kotlinx.android.synthetic.main.fragment_order.view.*
import kotlinx.coroutines.launch

// Cook Order
class OrderFragment : Fragment(), MyResponse<Order> {

    private lateinit var viewModel: CookOrderViewModel
    var navigationVisibility: Int = View.VISIBLE
    private lateinit var mainView: View
    private lateinit var adapter: OrderAdapter
    private lateinit var store: DataStoreRepository
    private lateinit var deliverOrder: Order
    private lateinit var cookId: String
    private var removeItemIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(CookOrderViewModel::class.java)

        mainView = inflater.inflate(R.layout.fragment_order, container, false)
        adapter = OrderAdapter(requireContext(), arrayListOf(), COOK)
        adapter.setListener(this)
        mainView.orderRecyclerView.hasFixedSize()
        mainView.orderRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.orderRecyclerView.adapter = adapter
        store = DataStoreRepository.getInstance(requireContext())
        lifecycleScope.launch {
            cookId = store.getUserId()!!
            getOrders(cookId)

        }



        return mainView
    }

    override fun onResume() {
        super.onResume()

        if (activity is CookDashboard) {
            var activity = activity as CookDashboard
            activity.setVisibilityBottomAppBar(navigationVisibility)
        }
    }


    private fun getOrders(userId: String) {
        viewModel.getCurrentOrders(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.loading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(mainView.loading)
                    mainView.orderRecyclerView.visibility = View.GONE
                    mainView.emptyOrderView.visibility = View.VISIBLE

                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(mainView.loading)
                    mainView.emptyOrderView.visibility = View.GONE
                    mainView.orderRecyclerView.visibility = View.VISIBLE
                    adapter.refreshList(it.data!!)
                }
            }
        })
    }

    override fun success(data: Order, msg: String) {
        removeItemIndex = Integer.parseInt(msg)
        deliverOrder = data!!
        viewModel.getOrderQueue(deliverOrder.cookId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(loading)
                    mainView.orderRecyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    mainView.orderRecyclerView.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    var currentValue = it.data!!
                    currentValue--
                    deQueue(currentValue)
                }
            }
        })
    }

    override fun onError(data: Order, msg: String) {

    }

    private fun deQueue(queueValue: Int) {
        viewModel.deQueueOrder(deliverOrder.cookId, queueValue)
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.ERROR -> {
                        Reuse.stopLoading(loading)
                        mainView.orderRecyclerView.visibility = View.VISIBLE
                        Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        deliverOrder()
                    }
                }
            })
    }

    private fun deliverOrder() {
        viewModel.deliverOrder(deliverOrder.orderId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    mainView.orderRecyclerView.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        "Your Order Cannat Deliver Due to some Issues Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Status.SUCCESS -> {
                    val notification = PushNotification(
                        NotificationData(
                            "Your Order is Completed!!",
                            "No more Hunger your ${deliverOrder.dish.name} is deliver by your Chef you will get it within 15 to 20 minutes",
                            COOK
                        ), deliverOrder.buyerToken
                    )
                    lifecycleScope.launch {
                        sendNotification(notification)
                    }
                    // Say Notify
                }
            }
        })
    }


    private suspend fun sendNotification(notification: PushNotification) {
        Reuse.stopLoading(loading)
        try {
            val response = RetrofitInstance.api.sendNotification(notification)
            if (response.isSuccessful) {
                viewModel.addIntoRating(deliverOrder).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            getOrders(cookId)
                        }
                    }
                })

            } else {
                Toast.makeText(context, "There is Server Issue", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

            Toast.makeText(context, "Error ${e.message.toString()}", Toast.LENGTH_SHORT).show()

        }
    }


}