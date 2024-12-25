package com.holytrinity.users.registrar.registrar

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistratEnrollmentListBinding
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.EnrolledAdapter
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileOutputStream

class RegistrarEnrollmentListFragment : Fragment() {
    private lateinit var binding: FragmentRegistratEnrollmentListBinding
    private var filteredStudents: List<Student> = emptyList()
    private lateinit var studentsAdapter: EnrolledAdapter
    private var students: List<Student> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistratEnrollmentListBinding.inflate(layoutInflater)
        studentsAdapter = EnrolledAdapter(
            filteredStudents,
            context = requireContext(),
        ) { studentId ->
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerEnrolledSubjects.layoutManager = LinearLayoutManager(context)
        binding.recyclerEnrolledSubjects.adapter = studentsAdapter
        binding.searchStudentTextView.addTextChangedListener {
            val query = it.toString().trim()
            filterStudents(query)
        }
        getAllStudents()
        // Add click listener for xlsChip
        binding.xlsChip.setOnClickListener {
            exportDataToExcel()
        }

        binding.pdfChip.setOnClickListener {
            exportDataToPdf()
        }

    }

    private fun exportDataToPdf() {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            paint.textSize = 12f
            var yPosition = 50f // Starting Y position

            // ---------------------------------------------------------
            // 1) Print the multi-line header text:
            // ---------------------------------------------------------
            // We'll just do them line by line. Adjust the xPosition if you want them centered differently.
            val headerLines = listOf(
                "   HOLY TRINITY COLLEGE SEMINARY   ",
                "Bautista 4604, Labo Camarines Norte"
            )
            headerLines.forEach { line ->
                canvas.drawText(line, 200.5f, yPosition, paint)
                yPosition += 20f
            }
            // Add spacing before title
            yPosition += 20f

            // ---------------------------------------------------------
            // 2) Title: "Enrolled Students"
            // ---------------------------------------------------------
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("Enrolled Students", 220f, yPosition, paint)

            // Reset paint for table content
            paint.textSize = 12f
            paint.isFakeBoldText = false
            yPosition += 30f // Move down

            // ---------------------------------------------------------
            // 3) Table headers
            // ---------------------------------------------------------
            canvas.drawText("ID", 50f, yPosition, paint)
            canvas.drawText("Name", 150f, yPosition, paint)
            canvas.drawText("Status", 350f, yPosition, paint)
            yPosition += 20f

            // ---------------------------------------------------------
            // 4) Data rows
            // ---------------------------------------------------------
            filteredStudents.forEach { student ->
                canvas.drawText(student.student_id.toString(), 50f, yPosition, paint)
                canvas.drawText(student.student_name ?: "", 150f, yPosition, paint)
                student.official_status?.let { canvas.drawText(it, 350f, yPosition, paint) }
                yPosition += 20f
            }

            pdfDocument.finishPage(page)

            // ---------------------------------------------------------
            // 5) Save the PDF file
            // ---------------------------------------------------------
            val fileName = "Enrolled_Students.pdf"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
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

    private fun exportDataToExcel() {
        try {
            val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook()
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
                row.createCell(2).setCellValue(student.official_status)
            }

            // Manually set column widths
            sheet.setColumnWidth(0, 15 * 256) // ID column
            sheet.setColumnWidth(1, 30 * 256) // Name column
            sheet.setColumnWidth(2, 20 * 256) // Status column

            // Save file
            val fileName = "Enrolled_Students.xlsx"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Enrolled_Students.xlsx")


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



    private fun getAllStudents(studentId: Int? = null, registrationVerified: Int? = null) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    // Set filteredStudents after successful data fetch
                    filteredStudents = students.filter { it.official_status == "Official" }
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


    private fun filterStudents(query: String) {
        filteredStudents = if (query.isEmpty()) {
            students.filter { it.official_status == "Official" }
        } else {
            students.filter {
                it.official_status == "Official" && it.student_name?.contains(query, ignoreCase = true) == true
            }
        }

        studentsAdapter.updateData(filteredStudents)
        binding.countTitleTextView.text = "Count: ${filteredStudents.size}"
    }

}
