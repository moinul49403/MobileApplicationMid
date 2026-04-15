package com.example.photogalleryapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val allPhotos: MutableList<Photo> = mutableListOf(
        Photo(1, android.R.drawable.ic_menu_gallery, "Sunrise Over Lake", "Nature"),
        Photo(2, android.R.drawable.ic_menu_compass, "Forest Trail View", "Nature"),
        Photo(3, android.R.drawable.ic_menu_camera, "Mountain Peaks", "Nature"),
        Photo(4, android.R.drawable.ic_menu_mapmode, "Rainy Downtown", "City"),
        Photo(5, android.R.drawable.ic_menu_directions, "Skyline at Dusk", "City"),
        Photo(6, android.R.drawable.ic_menu_myplaces, "Old Town Street", "City"),
        Photo(7, android.R.drawable.ic_menu_report_image, "Sleepy Koala", "Animals"),
        Photo(8, android.R.drawable.ic_menu_zoom, "Curious Fox", "Animals"),
        Photo(9, android.R.drawable.ic_menu_share, "Panda in Bamboo", "Animals"),
        Photo(10, android.R.drawable.ic_menu_search, "Italian Pasta Plate", "Food"),
        Photo(11, android.R.drawable.ic_menu_sort_by_size, "Fresh Fruit Bowl", "Food"),
        Photo(12, android.R.drawable.ic_menu_manage, "Grilled Street Tacos", "Food"),
        Photo(13, android.R.drawable.ic_menu_slideshow, "Beachside Resort", "Travel"),
        Photo(14, android.R.drawable.ic_menu_rotate, "Snowy Train Ride", "Travel"),
        Photo(15, android.R.drawable.ic_menu_info_details, "Desert Adventure", "Travel")
    )

    private val displayedPhotos: MutableList<Photo> = mutableListOf()
    private lateinit var adapter: PhotoAdapter

    private var isSelectionMode: Boolean = false
    private var currentCategory: String = "All"
    private var nextId: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appToolbar: LinearLayout = findViewById(R.id.appToolbar)
        val selectionToolbar: LinearLayout = findViewById(R.id.selectionToolbar)
        val tvSelectedCount: TextView = findViewById(R.id.tvSelectedCount)
        val btnShare: Button = findViewById(R.id.btnShare)
        val btnDelete: Button = findViewById(R.id.btnDelete)
        val tabAll: Button = findViewById(R.id.tabAll)
        val tabNature: Button = findViewById(R.id.tabNature)
        val tabCity: Button = findViewById(R.id.tabCity)
        val tabAnimals: Button = findViewById(R.id.tabAnimals)
        val tabFood: Button = findViewById(R.id.tabFood)
        val tabTravel: Button = findViewById(R.id.tabTravel)
        val gridView: GridView = findViewById(R.id.gridView)
        val fabAdd: FloatingActionButton = findViewById(R.id.fabAdd)

        displayedPhotos.addAll(allPhotos)
        adapter = PhotoAdapter(this, displayedPhotos)
        gridView.adapter = adapter

        fun refreshSelectionCount() {
            val count = adapter.getSelectedPhotos().size
            tvSelectedCount.text = "$count selected"
        }

        fun exitSelectionMode() {
            isSelectionMode = false
            adapter.isSelectionMode = false
            adapter.clearSelections()
            selectionToolbar.visibility = View.GONE
            appToolbar.visibility = View.VISIBLE
        }

        fun filterByCategory(category: String) {
            currentCategory = category
            displayedPhotos.clear()
            if (category == "All") {
                displayedPhotos.addAll(allPhotos)
            } else {
                displayedPhotos.addAll(allPhotos.filter { it.category == category })
            }
            adapter.notifyDataSetChanged()
            exitSelectionMode()
        }

        fun setActiveTab(activeTab: Button) {
            val tabs = listOf(tabAll, tabNature, tabCity, tabAnimals, tabFood, tabTravel)
            tabs.forEach { tab ->
                if (tab == activeTab) {
                    tab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1976D2"))
                    tab.setTextColor(Color.WHITE)
                } else {
                    tab.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    tab.setTextColor(Color.parseColor("#1976D2"))
                }
            }
        }

        tabAll.setOnClickListener {
            setActiveTab(tabAll)
            filterByCategory("All")
        }
        tabNature.setOnClickListener {
            setActiveTab(tabNature)
            filterByCategory("Nature")
        }
        tabCity.setOnClickListener {
            setActiveTab(tabCity)
            filterByCategory("City")
        }
        tabAnimals.setOnClickListener {
            setActiveTab(tabAnimals)
            filterByCategory("Animals")
        }
        tabFood.setOnClickListener {
            setActiveTab(tabFood)
            filterByCategory("Food")
        }
        tabTravel.setOnClickListener {
            setActiveTab(tabTravel)
            filterByCategory("Travel")
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val photo = displayedPhotos[position]
            if (isSelectionMode) {
                photo.isSelected = !photo.isSelected
                adapter.notifyDataSetChanged()
                refreshSelectionCount()
                if (adapter.getSelectedPhotos().isEmpty()) {
                    exitSelectionMode()
                }
            } else {
                val intent = Intent(this, FullscreenActivity::class.java)
                intent.putExtra("RESOURCE_ID", photo.resourceId)
                startActivity(intent)
            }
        }

        gridView.setOnItemLongClickListener { _, _, position, _ ->
            if (!isSelectionMode) {
                isSelectionMode = true
                adapter.isSelectionMode = true
                appToolbar.visibility = View.GONE
                selectionToolbar.visibility = View.VISIBLE
                displayedPhotos[position].isSelected = true
                adapter.notifyDataSetChanged()
                refreshSelectionCount()
            }
            true
        }

        btnDelete.setOnClickListener {
            val count = adapter.getSelectedPhotos().size
            if (count == 0) {
                Toast.makeText(this, "No photos selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIds = adapter.getSelectedPhotos().map { it.id }.toSet()
            allPhotos.removeAll { it.id in selectedIds }
            adapter.deleteSelected()

            Toast.makeText(this, "$count photo(s) deleted", Toast.LENGTH_SHORT).show()
            exitSelectionMode()
        }

        btnShare.setOnClickListener {
            val count = adapter.getSelectedPhotos().size
            if (count == 0) {
                Toast.makeText(this, "No photos selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Sharing $count photo(s) from Photo Gallery App!"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Share Photos"))
            exitSelectionMode()
        }

        fabAdd.setOnClickListener {
            val categories = listOf("Nature", "City", "Animals", "Food", "Travel")
            val category = categories[Random.nextInt(categories.size)]
            val photo = Photo(
                id = nextId++,
                resourceId = android.R.drawable.ic_menu_add,
                title = "New $category Photo",
                category = category
            )

            allPhotos.add(0, photo)
            if (currentCategory == "All" || currentCategory == category) {
                adapter.addPhoto(photo)
            }
            Toast.makeText(this, "Added: ${photo.title}", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isSelectionMode) {
            isSelectionMode = false
            adapter.isSelectionMode = false
            adapter.clearSelections()
            findViewById<LinearLayout>(R.id.selectionToolbar).visibility = View.GONE
            findViewById<LinearLayout>(R.id.appToolbar).visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}