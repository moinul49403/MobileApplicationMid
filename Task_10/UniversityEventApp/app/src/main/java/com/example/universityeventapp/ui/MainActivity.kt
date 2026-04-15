package com.example.universityeventapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.universityeventapp.R
import com.example.universityeventapp.databinding.ActivityMainBinding
import com.example.universityeventapp.model.Event
import com.example.universityeventapp.model.Speaker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBrowseEvents.setOnClickListener {
            startActivity(Intent(this, EventsListActivity::class.java))
        }

        binding.btnMyBookings.setOnClickListener { showComingSoonToast() }
        binding.btnNotifications.setOnClickListener { showComingSoonToast() }
        binding.btnProfile.setOnClickListener { showComingSoonToast() }

        val featuredEvent = createFeaturedEvent()
        binding.featuredEventCard.setOnClickListener {
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("event", featuredEvent)
            startActivity(intent)
        }

        binding.btnRegisterFeatured.setOnClickListener {
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("event", featuredEvent)
            startActivity(intent)
        }
    }

    private fun showComingSoonToast() {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun createFeaturedEvent(): Event {
        return Event(
            id = 100,
            title = "Annual Tech Summit 2025",
            date = "March 15, 2027",
            time = "10:00 AM",
            venue = "Main Auditorium",
            category = "Tech",
            description = "The Annual Tech Summit 2025 brings together student innovators, " +
                "industry experts, and researchers for a day of insightful sessions, networking, " +
                "and practical demonstrations focused on AI, cloud computing, and emerging technologies.",
            price = 15.0,
            totalSeats = 48,
            availableSeats = 32,
            imageRes = listOf(R.drawable.ic_menu_gallery),
            organizer = "Department of Computer Science",
            speakers = listOf(
                Speaker("Dr. Sarah Ahmed", "AI Research Lead", R.drawable.ic_launcher_foreground),
                Speaker("Engr. Kamal Hasan", "Cloud Architect", R.drawable.ic_launcher_foreground)
            )
        )
    }
}
