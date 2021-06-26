package com.example.apnakitchen.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.apnakitchen.R
import com.example.apnakitchen.pushNotify.NotifyResponse
import com.example.apnakitchen.pushNotify.PushNotification
import com.example.apnakitchen.pushNotify.RetrofitInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object Reuse {
    private val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    fun startLoading(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
    }

    fun stopLoading(bar: ProgressBar) {
        bar.visibility = View.GONE
    }

    fun getUniqueId(value: String): String {
        val time = System.currentTimeMillis()
        return "$time$value"
    }

    fun getTimeStamp(): Long {
        return (System.currentTimeMillis()) / 1000
    }

    fun showAlertDialog(context: Context, message: String) {
        MaterialAlertDialogBuilder(context).setTitle("Alert !")
            .setMessage(
                "$message"
            )
            .setIcon(R.drawable.alert_icon)
            .setCancelable(false)
            .setPositiveButton("Close") { dialog, it ->

                dialog.dismiss()
            }
            .show()

    }

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(Notification_Channel_Id, Channel_Name, importance).apply {
                    description = Channel_desc
                    setSound(soundUri, null)
                    enableVibration(true)
                    enableLights(true)

                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Channel Created Successfully")
        }
    }


    suspend fun send(notification: PushNotification, callback: NotifyResponse) {
        try {
            val response = RetrofitInstance.api.sendNotification(notification)
            if (response.isSuccessful) {
                callback.onDeliver("Your Order Apply Successfully")
            } else {
                callback.onError(
                    "There is bad Connection ${
                        response.errorBody().toString()
                    } Try Again"
                )

            }
        } catch (e: Exception) {
            callback.onError("${e.message.toString()}")

        }
    }

    fun confirmDialog(
        context: Context,
        title: String,
        message: String,
        myFunction: (Boolean) -> Unit
    ) {
        MaterialAlertDialogBuilder(context).setTitle(title)
            .setMessage(
                "$message"
            )
            .setIcon(R.drawable.alert_icon)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, it ->
                myFunction(true)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, it ->
                myFunction(false)
                dialog.dismiss()
            }
            .show()

    }

    fun customDialog(
        context: Context,
        icon: Int,
        title: String,
        message: String,
        lamdaFun: (Boolean) -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(
                "$message"
            )
            .setIcon(icon)
            .setCancelable(false)
            .setPositiveButton("View") { dialog, it ->
                lamdaFun(true)
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, it ->
                lamdaFun(false)
                dialog.dismiss()
            }
            .show()

    }


}