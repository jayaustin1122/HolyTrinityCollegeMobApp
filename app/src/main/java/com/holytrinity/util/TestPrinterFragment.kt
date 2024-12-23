package com.holytrinity.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.holytrinity.databinding.FragmentTestPrinterBinding
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
        val studentId = "21-0594"
        val studentName = "Julius"
        val totalAmount = "P 00.00"
        val cashAmount = "P 00.00"
        val changeAmount = "P 00.00"

        // Construct message dynamically
        val message = StringBuilder().apply {
            append("Roman Catholic Bishop of Daet\n")
            append("HOLY TRINITY COLLEGE SEMINARY\n")
            append("       FOUNDATION, Inc.      \n")
            append("Holy Trinity College Seminary\n")
            append("   P.3 Bautista 4604, Labo   \n")
            append("Camarines Norte, Philippines \n")
            append("=============================\n")
            append("Student ID:         $studentId  \n")
            append("Name:               $studentName   \n")
            append("=============================\n")
            append("Description         Amount   \n")
            append("=============================\n")
            append("Total              $totalAmount   \n")
            append("Cash               $cashAmount   \n")
            append("Change             $changeAmount   \n")
            append("=============================\n")
            append("          Thank You!         \n")
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
