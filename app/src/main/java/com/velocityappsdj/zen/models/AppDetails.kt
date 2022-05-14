package com.velocityappsdj.zen.models

import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo

data class AppDetails(
    var appName: String?,
    var packageName: String,
    var appInfo: ResolveInfo,
    var isSelected: Boolean,
    var isWhiteListed: Boolean
)
