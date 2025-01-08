package com.holytrinity.api.sample.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.holytrinity.NotificationService
import com.holytrinity.R
import com.holytrinity.api.CalendarService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddEventBinding
import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BottomSheetAddEventFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetAddEventBinding

    private var eventUpdatedListener: EventAdapter.OnEventUpdatedListener? = null
    private fun onEventUpdated(event: Event) {
        eventUpdatedListener?.onEventUpdated(event)
        dismiss()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventAdapter.OnEventUpdatedListener) {
            eventUpdatedListener = context
        }
    }
    companion object {
        const val TAG = "BottomSheetAddEventFragment"

        // Keys for the arguments
        private const val ARG_EVENT_ID = "event_id"
        private const val ARG_EVENT_NAME = "event_name"
        private const val ARG_START_DATE = "start_date"
        private const val ARG_END_DATE = "end_date"
        private const val ARG_DESCRIPTION = "description"

        // Create a new instance of the fragment with event data
        fun newInstance(eventId: Int, eventName: String, startDate: String, endDate: String, description: String): BottomSheetAddEventFragment {
            val fragment = BottomSheetAddEventFragment()
            val args = Bundle()
            args.putInt(ARG_EVENT_ID, eventId)
            args.putString(ARG_EVENT_NAME, eventName)
            args.putString(ARG_START_DATE, startDate)
            args.putString(ARG_END_DATE, endDate ?: "")
            args.putString(ARG_DESCRIPTION, description)
            fragment.arguments = args
            return fragment
        }
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

        val eventId = arguments?.getInt(ARG_EVENT_ID, 0)
        val eventName = arguments?.getString(ARG_EVENT_NAME, "")
        val startDate = arguments?.getString(ARG_START_DATE, "")
        val endDate = arguments?.getString(ARG_END_DATE, "")
        val description = arguments?.getString(ARG_DESCRIPTION, "")

        // Populate fields with event data if editing
        if (eventId != 0) {
            binding.etName.setText(eventName)
            binding.etDate.setText(startDate)
            binding.etEndDate.setText(endDate)
            binding.etDescription.setText(description)
            binding.btnSave.text = "Update Event"
        }

        // Set up listeners for date pickers
        binding.etDate.setOnClickListener { showDatePicker(binding.etDate) }
        binding.etEndDate.setOnClickListener { showDatePicker(binding.etEndDate) }

        // Save button click
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val startDateInput = binding.etDate.text.toString()
            val endDateInput = binding.etEndDate.text.toString()
            val descriptionInput = binding.etDescription.text.toString()

            if (name.isEmpty() || startDateInput.isEmpty() || descriptionInput.isEmpty()) {
                Toast.makeText(requireContext(), "All required fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateParsed = sdf.parse(startDateInput)
            val endDateParsed = if (endDateInput.isNotEmpty()) sdf.parse(endDateInput) else startDateParsed

            val event = Event(eventId ?: 0, name, startDateInput, endDateInput, descriptionInput,"")

            if (eventId == 0) {
                // If eventId is 0, it's a new event, otherwise it's an edit
                saveEvent(event)
            } else {
                binding.btnSave.text = "Update"
                updateEvent(event)
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
            override fun onResponse(call: Call<AddEventResponse>, response: Response<AddEventResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Event added successfully!", Toast.LENGTH_LONG).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEvent(event: Event) {
        val service = RetrofitInstance.create(CalendarService::class.java)

        // Define the event with "update" action
        val updatedEvent = event.copy(action = "update")

        val call = service.getDeleteEdit(updatedEvent)

        call.enqueue(object : Callback<AddEventResponse> {
            override fun onResponse(call: Call<AddEventResponse>, response: Response<AddEventResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_LONG).show()
                    onEventUpdated(updatedEvent)
                    val roleId = UserPreferences.getRoleId(requireContext())
                    navigateBasedOnRole(roleId)
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddEventResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun navigateBasedOnRole(roleId: Int) {
        when (roleId) {
            1 -> findNavController().navigate(R.id.adminDrawerFragment)
            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
            else -> {
                // Default navigation if roleId doesn't match any of the above
                Toast.makeText(
                    requireContext(),
                    "Invalid role, navigating back to dashboard",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.nav_dashboard) // You can replace this with a default fragment
            }
        }
    }
}
