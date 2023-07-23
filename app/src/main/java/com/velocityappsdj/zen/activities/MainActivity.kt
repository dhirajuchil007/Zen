package com.velocityappsdj.zen.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.velocityappsdj.zen.*
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false

    private val TAG = "MainActivity"
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getAppsList()

        if (!SharedPrefUtil(this).isDefaultSetupDone()) {
            doFirstTimeSetup()
        }
        //scheduleNotification()
        val navController: NavController =
            Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_main_bottom_navigation_view)
        setupWithNavController(bottomNavigationView, navController)

        viewModel.getTodaySCount()
        //    startActivity(BatchNotificationsListActivity.getActivityIntent(this,1))
        showNextBatch()
        MobileAds.initialize(
            this
        ) {
            Log.d(TAG, "onCreate() called" + it.adapterStatusMap)
        }
        loadAds()

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

    private fun showNextBatch() {
        CoroutineScope(Dispatchers.IO).launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBatches().collectLatest {

                    val list = it.sortedBy { batch ->
                        batch.timeStamp
                    }
                    val currentBatch = TimeUtils.getNextBatch(System.currentTimeMillis(), list)
                    // scheduleAlarm(currentBatch)
                    Log.d(
                        TAG, "showNextBatch: " + list
                    )
                }
            }

        }
    }

    private fun doFirstTimeSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        viewModel.addDefaultBatches().observe(
            this
        ) {
            scheduleFirstBatch()
        }
        val sharedPrefUtil = SharedPrefUtil(this)
        sharedPrefUtil.setDefaultSetupDone(true)
        sharedPrefUtil.setCurrentBatchId(1)

    }

    @OptIn(InternalCoroutinesApi::class)
    private fun scheduleFirstBatch() {
        Log.d(TAG, "scheduleFirstBatch() called")
        CoroutineScope(Dispatchers.IO).launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBatches().collectLatest {

                    val list = it.sortedBy { batch ->
                        batch.timeStamp
                    }
                    val currentBatch = TimeUtils.getNextBatch(System.currentTimeMillis(), list)
                    scheduleAlarm(currentBatch)
                }
            }

        }

    }

    private fun scheduleAlarm(currentBatch: BatchTimeEntity?) {
        Log.d(TAG, "scheduleAlarm() called with: currentBatch = $currentBatch")
        currentBatch?.let {
            val intent = Intent(applicationContext, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                SharedPrefUtil(this).getCurrentBatchId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it.timeStamp,
                    pendingIntent
                )
            } else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.timeStamp, pendingIntent)
        }
    }


    private fun scheduleNotification() {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val cal: Calendar = Calendar.getInstance()
        // add 30 seconds to the calendar object
        // add 30 seconds to the calendar object
        cal.add(Calendar.SECOND, 30)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        } else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 1651733288000, pendingIntent)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Batches"
        val description = "Display batch notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channel, name, importance)
        channel.description = description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}