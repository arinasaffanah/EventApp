package com.dicoding.eventapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.eventapp.R
import com.dicoding.eventapp.databinding.ActivityEventDetailBinding
import com.dicoding.eventapp.ui.DetailViewModel
import com.dicoding.eventapp.ui.MainViewModel

class EventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("eventId") ?: return
        showLoading(true)
        detailViewModel.getEventDetail(eventId)

        detailViewModel.eventDetail.observe(this) { detailEventResponse ->
            showLoading(false)
            detailEventResponse?.event?.let { event ->
                // Update UI dengan detail event
                binding.tvEventName.text = event.name
                binding.tvOwnerName.text = event.ownerName
                binding.tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                val quotaRemaining = event.quota - event.registrants
                binding.tvQuota.text = "Sisa quota: $quotaRemaining"
                binding.tvBeginTime.text = "Waktu Mulai: ${event.beginTime}"
                binding.tvEndTime.text = "Waktu Selesai: ${event.endTime}"

                Glide.with(this)
                    .load(event.mediaCover ?: event.imageLogo)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.imageEvent)

                binding.btnOpenLink.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(browserIntent)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        Log.d("EventDetailActivity", "Loading state: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}