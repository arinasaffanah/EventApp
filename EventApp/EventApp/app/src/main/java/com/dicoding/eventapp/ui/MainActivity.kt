package com.dicoding.eventapp.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventapp.R
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.databinding.ActivityMainBinding
import com.dicoding.eventapp.ui.detail.EventDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupNavigation()

        // Cek koneksi sebelum memanggil API
//        if (!isNetworkAvailable()) {
//            showOfflineMessage() // Tampilkan pesan offline
//        } else {
//            hideOfflineMessage() // Sembunyikan pesan offline
//            loadData()
//        }

        // Observe LiveData dari ViewModel
        mainViewModel.eventList.observe(this) { eventResponse ->
            eventResponse?.listEvents?.let {
                setEventData(it)
            } ?: Log.e("MainActivity", "No events found") // Logging jika tidak ada acara
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.snackBar.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

//    private fun loadData() {
//        mainViewModel.getActiveEvents()
//    }
//
//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = connectivityManager.activeNetworkInfo
//        return activeNetwork != null && activeNetwork.isConnected
//    }
//
//    private fun showOfflineMessage() {
//        binding.tvOfflineStatus.visibility = View.VISIBLE // Pastikan Anda memiliki TextView ini di layout
//        binding.tvOfflineStatus.text = "Anda offline. Periksa koneksi jaringan."
//    }
//
//    private fun hideOfflineMessage() {
//        binding.tvOfflineStatus.visibility = View.GONE // Sembunyikan pesan offline
//    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvEvents.layoutManager = layoutManager
        binding.rvEvents.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_upcoming, R.id.navigation_finished)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setEventData(events: List<ListEventsItem>) {
        val adapter = EventAdapter()
        adapter.submitList(events)
        adapter.setOnItemClickListener(object : EventAdapter.OnItemClickListener {
            override fun onItemClick(event: ListEventsItem) {
                val intent = Intent(this@MainActivity, EventDetailActivity::class.java)
                intent.putExtra("eventId", event.id.toString()) // Kirim eventId ke EventDetailActivity
                startActivity(intent)
            }
        })

        binding.rvEvents.adapter = adapter // Pastikan adapter diatur setelah submitList
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}