package com.velocityappsdj.zen.viewmodel

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.models.NotificationListItem
import com.velocityappsdj.zen.repo.AppListRepo
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.room.NotificationDatabase
import com.velocityappsdj.zen.room.NotificationEntity
import com.velocityappsdj.zen.room.WhiteListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationDatabase: NotificationDatabase,
    private val packageManager: PackageManager
) : ViewModel() {
    private val TAG = "MainViewModel"

    var appList = mutableListOf<AppDetails>()

    private var _todaySCount = MutableLiveData<Int>()
    var todaySCount = _todaySCount


    fun getTodaySCount(): LiveData<List<NotificationEntity>> {
        return notificationDatabase.notificationDao()
            .getTodaySNotificationCount(getTodaySStartTime())
    }

    private fun getTodaySStartTime(): Long {
        var cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return cal.timeInMillis
    }

    fun getNotification(): Flow<List<NotificationListItem>> {
        return notificationDatabase.notificationDao().getAllNotifications().map {
            it.map { entity ->

                var app = appList.firstOrNull {
                    it.packageName == entity.packageName
                }

                app?.let { it1 -> NotificationListItem(entity, it1) }
                    ?: NotificationListItem(entity, null)
            }
        }
    }


    fun getAppsList() {
        appList = AppListRepo.getAppsList(packageManager)
    }

    fun getWhiteListedAppDetails(): Flow<List<Pair<WhiteListEntity, AppDetails?>>> {
        return notificationDatabase.whiteListDao().getAllWhiteListedApps().map {
            it.map { whiteList ->
                Pair(whiteList,
                    appList.find { appDetails ->
                        appDetails.packageName == whiteList.appId
                    })
            }
        }
    }

    fun deleteWhiteListItem(whiteListEntity: WhiteListEntity) {
        viewModelScope.launch {
            notificationDatabase.whiteListDao().removeItem(whiteListEntity)
        }
    }

    fun updateWhiteListItem(whiteListEntity: WhiteListEntity) {
        viewModelScope.launch {
            notificationDatabase.whiteListDao().updateItem(whiteListEntity)
        }
    }

    fun addDefaultBatches(): MutableLiveData<Boolean> {
        var liveData = MutableLiveData<Boolean>()


        var timeEightAm = ZonedDateTime.now()
     //   Log.d(TAG, "addDefaultBatches1: $timeEightAm")

        if ((timeEightAm.hour == 8 && timeEightAm.minute > 0) || timeEightAm.hour > 8) {
            timeEightAm = timeEightAm.plusDays(1)
        }
        var timeEightAmUpdated = timeEightAm.withHour(8).withMinute(0).withSecond(0)
      //  Log.d(TAG, "addDefaultBatches: $timeEightAmUpdated")


        var timeEightPm = ZonedDateTime.now()
        /* if ((timeEightPm.hour == 20 && timeEightPm.minute > 0) || timeEightPm.hour > 20) {
             timeEightPm = timeEightPm.plusDays(1)
         }*/
       // Log.d(TAG, "addDefaultBatches: $timeEightPm")
        timeEightPm =
            timeEightPm.withZoneSameLocal(ZoneId.systemDefault()).withHour(14).withMinute(1)
                .withSecond(0)
        Log.d(TAG, "addDefaultBatches: $timeEightPm")
        val timeMilis = timeEightPm.toInstant().toEpochMilli()

        viewModelScope.launch(Dispatchers.IO) {

            notificationDatabase.batchTimeDao().addBatch(
                BatchTimeEntity(
                    "08:00am", 8, 0, timeEightAm.toInstant().toEpochMilli()
                )
            )
            notificationDatabase.batchTimeDao().addBatch(
                BatchTimeEntity(
                    "08:00pm",
                    13,
                    52,
                    timeMilis
                )
            )
            liveData.postValue(true)
        }


        return liveData
    }

    fun getBatches(): Flow<List<BatchTimeEntity>> {
        return notificationDatabase.batchTimeDao().getBatches()
    }

    fun addBatch(batchTimeEntity: BatchTimeEntity) {
        viewModelScope.launch {
            notificationDatabase.batchTimeDao().addBatch(batchTimeEntity)
        }

    }

    fun deleteBatch(batchTimeEntity: BatchTimeEntity) {
        viewModelScope.launch {
            notificationDatabase.batchTimeDao().deleteBatch(batchTimeEntity)
        }

    }


}
