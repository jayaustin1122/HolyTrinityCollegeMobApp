package com.holytrinity.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.api.CalendarService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.sample.calendar.BottomSheetAddEventFragment
import com.holytrinity.databinding.ItemEventBinding
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventAdapter(
    private var events: List<Event>,
    private val roleId: Int,
    private val fragmentManager: androidx.fragment.app.FragmentManager,
    private val eventUpdatedListener: OnEventUpdatedListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.eventName.text = event.event_name
            binding.eventDescription.text = event.description

            // Handle item click based on roleId
            binding.root.setOnClickListener {
                if (roleId == 1) {
                    showEditDeleteDialog(event)
                } else {
                    // Optionally, show a message if the user cannot edit or delete
                    // Toast.makeText(itemView.context, "You don't have permission to edit or delete events", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun showEditDeleteDialog(event: Event) {
            val options = arrayOf("Edit", "Delete")
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Choose an option")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Pass event data to BottomSheetAddEventFragment for editing
                        val bottomSheet = BottomSheetAddEventFragment.newInstance(
                            event.event_id,
                            event.event_name,
                            event.event_date,
                            event.end_date ?: "",
                            event.description
                        )
                        bottomSheet.show(fragmentManager, BottomSheetAddEventFragment.TAG)
                        updateEventInList(event)
                    }
                    1 -> {
                        showDeleteConfirmationDialog(event)
                    }
                }
            }
            builder.show()
        }

        private fun showDeleteConfirmationDialog(event: Event) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Are you sure?")
            builder.setMessage("Do you really want to delete this event?")
            builder.setPositiveButton("Yes") { dialog, which ->
                Log.d("EventAdapter", "Delete event ID: ${event.event_id}")
                deleteEvent(event.event_id)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
    private fun deleteEvent(eventId: Int) {
        val service = RetrofitInstance.create(CalendarService::class.java)
        val eventToDelete = Event(event_id = eventId, event_name = "", event_date = "", end_date = "", description = "", action = "delete")

        val call = service.getDeleteEdit(eventToDelete)

        call.enqueue(object : Callback<AddEventResponse> {
            override fun onResponse(call: Call<AddEventResponse>, response: Response<AddEventResponse>) {
                if (response.isSuccessful) {
                    Log.d("DeleteEvent", "Event deleted successfully: ${response.body()?.success}")

                    // Find the event in the list and remove it
                    val eventIndex = events.indexOfFirst { it.event_id == eventId }
                    if (eventIndex != -1) {
                        events.toMutableList().apply {
                            removeAt(eventIndex)
                            notifyItemRemoved(eventIndex)
                        }
                    }
                } else {
                    Log.e("DeleteEvent", "Error in deleting event: ${response.message()}, Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Log.e("DeleteEvent", "Request failed: ${t.message}")
            }
        })
    }

    fun updateEventInList(updatedEvent: Event) {
        val index = events.indexOfFirst { it.event_id == updatedEvent.event_id }
        if (index != -1) {
            val updatedList = events.toMutableList()
            updatedList[index] = updatedEvent
            events = updatedList
            notifyItemChanged(index)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
    interface OnEventUpdatedListener {
        fun onEventUpdated(updatedEvent: Event)
    }
}
