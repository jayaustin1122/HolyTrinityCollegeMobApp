package com.holytrinity.model

data class PrerequisitesResponse(
    val status: String,
    val data: List<Prerequisite>
)
data class SubjectsResponse(
    val status: String,
    val data: List<Subjecta>
)

data class CurriculumSubjectsResponse(
    val status: String,
    val data: List<CurriculumSubject>
)
// Prerequisite.kt
data class Prerequisite(
    val subject_id: String,
    val prerequisite_subject_id: String
)

// Subject.kt (fixed the typo from Subjecta to Subject)
data class Subjecta(
    val subject_id: String,
    val name: String
)

// CurriculumSubject.kt
data class CurriculumSubject(
    val curriculum_subject_id: String,
    val curriculum_id: String,
    val subject_id: String,
    val recommended_year_level: String,
    val recommended_sem: String
)


// PrerequisiteWithDetails.kt
data class PrerequisiteWithDetails(
    val subjectName: String,
    val prerequisiteSubjectName: String,
    val curriculum: CurriculumSubject?
)
