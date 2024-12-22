package com.holytrinity.api.sample.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
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
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val date = binding.etDate.text.toString()
            val description = binding.etDescription.text.toString()

            if (name.isEmpty() || date.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                val event = Event(0, name, date, description)
                saveEvent(event)
            }
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Event Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)
            binding.etDate.setText(formattedDate)
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
//                        Toast.makeText(
//                            requireContext(),
//                            "Event added successfully: ID = ${eventData?.event_id}, Name = ${eventData?.event_name}",
//                            Toast.LENGTH_LONG
//                        ).show()
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
