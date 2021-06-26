package com.example.apnakitchen.authentication

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.apnakitchen.authentication.fragment.SignIn
import com.example.apnakitchen.authentication.fragment.SignUp

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,var context: Context) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
      return  when (position) {
            0 -> {
               SignIn(context)
            }
            1 -> {
              SignUp(context)
            }

          else -> {
              SignIn(context)
          }
      }
    }


}