package com.example.apnakitchen.cookdashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View


abstract class BaseFragment : Fragment() {


    protected open var bottomBarViewVisibility = View.VISIBLE


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // get the reference of the parent activity and call the setBottomNavigationVisibility method.
        if (activity is CookDashboard) {
            var  activity = activity as CookDashboard
            activity.setVisibilityBottomAppBar(bottomBarViewVisibility)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is CookDashboard) {
            var activity= activity as CookDashboard
            activity.setVisibilityBottomAppBar(bottomBarViewVisibility)
        }
    }


    abstract var navigationVisibility: Int
}