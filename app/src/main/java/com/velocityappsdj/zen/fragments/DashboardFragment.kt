package com.velocityappsdj.zen.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.SharedPrefUtil
import com.velocityappsdj.zen.adapters.NotificationListAdapter
import com.velocityappsdj.zen.databinding.FragmentDashboardBinding
import com.velocityappsdj.zen.models.NotificationListItem
import com.velocityappsdj.zen.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {
    private val TAG = "Dashboard"
    lateinit var viewModel: MainViewModel
    lateinit var binding: FragmentDashboardBinding
    private lateinit var notificationListAdapter: NotificationListAdapter
    private var notificationItemList = mutableListOf<NotificationListItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dashboard, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRecycler(view)
        getData()
        binding.switchButton.isChecked = SharedPrefUtil(requireContext()).isZenModeEnabled()
        binding.switchButton.setOnCheckedChangeListener { view, isChecked ->
            SharedPrefUtil(requireContext()).setZenMode(isChecked)
        }
        binding.txtNextBatch?.text = SharedPrefUtil(requireContext()).getCurrentBatchPrimaryKey()
    }

    private fun setUpRecycler(view: View) {
        var recycler = view.findViewById<RecyclerView>(R.id.app_list)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        notificationListAdapter = NotificationListAdapter(notificationItemList, requireContext())
        recycler.adapter = notificationListAdapter
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.getNotification().collect {

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "getData: found")
                    notificationListAdapter.setList(it)
                }
            }
        }
        viewModel.getTodaySCount().observe(viewLifecycleOwner) {
            binding.txtNotificationCount.text = it.size.toString()
        }
    }
}