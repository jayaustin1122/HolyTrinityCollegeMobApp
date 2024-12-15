package com.holytrinity.api.sample

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentSampleBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SampleFragment : Fragment() {
    private lateinit var binding: FragmentSampleBinding
    private val CAMERA_PERMISSION_CODE = 101
    private val IMAGE_PICK_GALLERY_CODE = 102
    private val IMAGE_PICK_CAMERA_CODE = 103
    private var selectedImageUri: Uri? = null
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSampleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addimage.setOnClickListener {
            showImagePickerDialog()
        }


        binding.uploadFile.setOnClickListener {
            getFile()
        }

        fetchAllBooks()
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
            selectedFileUri?.let { uri ->
                binding.upload.setOnClickListener {
                    val bookTitle = binding.bookTitle.text.toString().trim()
                    val content = binding.bookcontent.text.toString().trim()

                    if (selectedImageUri != null) {
                        uploadData(bookTitle, content, selectedImageUri!!, selectedFileUri)
                    } else {
                        // Handle case when no image is selected
                    }
                }

            }
        }
    }
    private fun getFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Allow all file types
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf"))
        }
        pickFileLauncher.launch(intent)
    }


    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Image From")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (checkCameraPermission()) {
                            // Handle Camera functionality (not implemented in this code)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                selectedImageUri = data?.data
                binding.addimage.setImageURI(selectedImageUri)
            }
        }
    }

    private fun uploadData(bookTitle: String, content: String, imageUri: Uri, fileUri: Uri?) {
        // Convert bookTitle and content to RequestBody
        val bookTitleRequestBody = bookTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentRequestBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert Uri to File
        val imageFile = getFileFromUri(requireContext(), imageUri)
        val selectedFile = fileUri?.let { getFileFromUri(requireContext(), it) }

        if (imageFile != null && selectedFile != null) {
            // Convert File to RequestBody for image
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("cover_image", imageFile.name, imageRequestBody)

            // Convert File to RequestBody for the selected file (PDF/Word)
            val fileRequestBody = selectedFile.asRequestBody("application/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", selectedFile.name, fileRequestBody)

            // Make the API call using Retrofit
            val service = RetrofitInstance.create(BookApiService::class.java)
            val call = service.uploadBook(bookTitleRequestBody, contentRequestBody, imagePart, filePart)

            call.enqueue(object : Callback<BookResponse> {
                override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.id != null) {
                            Log.d("SampleFragment", "Book uploaded successfully: ${responseBody.id}")
                        } else {
                            Log.d("SampleFragment", "Book uploaded but no ID returned: $responseBody")
                        }
                    } else {
                        Log.e("SampleFragment", "Error uploading book: ${response.message()}")
                        try {
                            val errorResponse = response.errorBody()?.string()
                            Log.e("SampleFragment", "Error body: $errorResponse")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                    Log.d("SampleFragment", "Failed to upload book: ${t.message}")
                }
            })
        } else {
            Log.d("SampleFragment", "Error: One of the files (image or selected file) is null")
        }
    }



    private fun fetchAllBooks() {
        val service = RetrofitInstance.create(BookApiService::class.java)
        val call = service.getBooks()

        call.enqueue(object : Callback<BooksResponse> {
            override fun onResponse(
                call: Call<BooksResponse>,
                response: Response<BooksResponse>
            ) {
                if (response.isSuccessful) {
                    val books = response.body()?.books
                    Log.d("SampleFragment", "Books fetched: $books")
                } else {
                    Log.d("SampleFragment", "Error fetching books: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.d("SampleFragment", "Failed to fetch books: ${t.message}")
            }
        })
    }

}
