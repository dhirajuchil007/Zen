package com.velocityappsdj.zen

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.velocityappsdj.zen.activities.BatchNotificationsListActivity
import com.velocityappsdj.zen.services.NotificationSender

const val notificationId = 1
const val channel = "Batches"

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = "NotificationReceiver"
    override fun onReceive(context: Context, intent: Intent?) {
        val intent = Intent(context, NotificationSender::class.java)
        context.startService(intent)
    }
}