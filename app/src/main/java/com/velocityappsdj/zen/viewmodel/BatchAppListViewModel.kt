package com.velocityappsdj.zen.viewmodel

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.models.NotificationListItem
import com.velocityappsdj.zen.repo.AppListRepo
import com.velocityappsdj.zen.room.NotificationDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BatchAppListViewModel @Inject constructor(
    private val notificationDatabase: NotificationDatabase,
    private val packageManager: PackageManager
) : ViewModel() {
    var appList = mutableListOf<AppDetails>()


    fun getBatchNotifications(batchId: Int): Flow<List<NotificationListItem>> {
        return notificationDatabase.notificationDao().getBatchNotifications(batchId).map {
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
}