package com.holytrinity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.model.Event

class CalendarAdapter(
    private val days: List<String>,
    private val events: MutableMap<String, MutableList<Event>>, // Events mapped by their date
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedDate: String? = null // Tracks the currently selected date

    inner class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
        val eventMarker: View = view.findViewById(R.id.eventMarker)

        fun bind(day: String) {
            dayText.text = day.takeLast(2) // Show only the day number

            if (day.isNotEmpty() && events.containsKey(day)) {
                eventMarker.visibility = View.VISIBLE // Show marker if there's an event

                // Change event marker drawable color
                val drawable = eventMarker.background
                val wrappedDrawable = DrawableCompat.wrap(drawable) // Wrap the drawable for tinting
                if (day == selectedDate) {
                    DrawableCompat.setTint(wrappedDrawable, itemView.context.getColor(R.color.violet_primary)) // Selected color
                } else {
                    DrawableCompat.setTint(wrappedDrawable, itemView.context.getColor(R.color.black)) // Default color
                }
            } else {
                eventMarker.visibility = View.GONE // Hide marker otherwise
            }

            if (day.isNotEmpty() && events.containsKey(day)) {
                eventMarker.visibility = View.VISIBLE // Show marker if there's an event
            } else {
                eventMarker.visibility = View.GONE // Hide marker otherwise
            }

            // Handle day click
            itemView.setOnClickListener {
                if (day.isNotEmpty()) {
                    selectedDate = day // Update the selected date
                    notifyDataSetChanged() // Refresh the RecyclerView to reflect the change
                    onDayClick(day) // Trigger the callback for the selected day
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size
}
