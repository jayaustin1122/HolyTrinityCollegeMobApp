package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.api.AccountService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentAdminManageAccountBinding
import com.holytrinity.model.Account
import com.holytrinity.users.admin.adapter.AccountsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminManageAccountFragment : Fragment() {
    private lateinit var binding: FragmentAdminManageAccountBinding
    private lateinit var accountsAdapter: AccountsAdapter
    private var accountsList: List<Account> = emptyList()
    private var filteredAccounts: List<Account> = emptyList()

    private lateinit var roleBottomSheet: BottomSheetDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminManageAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupRoleFilter()

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_admin)
            }
            findNavController().navigate(R.id.adminDrawerFragment, bundle)
        }

        getAccounts()
    }

    private fun setupRecyclerView() {
        accountsAdapter = AccountsAdapter(filteredAccounts)
        binding.recyclerSummary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = accountsAdapter
        }
    }

    private fun setupSearch() {
        binding.searchStudentTextView.addTextChangedListener {
            filterAccounts()
        }
    }

    private fun setupRoleFilter() {
        binding.btnFilter.setOnClickListener {
            showRoleFilterBottomSheet()
        }
    }

    private fun showRoleFilterBottomSheet() {
        if (!::roleBottomSheet.isInitialized) {
            roleBottomSheet = RoleFilterBottomSheet { selectedRole ->
                filterAccounts(roleId = selectedRole)
            }
        }
        roleBottomSheet.show(childFragmentManager, roleBottomSheet.tag)
    }

    private fun getAccounts() {
        val service = RetrofitInstance.create(AccountService::class.java)
        service.getAllAccounts().enqueue(object : Callback<List<Account>> {
            override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                if (response.isSuccessful) {
                    accountsList = response.body() ?: emptyList()
                    filteredAccounts = accountsList
                    accountsAdapter.updateData(filteredAccounts)
                } else {
                    Toast.makeText(context, "Failed to load accounts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterAccounts(roleId: Int? = null) {
        val searchQuery = binding.searchStudentTextView.text.toString().lowercase()

        filteredAccounts = accountsList.filter { account ->
            val roleMatches = roleId?.let { account.role_id == it } ?: true
            val nameMatches = account.name.lowercase().contains(searchQuery)
            roleMatches && nameMatches
        }

        accountsAdapter.updateData(filteredAccounts)
    }
}
