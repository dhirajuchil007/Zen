package com.velocityappsdj.zen.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.adapters.NotificationListAdapter
import com.velocityappsdj.zen.databinding.ActivityBatchNotificationsListBinding
import com.velocityappsdj.zen.models.NotificationListItem
import com.velocityappsdj.zen.viewmodel.BatchAppListViewModel
import com.velocityappsdj.zen.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatchNotificationsListActivity : AppCompatActivity() {
    private  val TAG = "BatchNotificationsListA"
    lateinit var adapter: NotificationListAdapter
    lateinit var viewModel: BatchAppListViewModel
    lateinit var binding: ActivityBatchNotificationsListBinding
    var notifications = mutableListOf<NotificationListItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_batch_notifications_list)
        viewModel = ViewModelProvider(this).get(BatchAppListViewModel::class.java)
        viewModel.getAppsList()
        var batchId = intent.getIntExtra(BATCH_ID, 0)
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        adapter = NotificationListAdapter(notifications, this)
        binding.recyclerNotifications.adapter=adapter
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getBatchNotifications(batchId).collect {
                Log.d(TAG, "onCreate: $it")
                adapter.setList(it)
            }
        }


    }

    companion object {
        val BATCH_ID = "batchId"
        fun getActivityIntent(context: Context, batchId: Int): Intent {
            val intent = Intent(context, BatchNotificationsListActivity::class.java)
            intent.putExtra(BATCH_ID, batchId)
            return intent
        }
    }
}