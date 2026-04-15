package com.example.photogalleryapp

import android.graphics.Matrix
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullscreenActivity : AppCompatActivity() {

    private lateinit var ivFullscreen: ImageView
    private lateinit var btnBack: ImageButton
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var scaleFactor = 1.0f
    private val matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        ivFullscreen = findViewById(R.id.ivFullscreen)
        btnBack = findViewById(R.id.btnBack)

        val resourceId = intent.getIntExtra("RESOURCE_ID", -1)
        if (resourceId != -1) {
            ivFullscreen.setImageResource(resourceId)
        }

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 5.0f)

                matrix.reset()
                val centerX = ivFullscreen.width / 2f
                val centerY = ivFullscreen.height / 2f
                matrix.postScale(scaleFactor, scaleFactor, centerX, centerY)
                ivFullscreen.imageMatrix = matrix
                return true
            }
        })

        ivFullscreen.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }
}
