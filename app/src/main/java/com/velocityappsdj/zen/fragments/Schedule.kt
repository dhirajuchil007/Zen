package com.velocityappsdj.zen.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.velocityappsdj.zen.NotificationReceiver
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.SharedPrefUtil
import com.velocityappsdj.zen.TimeUtils
import com.velocityappsdj.zen.adapters.BatchListAdapter
import com.velocityappsdj.zen.databinding.FragmentScheduleBinding
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class Schedule : Fragment() {
    lateinit var binding: FragmentScheduleBinding
    lateinit var viewModel: MainViewModel
    private var batches = mutableListOf<BatchTimeEntity>()
    lateinit var adapter: BatchListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_schedule, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        adapter = BatchListAdapter(batches) {
            lifecycleScope.launch {
                viewModel.deleteBatch(it)
            }
        }
        binding.recyclerBatchList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerBatchList.adapter = adapter

        lifecycleScope.launch {
            viewModel.getBatches().collect {
                adapter.setList(it)
            }
        }
        binding.fab.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        var dialog =
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    addBatchTime(hourOfDay, minute)
                }

            }, ZonedDateTime.now().hour, ZonedDateTime.now().minute, false)
        dialog.show()
    }

    private fun addBatchTime(hourOfDay: Int, minute: Int) {
        var timeStamp = ZonedDateTime.now()
        if (isTimeOfDayPassed(timeStamp, hourOfDay, minute))
            timeStamp = timeStamp.plusDays(1)
        timeStamp = timeStamp.withHour(hourOfDay).withMinute(minute)
        var hour = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
        var amPm = if (hourOfDay > 12) "pm" else "am"

        viewModel.addBatch(
            BatchTimeEntity(
                timeStamp.format(DateTimeFormatter.ofPattern("hh:mma")).lowercase(),
                hourOfDay,
                minute,
                timeStamp.toInstant().toEpochMilli()
            )
        )
        cancelAllAndScheduleNextBatch()
    }

    private fun cancelAllAndScheduleNextBatch() {

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getBatches().collectLatest {
                var list = it.sortedBy { batch ->
                    batch.timeStamp
                }
                val currentBatch = TimeUtils.getNextBatch(System.currentTimeMillis(), list)
                scheduleAlarm(currentBatch)
            }

        }

    }

    private fun scheduleAlarm(currentBatch: BatchTimeEntity?) {

        currentBatch?.let {
            val intent = Intent(requireContext(), NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                SharedPrefUtil(requireContext()).getCurrentBatchId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager =
                requireActivity().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it.timeStamp,
                    pendingIntent
                )
            } else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.timeStamp, pendingIntent)

            SharedPrefUtil(requireContext()).setCurrentBatchPrimaryKey(it.batchTime)
        }
    }

    private fun isTimeOfDayPassed(timeStamp: ZonedDateTime, hourOfDay: Int, minute: Int): Boolean {
        return when {
            timeStamp.hour > hourOfDay -> true
            timeStamp.hour == hourOfDay -> return timeStamp.minute > minute
            else -> false
        }
    }
}