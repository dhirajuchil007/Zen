package com.velocityappsdj.zen.fragments

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.activities.AppListActivity
import com.velocityappsdj.zen.adapters.WhiteListAdapter
import com.velocityappsdj.zen.databinding.FragmentWhiteListBinding
import com.velocityappsdj.zen.models.AppDetails
import com.velocityappsdj.zen.room.WhiteListEntity
import com.velocityappsdj.zen.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class WhiteList : Fragment() {
    lateinit var binding: FragmentWhiteListBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: WhiteListAdapter
    var apps = mutableListOf<Pair<WhiteListEntity, AppDetails>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_white_list, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        adapter = WhiteListAdapter(apps, requireContext(), {
            viewModel.deleteWhiteListItem(it)
        }, {
            showTimePickers(it)
        })
        val recyclerView = binding.recyclerAppList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.getWhiteListedAppDetails().collect {
                adapter.setList(it)
            }
        }

        binding.fab.setOnClickListener {
            var intent = Intent(requireContext(), AppListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showTimePickers(whiteListEntity: WhiteListEntity) {

        var dialog =
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    whiteListEntity.whiteListStartHour = hourOfDay
                    whiteListEntity.whiteListStartMinutes = minute
                    showEndTime(whiteListEntity)
                }
            }, whiteListEntity.whiteListStartHour, whiteListEntity.whiteListStartMinutes, false)
        dialog.setTitle("Select Start time")
        dialog.show()

    }

    private fun showEndTime(whiteListEntity: WhiteListEntity) {
        var dialog =
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    whiteListEntity.whiteListEndHour = hourOfDay
                    whiteListEntity.whiteListEndMinutes = minute
                    viewModel.updateWhiteListItem(whiteListEntity)
                }
            }, whiteListEntity.whiteListEndHour, whiteListEntity.whiteListEndMinutes, false)
        dialog.setTitle("Select end time")
        dialog.show()
    }
}