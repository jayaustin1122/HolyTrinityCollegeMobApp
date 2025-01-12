package com.holytrinity.users.registrar.registrar

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistratEnrollmentListBinding
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.EnrolledAdapter
import com.holytrinity.users.registrar.admisson.BottomSheetFilterAdmissionFragment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class RegistrarEnrollmentListFragment : Fragment(){

    private lateinit var binding: FragmentRegistratEnrollmentListBinding
    private var filteredStudents: List<Student> = emptyList()
    private lateinit var studentsAdapter: EnrolledAdapter
    private var students: List<Student> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistratEnrollmentListBinding.inflate(layoutInflater)
        studentsAdapter = EnrolledAdapter(
            filteredStudents,
            context = requireContext()
        ) { studentId ->
            // Handle item click if needed
        }
        return binding.root
    }
    private fun applyFilter(year: String?) {
        filterStudentsByLevel(year)
    }

    private fun filterStudentsByLevel(year: String?) {
        // Map year levels to corresponding values
        val level = when (year) {
            "1st Year" -> "1"
            "2nd Year" -> "2"
            "3rd Year" -> "3"
            "4th Year" -> "4"
            else -> null
        }

        // Filter students by level (year) and only include "Official" students
        val filteredList = students.filter {
            (level == null || it.level == level) && it.official_status == "Official"
        }

        // Update RecyclerView with filtered data
        studentsAdapter.updateData(filteredList)
        binding.countTitleTextView.text = "Results: ${filteredList.size}"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.recyclerEnrolledSubjects.layoutManager = LinearLayoutManager(context)
        binding.recyclerEnrolledSubjects.adapter = studentsAdapter

        // Search listener
        binding.searchStudentTextView.addTextChangedListener {
            val query = it.toString().trim()
            filterStudents(query)
        }
        binding.btnFilter.setOnClickListener {
            val bottomSheet = BottomSheetFilterFragment().apply {
                setFilterListener { year ->
                    applyFilter(year)
                }
            }
            bottomSheet.show(childFragmentManager, "BottomSheetFilterFragment")
        }
        // Fetch data
        getAllStudents()

        // XLS button
        binding.xlsChip.setOnClickListener {
            exportDataToExcel()
        }

        // PDF button
        binding.pdfChip.setOnClickListener {
            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Export Options")
                .setContentText("Would you like to download this as a PDF or print it?")
                .setConfirmText("Download PDF")
                .setCancelText("Print")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    exportDataToPdf()  // Download as PDF (saved to device)
                }
                .setCancelClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    printData()        // Actual print function
                }
                .show()
        }
    }

    // ------------------------------------------------------------------------
    // 1) printData(): Uses PrintManager with a custom PrintDocumentAdapter
    // ------------------------------------------------------------------------
    private fun printData() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Enrollment List Print"
        printManager.print(
            jobName,
            MyPrintDocumentAdapter(requireContext(), filteredStudents),
            null
        )
    }

    /**
     * This PrintDocumentAdapter re-generates the PDF in onWrite(), then
     * writes it directly to the print framework's 'destination'.
     */
    inner class MyPrintDocumentAdapter(
        private val context: Context,
        private val studentsToPrint: List<Student>
    ) : PrintDocumentAdapter() {

        private var pageHeight: Int = 0
        private var pageWidth: Int = 0
        private var totalPages = 1

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: Bundle?
        ) {
            // Get print sizes in points
            newAttributes?.let {
                pageHeight = it.mediaSize!!.heightMils / 1000 * 72
                pageWidth = it.mediaSize!!.widthMils / 1000 * 72
            }

            // If cancellation requested
            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }

            // We only have 1 page in this example, but you can compute a real page count if needed
            totalPages = 1

            if (totalPages > 0) {
                val info = PrintDocumentInfo.Builder("EnrollmentList.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages)
                    .build()
                callback?.onLayoutFinished(info, true)
            } else {
                callback?.onLayoutFailed("No pages to print.")
            }
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: ParcelFileDescriptor?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            // Check for cancellation
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                return
            }

            // Create the PDF Document
            val pdfDocument = PdfDocument()

            // We only do 1 page, but if you want multiple pages, you loop here
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            if (cancellationSignal?.isCanceled == true) {
                pdfDocument.close()
                callback?.onWriteCancelled()
                return
            }

            // Let's draw the same PDF content as in exportDataToPdf(), but adapt to pageWidth/pageHeight
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }

            var yPosition = 50f

            // 1) Logo
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_main_logo)
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)
            canvas.drawBitmap(scaledLogo, 120f, 53f, paint)

            yPosition += 15f

            // 2) Header lines
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

            // Title in bigger font
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }

            yPosition += 30f
            val titleText = "Enrollment List"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 50f

            // Table headers
            paint.isFakeBoldText = true
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colNoX = 80f
            val colIdX = 130f
            val colNameX = 250f
            val colStatusX = 390f

            canvas.drawText("No.", colNoX, yPosition, paint)
            canvas.drawText("Student ID", colIdX, yPosition, paint)
            canvas.drawText("Student Name", colNameX, yPosition, paint)
            canvas.drawText("Status", colStatusX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // Table rows
            paint.isFakeBoldText = false
            filteredStudents.forEachIndexed { index, student ->
                canvas.drawText("${index + 1}", colNoX, yPosition, paint)
                canvas.drawText(student.student_id.toString(), colIdX, yPosition, paint)
                canvas.drawText(student.student_name ?: "", colNameX, yPosition, paint)
                student.official_status?.let {
                    canvas.drawText(it, colStatusX, yPosition, paint)
                }
                yPosition += 20f
            }
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)

            pdfDocument.finishPage(page)

            // Write the PDF to the 'destination'
            try {
                destination?.let {
                    val output = FileOutputStream(it.fileDescriptor)
                    pdfDocument.writeTo(output)
                }
            } catch (e: IOException) {
                Log.e("PrintDocumentAdapter", "Error writing PDF: ${e.message}")
                callback?.onWriteFailed(e.toString())
                pdfDocument.close()
                return
            }

            pdfDocument.close()
            callback?.onWriteFinished(arrayOf(PageRange(0, 0)))
        }
    }

    // ------------------------------------------------------------------------
    // 2) exportDataToPdf(): Just saves the PDF to Downloads folder
    // ------------------------------------------------------------------------
    private fun exportDataToPdf() {
        try {
            val pdfDocument = PdfDocument()

            // A4 size: 595 width x 842 height (points)
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }

            val pageWidth = pageInfo.pageWidth
            var yPosition = 50f

            // 1) Draw the logo
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_main_logo)
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)
            canvas.drawBitmap(scaledLogo, 120f, 53f, paint)
            yPosition += 15f

            // 2) Header lines
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

            // Title in bigger font
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }

            yPosition += 30f
            val titleText = "Enrollment List"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 50f

            // Table headers
            paint.isFakeBoldText = true
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colNoX = 80f
            val colIdX = 130f
            val colNameX = 250f
            val colStatusX = 390f

            canvas.drawText("No.", colNoX, yPosition, paint)
            canvas.drawText("Student ID", colIdX, yPosition, paint)
            canvas.drawText("Student Name", colNameX, yPosition, paint)
            canvas.drawText("Status", colStatusX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // Table rows
            paint.isFakeBoldText = false
            filteredStudents.forEachIndexed { index, student ->
                canvas.drawText("${index + 1}", colNoX, yPosition, paint)
                canvas.drawText(student.student_id.toString(), colIdX, yPosition, paint)
                canvas.drawText(student.student_name ?: "", colNameX, yPosition, paint)
                student.official_status?.let {
                    canvas.drawText(it, colStatusX, yPosition, paint)
                }
                yPosition += 20f
            }
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)

            pdfDocument.finishPage(page)

            // Save PDF file
            val fileName = "Enrolled_Students.pdf"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            pdfDocument.close()
            Toast.makeText(requireContext(), "Data exported to $fileName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("PdfExport", "Error exporting data: ${e.message}")
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------------------------------------------------------------
    // 3) exportDataToExcel()
    // ------------------------------------------------------------------------
    private fun exportDataToExcel() {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Enrolled Students")

            // Header row
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("ID")
            headerRow.createCell(1).setCellValue("Name")
            headerRow.createCell(2).setCellValue("Status")

            // Data rows
            filteredStudents.forEachIndexed { index, student ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(student.student_id.toString())
                row.createCell(1).setCellValue(student.student_name ?: "")
                row.createCell(2).setCellValue(student.official_status ?: "")
            }

            // Manually set column widths
            sheet.setColumnWidth(0, 15 * 256)
            sheet.setColumnWidth(1, 30 * 256)
            sheet.setColumnWidth(2, 20 * 256)

            // Save file
            val fileName = "Enrolled_Students.xlsx"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
                workbook.close()
            }

            Toast.makeText(requireContext(), "Data exported to $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ExcelExport", "Error exporting data: ${e.message}")
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------------------------------------------------------------
    // 4) getAllStudents()
    // ------------------------------------------------------------------------
    private fun getAllStudents(studentId: Int? = null, registrationVerified: Int? = null) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    filteredStudents = students.filter {
                        it.official_status == "Official" && (it.balance == null || it.balance == 0)
                    }
                    studentsAdapter.updateData(filteredStudents)
                    binding.countTitleTextView.text = "Results: ${filteredStudents.size}"
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }


    // ------------------------------------------------------------------------
    // 5) filterStudents()
    // ------------------------------------------------------------------------
    private fun filterStudents(query: String) {
        filteredStudents = if (query.isEmpty()) {
            students.filter { it.official_status == "Official" }
        } else {
            students.filter {
                it.official_status == "Official" &&
                        it.student_name?.contains(query, ignoreCase = true) == true
            }
        }
        studentsAdapter.updateData(filteredStudents)
        binding.countTitleTextView.text = "Count: ${filteredStudents.size}"
    }
}
