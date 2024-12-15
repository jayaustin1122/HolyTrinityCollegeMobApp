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
                loadingDialog.dismiss()

                onLogoutSuccess()
            }, 2000)
        }
    }
}
