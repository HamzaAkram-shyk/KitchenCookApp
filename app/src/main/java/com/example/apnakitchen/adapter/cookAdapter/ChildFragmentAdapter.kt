package com.example.apnakitchen.adapter.cookAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.apnakitchen.cookdashboard.ui.extrameal_module.AddExtraMeal
import com.example.apnakitchen.cookdashboard.ui.extrameal_module.ExtraMealList
import com.example.apnakitchen.cookdashboard.ui.extrameal_module.ExtraMealOrderFragment

class ChildFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 3

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> {
                AddExtraMeal.newInstance()
            }
            1 -> {
                ExtraMealList.newInstance()
            }
            2 -> {
                ExtraMealOrderFragment.newInstance()
            }
            else -> {
                throw  IllegalAccessError("")
            }
        }

    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                "Schedule Your Meal"
            }
            1 -> {
                "View Your Plans"
            }
            2 -> {
                "View Orders"
            }
            else -> {
                throw  IllegalAccessError("")
            }
        }


    }
}