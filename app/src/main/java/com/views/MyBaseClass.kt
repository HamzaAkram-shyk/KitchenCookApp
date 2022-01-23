package com.views

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

abstract class MyBaseClass : AppCompatActivity(), Type2ListenerCallback {
    private var rootCl: ConstraintLayout? = null
    private lateinit var context: Activity
    var blurView: BlurView? = null
    private var alertBox: MyAlertBox? = null

    fun addCommonViews(
        rootRl: ConstraintLayout?,
        context: Activity,
        blurViewColor: Int = Color.BLACK
    ) {
//        firebaseAuth = Firebase.auth
        this.rootCl = rootRl
        this.context = context
        addBlurViewBlack()
        addAlertBox()
    }


    private fun addBlurViewBlack() {
        blurView = BlurView(this)
        addViewMatchParent(blurView!!)
    }

    private fun addAlertBox() {
        alertBox = MyAlertBox(this, blurView!!)
        alertBox?.listener=this
        rootCl?.addView(alertBox)
    }

    private fun addViewMatchParent(view: View) {
        view.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        rootCl!!.addView(view)
        view.visibility = View.GONE
    }


     fun showDialog(){
        alertBox?.showDialog()
    }

    fun dismissDialog(){
        alertBox?.hideDialog()
    }

    override fun button1ClickListener() {

    }

    override fun button2ClickListener() {

    }




}