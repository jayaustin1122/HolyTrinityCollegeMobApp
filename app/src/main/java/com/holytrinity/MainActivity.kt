package com.holytrinity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.holytrinity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}


//    private fun fetchStudents(studentId: String? = null) {
//        val service = RetrofitInstance.create(StudentService::class.java)
//        service.getStudents(studentId).enqueue(object : Callback<Any> {
//            override fun onResponse(call: Call<Any>, response: Response<Any>) {
//                if (response.isSuccessful) {
//                    // Check if the response body is a list of students or a single student
//                    if (response.body() is List<*>) {
//                        // Handle the list of students
//                        val students = response.body() as List<Student>
//                        val names = students.joinToString { "${it.fname} ${it.lname}" }
//                        binding.text.text = names // Update the TextView with all student names
//                        students.forEach { student ->
//                            Log.d("Students", "ID: ${student.id}, Name: ${student.fname} ${student.lname}")
//                        }
//                    } else if (response.body() is Student) {
//                        // Handle a single student
//                        val student = response.body() as Student
//                        binding.text.text = "${student.fname} ${student.lname}"
//                        Log.d("Student123", "ID: ${student.id}, Name: ${student.fname} ${student.lname}")
//                    }
//                } else {
//                    Log.e("Error", "Failed to fetch students: ${response.code()}")
//                    binding.text.text = "Error fetching students."
//                }
//            }
//
//            override fun onFailure(call: Call<Any>, t: Throwable) {
//                Log.e("Error", "Error fetching students: ${t.message}")
//                binding.text.text = "Error fetching students."
//            }
//        })
//    }
//
//
//
//    private fun fetchDiscountFees() {
//        val service = RetrofitInstance.create(DiscountFeeService::class.java)
//
//        service.getDiscountFees().enqueue(object : Callback<List<DiscountFee>> {
//            override fun onResponse(call: Call<List<DiscountFee>>, response: Response<List<DiscountFee>>) {
//                if (response.isSuccessful) {
//                    val discountFees = response.body()
//                    discountFees?.forEach { fee ->
//
//                        Log.d("DiscountFee", "ID: ${fee.id}, Title: ${fee.title}, Code: ${fee.code}, Amount: ${fee.amount}, Description: ${fee.description}")
//                    }
//                } else {
//                    Log.e("Error", "Failed to fetch discount fees: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<DiscountFee>>, t: Throwable) {
//                Log.e("Error", "Error fetching discount fees: ${t.message}")
//            }
//        })
//    }

