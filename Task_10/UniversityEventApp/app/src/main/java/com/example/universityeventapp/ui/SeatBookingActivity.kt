package com.example.universityeventapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.universityeventapp.R
import com.example.universityeventapp.databinding.ActivitySeatBookingBinding
import com.example.universityeventapp.model.Event
import com.example.universityeventapp.model.Seat
import com.example.universityeventapp.model.SeatState
import kotlin.math.roundToInt
import kotlin.random.Random

class SeatBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBookingBinding
    private var event: Event? = null
    private val seats = mutableListOf<Seat>()
    private val selectedSeats = mutableListOf<Seat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        event = getEventFromIntent()
        if (event == null) {
            finish()
            return
        }

        setupToolbar()
        createSeats()
        renderSeatGrid()
        updateSummary()
        setupConfirmButton()
    }

    private fun getEventFromIntent(): Event? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun createSeats() {
        val rows = ('A'..'H').toList()
        val cols = (1..6).toList()
        rows.forEach { row ->
            cols.forEach { col ->
                seats.add(Seat(id = "$row$col"))
            }
        }

        val bookedCount = (seats.size * 0.3).roundToInt()
        seats.shuffled(Random(System.currentTimeMillis()))
            .take(bookedCount)
            .forEach { it.state = SeatState.BOOKED }
    }

    private fun renderSeatGrid() {
        binding.seatGrid.removeAllViews()
        seats.forEach { seat ->
            val seatView = TextView(this).apply {
                text = seat.id
                gravity = android.view.Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(getColor(android.R.color.white))
                background = getDrawableForState(seat.state)
            }

            val params = GridLayout.LayoutParams().apply {
                width = dpToPx(42)
                height = dpToPx(42)
                setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            }
            seatView.layoutParams = params

            if (seat.state != SeatState.BOOKED) {
                seatView.setOnClickListener {
                    toggleSeatSelection(seat, seatView)
                }
            }
            binding.seatGrid.addView(seatView)
        }
    }

    private fun toggleSeatSelection(seat: Seat, seatView: TextView) {
        if (seat.state == SeatState.AVAILABLE) {
            seat.state = SeatState.SELECTED
            selectedSeats.add(seat)
        } else if (seat.state == SeatState.SELECTED) {
            seat.state = SeatState.AVAILABLE
            selectedSeats.remove(seat)
        }
        seatView.background = getDrawableForState(seat.state)
        updateSummary()
    }

    private fun getDrawableForState(state: SeatState) = when (state) {
        SeatState.AVAILABLE -> getDrawable(R.drawable.seat_available)
        SeatState.BOOKED -> getDrawable(R.drawable.seat_booked)
        SeatState.SELECTED -> getDrawable(R.drawable.seat_selected)
    }

    private fun updateSummary() {
        val ticketPrice = event?.price ?: 0.0
        val totalPrice = selectedSeats.size * ticketPrice
        binding.selectedSeatsText.text = "${selectedSeats.size} Seats Selected"
        binding.totalPriceText.text = "Total: $${String.format("%.2f", totalPrice)}"
    }

    private fun setupConfirmButton() {
        binding.confirmBookingButton.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage("Book ${selectedSeats.size} seat(s) for this event?")
                .setPositiveButton("Confirm") { _, _ ->
                    startActivity(Intent(this, BookingConfirmationActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onBackPressed() {
        if (selectedSeats.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Leave Booking?")
                .setMessage("You have selected seats. Are you sure you want to go back?")
                .setPositiveButton("Leave") { _, _ -> super.onBackPressed() }
                .setNegativeButton("Stay", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}
