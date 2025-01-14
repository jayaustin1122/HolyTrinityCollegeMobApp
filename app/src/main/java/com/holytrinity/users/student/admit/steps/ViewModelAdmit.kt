package com.holytrinity.users.student.admit.steps

import android.net.Uri
import androidx.lifecycle.ViewModel

class ViewModelAdmit : ViewModel() {

    // User Data
    var userType: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var middleName: String = ""
    var sex: String = ""
    var dateOfBirth: String = ""
    var email: String = ""
    var phone: String = ""
    var municipality: String = ""
    var barangay: String = ""

    // File Data (Uri? for files)
    var baptismalCert: Uri? = null
    var confirmationCert: Uri? = null
    var nso: Uri? = null
    var marriageCert: Uri? = null
    var brgyResCert: Uri? = null
    var indigency: Uri? = null
    var birForm: Uri? = null
    var recommLetter: Uri? = null
    var parish: String = ""
    var medCert: Uri? = null

    // Enrollment Data
    var lrn: String = ""
    var attended: String = ""
    var form137: Uri? = null
    var diploma: Uri? = null
    var tor: Uri? = null
    var coh: Uri? = null
    var esc: Uri? = null

    // Authentication Data
    var userName: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    // Function to reset all fields
    fun resetFields() {
        userType = ""
        firstName = ""
        lastName = ""
        middleName = ""
        sex = ""
        dateOfBirth = ""
        email = ""
        phone = ""
        municipality = ""
        barangay = ""

        baptismalCert = null
        confirmationCert = null
        nso = null
        marriageCert = null
        brgyResCert = null
        indigency = null
        birForm = null
        recommLetter = null
        parish = ""
        medCert = null

        lrn = ""
        attended = ""
        form137 = null
        diploma = null
        tor = null
        coh = null
        esc = null

        userName = ""
        password = ""
        confirmPassword = ""
    }
}
