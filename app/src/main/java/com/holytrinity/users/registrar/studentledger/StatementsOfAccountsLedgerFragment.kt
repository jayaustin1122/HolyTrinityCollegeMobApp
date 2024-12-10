package com.holytrinity.users.registrar.studentledger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStatementsOfAccountsLedgerBinding
import com.holytrinity.databinding.FragmentSummaryOfAccountsLedgerBinding


class StatementsOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding : FragmentStatementsOfAccountsLedgerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatementsOfAccountsLedgerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}