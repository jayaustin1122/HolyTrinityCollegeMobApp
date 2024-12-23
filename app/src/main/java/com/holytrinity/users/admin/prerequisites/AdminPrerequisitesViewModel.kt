package com.holytrinity.users.admin.prerequisites

import android.util.Log
import androidx.lifecycle.*
import com.holytrinity.api.PrerequisiteService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.model.CurriculumSubject
import com.holytrinity.model.PrerequisiteWithDetails
import com.holytrinity.model.Subjecta
import kotlinx.coroutines.launch

class AdminPrerequisitesViewModel : ViewModel() {

    private val prerequisiteService = RetrofitInstance.create(PrerequisiteService::class.java)

    private val _prerequisites = MutableLiveData<List<PrerequisiteWithDetails>>()
    val prerequisites: LiveData<List<PrerequisiteWithDetails>> get() = _prerequisites

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchPrerequisites() {
        viewModelScope.launch {
            try {
                val prerequisitesResponse = prerequisiteService.getPrerequisites()
                val subjectsResponse = prerequisiteService.getSubjects()
                val curriculumSubjectsResponse = prerequisiteService.getCurriculumSubjects()

                if (prerequisitesResponse.isSuccessful &&
                    subjectsResponse.isSuccessful &&
                    curriculumSubjectsResponse.isSuccessful
                ) {
                    val prerequisitesBody = prerequisitesResponse.body()
                    val subjectsBody = subjectsResponse.body()
                    val curriculumSubjectsBody = curriculumSubjectsResponse.body()

                    if (prerequisitesBody?.status == "success" &&
                        subjectsBody?.status == "success" &&
                        curriculumSubjectsBody?.status == "success"
                    ) {
                        val prerequisites = prerequisitesBody.data
                        val subjects = subjectsBody.data
                        val curriculumSubjects = curriculumSubjectsBody.data

                        // Create maps for easy lookup
                        val subjectMap: Map<String, Subjecta> = subjects.associateBy { it.subject_id }
                        val curriculumMap: Map<String, CurriculumSubject> = curriculumSubjects.associateBy { it.subject_id }

                        // Combine data
                        val prerequisitesWithDetails = prerequisites.map { prereq ->
                            PrerequisiteWithDetails(
                                subjectName = subjectMap[prereq.subject_id]?.name ?: "Unknown Subject",
                                prerequisiteSubjectName = subjectMap[prereq.prerequisite_subject_id]?.name
                                    ?: "Unknown Prerequisite",
                                curriculum = curriculumMap[prereq.subject_id]
                            )
                        }

                        _prerequisites.postValue(prerequisitesWithDetails)
                    } else {
                        _error.postValue("Server returned an error status.")
                    }
                } else {
                    _error.postValue("Failed to fetch data from server.")
                    Log.e("AdminPrerequisitesViewModel", "Response Error: " +
                            "Prerequisites: ${prerequisitesResponse.errorBody()}, " +
                            "Subjects: ${subjectsResponse.errorBody()}, " +
                            "CurriculumSubjects: ${curriculumSubjectsResponse.errorBody()}")
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred: ${e.message}")
                Log.e("AdminPrerequisitesViewModel", "Exception: ", e)
            }
        }
    }
}
