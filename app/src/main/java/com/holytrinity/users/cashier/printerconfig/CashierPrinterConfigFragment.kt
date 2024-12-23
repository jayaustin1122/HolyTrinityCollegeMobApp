package com.holytrinity.users.cashier.printerconfig

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierPrinterConfigBinding
import com.holytrinity.util.BluetoothHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CashierPrinterConfigFragment : Fragment() {

    private lateinit var binding: FragmentCashierPrinterConfigBinding
    private lateinit var bluetoothFn: BluetoothHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashierPrinterConfigBinding.inflate(layoutInflater)
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
            append("\u001B\u0045\u0001") // Bold ON
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("         TEST PRINT!         \n\n")
            append("\u001B\u0045\u0000") // Bold OFF
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

        binding.btnPrintTest.setOnClickListener {
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

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_cashier)
            }
            findNavController().navigate(R.id.cashierDrawerFragment, bundle)
        }
    }
}