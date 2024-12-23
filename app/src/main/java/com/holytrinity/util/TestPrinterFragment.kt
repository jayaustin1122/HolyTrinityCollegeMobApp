package com.holytrinity.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.holytrinity.databinding.FragmentTestPrinterBinding
import com.holytrinity.model.Payment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TestPrinterFragment : Fragment() {
    private lateinit var binding: FragmentTestPrinterBinding
    private lateinit var bluetoothFn: BluetoothHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestPrinterBinding.inflate(layoutInflater)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bluetoothFn.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothFn = BluetoothHelper(requireActivity(), this.requireContext())
        bluetoothFn.checkAndRequestBluetoothPermission() // check permission

        // Configurable fields
        val date = "08/02/02"
        val time = "02:00 PM"
        val studentId = "21-0594"
        val studentName = "Julius"
        val totalAmount = "P 00.00"
        val cashAmount = "P 00.00"
        val changeAmount = "P 8000.00"

        val itemsToPay = listOf(
            Payment(description = "Tuition Fee", amount = "P 5000.00"),
            Payment(description = "Books", amount = "P 2000.00"),
            Payment(description = "Miscellaneous", amount = "P 1000.00")
        )

        val message = StringBuilder().apply {
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

        binding.btnOnBt.setOnClickListener {
            bluetoothFn.toggleBluetooth()
        }

        binding.btnConnPrinter.setOnClickListener {
            val isConnected = bluetoothFn.connectPrinter()
            if (isConnected) {
                Toast.makeText(requireContext(), "Connected to Printer", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Connection Failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPrint.setOnClickListener {
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

        binding.btnDisconnect.setOnClickListener {
            bluetoothFn.disconnectPrinter()
            Toast.makeText(requireContext(), "Disconnected from Printer", Toast.LENGTH_SHORT).show()
        }
    }

}
