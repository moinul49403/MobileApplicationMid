package com.example.universityeventapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universityeventapp.adapter.GalleryAdapter
import com.example.universityeventapp.adapter.SpeakerAdapter
import com.example.universityeventapp.databinding.ActivityEventDetailBinding
import com.example.universityeventapp.model.Event
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val event = getEventFromIntent() ?: run {
            finish()
            return
        }

        bindEventDetails(event)
        setupGallery(event)
        setupSpeakers(event)
        startCountdown(event)

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, SeatBookingActivity::class.java).putExtra("event", event))
        }
    }

    private fun getEventFromIntent(): Event? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }
    }

    private fun bindEventDetails(event: Event) {
        binding.eventTitle.text = event.title
        binding.eventDateTime.text = "${event.date} at ${event.time}"
        binding.eventVenue.text = event.venue
        binding.eventOrganizer.text = "Organizer: ${event.organizer}"
        binding.eventDescription.text = event.description
        event.imageRes.firstOrNull()?.let { binding.eventHeaderImage.setImageResource(it) }
    }

    private fun setupGallery(event: Event) {
        binding.photoGalleryRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.photoGalleryRecycler.adapter = GalleryAdapter(event.imageRes)
    }

    private fun setupSpeakers(event: Event) {
        binding.speakersRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.speakersRecycler.adapter = SpeakerAdapter(event.speakers)
    }

    private fun startCountdown(event: Event) {
        val parser = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault())
        val eventDate = parser.parse("${event.date} ${event.time}") ?: return
        val diff = eventDate.time - System.currentTimeMillis()
        if (diff <= 0) {
            binding.countdownTimer.text = "Event is ongoing or completed"
            return
        }

        countDownTimer = object : CountDownTimer(diff, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = millisUntilFinished / (1000 * 60 * 60 * 24)
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val mins = (millisUntilFinished / (1000 * 60)) % 60
                val secs = (millisUntilFinished / 1000) % 60
                binding.countdownTimer.text = "Starts in: ${days}d ${hours}h ${mins}m ${secs}s"
            }

            override fun onFinish() {
                binding.countdownTimer.text = "Event started!"
            }
        }.start()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
