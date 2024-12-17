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
import androidx.viewpager2.widget.ViewPager2
import cn.pedant.SweetAlert.SweetAlertDialog
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
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
            uploadData()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        val lrn = viewModel.lrn
        val attended = viewModel.attended
        val form137 = viewModel.form137 // file
        val diploma = viewModel.diploma // file
        val tor = viewModel.tor // file
        val coh = viewModel.coh // file
        val esc = viewModel.esc // file
        val baptismal = viewModel.baptismalCert // file
        val confirmCert = viewModel.confirmationCert // file
        val nso = viewModel.nso // file
        val marriageCert = viewModel.marriageCert // file
        val brgyCert = viewModel.brgyResCert // file
        val indigency = viewModel.indigency // file
        val birForm = viewModel.birForm // file
        val recommLetter = viewModel.recommLetter // file
        val medCert = viewModel.medCert // file
        val userType = viewModel.userType
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
        val curriculum = ""
        val entryPeriod = ""
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // Convert files to MultipartBody.Part
        val form137Part = createFilePart("form137", form137,requireContext())
        val diplomaPart = createFilePart("diploma", diploma, requireContext())
        val torPart = createFilePart("tor", tor, requireContext())
        val cohPart = createFilePart("coh", coh, requireContext())
        val escPart = createFilePart("esc", esc, requireContext())
        val baptismalPart = createFilePart("baptismalCert", baptismal, requireContext())
        val confirmCertPart = createFilePart("confirmationCert", confirmCert, requireContext())
        val nsoPart = createFilePart("nso", nso, requireContext())
        val marriageCertPart = createFilePart("marriageCert", marriageCert, requireContext())
        val brgyCertPart = createFilePart("brgyResCert", brgyCert, requireContext())
        val indigencyPart = createFilePart("indigency", indigency, requireContext())
        val birFormPart = createFilePart("birForm", birForm, requireContext())
        val recommLetterPart = createFilePart("recommLetter", recommLetter, requireContext())
        val medCertPart = createFilePart("medCert", medCert, requireContext())

        // Convert text data to RequestBody
        val lrnRequestBody = createRequestBody(lrn)
        val emailRequestBody = createRequestBody(email)
        val attendedRequestBody = createRequestBody(attended)
        val userNameRequestBody = createRequestBody(viewModel.userName)
        val passwordRequestBody = createRequestBody(viewModel.password)
        val fullNameRequestBody = createRequestBody(fullName)
        val lastNameRequestBody = createRequestBody(lastName)
        val firstNameRequestBody = createRequestBody(firstName)
        val middleNameRequestBody = createRequestBody(middleName)
        val sexRequestBody = createRequestBody(sex)
        val birthdateRequestBody = createRequestBody(dateOfBirth)
        val phoneRequestBody = createRequestBody(phone)
        val municipalityRequestBody = createRequestBody(municipality)
        val barangayRequestBody = createRequestBody(barangay)
        val curriculumRequestBody = createRequestBody(curriculum)
        val entryPeriodRequestBody = createRequestBody(entryPeriod)
        val createdAtRequestBody = createRequestBody(formattedDate)

        // API call setup
        val apiService = RetrofitInstance.create(StudentService::class.java)
        val call = apiService.uploadStudentData(
            lrnRequestBody, emailRequestBody, attendedRequestBody, userNameRequestBody, passwordRequestBody, fullNameRequestBody,
            lastNameRequestBody, firstNameRequestBody, middleNameRequestBody, sexRequestBody, birthdateRequestBody,
            phoneRequestBody, municipalityRequestBody, barangayRequestBody, curriculumRequestBody, entryPeriodRequestBody,
            createdAtRequestBody,
            form137Part, diplomaPart, torPart, cohPart, escPart, baptismalPart, confirmCertPart, nsoPart,
            marriageCertPart, brgyCertPart, indigencyPart, birFormPart, recommLetterPart, medCertPart
        )

        // Log the request details (request body and files)
        Log.d("UploadData", "LRN: $lrn, FullName: $fullName, Email: $email")
        Log.d("UploadData", "Files: form137=${form137?.path}, diploma=${diploma?.path}, tor=${tor?.path}")

        // Execute the call asynchronously
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Log the response status
                Log.d("UploadData", "Response code: ${response.code()}")
                Log.d("UploadData", "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    Toast.makeText(requireActivity(), "Student data uploaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "Failed to upload data: ${response.message()}", Toast.LENGTH_SHORT).show()
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

    private fun createFilePart(partName: String, fileUri: Uri?, context: Context): MultipartBody.Part? {
        if (fileUri == null) return null

        // Convert the Uri to a File
        val file = getFileFromUri(fileUri, context) ?: return null

        // Create RequestBody from the file
        val fileRequestBody = file.asRequestBody("application/*".toMediaTypeOrNull())

        // Create the MultipartBody.Part using form data
        return MultipartBody.Part.createFormData(partName, file.name, fileRequestBody)
    }
    private fun getFileFromUri(uri: Uri, context: Context): File? {
        // Use content resolver to get the file from Uri
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)

        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()

        return filePath?.let { File(it) }
    }

    private fun createRequestBody(data: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), data)
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