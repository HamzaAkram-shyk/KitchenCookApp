package com.example.apnakitchen.cookdashboard

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController

import com.example.apnakitchen.Utils.Notification_broadcast
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.pushNotify.FirebaseService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.views.MyBaseClass
import kotlinx.android.synthetic.main.activity_cook_dashboard.*

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import com.example.apnakitchen.R
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishFragment
import com.example.apnakitchen.cookdashboard.ui.extrameal_module.SpecialExtraMeal
import kotlinx.android.synthetic.main.custom_dialog.view.*


class CookDashboard : MyBaseClass() {

    companion object {
        var isRunningOnForeground: Boolean = false
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Notification_broadcast -> {
                    val title = intent.getStringExtra(FirebaseService._titleKey)!!
                    val message = intent.getStringExtra(FirebaseService._messageKey)!!
                    Reuse.customDialog(this@CookDashboard, R.drawable.chef_icon, title, message) {
                        if (it) {
                            navController.navigate(R.id.navigation_order)
                        }

                    }
                }
            }
        }

    }

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_cook_dashboard)
        addCommonViews(mainLayout, this)

//        tootlbar.title = "Cook Dashboard"
//        setSupportActionBar(tootlbar)
        bottomNavigation = findViewById(R.id.customer_bottomBar)
        bottomNavigation.menu.getItem(2).isEnabled = false
        bottomNavigation.background = null
        navController = findNavController(R.id.cook_fragment)
        bottomNavigation.setupWithNavController(navController)
        isRunningOnForeground = true

        addDishBtn.setOnClickListener {


            showKitchenPlans()
        }


    }

    fun setVisibilityBottomAppBar(visibility: Int) {
        addDishBtn.visibility = visibility
        bottomAppBar.visibility = visibility
        setMargin(visibility)
    }

    private fun setMargin(visibility: Int) {
        val params = mainLayout.layoutParams as CoordinatorLayout.LayoutParams
        params.bottomMargin =
            if (visibility == View.VISIBLE) 70.toDp(applicationContext) else 0.toDp(
                applicationContext
            )
    }

    private fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(Notification_broadcast))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }


    override fun onDestroy() {
        super.onDestroy()
        isRunningOnForeground = false
        Log.d(com.example.apnakitchen.Utils.TAG, "Foreground $isRunningOnForeground")
    }


    private fun showKitchenPlans() {
        var builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.custom_dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.addDish.setOnClickListener {
            dialog.dismiss()
            val sheet = CreateDishFragment()
            sheet.isCancelable = false
            sheet.show(supportFragmentManager, "Sheet")

        }

        view.trySpecial.setOnClickListener {
            dialog.dismiss()
            val houseWifePlan = SpecialExtraMeal()
            houseWifePlan.isCancelable = false
            houseWifePlan.show(supportFragmentManager, "Plan")
        }

//        val dialog = Dialog(this, android.R.style.Theme_Dialog)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.custom_dialog)
//        dialog.setCanceledOnTouchOutside(true)

//        val btnReopenId: Button = dialog.findViewById(R.id.btnReopenId) as Button
//        val btnCancelId: Button = dialog.findViewById(R.id.btnCancelId) as Button
//
//        btnReopenId.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {}
//        })
//
//
//        btnCancelId.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {}
//        })

    }


}