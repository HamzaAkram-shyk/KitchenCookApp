package com.example.apnakitchen.userdashboard.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.Utils.TAG
import com.example.apnakitchen.adapter.customerAdapter.OuterViewAdapter
import com.example.apnakitchen.model.customerModel.ListModel
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var mainView: View

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: OuterViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.home_fragment, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        mainView.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mainView.mainRecyclerView.hasFixedSize()
        adapter = OuterViewAdapter(requireContext(), mutableListOf<ListModel>())
        fetchItems()
        return mainView
    }


    private fun getItem(value: String,index:Int) {
        viewModel.getItem(value).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(mainView.loading)
                }
                Status.SUCCESS -> {
                    if(index>=4){
                        Reuse.stopLoading(mainView.loading)
                    }
                    mainView.mainRecyclerView.adapter = adapter
                    adapter.refreshList(it.data!!)
                }

            }
        })


    }

    private fun fetchItems() {
        Reuse.startLoading(mainView.loading)
        val array = resources.getStringArray(R.array.FoodCategories).toMutableList()
        for ((index, value) in array.withIndex()) {
            Log.d(TAG, value)
            getItem(value,index)
        }
    }


}






