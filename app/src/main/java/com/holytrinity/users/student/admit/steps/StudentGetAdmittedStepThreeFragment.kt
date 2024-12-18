package com.holytrinity.users.student.admit.steps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepThreeBinding
import java.io.File

class StudentGetAdmittedStepThreeFragment : Fragment() {
    private lateinit var binding: FragmentStudentGetAdmittedStepThreeBinding
    private lateinit var viewModel: ViewModelAdmit
    private var currentFileType: FileType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGetAdmittedStepThreeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelAdmit::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUIs()

        // Add logs to track changes in the ViewModel
        binding.inputLRN.addTextChangedListener {
            val lrn = it.toString()
            viewModel.lrn = lrn
            Log.d("ViewModelUpdate", "LRN set in ViewModel: $lrn")
        }

        binding.inputSHS.addTextChangedListener {
            val attended = it.toString()
            viewModel.attended = attended
            Log.d("ViewModelUpdate", "Attended set in ViewModel: $attended")
        }

        binding.inputForm137.setOnClickListener {
            currentFileType = FileType.FORM137
            getFile()
        }

        binding.inputDiploma.setOnClickListener {
            currentFileType = FileType.DIPLOMA
            getFile()
        }

        binding.inputTranscript.setOnClickListener {
            currentFileType = FileType.TOR
            getFile()
        }

        binding.inputDismissalCertificate.setOnClickListener {
            currentFileType = FileType.DISMISSCERT
            getFile()
        }

        binding.inputEsc.setOnClickListener {
            currentFileType = FileType.ESC
            getFile()
        }
    }

    private fun loadUIs() {
        val user = viewModel.userType
        if (user == "College") {
            binding.attended.hint = "SHS Attended"
            binding.diploma.hint = "SHS Diploma"
        } else {
            binding.attended.hint = "JHS Attended"
            binding.diploma.hint = "JHS Diploma"
            binding.tor.visibility = View.GONE
            binding.coh.visibility = View.GONE
            binding.esc.visibility = View.VISIBLE
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
    // Activity result launcher for picking a file
    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri: Uri? = result.data?.data
                selectedFileUri?.let { uri ->
                    val fileName = getFileNameFromUri(uri) // Get the file name
                    when (currentFileType) {
                        FileType.FORM137 -> {
                            viewModel.form137 = uri
                            binding.inputForm137.setText(fileName)
                            Log.d("ViewModelUpdate", "Form 137 set in ViewModel: ${uri.path}")
                        }
                        FileType.DIPLOMA -> {
                            viewModel.diploma = uri
                            binding.inputDiploma.setText(fileName)
                            Log.d("ViewModelUpdate", "Diploma set in ViewModel: ${uri.path}")
                        }
                        FileType.TOR -> {
                            viewModel.tor = uri
                            binding.inputTranscript.setText(fileName)
                            Log.d("ViewModelUpdate", "Transcript set in ViewModel: ${uri.path}")
                        }
                        FileType.DISMISSCERT -> {
                            viewModel.coh = uri
                            binding.inputDismissalCertificate.setText(fileName)
                            Log.d("ViewModelUpdate", "Dismissal Certificate set in ViewModel: ${uri.path}")
                        }
                        FileType.ESC -> {
                            viewModel.esc = uri
                            binding.inputEsc.setText(fileName)
                            Log.d("ViewModelUpdate", "ESC set in ViewModel: ${uri.path}")
                        }
                        else -> {
                            Log.w("ViewModelUpdate", "Unhandled file type")
                        }
                    }
                }
            }
        }

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
        FORM137,
        DIPLOMA,
        TOR,
        DISMISSCERT,
        ESC,
    }
}
