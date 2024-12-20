package com.holytrinity.users.registrar.admisson

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RegistrarAdmissionService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistrarAdmissionDetailsBinding
import com.holytrinity.model.Student
import retrofit2.Call
import retrofit2.Callback

class RegistrarAdmissionDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarAdmissionDetailsBinding
    private var studentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarAdmissionDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentId = arguments?.getString("studentId")

        // Set up the back button functionality
        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_admission)
            }
            findNavController().navigate(R.id.registrarAdmissionFragment, bundle)
        }

        // Fetch student admission data
        studentId?.let {
            getStudentAdmission(it)
        }

        binding.btnAdmit.setOnClickListener {
            updateStudentAdmission(studentId, "approve")
        }

        binding.btnDeny.setOnClickListener {
            updateStudentAdmission(studentId, "deny")
        }


    }
    private fun updateStudentAdmission(studentId: String?, action: String) {
        if (studentId == null) {
            Toast.makeText(requireContext(), "Student ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val studentService = RetrofitInstance.create(RegistrarAdmissionService::class.java)
        studentService.updateStudentAdmission(studentId, action).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    val message = if (action == "approve") {
                        "Admission Approved"
                    } else {
                        "Admission Denied"
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.registrarAdmissionFragment)
                } else {
                    Log.e("Error", "Failed to update admission: ${response.code()}")
                    Toast.makeText(requireContext(), "Error updating admission", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error", "Failed to update admission: ${t.message}")
                Toast.makeText(requireContext(), "Error updating admission", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getStudentAdmission(studentId: String) {
        val studentService = RetrofitInstance.create(RegistrarAdmissionService::class.java)
        studentService.getStudentAdmission(studentId).enqueue(object : Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    val students = response.body()
                    if (!students.isNullOrEmpty()) {
                        val student = students[0]
                        student?.let {
                            student?.let {
                                // Set the data to the UI components
                                binding.firstName.setText(it.first_name ?: "Unknown")
                                binding.middleName.setText(it.middle_name ?: "Unknown")
                                binding.lastName.setText(it.last_name ?: "Unknown")
                                binding.sexDropdown.setText(it.gender ?: "Unknown", false)
                                binding.dateOfBirth.setText(it.birthdate ?: "Unknown")
                                binding.email.setText(it.email ?: "Unknown")
                                binding.phone.setText(it.phone ?: "Unknown")
                                binding.municipalityDropdown.setText(it.line1 ?: "Unknown")
                                binding.barangayDropdown.setText(it.line2 ?: "Unknown")
                                binding.editParish.setText(it.parish ?: "Unknown")
                                binding.inputLRN.setText(it.learner_ref_no ?: "Unknown")

                                // Set document filenames by filtering through the documents list
                                val confirmCertDoc = it.documents?.find { doc -> doc.doc_type == "confirmCert" }
                                binding.editConfirmationCertificate.setText(confirmCertDoc?.file_name ?: "Not Provided")

                                val baptismalCertDoc = it.documents?.find { doc -> doc.doc_type == "baptismal" }
                                binding.editBaptismalCertificate.setText(baptismalCertDoc?.file_name ?: "Not Provided")

                                val birForm = it.documents?.find { doc -> doc.doc_type == "birForm" }
                                binding.editBirForm.setText(birForm?.file_name ?: "Not Provided")

                                val diploma = it.documents?.find { doc -> doc.doc_type == "diploma" }
                                binding.inputDiploma.setText(diploma?.file_name ?: "Not Provided")



                                val esc = it.documents?.find { doc -> doc.doc_type == "esc" }
                                binding.inputEsc.setText(esc?.file_name ?: "Not Provided")

                                val nso = it.documents?.find { doc -> doc.doc_type == "nso" }
                                binding.editNsoCertificate.setText(nso?.file_name ?: "Not Provided")

                                val marriageCert = it.documents?.find { doc -> doc.doc_type == "marriageCert" }
                                binding.editMarriageCertificate.setText(marriageCert?.file_name ?: "Not Provided")

                                val brgyCert = it.documents?.find { doc -> doc.doc_type == "brgyCert" }
                                binding.editBrgyResidenceCertificate.setText(brgyCert?.file_name ?: "Not Provided")
                                val indigency = it.documents?.find { doc -> doc.doc_type == "indigency" }
                                binding.editCertificateOfIndigency.setText(indigency?.file_name ?: "Not Provided")

                                val form137 = it.documents?.find { doc -> doc.doc_type == "form137" }
                                binding.inputForm137.setText(form137?.file_name ?: "Not Provided")


                                val recommLetter = it.documents?.find { doc -> doc.doc_type == "recommLetter" }
                                binding.editRecommendationLetter.setText(recommLetter?.file_name ?: "Not Provided")

                                val medCert = it.documents?.find { doc -> doc.doc_type == "medCert" }
                                binding.editMedicalCertificate.setText(medCert?.file_name ?: "Not Provided")

                                val tor = it.documents?.find { doc -> doc.doc_type == "tor" }
                                binding.inputTranscript.setText(tor?.file_name ?: "Not Provided")

                                val coh = it.documents?.find { doc -> doc.doc_type == "coh" }
                                binding.inputDismissalCertificate.setText(coh?.file_name ?: "Not Provided")

                                val attended = it.documents?.find { doc -> doc.doc_type == "attended" }
                                binding.inputSHS.setText(attended?.file_name ?: "Not Provided")

                                // Add click listeners to open the documents
                                binding.editConfirmationCertificate.setOnClickListener {
                                    confirmCertDoc?.let { doc ->
                                        // Navigate to the DocumentViewerFragment with the file path as an argument
                                        val bundle = Bundle().apply {
                                            putString("filePath", doc.file_path)
                                        }
                                        findNavController().navigate(R.id.documentViewerFragment, bundle)
                                    }
                                }

                                binding.editBaptismalCertificate.setOnClickListener {
                                    baptismalCertDoc?.let { doc ->
                                        // Navigate to the DocumentViewerFragment with the file path as an argument
                                        val bundle = Bundle().apply {
                                            putString("filePath", doc.file_path)
                                        }
                                        findNavController().navigate(R.id.documentViewerFragment, bundle)
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e("Error", "No student data found")
                        // Handle the empty state here, e.g., show a message to the user
                    }
                } else {
                    Log.e("Error", "Failed to fetch student details: ${response.code()}")
                }

            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch student details: ${t.message}")
            }
        })
    }



}
