package com.holytrinity.users.admin.subjects

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.api.DiscountFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SubjectService
import com.holytrinity.databinding.FragmentBottomSheetAddDiscountBinding
import com.holytrinity.databinding.FragmentBottomSheetAddSubjectBinding

class BottomSheetAddSubjectFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddSubjectBinding? = null
    private val binding get() = _binding!!
    private val apiService: SubjectService by lazy {
        RetrofitInstance.create(SubjectService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    // Variables to determine if we are in edit mode
    private var editMode = false
    private var editId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke() // Trigger the callback when dialog is dismissed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}