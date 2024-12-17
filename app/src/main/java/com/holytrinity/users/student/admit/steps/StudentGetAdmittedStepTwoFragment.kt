package com.holytrinity.users.student.admit.steps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepTwoBinding

class StudentGetAdmittedStepTwoFragment : Fragment() {
    private lateinit var binding: FragmentStudentGetAdmittedStepTwoBinding
    private lateinit var viewModel: ViewModelAdmit

    private var currentFileType: FileType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentGetAdmittedStepTwoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelAdmit::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle click for Baptismal Certificate
        binding.editBaptismalCertificate.setOnClickListener {
            currentFileType = FileType.BAPTISMAL_CERT
            getFile()
        }

        // Handle click for BIR Form
        binding.editBirForm.setOnClickListener {
            currentFileType = FileType.BIR_FORM
            getFile()
        }
        binding.editParish.addTextChangedListener {
            viewModel.parish = it.toString()
        }
        binding.editConfirmationCertificate.setOnClickListener {
            currentFileType = FileType.CONFIMATION_CERTIFICATE
            getFile()
        }
        binding.editNsoCertificate.setOnClickListener {
            currentFileType = FileType.NSO
            getFile()
        }
        binding.editMarriageCertificate.setOnClickListener {
            currentFileType = FileType.MARRIAGE_CERTIFICATE
            getFile()
        }
        binding.editBrgyResidenceCertificate.setOnClickListener {
            currentFileType = FileType.BRGY_RESIDENCE_CERTIFICATE
            getFile()
        }
        binding.editCertificateOfIndigency.setOnClickListener {
            currentFileType = FileType.CERTIFICATE_OF_INDIGENCY
            getFile()
        }
        binding.editRecommendationLetter.setOnClickListener {
            currentFileType = FileType.RECOMMENDATION_LETTER
            getFile()
        }
        binding.editMedicalCertificate.setOnClickListener {
            currentFileType = FileType.MEDICAL_CERTIFICATE
            getFile()
        }
    }

    // Activity result launcher for picking a file
    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri: Uri? = result.data?.data
                selectedFileUri?.let { uri ->
                    when (currentFileType) {
                        FileType.BAPTISMAL_CERT -> {
                            viewModel.baptismalCert = uri
                            Log.d("ViewModelUpdate", "Batismal set in ViewModel: ${uri.path}")
                        }

                        FileType.BIR_FORM -> {
                            viewModel.birForm = uri

                        }

                        FileType.CONFIMATION_CERTIFICATE -> {
                            viewModel.confirmationCert = uri

                        }
                        FileType.NSO -> {
                            viewModel.nso = uri
                        }
                        FileType.MARRIAGE_CERTIFICATE -> {
                            viewModel.marriageCert = uri
                        }
                        FileType.BRGY_RESIDENCE_CERTIFICATE -> {
                            viewModel.brgyResCert = uri
                        }
                        FileType.CERTIFICATE_OF_INDIGENCY -> {
                            viewModel.indigency = uri
                        }
                        FileType.RECOMMENDATION_LETTER -> {
                            viewModel.recommLetter = uri
                        }
                        FileType.MEDICAL_CERTIFICATE -> {
                            viewModel.medCert = uri
                        }
                        else -> {
                            // Handle other cases if needed
                        }
                    }
                }
            }
        }

    // Function to pick a file from the file picker
    private fun getFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Allow all file types
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/pdf"
                )
            )
        }
        pickFileLauncher.launch(intent)
    }

    // Enum to differentiate the type of file being picked
    enum class FileType {
        BAPTISMAL_CERT,
        BIR_FORM,
        CONFIMATION_CERTIFICATE,
        NSO,
        MARRIAGE_CERTIFICATE,
        BRGY_RESIDENCE_CERTIFICATE,
        CERTIFICATE_OF_INDIGENCY,
        RECOMMENDATION_LETTER,
        MEDICAL_CERTIFICATE
    }
}
