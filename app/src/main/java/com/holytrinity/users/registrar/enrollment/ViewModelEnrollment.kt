package com.holytrinity.users.registrar.enrollment

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider

class ViewModelEnrollment  : ViewModel() {
    var studentID: String = ""
    var name: String = ""
    var email: String = ""
    var phone: String = ""
    var curr_id: String = "Curriculum A"
    var department_id: String = ""
    var course: String = ""
    var level: String = ""
    var status: String = ""
    var section: String = ""
    var classification_of_student: String = ""
    var birthdate: String = ""
    var sex: String = ""
    var address: String = ""


}