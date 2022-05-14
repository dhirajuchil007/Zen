package com.velocityappsdj.zen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.TimeUtils
import com.velocityappsdj.zen.models.NotificationListItem

class NotificationListAdapter(var notifications: List<NotificationListItem>, var context: Context) :
    RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder>() {
    class NotificationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notification_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        var pm = context.packageManager
        holder.itemView.findViewById<TextView>(R.id.txt_app_name).text =
            notifications[position].appDetails?.appName
        holder.itemView.findViewById<ImageView>(R.id.img_app_icon)
            .setImageDrawable(notifications[position].appDetails?.appInfo?.loadIcon(pm))
        holder.itemView.findViewById<TextView>(R.id.txt_notification_title).text =
            notifications[position].notificationEntity.title
        holder.itemView.findViewById<TextView>(R.id.txt_notification_text).text =
            notifications[position].notificationEntity.text
        holder.itemView.findViewById<TextView>(R.id.txt_time_elapsed).text =
                //notifications[position].notificationEntity.id
            TimeUtils.getTimePassed(notifications[position].notificationEntity.timestamp)

    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun setList(list: List<NotificationListItem>) {
        notifications = list
        notifyDataSetChanged()
    }
}