package com.velocityappsdj.zen

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.velocityappsdj.zen.room.BatchTimeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AlarmUtil {

    fun scheduleAlarm(currentBatch: BatchTimeEntity?, applicationContext: Context) {
        currentBatch?.let {
            val intent = Intent(applicationContext, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                SharedPrefUtil(applicationContext).getCurrentBatchId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager =
                applicationContext.getSystemService(IntentService.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it.timeStamp,
                    pendingIntent
                )
            } else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.timeStamp, pendingIntent)
           /* SharedPrefUtil(applicationContext).setCurrentBatchPrimaryKey(it.batchTime)*/
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreUtil(applicationContext).saveBatch(it.batchTime)
            }
        }
    }
}