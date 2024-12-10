package com.holytrinity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.holytrinity.api.DiscountFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.ActivityMainBinding
import com.holytrinity.databinding.FragmentSignInBinding
import com.holytrinity.model.DiscountFee
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.RegistrarDashBoardFragment
import com.holytrinity.users.registrar.RegistrarEnrollmentFragment
import com.holytrinity.users.registrar.RegistrarStudentLedgerFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.myDrawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            openFragment(RegistrarDashBoardFragment())
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> openFragment(RegistrarDashBoardFragment())
                R.id.nav_enrollment -> openFragment(RegistrarEnrollmentFragment())
                R.id.nav_ledger -> openFragment(RegistrarStudentLedgerFragment())

                else -> false
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun openFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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

