package com.holytrinity.users.student.admit.steps

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
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
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepTwoBinding
class StudentGetAdmittedStepTwoFragment : Fragment() {
    private lateinit var binding: FragmentStudentGetAdmittedStepTwoBinding
    private lateinit var viewModel: ViewModelAdmit

    private var currentImageType: String = ""
    private val CAMERA_PERMISSION_CODE = 101
    private val IMAGE_PICK_GALLERY_CODE = 102
    private val IMAGE_PICK_CAMERA_CODE = 103

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGetAdmittedStepTwoBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelAdmit::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBaptismalCertificate.setOnClickListener {
            currentImageType = "baptismal"
            showImagePickerDialog()
        }

        binding.editConfirmationCertificate.setOnClickListener {
            currentImageType = "confirmation"
            showImagePickerDialog()
        }
        binding.editNsoCertificate.setOnClickListener {
            currentImageType = "nso"
            showImagePickerDialog()
        }
        binding.editMarriageCertificate.setOnClickListener {
            currentImageType = "marriage"
            showImagePickerDialog()
        }
        binding.editBrgyResidenceCertificate.setOnClickListener {
            currentImageType = "residence"
            showImagePickerDialog()
        }
        binding.editCertificateOfIndigency.setOnClickListener {
            currentImageType = "indigency"
            showImagePickerDialog()
        }
        binding.editBirForm.setOnClickListener {
            currentImageType = "form"
            showImagePickerDialog()
        }
        binding.editRecommendationLetter.setOnClickListener {
            currentImageType = "recommLetter"
            showImagePickerDialog()
        }
        binding.editMedicalCertificate.setOnClickListener {
            currentImageType = "medicalCertificate"
            showImagePickerDialog()
        }
        binding.editParish.addTextChangedListener {
            viewModel.parish = it.toString()
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
            "baptismal" -> {
                viewModel.baptismalCert = imageUri
                Log.d("StudentGetAdmitted", "Image selected: ${viewModel.baptismalCert}")
                binding.editBaptismalCertificate.setText("Added")

            }
            "confirmation" -> {
                viewModel.confirmationCert = imageUri
                binding.editConfirmationCertificate.setText("Added")
                Log.d("StudentGetAdmitted", "Image selected: ${viewModel.confirmationCert}")
            }
            "nso" -> {
                viewModel.nso = imageUri
                binding.editNsoCertificate.setText("Added")
            }
            "marriage" -> {
                viewModel.marriageCert = imageUri
                binding.editMarriageCertificate.setText("Added")
            }
            "residence" -> {
                viewModel.brgyResCert= imageUri
                binding.editBrgyResidenceCertificate.setText("Added")
            }
            "indigency" -> {
                viewModel.indigency= imageUri
                binding.editCertificateOfIndigency.setText("Added")
            }
            "form" -> {
                viewModel.birForm= imageUri
                binding.editBirForm.setText("Added")
            }
            "recommLetter" -> {
                viewModel.recommLetter= imageUri
                binding.editRecommendationLetter.setText("Added")
            }
            "medicalCertificate" -> {
                viewModel.medCert= imageUri
                binding.editMedicalCertificate.setText("Added")
            }

        }
     }
}
