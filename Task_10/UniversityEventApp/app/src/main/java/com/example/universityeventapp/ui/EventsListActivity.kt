package com.example.universityeventapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.universityeventapp.R
import com.example.universityeventapp.adapter.EventAdapter
import com.example.universityeventapp.databinding.ActivityEventsListBinding
import com.example.universityeventapp.model.Event
import com.example.universityeventapp.model.Speaker
import com.google.android.material.chip.Chip
import androidx.recyclerview.widget.LinearLayoutManager

class EventsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventsListBinding
    private lateinit var eventAdapter: EventAdapter
    private lateinit var allEvents: List<Event>
    private var selectedCategory: String = "All"
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        allEvents = createSampleEvents()
        setupRecyclerView()
        setupCategoryChips()
        setupSearchView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(allEvents) { event ->
            startActivity(Intent(this, EventDetailActivity::class.java).putExtra("event", event))
        }
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEvents.adapter = eventAdapter
    }

    private fun setupCategoryChips() {
        val categories = mutableListOf("All")
        categories.addAll(allEvents.map { it.category }.distinct().sorted())

        categories.forEachIndexed { index, category ->
            val chip = Chip(this).apply {
                text = category
                isCheckable = true
                id = index + 1
                if (category == "All") isChecked = true
            }
            binding.chipGroupCategory.addView(chip)
        }

        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            selectedCategory = group.findViewById<Chip>(checkedId)?.text?.toString() ?: "All"
            applyFilters()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.orEmpty()
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })
    }

    private fun applyFilters() {
        val filtered = allEvents.filter { event ->
            val categoryMatch = selectedCategory == "All" || event.category == selectedCategory
            val text = searchQuery.trim().lowercase()
            val searchMatch = text.isBlank() ||
                event.title.lowercase().contains(text) ||
                event.venue.lowercase().contains(text) ||
                event.description.lowercase().contains(text)
            categoryMatch && searchMatch
        }
        eventAdapter.filterList(filtered)
    }

    private fun createSampleEvents(): List<Event> {
        val commonSpeakers = listOf(
            Speaker("Prof. Lina Karim", "Department Chair", R.drawable.ic_launcher_foreground),
            Speaker("Arif Rahman", "Industry Mentor", R.drawable.ic_launcher_foreground),
            Speaker("Nadia Islam", "Student Leader", R.drawable.ic_launcher_foreground)
        )

        return listOf(
            Event(
                1, "Annual Tech Summit 2025", "March 15, 2027", "10:00 AM", "Main Auditorium", "Tech",
                "Join faculty, students, and industry guests for a full-day summit covering applied AI, " +
                    "mobile development, cloud-native systems, and product strategy. The program includes " +
                    "hands-on demos, networking sessions, and collaborative idea exchanges.",
                15.0, 48, 30, listOf(R.drawable.ic_menu_gallery), "CSE Department", commonSpeakers
            ),
            Event(
                2, "Inter-University Football Cup", "April 02, 2027", "3:30 PM", "Central Field", "Sports",
                "A competitive football tournament where teams from multiple universities compete in " +
                    "group and knockout rounds. Spectators can enjoy live commentary, halftime shows, " +
                    "and student fan activities.",
                5.0, 48, 18, listOf(R.drawable.ic_menu_gallery), "Sports Club", commonSpeakers
            ),
            Event(
                3, "Spring Cultural Night", "May 01, 2027", "6:00 PM", "Open Air Theatre", "Cultural",
                "An evening celebration of music, dance, drama, and visual arts by student performers " +
                    "from different departments. The event highlights diversity, creativity, and campus spirit.",
                8.0, 48, 22, listOf(R.drawable.ic_menu_gallery), "Cultural Society", commonSpeakers
            ),
            Event(
                4, "Research Colloquium", "March 28, 2027", "11:00 AM", "Seminar Hall 2", "Academic",
                "Faculty and postgraduate students present recent findings in data science, sustainable " +
                    "engineering, education technology, and health analytics. The colloquium promotes " +
                    "interdisciplinary discussion and collaboration.",
                0.0, 48, 40, listOf(R.drawable.ic_menu_gallery), "Academic Affairs", commonSpeakers
            ),
            Event(
                5, "Campus Volunteer Meetup", "April 10, 2027", "9:00 AM", "Student Center", "Social",
                "A community-focused meetup connecting students with upcoming volunteering opportunities " +
                    "in education, healthcare camps, and environmental projects. Includes briefing sessions " +
                    "and group onboarding.",
                0.0, 48, 35, listOf(R.drawable.ic_menu_gallery), "Community Engagement Office", commonSpeakers
            ),
            Event(
                6, "Cybersecurity Bootcamp", "June 12, 2027", "1:00 PM", "Lab Complex A", "Tech",
                "An intensive bootcamp on ethical hacking basics, secure coding principles, threat modeling, " +
                    "and incident response planning. Participants engage in guided labs and simulation exercises.",
                20.0, 48, 12, listOf(R.drawable.ic_menu_gallery), "ICT Cell", commonSpeakers
            ),
            Event(
                7, "Basketball Championship Finals", "May 20, 2027", "4:00 PM", "Indoor Sports Arena", "Sports",
                "Watch the top two campus teams compete in the championship game after a season of " +
                    "high-energy matches. The final includes pre-game analysis, cheer performances, and awards.",
                6.0, 48, 10, listOf(R.drawable.ic_menu_gallery), "Athletics Committee", commonSpeakers
            ),
            Event(
                8, "Debate & Public Speaking Forum", "April 22, 2027", "2:00 PM", "Conference Room B", "Academic",
                "A structured debate and public speaking forum where students discuss contemporary social, " +
                    "scientific, and policy issues. Judges provide feedback on argument quality, clarity, " +
                    "and delivery.",
                3.0, 48, 28, listOf(R.drawable.ic_menu_gallery), "Language and Communication Club", commonSpeakers
            )
        )
    }
}
