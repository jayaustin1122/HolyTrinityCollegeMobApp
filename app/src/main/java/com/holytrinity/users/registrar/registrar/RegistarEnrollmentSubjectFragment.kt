package com.holytrinity.users.registrar.registrar

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
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistarEnrollmentSubjectBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.model.StudentSolo
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class RegistarEnrollmentSubjectFragment : Fragment() {

    private lateinit var binding: FragmentRegistarEnrollmentSubjectBinding
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter2
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>

    // We'll store the currently selected student's data here for PDF/Print
    private var currentStudent: StudentSolo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistarEnrollmentSubjectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Setup the adapter for your SOA (though it looks like we might not be using it heavily now)
        val soaList: List<Soa> = emptyList()
        soaAdapter = SoaAdapter2(soaList, mutableMapOf(), false)

        // Search bar filters the adapter
        binding.searchStudentTextView.addTextChangedListener { text ->
            soaAdapter.filter(text.toString())
        }

        // By default, hide the student info layout and PDF layout
        binding.studentInfoLayout.visibility = View.GONE
        binding.pdfLayout.visibility = View.GONE

        // Fetch all students (for the autocomplete dropdown)
        getAllStudents()

        // Set click listener on the PDF layout (the card/button that user taps to do PDF or print)
        binding.pdfChip.setOnClickListener {
            // We only proceed if we have a currentStudent
            if (currentStudent == null) {
                return@setOnClickListener
            }
            // Show SweetAlert for PDF or Print
            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Options")
                .setContentText("Download PDF or Print?")
                .setConfirmText("Download PDF")
                .setCancelText("Print")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    exportStudentSubjectsToPdf(currentStudent!!)
                }
                .setCancelClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    printStudentSubjects(currentStudent!!)
                }
                .show()
        }
    }

    // ---------------------------------------------------------------------
    // 1) getAllStudents(): Fills the autocomplete with all students
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
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }

    // ---------------------------------------------------------------------
    // 2) setupAutoCompleteTextView(): Allows user to pick a student name
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
                getStudent(selectedStudent.student_id.toString())
            }
        }
    }

    // ---------------------------------------------------------------------
    // 3) getStudent(): Fetches the details (StudentSolo) of a selected student
    // ---------------------------------------------------------------------
    private fun getStudent(studentId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudent(studentId).enqueue(object : Callback<StudentSolo> {
            @SuppressLint("MissingInflatedId")
            override fun onResponse(call: Call<StudentSolo>, response: Response<StudentSolo>) {
                if (response.isSuccessful) {
                    val student = response.body()
                    if (student != null) {
                        currentStudent = student

                        // Show student info
                        binding.studentInfoLayout.visibility = View.VISIBLE
                        binding.pdfLayout.visibility = View.VISIBLE

                        binding.studentNameTextView.text = student.student_name
                        binding.studentNumber.text = "${student.student_id}"

                        // Clear previous subject cards (if any)
                        binding.subjectsContainer.removeAllViews()

                        // Display each enrolled subject in a separate CardView
                        student.enrolled_subjects.forEach { subject ->
                            val subjectCardView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.item_subject_card, null)

                            val subjectNameTextView =
                                subjectCardView.findViewById<TextView>(R.id.subjectNameTextView)
                            val subjectInstructorTextView =
                                subjectCardView.findViewById<TextView>(R.id.subjectInstructorTextView)
                            val subjectCodeTextView =
                                subjectCardView.findViewById<TextView>(R.id.subjectCodeTextView)
                            val subjectScheduleTextView =
                                subjectCardView.findViewById<TextView>(R.id.subjectScheduleTextView)
                            val subjectUnitsTextView =
                                subjectCardView.findViewById<TextView>(R.id.subjectUnitsTextView)

                            subjectNameTextView.text = subject.subject_name
                            if (subject.class_info != null) {
                                val instructor = subject.class_info.instructor?.instructor_name ?: "Unknown Instructor"
                                val schedule = subject.class_info.schedule ?: "TBA"
                                subjectInstructorTextView.text = instructor
                                subjectScheduleTextView.text = schedule
                            } else {
                                subjectInstructorTextView.text = "N/A"
                                subjectScheduleTextView.text = "N/A"
                            }

                            subjectCodeTextView.text = subject.subject_id.toString()
                            subjectUnitsTextView.text = subject.subject_units.toString()

                            // Add the subject card to the container
                            binding.subjectsContainer.addView(subjectCardView)
                        }
                    }
                } else {
                    Log.e("Error", "Failed to fetch student details: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StudentSolo>, t: Throwable) {
                Log.e("Error", "Failed to fetch student details: ${t.message}")
            }
        })
    }

    // ---------------------------------------------------------------------
    // 4) exportStudentSubjectsToPdf(): Download as PDF
    // ---------------------------------------------------------------------
    private fun exportStudentSubjectsToPdf(student: StudentSolo) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }

            val pageWidth = pageInfo.pageWidth
            var yPosition = 50f

            // 1) Logo + header lines
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

            // 2) Big title: "Enrolled Subjects"
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            yPosition += 30f
            val titleText = "Enrolled Subjects"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 30f

            // 3) Show Student Info (Name and ID) at the top
            paint.isFakeBoldText = true
            val studentInfo = "Name: ${student.student_name}   |   ID: ${student.student_id}"
            canvas.drawText(studentInfo, 50f, yPosition, paint)
            yPosition += 40f

            // 4) Table headers
            paint.isFakeBoldText = true
            // We'll define columns for: No., Code, Name, Instructor, Schedule, Units
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colCodeX = 50f
            val colNameX = 120f
            val colInstructorX = 260f
            val colScheduleX = 380f
            val colUnitsX = 495f

            canvas.drawText("Code", colCodeX, yPosition, paint)
            canvas.drawText("Name", colNameX, yPosition, paint)
            canvas.drawText("Instructor", colInstructorX, yPosition, paint)
            canvas.drawText("Schedule", colScheduleX, yPosition, paint)
            canvas.drawText("Units", colUnitsX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // Rows
            paint.isFakeBoldText = false
            student.enrolled_subjects.forEach { subj ->
                canvas.drawText(subj.subject_id.toString(), colCodeX, yPosition, paint)
                canvas.drawText(subj.subject_name ?: "", colNameX, yPosition, paint)

                val instructor = subj.class_info?.instructor?.instructor_name ?: "N/A"
                val schedule = subj.class_info?.schedule ?: "N/A"

                canvas.drawText(instructor, colInstructorX, yPosition, paint)
                canvas.drawText(schedule, colScheduleX, yPosition, paint)
                canvas.drawText(subj.subject_units.toString(), colUnitsX, yPosition, paint)
                yPosition += 20f
            }

            pdfDocument.finishPage(page)

            // 6) Save the PDF
            val fileName = "Enrolled_Subjects_${student.student_id}.pdf"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            pdfDocument.close()
            Toast.makeText(requireContext(), "PDF saved to $fileName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("PdfExport", "Error exporting data: ${e.message}")
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------------------------------------------------------------
    // 5) printStudentSubjects(): Uses PrintManager with a custom Adapter
    // ---------------------------------------------------------------------
    private fun printStudentSubjects(student: StudentSolo) {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Enrolled Subjects Print"
        printManager.print(jobName, MyPrintDocumentAdapter(student), null)
    }

    // ---------------------------------------------------------------------
    // 6) MyPrintDocumentAdapter: Re-generates the PDF for printing
    // ---------------------------------------------------------------------
    inner class MyPrintDocumentAdapter(private val studentData: StudentSolo) : PrintDocumentAdapter() {
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
            newAttributes?.let {
                pageHeight = it.mediaSize!!.heightMils / 1000 * 72
                pageWidth = it.mediaSize!!.widthMils / 1000 * 72
            }

            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }

            // One page for simplicity
            totalPages = 1

            if (totalPages > 0) {
                val info = PrintDocumentInfo.Builder("Enrolled_Subjects_${studentData.student_id}.pdf")
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
            destination: android.os.ParcelFileDescriptor?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                return
            }

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }

            var yPosition = 50f

            // Logo + header
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_main_logo)
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)
            canvas.drawBitmap(scaledLogo, 120f, 53f, paint)
            yPosition += 15f

            val headerLines = listOf(
                "HOLY TRINITY COLLEGE SEMINARY",
                "Brgy. Bautista, Labo, Camarines Norte"
            )
            val lineXCenter = (pageWidth.toFloat())
            for (line in headerLines) {
                val lineWidth = paint.measureText(line)
                val lineX = (pageWidth - lineWidth) / 2
                canvas.drawText(line, lineX, yPosition, paint)
                yPosition += 20f
            }

            // Title
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            yPosition += 30f
            val titleText = "Enrolled Subjects"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 30f

            // Student info
            paint.isFakeBoldText = true
            val studentInfo = "Name: ${studentData.student_name}   |   ID: ${studentData.student_id}"
            canvas.drawText(studentInfo, 50f, yPosition, paint)
            yPosition += 40f

            // Table headers
            paint.isFakeBoldText = true
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colCodeX = 50f
            val colNameX = 120f
            val colInstructorX = 260f
            val colScheduleX = 380f
            val colUnitsX = 495f

            canvas.drawText("Code", colCodeX, yPosition, paint)
            canvas.drawText("Name", colNameX, yPosition, paint)
            canvas.drawText("Instructor", colInstructorX, yPosition, paint)
            canvas.drawText("Schedule", colScheduleX, yPosition, paint)
            canvas.drawText("Units", colUnitsX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // Rows
            paint.isFakeBoldText = false
            studentData.enrolled_subjects.forEach { subj ->
                canvas.drawText(subj.subject_id.toString(), colCodeX, yPosition, paint)
                canvas.drawText(subj.subject_name ?: "", colNameX, yPosition, paint)

                val instructor = subj.class_info?.instructor?.instructor_name ?: "N/A"
                val schedule = subj.class_info?.schedule ?: "N/A"

                canvas.drawText(instructor, colInstructorX, yPosition, paint)
                canvas.drawText(schedule, colScheduleX, yPosition, paint)
                canvas.drawText(subj.subject_units.toString(), colUnitsX, yPosition, paint)
                yPosition += 20f
            }

            pdfDocument.finishPage(page)

            // Write to the print spooler
            try {
                destination?.let {
                    val output = FileOutputStream(it.fileDescriptor)
                    pdfDocument.writeTo(output)
                }
            } catch (e: IOException) {
                Log.e("MyPrintDocumentAdapter", "Error writing PDF: ${e.message}")
                callback?.onWriteFailed(e.toString())
                pdfDocument.close()
                return
            }

            pdfDocument.close()
            callback?.onWriteFinished(arrayOf(PageRange(0, 0)))
        }
    }
}
