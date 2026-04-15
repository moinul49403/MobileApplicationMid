package com.example.universityeventapp.model

data class Seat(
    val id: String,
    var state: SeatState = SeatState.AVAILABLE
)

enum class SeatState {
    AVAILABLE, BOOKED, SELECTED
}
