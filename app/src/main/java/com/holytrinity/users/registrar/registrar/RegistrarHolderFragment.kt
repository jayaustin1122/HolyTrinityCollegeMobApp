package com.holytrinity.users.registrar.registrar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarHolderBinding
import com.holytrinity.model.StudentSolo
import com.holytrinity.users.registrar.adapter.MyFragmentStateAdapter

class RegistrarHolderFragment : Fragment() {
    private lateinit var binding: FragmentRegistrarHolderBinding
    private lateinit var myAdapter: MyFragmentStateAdapter
    lateinit var studentSolo: StudentSolo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarHolderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.pager

        // Initialize the adapter
        myAdapter = MyFragmentStateAdapter(
            this@RegistrarHolderFragment.requireActivity(),
            listOf(
                RegistrarEnrollmentListFragment(),
                RegistarEnrollmentSubjectFragment()
            )
        )
        viewPager2.adapter = myAdapter

        // Set up the TabLayoutMediator
        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Enrollment List"
                1 -> tab.text = "Enrolled Subjects"
            }
        }.attach()

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", R.id.nav_dashboard)
            }
            findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
        }
    }


}
