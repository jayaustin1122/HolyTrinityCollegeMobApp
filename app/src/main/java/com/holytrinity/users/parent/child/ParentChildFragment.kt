package com.holytrinity.users.parent.child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.ApiResponse
import com.holytrinity.databinding.FragmentParentChildBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentChildFragment : Fragment() {

    private lateinit var binding: FragmentParentChildBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentChildBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
