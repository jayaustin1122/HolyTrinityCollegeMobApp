package com.holytrinity.users.student.admit.steps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepTwoBinding
import java.io.File

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
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val context = context ?: return fileName  // Ensure context is not null
        if (uri.scheme.equals("content")) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        fileName = it.getString(columnIndex)
                    }
                }
                it.close()
            }
        } else if (uri.scheme.equals("file")) {
            fileName = File(uri.path!!).name  // For file URI, get file name from path
        }
        return fileName
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri: Uri? = result.data?.data
                selectedFileUri?.let { uri ->
                    val fileName = getFileNameFromUri(uri) // Get the file name
                    when (currentFileType) {
                        FileType.BAPTISMAL_CERT -> {
                            viewModel.baptismalCert = uri
                            binding.editBaptismalCertificate.setText(fileName)  // Set file name instead of URI path
                            Log.d("ViewModelUpdate", "Baptismal set in ViewModel: $fileName")
                        }

                        FileType.BIR_FORM -> {
                            viewModel.birForm = uri
                            binding.editBirForm.setText(fileName)
                        }

                        FileType.CONFIMATION_CERTIFICATE -> {
                            viewModel.confirmationCert = uri
                            binding.editConfirmationCertificate.setText(fileName)
                        }
                        FileType.NSO -> {
                            viewModel.nso = uri
                            binding.editNsoCertificate.setText(fileName)
                        }
                        FileType.MARRIAGE_CERTIFICATE -> {
                            viewModel.marriageCert = uri
                            binding.editMarriageCertificate.setText(fileName)
                        }
                        FileType.BRGY_RESIDENCE_CERTIFICATE -> {
                            viewModel.brgyResCert = uri
                            binding.editBrgyResidenceCertificate.setText(fileName)
                        }
                        FileType.CERTIFICATE_OF_INDIGENCY -> {
                            viewModel.indigency = uri
                            binding.editCertificateOfIndigency.setText(fileName)
                        }
                        FileType.RECOMMENDATION_LETTER -> {
                            viewModel.recommLetter = uri
                            binding.editRecommendationLetter.setText(fileName)
                        }
                        FileType.MEDICAL_CERTIFICATE -> {
                            viewModel.medCert = uri
                            binding.editMedicalCertificate.setText(fileName)
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
