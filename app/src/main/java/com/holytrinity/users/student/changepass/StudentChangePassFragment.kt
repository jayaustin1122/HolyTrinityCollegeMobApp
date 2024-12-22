package com.holytrinity.users.student.changepass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.ChangePasswordRequest
import com.holytrinity.api.ChangePasswordResponse
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.UserService
import com.holytrinity.databinding.FragmentStudentChangePassBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentChangePassFragment : Fragment() {
    private lateinit var binding: FragmentStudentChangePassBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentChangePassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_student)
            }
            findNavController().navigate(R.id.studentDrawerHolderFragment, bundle)
        }

        binding.saveButton.setOnClickListener {
            val oldPassword = binding.oldPasswordInputEditText.text.toString()
            val newPassword = binding.newPasswordInputEditText.text.toString()
            val confirmPassword = binding.confirmPasswordInputEditText.text.toString()

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val userId = getUserId()
            if (userId == -1) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            changePassword(userId, oldPassword, newPassword)
        }
    }


    private fun getUserId(): Int {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1) // Return -1 if no user_id is found
    }

    private fun changePassword(userId: Int, oldPassword: String, newPassword: String) {
        val service = RetrofitInstance.create(UserService::class.java)
        val request = ChangePasswordRequest(
            user_id = userId,
            old_password = oldPassword,
            new_password = newPassword
        )

        service.changePassword(request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: Response<ChangePasswordResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Password Changed")
                        .setContentText("Your password has been changed successfully.")
                        .setConfirmText("Done")
                        .setConfirmClickListener { dialog ->
                            dialog.dismissWithAnimation()
                            val bundle = Bundle().apply {
                                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_student)
                            }
                            findNavController().navigate(R.id.studentDrawerHolderFragment, bundle)
                        }
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.body()?.message ?: "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}
