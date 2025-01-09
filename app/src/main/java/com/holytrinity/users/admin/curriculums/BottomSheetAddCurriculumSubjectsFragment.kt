package com.holytrinity.users.admin.curriculums

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.api.AddSubjectRequest
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.Subject
import com.holytrinity.databinding.FragmentBottomSheetAddCurriculumSubjectsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddCurriculumSubjectsFragment : BottomSheetDialogFragment() {
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentBottomSheetAddCurriculumSubjectsBinding
    private val apiService: CurriculumService by lazy {
        RetrofitInstance.create(CurriculumService::class.java)
    }

    // Para ma-notify natin ang parent fragment (AdminCurriculumDetailsFragment) pag na-dismiss
    var onDismissListener: (() -> Unit)? = null

    private var curriculumId: Int = 0

    // Ito ang listahan ng lahat ng subjects (mula sa API).
    private var subjectList: List<Subject> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetAddCurriculumSubjectsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        curriculumId = arguments?.getInt("curriculum_id", 0) ?: 0


        loadSubjectSpinner()
        loadYearSpinner()
        loadSemesterSpinner()


        binding.doneButton.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                addSubjectToCurriculum()
            }, 2000)
        }
    }

    private fun loadSubjectSpinner() {
        // Kunin lahat ng subjects (halimbawa lang, adjust as needed)
        apiService.getAllSubjects("getAllSubjects").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Ito ang field name na ginamit natin: subjectData
                    val allSubs = response.body()?.subject ?: emptyList()
                    // Convert to List<Subject>
                    // Note: subjectData ay List<Subject> din, kaya direkta natin ma-cast (depende sa iyong ApiResponse).
                    subjectList = allSubs.map {
                        Subject(
                            subject_id = it.subject_id,
                            code = it.code,
                            name = it.name,
                            units = it.units
                        )
                    }

                    // Ipakita sa Spinner ang code (o name)
                    val subjectCodes = subjectList.map { "${it.code} - ${it.name}" }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        subjectCodes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.subjectSpinner.adapter = adapter
                } else {

                    Toast.makeText(
                        context,
                        "Failed to load subjects",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {

                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadYearSpinner() {
        val years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = adapter
    }

    private fun loadSemesterSpinner() {
        val semesters = listOf("1st Sem", "2nd Sem")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, semesters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.semesterSpinner.adapter = adapter
    }

    private fun addSubjectToCurriculum() {
        // Kunin ang napiling subject index mula subjectList
        val selectedSubjectIndex = binding.subjectSpinner.selectedItemPosition
        if (selectedSubjectIndex < 0 || selectedSubjectIndex >= subjectList.size) {
            Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
            return
        }

        // Kunin ang napiling Subject
        val selectedSubject = subjectList[selectedSubjectIndex]

        // Kunin ang year level (1,2,3,4) mula spinner text
        val selectedYearString = binding.yearSpinner.selectedItem as String? ?: ""
        // Kunin ang semester (1st Sem o 2nd Sem)
        val selectedSemString = binding.semesterSpinner.selectedItem as String? ?: ""
        // Halimbawa: store as "1st Sem" / "2nd Sem" (pwede mo rin gawing numeric kung gusto mo)
        val semValue = selectedSemString

        val request = AddSubjectRequest(
            curriculum_id = curriculumId,
            subject_id = selectedSubject.subject_id,
            recommended_year_level = selectedYearString,
            recommended_sem = semValue
        )

        apiService.addSubjectToCurriculum(request).enqueue(object : Callback<ApiResponse>  {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Subject added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // I-dismiss ang bottom sheet
                    dismiss()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to add subject",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog.dismiss()
        // Tawagin ang onDismissListener kung meron
        onDismissListener?.invoke()
    }
}
