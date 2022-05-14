package com.velocityappsdj.zen.models

import com.velocityappsdj.zen.room.NotificationEntity

data class NotificationListItem(
    var notificationEntity: NotificationEntity,
    var appDetails: AppDetails?
)
