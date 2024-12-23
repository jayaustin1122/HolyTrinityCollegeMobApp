package com.holytrinity.model

// Updated Enrollment class to handle multiple classes inside each subject
data class Subjects(
    val subjectId: Int,
    val code: String,
    val name: String,
    val units: Int,
    val classes: List<Class> // Nested list of classes for each subject
)

data class Enrollment(
    val subjects: List<Subjects> // List of subjects, each with its classes
)
data class EnrollmentResponse(
    val subjects: List<Subjects> // Wrap the list of subjects inside a response object
)
data class Assessment(
    val studentId: Int,
    val enrollmentPeriodId: Int,
    val tuitionFee: Double,
    val boardLodging: Double,
    val riceShare: Double,
    val miscellaneous: Double,
    val others: Double,
    val total: Double
)

data class Students(
    val studentId: Int,
    val deptId: Int,
    val courseId: Int,
    val curriculumId: Int,
    val entryPeriodId: Int,
    val level: Int,
    val officialStatus: Int,
    val registrationVerified: Int
)

data class StudentLedger(
    val studentId: Int,
    val enrollmentPeriodId: Int,
    val totalDue: Double,
    val totalPaid: Double,
    val balance: Double
)
