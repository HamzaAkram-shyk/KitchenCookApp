package com.example.apnakitchen.authentication.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.authentication.SigIn_SignUp
import com.example.apnakitchen.authentication.`interface`.FragmentReturn
import com.example.apnakitchen.authentication.viewmodel.AuthViewModel
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.userdashboard.CustomerDashboard
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.coroutines.launch


class SignUp(var instance: Context) : Fragment() {

    private lateinit var mainView: View
    private lateinit var authViewModel: AuthViewModel
    private lateinit var callback: FragmentReturn
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callback = instance as FragmentReturn
        mainView = inflater.inflate(R.layout.fragment_sign_up, container, false)
        authViewModel = activity?.let {
            ViewModelProvider(it).get(AuthViewModel::class.java)
        }!!
        returnParentActivity()


        mainView.signUpButton.setOnClickListener {
            startLoading()
            if (!validateFields()) {
                stopLoading()
            } else {
                signInUp()
            }

        }

        return mainView
    }

    private fun signInUp() {
        // COOK and CUSTOMER are the String Constants to specify UserType of our app
        when (mainView.radioGroup.checkedRadioButtonId) {
            R.id.cook_radioButton -> {
                authViewModel.signUp(
                    name_field.text.toString(),
                    email_field.text.toString(),
                    password_field.text.toString(),
                    COOK,
                    NULL

                )
                    .observe(viewLifecycleOwner, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                startLoading()
                            }
                            Status.SUCCESS -> {
                                stopLoading()
                                lifecycleScope.launch {
                                    val store =
                                        DataStoreRepository.getInstance(requireContext())
                                    store.setUserLogin(true)
                                    store.setUserType(it.data!!)
                                    store.setUserId(it.msg!!)
                                    store.setUserName(name_field.text?.trim().toString())
                                    startActivity(Intent(context, CookDashboard::class.java))
                                    requireActivity().finish()
                                }
                            }
                            Status.ERROR -> {
                                stopLoading()
                                Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
            R.id.user_radioButton -> {
                authViewModel.signUp(
                    name_field.text.toString(),
                    email_field.text.toString(),
                    password_field.text.toString(),
                    CUSTOMER,
                    NULL
                )
                    .observe(viewLifecycleOwner, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                startLoading()
                            }
                            Status.SUCCESS -> {
                                stopLoading()
                                lifecycleScope.launch {
                                    val store =
                                        DataStoreRepository.getInstance(requireContext())
                                    store.setUserLogin(true)
                                    store.setUserType(it.data!!)
                                    store.setUserId(it.msg!!)
                                    startActivity(Intent(context, CustomerDashboard::class.java))
                                    requireActivity().finish()

                                }
                            }
                            Status.ERROR -> {
                                stopLoading()
                                Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
            -1 -> {
                Toast.makeText(context, "Please Select Login Type", Toast.LENGTH_SHORT).show()
                stopLoading()
            }
        }

    }

    private fun validateFields(): Boolean {
        var isUnlock = true
        if (mainView.name_field.text?.trim().isNullOrEmpty()) {
            mainView.name_field.error = "Enter Name"
            isUnlock = false
        }
        if (mainView.email_field.text?.trim().isNullOrEmpty()) {
            mainView.email_field.error = "Enter Email"
            isUnlock = false
        }
        if (mainView.password_field.text?.trim()
                .isNullOrEmpty() || mainView.password_field.text?.length!! < 8
        ) {
            mainView.password_field.error = "Password length should be atleast 8 "
            isUnlock = false
        }

        return isUnlock
    }

    private fun startLoading() {
        Reuse.startLoading(mainView.progress_bar)
    }

    private fun stopLoading() {
        Reuse.stopLoading(mainView.progress_bar)
    }

    private fun returnParentActivity() {

        // This function return value to parent Activity for acknowledge which type of user is Login
        // We Need because our Gmail SignIn button is inside activity not in the fragment
        SigIn_SignUp.Parent.returnValue(
            callback,
            mainView.cook_radioButton,
            mainView.user_radioButton
        )

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "OnPause")
        mainView.radioGroup.clearCheck()
        SigIn_SignUp.Parent.userType = NULL
    }


}