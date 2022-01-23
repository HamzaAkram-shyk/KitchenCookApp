package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apnakitchen.R
import com.example.apnakitchen.adapter.cookAdapter.ChildFragmentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.special_extra_meal_fragment.view.*

class SpecialExtraMeal : BottomSheetDialogFragment() {

    private lateinit var viewModel: SpecialExtraMealViewModel
    private lateinit var mainView: View
    private lateinit var childFragmentAdapter: ChildFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.special_extra_meal_fragment, container, false)
        mainView.close_icon.setOnClickListener {
            dismiss()
        }

        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SpecialExtraMealViewModel::class.java)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentAdapter = ChildFragmentAdapter(childFragmentManager)

        mainView.pager.adapter = childFragmentAdapter

    }

}