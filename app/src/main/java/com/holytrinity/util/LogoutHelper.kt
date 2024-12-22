package com.holytrinity.util

import android.content.Context
import android.os.Handler
import com.example.canorecoapp.utils.DialogUtils

object LogoutHelper {

    fun logout(context: Context, onLogoutSuccess: () -> Unit) {
        DialogUtils.showWarningMessage(
            context, "Logout", "Are you sure you want to Logout?"
        ) { sweetAlertDialog ->
            sweetAlertDialog.dismissWithAnimation()
            val loadingDialog = DialogUtils.showLoading(context)
            loadingDialog.show()

            Handler().postDelayed({
                // Clear SharedPreferences
                val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                loadingDialog.dismiss()

                // Invoke the success callback
                onLogoutSuccess()
            }, 2000)
        }
    }
}
