package com.example.universityeventapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universityeventapp.databinding.ItemSpeakerBinding
import com.example.universityeventapp.model.Speaker

class SpeakerAdapter(private val speakers: List<Speaker>) :
    RecyclerView.Adapter<SpeakerAdapter.SpeakerViewHolder>() {

    inner class SpeakerViewHolder(private val binding: ItemSpeakerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(speaker: Speaker) {
            binding.speakerName.text = speaker.name
            binding.speakerDesignation.text = speaker.designation
            binding.speakerPhoto.setImageResource(speaker.photoRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val binding = ItemSpeakerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpeakerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.bind(speakers[position])
    }

    override fun getItemCount(): Int = speakers.size
}
