package com.holytrinity.users.admin.courses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentBottomSheetAddCoursesBinding

class BottomSheetAddCoursesFragment : Fragment() {
   private lateinit var binding: FragmentBottomSheetAddCoursesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetAddCoursesBinding.inflate(layoutInflater)
        return binding.root
    }

}