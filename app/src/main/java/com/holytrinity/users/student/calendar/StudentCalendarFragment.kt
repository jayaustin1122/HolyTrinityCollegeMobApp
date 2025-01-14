package com.holytrinity.users.student.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.adapter.CalendarAdapter
import com.holytrinity.adapter.EventAdapter
import com.holytrinity.api.CalendarService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentStudentCalendarBinding
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar


class StudentCalendarFragment : Fragment() {

    private lateinit var binding : FragmentStudentCalendarBinding
    private val eventMap = mutableMapOf<String, MutableList<Event>>() // Stores events mapped by date
    private var currentCalendar: Calendar = Calendar.getInstance()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentCalendarBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Configure RecyclerViews
        val roleId = UserPreferences.getRoleId(requireContext())

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(
            requireContext(), emptyList(), roleId, childFragmentManager, object :
                EventAdapter.OnEventUpdatedListener {
                override fun onEventUpdated(updatedEvent: Event) {
                    // Handle the update, e.g., update the list
                    eventAdapter.updateEventInList(updatedEvent)
                }
            }
        )
        binding.eventRecyclerView.adapter = eventAdapter

        // Load events and update calendar
        loadEvents()

        // Navigation for months
        binding.buttonPrevMonth.setOnClickListener { changeMonth(-1) }
        binding.buttonNextMonth.setOnClickListener { changeMonth(1) }

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_student)
            }
            findNavController().navigate(R.id.studentDrawerHolderFragment, bundle)
        }
    }

    private fun changeMonth(offset: Int) {
        currentCalendar.add(Calendar.MONTH, offset)
        loadEvents() // Reload events for the updated month
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
                        eventMap.clear() // Clear previous events

                        events.forEach { event ->
                            val eventDateStr = event.event_date
                            val endDateStr = event.end_date ?: event.event_date // Default to event_date if end_date is null

                            if (!eventDateStr.isNullOrEmpty() && !endDateStr.isNullOrEmpty()) {
                                try {
                                    val eventDate = SimpleDateFormat("yyyy-MM-dd").parse(eventDateStr)
                                    val endDate = SimpleDateFormat("yyyy-MM-dd").parse(endDateStr)

                                    val tempCalendar = Calendar.getInstance()
                                    tempCalendar.time = eventDate

                                    while (!tempCalendar.time.after(endDate)) {
                                        val dateString = SimpleDateFormat("yyyy-MM-dd").format(tempCalendar.time)
                                        eventMap.computeIfAbsent(dateString) { mutableListOf() }.add(event)
                                        tempCalendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
                                    }
                                } catch (e: Exception) {
                                    Log.e("DateParsing", "Error parsing dates for event: ${event.event_id} - ${e.message}")
                                }
                            } else {
                                Log.e("DateValidation", "Invalid event_date or end_date for event: $event")
                            }
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

        // Set the header for the current month and year
        binding.textViewMonthYear.text = sdf.format(currentCalendar.time)

        // Prepare the data list for RecyclerView
        val calendarItems = mutableListOf<String>()

        // Move to the first day of the month
        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        // Add empty cells for days before the first day of the month
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)
        for (i in 1 until firstDayOfWeek) {
            calendarItems.add("")
        }

        // Add days of the current month
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

        // Add empty cells to fill the grid
        while (calendarItems.size % 7 != 0) {
            calendarItems.add("")
        }

        // Set the adapter with event-aware data
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