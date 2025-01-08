package com.holytrinity.api.sample.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentCalendarBinding
import com.holytrinity.api.CalendarService
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val eventMap = mutableMapOf<String, MutableList<Event>>() // Stores events mapped by date
    private var currentCalendar: Calendar = Calendar.getInstance()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roleId = UserPreferences.getRoleId(requireContext()) // Get the roleId

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter =  EventAdapter(
            requireContext(), emptyList(), roleId, childFragmentManager, object :
                EventAdapter.OnEventUpdatedListener {
                override fun onEventUpdated(updatedEvent: Event) {
                    // Handle the update, e.g., update the list
                    eventAdapter.updateEventInList(updatedEvent)
                }
            }
        )
        binding.eventRecyclerView.adapter = eventAdapter
        loadEvents()
        binding.buttonPrevMonth.setOnClickListener { changeMonth(-1) }
        binding.buttonNextMonth.setOnClickListener { changeMonth(1) }
        if (roleId == 1) {
            binding.fabAddEvent.setOnClickListener {
                BottomSheetAddEventFragment().show(childFragmentManager, BottomSheetAddEventFragment.TAG)
            }
        } else {
            binding.fabAddEvent.isEnabled = false
        }
        binding.fabAddEvent.setOnClickListener {
            BottomSheetAddEventFragment().show(childFragmentManager, BottomSheetAddEventFragment.TAG)
        }
    }

    private fun changeMonth(offset: Int) {
        currentCalendar.add(Calendar.MONTH, offset)
        loadEvents()
    }

    private fun loadEvents() {
        val service = RetrofitInstance.create(CalendarService::class.java)
        val call = service.getAllEvents()

        call.enqueue(object : Callback<AddEventResponse> {
            override fun onResponse(
                call: Call<AddEventResponse>,
                response: Response<AddEventResponse>
            ) {
                if (response.isSuccessful) {
                    val events = response.body()?.events
                    if (events != null) {
                        eventMap.clear()
                        events.forEach { event ->
                            eventMap.computeIfAbsent(event.event_date) { mutableListOf() }
                                .add(event)
                        }
                        updateCalendar()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load events: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error loading events: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateCalendar() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val sdf = SimpleDateFormat("MMMM yyyy")
        binding.textViewMonthYear.text = sdf.format(currentCalendar.time)
        val calendarItems = mutableListOf<String>()
        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)
        for (i in 1 until firstDayOfWeek) {
            calendarItems.add("")
        }
        val maxDaysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDaysInMonth) {
            val dateString = String.format(
                "%04d-%02d-%02d",
                tempCalendar.get(Calendar.YEAR),
                tempCalendar.get(Calendar.MONTH) + 1,
                i
            )
            calendarItems.add(dateString)
        }
        while (calendarItems.size % 7 != 0) {
            calendarItems.add("")
        }
        binding.recyclerView.adapter = CalendarAdapter(calendarItems, eventMap) { day ->
            if (day.isNotEmpty()) {
                showEventsForDate(day)
            }
        }
    }

    private fun showEventsForDate(date: String) {
        val events = eventMap[date] ?: emptyList()
        eventAdapter.updateEvents(events)
    }
}