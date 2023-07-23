package com.velocityappsdj.zen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.velocityappsdj.zen.AdUtil
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
    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_list)

        viewModel = ViewModelProvider(this).get(AppListViewModel::class.java)

        binding.recyclerAppList.layoutManager = LinearLayoutManager(this)
        adapter = AppListAdapter(apps, this) { app, selected ->
            if (selected) {
                selectedApps.add(app)
                app.isSelected = true
            } else {
                selectedApps.remove(app)
                app.isSelected = false
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
        loadAds()

    }

    private fun setCount() {
        binding.txtNumberSelected.text =
            String.format(getString(R.string.apps_selected), selectedApps.size.toString())
    }

    private fun loadAds() {
        adContainerView = findViewById(R.id.adViewContainer)
        adView = AdView(this)
        adContainerView.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need
        // to wait until this view is laid out before we can get the width.
        adContainerView.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                AdUtil.loadBanner(
                    adView,
                    AdUtil.getAdSize(windowManager, adContainerView, resources, this)
                )
            }
        }
    }
}