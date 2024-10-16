package com.dicoding.eventapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.databinding.FragmentFinishedBinding
import com.dicoding.eventapp.ui.EventAdapter
import com.dicoding.eventapp.ui.MainViewModel
import com.dicoding.eventapp.ui.detail.EventDetailActivity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        showLoading(true)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

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
                // Tampilkan pesan kesalahan menggunakan Snackbar
                Snackbar.make(binding.root, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }


        // Memanggil untuk mendapatkan acara yang telah selesai
        mainViewModel.getAllEvents()

        return binding.root
    }

    private fun setEventData(events: List<com.dicoding.eventapp.data.response.ListEventsItem>) {
        val adapter = EventAdapter()
        adapter.setOnItemClickListener(object : EventAdapter.OnItemClickListener {
            override fun onItemClick(event: ListEventsItem) {
                val intent = Intent(requireContext(), EventDetailActivity::class.java)
                intent.putExtra("eventId", event.id.toString())
                startActivity(intent)
            }
        })
        adapter.submitList(events)
        binding.rvFinishedEvents.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Menghindari memory leak
    }
}