package com.example.apnakitchen.cookdashboard.ui.sale

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.cookAdapter.DishAdapter
import com.example.apnakitchen.commonModule.detailActivity.DetailedActivity
import com.example.apnakitchen.cookdashboard.BaseFragment
import com.example.apnakitchen.model.cookModel.Dish
import kotlinx.android.synthetic.main.fragment_dish.view.*
import kotlinx.android.synthetic.main.fragment_dish.view.dishRecyclerView
import kotlinx.android.synthetic.main.fragment_sale.view.*


class SaleFragment : Fragment(), MyResponse<Dish> {

    private lateinit var saleViewModel: SaleViewModel
    private lateinit var mainView: View
    private lateinit var adapter: DishAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saleViewModel =
            ViewModelProvider(this).get(SaleViewModel::class.java)
        mainView = inflater.inflate(R.layout.fragment_sale, container, false)
        adapter = DishAdapter(arrayListOf<Dish>(), requireContext(), this)
        mainView.saleRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.saleRecyclerView.hasFixedSize()
        loadDishes()

        return mainView
    }


    private fun loadDishes() {
        saleViewModel.getSaleList().observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.loadingSale)
                }

                Status.ERROR -> {
                    Toast.makeText(context, "Error = ${it.msg}", Toast.LENGTH_SHORT).show()
                    Reuse.stopLoading(mainView.loadingSale)
                }

                Status.SUCCESS -> {
                    val list = it.data
                    mainView.saleRecyclerView.adapter = adapter
                    adapter.addList(list as ArrayList<Dish>)
                    Reuse.stopLoading(mainView.loadingSale)

                }

            }
        })
    }

    override fun success(data: Dish, msg: String) {
        activity?.let {
            val intent = Intent(requireContext(), DetailedActivity::class.java)
            intent.putExtra(DetailedActivity.dataKey, data)
            intent.putExtra(DetailedActivity.typeKey,true)
            startActivity(intent)
        }
    }

    override fun onError(data: Dish, msg: String) {

    }
}