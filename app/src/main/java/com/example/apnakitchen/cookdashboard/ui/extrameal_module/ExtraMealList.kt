package com.example.apnakitchen.cookdashboard.ui.extrameal_module

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
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.cookAdapter.ExtraMealAdapter
import com.example.apnakitchen.model.cookModel.ExtraMeal
import kotlinx.android.synthetic.main.extra_meal_list_fragment.view.*

class ExtraMealList : Fragment(), MyResponse<ExtraMeal> {

    companion object {
        fun newInstance() = ExtraMealList()
    }

    private lateinit var viewModel: ExtraMealListViewModel
    private lateinit var mainView: View
    private lateinit var mealAdapter: ExtraMealAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.extra_meal_list_fragment, container, false)
        mealAdapter = ExtraMealAdapter(arrayListOf(), requireContext(), this,)
        mainView.mealListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExtraMealListViewModel::class.java)
        getMeals()
    }


    private fun getMeals() {
        viewModel.getMeals().observe(viewLifecycleOwner, Observer {
            when (it.status) {

                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    mealAdapter.addList(it.data!! as ArrayList<ExtraMeal>)
                }
                Status.ERROR -> {
                    Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun success(data: ExtraMeal, msg: String) {
        Toast.makeText(requireContext(), "${msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onError(data: ExtraMeal, msg: String) {
        Toast.makeText(requireContext(), "${msg}", Toast.LENGTH_SHORT).show()
    }

}