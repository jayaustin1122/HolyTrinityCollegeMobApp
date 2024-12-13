package com.holytrinity.users.student.admit.steps

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepThreeBinding

class StudentGetAdmittedStepThreeFragment : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedStepThreeBinding
    private lateinit var viewModel: ViewModelAdmit
    private var currentImageType: String = ""
    private val CAMERA_PERMISSION_CODE = 101
    private val IMAGE_PICK_GALLERY_CODE = 102
    private val IMAGE_PICK_CAMERA_CODE = 103

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
        binding.inputLRN.addTextChangedListener {
            viewModel.lrn = it.toString()
        }
        binding.inputSHS.addTextChangedListener {
            viewModel.attended = it.toString()
        }
        binding.inputForm137.setOnClickListener {
            currentImageType = "form137"
            showImagePickerDialog()
        }
        binding.inputDiploma.setOnClickListener {
            currentImageType = "diploma"
            showImagePickerDialog()
        }
        binding.inputTranscript.setOnClickListener {
            currentImageType = "transcript"
            showImagePickerDialog()
        }
        binding.inputDismissalCertificate.setOnClickListener {
            currentImageType = "dismissal"
            showImagePickerDialog()
        }
        binding.inputEsc.setOnClickListener{
            currentImageType = "esc"
            showImagePickerDialog()
        }
    }

    private fun loadUIs() {
        val user = viewModel.userType
        if (user =="College"){
            binding.attended.hint = "SHS Attended"
            binding.diploma.hint = "SHS Diploma"
        }
        else{
            binding.attended.hint = "JHS Attended"
            binding.diploma.hint = "JHS Diploma"
            binding.tor.visibility = View.GONE
            binding.coh.visibility = View.GONE
            binding.esc.visibility = View.VISIBLE
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Image From")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (checkCameraPermission()) {
                            pickImageFromCamera()
                        } else {
                            requestCameraPermission()
                        }
                    }
                    1 -> pickImageFromGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun pickImageFromCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Temp Pic")
            put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        }
        val selectedImage = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, selectedImage)
        }
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null || requestCode == IMAGE_PICK_CAMERA_CODE) {
                val imageUri = selectedImageUri ?: Uri.EMPTY
                setImageInViewModel(imageUri)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setImageInViewModel(imageUri: Uri) {
        when (currentImageType) {
            "form137" -> {
                viewModel.form137 = imageUri
                binding.inputForm137.setText("Added")

            }
            "diploma" -> {
                viewModel.diploma = imageUri
                binding.inputDiploma.setText("Added")

            }
            "transcript" -> {
                viewModel.tor = imageUri
                binding.inputTranscript.setText("Added")
            }
            "dismissal" -> {
                viewModel.coh = imageUri
                binding.inputDismissalCertificate.setText("Added")
            }
            "esc" -> {
                viewModel.esc = imageUri
                binding.inputEsc.setText("Added")
            }


        }
    }
}