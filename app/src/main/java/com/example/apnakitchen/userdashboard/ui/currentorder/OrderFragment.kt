package com.example.apnakitchen.userdashboard.ui.currentorder

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.CUSTOMER
import com.example.apnakitchen.Utils.DataStoreRepository
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.adapter.OrderAdapter
import com.example.apnakitchen.model.Order
import kotlinx.android.synthetic.main.customer_current_order.*
import kotlinx.android.synthetic.main.customer_current_order.view.*
import kotlinx.coroutines.launch

// Customer Order
class OrderFragment : Fragment() {

    private lateinit var viewModel: CustomerOrderViewModel
    private lateinit var mainView: View
    private lateinit var adapter: OrderAdapter
    private lateinit var store: DataStoreRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CustomerOrderViewModel::class.java)
        mainView = inflater.inflate(R.layout.customer_current_order, container, false)
        adapter = OrderAdapter(requireContext(), arrayListOf(), CUSTOMER)
        mainView.orderRecyclerView.hasFixedSize()
        mainView.orderRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.orderRecyclerView.adapter = adapter
        store = DataStoreRepository.getInstance(requireContext())
        lifecycleScope.launch {
            val userId = store.getUserId()!!
            getOrders(userId)
        }

        return mainView
    }


    private fun getOrders(userId: String) {
        viewModel.getPendingOrders(userId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.loading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(mainView.loading)
                    empty_label.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(mainView.loading)
                    empty_label.visibility = View.GONE
                    adapter.refreshList(it.data!!)
                }
            }
        })
    }

}