package com.velocityappsdj.zen.repo

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.velocityappsdj.zen.models.AppDetails

object AppListRepo {
    fun getAppsList(packageManager: PackageManager): MutableList<AppDetails> {
        var appList = mutableListOf<AppDetails>()
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> =
            packageManager.queryIntentActivities(mainIntent, 0)

        for (resolveInfo in pkgAppsList) {

            appList.add(
                AppDetails(
                    resolveInfo.loadLabel(packageManager).toString(),
                    resolveInfo.activityInfo.packageName,
                    resolveInfo, false, false
                )
            )
        }

        return appList
    }
}