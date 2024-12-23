// AdminPreRequisitesFragment.kt
package com.holytrinity.users.admin.prerequisites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.databinding.FragmentAdminPreRequisitesBinding
import com.holytrinity.users.admin.adapter.PrerequisiteAdapter
import com.holytrinity.users.registrar.fee_management.BottomSheetAddAssessmentFragment

class AdminPreRequisitesFragment : Fragment() {

    private lateinit var binding: FragmentAdminPreRequisitesBinding
    private lateinit var viewModel: AdminPrerequisitesViewModel
    private lateinit var adapter: PrerequisiteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminPreRequisitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(AdminPrerequisitesViewModel::class.java)

        // Set up RecyclerView
        adapter = PrerequisiteAdapter(
            prerequisites = emptyList(),
            onEditClick = { prerequisite ->
                // Handle edit action
                // You can navigate to an edit screen or show a dialog
                Toast.makeText(context, "Edit clicked for ${prerequisite.subjectName}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { prerequisite ->
                // Handle delete action
                // Implement deletion logic
                Toast.makeText(context, "Delete clicked for ${prerequisite.subjectName}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // Observe LiveData from ViewModel
        viewModel.prerequisites.observe(viewLifecycleOwner, { prerequisites ->
            adapter.updateData(prerequisites)
        })

        viewModel.error.observe(viewLifecycleOwner, { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        })

        // Fetch data
        viewModel.fetchPrerequisites()

        // Handle toolbar back button
        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", R.id.nav_dashboard_admin)
            }
            findNavController().navigate(R.id.adminDrawerFragment, bundle)
        }

        // Handle add FAB button
        binding.addFabButton.setOnClickListener {
            val detailsDialog = BottomSheetAddPreRequisitesFragment()
            detailsDialog.show(childFragmentManager, "BottomSheetAddPreRequisitesFragment")
        }
    }
}
