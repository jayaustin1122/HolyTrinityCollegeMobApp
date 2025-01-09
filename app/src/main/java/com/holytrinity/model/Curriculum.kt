package com.holytrinity.model

data class Curriculum (
    val curriculum_id:Int?,
    val name: String,
    val code: String,
    val description: String
)
data class CurriculumResponse(
    val status: String,
    val curriculum: List<Curriculum>
)