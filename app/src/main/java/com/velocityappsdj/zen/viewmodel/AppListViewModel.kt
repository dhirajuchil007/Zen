package com.velocityappsdj.zen.viewmodel

import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.repo.AppListRepo
import com.velocityappsdj.zen.room.NotificationDatabase
import com.velocityappsdj.zen.room.WhiteListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val notificationDatabase: NotificationDatabase,
    private val packageManager: PackageManager
) : ViewModel() {
    enum class STATUS {
        LOADING,
        DONE
    }

    var appList = mutableListOf<AppDetails>()
    var appsAdded = MutableLiveData<STATUS>()
    fun getAppsWithWhiteList(): MutableLiveData<List<AppDetails>> {
        var list = MutableLiveData<List<AppDetails>>()

        viewModelScope.launch {
            notificationDatabase.whiteListDao().getAllWhiteListedApps().collect { whiteList ->
                appList.forEach { appDetails ->
                    var whiteListItem = whiteList.find { it.appId == appDetails.packageName }
                    if (whiteListItem != null) {
                        appDetails.isWhiteListed = true
                    }
                }

                list.postValue(appList.sortedBy {
                    it.appInfo.loadLabel(packageManager).toString()
                })
            }
        }

        return list
    }

    fun getAppsList() {
        appList = AppListRepo.getAppsList(packageManager)
    }

    fun addAppsToWhiteList(apps: List<AppDetails>) {
        appsAdded.postValue(STATUS.LOADING)
        viewModelScope.launch {
            apps.forEach {
                notificationDatabase.whiteListDao()
                    .addToWhiteList(WhiteListEntity(it.packageName, 0, 0, 23, 59))
            }
            appsAdded.postValue(STATUS.DONE)
        }

    }


}