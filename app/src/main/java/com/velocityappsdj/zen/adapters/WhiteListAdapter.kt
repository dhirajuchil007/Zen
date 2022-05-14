package com.velocityappsdj.zen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.databinding.WhiteListItemBinding
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.room.WhiteListEntity
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WhiteListAdapter(
    var apps: List<Pair<WhiteListEntity, AppDetails?>>,
    var context: Context,
    var delete: (whiteListItem: WhiteListEntity) -> Unit,
    var edit: (whiteListItem: WhiteListEntity) -> Unit
) :
    RecyclerView.Adapter<WhiteListAdapter.WhiteListViewHolder>() {
    class WhiteListViewHolder(
        var binding: WhiteListItemBinding,
        var context: Context,
        var delete: (whiteListItem: WhiteListEntity) -> Unit,
        var edit: (whiteListItem: WhiteListEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: AppDetails?, whiteListItem: WhiteListEntity) {
            var pm = context.packageManager
            binding.imgAppIcon.setImageDrawable(app?.appInfo?.loadIcon(pm))
            binding.txtAppName.text = app?.appInfo?.loadLabel(pm)
            binding.txtWhitelistedFrom.text = getWhiteListedTime(whiteListItem)
            binding.imgDelete.setOnClickListener {
                delete.invoke(whiteListItem)
            }

            binding.txtEdit.setOnClickListener {
                edit.invoke(whiteListItem)
            }
        }

        private fun getWhiteListedTime(whiteListItem: WhiteListEntity): String? {
            var df = DateTimeFormatter.ofPattern("hh:mm a")
            var startTime =
                LocalTime.of(whiteListItem.whiteListStartHour, whiteListItem.whiteListStartMinutes)
            var endTime =
                LocalTime.of(whiteListItem.whiteListEndHour, whiteListItem.whiteListEndMinutes)
            return String.format(
                context.getString(R.string.whitelisted_from__to__),
                startTime.format(df),
                endTime.format(df)
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhiteListViewHolder {
        return WhiteListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.white_list_item,
                parent,
                false
            ), context, delete, edit
        )
    }

    override fun onBindViewHolder(holder: WhiteListViewHolder, position: Int) {
        holder.bind(apps[position].second, apps[position].first)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun setList(appList: List<Pair<WhiteListEntity, AppDetails?>>) {
        apps = appList
        notifyDataSetChanged()
    }
}