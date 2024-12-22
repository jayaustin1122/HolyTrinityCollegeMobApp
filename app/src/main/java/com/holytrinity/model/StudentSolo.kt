package com.holytrinity.model

data class StudentSolo(
    val student_id: Int,
    val user_id: Int,
    val learner_ref_no: String?,
    val entry_date: String?,
    val dept_id: Int?,
    val course_id: Int,
    val curriculum_id: Int?,
    val entry_period_id: Int?,
    val level: String?,
    val official_status: String?,
    val registration_verified: Boolean?,
    val student_name: String,
    val student_email: String,
    val birthdate: String,
    val gender: String,
    val course: Course,
    val enrolled_subjects: List<EnrolledSubject>,
    val contacts: List<Contact> // New field for contacts
)

data class Course(
    val course_id: Int,
    val course_code: String,
    val course_name: String,
    val course_description: String
)

data class EnrolledSubject(
    val subject_id: Int,
    val subject_name: String,
    val subject_units: Int,
    val class_info: ClassInfo
)

data class ClassInfo(
    val class_id: Int,
    val schedule: String,
    val section: String,
    val max_enrollment: Int,
    val instructor: Instructor
)

data class Instructor(
    val instructor_name: String,
    val instructor_email: String
)

data class Contact(
    val contact_type: String,
    val contact_value: String
)
