package com.example.apnakitchen.pushNotify

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.userdashboard.CustomerDashboard
import com.example.chatapp.ChatPortal
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.math.log
import kotlin.random.Random

// This Service Push Notification When the application is running in the background if this
// is on Foreground then simply send custom broadcast which listen inside the activity and it show alert
class FirebaseService : FirebaseMessagingService() {
    private lateinit var intent: Intent

    companion object {
        const val _titleKey = "title"
        const val _messageKey = "message"
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val tittle = p0.data["title"]!!
        val message = p0.data["message"]!!
        val senderType = p0.data["senderType"]!!
        Log.d(TAG, p0.data["notificationType"]!!)
        when (p0.data["notificationType"]!!) {
            Default -> {
                if (senderType == CUSTOMER) {
                    when (CookDashboard.isRunningOnForeground) {
                        true -> {
                            sendBroadCast(tittle, message)
                            Log.d(TAG, "Foreground ${CookDashboard.isRunningOnForeground}")
                        }
                        false -> {
                            intent = Intent(this, CookDashboard::class.java)
                            Log.d(TAG, p0.data["senderType"]!!)
                            showNotification(intent, tittle, message)
                            Log.d(TAG, "Foreground ${CookDashboard.isRunningOnForeground}")
                        }
                    }

                } else {
                    when (CustomerDashboard.isRunningOnForeground) {
                        true -> {
                            sendBroadCast(tittle, message)
                            Log.d(TAG, "Foreground ${CookDashboard.isRunningOnForeground}")
                        }
                        false -> {
                            intent = Intent(this, CustomerDashboard::class.java)
                            intent.putExtra(CustomerDashboard.Key, 1)
                            showNotification(intent, tittle, message)
                        }
                    }


                }
            }
            CHAT -> {
                if (!ChatPortal.isRunning) {
                    // Here Title is orderId
                    intent = Intent(this, ChatPortal::class.java)
                    intent.putExtra(ChatPortal._key, tittle)
                    intent.putExtra(ChatPortal.mainKey,true)
                    val _title = if (senderType == COOK) "Chef" else "Customer"
                    Log.d(TAG,"Running...")
                    showNotification(intent, _title, message)

                }

            }

        }


    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun showNotification(intent: Intent, tittle: String, message: String) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notificationId = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chatTune =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.order_tune)
        //val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, Notification_Channel_Id)
            .setSmallIcon(R.drawable.chef_icon)
            .setContentTitle("$tittle")
            .setContentText("Its an Order")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val ringtone = RingtoneManager.getRingtone(this, chatTune)
        ringtone.play()
        builder.vibrate = longArrayOf(100, 500, 100, 500)
        notificationManager.notify(notificationId, builder)
    }

    private fun sendBroadCast(tittle: String, message: String) {
        Intent().also { intent ->
            intent.setAction(Notification_broadcast)
            intent.putExtra(_titleKey, tittle)
            intent.putExtra(_messageKey, message)
            sendBroadcast(intent)
        }
    }

}
