package com.holytrinity.model

data class Soa(
    val id: String,                    // SOA ID
    val student_id: String,            // Student ID (studentID in the table)
    val level: String,            // Student ID (studentID in the table)
    val transaction: String,           // Transaction type
    val transaction_number: String,    // Transaction number
    val department: String,    // Transaction number
    val year: String,    // Transaction number
    val date_of_transaction: String,  // Date of transaction (can be Date, String, or Timestamp depending on your requirement)
    val due_amount: Double,            // Amount due (Changed to Double for currency representation)
    val total_due: Double,
    val total_paid: Double,           // Payment made (Changed to Double for currency representation)
    val student_name: String   ,        // Payment made (Changed to Double for currency representation)
    val balance: Double      ,     // Payment made (Changed to Double for currency representation)
    val amount: Double           // Payment made (Changed to Double for currency representation)
)
