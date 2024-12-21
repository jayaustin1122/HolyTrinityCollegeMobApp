package com.holytrinity.api.sample.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.main.Event
import com.holytrinity.databinding.FragmentCalendarBinding
import com.holytrinity.api.CalendarService
import com.holytrinity.api.sample.calendar.BottomSheetAddEventFragment.Companion.TAG
import com.holytrinity.main.AddEventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val eventMap = mutableMapOf<String, Event>()
    private var currentCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        loadEvents()
        binding.buttonPrevMonth.setOnClickListener {
            changeMonth(-1)
        }
        binding.buttonNextMonth.setOnClickListener {
            changeMonth(1)
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
            override fun onResponse(call: Call<AddEventResponse>, response: Response<AddEventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.events
                    if (events != null) {
                        eventMap.clear()
                        events.forEach {
                            eventMap[it.event_date] = it
                        }
                        updateCalendar()
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Log.e(TAG, "Request failed: ${t.message}")
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun updateCalendar() {
        val month = currentCalendar.get(Calendar.MONTH)
        val year = currentCalendar.get(Calendar.YEAR)

        binding.textViewMonthYear.text = String.format("%tB %d", currentCalendar, year)

        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = mutableListOf<String>()
        val maxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDays) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, i)
            val dateString = String.format(
                "%04d-%02d-%02d",
                tempCalendar.get(Calendar.YEAR),
                tempCalendar.get(Calendar.MONTH) + 1,
                tempCalendar.get(Calendar.DAY_OF_MONTH)
            )
            daysInMonth.add(dateString)
        }

        val adapter = CalendarAdapter(daysInMonth, eventMap) { day ->
            showEventDetails(day)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun showEventDetails(date: String) {
        val event = eventMap[date]
        if (event != null) {
            Toast.makeText(
                requireContext(),
                "Event: ${event.event_name}\nDescription: ${event.description}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(requireContext(), "No events on this date.", Toast.LENGTH_SHORT).show()
        }
    }
}
