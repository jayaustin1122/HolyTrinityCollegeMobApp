package com.holytrinity.util

import android.content.Context
import android.os.Handler
import com.example.canorecoapp.utils.DialogUtils

object LogoutHelper {

    fun logout(context: Context, onLogoutSuccess: () -> Unit) {
        // Show a confirmation dialog before logging out
        DialogUtils.showWarningMessage(
            context, "Logout", "Are you sure you want to Logout?"
        ) { sweetAlertDialog ->
            sweetAlertDialog.dismissWithAnimation()
            val loadingDialog = DialogUtils.showLoading(context)
            loadingDialog.show()

            // Simulate a delay (can be replaced with actual logout logic)
            Handler().postDelayed({
                loadingDialog.dismiss()
                // Call the onLogoutSuccess callback once logout is complete
                onLogoutSuccess()
            }, 2000) // Adjust the delay as needed
        }
    }
}
