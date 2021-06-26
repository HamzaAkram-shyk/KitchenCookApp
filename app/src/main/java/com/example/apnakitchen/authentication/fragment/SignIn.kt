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
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.example.apnakitchen.userdashboard.CustomerDashboard
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.coroutines.launch


class SignIn(var instance: Context) : Fragment() {


    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainView: View
    private lateinit var callback: FragmentReturn


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_sign_in, container, false)
        callback = instance as FragmentReturn

        authViewModel = activity?.let {
            ViewModelProvider(it).get(AuthViewModel::class.java)
        }!!
        mainView.signInButton.setOnClickListener {
            sigIn()
        }
        returnParentActivity()
        return mainView

    }

    private fun sigIn() {
        val validate = validateFields()
        when (mainView.sRadioGroup.checkedRadioButtonId) {
            R.id.sUser_radioButton -> {
                if (validate) {
                    authViewModel.signIn(
                        mainView.sEmail_field.text.toString(),
                        mainView.sPassword_field.text.toString(),
                        CUSTOMER
                    ).observe(viewLifecycleOwner, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                Reuse.startLoading(mainView.loading)
                            }
                            Status.SUCCESS -> {
                                Reuse.stopLoading(mainView.loading)
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
                                Reuse.stopLoading(mainView.loading)
                                Toast.makeText(context, " ${it.msg}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
            R.id.sCook_radioButton -> {
                if (validate) {
                    authViewModel.signIn(
                        mainView.sEmail_field.text.toString(),
                        mainView.sPassword_field.text.toString(),
                        COOK
                    ).observe(viewLifecycleOwner, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                Reuse.startLoading(mainView.loading)
                            }
                            Status.SUCCESS -> {
                                Reuse.stopLoading(mainView.loading)
                                lifecycleScope.launch {
                                    val store =
                                        DataStoreRepository.getInstance(requireContext())
                                    store.setUserLogin(true)
                                    store.setUserType(it.data!!)
                                    store.setUserId(it.msg!!)
                                    startActivity(Intent(context, CookDashboard::class.java))
                                    requireActivity().finish()
                                }

                            }
                            Status.ERROR -> {
                                Reuse.stopLoading(mainView.loading)
                                Toast.makeText(context, " ${it.msg}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
            -1 -> {
                Toast.makeText(context, "Select User Type ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun returnParentActivity() {

        // This function return value to parent Activity for acknowledge which type of user is Login
        // We Need because our Gmail SignIn button is inside activity not in the fragment
        SigIn_SignUp.Parent.returnValue(
            callback,
            mainView.sCook_radioButton,
            mainView.sUser_radioButton
        )

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "OnPause")
        mainView.sRadioGroup.clearCheck()
        SigIn_SignUp.Parent.userType = NULL
    }

    private fun validateFields(): Boolean {
        var isUnlock = true
        if (mainView.sEmail_field.text?.trim().isNullOrEmpty()) {
            mainView.sEmail_field.error = "Enter Email"
            isUnlock = false
        }
        if (mainView.sPassword_field.text?.trim()
                .isNullOrEmpty() || mainView.sPassword_field.text?.length!! < 8
        ) {
            mainView.sPassword_field.error = "Password length should be atleast 8 "
            isUnlock = false
        }

        return isUnlock
    }

}