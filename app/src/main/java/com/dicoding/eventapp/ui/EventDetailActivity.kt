package com.dicoding.eventapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.eventapp.R
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.databinding.ActivityEventDetailBinding
import com.dicoding.eventapp.ui.MainViewModel

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("eventId") // Sesuaikan dengan parameter yang Anda gunakan di fragment

        eventId?.let { id ->
            mainViewModel.getEventDetails(id) // Ambil detail acara berdasarkan ID
        }

        // Observe data dari ViewModel
        mainViewModel.eventDetails.observe(this) { event ->
            event?.let {
                displayEventDetails(it) // `it` harus bertipe ListEventsItem
            }
        }
    }

    private fun displayEventDetails(event: ListEventsItem) {
        Glide.with(this)
            .load(event.mediaCover) // Pastikan menggunakan field yang sesuai
            .into(binding.imageLogo)

        binding.textEventName.text = event.name ?: "Nama tidak tersedia"
        binding.textOwnerName.text = event.ownerName ?: "Penyelenggara tidak tersedia"
        binding.textBeginTime.text = event.beginTime ?: "Waktu tidak tersedia"
        binding.textQuota.text = "${(event.quota ?: 0) - (event.registrants ?: 0)} tersisa"
        binding.textDescription.text = event.description ?: "Deskripsi tidak tersedia"
        val buttonLink: Button = findViewById(R.id.buttonLink)

        binding.buttonLink.setOnClickListener {
            event.link?.let { link ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(browserIntent)
            }
        }
    }
}