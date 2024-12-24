package com.holytrinity.users.cashier.payment_transaction.steps

import androidx.lifecycle.ViewModel

class ViewModelPayment : ViewModel() {

    var paymentTitle: String = ""
    var studentID: String = ""
    var paymentAmount: Double = 0.0
    var paymentDescription: String = ""
    var middleName: String = ""
    var sex: String = ""
    var dateOfBirth: String = ""
    var email: String = ""
    var phone: String = ""
    var municipality: String = ""
    var barangay: String = ""
}