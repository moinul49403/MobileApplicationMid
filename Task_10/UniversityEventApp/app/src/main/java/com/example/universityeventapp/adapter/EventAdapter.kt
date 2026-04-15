package com.example.universityeventapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universityeventapp.R
import com.example.universityeventapp.databinding.ItemEventBinding
import com.example.universityeventapp.model.Event

class EventAdapter(
    private var events: List<Event>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val fullEventsList: List<Event> = events.toList()

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.eventTitle.text = event.title
            binding.eventDate.text = "${event.date} at ${event.time}"
            binding.eventVenue.text = event.venue
            binding.eventSeats.text = "Available Seats: ${event.availableSeats}/${event.totalSeats}"
            binding.eventPrice.text = "Price: $${String.format("%.2f", event.price)}"
            binding.eventSeats.setTextColor(
                if (event.availableSeats > 0) {
                    binding.root.context.getColor(R.color.seat_available)
                } else {
                    binding.root.context.getColor(R.color.seat_booked)
                }
            )

            event.imageRes.firstOrNull()?.let { binding.eventImage.setImageResource(it) }
            itemView.setOnClickListener { onEventClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun filterList(filteredEvents: List<Event>) {
        events = filteredEvents
        notifyDataSetChanged()
    }

    fun getFullEventsList(): List<Event> = fullEventsList
}
