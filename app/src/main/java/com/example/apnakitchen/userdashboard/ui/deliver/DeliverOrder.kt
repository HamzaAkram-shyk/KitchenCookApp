package com.example.apnakitchen.userdashboard.ui.deliver

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.CUSTOMER
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.adapter.OrderAdapter
import kotlinx.android.synthetic.main.deliver_order_fragment.view.*

class DeliverOrder : Fragment() {
    private lateinit var viewModel: DeliverOrderViewModel
    private lateinit var mainView: View
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=inflater.inflate(R.layout.deliver_order_fragment, container, false)
        viewModel = ViewModelProvider(this).get(DeliverOrderViewModel::class.java)
        adapter = OrderAdapter(requireContext(), arrayListOf(), CUSTOMER)
        mainView.deliverRecyclerView.hasFixedSize()
        mainView.deliverRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.deliverRecyclerView.adapter = adapter
        fetchDeliverOrders()
        return mainView
    }

    private fun fetchDeliverOrders(){
        viewModel.deliverOrder?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.deliverLoading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(mainView.deliverLoading)
                    Toast.makeText(context, "Error = ${it.msg}", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(mainView.deliverLoading)
                    adapter.refreshList(it.data!!)
                }
            }

        })
    }

}