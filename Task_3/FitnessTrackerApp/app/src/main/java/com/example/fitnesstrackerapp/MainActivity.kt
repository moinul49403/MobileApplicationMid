package com.example.fitnesstrackerapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var txtDate: TextView
    private lateinit var txtStepsValue: TextView
    private lateinit var txtPercentage: TextView
    private lateinit var progressSteps: ProgressBar
    private lateinit var btnUpdate: Button

    private val dailyGoal = 10000
    private var currentSteps = 3500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtDate = findViewById(R.id.txtDate)
        txtStepsValue = findViewById(R.id.txtStepsValue)
        txtPercentage = findViewById(R.id.txtPercentage)
        progressSteps = findViewById(R.id.progressSteps)
        btnUpdate = findViewById(R.id.btnUpdate)

        // Set current date
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        txtDate.text = currentDate

        // Initial progress update
        updateProgress()

        btnUpdate.setOnClickListener {
            showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        val input = EditText(this)
        input.hint = "Enter new step count"
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Update Steps")
            .setMessage("Enter your latest step count for today")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val value = input.text.toString().trim()

                if (value.isNotEmpty()) {
                    currentSteps = value.toInt()
                    updateProgress()
                } else {
                    Toast.makeText(
                        this,
                        "Please enter a valid number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProgress() {
        txtStepsValue.text = currentSteps.toString()

        var percentage = (currentSteps * 100) / dailyGoal

        if (percentage > 100) {
            percentage = 100
        }

        progressSteps.progress = percentage
        txtPercentage.text = "$percentage%"

        if (percentage >= 100) {
            Toast.makeText(
                this,
                "🎉 Great job! You reached today's goal!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}