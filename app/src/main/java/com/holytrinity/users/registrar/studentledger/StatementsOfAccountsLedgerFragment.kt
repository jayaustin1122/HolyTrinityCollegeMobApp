// StatementsOfAccountsLedgerFragment.kt

package com.holytrinity.users.registrar.studentledger

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.print.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStatementsOfAccountsLedgerBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StatementsOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding: FragmentStatementsOfAccountsLedgerBinding
    private lateinit var soaList: List<Soa>
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter2
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var studentNames: MutableMap<String, String>

    // We'll store the currently selected student's data here for PDF/Print
    private var currentStudent: Student? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatementsOfAccountsLedgerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentNames = mutableMapOf()
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        getAllStudents()

        // Set click listener on the PDF/Print chip
        binding.pdfChip.setOnClickListener {
            // Proceed only if a student is selected
            if (currentStudent == null) {
                Toast.makeText(requireContext(), "Please select a student first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show SweetAlert for PDF or Print options
            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Options")
                .setContentText("Download PDF or Print?")
                .setConfirmText("Download PDF")
                .setCancelText("Print")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    exportStudentSoaToPdf(currentStudent!!)
                }
                .setCancelClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    printStudentSoa(currentStudent!!)
                }
                .show()
        }
    }

    // ---------------------------------------------------------------------
    // 1) Fetch all students for the autocomplete dropdown
    // ---------------------------------------------------------------------
    private fun getAllStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                loadingDialog.dismiss()
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    // Build a map: studentName -> studentID
                    studentNamesMap = students.associate {
                        it.student_name.toString() to it.student_id.toString()
                    }.toMutableMap()
                    setupAutoCompleteTextView()
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to fetch students.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Error", "Failed to fetch students: ${t.message}")
                Toast.makeText(requireContext(), "Failed to fetch students.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ---------------------------------------------------------------------
    // 2) Setup AutoCompleteTextView for student selection
    // ---------------------------------------------------------------------
    private fun setupAutoCompleteTextView() {
        val studentNamesList = studentNamesMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentNamesList)
        binding.searchStudentTextView.setAdapter(adapter)
        binding.searchStudentTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                currentStudent = selectedStudent
                binding.studentInfoLayout.visibility = View.VISIBLE

                binding.linear.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE

                binding.pdfLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name

                // Fetch the SOA for the selected student and update the RecyclerView
                fetchAllSoa(selectedStudent.student_id.toString())
            }
        }
    }

    // ---------------------------------------------------------------------
    // 3) Fetch all SOA records for the selected student
    // ---------------------------------------------------------------------
    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body() ?: emptyList()
                    if (soaList.isNotEmpty()) {
                        // Update the adapter with the new data and refresh the RecyclerView
                        searchStudents()
                    } else {
                        Log.e("No Data", "No SOA records found for student ID: $studentId")
                        Toast.makeText(requireContext(), "No SOA records found for this student.", Toast.LENGTH_SHORT).show()
                    }
                    loadingDialog.dismiss()
                } else {
                    Log.e("Error", "Failed to fetch SOA: ${response.code()}")
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to fetch SOA.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA: ${t.message}")
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to fetch SOA.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ---------------------------------------------------------------------
    // 4) Search and map student names for the adapter
    // ---------------------------------------------------------------------
    private fun searchStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    students.forEach { student ->
                        studentNames[student.student_id.toString()] = student.student_name.toString()
                    }
                    if (::soaAdapter.isInitialized) {
                        soaAdapter.updateSoaList(soaList)
                    } else {
                        setupRecyclerView(soaList)
                    }
                } else {
                    loadingDialog.dismiss()
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to fetch students.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Error", "Failed to fetch students: ${t.message}")
                Toast.makeText(requireContext(), "Failed to fetch students.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ---------------------------------------------------------------------
    // 5) Setup RecyclerView with SOA data
    // ---------------------------------------------------------------------
    private fun setupRecyclerView(soaList: List<Soa>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        soaAdapter = SoaAdapter2(soaList, studentNames, false)
        binding.recyclerView.adapter = soaAdapter
    }

    // ---------------------------------------------------------------------
    // 6) Export Student SOA to PDF
    // ---------------------------------------------------------------------
    private fun exportStudentSoaToPdf(student: Student) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }

            val pageWidth = pageInfo.pageWidth
            var yPosition = 50f

            // 1) Logo
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_main_logo)
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)
            canvas.drawBitmap(scaledLogo, 120f, 53f, paint)

            yPosition += 15f

            val headerLines = listOf(
                "HOLY TRINITY COLLEGE SEMINARY",
                "Brgy. Bautista, Labo, Camarines Norte"
            )
            for (line in headerLines) {
                val lineWidth = paint.measureText(line)
                val lineX = (pageWidth - lineWidth) / 2
                canvas.drawText(line, lineX, yPosition, paint)
                yPosition += 20f
            }

            // 2) Title: "Statement of Accounts"
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            yPosition += 20f
            val titleText = "Statement of Accounts"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 30f

            // 3) Student Information
            paint.isFakeBoldText = true
            val studentInfo = "Name: ${student.student_name}    |    ID: ${student.student_id}"
            canvas.drawText(studentInfo, 50f, yPosition, paint)
            yPosition += 30f

            // 4) SOA Table Headers
            paint.isFakeBoldText = true
            val colIDX = 50f
            val colNameX = 200f
            val colPaidX = 350f
            val colBalanceX = 450f

            canvas.drawText("Student ID", colIDX, yPosition, paint)
            canvas.drawText("Name", colNameX, yPosition, paint)
            canvas.drawText("Paid", colPaidX, yPosition, paint)
            canvas.drawText("Balance", colBalanceX, yPosition, paint)

            yPosition += 20f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(colIDX, yPosition, pageWidth - 50f, yPosition, paint)
            paint.style = Paint.Style.FILL
            yPosition += 20f

            // 5) SOA Data Rows
            paint.isFakeBoldText = false
            soaList.forEach { soa ->
                canvas.drawText(soa.student_id.toString(), colIDX, yPosition, paint)
                canvas.drawText(soa.student_name ?: "N/A", colNameX, yPosition, paint)
                canvas.drawText("₱${soa.total_paid}", colPaidX, yPosition, paint)
                canvas.drawText("₱${soa.balance}", colBalanceX, yPosition, paint)
                yPosition += 20f
            }

            pdfDocument.finishPage(page)

            // 6) Save the PDF
            val fileName = "SOA_${student.student_id}.pdf"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            pdfDocument.close()
            Toast.makeText(requireContext(), "PDF saved to Downloads/$fileName", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            Log.e("PdfExport", "Error exporting data: ${e.message}")
            Toast.makeText(requireContext(), "Failed to export PDF.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("PdfExport", "Unexpected error: ${e.message}")
            Toast.makeText(requireContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------------------------------------------------------------
    // 7) Print Student SOA
    // ---------------------------------------------------------------------
    private fun printStudentSoa(student: Student) {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "SOA_Print_${student.student_id}"
        printManager.print(jobName, MyPrintDocumentAdapter(student, soaList), null)
    }

    // ---------------------------------------------------------------------
    // 8) Custom PrintDocumentAdapter for Printing SOA
    // ---------------------------------------------------------------------
    inner class MyPrintDocumentAdapter(
        private val studentData: Student,
        private val soaData: List<Soa>
    ) : PrintDocumentAdapter() {
        private var pdfDocument: PdfDocument? = null
        private var pageHeight = 0
        private var pageWidth = 0
        private var totalPages = 1

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: Bundle?
        ) {
            pdfDocument = PdfDocument()
            newAttributes?.let {
                pageHeight = it.mediaSize?.heightMils?.div(1000)?.times(72)?.toInt() ?: 842 // Default A4
                pageWidth = it.mediaSize?.widthMils?.div(1000)?.times(72)?.toInt() ?: 595
            }

            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }

            // For simplicity, assuming single page. Adjust as needed.
            totalPages = 1

            if (totalPages > 0) {
                val info = PrintDocumentInfo.Builder("SOA_${studentData.student_id}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages)
                    .build()
                callback?.onLayoutFinished(info, true)
            } else {
                callback?.onLayoutFailed("No content to print.")
            }
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: android.os.ParcelFileDescriptor?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                pdfDocument?.close()
                pdfDocument = null
                return
            }

            try {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                val page = pdfDocument?.startPage(pageInfo) ?: throw IOException("Unable to start page")
                val canvas = page.canvas

                val paint = Paint().apply {
                    textSize = 12f
                    color = Color.BLACK
                }

                var yPosition = 50f

                // 1) Logo
                val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_main_logo)
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)
                canvas.drawBitmap(scaledLogo, 120f, 53f, paint)

                yPosition += 15f

                val headerLines = listOf(
                    "HOLY TRINITY COLLEGE SEMINARY",
                    "Brgy. Bautista, Labo, Camarines Norte"
                )
                for (line in headerLines) {
                    val lineWidth = paint.measureText(line)
                    val lineX = (pageWidth - lineWidth) / 2
                    canvas.drawText(line, lineX, yPosition, paint)
                    yPosition += 20f
                }

                // 2) Title: "Statement of Accounts"
                val titlePaint = Paint().apply {
                    textSize = 18f
                    isFakeBoldText = true
                    color = Color.BLACK
                }
                yPosition += 20f
                val titleText = "Statement of Accounts"
                val titleWidth = titlePaint.measureText(titleText)
                val titleX = (pageWidth - titleWidth) / 2
                canvas.drawText(titleText, titleX, yPosition, titlePaint)
                yPosition += 30f

                // 3) Student Information
                paint.isFakeBoldText = true
                val studentInfo = "Name: ${studentData.student_name}    |    ID: ${studentData.student_id}"
                canvas.drawText(studentInfo, 50f, yPosition, paint)
                yPosition += 30f

                // 4) SOA Table Headers
                paint.isFakeBoldText = true
                val colIDX = 50f
                val colNameX = 200f
                val colPaidX = 350f
                val colBalanceX = 450f

                canvas.drawText("Student ID", colIDX, yPosition, paint)
                canvas.drawText("Name", colNameX, yPosition, paint)
                canvas.drawText("Paid", colPaidX, yPosition, paint)
                canvas.drawText("Balance", colBalanceX, yPosition, paint)

                yPosition += 20f
                paint.style = Paint.Style.STROKE
                canvas.drawLine(colIDX, yPosition, pageWidth - 50f, yPosition, paint)
                paint.style = Paint.Style.FILL
                yPosition += 20f

                // 5) SOA Data Rows
                paint.isFakeBoldText = false
                soaData.forEach { soa ->
                    if (yPosition > pageHeight - 50) {
                        // Handle multiple pages if necessary
                        // For simplicity, we're limiting to one page
                        // You can implement pagination here
                        return@forEach
                    }
                    canvas.drawText(soa.student_id.toString(), colIDX, yPosition, paint)
                    canvas.drawText(soa.student_name ?: "N/A", colNameX, yPosition, paint)
                    canvas.drawText("₱${soa.total_paid}", colPaidX, yPosition, paint)
                    canvas.drawText("₱${soa.balance}", colBalanceX, yPosition, paint)
                    yPosition += 20f
                }

                pdfDocument?.finishPage(page)

                // Write the document content
                pdfDocument?.writeTo(FileOutputStream(destination?.fileDescriptor))

                // Close the document
                pdfDocument?.close()
                pdfDocument = null

                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

            } catch (e: IOException) {
                Log.e("PrintError", "Error writing PDF: ${e.message}")
                callback?.onWriteFailed(e.toString())
                pdfDocument?.close()
                pdfDocument = null
            }
        }
    }
}
