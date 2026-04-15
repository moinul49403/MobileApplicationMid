package com.example.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBookmark = findViewById<ImageButton>(R.id.btnBookmark)
        val btnShare = findViewById<ImageButton>(R.id.btnShare)
        val tvArticleTitle = findViewById<TextView>(R.id.tvArticleTitle)
        val nestedScrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        val btnNavIntro = findViewById<MaterialButton>(R.id.btnNavIntro)
        val btnNavKeyPoints = findViewById<MaterialButton>(R.id.btnNavKeyPoints)
        val btnNavAnalysis = findViewById<MaterialButton>(R.id.btnNavAnalysis)
        val btnNavConclusion = findViewById<MaterialButton>(R.id.btnNavConclusion)
        val sectionIntro = findViewById<TextView>(R.id.sectionIntro)
        val sectionKeyPoints = findViewById<TextView>(R.id.sectionKeyPoints)
        val sectionAnalysis = findViewById<TextView>(R.id.sectionAnalysis)
        val sectionConclusion = findViewById<TextView>(R.id.sectionConclusion)
        val fabBackToTop = findViewById<FloatingActionButton>(R.id.fabBackToTop)

        btnBookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                Toast.makeText(this, "Article Bookmarked", Toast.LENGTH_SHORT).show()
            } else {
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                Toast.makeText(this, "Bookmark Removed", Toast.LENGTH_SHORT).show()
            }
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, tvArticleTitle.text.toString())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        btnNavIntro.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, sectionIntro.top)
        }
        btnNavKeyPoints.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, sectionKeyPoints.top)
        }
        btnNavAnalysis.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, sectionAnalysis.top)
        }
        btnNavConclusion.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, sectionConclusion.top)
        }

        fabBackToTop.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, 0)
        }
    }
}