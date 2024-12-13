package com.holytrinity.users.student.admit.steps

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepHolderBinding
import com.holytrinity.users.student.adapter.StudentAdmitAdapter
import com.shuhart.stepview.StepView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class StudentGetAdmittedStepHolder : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedStepHolderBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var stepView: StepView
    private lateinit var viewModel: ViewModelAdmit
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var adapter: StudentAdmitAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentStudentGetAdmittedStepHolderBinding.inflate(layoutInflater)
        viewPager = binding.viewpagersignup
        stepView = binding.stepView
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelAdmit::class.java]
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StudentAdmitAdapter(requireActivity())
        viewPager.adapter = adapter
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backItem()
                }
            })
        adapter.addFragment(StudentGetAdmittedStepOneFragment())
        adapter.addFragment(StudentGetAdmittedStepTwoFragment())
        adapter.addFragment(StudentGetAdmittedStepThreeFragment())
        adapter.addFragment(StudentGetAdmittedStepFourFragment())
        stepView.go(0, true)
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stepView.go(position, true)
            }
        })
        binding.btnContinue.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> validateFragmentOne()
                1 -> validateFragmentTwo()
                2 -> validateFragmentThree()
                3 -> validateFragmentFour()
            }
        }

    }
    fun backItem() {
        val currentItem = viewPager.currentItem
        val nextItem = currentItem - 1
        if (nextItem >= 0) {
            viewPager.currentItem = nextItem
        }
    }

    private fun nextItem() {
        val currentItem = viewPager.currentItem
        val nextItem = currentItem + 1
        if (nextItem < adapter.itemCount) {
            viewPager.currentItem = nextItem
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateFragmentFour() {
        val userName = viewModel.userName
        val password = viewModel.password
        val confirmPassword = viewModel.confirmPassword

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Username and password are required", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            uploadData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        val lrn = viewModel.lrn
        val attended = viewModel.attended
        val form137 = viewModel.form137
        val diploma = viewModel.diploma
        val tor = viewModel.tor
        val coh = viewModel.coh
        val esc = viewModel.esc
        val userName = viewModel.userName
        val password = viewModel.password
        val baptismal = viewModel.baptismalCert
        val confirmCert = viewModel.confirmationCert
        val nso = viewModel.nso
        val marriageCert = viewModel.marriageCert
        val brgyCert = viewModel.brgyResCert
        val indigency = viewModel.indigency
        val birForm = viewModel.birForm
        val recommLetter = viewModel.recommLetter
        val parish = viewModel.parish
        val medCert = viewModel.medCert
        val userType = viewModel.userType
        val email = viewModel.email
        val fullName = "${ viewModel.firstName }${viewModel.lastName}"
        val lastName = viewModel.lastName
        val fistName = viewModel.firstName
        val middleName = viewModel.middleName
        val sex = viewModel.sex
        val dateOfBirth = viewModel.dateOfBirth
        val phone = viewModel.phone
        val municipality = viewModel.municipality
        val curriculum = ""
        val entryPeroid = ""
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val barangay = viewModel.barangay
        val studentID = ""



    }

    private fun validateFragmentThree() {
        val lrn = viewModel.lrn
        val attended = viewModel.attended
        val form137 = viewModel.form137
        val diploma = viewModel.diploma
        val tor = viewModel.tor
        val coh = viewModel.coh
        val esc = viewModel.esc

        if (lrn.isEmpty() || attended.isEmpty() || form137 == null || diploma == null ||
            tor == null || coh == null || esc == null) {
            nextItem()
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
            nextItem()
        }
    }


    private fun validateFragmentTwo() {
        val baptismal = viewModel.baptismalCert
        val confirmCert = viewModel.confirmationCert
        val nso = viewModel.nso
        val marriageCert = viewModel.marriageCert
        val brgyCert = viewModel.brgyResCert
        val indigency = viewModel.indigency
        val birForm = viewModel.birForm
        val recommLetter = viewModel.recommLetter
        val parish = viewModel.parish
        val medCert = viewModel.medCert
        if (baptismal == null || confirmCert == null || nso == null || marriageCert == null ||
            brgyCert == null || indigency == null || birForm == null ||
            recommLetter == null || parish.isEmpty() || medCert == null) {
            nextItem()
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
                nextItem()
        }
    }

    private fun validateFragmentOne() {
        val firstName = viewModel.firstName
        val lastName = viewModel.lastName
        val middleName = viewModel.middleName
        val sex = viewModel.sex
        val dateOfBirth = viewModel.dateOfBirth
        val email = viewModel.email
        val phone = viewModel.phone
        val municipality = viewModel.municipality
        val barangay = viewModel.barangay



        if (firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty() ||
            sex.isEmpty() || dateOfBirth.isEmpty() || email.isEmpty() ||
            phone.isEmpty() || municipality.isEmpty() || barangay.isEmpty()) {
            nextItem()
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
           nextItem()
        }
    }

}