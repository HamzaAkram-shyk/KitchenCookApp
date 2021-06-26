package com.example.apnakitchen.cookdashboard.ui.applySale

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishFragment
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.apply_sale_fragment.*
import kotlinx.android.synthetic.main.apply_sale_fragment.view.*
import kotlinx.android.synthetic.main.create_dish_fragment.view.*

class ApplySale(val listener: MyResponse<Int>) : BottomSheetDialogFragment(),
    AdapterView.OnItemSelectedListener {


    private lateinit var viewModel: ApplySaleViewModel
    private lateinit var mainView: View
    private var percentage: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.apply_sale_fragment, container, false)
        setSpinner()
        viewModel = ViewModelProvider(this).get(ApplySaleViewModel::class.java)

        mainView.applySale.setOnClickListener {


            if (percentage > -1) {
                listener.success(percentage, "0")
                dismiss()
            }

        }

        mainView.dismiss.setOnClickListener {
            listener.onError(0, "")
            dismiss()
        }




        return mainView
    }


    private fun setSpinner() {
        val spinner = mainView.saleSpinner
        val adapter =
            context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.SalePercentage,
                    android.R.layout.simple_spinner_item
                )
            }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()
        percentage = Integer.parseInt(text)
        mainView.saleLabel.text = "$percentage% Selected"


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(context, "Nothing..", Toast.LENGTH_SHORT).show()
    }


}