package com.holytrinity.users.registrar.enrollment.enroll

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelEnrollment : ViewModel() {
    private val _studentID = MutableLiveData<String>()
    val studentID: LiveData<String> get() = _studentID

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _dept_id = MutableLiveData<String>()
    val dept_id: LiveData<String> get() = _dept_id

    private val _curr_id = MutableLiveData<String>()
    val curr_id: LiveData<String> get() = _curr_id

    private val _course = MutableLiveData<String>()
    val course: LiveData<String> get() = _course

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _section = MutableLiveData<String>()
    val section: LiveData<String> get() = _section

    private val _classification_of_student = MutableLiveData<String>()
    val classification_of_student: LiveData<String> get() = _classification_of_student

    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level

    // New properties
    private val _student_id = MutableLiveData<String>()
    val student_id: LiveData<String> get() = _student_id

    private val _subject = MutableLiveData<MutableList<Int>>()  // Mutable list of subjects
    val subject: LiveData<MutableList<Int>> get() = _subject

    private val _enrollment_period_id = MutableLiveData<String>()
    val enrollment_period_id: LiveData<String> get() = _enrollment_period_id

    // Setters for the new properties
    fun setStudentId(value: String) {
        _student_id.value = value
    }

    fun setSubjects(subjectList: List<Int>) {
        _subject.value = subjectList.toMutableList()  // Convert to mutable list before setting
    }

    fun addSubject(subject: Int) {
        val currentSubjects = _subject.value ?: mutableListOf()  // Get current list or create an empty list
        currentSubjects.add(subject)  // Add new subject
        _subject.value = currentSubjects  // Update the LiveData with the new list
    }

    fun removeSubject(subject: Int) {
        val currentSubjects = _subject.value ?: mutableListOf()
        currentSubjects.remove(subject)  // Remove the subject from the list
        _subject.value = currentSubjects  // Update the LiveData with the modified list
    }

    fun setEnrollmentPeriodId(value: String) {
        _enrollment_period_id.value = value
    }

    // Setters for existing properties
    fun setLevel(level: String) {
        _level.value = level
    }

    fun setStudentID(value: String) {
        _studentID.value = value
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun setDeptId(value: String) {
        _dept_id.value = value
    }

    fun setCurrId(value: String) {
        _curr_id.value = value
    }

    fun setCourseId(value: String) {
        _course.value = value
    }

    fun setStatus(value: String) {
        _status.value = value
    }

    fun setSection(value: String) {
        _section.value = value
    }

    fun setClassificationOfStudent(value: String) {
        _classification_of_student.value = value
    }
}
