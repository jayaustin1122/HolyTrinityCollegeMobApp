package com.holytrinity.model

data class SubjectsModel(
    val id: Int,
    val name: String,
    val code: String,
    val description: String,
    val schedule: String,
    val units: Int,
    val type: String,
    val year: String
)