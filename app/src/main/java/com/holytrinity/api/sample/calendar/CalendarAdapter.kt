package com.holytrinity.api.sample.calendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.main.Event

class CalendarAdapter(
    private val days: List<String>,  // List of days in the current month (e.g., "2024-12-01")
    private val events: Map<String, Event>,  // Map of event dates
    private val onDayClick: (String) -> Unit  // Click listener to show event details
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
        val eventMarker: View = view.findViewById(R.id.eventMarker)
        val cardView: android.widget.FrameLayout = view.findViewById(R.id.cardView)  // Get the CardView to change background color

        fun bind(day: String) {
            dayText.text = day.takeLast(2)  // Get the day part (e.g., "01" from "2024-12-01")

            // If there's an event for this day, show the event marker
            if (events.containsKey(day)) {
                eventMarker.visibility = View.VISIBLE

                // Change the background color to light blue if there's an event
                cardView.setBackgroundColor(itemView.context.getColor(R.color.light_blue))  // Use light blue color
            } else {
                eventMarker.visibility = View.GONE

                // Set the default background if there's no event
                cardView.setBackgroundColor(itemView.context.getColor(R.color.white))  // Default white background
            }

            // Set the day click listener
            itemView.setOnClickListener {
                onDayClick(day)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int = days.size
}
