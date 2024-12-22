package com.holytrinity.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.api.UserLoginService
import com.holytrinity.api.UserResponse
import com.holytrinity.databinding.FragmentSignInBinding
import com.holytrinity.model.Student
import com.holytrinity.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.admitTextButton.setOnClickListener {
            findNavController().navigate(R.id.studentGetAdmittedFragment)
        }

        binding.loginButton.setOnClickListener {
            validateLogin()
        }
    }

    private fun validateLogin() {
        val userName = binding.useridInputEditLayout.text?.trim().toString()
        val password = binding.passwordInputEditLayout.text?.trim().toString()

        if (userName.isNotEmpty() && password.isNotEmpty()) {
            getUserData(userName, password)
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getUserData(userName: String, password: String) {
        val studentService = RetrofitInstance.create(UserLoginService::class.java)
        studentService.getUser(userName, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.status == "success" && userResponse.user != null) {
                        val user = userResponse.user
                        val roleId = user.role_id

                        // Save user data in SharedPreferences
                        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("user_id", user.user_id)
                        editor.putInt("role_id", user.role_id)
                        editor.putString("username", user.username)
                        editor.putString("name", user.name)
                        editor.putString("email", user.email)
                        editor.putBoolean("is_logged_in", true)
                        editor.apply()

                        Toast.makeText(requireContext(), "Welcome ${user.name}", Toast.LENGTH_SHORT).show()

                        // Navigate based on role
                        when (roleId) {
                            1 -> findNavController().navigate(R.id.adminDrawerFragment)
                            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
                            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
                            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
                            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
                            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
                            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
                            else -> Toast.makeText(requireContext(), "Role not recognized", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
