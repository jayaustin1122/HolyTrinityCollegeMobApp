package com.holytrinity.model

data class Soa(
    val id: String,                // SOA ID
    val studentID: String,         // ID No (studentID in the table)
    val transaction: String,       // Transaction type
    val transaction_number: String,// Transaction number
    val date_of_transaction: String,// Date of transaction
    val due_amount: String,        // Amount due
    val payment_made: String       // Payment made
)
