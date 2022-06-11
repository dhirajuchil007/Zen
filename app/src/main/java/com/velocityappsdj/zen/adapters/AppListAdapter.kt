package com.velocityappsdj.zen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.databinding.AppListItemBinding
import com.velocityappsdj.zen.models.AppDetails

class AppListAdapter(
    var apps: List<AppDetails>,
    var context: Context,
    var callback: (app: AppDetails, selected: Boolean) -> Unit
) :
    RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {
    class AppViewHolder(
        var binding: AppListItemBinding,
        var context: Context,
        var callback: (app: AppDetails, selected: Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: AppDetails) {
            var pm = context.packageManager
            binding.imgAppIcon.setImageDrawable(app.appInfo.loadIcon(pm))
            binding.txtAppName.text = app.appInfo.loadLabel(pm)
            if (app.isWhiteListed) {
                binding.checkboxAdd.visibility = View.GONE
                binding.txtAdded.visibility = View.VISIBLE
            } else {
                binding.checkboxAdd.visibility = View.VISIBLE
                binding.txtAdded.visibility = View.GONE
            }

            binding.checkboxAdd.isChecked = app.isSelected

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.app_list_item,
                parent,
                false
            ), context, callback
        )
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appDetails = apps[position]
        holder.bind(appDetails)
        holder.binding.checkboxAdd.setOnClickListener { buttonView ->
            callback.invoke(appDetails, !appDetails.isSelected)
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun setList(appList: List<AppDetails>) {
        apps = appList
        notifyDataSetChanged()
    }
}