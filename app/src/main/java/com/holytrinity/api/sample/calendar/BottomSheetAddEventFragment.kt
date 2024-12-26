package com.holytrinity.api.sample.calendar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.holytrinity.NotificationService
import com.holytrinity.api.CalendarService
import com.holytrinity.databinding.FragmentBottomSheetAddEventBinding
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BottomSheetAddEventFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetAddEventBinding

    companion object {
        const val TAG = "BottomSheetAddEventFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTextView.setOnClickListener {
            val notificationIntent = Intent(context, NotificationService::class.java).apply {
                putExtra("title", "New Event!")
                putExtra("message", "Read and View New Event")
            }
            context?.startService(notificationIntent)

        }
        // Set up listeners for date pickers
        binding.etDate.setOnClickListener { showDatePicker(binding.etDate) }
        binding.etEndDate.setOnClickListener { showDatePicker(binding.etEndDate) }

        // Save button click
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val startDate = binding.etDate.text.toString()
            val endDate = binding.etEndDate.text.toString()
            val description = binding.etDescription.text.toString()

            if (name.isEmpty() || startDate.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "All required fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDateParsed = sdf.parse(startDate)
                val currentDate = Calendar.getInstance().time

                if (startDateParsed.before(currentDate)) {
                    Toast.makeText(requireContext(), "Start date cannot be before the current date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val endDateParsed = if (endDate.isNotEmpty()) sdf.parse(endDate) else startDateParsed

                if (endDateParsed.before(startDateParsed)) {
                    Toast.makeText(requireContext(), "End date cannot be before start date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val event = Event(0, name, startDate, endDate, description)
                saveEvent(event)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(targetEditText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            targetEditText.setText(dateFormat.format(calendar.time))
        }

        datePicker.show(parentFragmentManager, "date_picker")
    }


    private fun saveEvent(event: Event) {
        val service = RetrofitInstance.create(CalendarService::class.java)
        val call = service.addEvent(event)

        call.enqueue(object : Callback<AddEventResponse> {
            override fun onResponse(
                call: Call<AddEventResponse>,
                response: Response<AddEventResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success != null) {
                        val eventData = body.events
                        Toast.makeText(
                            requireContext(),
                            "Event added successfully!",
                            Toast.LENGTH_LONG
                        ).show()

                        // Sending a broadcast to trigger the notification
                        val notificationIntent = Intent(context, NotificationService::class.java).apply {
                            putExtra("title", "Event Added")
                            putExtra("message", "Your event has been successfully added.")
                        }
                        context?.startService(notificationIntent)

                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add event.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
