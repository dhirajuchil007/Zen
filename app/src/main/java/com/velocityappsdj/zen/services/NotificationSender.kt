package com.velocityappsdj.zen.services

import android.app.AlarmManager
import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.velocityappsdj.zen.*
import com.velocityappsdj.zen.activities.BatchNotificationsListActivity
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.room.NotificationDBBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationSender : IntentService("NotificationSender") {
    private val TAG = "NotificationSender"

    @OptIn(InternalCoroutinesApi::class)
    override fun onHandleIntent(intent: Intent?) {

        val sharedPrefUtil = SharedPrefUtil(applicationContext)
        val batchId = sharedPrefUtil.getCurrentBatchId()
        Log.d(TAG, "onReceive: $batchId")
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            BatchNotificationsListActivity.getActivityIntent(
                applicationContext, batchId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Zen test batch")
            .setAutoCancel(true)
            .setContentText("zen body").build()
        notification.contentIntent = pendingIntent
        val currentBatchPrimaryKey = sharedPrefUtil.getCurrentBatchPrimaryKey()
        val instance = NotificationDBBuilder.getInstance(applicationContext)
        currentBatchPrimaryKey?.let {
            CoroutineScope(Dispatchers.IO).launch {
                instance.batchTimeDao().getBatch(it)
                    .collect {
                        var timeSTamp = it[0].timeStamp
                        val timestamp: Instant = Instant.ofEpochMilli(timeSTamp)
                        var zomeTime: ZonedDateTime = timestamp.atZone(ZoneId.systemDefault())
                        zomeTime = zomeTime.plusDays(1)

                        it[0].timeStamp = zomeTime.toInstant().toEpochMilli()
                        instance.batchTimeDao().updateBatch(it[0])
                    }
            }
        }


        sharedPrefUtil.setCurrentBatchId(batchId + 1)


        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

        CoroutineScope(Dispatchers.IO).launch {
            instance.batchTimeDao().getBatches()
                .collectLatest {
                    var batch = TimeUtils.getNextBatch(System.currentTimeMillis(), it)
                    scheduleAlarm(batch)
                }
        }


    }

    private fun scheduleAlarm(currentBatch: BatchTimeEntity?) {
        Log.d(TAG, "scheduleAlarm() called with: currentBatch = $currentBatch")
        currentBatch?.let {
            val intent = Intent(applicationContext, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                SharedPrefUtil(this).getCurrentBatchId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it.timeStamp,
                    pendingIntent
                )
            } else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.timeStamp, pendingIntent)
            SharedPrefUtil(this).setCurrentBatchPrimaryKey(it.batchTime)
        }
    }

}