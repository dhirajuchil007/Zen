package com.velocityappsdj.zen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.adapters.AppListAdapter
import com.velocityappsdj.zen.databinding.ActivityAppListBinding
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.viewmodel.AppListViewModel
import com.velocityappsdj.zen.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppListActivity : AppCompatActivity() {
    lateinit var viewModel: AppListViewModel
    lateinit var binding: ActivityAppListBinding
    lateinit var adapter: AppListAdapter
    var apps: List<AppDetails> = mutableListOf()
    var selectedApps = mutableListOf<AppDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_list)

        viewModel = ViewModelProvider(this).get(AppListViewModel::class.java)

        binding.recyclerAppList.layoutManager = LinearLayoutManager(this)
        adapter = AppListAdapter(apps, this) { app, selected ->
            if (selected) {
                selectedApps.add(app)
                app.isSelected=true
            }
            else {
                selectedApps.remove(app)
                app.isSelected=false
            }
            setCount()
        }
        binding.recyclerAppList.adapter = adapter
        viewModel.getAppsList()
        viewModel.getAppsWithWhiteList().observe(this) {
            adapter.setList(it)
        }
        viewModel.appsAdded.observe(this) {
            when (it) {
                AppListViewModel.STATUS.DONE -> finish()
            }
        }

        binding.done.setOnClickListener {
            viewModel.addAppsToWhiteList(selectedApps)
        }

    }

    private fun setCount() {
        binding.txtNumberSelected.text =
            String.format(getString(R.string.apps_selected), selectedApps.size.toString())
    }
}