package com.velocityappsdj.zen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.databinding.BatchItemBinding
import com.velocityappsdj.zen.databinding.WhiteListItemBinding
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.room.WhiteListEntity
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BatchListAdapter(
    var batches: List<BatchTimeEntity>,
    var delete: (batchTomeEntity: BatchTimeEntity) -> Unit
) :
    RecyclerView.Adapter<BatchListAdapter.BatchListViewHolder>() {
    class BatchListViewHolder(
        var binding: BatchItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(batchTimeEntity: BatchTimeEntity) {
            binding.txtBatchTime.text = batchTimeEntity.batchTime
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchListViewHolder {
        return BatchListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.batch_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BatchListViewHolder, position: Int) {
        holder.bind(batches[position])
        holder.binding.imgDelete.setOnClickListener {
            delete.invoke(batches[position])
        }
    }

    override fun getItemCount(): Int {
        return batches.size
    }

    fun setList(batches: List<BatchTimeEntity>) {
        this.batches = batches
        notifyDataSetChanged()
    }
}