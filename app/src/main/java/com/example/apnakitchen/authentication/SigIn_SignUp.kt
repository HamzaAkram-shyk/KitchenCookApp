package com.example.apnakitchen.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.authentication.`interface`.FragmentReturn
import com.example.apnakitchen.authentication.viewmodel.AuthViewModel
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.databinding.ActivitySigInSignUpBinding
import com.example.apnakitchen.userdashboard.CustomerDashboard

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.coroutines.launch

class SigIn_SignUp : AppCompatActivity(), FragmentReturn {
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivitySigInSignUpBinding
    private val RC_SIGN_IN: Int = 1
    private lateinit var auth: FirebaseAuth
    private var userType: String = NULL
    private lateinit var authViewModel: AuthViewModel
// ...
// Initialize Firebase Auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigInSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        lifecycleScope.launch {
            val isLogin = DataStoreRepository.getInstance(this@SigIn_SignUp).isUserLogin()
            if (isLogin == true) {
                when (DataStoreRepository.getInstance(this@SigIn_SignUp).getUserType()) {
                    COOK -> {
                        startActivity(Intent(this@SigIn_SignUp, CookDashboard::class.java))
                        finish()
                    }
                    CUSTOMER -> {
                        startActivity(Intent(this@SigIn_SignUp, CustomerDashboard::class.java))
                        finish()
                    }

                }

            }
        }

        integrateNotificationChannel()

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, this)
        binding.viewPager.adapter = adapter
        binding.tabs.tabGravity = TabLayout.GRAVITY_FILL
        binding.tabs.addTab(binding.tabs.newTab().setText("SignIn"))
        binding.tabs.addTab(binding.tabs.newTab().setText("SignUp"))

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "SigIn"
                }
                1 -> {
                    tab.text = "SigUp"
                }
            }

        }.attach()
        auth = FirebaseAuth.getInstance()
        configureSignIn()
        authViewModel = let {
            ViewModelProvider(it).get(AuthViewModel::class.java)
        }!!


        binding.gmailLoginButton.setOnClickListener {
            if (Parent.userType != NULL) {
                userType = Parent.userType
                signIn()
            } else {
                Toast.makeText(this, "Select Your Login Type ", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun configureSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        googleSignInClient?.signOut()
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                // Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(com.example.apnakitchen.Utils.TAG, e.message.toString())
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    authViewModel.googleSigIn(
                        user.uid,
                        user.displayName,
                        user.email,
                        user.photoUrl.toString(),
                        userType
                    ).observe(
                        this,
                        Observer {
                            when (it.status) {
                                Status.LOADING -> {
                                    // Start Loading
                                    Reuse.startLoading(binding.loading)
                                }
                                Status.SUCCESS -> {
                                    Reuse.stopLoading(binding.loading)
                                    lifecycleScope.launch {
                                        val store =
                                            DataStoreRepository.getInstance(this@SigIn_SignUp)
                                        store.setUserLogin(true)
                                        store.setUserType(it.data!!)
                                        store.setUserId(it.msg!!)
                                        store.setUserName(user.displayName)
                                        store.setUserImage(user.photoUrl.toString())
                                        if (it.data == COOK) {
                                            startActivity(
                                                Intent(
                                                    this@SigIn_SignUp,
                                                    CookDashboard::class.java
                                                )
                                            )

                                        } else {
                                            startActivity(
                                                Intent(
                                                    this@SigIn_SignUp,
                                                    CustomerDashboard::class.java
                                                )
                                            )
                                        }

                                        finish()

                                    }

                                }
                                Status.ERROR -> {
                                    Reuse.stopLoading(binding.loading)
                                    Toast.makeText(
                                        this,
                                        "Login Failed ${it.msg}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        })


                    //   updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // updateUI(null)
                    Log.d(com.example.apnakitchen.Utils.TAG, task.exception.toString())
                    Toast.makeText(this, "Error ${task.exception.toString()}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun userType(value: String) {
        Parent.userType = value

        Toast.makeText(this, "${Parent.userType}", Toast.LENGTH_SHORT).show()
    }

    object Parent {
        var userType: String = NULL

        fun returnValue(callback: FragmentReturn, cook: RadioButton, user: RadioButton) {
            cook.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    callback.userType(COOK)
                }


            }
            user.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    callback.userType(CUSTOMER)
                }

            }
        }
    }

    private fun integrateNotificationChannel() {
        // This method create notification channel first time when user install the app
        lifecycleScope.launch {
            val store = DataStoreRepository.getInstance(this@SigIn_SignUp)
            if (store.getFirstVisit() == true) {
                Reuse.createNotificationChannel(this@SigIn_SignUp)
                store.setFirstVisit(false)
                Log.d(TAG, "Channel Connected Success")
            }
        }

    }


}