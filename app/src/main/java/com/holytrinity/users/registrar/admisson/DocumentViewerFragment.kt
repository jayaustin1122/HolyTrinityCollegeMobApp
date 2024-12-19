package com.holytrinity.users.registrar.admisson

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocumentViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the file path passed in the arguments
        filePath = arguments?.getString("filePath")

        // If file path exists, open the document
        filePath?.let {
            openDocument(it)
        }
    }

    private fun openDocument(filePath: String) {
        val fileUri = Uri.parse(filePath)
        val mimeType = getMimeType(fileUri)

        // If it's a PDF, load it in WebView
        if (mimeType == "application/pdf") {
            binding.pdfWebView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.errorMessage.visibility = View.GONE

            // Using Google Docs viewer to display the PDF file from URL
            binding.pdfWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=$fileUri")
            binding.pdfWebView.setWebViewClient(object : android.webkit.WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                }
            })
        } else {
            // For other file types (like Word, Excel), try to open them with an external app
            binding.pdfWebView.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.errorMessage.visibility = View.VISIBLE

            try {
                // Create an intent to open the document with a compatible app
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
        // This function determines the MIME type of the file (e.g., application/pdf for PDFs)
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
    }
}
