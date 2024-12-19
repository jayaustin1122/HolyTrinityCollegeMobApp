package com.holytrinity.api.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.main.Event
import com.holytrinity.databinding.FragmentCalendarBinding
import com.holytrinity.api.CalendarService
import com.holytrinity.api.sample.calendar.BottomSheetAddEventFragment
import com.holytrinity.api.sample.calendar.CalendarAdapter
import com.holytrinity.main.AddEventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val eventMap = mutableMapOf<String, Event>()  // Map to store events by date

    private val daysInMonth = mutableListOf<String>()  // List to store days in the current month

    companion object {
        private const val TAG = "CalendarFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView for the custom calendar
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 7) // 7 columns for a week
        loadEvents()

        // FAB button click to add an event
        binding.fabAddEvent.setOnClickListener {
            BottomSheetAddEventFragment().show(childFragmentManager, BottomSheetAddEventFragment.TAG)
        }
    }

    private fun loadEvents() {
        val service = RetrofitInstance.create(CalendarService::class.java)
        val call = service.getAllEvents()

        call.enqueue(object : Callback<AddEventResponse> {
            override fun onResponse(call: Call<AddEventResponse>, response: Response<AddEventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.events
                    if (events != null) {
                        eventMap.clear()
                        events.forEach {
                            eventMap[it.event_date] = it
                        }
                        // Create the list of days for the current month
                        loadCalendarDays()
                    }
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Log.e(TAG, "Request failed: ${t.message}")
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun loadCalendarDays() {
        // Here we generate the list of days for the current month (e.g., "2024-12-01" to "2024-12-31")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = mutableListOf<String>()
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDays) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val dateString = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            daysInMonth.add(dateString)
        }

        // Update RecyclerView with the list of days and events
        val adapter = CalendarAdapter(daysInMonth, eventMap) { day ->
            showEventDetails(day)  // Show event details when a day is clicked
        }
        binding.recyclerView.adapter = adapter
    }

    private fun showEventDetails(date: String) {
        val event = eventMap[date]
        if (event != null) {
            Toast.makeText(requireContext(), "Event: ${event.event_name}\nDescription: ${event.description}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "No events on this date.", Toast.LENGTH_SHORT).show()
        }
    }
}
