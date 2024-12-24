package com.holytrinity.users.cashier.payment_transaction

import StepOneCashierPaymentFragment
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierPaymentHolderBinding
import com.holytrinity.model.Payment
import com.holytrinity.users.cashier.payment_transaction.steps.StepThreeCashierPaymentFragment
import com.holytrinity.users.cashier.payment_transaction.steps.StepTwoCashierPaymentFragment
import com.holytrinity.users.cashier.payment_transaction.steps.ViewModelPayment
import com.holytrinity.users.student.adapter.StudentAdmitAdapter
import com.holytrinity.users.student.admit.steps.StudentGetAdmittedStepFourFragment
import com.holytrinity.users.student.admit.steps.StudentGetAdmittedStepOneFragment
import com.holytrinity.users.student.admit.steps.StudentGetAdmittedStepThreeFragment
import com.holytrinity.users.student.admit.steps.StudentGetAdmittedStepTwoFragment
import com.holytrinity.util.BluetoothHelper
import com.shuhart.stepview.StepView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CashierPaymentHolderFragment : Fragment() {
    private lateinit var binding: FragmentCashierPaymentHolderBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var stepView: StepView
    private lateinit var adapter: StudentAdmitAdapter
    private lateinit var viewModel: ViewModelPayment
    private lateinit var bluetoothFn : BluetoothHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashierPaymentHolderBinding.inflate(layoutInflater)
        viewPager = binding.viewPager
        stepView = binding.stepView
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelPayment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(
                requireActivity(),
                "Confirm Exit",
                "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()

                val bundle = Bundle().apply {
                    putInt("selectedFragmentId", null ?: R.id.nav_dashboard_cashier)
                }
                findNavController().navigate(R.id.cashierDrawerFragment, bundle)
            }
        }

        adapter = StudentAdmitAdapter(requireActivity())
        viewPager.adapter = adapter
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backItem()
                }
            })
        adapter.addFragment(StepOneCashierPaymentFragment())
        adapter.addFragment(StepTwoCashierPaymentFragment())
        adapter.addFragment(StepThreeCashierPaymentFragment())
        stepView.go(0, true)
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stepView.go(position, true)

                // Hide Back Button on the first page
                if (position == 0) {
                    binding.backButton.visibility = View.INVISIBLE
                } else {
                    binding.backButton.visibility = View.VISIBLE
                }
            }
        })

        binding.backButton.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem = viewPager.currentItem - 1
            }
        }
        binding.nextButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> validateFragmentOne()
                1 -> validateFragmentTwo()
                2 -> validateFragmentThree()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above (API 30+)
            if (!Environment.isExternalStorageManager()) {
                try {
                    // Intent to redirect user to allow 'All Files Access'
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val packageName = requireContext().packageName // Get your app's package name
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the error or fallback to an alternative behavior
                }
            }
        } else {
            // For Android versions < 11 (API 29 and below)
            val REQUEST_CODE = 1
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }

    }
    fun printDetails(){
        val itemsToPay = listOf(
            Payment(description = viewModel.paymentTitle, amount = viewModel.paymentAmount.toString()),

            )


        // Get the current date and time
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        // Generate the receipt message with the current date and time
        val message = generateReceiptMessage(
            currentDate, currentTime, viewModel.studentID, viewModel.student_name, itemsToPay, viewModel.total, "s1000", "1209102"
        )

        // Start a coroutine to print the receipt
        CoroutineScope(Dispatchers.IO).launch {
            val isPrinted = bluetoothFn.printTextFeed(message)
            withContext(Dispatchers.Main) {
                if (isPrinted) {
                    Toast.makeText(requireContext(), "Printed Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Printing Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun validateFragmentThree() {

    }

    private fun validateFragmentTwo() {
        nextItem()
    }

    private fun validateFragmentOne() {
        val userId = viewModel.studentID
        val paymentTitle = viewModel.paymentTitle

        if (userId.isEmpty() || paymentTitle.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please Select Transaction or Search Student",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            nextItem()
        }
    }

    fun backItem() {
        val currentItem = viewPager.currentItem
        val nextItem = currentItem - 1
        if (nextItem >= 0) {
            viewPager.currentItem = nextItem
        }
    }

    private fun nextItem() {
        val currentItem = viewPager.currentItem
        val nextItem = currentItem + 1
        if (nextItem < adapter.itemCount) {
            viewPager.currentItem = nextItem
        }
    }

    fun generateReceiptMessage(
        date: String,
        time: String,
        studentId: String,
        studentName: String,
        itemsToPay: List<Payment>,
        totalAmount: String,
        cashAmount: String,
        changeAmount: String
    ): String {
        return StringBuilder().apply {
            append("\n\n\n\nRoman Catholic Bishop of Daet\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("HOLY TRINITY COLLEGE SEMINARY\n")
            append("       FOUNDATION, Inc.      \n")
            append("\u001B\u0045\u0000") // Bold OFF
            append("Holy Trinity College Seminary\n")
            append("   P.3 Bautista 4604, Labo   \n")
            append("Camarines Norte, Philippines \n")
            append("=============================\n")
            append("ID:   $studentId  DATE: $date\n")
            append("Name: $studentName   TIME: $time\n")
            append("=============================\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("Description         Amount   \n")
            append("\u001B\u0045\u0000") // Bold OFF
            // Add items dynamically
            itemsToPay.forEach { item ->
                append("${item.description.padEnd(17)} ${item.amount.padStart(10)}\n")
            }
            append("=============================\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("Total              $totalAmount   \n")
            append("\u001B\u0045\u0000") // Bold OFF
            append("Cash               $cashAmount   \n")
            append("Change             $changeAmount   \n")
            append("=============================\n\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("          Thank You!         \n\n\n\n")
            append("\u001B\u0045\u0000") // Bold OFF
        }.toString()
    }
    }