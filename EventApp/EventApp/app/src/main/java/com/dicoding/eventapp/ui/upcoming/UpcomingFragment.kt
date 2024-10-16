package com.dicoding.eventapp.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.databinding.FragmentUpcomingBinding
import com.dicoding.eventapp.ui.EventAdapter
import com.dicoding.eventapp.ui.MainViewModel
import com.dicoding.eventapp.ui.detail.EventDetailActivity
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        showLoading(true)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

//        setupRecyclerView()

        // Mengamati LiveData untuk daftar acara
        mainViewModel.eventList.observe(viewLifecycleOwner) { eventResponse ->
            eventResponse?.listEvents?.let {
                setEventData(it)
            }
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.snackBar.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(binding.root, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }
        showLoading(true)

        // Memanggil untuk mendapatkan acara yang akan datang
        mainViewModel.getActiveEvents()

        return binding.root
    }

//    private fun setupRecyclerView() {
//        // Mengatur LayoutManager untuk RecyclerView
//        val layoutManager = LinearLayoutManager(requireContext())
//        binding.rvUpcomingEvents.layoutManager = layoutManager
//        binding.rvUpcomingEvents.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation)) // Menambahkan pemisah jika diperlukan
//    }

    private fun setEventData(events: List<ListEventsItem>) {
        val adapter = EventAdapter()
        adapter.setOnItemClickListener(object : EventAdapter.OnItemClickListener {
            override fun onItemClick(event: ListEventsItem) {
                val eventId = event.id?.toString()
                Log.d("UpcomingFragment", "Event ID: $eventId")  // Memastikan eventId valid
                if (eventId != null) {
                    val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    intent.putExtra("eventId", eventId)  // Mengirim eventId ke Activity detail
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.root, "ID event tidak valid", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        adapter.submitList(events)
        binding.rvUpcomingEvents.adapter = adapter // Menghubungkan adapter ke RecyclerView
    }

    private fun showLoading(isLoading: Boolean) {
        Log.d("UpcomingFragment", "Loading state: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.layoutManager = layoutManager
        binding.rvUpcomingEvents.adapter = EventAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Menghindari memory leak
    }
}