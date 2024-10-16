package com.dicoding.eventapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.eventapp.R
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.databinding.ItemEventBinding

class EventAdapter(private val onItemClick: (ListEventsItem) -> Unit) : ListAdapter<ListEventsItem, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            onItemClick(event) // Panggil fungsi yang diberikan saat item diklik
        }

    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onItemClick: (ListEventsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            // Menggunakan Glide untuk memuat gambar dari URL
            Glide.with(binding.imageEvent.context)
                .load(event.mediaCover)
                .placeholder(R.drawable.placeholder)
                .into(binding.imageEvent)

            binding.textEventName.text = event.name

            // Set listener untuk item yang di-klik
            itemView.setOnClickListener {
                onItemClick(event) // Panggil onItemClick dengan event
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id // Sesuaikan dengan field ID Anda
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
