package com.example.apnakitchen.cookdashboard.ui.dishes

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.apnakitchen.Utils.TAG
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.cookAdapter.DishAdapter
import com.example.apnakitchen.commonModule.detailActivity.DetailedActivity
import com.example.apnakitchen.model.cookModel.Dish
import kotlinx.android.synthetic.main.fragment_dish.view.*


class DishesFragment : Fragment(), MyResponse<Dish> {

    private lateinit var dishesViewModel: DishesViewModel
    private lateinit var mainView: View
    private lateinit var adapter: DishAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dishesViewModel =
            ViewModelProvider(this).get(DishesViewModel::class.java)
        mainView = inflater.inflate(R.layout.fragment_dish, container, false)
        adapter = DishAdapter(arrayListOf<Dish>(), requireContext(), this)
        mainView.dishRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.dishRecyclerView.hasFixedSize()



        return mainView
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

    private fun loadDishes() {
        dishesViewModel.getDishes().observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.loading)
                }

                Status.ERROR -> {
                    Toast.makeText(context, "Error = ${it.msg}", Toast.LENGTH_SHORT).show()
                    Reuse.stopLoading(mainView.loading)
                }

                Status.SUCCESS -> {
                    val list = it.data
                    mainView.dishRecyclerView.adapter = adapter
                    adapter.addList(list as ArrayList<Dish>)
                    Reuse.stopLoading(mainView.loading)

                }

            }
        })
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause of Dishes")
    }

    override fun onResume() {
        super.onResume()
        loadDishes()

    }


}