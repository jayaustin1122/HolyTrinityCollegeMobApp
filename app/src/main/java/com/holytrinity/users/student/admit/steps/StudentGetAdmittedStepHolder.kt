package com.holytrinity.users.student.admit.steps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.api.sample.getFileFromUri
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepHolderBinding
import com.holytrinity.model.Student
import com.holytrinity.users.student.adapter.StudentAdmitAdapter
import com.shuhart.stepview.StepView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StudentGetAdmittedStepHolder : Fragment() {
    private lateinit var binding: FragmentStudentGetAdmittedStepHolderBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var stepView: StepView
    private lateinit var viewModel: ViewModelAdmit
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var adapter: StudentAdmitAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGetAdmittedStepHolderBinding.inflate(layoutInflater)
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

        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(requireActivity(), "Confirm Exit", "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()

                findNavController().navigate(R.id.signInFragment)
            }
        }

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

                // Hide Back Button on the first page
                if (position == 0) {
                    binding.backButton.visibility = View.INVISIBLE
                } else {
                    binding.backButton.visibility = View.VISIBLE
                }
            }
        })

        binding.backButton.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem = viewPager.currentItem - 1
            }
        }
        binding.btnContinue.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> validateFragmentOne()
                1 -> validateFragmentTwo()
                2 -> validateFragmentThree()
                3 -> validateFragmentFour()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above (API 30+)
            if (!Environment.isExternalStorageManager()) {
                try {
                    // Intent to redirect user to allow 'All Files Access'
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val packageName = requireContext().packageName // Get your app's package name
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the error or fallback to an alternative behavior
                }
            }
        } else {
            // For Android versions < 11 (API 29 and below)
            val REQUEST_CODE = 1
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
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
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            uploadData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        // Retrieve basic form fields
        val userName = viewModel.userName
        val password = viewModel.password
        val lrn = viewModel.lrn
        val attended = viewModel.attended
        val email = viewModel.email
        val fullName = "${viewModel.firstName} ${viewModel.lastName}"
        val lastName = viewModel.lastName
        val firstName = viewModel.firstName
        val middleName = viewModel.middleName
        val sex = viewModel.sex
        val dateOfBirth = viewModel.dateOfBirth
        val phone = viewModel.phone
        val municipality = viewModel.municipality
        val barangay = viewModel.barangay
        val parish = viewModel.parish
        val curriculum = ""  // You can assign values here if needed
        val entryPeriod = "" // You can assign values here if needed
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // Create RequestBody for text fields
        val lrnRequestBody = lrn.toRequestBody("text/plain".toMediaTypeOrNull())
        val parishRequestBody = parish.toRequestBody("text/plain".toMediaTypeOrNull())
        val userTypeRequestBody = "student".toRequestBody("text/plain".toMediaTypeOrNull())
        val userNameRequestBody = userName.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val attendedRequestBody = attended.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val fullNameRequestBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastNameRequestBody = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
        val firstNameRequestBody = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
        val middleNameRequestBody = middleName.toRequestBody("text/plain".toMediaTypeOrNull())
        val sexRequestBody = sex.toRequestBody("text/plain".toMediaTypeOrNull())
        val birthdateRequestBody = dateOfBirth.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneRequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val municipalityRequestBody = municipality.toRequestBody("text/plain".toMediaTypeOrNull())
        val barangayRequestBody = barangay.toRequestBody("text/plain".toMediaTypeOrNull())
        val curriculumRequestBody = curriculum.toRequestBody("text/plain".toMediaTypeOrNull())
        val entryPeriodRequestBody = entryPeriod.toRequestBody("text/plain".toMediaTypeOrNull())
        val studentIDPeriodRequestBody = entryPeriod.toRequestBody("text/plain".toMediaTypeOrNull())
        val createdAtRequestBody = formattedDate.toRequestBody("text/plain".toMediaTypeOrNull())

        // Function to prepare file parts


        // Prepare multipart file parts
        val selectedFileForm137 = prepareFilePart("form137", viewModel.form137)
        val diplomaPart = prepareFilePart("diploma", viewModel.diploma)
        val torPart = prepareFilePart("tor", viewModel.tor)
        val cohPart = prepareFilePart("coh", viewModel.coh)
        val escPart = prepareFilePart("esc", viewModel.esc)
        val baptismalPart = prepareFilePart("baptismal", viewModel.baptismalCert)
        val confirmCertPart = prepareFilePart("confirmCert", viewModel.confirmationCert)
        val nsoPart = prepareFilePart("nso", viewModel.nso)
        val marriageCertPart = prepareFilePart("marriageCert", viewModel.marriageCert)
        val brgyCertPart = prepareFilePart("brgyCert", viewModel.brgyResCert)
        val indigencyPart = prepareFilePart("indigency", viewModel.indigency)
        val birFormPart = prepareFilePart("birForm", viewModel.birForm)
        val recommLetterPart = prepareFilePart("recommLetter", viewModel.recommLetter)
        val medCertPart = prepareFilePart("medCert", viewModel.medCert)

        // API call setup
        val apiService = RetrofitInstance.create(StudentService::class.java)
        val call = apiService.uploadStudentData(
            lrnRequestBody,
            attendedRequestBody,
            selectedFileForm137,
            diplomaPart,
            torPart,
            cohPart,
            escPart,
            userNameRequestBody,
            passwordRequestBody,
            baptismalPart,
            confirmCertPart,
            nsoPart,
            marriageCertPart,
            brgyCertPart,
            indigencyPart,
            birFormPart,
            recommLetterPart,
            parishRequestBody,
            medCertPart,
            userTypeRequestBody,
            emailRequestBody,
            fullNameRequestBody,
            lastNameRequestBody,
            firstNameRequestBody,
            middleNameRequestBody,
            sexRequestBody,
            birthdateRequestBody,
            phoneRequestBody,
            municipalityRequestBody,
            barangayRequestBody,
            curriculumRequestBody,
            entryPeriodRequestBody,
            studentIDPeriodRequestBody,
            createdAtRequestBody,


        )

        // Log the request details (request body and files)
        Log.d("UploadData", "LRN: ${viewModel.form137}, FullName: $fullName, Email: $email")
        Log.d(
            "UploadData",
            "Files: form137=${viewModel.form137?.path}, diploma=${viewModel.diploma?.path}, tor=${viewModel.tor?.path}"
        )

        // Execute the call asynchronously
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Log the response status
                Log.d("UploadData", "Response code: ${response.code()}")
                Log.d("UploadData", "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    loadingDialog.dismiss()
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Student Added"
                    ).show()
                    findNavController().navigate(R.id.studentGetAdmittedFragment)

                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Failed to upload data: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UploadData", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Log the error details
                Log.e("UploadData", "Upload failed: ${t.message}", t)
                Toast.makeText(requireActivity(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun prepareFilePart(partName: String, uri: Uri?): MultipartBody.Part? {
        return uri?.let { fileUri ->
            getFileFromUri(requireContext(), fileUri)?.let { file ->
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(partName, file.name, requestFile)
            }
        }
    }
    private fun validateFragmentThree() {
        val lrn = viewModel.lrn
        val attended = viewModel.attended
        val form137 = viewModel.form137
        val diploma = viewModel.diploma



        if (lrn.isEmpty() || attended.isEmpty() || form137 == null || diploma == null
        ) {
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
            recommLetter == null || parish.isEmpty() || medCert == null
        ) {
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
            phone.isEmpty() || municipality.isEmpty() || barangay.isEmpty()
        ) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
            nextItem()
        }
    }

}