package com.holytrinity.users.admin.curriculums

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.Subject
import com.holytrinity.databinding.FragmentAdminCurriculumDetailsBinding
import com.holytrinity.users.admin.adapter.CurriculumSubjectAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCurriculumDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAdminCurriculumDetailsBinding
    private val apiService: CurriculumService by lazy {
        RetrofitInstance.create(CurriculumService::class.java)
    }

    private var curriculumId: Int = 0
    private lateinit var subjectsAdapter: CurriculumSubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminCurriculumDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kunin ang curriculum_id na pinasa galing sa bundle
        curriculumId = arguments?.getInt("curriculum_id", 0) ?: 0

        // I-setup ang RecyclerView
        subjectsAdapter = CurriculumSubjectAdapter(
            subjects = emptyList(),
            // OnSubjectClick callback:
            onSubjectClick = { subject ->
                showRemoveSubjectDialog(subject)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = subjectsAdapter

        // I-fetch ang subjects para sa curriculumId
        fetchCurriculumSubjects(curriculumId)

        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminCurriculumsFragment)
            }
        }

        binding.addFabButton.setOnClickListener {
            val bottomSheet = BottomSheetAddCurriculumSubjectsFragment().apply {
                // Ibigay ang curriculumId
                arguments = Bundle().apply {
                    putInt("curriculum_id", curriculumId)
                }
                // Mag-refresh ng list kapag na-dismiss
                onDismissListener = {
                    fetchCurriculumSubjects(curriculumId)
                }
            }
            bottomSheet.show(childFragmentManager, "BottomSheetAddCurriculumSubjectsFragment")
        }

    }

    private fun fetchCurriculumSubjects(curriculumId: Int) {
        apiService.getCurriculumSubjects("getCurriculumSubjects", curriculumId)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        // Halimbawa, nasa "subject" field ang list ng subjects.
                        val subjectList = response.body()?.subject ?: emptyList()
                        // Ipakita ang data sa RecyclerView
                        subjectsAdapter.updateList(subjectList)
                    } else {
                        Toast.makeText(context, "Failed to fetch subjects", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showRemoveSubjectDialog(subject: Subject) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Remove Subject")
            .setContentText("Are you sure you want to remove ${subject.code}? from this curriculum")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                removeSubjectFromCurriculum(subject)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun removeSubjectFromCurriculum(subject: Subject) {
        val requestBody = mapOf(
            "curriculum_id" to curriculumId,
            "subject_id" to subject.subject_id
        )

        apiService.removeSubjectFromCurriculum(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Subject removed successfully", Toast.LENGTH_SHORT).show()
                    // Alisin din sa adapter side:
                    subjectsAdapter.removeItem(subject)
                } else {
                    Toast.makeText(context, "Failed to remove subject", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("AdminCurriculumDetails", "Error removing subject: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}