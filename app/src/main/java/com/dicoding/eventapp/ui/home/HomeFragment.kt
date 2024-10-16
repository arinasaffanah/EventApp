package com.dicoding.eventapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dicoding.eventapp.R
import com.dicoding.eventapp.data.repository.EventRepository
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.data.retrofit.ApiService
import com.dicoding.eventapp.databinding.FragmentHomeBinding
import com.dicoding.eventapp.ui.EventAdapter
import com.dicoding.eventapp.ui.MainViewModel
import com.dicoding.eventapp.ui.MainViewModelFactory
import com.dicoding.eventapp.ui.finished.FinishedFragment
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: EventRepository // Declare the repository
    private lateinit var apiService: ApiService // Declare the apiService

    private lateinit var adapter: EventAdapter // Inisialisasi adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        apiService = ApiService.create()

        // Initialize the repository with the apiService
        repository = EventRepository(apiService)

        // Create ViewModelFactory with the repository
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

//        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Inisialisasi adapter di sini
        adapter = EventAdapter { event ->
            // Navigasi ke EventDetailActivity dengan ID acara
            val action = R.id.action_navigation_home_to_eventDetailActivity // Menggunakan Safe Args
            findNavController().navigate(action)
        }

        // Mengatur adapter pada RecyclerView
        binding.rvUpcomingEvents.adapter = adapter

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

        // Memanggil untuk mendapatkan acara yang akan datang
        mainViewModel.getActiveEvents()

        // Memuat FinishedFragment ke dalam HomeFragment
        if (savedInstanceState == null) {
            val finishedFragment = FinishedFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.container_finished_fragment, finishedFragment)
                .commit()
        }

        return binding.root
    }

    private fun setEventData(events: List<ListEventsItem>) {
        adapter.submitList(events) // Gunakan adapter yang sudah ada
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Menghindari memory leak
    }
}