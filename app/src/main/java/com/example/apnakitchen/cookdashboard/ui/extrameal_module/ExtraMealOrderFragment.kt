package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.adapter.cookAdapter.ExtraMealOrderAdapter
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import kotlinx.android.synthetic.main.extra_meal_order_fragment.view.*

class ExtraMealOrderFragment : Fragment() {

    companion object {
        fun newInstance() = ExtraMealOrderFragment()
    }

    private lateinit var viewModel: ExtraMealOrderViewModel
    private lateinit var mainView: View
    private lateinit var mealAdapter: ExtraMealOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.extra_meal_order_fragment, container, false)
        mealAdapter = ExtraMealOrderAdapter(arrayListOf(), requireContext())
        mainView.mealOrderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExtraMealOrderViewModel::class.java)
        viewModel.getOrders().observe(viewLifecycleOwner, {
            when (it.status) {

                Status.SUCCESS -> {
                    mealAdapter.addList(it.data!! as ArrayList<ExtraMealOrder>)
                }
                Status.LOADING -> {

                }
                Status.ERROR->{
                    Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}