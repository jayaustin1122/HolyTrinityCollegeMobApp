import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.api.PaymentFeeApiService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStepOneCashierPaymentBinding
import com.holytrinity.model.PaymentFee
import com.holytrinity.model.Student
import com.holytrinity.users.cashier.payment_transaction.steps.ViewModelPayment
import com.holytrinity.users.student.admit.steps.ViewModelAdmit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StepOneCashierPaymentFragment : Fragment() {
    private lateinit var binding: FragmentStepOneCashierPaymentBinding
    private lateinit var apiService: PaymentFeeApiService
    private lateinit var viewModel: ViewModelPayment
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var studentNames: MutableMap<String, String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepOneCashierPaymentBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelPayment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = RetrofitInstance.create(PaymentFeeApiService::class.java)
        fetchPaymentFees()
        getAllStudents()
    }
    private fun getAllStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    students.forEach { student ->
                        Log.d("StudentData", "ID: ${student.student_id}, Name: ${student.student_name}")
                    }
                    studentNamesMap = students.associate { it.student_name.toString() to it.student_id.toString() }.toMutableMap()
                    setupAutoCompleteTextView()
                } else {

                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")

            }
        })
    }
    private fun setupAutoCompleteTextView() {
        val studentNamesList = studentNamesMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentNamesList)
        binding.studentTextView.setAdapter(adapter)
        binding.studentTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                viewModel.studentID = selectedStudent.student_id.toString()
            }
        }
    }
    private fun fetchPaymentFees() {
        apiService.getAllFees().enqueue(object : Callback<List<PaymentFee>> {
            override fun onResponse(
                call: Call<List<PaymentFee>>,
                response: Response<List<PaymentFee>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val paymentFees = response.body()!!
                    populateDropdown(paymentFees)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load data. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<PaymentFee>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun populateDropdown(paymentFees: List<PaymentFee>) {
        val titles = paymentFees.map { it.title }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, titles)
        binding.transactionTypeTextView.setAdapter(adapter)

        binding.transactionTypeTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedFee = paymentFees[position]
            viewModel.paymentTitle = selectedFee.title
            viewModel.paymentAmount = selectedFee.amount
            viewModel.paymentDescription = selectedFee.description
            //Toast.makeText(requireContext(), "Selected: ${selectedFee.title}", Toast.LENGTH_SHORT).show()
        }
    }
}
