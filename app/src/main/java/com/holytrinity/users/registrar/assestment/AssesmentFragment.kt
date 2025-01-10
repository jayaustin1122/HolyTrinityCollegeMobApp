package com.holytrinity.users.registrar.assestment

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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.*
import com.holytrinity.databinding.FragmentAssesmentBinding
import com.holytrinity.model.EnrollmentResponse
import com.holytrinity.model.Fees
import com.holytrinity.model.Student
import com.holytrinity.model.StudentLedger
import com.holytrinity.users.registrar.adapter.FeesAdapter
import com.holytrinity.util.SharedPrefsUtil
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AssesmentFragment : Fragment() {

    private lateinit var binding: FragmentAssesmentBinding
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var feesAdapter: FeesAdapter
    private var totalAmount = 0.0

    // Student info
    private var studentID: String = ""
    private var enrollment_period_id: String = ""
    private var yearLevel: String = ""
    private var curr_id: String = ""

    // Our main fees container
    private var allFeesList: MutableList<Fees> = mutableListOf()
    private var feesFetched = false
    private var enrolledSubsFetched = false

    private lateinit var loadingDialog: SweetAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssesmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feesAdapter = FeesAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = feesAdapter

        // Initially hide the assessment UI until we pick a student
        binding.studentInfoLayout.visibility = View.GONE
        binding.assessmentCardView.visibility = View.GONE
        binding.nextButton.visibility = View.GONE

        getAllStudents()
        val roleId = UserPreferences.getRoleId(requireContext())
        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(requireActivity(), "Confirm Exit", "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()

                navigateBasedOnRole(roleId)
            }
        }

        fetchFees()

        // Enrolled Subjects (once we know the student's info)
        getEnrolledSubs()

        binding.nextButton.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            addStudentLedgerToApi(
                studentID.toInt(), enrollment_period_id.toInt(),
                totalAmount, totalAmount
            )
        }

        // PDF Layout (the clickable row that triggers PDF/Print)
        binding.pdfLayout.setOnClickListener {
            if (studentID.isNotEmpty() && allFeesList.isNotEmpty()) {
                // Show a SweetAlert with two options: PDF or Print
                SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Options")
                    .setContentText("Download PDF or Print?")
                    .setConfirmText("Download PDF")
                    .setCancelText("Print")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation()
                        exportAssessmentToPdf()
                    }
                    .setCancelClickListener { dialog ->
                        dialog.dismissWithAnimation()
                        printAssessment()
                    }
                    .show()
            } else {
                Toast.makeText(requireContext(), "No fees to export", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 1) addStudentLedgerToApi(): Example logic
     */
    private fun addStudentLedgerToApi(
        studentId: Int,
        enrollmentPeriodId: Int,
        totalDue: Double,
        balance: Double
    ) {
        val studentLedger = RetrofitInstance.create(StudentService::class.java)
        studentLedger.addStudentLedger(studentId, enrollmentPeriodId, totalDue, balance)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    loadingDialog.dismiss()
                    if (response.isSuccessful) {
                        DialogUtils.showSuccessMessage(
                            requireActivity(),
                            "Success",
                            "Student Assessment Complete"
                        )
                        findNavController().navigate(R.id.registrarDrawerHolderFragment)
                    } else {
                        Log.e("Retrofit", "Failed to add student ledger: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    loadingDialog.dismiss()
                    Log.e("Retrofit", "Error: ${t.message}")
                }
            })
    }

    private fun getEnrolledSubs() {
        val service = RetrofitInstance.create(EnrollmentService::class.java)

        if (yearLevel.isBlank()) {
            Log.e("getEnrolledSubs", "Year level is empty or invalid")
            return
        }
        val semester = SharedPrefsUtil.getSelectedPeriod(requireContext())
        if (semester != null) {
            Log.d(
                "SavedPeriod", "Year: ${semester.enrollment_period_id}, Semester: ${semester.semester}, " +
                        "Start Date: ${semester.start_date}, End Date: ${semester.end_date}"
            )
            val yearLevelString = getYearLevelString(yearLevel)
            service.enrollFlow(
                curr_id.toIntOrNull() ?: 0,
                yearLevelString,
                enrollment_period_id.toIntOrNull() ?: 0,
                studentID.toIntOrNull() ?: 0,semester.semester
            ).enqueue(object : Callback<EnrollmentResponse> {
                override fun onResponse(
                    call: Call<EnrollmentResponse>,
                    response: Response<EnrollmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val enrollmentResponse = response.body()
                        if (enrollmentResponse != null) {
                            val subjects = enrollmentResponse.subjects ?: emptyList()
                            val enrolledFeesList = subjects.map { subject ->
                                val title = "${subject.name} (${subject.units} units)"
                                // Example: 250 per unit
                                val amount = subject.units * 250.0
                                Fees(
                                    fee_id = null,
                                    title = title,
                                    amount = amount,
                                    description = title,
                                    data = null
                                )
                            }

                            allFeesList.addAll(enrolledFeesList)
                            enrolledSubsFetched = true
                            checkAndUpdateAdapter()
                        } else {
                            Log.e("getEnrolledSubs", "EnrollmentResponse is null")
                        }
                    } else {
                        Log.e(
                            "API_ERROR",
                            "Error fetching subjects: ${response.message()} | Code: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<EnrollmentResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to connect", t)
                }
            })
        } else {
            Log.d("SavedPeriod", "No saved period found.")
            return
        }

    }

    /**
     * 3) fetchFees(): Additional fees. Add them to the same container (allFeesList).
     */
    private fun fetchFees() {
        val feesService = RetrofitInstance.create(FeesService::class.java)
        feesService.getStudentFees().enqueue(object : Callback<Fees> {
            override fun onResponse(call: Call<Fees>, response: Response<Fees>) {
                if (response.isSuccessful && response.body() != null) {
                    val fees = response.body()!!.data ?: emptyList()
                    allFeesList.addAll(fees)
                    feesFetched = true
                    checkAndUpdateAdapter()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch fees",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Fees>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * 4) checkAndUpdateAdapter(): Wait until we've fetched both enrolledSubs and fees
     */
    private fun checkAndUpdateAdapter() {
        if (feesFetched && enrolledSubsFetched) {
            feesAdapter.submitList(allFeesList)
            calculateTotalAmount(allFeesList)
        }
    }

    private fun calculateTotalAmount(fees: List<Fees>) {
        totalAmount = fees.sumOf { it.amount }
        binding.totalAmountTextView.text = totalAmount.toString()
    }

    private fun getYearLevelString(yearLevel: String): String {
        return when (yearLevel.toIntOrNull()) {
            1 -> "1st Year"
            2 -> "2nd Year"
            3 -> "3rd Year"
            4 -> "4th Year"
            else -> "Unknown Year"
        }
    }

    /**
     * 5) getAllStudents(): For searching & picking a student
     */
    private fun getAllStudents(studentId: Int? = null, registrationVerified: Int? = 1) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    studentNamesMap = students.associate { it.student_name!! to it.student_id.toString() }
                        .toMutableMap()
                    setupAutoCompleteTextView()
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }

    private fun setupAutoCompleteTextView() {
        val studentNamesList = studentNamesMap.keys.toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            studentNamesList
        )
        binding.useridTextView.setAdapter(adapter)

        binding.useridTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                studentID = selectedStudent.student_id.toString()
                enrollment_period_id = selectedStudent.entry_period_id.toString()
                yearLevel = selectedStudent.level.toString()
                curr_id = selectedStudent.curriculum_id.toString()

                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name.toString()

                // Show the main UI
                binding.studentInfoLayout.visibility = View.VISIBLE
                binding.assessmentCardView.visibility = View.VISIBLE
                binding.nextButton.visibility = View.VISIBLE
                binding.pdfLayout.visibility = View.VISIBLE

                // Re-fetch the enrolled subjects for the newly selected student
                allFeesList.clear()
                feesFetched = false
                enrolledSubsFetched = false

                fetchFees()
                getEnrolledSubs()
            } else {
                Toast.makeText(requireContext(), "Student not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ------------------------------------------------------------------------
    // 6) Export to PDF: Show Student Info at top, list Fees (Particulars) & Amount, then total
    // ------------------------------------------------------------------------
    private fun exportAssessmentToPdf() {
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

            // 2) Big title: "Assessment"
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            yPosition += 30f
            val titleText = "Assessment"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 30f

            // 3) Student info
            paint.isFakeBoldText = true
            val studentInfo = "Name: ${binding.studentName.text}   |   ID: ${binding.studentNumber.text}"
            canvas.drawText(studentInfo, 50f, yPosition, paint)
            yPosition += 40f

            // 4) Table headers: Particulars, Amount
            paint.isFakeBoldText = true
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colParticularsX = 80f
            val colAmountX = 400f

            canvas.drawText("Particulars", colParticularsX, yPosition, paint)
            canvas.drawText("Amount", colAmountX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // 5) Table rows
            paint.isFakeBoldText = false
            allFeesList.forEach { fee ->
                canvas.drawText(fee.title ?: "", colParticularsX, yPosition, paint)
                canvas.drawText(String.format("%.2f", fee.amount), colAmountX, yPosition, paint)
                yPosition += 20f
            }

            // 6) Show total at the bottom
            yPosition += 20f
            paint.isFakeBoldText = true
            val totalLabel = "TOTAL: ${String.format("%.2f", totalAmount)}"
            canvas.drawText(totalLabel, colAmountX, yPosition, paint)

            pdfDocument.finishPage(page)

            // 7) Save PDF
            val fileName = "Assessment_${binding.studentNumber.text}.pdf"
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

    // ------------------------------------------------------------------------
    // 7) printAssessment(): Use PrintManager with a custom PrintDocumentAdapter
    // ------------------------------------------------------------------------
    private fun printAssessment() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Assessment Print"
        printManager.print(jobName, MyPrintDocumentAdapter(), null)
    }

    inner class MyPrintDocumentAdapter : PrintDocumentAdapter() {
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

            // We'll assume 1 page for simplicity
            totalPages = 1

            if (totalPages > 0) {
                val info = PrintDocumentInfo.Builder("Assessment_${binding.studentNumber.text}.pdf")
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

            // 1) Logo + header
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

            // 2) Big title: "Assessment"
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            yPosition += 30f
            val titleText = "Assessment"
            val titleWidth = titlePaint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(titleText, titleX, yPosition, titlePaint)
            yPosition += 30f

            // 3) Student info
            paint.isFakeBoldText = true
            val studentInfo = "Name: ${binding.studentName.text}   |   ID: ${binding.studentNumber.text}"
            canvas.drawText(studentInfo, 50f, yPosition, paint)
            yPosition += 40f

            // 4) Table headers
            paint.isFakeBoldText = true
            canvas.drawLine(40f, yPosition - 25f, pageWidth - 40f, yPosition - 25f, paint)

            val colParticularsX = 80f
            val colAmountX = 400f

            canvas.drawText("Particulars", colParticularsX, yPosition, paint)
            canvas.drawText("Amount", colAmountX, yPosition, paint)

            yPosition += 20f
            canvas.drawLine(40f, yPosition, pageWidth - 40f, yPosition, paint)
            yPosition += 25f

            // 5) Table rows
            paint.isFakeBoldText = false
            allFeesList.forEach { fee ->
                canvas.drawText(fee.title ?: "", colParticularsX, yPosition, paint)
                canvas.drawText(String.format("%.2f", fee.amount), colAmountX, yPosition, paint)
                yPosition += 20f
            }

            // 6) Show total
            yPosition += 20f
            paint.isFakeBoldText = true
            val totalLabel = "TOTAL: ${String.format("%.2f", totalAmount)}"
            canvas.drawText(totalLabel, colAmountX, yPosition, paint)

            pdfDocument.finishPage(page)

            // 7) Write to print spooler
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

    private fun navigateBasedOnRole(roleId: Int) {
        when (roleId) {
            1 -> findNavController().navigate(R.id.adminDrawerFragment)
            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
            else -> {
                // Default navigation if roleId doesn't match any of the above
                Toast.makeText(
                    requireContext(),
                    "Invalid role, navigating back to dashboard",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.nav_dashboard) // You can replace this with a default fragment
            }
        }
    }
}
