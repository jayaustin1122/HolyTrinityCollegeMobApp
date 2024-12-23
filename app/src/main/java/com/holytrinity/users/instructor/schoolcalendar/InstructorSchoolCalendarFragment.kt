package com.holytrinity.users.instructor.schoolcalendar

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.adapter.CalendarAdapter
import com.holytrinity.adapter.EventAdapter
import com.holytrinity.api.CalendarService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentInstructorSchoolCalendarBinding
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class InstructorSchoolCalendarFragment : Fragment() {

    private lateinit var binding: FragmentInstructorSchoolCalendarBinding
    private val eventMap = mutableMapOf<String, MutableList<Event>>() // Stores events mapped by date
    private var currentCalendar: Calendar = Calendar.getInstance()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructorSchoolCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure RecyclerViews
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the event adapter
        eventAdapter = com.holytrinity.adapter.EventAdapter(emptyList())
        binding.eventRecyclerView.adapter = eventAdapter

        // Load events and update calendar
        loadEvents()

        // Navigation for months
        binding.buttonPrevMonth.setOnClickListener { changeMonth(-1) }
        binding.buttonNextMonth.setOnClickListener { changeMonth(1) }

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_instructor)
            }
            findNavController().navigate(R.id.instructorDrawerHolderFragment, bundle)
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