package com.example.apnakitchen.userdashboard.ui.profile

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
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.cookAdapter.ExtraMealAdapter
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import com.example.apnakitchen.repository.authRepository.AuthRepository
import kotlinx.android.synthetic.main.customer_profile.view.*

class UserExtraMealFragment : Fragment(), MyResponse<ExtraMeal> {

    private lateinit var viewModel: UserExtraMealViewModel
    private lateinit var mainView: View
    private lateinit var mealAdapter: ExtraMealAdapter
    private lateinit var extraMealOrder: ExtraMealOrder
    private var buyerId: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserExtraMealViewModel::class.java)
        mainView = inflater.inflate(R.layout.customer_profile, container, false)
        mealAdapter = ExtraMealAdapter(arrayListOf(), requireContext(), this, false)
        mainView.extraMealList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }
        buyerId = AuthRepository.getCurrentUser().uid
        getMeals()
        return mainView
    }

    override fun success(data: ExtraMeal, msg: String) {
        extraMealOrder=ExtraMealOrder()
        extraMealOrder.meal = data
        extraMealOrder.payment = data.price
        extraMealOrder.cookId = data.cookId
        extraMealOrder.buyerId = buyerId
        extraMealOrder.orderId = Reuse.getUniqueId(buyerId)
        viewModel.placeOrder(extraMealOrder).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Reuse.stopLoading(mainView.loadingView)
                    Toast.makeText(
                        requireContext(),
                        "Place Order Successfully....",
                        Toast.LENGTH_SHORT
                    ).show()
                    activity?.onBackPressed()
                }

                Status.ERROR -> {
                    Reuse.stopLoading(mainView.loadingView)
                    Toast.makeText(requireContext(), "${it.msg}", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    Reuse.startLoading(mainView.loadingView)
                }
            }
        })

    }

    override fun onError(data: ExtraMeal, msg: String) {
        TODO("Not yet implemented")
    }

    private fun getMeals() {
        viewModel.getValidMeals().observe(viewLifecycleOwner, Observer {
            when (it.status) {

                Status.LOADING -> {
                    Reuse.startLoading(mainView.loadingView)
                }
                Status.SUCCESS -> {
                    mealAdapter.addList(it.data!! as ArrayList<ExtraMeal>)
                    Toast.makeText(context, "${it.data.size}", Toast.LENGTH_SHORT).show()
                    Reuse.stopLoading(mainView.loadingView)
                }
                Status.ERROR -> {
                    Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                    Reuse.stopLoading(mainView.loadingView)
                }
            }
        })

    }


}

//        mainView.logout.setOnClickListener {
//            AuthRepository.signOut().observe(viewLifecycleOwner, Observer {
//                when (it) {
//                    true -> {
//                        lifecycleScope.launch {
//                            DataStoreRepository.getInstance(requireContext()).setUserLogin(false)
//                            startActivity(Intent(requireContext(), SigIn_SignUp::class.java))
//                            activity?.finish()
//                        }
//
//                    }
//                    false -> {
//                        Toast.makeText(context, "Failed In Logout", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            })
//        }