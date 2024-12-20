package com.holytrinity.users.registrar.admisson

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.holytrinity.R
import com.holytrinity.databinding.FragmentDocumentViewerBinding

class DocumentViewerFragment : Fragment() {

    private lateinit var binding: FragmentDocumentViewerBinding
    private var filePath: String? = null
    private val baseUrl = "http://lesterintheclouds.com/crud-android/uploads/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocumentViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filePath = arguments?.getString("filePath")
        Log.d("Path", "$filePath")

        filePath?.let {
            openDocument(it)
        }
    }

    private fun openDocument(filePath: String) {
        val fileUri = Uri.parse(getPublicUrl(filePath))
        val mimeType = getMimeType(fileUri)

        if (mimeType == "application/pdf" || mimeType == "application/msword" || mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
            binding.pdfWebView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.errorMessage.visibility = View.GONE


            binding.pdfWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=$fileUri")
            binding.pdfWebView.setWebViewClient(object : android.webkit.WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                }
            })
        } else {

            binding.pdfWebView.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.errorMessage.visibility = View.VISIBLE

            try {

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, mimeType)
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "No application found to open this file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getMimeType(uri: Uri): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
    }

    private fun getPublicUrl(filePath: String): String {

        val fileName = filePath.substringAfterLast("/crud-android/uploads/")
        return "$baseUrl$fileName".replace(" ", "%20")
    }
}
