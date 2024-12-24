package com.holytrinity.users.cashier.payment_transaction.steps

import androidx.lifecycle.ViewModel

class ViewModelPayment : ViewModel() {

    var paymentTitle: String = ""
    var studentID: String = ""
    var paymentAmount: Double = 0.0
    var paymentDescription: String = ""
    var student_name: String = ""
    var total: String = ""
    var benefactor_id: Int? =0
    var discount_id: Int? =0
    var transaction: String = ""
    var amountPay: String = ""

}