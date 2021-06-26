package com.example.apnakitchen.cookdashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
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
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Notification_broadcast
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.cookdashboard.ui.createDish.CreateDishFragment
import com.example.apnakitchen.pushNotify.FirebaseService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_cook_dashboard.*

class CookDashboard : AppCompatActivity() {

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
        );
        setContentView(R.layout.activity_cook_dashboard)

//        tootlbar.title = "Cook Dashboard"
//        setSupportActionBar(tootlbar)
        bottomNavigation = findViewById(R.id.customer_bottomBar)
        bottomNavigation.menu.getItem(2).isEnabled = false
        bottomNavigation.background = null
        navController = findNavController(R.id.cook_fragment)
        bottomNavigation.setupWithNavController(navController)
        isRunningOnForeground = true

        addDishBtn.setOnClickListener {
            val sheet = CreateDishFragment()
            sheet.isCancelable = false
            sheet.show(supportFragmentManager, "Sheet")
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


}