package com.holytrinity.model

import com.google.gson.annotations.SerializedName

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
    val registration_verified: String?,
    val student_name: String,
    val student_email: String,
    val birthdate: String,
    val gender: String,
    val course: Course,
    val curriculum: Curriculums?, // New field for curriculum information
    val enrollment_period: Enrollment_Period?, // New field for enrollment period information
    val personal_info: PersonalInfo?, // New field for personal info
    val enrolled_subjects: List<EnrolledSubject>,
    val contacts: List<Contact>, // New field for contacts
    val addresses: List<Address> // New field for addresses
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

    @SerializedName("class")
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

// New data class for curriculum information
data class Curriculums(
    val curriculum_id: Int,
    val curriculum_code: String,
    val curriculum_name: String,
    val curriculum_description: String
)

// New data class for enrollment period information
data class EnrollmentPeriod(
    val enrollment_period_id: Int,
    val year: Int,
    val semester: String,
    val start_date: String,
    val end_date: String
)

// New data class for personal information
data class PersonalInfo(
    val first_name: String,
    val middle_name: String?,
    val last_name: String,
    val birthdate: String,
    val gender: String,
    val religion_id: Int?,
    val parish: String?,
    val attended: String?
)

// New data class for student addresses
data class Address(
    val address_type: String,
    val address_line1: String,
    val address_line2: String?
)
