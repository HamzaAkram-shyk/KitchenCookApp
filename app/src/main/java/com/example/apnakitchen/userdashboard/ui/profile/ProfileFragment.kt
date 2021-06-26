package com.example.apnakitchen.userdashboard.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.DataStoreRepository
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.authentication.SigIn_SignUp
import com.example.apnakitchen.repository.authRepository.AuthRepository
import kotlinx.android.synthetic.main.cook_profile.*
import kotlinx.android.synthetic.main.customer_profile.view.*
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var mainView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        mainView = inflater.inflate(R.layout.customer_profile, container, false)

        mainView.logout.setOnClickListener {
            AuthRepository.signOut().observe(viewLifecycleOwner, Observer {
                when (it) {
                    true -> {
                        lifecycleScope.launch {
                            DataStoreRepository.getInstance(requireContext()).setUserLogin(false)
                            startActivity(Intent(requireContext(), SigIn_SignUp::class.java))
                            activity?.finish()
                        }

                    }
                    false -> {
                        Toast.makeText(context, "Failed In Logout", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }

        return mainView
    }


}